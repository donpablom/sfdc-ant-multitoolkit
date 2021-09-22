package util

/**
 * A metadata detail created from a list metadata call.
 */
public class MetadataDetail {
    private static final String INSTALLEDPACKAGE = "installed"

    /**
     * To which metadata type this detail belongs to.
     * E.g.
     * <ul>
     * <li>ApexClass</li>
     * <li>CustomObject</li>
     * </ul>
     */
    public String xmlName

    /**
     * To which metadata type this detail belongs to.
     * E.g.
     * <ul>
     * <li>classes</li>
     * <li>objects</li>
     * </ul>
     */
    public String dirName

    public String fileName
    public String fullName
    public String ID
    public String manageableState
    public String nameSpacePrefix
    public String createdByNameID
    public String lastModifiedByNameID

    /**
     * Constructor
     */
    public MetadataDetail() {
    }

    /**
     * Constructor
     * @param xmlName,
     * @param dirName,
     * @param fileName,
     * @param fullName,
     * @param ID,
     * @param manageableState,
     * @param nameSpacePrefix,
     * @param createdByNameID,
     * @param lastModifiedByNameID
     */
    public MetadataDetail (String xmlName,
                           String dirName,
                           String fileName,
                           String fullName,
                           String ID,
                           String manageableState,
                           String nameSpacePrefix,
                           String createdByNameID,
                           String lastModifiedByNameID) {
        this.xmlName               = xmlName
        this.dirName               = dirName
        this.fileName              = fileName
        this.fullName              = fullName
        this.ID                    = ID
        this.manageableState       = manageableState
        this.nameSpacePrefix       = nameSpacePrefix
        this.createdByNameID       = createdByNameID
        this.lastModifiedByNameID  = lastModifiedByNameID
    }

    /**
     * Constructor from a file name.
     * TODO: Attention: The following information is not set:<ul>
     *     <li>fullName</li>
     *     <li>ID</li>
     *     <li>manageableState</li>
     *     <li>nameSpacePrefix</li>
     *     <li>createdByNameID</li>
     *     <li>lastModifiedByNameID</li>
     * </ul>
     * @param xmlName
     * @param fileName Has to start after src.
     */
    public MetadataDetail(String xmlName, String fileName) {
        this.xmlName = xmlName
        this.dirName = fileName.substring(0, fileName.indexOf("/"))
        this.fileName = fileName
        //How to build the following information from file name?
        this.fullName = null
        this.ID = null
        this.manageableState = null
        this.nameSpacePrefix = null
        this.createdByNameID = null
        this.lastModifiedByNameID = null
    }

    /**
     * Whether the detail belongs to an installed package
     * @return
     */
    public Boolean isInstalledPackage() {
        return manageableState.equals(INSTALLEDPACKAGE)
    }

    /**
     * Returns the full name to this detail.
     * @return
     */
    public String getFullName() {
        return fullName
    }
}
