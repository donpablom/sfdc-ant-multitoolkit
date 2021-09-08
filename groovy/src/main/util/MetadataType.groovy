package util

import info.EnvironmentRetrieverInfo
import util.AntWrapper
import util.CommandRunner
import info.ConnectionInfo
import util.MetadataDetail
import util.MetadataFilter

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * A metadata type created from a describe call.
 */
public class MetadataType {
    private static final String SECTIONDELIMITER = "************************************************************"
    private static final String FileNamePrefix = "FileName: "
    private static final String FullNameIdPrefix = "FullName/Id: "
    private static final String ManageableStatePrefix = "Manageable State: "
    private static final String NamespacePrefixPrefix = "Namespace Prefix: "
    private static final String CreatedByPrefix = "Created By (Name/Id): "
    private static final String LastModifiedByPrefix = "Last Modified By (Name/Id): "

    private static final Pattern FOLDERPREFIXPATTERN = Pattern.compile("^\\s*[-]+\\s*\$")
    private static final Pattern FOLDERPREFIXCOLUMNHEAD = Pattern.compile("^\\s*DeveloperName\\s*\$")
    private static final Pattern FOLDERPOSTFIXPATTERN = Pattern.compile("\\s+\\(\\d+\\srecords\\)")

    private static List<String> UnfiledPublicFolders = new ArrayList<>()
    static {
        UnfiledPublicFolders.add("Report")
        UnfiledPublicFolders.add("EmailTemplate")
    }

    /**
     * E.g.
     * <ul>
     * <li>ApexClass</li>
     * <li>CustomObject</li>
     * <li>Workflow</li>
     * </ul>
     */
    public String xmlName = null

    /**
     * E.g.
     * <ul>
     * <li>classes</li>
     * <li>objects</li>
     * <li>workflows</li>
     * </ul>
     */
    public String dirName = null

    public String suffix = null
    public Boolean hasMetadataFile = false
    public Boolean inFolder = false

    /**
     * A map of all metadata details.
     * Key is the full name, e.g.
     * <ul>
     * <li>ApexClass: CCR_AssetReplaceAPITest</li>
     * <li>CustomObject: Account or CCR_AssetCompability__c</li>
     * <li>Workflow: Account or DTE_AcceptedTermsAndConditions__c</li>
     * </ul>
     * Each detail might have child objects inside, e.g
     * <ul>
     * <li>CustomObject: CustomField</li>
     * <li>Workflow: WorkflowFieldUpdate</li>
     * </ul>
     */
    public Map<String, MetadataDetail> fullName2MetadataDetails = new LinkedHashMap<String, MetadataDetail>();

    /**
     * A metadata type mapping from the file name to the full name.
     * E.g.
     * <ul>
     * <li>classes/CCR_AssetReplaceAPITest.cls: CCR_AssetReplaceAPITest</li>
     * <li>objects/DTE_AcceptedTermsAndConditions__c.object: CCR_AssetCompability__c</li>
     * <li>workflows/DTE_AcceptedTermsAndConditions__c.workflow: DTE_AcceptedTermsAndConditions__c</li>
     * </ul>
     */
    public Map<String, String> fileName2FullName = new LinkedHashMap<String, String>();

    /**
     * The name of the parent of a child object metadata.
     * Null in case it is a parent metadata.
     * E.g.
     * <ul>
     * <li>CustomField: CustomObject</li>
     * <li>WorkflowFieldUpdate: Workflow</li>
     * </ul>
     */
    public String parentXMLName = null

    /**
     * E.g.
     * <ul>
     * <li>CustomField</li>
     * <li>WorkflowFieldUpdate</li>
     * </ul>
     */
    public List<String> childObjectMetadataTypes = null

    /**
     * Constructor
     */
    public MetadataType() {
    }

    /**
     * Copy constructor.
     * Copies all fields except fullName2MetadataDetails and fileName2FullName.
     */
    public MetadataType(MetadataType origin) {
        xmlName = origin.xmlName
        dirName = origin.dirName
        suffix = origin.suffix
        hasMetadataFile = origin.hasMetadataFile
        inFolder = origin.inFolder
        parentXMLName = origin.parentXMLName
        childObjectMetadataTypes = origin.childObjectMetadataTypes
    }

    /**
     * Whether this metadata type has child object metadata types
     * @return true e.g. for CustomObject or Workflow
     */
    public Boolean hasChildObjectMetadataTypes() {
        if ( null == childObjectMetadataTypes ) {
            return false
        }
        return (0 < childObjectMetadataTypes.size())
    }

    /**
     * Whether this metadata type belongs to a child object metadata type
     * @return true e.g. for CustomField or WorkflowFieldUpdate
     */
    public Boolean belongsToChildObjectMetadataType() {
        return parentXMLName != null;
    }

    /**
     * Runs a describe to get details for that metadata type
     * @param connectionInfo
     * @param environmentInfo
     * @param antWrapper
     * @param metadataFilter
     */
    public void describe(ConnectionInfo connectionInfo,
                         EnvironmentRetrieverInfo environmentInfo,
                         AntWrapper antWrapper,
                         MetadataFilter metadataFilter) {
        if ( ! inFolder ) {
            _describeNonFolders(connectionInfo, environmentInfo, antWrapper, metadataFilter)
        }
        else {
            //Handle folder metadata
            _describeFolders(connectionInfo, environmentInfo, antWrapper, metadataFilter)
        }
    }

    /**
     * Determines a describe to get details for that (non-folder) metadata type.
     * @param connectionInfo
     * @param environmentInfo
     * @param antWrapper
     * @param metadataFilter
     */
    private _describeNonFolders(ConnectionInfo connectionInfo,
                                EnvironmentRetrieverInfo environmentInfo,
                                AntWrapper antWrapper,
                                MetadataFilter metadataFilter) {

        //Run describe for the metadata type
        Map<String, MetadataDetail> fullName2Details =
            _describe(connectionInfo, environmentInfo, antWrapper, metadataFilter, null)

        //Sort by the full names
        List<String> fullNames = new ArrayList<String>(fullName2Details.keySet())
        for ( String fullName : fullName2Details.keySet() ){
            fullNames.add(fullName)
        }
        fullNames = fullNames.sort{ a, b -> a.toLowerCase() <=> b.toLowerCase() }
        for ( String fullName : fullNames ) {
            MetadataDetail metadataDetail = fullName2Details.get(fullName)
            fullName2MetadataDetails.put(fullName, metadataDetail)
            if ( ! fileName2FullName.containsKey(metadataDetail.fileName)) {
                fileName2FullName.put(metadataDetail.fileName, metadataDetail.fullName)
            }
        }
    }

    /**
     * Determines a describe to get details for that folder metadata type.
     * @param connectionInfo
     * @param environmentInfo
     * @param antWrapper
     * @param metadataFilter
     */
    private _describeFolders(ConnectionInfo connectionInfo,
                             EnvironmentRetrieverInfo environmentInfo,
                             AntWrapper antWrapper,
                             MetadataFilter metadataFilter) {
        String soqlStatement = "SELECT DeveloperName " +
                               "FROM Folder " +
                               "WHERE (Type='" + xmlName + "' OR Type='" + dirName + "') AND DeveloperName<>''"
        def output
        output = CommandRunner.runCommand(environmentInfo.tempDir,
                                          environmentInfo.forceExecutable + " " +
                                          "login " +
                                          "-i " + connectionInfo.serverUrl + " " +
                                          "-u " + connectionInfo.userName + " " +
                                          "-p " + connectionInfo.password)
        println "OUTPUT:" + output
        output = CommandRunner.runCommand(environmentInfo.tempDir,
                                          environmentInfo.forceExecutable + " " +
                                          "apiversion" + " " +
                                          connectionInfo.apiVersion)
        println "OUTPUT:" + output
        output = CommandRunner.runCommand(environmentInfo.tempDir,
                                          environmentInfo.forceExecutable + " " +
                                          "query" + " " +
                                          "\"" + soqlStatement + "\"")
        println "OUTPUT:" + output

        List<String> folderNames = new ArrayList<>()
        if ( !CommandRunner.ERROR.equals(output[0] && !output[1])) {
            Boolean handleLine = false
            for ( String line : output[0].split("\\n")) {
                line = line.replaceAll("\"","")
                //println line
                Matcher folderPrefixMatcher = FOLDERPREFIXPATTERN.matcher(line)
                if ( folderPrefixMatcher.matches()) {
                    continue;
                }
                if ( ! handleLine ) {
                    Matcher folderPrefixColumnHead = FOLDERPREFIXCOLUMNHEAD.matcher(line)
                    if ( folderPrefixColumnHead.matches()) {
                        handleLine = true
                    }
                } else {
                    Matcher folderPostfixMatcher = FOLDERPOSTFIXPATTERN.matcher(line)
                    if ( ( folderPostfixMatcher.matches())) {
                        handleLine = false
                    } else {
                        folderNames.add(line.trim())
                    }
                }
            }
        }
        if ( UnfiledPublicFolders.contains(xmlName)) {
            folderNames.add("unfiled\$public")
        }

        folderNames = folderNames.sort{ a, b -> a.toLowerCase() <=> b.toLowerCase() }
        for ( String folderName : folderNames) {
            //Determine child folder and add them
            Map<String, MetadataDetail> folderName2Details =
                _describe(connectionInfo, environmentInfo, antWrapper, metadataFilter, folderName)
            List<String> childFolderNames = new ArrayList<>(folderName2Details.keySet())
            childFolderNames = childFolderNames.sort{ a, b -> a.toLowerCase() <=> b.toLowerCase() }
            for ( String childFolderName : childFolderNames ) {
                MetadataDetail childFolderMetadataDetail = folderName2Details.get(childFolderName)
                fullName2MetadataDetails.put(childFolderName, childFolderMetadataDetail)
                if ( ! fileName2FullName.containsKey(childFolderMetadataDetail.fileName)) {
                    fileName2FullName.put(childFolderMetadataDetail.fileName, childFolderMetadataDetail.fullName)
                }
            }

            //Add the folder itself if there is at least one child folder
            if ( 0 < childFolderNames.size() ) {
                MetadataDetail metadataDetail = new MetadataDetail()
                metadataDetail.xmlName = xmlName
                metadataDetail.dirName = dirName
                metadataDetail.fullName = folderName
                metadataDetail.fileName = dirName + "/"+ folderName
                fullName2MetadataDetails.put(folderName, metadataDetail)
                if ( ! fileName2FullName.containsKey(metadataDetail.fileName)) {
                    fileName2FullName.put(metadataDetail.fileName, metadataDetail.fullName)
                }
            }
        }
    }

    /**
     * Runs a describe to get details for that metadata type.
     * This method also supports sub-metadata types like WorkflowAlerts for Workflows.
     * @param connectionInfo
     * @param environmentInfo
     * @param antWrapper
     * @param metadataFilter
     * @param folderName null in case of non-folder metadata types
     * @return key is full name
     */
    private Map<String, MetadataDetail> _describe(ConnectionInfo connectionInfo,
                                                  EnvironmentRetrieverInfo environmentInfo,
                                                  AntWrapper antWrapper,
                                                  MetadataFilter metadataFilter,
                                                  String folderName) {
        //Create temp directory if needed
        String describeDirName = environmentInfo.tempDir + "/" + dirName
        File describeDirFile = new File(describeDirName)
        if ( ! describeDirFile.exists() ) {
            describeDirFile.mkdir()
        }

        //Run describe
        String describeFileName = describeDirName + "/"
        if ( null == folderName ) {
            describeFileName += xmlName + ".metadata"
        } else {
            describeFileName += folderName + ".metadata"
        }
println "ListMetadata call commented out!"
        antWrapper.antBuilder.listMetadata(username: connectionInfo.userName,
                                           password: connectionInfo.password,
                                           serverurl: connectionInfo.serverUrl,
                                           metadataType: xmlName,
                                           folder: (null == folderName) ? "" : folderName,
                                           resultFilePath: describeFileName,
                                           trace: "false",
                                           apiVersion: connectionInfo.apiVersion)

        //Read describe result file
        File describeFile = new File(describeFileName)
        Map<String, MetadataDetail> metadataDetails = new HashMap<String, MetadataDetail>();
        if ( describeFile.exists()){
            MetadataDetail metadataDetail = null
            describeFile.eachLine { String line ->
                if ( line.equals(SECTIONDELIMITER)) {
                    if ( null != metadataDetail ) {
                        if ( ! metadataFilter.filterByMetadataDetail(metadataDetail)) {
                            metadataDetails.put(metadataDetail.fullName, metadataDetail);
                        }
                    }
                    metadataDetail = null
                } else {
                    if ( null == metadataDetail ) {
                        metadataDetail = new MetadataDetail();
                        metadataDetail.xmlName = xmlName
                        metadataDetail.dirName = dirName
                    }
                    if ( line.startsWith(FileNamePrefix)) {
                        line = line.substring(FileNamePrefix.length())
                        if (line.startsWith("ManagedTopics/")) {
                            line = "managedTopics/" + line.substring("ManagedTopics/".length())
                        } else if (line.startsWith("Workflow/")) {
                            line = "workflows/" + line.substring("Workflow/".length())
                        }
                        metadataDetail.fileName = line;
                    } else if ( line.startsWith(FullNameIdPrefix)) {
                        line = line.substring(FullNameIdPrefix.length())
                        metadataDetail.fullName = line.substring(0, line.lastIndexOf("/"))
                        metadataDetail.ID = line.substring(line.lastIndexOf("/")+1)
                        if ( "SynonymDictionary".equals(xmlName) && "null".equals(metadataDetail.fileName.trim())) {
                            metadataDetail.fileName = dirName + "/" + metadataDetail.fullName + "." + suffix
                        }
                    } else if ( line.startsWith(ManageableStatePrefix)) {
                        line = line.substring(ManageableStatePrefix.length())
                        if ( (null == line) || "null".equals(line)) {
                            line = ""
                        }
                        metadataDetail.manageableState = line;
                    } else if ( line.startsWith(NamespacePrefixPrefix)) {
                        line = line.substring(NamespacePrefixPrefix.length())
                        if ( (null == line) || "null".equals(line)) {
                            line = ""
                        }
                        metadataDetail.nameSpacePrefix = line;
                        //In case the full name belongs to a managed package, and
                        //it is a layout, the package name has to be prefixed for the detail name.
                        if ( ( ! metadataDetail.nameSpacePrefix.isEmpty() ) && "Layout".equals(xmlName)) {
                            int delimiterPos = metadataDetail.fullName.indexOf("-")
                            String objectFullName = metadataDetail.fullName.substring(0, delimiterPos)
                            String layoutFullName = metadataDetail.fullName.substring(delimiterPos + 1)
                            String patchedFullName = metadataDetail.fullName
                            if (! layoutFullName.startsWith(metadataDetail.nameSpacePrefix)) {
                                patchedFullName = objectFullName + "-" + metadataDetail.nameSpacePrefix + "__" + layoutFullName
                                println "Patch full name from " + metadataDetail.fullName + " to " + patchedFullName
                                metadataDetail.fullName = patchedFullName
                            }
                        }
                    } else if ( line.startsWith(CreatedByPrefix)) {
                        line = line.substring(CreatedByPrefix.length())
                        metadataDetail.createdByNameID = line;
                    } else if ( line.startsWith(LastModifiedByPrefix)) {
                        line = line.substring(LastModifiedByPrefix.length())
                        metadataDetail.lastModifiedByNameID = line;
                    }
                }
            }
        }
        return metadataDetails;
    }
}
