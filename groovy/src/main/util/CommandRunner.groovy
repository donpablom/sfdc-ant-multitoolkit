package util;

/**
 * Class to run a command line
 */
public class CommandRunner {

    public static final String ERROR = "Error"
    /**
     * Run a command in the operation system command prompt
     * @param baseDir
     * @param command
     * @return array of stdout and stderr and exitcode
     */
    public static String[] runCommand(String baseDir, String command){
        String cmd = command
        println "Running command " + cmd
        def stdout = ""
        def stderr = ""
        def result = new String [3]
        Integer exit_code = 0

        try {
            def proc = cmd.execute(null, new File(baseDir))

            //Any error message?
            StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR")

            //Any output?
            StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT")

            //Kick them off
            errorGobbler.start();
            outputGobbler.start();

            //Any error???
            int exitVal = proc.waitFor();
            errorGobbler.join();
            outputGobbler.join();
            stdout = outputGobbler.buffer
            stderr = errorGobbler.buffer
            println "Executing command: " + command
            exit_code = proc.exitValue()
            println "${stdout}"
            result = [ stdout.toString(), "", exit_code.toString() ]
        } catch (Throwable t) {
            t.printStackTrace();
            stderr = t.getMessage()
            exit_code = 2
            result = [ "ERROR", stderr.toString(), exit_code.toString() ]
        } finally {
            println "${result}"
            return result
        }
    }
}
