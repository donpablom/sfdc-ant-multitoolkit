package util

/**
 * An Ant wrapper
 */
public class AntWrapper {
    /**
     * The Ant Builder instance
     */
    public AntBuilder antBuilder = null

    /**
     * Returns an AntBuilder instance.
     * The instance also has the Salesforce tasks initialized.
     * @return
     */
    public AntWrapper() {
        println "AntWrapper start"

        if ( null == antBuilder ) {
            antBuilder = new AntBuilder()
            antBuilder.taskdef(resource: "com/salesforce/antlib.xml"){
                classpath() {
                    pathelement(location: "./sfdc-ant-multitoolkit/lib/ant-salesforce.jar")
                }
            }
        }
        println "AntWrapper end"
    }
}
