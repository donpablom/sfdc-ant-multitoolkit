package util

/**
 * A class to read input and error streams.
 */
public class StreamGobbler extends Thread {

    public String buffer = ""
    private InputStream is;
    private String type;

    /**
     * Constructor
     * @param is
     * @param type
     */
    public StreamGobbler(InputStream is, String type) {
        this.is = is;
        this.type = type;
    }

    /**
     * Run gobbler
     */
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null) {
                buffer += line+"\n"
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
