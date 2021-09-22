package retriever

import util.AntWrapper
import info.ConnectionInfo
import util.MetadataFilter
import util.MetadataType
import util.MetadataTypes

/**
 * Retrieves metadata.
 */
class Retriever {
    /**
     * Retrieve all metadata into an internal structure.
     * @param connectionInfo
     * @param environmentInfo
     * @param antWrapper
     * @param metadataFilter
     * @return
     */
    public MetadataTypes retrieve(ConnectionInfo connectionInfo,
                                  EnvironmentInfo environmentInfo,
                                  AntWrapper antWrapper,
                                  MetadataFilter metadataFilter) {
        //Determine list of metadata types
        println "Describe metadata types"
        MetadataTypes metadataTypes = new MetadataTypes()
        metadataTypes.describe(connectionInfo, environmentInfo, antWrapper, metadataFilter)
        println "  Found " + metadataTypes.xmlName2MetadataType.size() + " unfiltered metadata types to handle"

        //Determine details for each metadata type
        for (String xmlName : metadataTypes.xmlName2MetadataType.keySet()) {
            MetadataType metadataType = metadataTypes.xmlName2MetadataType.get(xmlName)
            if (! metadataType.belongsToChildObjectMetadataType()) {
                println "  Describe metadata type " + xmlName
            } else {
                println "  Describe metadata type " + xmlName + " that belongs to " + metadataType.parentXMLName
            }
            metadataType.describe(connectionInfo, environmentInfo, antWrapper, metadataFilter)
        }

        //Patch details, e.g. to get exact version of active flows
        metadataTypes.patch(connectionInfo, environmentInfo, antWrapper)

        return metadataTypes
    }
}
