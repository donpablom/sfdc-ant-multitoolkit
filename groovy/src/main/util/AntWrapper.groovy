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
        println "START: ANT WRAPPER"

        if ( null == antBuilder ) {
            antBuilder = new AntBuilder()
            antBuilder.taskdef(resource: "com/salesforce/antlib.xml"){
                classpath() {
                    pathelement(location: "./b2b_toolkit/lib/ant-salesforce.jar")
                }
            }
        }
        println "END: ANT WRAPPER"
    }
}
