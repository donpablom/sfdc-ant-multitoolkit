package info

/**
 * Environment information like pathes
 */
public class EnvironmentRetrieverInfo {
    public String targetDir
    public String tempDir
    public String forceExecutable

    /**
     * Constructor
     * @param targetDir
     * @param tempDir
     * @param forceExecutable
     */
    public EnvironmentRetrieverInfo(String targetDir,
                           String tempDir,
                           String forceExecutable) {
        this.targetDir = targetDir
        this.tempDir = tempDir
        this.forceExecutable = forceExecutable;
    }
}
