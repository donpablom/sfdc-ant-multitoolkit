package util

import info.ConnectionInfo
import info.EnvironmentRetrieverInfo
import java.util.regex.Matcher
import java.util.regex.Pattern
/**
 * Tools to deal with flows and flow definitions
 */
public class FlowTools {
    static Pattern ACTIVEFLOWPATTERN = Pattern.compile("^\\s*<activeVersionNumber>(\\d+)</activeVersionNumber>\\s*\$")
    /**
     * Returns the name of the flow (without version and extension) from a flow definition name
     * @param flowDefinitionName
     * @return flowBaseName
     */
    static String getBaseNameFromFlowDefinition(String flowDefinitionName) {
        String name = flowDefinitionName.replaceAll("\\\\", "/")
        String flowBaseName = name.substring(name.replaceAll("\\\\", "/").lastIndexOf("/") + 1, name.lastIndexOf("."))
        return flowBaseName
    }
    /**
     * Constructs a flow definition name from a flow base name
     * @param flowBaseName
     * @return flow definition name
     */
    static String constructFlowDefinitionName(String flowBaseName) {
        String flowDefinitionName = flowBaseName + '.flowDefinition'
        return flowDefinitionName
    }
    /**
     * Returns the name of the flow (without version and extension) from a flow name
     * @param flowName
     * @return flowBaseName
     */
    static String getBaseNameFromFlow(String flowName) {
        String name = flowName.replaceAll("\\\\", "/")
        ///22.11.2019 - RIN - fix adding flows to package
        String flowBaseName = name.substring(name.lastIndexOf("/") + 1, name.lastIndexOf("."))
        return flowBaseName
    }
    /**
     * Constructs a flow name from a flow base name
     * @param flowBaseName
     * @return flow name
     */
    static String constructFlowBaseName(String flowBaseName) {
        String flowName = flowBaseName + '.flow'
        return flowName
    }
    /**
     * Constructs a flow name from a flow base name and a version
     * @param flowBaseName
     * @param version
     * @return flow name
     */
    static String constructFlowName(String flowBaseName, String version) {
        String flowName = flowBaseName + '-' + version + '.flow'
        return flowName
    }
    /**
     * Returns the version number from a flow
     * @param flowName
     * @return flow version
     */
    static String getVersionFromFlow(String flowName) {
        String flowVersion = flowName.substring(flowName.lastIndexOf("-") + 1, flowName.lastIndexOf("."))
        return flowVersion
    }
    /**
     * Determines the active version numbers of flows.
     * @param baseFolder folder that contains "flowDefintions" and "flows" folder
     * @return map with flow base name (without version and extension) as key and active version number as value
     */
    static Map<String, String> determineActiveFlowVersions(String baseFolder) {
        String flowDefinitionsDirName = baseFolder + "/flowDefinitions"
        File temp = new File(flowDefinitionsDirName)
        File flowDefinitionsDir = null
        Map<String, String> names2ActiveVersions = new HashMap()
/*        if (! temp.exists()) {
            flowDefinitionsDir = new File(baseFolder + "/")
        } else {
            flowDefinitionsDir = temp
        }
        */
        if ( temp.exists()) {
            flowDefinitionsDir = temp

            flowDefinitionsDir.eachFile() { flowDefinitionFile ->
                String flowBaseName = getBaseNameFromFlowDefinition(flowDefinitionFile.getName())
                println "FlowsTools flowBaseName ${flowBaseName} "
                flowDefinitionFile.eachLine { line ->
                    Matcher matcher = ACTIVEFLOWPATTERN.matcher(line)
                    if (matcher.matches()) {
                        String activeFlowVersion = matcher.group(1)
                        names2ActiveVersions.put(flowBaseName, activeFlowVersion)
                    }
                }
            }
        }
        else{
            println "WARNING There is no ${flowDefinitionsDir} directory"
        }
        return names2ActiveVersions
    }
    /**
     * Retrieves all flow definitions and flows from the org sepcified in the connection info.
     * @param connectionInfo
     * @param environmentInfo
     * @param antWrapper
     * @return
     */
    static Map<String, String> retrieveFlowDefinitionsAndFlows(ConnectionInfo connectionInfo,
                                                               EnvironmentRetrieverInfo environmentInfo,
                                                               AntWrapper antWrapper) {
        //Create target directory if needed
        String retrieveDirName = environmentInfo.targetDir
        File retrieveDirFile = new File(retrieveDirName)
        if ( ! retrieveDirFile.exists() ) {
            retrieveDirFile.mkdir()
        }
        //Construct a package xml only for flow definitions
        List<String> xmlNames2Handle = new ArrayList<>();
        xmlNames2Handle.add("FlowDefinition")
        xmlNames2Handle.add("Flow")
        PackageXmlConstructor packageConstructor = new PackageXmlConstructor()
        String packageXMLContent = packageConstructor.constructPackageXMLContentWithWildcard(connectionInfo, xmlNames2Handle)
        //Retrieve flow definitions and flows
        String packageXMLName = retrieveDirName + "/" + "retrieve_flowDefinitionsAndFlows.xml"
        File packageXMLFile = new File(packageXMLName)
        packageXMLFile.write(packageXMLContent)
        antWrapper.antBuilder.retrieve(username: connectionInfo.userName,
                                       password: connectionInfo.password,
                                       serverurl: connectionInfo.serverUrl,
                                       unpackaged: packageXMLName,
                                       retrieveTarget: retrieveDirName,
                                       unzip: "true")
        Map<String, String> names2ActiveVersions = determineActiveFlowVersions(retrieveDirName)
        return names2ActiveVersions
    }
    /**
     * Replaces the active version in a flow definition
     * @param currentContent
     * @param version
     * @return
     */
    static String replaceActiveVersionInFlowDefinition(String currentContent, String version) {
        String newContent = currentContent.replaceAll("<activeVersionNumber>(\\d+)</activeVersionNumber>",
                                                        "<activeVersionNumber>" + version + "</activeVersionNumber>")
        return newContent
    }
    /**
     * Generates a dummy flow that can always be deployed and has no negative
     * impact on any tests
     * @param baseFolder
     * @param flowBaseName
     * @param version
     */
    static void generateDummyFlow(String baseFolder, String flowBaseName, String version) {
        String flowPath = baseFolder + "/" + constructFlowName(flowBaseName, version)
        String dummyContent = ""
        dummyContent += ""
        dummyContent += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        dummyContent += "<Flow xmlns=\"http://soap.sforce.com/2006/04/metadata\">\n"
        dummyContent += "    <description>This dummy flow has been created to support support automatic deployment of new flows. If for some reasons the real version of the flow is not deployed, this dummy can be deleted.</description>\n"
        dummyContent += "    <label>" + flowBaseName + "</label>\n"
        dummyContent += "</Flow>\n"
        File dummyFlow = new File(flowPath)
        def parentDir = dummyFlow.parentFile
        if ( ! parentDir.exists() ) {
            parentDir.mkdirs()
        }
        if ( ! dummyFlow.exists()) {
            dummyFlow.createNewFile()
        }
        dummyFlow << dummyContent
    }
}
