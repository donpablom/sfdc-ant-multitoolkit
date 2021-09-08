package util

import info.ConnectionInfo
/**
 * Class to generate package.xml files.
 * Multiple files are created to support some Salesforce specifica and deal with 10000 limit:
 * <ul>
 *     <li>for permission sets (to get only true permissions)</li>
 *     <li>for all metadata types related to profiles, which are: ApexClass, CustomApplication, CustomField, CustomObject, CustomTab, ExternalDataSource, ReportType, ApexPage
 *     <li>for everything else which cannot be added to the second package xml because of the
 *     10000 limit
 * </ul>
 */
public class PackageXmlConstructor {
    public static final String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                        "<Package xmlns=\"http://soap.sforce.com/2006/04/metadata\">"

    public static final String FOOTER = "</Package>"

    /**
     * Constructs empty package.xml content.
     * @param connectionInfo
     * @return
     */
    public static String constructEmptyPackageXM(ConnectionInfo connectionInfo) {
        println "PackageXmlConstructor.constructEmptyPackageXM"
        Map<String, String> xmlName2ParentXmlName = new HashMap<String, String>()
        String packageXML = HEADER + "\n"
        packageXML += "  <version>" + connectionInfo.apiVersion + "</version>" + "\n"
        packageXML += FOOTER + "\n"
        return packageXML
    }

    /**
     * Constructs the package.xml content.
     * The file will only contain members of the specified metadata types, and "*" as member.
     * @param connectionInfo
     * @param xmlNames2Handle
     * @return
     */
    public String constructPackageXMLContentWithWildcard(ConnectionInfo connectionInfo,
                                             List<String> xmlNames2Handle) {
        println "PackageXmlConstructor.constructPackageXMLContentWithWildcard"

        Map<String, String> xmlName2ParentXmlName = new HashMap<String, String>()
        String packageXML = HEADER + "\n"
        for ( String xmlName : xmlNames2Handle ) {
            packageXML += "  <types>" + "\n"
            packageXML += "    <members>*</members>" + "\n"
            packageXML += "    <name>" + xmlName + "</name>" + "\n"
            packageXML += "  </types>" + "\n"
        }
        packageXML += "  <version>" + connectionInfo.apiVersion + "</version>" + "\n"
        packageXML += FOOTER + "\n"
        return packageXML
    }

    /**
     * Constructs the package.xml content.
     * The file will contain members of all metadata types.
     * @param connectionInfo
     * @param metadataTypes
     * @return one string for each package
     */
    public List<String> constructPackagesXMLContent(ConnectionInfo connectionInfo,
                                                    MetadataTypes metadataTypes) {
        println "PackageXmlConstructor.constructPackagesXMLContent"
        List<String> xmlNames2Handle = new ArrayList<>(metadataTypes.xmlName2MetadataType.keySet())
        return constructPackagesXMLContent(connectionInfo, metadataTypes, xmlNames2Handle)
    }

    /**
     * Constructs the content of all package.xml files needed to retrieve
     * metadata components defined in metadata types.
     * The file will only contain members of the specified metadata types.
     * @param connectionInfo
     * @param metadataTypes
     * @param xmlNames2Handle
     * @return one string for each package
     */
    public List<String> constructPackagesXMLContent(ConnectionInfo connectionInfo,
                                                    MetadataTypes metadataTypes,
                                                         List<String> xmlNames2Handle) {
        println "PackageXmlConstructor.constructPackagesXMLContent"
        List<List<MetadataType>> splitMetadataTypes = metadataTypes.splitMetadataTypes(xmlNames2Handle)
        List<String> packagesXMLContent = new ArrayList<>()
        for ( List<MetadataType> packageMetadataTypes : splitMetadataTypes ){
            String packageXMLContent = constructPackageXMLContent(connectionInfo, packageMetadataTypes)
            packagesXMLContent.add(packageXMLContent)
        }
        return packagesXMLContent
    }

    /**
     * Constructs the content of one package.xml file
     * @param connectionInfo
     * @param packageMetadataTypes
     * @return
     */
    private String constructPackageXMLContent(ConnectionInfo connectionInfo,
                                              List<MetadataType> packageMetadataTypes) {
        println "PackageXmlConstructor.constructPackageXMLContent"
        String packageXML = HEADER + "\n"
        for ( MetadataType metadataType : packageMetadataTypes ) {
            if ( 0 < metadataType.fullName2MetadataDetails.size() ) {
                String xmlName = metadataType.xmlName
                packageXML += "  <types>" + "\n"
                for ( MetadataDetail metadataDetail : metadataType.fullName2MetadataDetails.values()) {
                    String fullName = metadataDetail.getFullName()
                    packageXML += "    <members>" + fullName + "</members>" + "\n"
                }
                packageXML += "    <name>" + xmlName + "</name>" + "\n"
                packageXML += "  </types>" + "\n"
            }
        }
        packageXML += "  <version>" + connectionInfo.apiVersion + "</version>" + "\n"
        packageXML += FOOTER + "\n"
        return packageXML
    }
}
