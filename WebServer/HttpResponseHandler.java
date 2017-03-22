package WebServer;

import java.io.*;
import java.io.File;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.time.*;
import java.net.InetAddress;

/**
 * Created by jakecoll on 2/1/17.
 */


public class HttpResponseHandler {

    private HttpRequestHandler httpRequest;
    private String strResponse;
    private String strRoot;
    private File requestFile;

    private FileInputStream fileInputStream;

    public static final String VERSION = "HTTP/1.1";
    private ArrayList<String> headers = new ArrayList<String>();
    private byte[] data;

    private Instant date;
    private String hostname;


    /**
     * Constructor for Http Response
     * @param request
     * @throws IOException
     */
    public HttpResponseHandler(HttpRequestHandler request) throws IOException {

        httpRequest = request;
        date = Instant.now();
        hostname = InetAddress.getLocalHost().getHostName();
        strRoot = "/home/jakecoll/54001/project1/www/";

        requestFile = new File(strRoot + httpRequest.getFileName());


        //If request is not HEAD or GET request return 403 forbidden
        if (!httpRequest.getRequestType().equals("HEAD") && !httpRequest.getRequestType().equals("GET")) {

            getHeaders("403 Forbidden");

        } else if (!requestFile.exists()) {

            //if file does not exist check if it is a predefinied redirect and send 301 Moved Permanently with location
            // Else send a 404 Not Found response

            try {

                FileReader fr = new FileReader(strRoot + "redirect.defs");
                BufferedReader reader = new BufferedReader(fr);

                String reqRdr = "";
                String urlRdr = "";

                for (String line = reader.readLine(); line != null; line = reader.readLine()) {

                    //System.out.println(line);

                    String[] parts = line.split(" ");
                    String tmpReq = parts[0];
                    String tmpUrl = parts[1];

                    if (tmpReq.equals(httpRequest.getFileName())) {
                        reqRdr = tmpReq;
                        urlRdr = tmpUrl;
                    } 
                }

                reader.close();


                if (reqRdr.equals(httpRequest.getFileName()))
                {
                    getHeadersForRedirect("301 Moved Permanently", urlRdr);

                } else {
                    getHeaders("404 Not Found");
                }

            } catch (FileNotFoundException e) {
                getHeaders("404 Not Found");
            }




        } else if (httpRequest.getRequestType().equals("HEAD")) {

            //If head request and file exists then send 200 OK response

            if (requestFile.exists()) {
                getHeaders("200 OK");
            } else {
                getHeaders("404 Not Found");
            }

        } else if (httpRequest.getRequestType().equals("GET")) {

            //If GET request and file exists send 200 response and update body response
            if (requestFile.exists()) {
                getHeaders("200 OK");
                getResponse(getBytes(requestFile));

            } else {
                getHeaders("404 NOT FOUND");
            }

        }

        //uncomment to see response in console
        //testResponse();

    }

    /**
     * update data bit array with body of Http Response for global access
     * @param responseBody
     */
    private void getResponse(byte[] responseBody) {
        data = responseBody;
    }

    /**
     * Get headers for all responses other than 301 Moved
     * @param status
     * @throws IOException
     */
    private void getHeaders(String status) throws IOException {

        headers.add(VERSION + " " + status + "\r\n");
        headers.add("Connection: close" + "\r\n");
        headers.add("Date: " + date.toString() + "\r\n");
        headers.add("Server: " + hostname + "\r\n");
        headers.add("Connection: close" + "\r\n");
        headers.add("Content-Length: " + requestFile.length() + "\r\n");
        headers.add("Content-Type: " + getContentType(requestFile) + "\r\n");

    }

    /**
     * Returns header for 301 with Location component so browser and redirect
     * @param status
     * @param link
     * @throws IOException
     */
    private void getHeadersForRedirect(String status, String link) throws IOException {

        headers.add(VERSION + " " + status + "\r\n");
        headers.add("Location: " + link + "\r\n");
        headers.add("Connection: close" + "\r\n");
        headers.add("Date: " + date.toString() + "\r\n");
        headers.add("Server: " + hostname + "\r\n");
        headers.add("Connection: close" + "\r\n");
        headers.add("Content-Length: " + requestFile.length() + "\r\n");
        headers.add("Content-Type: text/html" + "\r\n");

    }

    /**
     * Converts file to array of bytes
     * @param file
     * @return
     * @throws IOException
     */
    private byte[] getBytes(File file) throws IOException {

        int length = (int) file.length();
        byte[] arrBytes = new byte[length];
        InputStream inputStream = new FileInputStream(file);

        int i = 0;
        while (i < length) {
            int count = inputStream.read(arrBytes, i, (length-i));
            i += count;
        }

        inputStream.close();
        return arrBytes;

    }

    /**
     * Uses java's Files.probeContentType to return content type for headers
     * @param file
     * @return
     * @throws IOException
     */
    private String getContentType(File file) throws IOException {

        if (file.exists()) {

            return Files.probeContentType(file.toPath());
        } else {
            return null;
        }
    }

    /**
     * Method writes response as bytes to a portable output stream
     * @param outputStream
     * @throws IOException
     */
    public void sendResponse(OutputStream outputStream) throws IOException {
        DataOutputStream outputData = new DataOutputStream(outputStream);
        for (String header : headers) {
            outputData.writeBytes(header);
        }
        outputData.writeBytes("\r\n");
        if (data != null) {
            outputData.write(data);
        }
        outputData.writeBytes("\r\n");
        outputData.flush();
    }

    /**
     * Method reads redirect.defs file and returns a boolean value for whether a request should be redirected
     * @param redirects
     * @param filename
     * @return
     */
    private boolean checkForRedirect(ArrayList<String> redirects, String filename) {

        boolean IsARedirect = false;

        for (String path : redirects) {
            if (path.equals(filename)) {
                IsARedirect = true;
            }
        }

        return IsARedirect;
    }

    /**
     * Method for printing response in console
     */
    private void testResponse() {
        for (String header : headers) {
            System.out.println(header);
        }

        if (data != null) {
            System.out.println(new String(data, StandardCharsets.UTF_8));
        }
    }

}
