package util
/**
 * A class to filter out metadata that should not be handled
 */
public class MetadataFilter {
    /**
     * Metadata types filter set.
     * Use the XML names, like
     * <ul>
     * <li>ApexClass</li>
     * <li>CustomObject</li>
     * </ul>
     */
    public FilterSet metadataTypesFilterSet = null

    /**
     * Whether installed package should be skipped
     */
    public Boolean skipInstalledPackages = false

    /**
     * Whether unmanaged components should be skipped
     */
    public Boolean skipUnmanagedComponents = false

    /**
     * Managed packages filter set.
     * Use the namespace as key
     */
    public Map<String, InstalledPackageFilterSet> installedPackagesFilterSet = null

    /**
     * Metadata detail file name filter set.
     * Use the XML names (resp. child object metadata name) as key
     */
    public Map<String, FilterSet> fileNamesFilterSet = null

    /**
     * Metadata detail full name filter set.
     * Use the XML names (resp. child object metadata name) as key
     */
    public Map<String, FilterSet> fullNamesFilterSet = null

    /**
     * Default constructor
     */
    public MetadataFilter() {
        metadataTypesFilterSet = null
        skipInstalledPackages = false
        skipUnmanagedComponents = false
        installedPackagesFilterSet = new HashMap<>()
        fileNamesFilterSet = new HashMap<>()
        fullNamesFilterSet = new HashMap<>()
    }

    /**
     * Filters out unneeded/not required metadata types.
     * @param metadataType
     * @return
     */
    public Boolean filterByMetadataTypes(MetadataType metadataType) {
        if ( null != metadataTypesFilterSet ) {
            if ( metadataTypesFilterSet.filter(metadataType.xmlName) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Filters out metadata details that belong to managed packages or have
     * specific file rsp. full names.
     * First the installed package filters are checked, followed by checks
     * for the file name and full name.
     * @param metadataDetail
     * @return
     */
    public Boolean filterByMetadataDetail(MetadataDetail metadataDetail) {
        if ( filterByMetadataDetailPackage(metadataDetail)) {
            return true
        }
        if ( filterByMetadataDetailFileName(metadataDetail)) {
            return true
        }
        if ( filterByMetadataDetailFullName(metadataDetail)) {
            return true
        }
        return false
    }

    /**
     * Filters out metadata that belongs to managed packages.
     * If installed package content should be skipped filter it out.
     * Everything that is not part of a installed package should not be filtered out.
     * For installed packages with no filter sets filter them out.
     * For installed packages with filter set call that filter.
     * @param metadataDetail
     * @return
     */
    private Boolean filterByMetadataDetailPackage(MetadataDetail metadataDetail) {
        //If installed package content should be skipped...
        if ( skipInstalledPackages && metadataDetail.isInstalledPackage() ) {
            return true
        }

        //If unmanaged components content should be skipped...
        if ( skipUnmanagedComponents && ( !metadataDetail.isInstalledPackage() ) ) {
            return true
        }

        //Check installed packages
        if ( metadataDetail.isInstalledPackage() ) {
            //If there are no installed package filter sets at all
            //do not filter out anything from an installed package.
            if ((null == installedPackagesFilterSet) || (installedPackagesFilterSet.isEmpty())) {
                return false;
            }

            //Find out the filter set for this specific installed package. If there is
            //no one filter out the metadata component.
            //If there is one, call that filter.
            InstalledPackageFilterSet filterSet = installedPackagesFilterSet.get(metadataDetail.nameSpacePrefix)
            if (null == filterSet) {
                return true
            }
            if (filterSet.filter(metadataDetail)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Filters out metadata that belongs to specific file names.
     * If there is no filter set for that metadata component type do not filter it out.
     * Else call the filter.
     * @param metadataDetail
     * @return
     */
    private Boolean filterByMetadataDetailFileName(MetadataDetail metadataDetail) {
        FilterSet filterSet = fileNamesFilterSet.get(metadataDetail.xmlName)
        if ( null != filterSet ) {
            if ( filterSet.filter(metadataDetail.fileName)) {
                return true
            }
        }
        return false
    }

    /**
     * Filters out metadata that belongs to specific full names.
     * If there is no filter set for that metadata component type do not filter it out.
     * Else call the filter.
     * @param metadataDetail
     * @return
     */
    private Boolean filterByMetadataDetailFullName(MetadataDetail metadataDetail) {
        FilterSet filterSet = fullNamesFilterSet.get(metadataDetail.xmlName)
        if ( null != filterSet ) {
            if ( filterSet.filter(metadataDetail.fullName)) {
                return true
            }
        }
        return false
    }
}
