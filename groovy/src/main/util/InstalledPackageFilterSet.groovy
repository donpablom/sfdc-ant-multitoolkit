package util

/**
 * Filter set for installed packages
 */
public class InstalledPackageFilterSet {
    /**
     * Metadata detail full name filter set.
     * Use the XML names (resp. child object metadata name) as key.
     * There is one special filter set "*" that is used in case there is no
     * one for a specific XML names.
     */
    public Map<String, FilterSet> fullNamesFilterSet = null

    /**
     * Constructor
     * @param packageName
     */
    public InstalledPackageFilterSet(String packageName) {
        packageName = packageName
        fullNamesFilterSet = new HashMap<>()
    }

    /**
     * Filters out all metadata components which belong to unwanted managed packages.
     * If there is a filter set for a specific metadata component type than filter set
     * is called using the full name.
     * If there is no filter set for a specific metadata component type, than a filter
     * set with the name "*" is used and applied.
     * If that also does not exist, the metadata component is filtered out.
     * @param metadataDetail
     * @return true o filter out, else false
     */
    public Boolean filter(MetadataDetail metadataDetail) {
        FilterSet filterSet = fullNamesFilterSet.get(metadataDetail.xmlName)
        if ( null == filterSet ) {
            filterSet = fullNamesFilterSet.get("*")
            if ( null == filterSet ) {
                return true
            }
        }
        if ( filterSet.filter(metadataDetail.fullName)) {
            return true
        }
        return false
    }
}
