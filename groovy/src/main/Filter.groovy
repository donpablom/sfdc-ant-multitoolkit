import util.FilterSet
import util.MetadataFilter

import java.util.regex.Pattern

/**
 * A class that provides the filter used for retrieving and deploying metadata from/to a Salesforce org.
 */
public class Filter {
    /**
     * Possible values for the purpose of the filter
     * <ul>
     *     <li>RETRIEVE</li>
     *     <li>DEPLOY</li>
     * </ul>
     */
    enum CreateFor{
        RETRIEVE,
        DEPLOY
    }

    /**
     * The purpose of the filter.
     * Defaults to CreateFor.RETRIEVE.
     */
    public CreateFor createFor = CreateFor.RETRIEVE;

    /**
     * Metadata types filter set.
     * Use the XML names, like
     * <ul>
     * <li>ApexClass</li>
     * <li>CustomObject</li>
     * </ul>
     */
    public MetadataFilter metadataFilter = null

    /**
     * Default constructor
     */
    public Filter() {
        metadataFilter = new MetadataFilter()
    }

    /**
     * Default constructor
     * @param createFor
     */
    public Filter(CreateFor createFor) {
        this();
        this.createFor = createFor;
    }

    /**
     * Adds default filters (based on metadata type, managed packages and file names)
     */
    public void addFilter() {
        println "void addFilter"
        addMetadataTypeFilter()
        addManagedPackageFilter(null)
        addFileNameFilter()
    }

    /**
     * Adds default filters (based on metadata type, managed packages and file names)
     * @param packageName Name of a managed package, can be null
     */
    public void addFilter(String packageName) {
        println "void addFilter string package name:" + packageName
        addMetadataTypeFilter()
        addManagedPackageFilter(packageName)
        addFileNameFilter()
    }

    /**
     * Filters out all metadata which do not belong to some specified types.
     * Useful for test purposes.
     */
    private void addMetadataTypeFilter() {
        //List toKeep = new ArrayList()
        //List toSkip = new ArrayList()
        //metadataFilter.metadataTypesFilterSet = new FilterSet(toKeep, toSkip)
    }

    /**
     * Specifies filter for managed packages.
     * @param packageName
     */
    private void addManagedPackageFilter(String packageName) {
        //if ( "ACCL".equals(packageName) ) {
        //    InstalledPackageFilterSet installedPackageFilterSet = new InstalledPackageFilterSet()
        //    if ( true ) {
        //        List toKeep = new ArrayList()
        //        List toSkip = null
        //        toKeep.add(Pattern.compile(/^${packageName}__.*?$/))
        //        installedPackageFilterSet.fullNamesFilterSet.put("ApexPage", new FilterSet(toKeep, toSkip))
        //    }
        //    if ( true ) {
        //        List toKeep = new ArrayList()
        //        List toSkip = null
        //        toKeep.add(Pattern.compile(/^${packageName}__.*?$/))
        //        installedPackageFilterSet.fullNamesFilterSet.put("ApprovalProcess", new FilterSet(toKeep, toSkip))
        //    }
        //    if ( true ) {
        //        List toKeep = new ArrayList()
        //        List toSkip = null
        //        toKeep.add(Pattern.compile(/^${packageName}__.*?$/))
        //        installedPackageFilterSet.fullNamesFilterSet.put("CustomApplication", new FilterSet(toKeep, toSkip))
        //    }
        //    if ( true ) {
        //        List toKeep = new ArrayList()
        //        List toSkip = null
        //        toKeep.add(Pattern.compile(/^.*?\.${packageName}__.*?$/))
        //        installedPackageFilterSet.fullNamesFilterSet.put("CustomField", new FilterSet(toKeep, toSkip))
        //    }
        //
        //    if ( true ) {
        //        List toKeep = new ArrayList()
        //        List toSkip = null
        //        toKeep.add(Pattern.compile(/^${packageName}__.*?$/))
        //        installedPackageFilterSet.fullNamesFilterSet.put("CustomObject", new FilterSet(toKeep, toSkip))
        //    }
        //
        //    if ( true ) {
        //        List toKeep = new ArrayList()
        //        List toSkip = null
        //        toKeep.add(Pattern.compile(/^${packageName}__Order_Template__c.*\.objectTranslation$/))
        //        toKeep.add(Pattern.compile(/^${packageName}__Call__c.*\.objectTranslation$/))
        //        toKeep.add(Pattern.compile(/^${packageName}__Call_Attachment__c.*\.objectTranslation$/))
        //        toKeep.add(Pattern.compile(/^${packageName}__Call_Template__c.*\.objectTranslation$/))
        //        toKeep.add(Pattern.compile(/^${packageName}__Account_Extension__c.*\.objectTranslation$/))
        //        toKeep.add(Pattern.compile(/^${packageName}__Account_Call_Setting__c.*\.objectTranslation$/))
        //        toKeep.add(Pattern.compile(/^${packageName}__Account_Manager__c.*\.objectTranslation$/))
        //        toKeep.add(Pattern.compile(/^${packageName}__Account_Template__c.*\.objectTranslation$/))
        //        toKeep.add(Pattern.compile(/^${packageName}__Job_Definition_List__c.*\.objectTranslation$/))
        //        toKeep.add(Pattern.compile(/^${packageName}__Job_Definition_List_Template__c.*\.objectTranslation$/))
        //        toKeep.add(Pattern.compile(/^${packageName}__Order_Item__c.*\.objectTranslation$/))
        //        toKeep.add(Pattern.compile(/^${packageName}__Org_Unit__c.*\.objectTranslation$/))
        //        toKeep.add(Pattern.compile(/^${packageName}__Org_Unit_User__c.*\.objectTranslation$/))
        //        toKeep.add(Pattern.compile(/^${packageName}__Sales_Organization__c.*\.objectTranslation$/))
        //        toKeep.add(Pattern.compile(/^${packageName}__Tactic_Template__c.*\.objectTranslation$/))
        //        toKeep.add(Pattern.compile(/^${packageName}__Unit_of_Measure__c.*\.objectTranslation$/))
        //        toKeep.add(Pattern.compile(/^${packageName}__Workflow_State_Transition__c.*\.objectTranslation$/))
        //        installedPackageFilterSet.fullNamesFilterSet.put("CustomObjectTranslation", new FilterSet(toKeep, toSkip))
        //    }
        //    if ( true ) {
        //        List toKeep = new ArrayList()
        //        List toSkip = null
        //        toKeep.add(Pattern.compile(/^${packageName}__.*?$/))
        //        installedPackageFilterSet.fullNamesFilterSet.put("CustomPermission", new FilterSet(toKeep, toSkip))
        //    }
        //    if ( true ) {
        //        List toKeep = new ArrayList()
        //        List toSkip = null
        //        toKeep.add(Pattern.compile(/^${packageName}__.*?$/))
        //        installedPackageFilterSet.fullNamesFilterSet.put("CustomTab", new FilterSet(toKeep, toSkip))
        //    }
        //    if ( true ) {
        //        List toKeep = new ArrayList()
        //        List toSkip = null
        //        toKeep.add(Pattern.compile(/^.*?\.${packageName}__.*?$/))
        //        installedPackageFilterSet.fullNamesFilterSet.put("FieldSet", new FilterSet(toKeep, toSkip))
        //    }
        //    //bcs this globalpicklist can not be deployed if ( true ) {
        //    //    List toKeep = new ArrayList()
        //    //    List toSkip = null
        //    //    toKeep.add(Pattern.compile(/^${packageName}__.*?$/))
        //    //    installedPackageFilterSet.fullNamesFilterSet.put("GlobalValueSet", new FilterSet(toKeep, toSkip))
        //    //}
        //    if ( true ) {
        //        List toKeep = new ArrayList()
        //        List toSkip = null
        //        toKeep.add(Pattern.compile(/^${packageName}__.*?$/))
        //        toKeep.add(Pattern.compile(/^.*?-${packageName}__.*?$/))
        //        installedPackageFilterSet.fullNamesFilterSet.put("Layout", new FilterSet(toKeep, toSkip))
        //    }
        //    if ( true ) {
        //        List toKeep = new ArrayList()
        //        List toSkip = null
        //        toKeep.add(Pattern.compile(/^${packageName}__.*?$/))
        //        installedPackageFilterSet.fullNamesFilterSet.put("StaticResource", new FilterSet(toKeep, toSkip))
        //    }
        //    if ( true ) {
        //        List toKeep = new ArrayList()
        //        List toSkip = null
        //        toKeep.add(Pattern.compile(/^.*?\.${packageName}__.*?$/))
        //        installedPackageFilterSet.fullNamesFilterSet.put("ValidationRule", new FilterSet(toKeep, toSkip))
        //    }
        //    metadataFilter.installedPackagesFilterSet.put(packageName, installedPackageFilterSet)
        //}
    }

    /**
     * Add file name filters
     */
    private void addFileNameFilter() {
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("ActionLinkGroupTemplate", new FilterSet(toKeep, toSkip))
        //}
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("AnalyticSnapshot", new FilterSet(toKeep, toSkip))
        //}
        //Development
        /*if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("ApexClass", new FilterSet(toKeep, toSkip))
        }
          //Configuration
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("cmsConnectSource", new FilterSet(toKeep, toSkip))
        }
          //Development
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("ApexComponent", new FilterSet(toKeep, toSkip))
        }*/
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("ApexPage", new FilterSet(toKeep, toSkip))
        //}
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("ApexTestSuite", new FilterSet(toKeep, toSkip))
        //}
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("ApexTrigger", new FilterSet(toKeep, toSkip))
        //}
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("AppMenu", new FilterSet(toKeep, toSkip))
        //}
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("ApprovalProcess", new FilterSet(toKeep, toSkip))
        //}
        //Configuration: Does not work on file names filter!
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fullNamesFilterSet.put("AssignmentRule", new FilterSet(toKeep, toSkip))
        }
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("AssignmentRules", new FilterSet(toKeep, toSkip))
        }
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("AssistantRecommendationType", new FilterSet(toKeep, toSkip))
        //}
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("AuraDefinitionBundle", new FilterSet(toKeep, toSkip))
        //}
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("AuthProvider", new FilterSet(toKeep, toSkip))
        //}
        //Configuration: Does not work on file names filter!
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fullNamesFilterSet.put("AutoResponseRule", new FilterSet(toKeep, toSkip))
        }
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("AutoResponseRules", new FilterSet(toKeep, toSkip))
        }
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("BrandingSet", new FilterSet(toKeep, toSkip))
        //}
        //Development: Does not work on file names filter!
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fullNamesFilterSet.put("BusinessProcess", new FilterSet(toKeep, toSkip))
        }
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("CallCenter", new FilterSet(toKeep, toSkip))
        //}
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("CaseSubjectParticle", new FilterSet(toKeep, toSkip))
        //}
        //Configuration
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("Certificate", new FilterSet(toKeep, toSkip))
        }
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("ChannelLayout", new FilterSet(toKeep, toSkip))
        //}
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("ChatterExtension", new FilterSet(toKeep, toSkip))
        //}
        //Configuration
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("CleanDataService", new FilterSet(toKeep, toSkip))
        }
        //Configuration
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("Community", new FilterSet(toKeep, toSkip))
        }
        //Configuration
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("CommunityTemplateDefinition", new FilterSet(toKeep, toSkip))
        }
        //Configuration
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("CommunityThemeDefinition", new FilterSet(toKeep, toSkip))
        }
        //Development: Does not work on file names filter!
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fullNamesFilterSet.put("CompactLayout", new FilterSet(toKeep, toSkip))
        }
        //Development
        if (CreateFor.RETRIEVE == createFor ) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("ConnectedApp", new FilterSet(toKeep, toSkip))
        } else if (CreateFor.DEPLOY == createFor ) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("ConnectedApp", new FilterSet(toKeep, toSkip))
        }
        //Configuration
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("ContentAsset", new FilterSet(toKeep, toSkip))
        }
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("CorsWhitelistOrigin", new FilterSet(toKeep, toSkip))
        //}
        //Configuration
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("CspTrustedSite", new FilterSet(toKeep, toSkip))
        }
        //Development
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("CustomApplication", new FilterSet(toKeep, toSkip))
        }
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("CustomApplicationComponent", new FilterSet(toKeep, toSkip))
        //}
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("CustomFeedFilter", new FilterSet(toKeep, toSkip))
        //}
        //Development: Does not work on file names filter!
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fullNamesFilterSet.put("CustomField", new FilterSet(toKeep, toSkip))
        }
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("CustomLabel", new FilterSet(toKeep, toSkip))
        //}
        //Development
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("CustomMetadata", new FilterSet(toKeep, toSkip))
        }
        //Development
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("CustomObject", new FilterSet(toKeep, toSkip))
        }
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //      toKeep.add(Pattern.compile(/^objectTranslations\/CCB2B_AccountSellerInfo__c-sh\.objectTranslation$/))
        //    metadataFilter.fileNamesFilterSet.put("CustomObjectTranslation", new FilterSet(toKeep, toSkip))
        //}
        //Configuration
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //      toKeep.add(Pattern.compile(/^objectTranslations\/CCB2B_AccountSellerInfo__c-sh\.objectTranslation$/))
        //    metadataFilter.fileNamesFilterSet.put("CustomObjectTranslation", new FilterSet(toKeep, toSkip))
        //}
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("CustomPageWebLink", new FilterSet(toKeep, toSkip))
        //}
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("CustomPermission", new FilterSet(toKeep, toSkip))
        //}
        //Configuration
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("CustomSite", new FilterSet(toKeep, toSkip))
        }
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("CustomTab", new FilterSet(toKeep, toSkip))
        //}
        //Development
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("Dashboard", new FilterSet(toKeep, toSkip))
        }
        //Configuration
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("DataCategoryGroup", new FilterSet(toKeep, toSkip))
        }
        //Configuration
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("DelegateGroup", new FilterSet(toKeep, toSkip))
        }
        //Development
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("Document", new FilterSet(toKeep, toSkip))
        }
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("DuplicateRule", new FilterSet(toKeep, toSkip))
        //}
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("EclairGeoData", new FilterSet(toKeep, toSkip))
        //}
        //Development
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("EmailTemplate", new FilterSet(toKeep, toSkip))
        }
       //Configuration
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("EmailServicesFunction", new FilterSet(toKeep, toSkip))
        }
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("EmbeddedServiceBranding", new FilterSet(toKeep, toSkip))
        //}
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("EmbeddedServiceConfig", new FilterSet(toKeep, toSkip))
        //}
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("EmbeddedServiceLiveAgent", new FilterSet(toKeep, toSkip))
        //}
        //Configuration: Does not work on file names filter!
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fullNamesFilterSet.put("EscalationRule", new FilterSet(toKeep, toSkip))
        }
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("EscalationRules", new FilterSet(toKeep, toSkip))
        }
        //Undefined
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("EventDelivery", new FilterSet(toKeep, toSkip))
        //}
        //Undefined
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("EventSubscription", new FilterSet(toKeep, toSkip))
        //}
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("ExternalDataSource", new FilterSet(toKeep, toSkip))
        //}
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("ExternalServiceRegistration", new FilterSet(toKeep, toSkip))
        //}
        //Development: Does not work on file names filter!
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fullNamesFilterSet.put("FieldSet", new FilterSet(toKeep, toSkip))
        }
        //Configuration
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    toSkip.add(Pattern.compile(/^flexipages\/.*?$/))
        //    metadataFilter.fileNamesFilterSet.put("FlexiPage", new FilterSet(toKeep, toSkip))
        //}
        //Development
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("Flow", new FilterSet(toKeep, toSkip))
        }
        //Development
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("FlowDefinition", new FilterSet(toKeep, toSkip))
        }
        //Development
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("GlobalValueSet", new FilterSet(toKeep, toSkip))
        }
        //Configuration
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("GlobalValueSetTranslation", new FilterSet(toKeep, toSkip))
        }
        //Development
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("Group", new FilterSet(toKeep, toSkip))
        }
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("HomePageComponents", new FilterSet(toKeep, toSkip))
        //}
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("HomePageLayouts", new FilterSet(toKeep, toSkip))
        //}
        //Development
        if (CreateFor.RETRIEVE == createFor ) {
            //List toKeep = new ArrayList();
            //List toSkip = new ArrayList();
            //metadataFilter.fileNamesFilterSet.put("InstalledPackage", new FilterSet(toKeep, toSkip))
        } else if (CreateFor.DEPLOY == createFor ) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            //toSkip.add(Pattern.compile(/^installedPackages\/.*?$/))
              //toKeep.add(Pattern.compile(/^installedPackages\/FieloSHS.installedPackage?$/))
            metadataFilter.fileNamesFilterSet.put("InstalledPackage", new FilterSet(toKeep, toSkip))
        }
        //Configuration
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("KeywordList", new FilterSet(toKeep, toSkip))
        }
        //Development
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("Layout", new FilterSet(toKeep, toSkip))
        }
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("LeadConvertSettings", new FilterSet(toKeep, toSkip))
        //}
        //Development
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("Letterhead", new FilterSet(toKeep, toSkip))
        }
        //Development: Does not work on file names filter!
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fullNamesFilterSet.put("ListView", new FilterSet(toKeep, toSkip))
        }
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("LiveChatAgentConfig", new FilterSet(toKeep, toSkip))
        //}
        //Development (not yet deployed)
        if (CreateFor.RETRIEVE == createFor ) {
            //List toKeep = new ArrayList();
            //List toSkip = new ArrayList();
            //metadataFilter.fileNamesFilterSet.put("LiveChatButton", new FilterSet(toKeep, toSkip))
        } else if (CreateFor.DEPLOY == createFor ) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("LiveChatButton", new FilterSet(toKeep, toSkip))
        }
        //Undefined
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("LiveChatDeployment", new FilterSet(toKeep, toSkip))
        //}
        //Undefined
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("LiveChatSensitiveDataRule", new FilterSet(toKeep, toSkip))
        //}
        //Configuration: Does not work on file names filter!
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fullNamesFilterSet.put("ManagedTopic", new FilterSet(toKeep, toSkip))
        }
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("ManagedTopics", new FilterSet(toKeep, toSkip))
        }
        //Development: Does not work on file names filter!
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fullNamesFilterSet.put("MatchingRule", new FilterSet(toKeep, toSkip))
        //}
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("MatchingRules", new FilterSet(toKeep, toSkip))
        //}
        //Configuration
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("ModerationRule", new FilterSet(toKeep, toSkip))
        }
        //Configuration
        // if (true) {
        //     List toKeep = new ArrayList();
        //     List toSkip = new ArrayList();
        //     toSkip.add(Pattern.compile(/^namedCredentials\/.*?$/))
        //     metadataFilter.fileNamesFilterSet.put("NamedCredential", new FilterSet(toKeep, toSkip))
        // }
        //Configuration
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("Network", new FilterSet(toKeep, toSkip))
        }
        //Configuration
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("NetworkBranding", new FilterSet(toKeep, toSkip))
        }
        //Development
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("PathAssistant", new FilterSet(toKeep, toSkip))
        }
        //Development
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("PermissionSet", new FilterSet(toKeep, toSkip))
        }
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("PlatformCachePartition", new FilterSet(toKeep, toSkip))
        //}
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("PostTemplate", new FilterSet(toKeep, toSkip))
        //}
        //Development (not yet deployed)
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("Profile", new FilterSet(toKeep, toSkip))
        }
        //Configuration
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("ProfilePasswordPolicy", new FilterSet(toKeep, toSkip))
        }
        //Configuration
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("ProfileSessionSetting", new FilterSet(toKeep, toSkip))
        }
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("Queue", new FilterSet(toKeep, toSkip))
        //}
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("QuickAction", new FilterSet(toKeep, toSkip))
        //}
        //Development: Does not work on file names filter!
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fullNamesFilterSet.put("RecordType", new FilterSet(toKeep, toSkip))
        }
        //Development
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("RemoteSiteSetting", new FilterSet(toKeep, toSkip))
        }
        //Development
//        if (true) {
//            List toKeep = new ArrayList();
//            List toSkip = new ArrayList();
//            toKeep.add(Pattern.compile(/^reports\/SPOT_Team-meta.xml$/))
//            metadataFilter.fileNamesFilterSet.put("Report", new FilterSet(toKeep, toSkip))
//        }
//        //Development
//        if (true) {
//            List toKeep = new ArrayList();
//            List toSkip = new ArrayList();
//            metadataFilter.fileNamesFilterSet.put("ReportType", new FilterSet(toKeep, toSkip))
//        }
        //Configuration
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("Role", new FilterSet(toKeep, toSkip))
        }
        //Configuration
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("SamlSsoConfig", new FilterSet(toKeep, toSkip))
        }
        //Configuration
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("Scontrol", new FilterSet(toKeep, toSkip))
        }
        //Configuration
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("ServiceChannel", new FilterSet(toKeep, toSkip))
        }
        //Development
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("Settings", new FilterSet(toKeep, toSkip))
        }
        //Undefined: Does not work on file names filter!
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("SharingCriteriaRule", new FilterSet(toKeep, toSkip))
        //}
        //Undefined: Does not work on file names filter!
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("SharingOwnerRule", new FilterSet(toKeep, toSkip))
        //}
        //Development: Does not work on file names filter!
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fullNamesFilterSet.put("SharingReason", new FilterSet(toKeep, toSkip))
        }
        //Development
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("SharingRules", new FilterSet(toKeep, toSkip))
        //}
        //Undefined: Does not work on file names filter!
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("SharingTerritoryRule", new FilterSet(toKeep, toSkip))
        //}
        //Configuration
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("SiteDotCom", new FilterSet(toKeep, toSkip))
        }
        //Undefined
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("Skill", new FilterSet(toKeep, toSkip))
        //}
        //Development
        //The value sets are not returned when describing this type.
        //Therefore the list of value sets to retrieve is added in the patch target.
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    metadataFilter.fileNamesFilterSet.put("StandardValueSet", new FilterSet(toKeep, toSkip))
        //}
        //Configuration
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("StandardValueSetTranslation", new FilterSet(toKeep, toSkip))
        }
        //Development
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
           // toSkip.add(Pattern.compile(/^staticresources\/.*?$/))
            metadataFilter.fileNamesFilterSet.put("StaticResource", new FilterSet(toKeep, toSkip))
        }
        //Configuration
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("SynonymDictionary", new FilterSet(toKeep, toSkip))
        }
        //Development
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("Territory", new FilterSet(toKeep, toSkip))
        }
        //Development
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("TopicsForObjects", new FilterSet(toKeep, toSkip))
        }
        //Configuration
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("TransactionSecurityPolicy", new FilterSet(toKeep, toSkip))
        }
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("Profile", new FilterSet(toKeep, toSkip))
        }
        //Configuration
        //if (true) {
        //    List toKeep = new ArrayList();
        //    List toSkip = new ArrayList();
        //    toSkip.add(Pattern.compile(/^translations\/.*?$/))
        //    metadataFilter.fileNamesFilterSet.put("Translations", new FilterSet(toKeep, toSkip))
        //}
        //Configuration
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("UserCriteria", new FilterSet(toKeep, toSkip))
        }
        //Development: Does not work on file names filter!
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fullNamesFilterSet.put("ValidationRule", new FilterSet(toKeep, toSkip))
        }
        //Development: Does not work on file names filter!
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fullNamesFilterSet.put("WebLink", new FilterSet(toKeep, toSkip))
        }
        //Development
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fileNamesFilterSet.put("Workflow", new FilterSet(toKeep, toSkip))
        }
        //Development: Does not work on file names filter!
        if (true) {
            List toKeep = new ArrayList()
            List toSkip = new ArrayList()
            metadataFilter.fullNamesFilterSet.put("WorkflowAlert", new FilterSet(toKeep, toSkip))
        }
        //Development: Does not work on file names filter!
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fullNamesFilterSet.put("WorkflowFieldUpdate", new FilterSet(toKeep, toSkip))
        }
        //Development: Does not work on file names filter!
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fullNamesFilterSet.put("WorkflowKnowledgePublish", new FilterSet(toKeep, toSkip))
        }
        //Development: Does not work on file names filter!
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fullNamesFilterSet.put("WorkflowOutboundMessage", new FilterSet(toKeep, toSkip))
        }
        //Development: Does not work on file names filter!
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fullNamesFilterSet.put("WorkflowSend", new FilterSet(toKeep, toSkip))
        }
        //Development: Does not work on file names filter!
        if (true) {
            List toKeep = new ArrayList();
            List toSkip = new ArrayList();
            metadataFilter.fullNamesFilterSet.put("WorkflowTask", new FilterSet(toKeep, toSkip))
        }
    }
}
