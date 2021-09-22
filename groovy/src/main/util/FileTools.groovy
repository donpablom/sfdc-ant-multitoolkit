package util

/**
 * Tools to deal with file system
 */
class FileTools {
    /**
     * Copy file from source to target.
     * Source file must exist.
     * Target base dir must exist.
     * @param source
     * @param target
     */
    static void copyFile(String source, String target) {
        def sourceFile = new File(source)
        def targetFile = new File(target)
        def parentTargetDir = targetFile.parentFile
        if ( ! parentTargetDir.exists() ) {
            parentTargetDir.mkdirs()
        }
        if ( targetFile.exists()) {
            targetFile.delete()
        }
        targetFile.createNewFile()
        targetFile << sourceFile.bytes
    }
    /**
     * Deletes a file. Does nothing if the file does not exist.
     * @param fileName
     */
    static void deleteFile(String fileName) {
        def file = new File(fileName)
        if ( file.exists() ) {
            file.delete()
        }
    }
}
