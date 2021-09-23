package packagecreator

import info.EnvironmentInfo
import info.ConnectionInfo
import util.AntWrapper
import util.CommandRunner
import util.FileTools
import util.FilterSet
import util.FlowTools
import util.MetadataDetail
import util.MetadataFilter
import util.MetadataType
import util.MetadataTypes
import util.PackageXmlConstructor
import groovy.json.JsonSlurper

/**
 * Creates a deployment package by analyzing the Git history.
 */
public class PackageCreator {
    private static final String FILE_SEPARATOR = File.separator
    private static final String[] META_DATA_NEEDED = ['.trigger', '.resource', '.cls', '.component', '.page', '.email', '.asset']
    private static final String META_DATA_SUFIX = '-meta.xml'
    private String[] specialClasses = []
    private String[] specialTests = []

    /**
     * Creates a delta deployment package.
     * The method checks the src directory for changes according to the specified versions, and
     * copies all modified files into a target folder.
     * If the modified file have one of the suffix listed on META_DATA_NEEDED variable, its corresponding meta
     * data file also will be copied.
     * For Aura packages, the whole package is copied.
     * For folders, the folder descriptor is also copied.
     * @param connectionInfo
     * @param environmentInfo
     * @param antWrapper
     * @param metadataFilter
     */
    public void createPackage(ConnectionInfo connectionInfo,
                              EnvironmentInfo environmentInfo,
                              AntWrapper antWrapper,
                              MetadataFilter metadataFilter,
                              Boolean isHotfix) {

        String organization = environmentInfo.organization
        String sourceDirectory = ""
        if (environmentInfo.srcDir.contains("force-app")){
            sourceDirectory = (environmentInfo.srcDir =~ /force-app.*/)[0]
        } else if (environmentInfo.srcDir.contains("src")) {
            sourceDirectory = (environmentInfo.srcDir =~ /src.*/)[0]
        } else {
            sourceDirectory = environmentInfo.srcDir.minus("../../")
        }
        println "Deploying code to " + organization

        //Get all modified files in src folder
        List<String> modifiedFileNames = getModifiedFiles(environmentInfo, isHotfix)

        List<String> modifiedSrcFileNames = new ArrayList<>()
        for ( String modifiedFileName : modifiedFileNames) {
            if (modifiedFileName.startsWith("${sourceDirectory}/")){
                if (! modifiedFileName.equals("${sourceDirectory}/package.xml")) {
                    modifiedSrcFileNames.add(modifiedFileName.substring("${sourceDirectory}/".length()))
                }
            }
        }
        modifiedSrcFileNames.sort{ a, b -> a.toLowerCase() <=> b.toLowerCase() }
        println "Found " + modifiedSrcFileNames.size() + " modified files in ${sourceDirectory}"

        if (modifiedSrcFileNames.size() < 1) {
            println "There were no changes to deploy."
        } else {
            println "Modified SRC files > 1  PackageCreator"
            //Get list of metadata types and build a mapping between XML names and folder names
            String orgTargetDir = environmentInfo.tempDir + "/deploy_delta_retrievals"
            info.EnvironmentRetrieverInfo ei = new info.EnvironmentRetrieverInfo(orgTargetDir,
                                                                        environmentInfo.tempDir,
                                                                        null)
            MetadataTypes metadataTypes = new MetadataTypes()
            metadataTypes.describe(connectionInfo, ei, antWrapper, metadataFilter)
            println "  Found " + metadataTypes.xmlName2MetadataType.size() + " unfiltered metadata types to handle"

    //modifiedSrcFileNames = new ArrayList<String>()
    //modifiedSrcFileNames.add('flowDefinitions/DefWithoutActiveFlow.flowDefinition');//must be skipped with an error message missing active flow version for flow
    //modifiedSrcFileNames.add('flowDefinitions/DefButNoFlow.flowDefinition');//must be skipped with an error message because no flow 30 exists
    //modifiedSrcFileNames.add('flowDefinitions/DefAndFlow.flowDefinition');//must copy the flow version 10
    //modifiedSrcFileNames.add('flowDefinitions/DefAndInactiveFlow.flowDefinition');//must copy the active flow version 22
    //modifiedSrcFileNames.add('flows/DefAndInactiveFlow-21.flow');//must be skipped because it is inactive
    //modifiedSrcFileNames.add('flows/FlowButNoDef-41.flow');//must be skipped with an error message because no flow definition exists
    //modifiedSrcFileNames.add('flows/FlowButInactive-51.flow');//must be skipped with an error message because the flow is inactive according to the definition
    //modifiedSrcFileNames.add('flows/DefAndFlowNew-60.flow');//must copy the flow defintion

            // generate a clone of modified files to iterate over it
            List<String> modifiedSrcFileCopy = modifiedSrcFileNames.clone()
            modifiedSrcFileCopy.each { modifiedFileName ->
                // check if file belongs to classes subdirectory
                if (modifiedFileName =~ /classes\//) {
                    // check if this is test class already or meta file
                    if (!(modifiedFileName =~ /_Test\.cls/ || modifiedFileName =~ /-meta\.xml/)) {
                         println "DEBUG:: Processing ${modifiedFileName}"

                        // is this a special class with its own test file name?
                        def inputFile = new File("sfdc-ant-multitoolkit/scripts/class_exceptions.json")
                        def inputJSON = new JsonSlurper().parseText(inputFile.text)

                        specialClasses = inputJSON['exceptions'].keySet() as String[];
                        String modifiedTestClassName

                        Integer SpecialClassIndex = specialClasses.findIndexOf { it + ".cls" == modifiedFileName.replace('classes/','') }
                        if (SpecialClassIndex != -1) {
                            String className = specialClasses[SpecialClassIndex]
                            println "DEBUG::: Special class found: " + className
                            modifiedTestClassName = "classes/" + inputJSON['exceptions'][className] + ".cls"
                        } else {
                            modifiedTestClassName = modifiedFileName.replace('.cls', '_Test.cls')
                        }

                        // skip the class for which the test class is already in modified files list
                        String TestClassFound = modifiedSrcFileNames.find { it == modifiedTestClassName }
                        if (TestClassFound != null) {
                            println "DEBUG::: ${modifiedTestClassName} class found in modified files list, TestClassFound: ${TestClassFound}"
                            return
                        }

                        println "DEBUG::: Searching for a test class for file: ${modifiedFileName}, with pattern: ${modifiedTestClassName}"
                        String source = environmentInfo.srcDir    + "/" + modifiedTestClassName
                        File sourceFile = new File(source)
                        if (sourceFile.exists() && sourceFile.isFile() ){
                            println "DEBUG::: Including test class in the package: ${modifiedTestClassName}"
                            modifiedSrcFileNames.add(modifiedTestClassName)
                            println "Including ${sourceFile} to the package"
                        } else {
                            println "WARNING!::: Cannot find the Test class for ${modifiedFileName}. File not found (${sourceFile})"
                        }
                    }
                } else if (modifiedFileName =~ /triggers\//) {
                    // is this a special class with its own test file name?
                    def inputFile = new File("sfdc-ant-multitoolkit/scripts/class_exceptions.json")
                    def inputJSON = new JsonSlurper().parseText(inputFile.text)

                    specialClasses = inputJSON['exceptions'].keySet() as String[];
                    String modifiedTestClassName

                    Integer SpecialClassIndex = specialClasses.findIndexOf { it + ".trigger" == modifiedFileName.replace('triggers/','') }
                    if (SpecialClassIndex != -1) {
                        String className = specialClasses[SpecialClassIndex]
                        println "DEBUG::: Special class found: " + className
                        modifiedTestClassName = "classes/" + inputJSON['exceptions'][className] + ".cls"
                    } else {
                        modifiedTestClassName = modifiedFileName.replace('.trigger', '_Test.cls').replace('triggers/', 'classes/')
                    }

                    // skip the class for which the test class is already in modified files list
                    String TestClassFound = modifiedSrcFileNames.find { it == modifiedTestClassName }
                    if (TestClassFound != null) {
                        println "DEBUG::: ${modifiedTestClassName} class found in modified files list, TestClassFound: ${TestClassFound}"
                        return
                    }

                    println "DEBUG::: Searching for a test class for file: ${modifiedFileName}, with pattern: ${modifiedTestClassName}"
                    String source = environmentInfo.srcDir    + "/" + modifiedTestClassName
                    File sourceFile = new File(source)
                    if (sourceFile.exists() && sourceFile.isFile() ){
                        println "DEBUG::: Including test class in the package: ${modifiedTestClassName}"
                        modifiedSrcFileNames.add(modifiedTestClassName)
                        println "Including ${sourceFile} to the package"
                    } else {
                        println "WARNING!::: Cannot find the Test class for ${modifiedFileName}. File not found (${sourceFile})"
                    }
                }
            }

            Map<String, String> names2SrcActiveVersions = null
            modifiedSrcFileNames.each { modifiedFileName ->
             println "Handle ${modifiedFileName}"
                //In case it is a metadata file, convert the name to the according normal
                //file to make sure both are copied.
                //Ok, this might lead to later copying files twice...
                Integer index = modifiedFileName.lastIndexOf(META_DATA_SUFIX)
                if (-1 != index) {
                    modifiedFileName = modifiedFileName.substring(0, index)
                }

                String source = environmentInfo.srcDir    + "/" + modifiedFileName
                String target = environmentInfo.targetDir + "/" + modifiedFileName


                //Filter out everything which should not be part of a deployment
                //package


                    MetadataType metadataType = metadataTypes.getMetadataTypeFromFileName(modifiedFileName)
                    if (null != metadataType) {
                        MetadataDetail metadataDetail = new MetadataDetail(metadataType.xmlName, modifiedFileName)
                        FilterSet filterSet = metadataFilter.fileNamesFilterSet.get(metadataDetail.xmlName)
                        // cprintln "Handle source ${source}"
                        //Call directly the filter method for the file name
                        //This is because the metadataDetail is incomplete: some information like full name and manageableState
                        //cannot be set from file name.
                        Boolean filterOut = (null != filterSet) ? filterSet.filter(modifiedFileName) : false
                        try {
                        if (!filterOut) {
                            File sourceFile = new File(source)
                            if (sourceFile.exists() && sourceFile.isFile()) {
                                createParentDir(target)

                                //Copy changed file
                                // println "Copy ${source} to ${target}"
                                if (new File(source).exists()) {
                                    // println "DEBUG::: copying source file: ${source} to ${target}"
                                    FileTools.copyFile(source, target)
                                } else {
                                    println "Missing file ${source}"
                                }

                                //Copy metadata file for changed file if required
                                if (hasMetaData(source) || source =~ /[\\\/]src[\\\/]documents[\\\/]/) {
                                    String metaSource = source + META_DATA_SUFIX
                                    String metaTarget = target + META_DATA_SUFIX
                                    //println "Copy Metadata file from ${metaSource} to ${metaTarget}"
                                    if (new File(metaSource).exists()) {
                                        //println "DEBUG::: Copy Metadata file from ${metaSource} to ${metaTarget}"
                                        FileTools.copyFile(metaSource, metaTarget)
                                    } else {
                                        println "Missing file ${metaSource}"
                                    }
                                }


                                //Copy also the active flow for the flow definition.
                                //In case it cannot be found, skip also the flow definition
                                if (source =~ /flowDefinitions[\\\/]/) {
                                    println "Handle FlowsDefinitions"
                                    //Find out the version of all active flow
                                    if (null == names2SrcActiveVersions) {
                                        names2SrcActiveVersions = FlowTools.determineActiveFlowVersions(environmentInfo.srcDir)
                                    }

                                    //Copy the according active flow from sources
                                    String flowBaseName = FlowTools.getBaseNameFromFlowDefinition(source)
                                    String activeVersion = names2SrcActiveVersions.get(flowBaseName)
                                    if (null != activeVersion) {
                                        /////////////
                                        /// Flow name like name+version: MyFlow-1
                                        //String flowName = FlowTools.constructFlowName(flowBaseName, activeVersion)
                                        /// Flow name just like flowDefinition name
                                        String flowName = FlowTools.constructFlowBaseName(flowBaseName)
                                        String sourceFlow = environmentInfo.srcDir + "/flows/" + flowName
                                        String targetFlow = environmentInfo.targetDir + "/flows/" + flowName
                                        if (new File(sourceFlow).exists()) {
                                            //println "Found flow ${flowName} for flow definition ${flowBaseName} and version ${activeVersion}"
                                            FileTools.copyFile(sourceFlow, targetFlow)
                                        } else {
                                            println "WARNING!::: Missing flow file for flow ${flowBaseName} and active version ${activeVersion}, skip also flow definition ${source}"
                                            FileTools.deleteFile(target)
                                        }
                                    } else {
                                        println "WARNING!::: Missing active flow version for flow definition ${flowBaseName}, skip also flow definition ${source}"
                                        FileTools.deleteFile(target)
                                    }
                                }

                                //Copy also the flow definition for the active flow.
                                //If the active flow version could not be determined (no flow definition found), delete the flow version
                                //In the active flow version is another than the version of the flow to handle, delete the flow version.
                                if (source =~ /[\\\/]flows[\\\/]/) {
                                    println "Handle FlowsDefinitions"
                                    //Find out the version of all active flow
                                    if (null == names2SrcActiveVersions) {
                                        names2SrcActiveVersions = FlowTools.determineActiveFlowVersions(environmentInfo.srcDir)
                                    }
                                    //Skip the flow if it is not the active version
                                    String flowBaseName = FlowTools.getBaseNameFromFlow(source)
                                    String activeVersion = names2SrcActiveVersions.get(flowBaseName)
                                    if (null != activeVersion) {
                                        String flowDefinitionName = FlowTools.constructFlowDefinitionName(flowBaseName)
                                        String sourceFlowDef = environmentInfo.srcDir + "/flowDefinitions/" + flowDefinitionName
                                        String targetFlowDef = environmentInfo.targetDir + "/flowDefinitions/" + flowDefinitionName
                                        if (new File(sourceFlowDef).exists()) {
                                            //println "Found flow definition ${flowDefinitionName} for flow definition ${flowBaseName} and version ${activeVersion}"
                                            FileTools.copyFile(sourceFlowDef, targetFlowDef)
                                        } else {
                                            println "WARNING!::: Missing flow definition file for flow ${flowBaseName} - copy only flow ${source}"
                                            //FileTools.deleteFile(target)
                                        }
                                    } else {
                                        println "WARNING!::: No active version found for flow ${flowBaseName} - copy only ${source}"
                                        //FileTools.deleteFile(target)
                                    }
                                }

                                //Copy whole bundle for lightning bundles
                                def bp = /^.*?[\\\/]aura[\\\/](.*?)[\\\/][^\\\/]*$/
                                def bm = source =~ bp
                                if (bm.matches()) {
                                    String sourceDir = environmentInfo.srcDir + "/aura/" + bm.group(1)
                                    String targetDir = environmentInfo.targetDir + "/aura/" + bm.group(1)
                                    println "Copy whole aura bundle in " + sourceDir + " to " + targetDir

                                    if (new File(sourceDir).exists()) {
                                        antWrapper.antBuilder.copy(todir: targetDir) {
                                            fileset(dir: sourceDir)
                                        }
                                    } else {
                                        println "Missing directory ${sourceDir}"
                                    }
                                }

                                //Copy whole bundle for lwc bundles
                                def bpp = /^.*?[\\\/]lwc[\\\/](.*?)[\\\/][^\\\/]*$/
                                def bmm = source =~ bpp
                                if (bmm.matches()) {
                                    String sourceDir = environmentInfo.srcDir + "/lwc/" + bmm.group(1)
                                    String targetDir = environmentInfo.targetDir + "/lwc/" + bmm.group(1)
                                    println "Copy whole lwc bundle in " + sourceDir + " to " + targetDir

                                    if (new File(sourceDir).exists()) {
                                        antWrapper.antBuilder.copy(todir: targetDir) {
                                            fileset(dir: sourceDir)
                                        }
                                    } else {
                                        println "Missing directory ${sourceDir}"
                                    }
                                }

                                //Copy folder descriptors
                                if (source =~ /documents[\\\/]/ ||
                                        source =~ /dashboards[\\\/]/ ||
                                        // source =~ /email[\\\/]/ ||
                                        source =~ /reports[\\\/]/) {
                                    String[] types = ["documents", "dashboards", /*"email",*/ "reports"]
                                    String main_type = "";
                                    String[] results = []
                                    for (type in types) {
                                        if (source.contains(type)) {
                                            main_type = type
                                        }
                                    }
                                    String[] dirs = getDirectories(source, main_type, results)
                                    String directoryPath = environmentInfo.srcDir + "/" + main_type + "/"
                                    String folderDescriptor = directoryPath
                                    String destinationDir = environmentInfo.targetDir + "/" + main_type + "/"
                                    for (directory in dirs) {
                                        folderDescriptor = directoryPath + directory + "-meta.xml"
                                        if (new File(folderDescriptor).exists()) {
                                            antWrapper.antBuilder.copy(todir: destinationDir) {
                                                fileset(file: folderDescriptor)
                                            }
                                        } else {
                                            println "Missing folder descriptor ${folderDescriptor}"
                                        }
                                        directoryPath += directory + "/"
                                        destinationDir += directory + "/"
                                    }
                                }
                            } else {
                                println "Could not find file " + source
                            }
                        } else {
                            println "Skip " + modifiedFileName + " because of general filter"
                        }
                    }  catch(Exception ex) {
                        println("Catching the exception");
                        println(ex.toString());
                        println(ex.getMessage());
                        println(ex.getStackTrace());
                    }
                } else {
                    println "Skip " + modifiedFileName + " as metadata type could not be found"
                }

            }

            //Patch flows
            //Only active flows are handled (has been made sure via previous checks)
            //Active flows that do not exist on the server (or have no active version) can be deployed
            //Active flows that do not differ from org version can be skipped
            //Active flows that differ from org version but have the same version have to get a new version number
            //Active flows that differ from org version and have a different version number can be deployed
            Map<String, String> names2OrgActiveVersions = null
            File targetFlowDefinitionsDir = new File(environmentInfo.targetDir + "/flowDefinitions")
            if (targetFlowDefinitionsDir.exists() && ( 0 < targetFlowDefinitionsDir.list().length )) {
                println "Retrieving flow information as " + connectionInfo.userName + " to verify flows to deploy"

                targetFlowDefinitionsDir.eachFile() { targetFlowDefinitionFile->
                    //Retrieve flows from server to find out active flow versions
                    if (null == names2OrgActiveVersions) {
                        names2OrgActiveVersions = FlowTools.retrieveFlowDefinitionsAndFlows(connectionInfo, ei, antWrapper)
                    }
                    String flowBaseName = FlowTools.getBaseNameFromFlowDefinition(targetFlowDefinitionFile.getAbsolutePath())
                    String srcActiveVersion = names2SrcActiveVersions.get(flowBaseName)
                    File orgFlowDefinitionFile = new File(orgTargetDir + "/flowDefinitions/" + FlowTools.constructFlowDefinitionName(flowBaseName))

                    if (! orgFlowDefinitionFile.exists()) {
                        //New flow definition or no active flow (and assumed there is also no flow)
                        //->generate a pre deployment package as zip that contains only the flow as dummy flow
                        //->later deploy real flow and flow definition to activate the flow
                        println "Run a pre-deployment to deploy flow ${flowBaseName} as it does not yet exist in the org, but as (inactive) dummy."
                        String dummyFlowTargetDir = environmentInfo.tempDir + "/deploy_delta_DummyFlows"
                        FlowTools.generateDummyFlow(dummyFlowTargetDir + "/flows", flowBaseName, srcActiveVersion)
                        String dummyPackageContent = PackageXmlConstructor.constructEmptyPackageXM(connectionInfo)
                        File dummyPackageFile = new File(dummyFlowTargetDir + "/package.xml")
                        dummyPackageFile.createNewFile()
                        dummyPackageFile << dummyPackageContent
                        antWrapper.antBuilder.zip(destfile: environmentInfo.targetDir + "/preDeployment.zip",
                                                basedir: dummyFlowTargetDir,
                                                update: "true",
                                                includes: "package.xml, flows/" + FlowTools.constructFlowName(flowBaseName, srcActiveVersion))
                        println "With normal deployment, update flow ${flowBaseName} with normal version ${srcActiveVersion} content and activate it."
                    } else {
                        //Existing flow definition
                        String srcFlowFileName = environmentInfo.srcDir + "/flows/" + FlowTools.constructFlowName(flowBaseName, srcActiveVersion)
                        File srcFlowFile = new File(srcFlowFileName)
                        String srcFlowContent = srcFlowFile.text
                        String orgActiveVersion = names2OrgActiveVersions.get(flowBaseName)
                        if (null != orgActiveVersion) {
                            //There is an active flow in the org
                            String orgFlowFileName = orgTargetDir + "/flows/" + FlowTools.constructFlowName(flowBaseName, orgActiveVersion)
                            File orgFlowFile = new File(orgFlowFileName)
                            String orgFlowContent = orgFlowFile.text
                            if (srcFlowContent.equals(orgFlowContent)) {
                                //Existing flow definition (and flow)
                                //Content does not differ
                                //->Skip deployment
                                File targetFlowFile = new File(environmentInfo.targetDir + "/flows/" + FlowTools.constructFlowName(flowBaseName, srcActiveVersion))
                                println "Same content found for flow ${flowBaseName} (source: ${srcFlowFileName}, org: ${orgFlowFileName}), skip this flow"
                                FileTools.deleteFile(targetFlowDefinitionFile.getAbsolutePath())
                                FileTools.deleteFile(targetFlowFile.getAbsolutePath())
                            } else {
                                //Flow content is different bertween source and org
                                if (srcActiveVersion.equals(orgActiveVersion)) {
                                    //Existing flow definition (and flow)
                                    //Content differs
                                    //Active version is the same as in the org so that version cannot be deployed
                                    //->Increase version number
                                    String increasedOrgActiveVersion = ((orgActiveVersion as Integer)+1).toString()
                                    println "Version to deploy ${srcActiveVersion} of flow ${flowBaseName} already exists in the org and is active and the content is different. Increase active version to ${increasedOrgActiveVersion}."

                                    //Update flow definition file with the increased version number
                                    String targetContent = targetFlowDefinitionFile.text
                                    String increasedTargetContent = FlowTools.replaceActiveVersionInFlowDefinition(targetContent, increasedOrgActiveVersion)
                                    targetFlowDefinitionFile.text = increasedTargetContent

                                    //Remove flow with increased version if it exists, and rename the active src flow
                                    File targetFlowFile = new File(environmentInfo.targetDir + "/flows/" + FlowTools.constructFlowName(flowBaseName, increasedOrgActiveVersion))
                                    FileTools.deleteFile(targetFlowFile.getAbsolutePath())
                                    targetFlowFile = new File(environmentInfo.targetDir + "/flows/" + FlowTools.constructFlowName(flowBaseName, srcActiveVersion))
                                    targetFlowFile.renameTo(environmentInfo.targetDir + "/flows/" + FlowTools.constructFlowName(flowBaseName, increasedOrgActiveVersion))
                                } else {
                                    //Existing flow definition (and flow)
                                    //Content differs
                                    //Active version is different as in the org
                                    //->deploy
                                    println "Deploy flow ${flowBaseName} as it exist with different content and different active version"
                                }
                            }
                        } else {
                            //Existing flow definition (and flow)
                            //Active version in the org could not be found
                            //->deploy
                        }
                    }
                }
            }
        }
    }

    /**
     * Get a list of list of modified files between a set if specific versions
     * @param environmentInfo
     * @return
     */
    public List<String> getModifiedFiles(EnvironmentInfo environmentInfo, Boolean isHotfix){
        List<String> fileNames = new ArrayList<>()
        Boolean runCommand = true
        String[] output = new String[3]
        List<String> versionRanges = environmentInfo.versions.split("\\s*;\\s*")
        for (String versionRange : versionRanges) {
            println "Get modifiedFiles for range " + versionRange
            String command = environmentInfo.gitExecutable + " " +
                             "diff" + " " +
                             "--oneline" + " " +
                             "--name-status" + " " +
                             "--no-renames" + " "

            //Quoting revisions does not work in Unix command line:
            //Error: fatal: ambiguous argument '"0a1d395009d888b88702ff6647fe637e4bcbd951"': unknown revision or path not in the working tree.
            //[groovy] Use '--' to separate paths from revisions, like this:
            if (versionRange =~ /^[\d\w]+$/ && !isHotfix) {
                String fromVersion = versionRange
                fromVersion = fromVersion.trim()
                println "Get modifiedFiles for versions after ${fromVersion} incl. indexed working copy changes"
                command += "${fromVersion}"
            } else if (versionRange =~ /^[\d\w,]+$/ || isHotfix) {
                runCommand = false
                output[0] = ""
                String outer_command = " "
                List<String> smallerRevisions = "${versionRange}".split("\\s*,\\s*")
                for (String smallerRev : smallerRevisions) {
                    String toVersion = smallerRev
                    String fromVersion = smallerRev.trim() + "^1"
                    fromVersion = fromVersion.trim()
                    println "Get modifiedFiles for versions for revision ${toVersion} incl. indexed working copy changes"
                    outer_command = command + "${fromVersion} ${toVersion}"
                    String[] new_output = CommandRunner.runCommand(environmentInfo.srcDir,
                                                        outer_command)
                    if (new_output[1]) {
                        throw new Exception("An error occured during command execution.")
                    } else {
                        output[0] += new_output[0]
                    }
                }
            } else if (-1 != versionRange.indexOf(' ')) {
                String fromVersion = versionRange.substring(0, versionRange.indexOf(' '))
                String toVersion = versionRange.substring(versionRange.indexOf(' ') + 1)
                fromVersion = fromVersion.trim()
                toVersion = toVersion.trim()
                println "Get modifiedFiles for versions after ${fromVersion} until incl. ${toVersion}"
                command += "${fromVersion} ${toVersion}"
            } else {
                println "Get modifiedFiles for versions after ${versionRange}"
                command += "${versionRange}"
            }

            if (runCommand) {
                println "Command: ${command}"
                output = CommandRunner.runCommand(environmentInfo.srcDir, command)
                if (output[1]) {
                    throw new Exception("An error occured during command execution.")
                }
            }

            println "tempDir: ${environmentInfo.tempDir}"
            def tempFile = new File(environmentInfo.tempDir)
            tempFile.mkdir()
            def diffFile = new File(environmentInfo.tempDir + '/deploy_delta-diff.txt')
            diffFile.write output[0]

            String command_output = output[0].trim();
            if (0 < command_output.length()) {
                command_output.split('\n').each { line ->
                    //Skip deleted files
                    String changeType = line.substring(0, 1)
                    if (! "D".equals(changeType)){
                        // Remove the first character because it only indicates the file status
                        String modifiedFile = line.substring(1).trim()
                        fileNames.add(modifiedFile)
                    }
                }
            }
           // println "===> " + fileNames.toString()
        }
        //println "------> " + fileNames.toString()
        return fileNames
    }

    /**
     * Create a the parent folder needed for a file
     * @param filePath
     */
    private void createParentDir(String filePath){
        File file = new File(filePath)
        File parent = file.getParentFile()

        if (!parent.exists()){
            parent.mkdirs()
        }
    }

    /**
     * Return true if the file provided has one of the suffix listed on
     * META_DATA_SUFIX, it helps to determine if a file has meta data or not
     * @param fileName
     * @return
     */
    private Boolean hasMetaData(String fileName){
        boolean hasMetaData = false
        for ( sufix in META_DATA_NEEDED){
            hasMetaData |= fileName.endsWith(sufix)
        }
        return hasMetaData
    }

    def getDirectories(String filePath, String type, String [] dirs) {
        File parentPath = new File(filePath).getParentFile()
        String parent = parentPath.getName()
        def results = dirs
        if (parent == type) {
            return dirs
        } else {
            results = getDirectories(parentPath.toString(), type, results) + parent.toString()
        }
        return results
    }
}
