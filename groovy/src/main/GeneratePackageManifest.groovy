package util
import groovy.xml.MarkupBuilder

Map<String, String> supportedObjects = [:]
supportedObjects.put('classes', 'ApexClass')
supportedObjects.put('pages', 'ApexPage')
supportedObjects.put('aura', 'AuraDefinitionBundle')
supportedObjects.put('triggers','ApexTrigger')
supportedObjects.put('appMenus','AppMenu')
supportedObjects.put('assignmentRules', 'AssignmentRules')
supportedObjects.put('autoResponseRules','AutoResponseRules')
supportedObjects.put('campaignInfluenceModels','CampaignInfluenceModel')
supportedObjects.put('connectedApps','ConnectedApp')
supportedObjects.put('applications','CustomApplication')
supportedObjects.put('labels','CustomLabels')
supportedObjects.put('objects','CustomObject')
supportedObjects.put('objectTranslations','CustomObjectTranslation')
supportedObjects.put('labels','CustomLabel')
supportedObjects.put('tabs','CustomTab')
supportedObjects.put('certs','Certificate')
supportedObjects.put('customMetadata','CustomMetadata')
supportedObjects.put('customPermissions','CustomPermission')
supportedObjects.put('dashboards','Dashboard')
supportedObjects.put('duplicateRules','DuplicateRule')
supportedObjects.put('escalationRules','EscalationRules')
supportedObjects.put('email','EmailTemplate')
supportedObjects.put('flexipages','FlexiPage')
supportedObjects.put('flows','Flow')
supportedObjects.put('flowDefinitions','FlowDefinition')
supportedObjects.put('globalValueSets','GlobalValueSet')
supportedObjects.put('groups','Group')
supportedObjects.put('homePageLayouts','HomePageLayout')
supportedObjects.put('layouts','Layout')
supportedObjects.put('LeadConvertSettings','LeadConvertSettings')
supportedObjects.put('matchingRules','MatchingRules')
supportedObjects.put('namedCredentials','NamedCredential')
supportedObjects.put('networkBranding','NetworkBranding')
supportedObjects.put('permissionsets','PermissionSet')
supportedObjects.put('profiles','Profile')
supportedObjects.put('profilePasswordPolicies','ProfilePasswordPolicy')
supportedObjects.put('profileSessionSettings','ProfileSessionSetting')
supportedObjects.put('queues','Queue')
supportedObjects.put('quickActions','QuickAction')
supportedObjects.put('remoteSiteSettings','RemoteSiteSetting')
supportedObjects.put('reports','Report')
supportedObjects.put('settings','Settings')
supportedObjects.put('sharingRules','SharingRules')
supportedObjects.put('standardValueSets','StandardValueSet')
supportedObjects.put('standardValueSetTranslations','StandardValueSetTranslation')
supportedObjects.put('staticresources','StaticResource')
supportedObjects.put('topicsForObjects','TopicsForObjects')
supportedObjects.put('translations','Translations')
supportedObjects.put('workflows','Workflow')
supportedObjects.put('contentassets','ContentAsset')
supportedObjects.put('approvalProcesses','ApprovalProcess')

String srcDir = "${properties['component.src']}".minus("../../")

println "GENERATEPACKAGEMANIFEST"
println "srcDIR vs component.src"
println "component.src:" + ${properties['component.src']}
println "srcDir:" + srcDir

Map<String, String> packageNodes = [:]
Set<String> useParentDir =['aura']
new File('b2b_toolkit/build/artifact/ctemp/deploy_delta-diff.txt').eachLine{
    line ->
    if(line.indexOf("${srcDir}/") > 0){
        String path = line.substring(line.indexOf("${srcDir}/")+4)
        println("path: " + path)
        if (path?.trim()) {
            if (!path.contains('meta.xml') && !path.contains('package.xml')) {
                path = path.substring(0, path.lastIndexOf('.'))
                String key = path.substring(0, path.indexOf('/'))
                String value;
                if (useParentDir.contains(key)) {
                    value = path.substring(path.indexOf('/')+1, path.indexOf('/', path.indexOf('/')+1))
                } else {
                    value = path.substring(path.indexOf('/') + 1)
                }
                if (packageNodes.containsKey(supportedObjects.get(key))) {
                    packageNodes.get(supportedObjects.get(key)).add(value)
                } else {
                    Set<String> elements = [value]
                    packageNodes.put(supportedObjects.get(key), elements)
                }
            }
        }
    }
}
String retrieveDir = "${properties['component.temp_retrieve']}"
File retrieveDirFile = new File(retrieveDir)
if ( ! retrieveDirFile.exists() ) {
    retrieveDirFile.mkdir()
}
def myList = [] //types that have already been added
def flag = false //to check if the type is added
String destDir = "${properties['component.temp_retrieve']}"+"/package.xml"
def fileWriter = new FileWriter(destDir)//add path to file
def packageFile = new MarkupBuilder(fileWriter)

packageFile.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8") //add declaration
packageFile.Package(xmlns: "http://soap.sforce.com/2006/04/metadata") {
    for (itemName in packageNodes.keySet()) {
        types() {
            for (element in packageNodes.get(itemName)) {
                members(element)
            }
            name(itemName)
        }
    }
    version(52.0)
}
fileWriter.close()

println(packageNodes)
