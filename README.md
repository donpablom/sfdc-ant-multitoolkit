# SFDC Multitoolkit 

**This toolkit is a result of work of many developers, however i cant even track back original creators. Code shared in good faith for benefit of humanity. I do not claim ownership neither I do not take responsibility**



# SFDC Multitoolkit 

        * Incremental package based on certain commit hash
        * Static code analyzee PMD with rules for apex included
        * Created to use simplyfy CI CD proccesses
        * Not so hard (JK) to include in your own pieline
         
    ## 101    
    
        Important files and directories 
        * build/build.xml <-- ya know ANT entry file
            Important targets
            *  delta.package
            *  delta.package.validate
            *  delta.package.run.tests
            *
        * build/build.properties <-- make sure to have context/current directory properly set 
        
            Variables:
                * sf.*   - all  salesforce ant deployment tool vars
                * git.versions  - commit has for diff
                * git.executable - how git is called from cli/terminal
                * component.jsonfile - points json file with class - test class pairs that are exceptions from common format 
                * component.*  - directories of specific part of code
            
        * build/artifact/   <- you want to look for your generated packages there
        
        * groovy/src/main/GenerateDeltaPackage.groovy
        
