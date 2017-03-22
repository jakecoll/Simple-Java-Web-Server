package WebServer;


/**
 * Created by jakecoll on 2/1/17.
 */


public class HttpRequestHandler {

    private String filename;
    public String requestType;
    public String version;

    /**
     * Constructor for HttpRequestHandler
     * @param request
     */
    public HttpRequestHandler(String request) {

        String parts[] = request.split("\n");

        filename = parts[0].split(" ")[1];
        requestType = parts[0].split(" ")[0];
        version = parts[0].split(" ")[2];

    }

    /**
     * Method returns name of requested file as a string
     * @return
     */
    public String getFileName() {

        return filename;
    }

    /**
     * Method returns type of request (GET, HEAD, etc.) as a string
     * @return
     */
    public String getRequestType() {

        return requestType;
    }

    /**
     * Method returns version of http.
     * Not used because decided to hard code HTTP/1.1
     * @return
     */
    public String getVersion() {
        return version;
    }
}
