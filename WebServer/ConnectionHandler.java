package WebServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;


/**
 * Created by jakecoll on 2/1/17.
 * This class will handle the connection that contains the requests
 */


public class ConnectionHandler extends Thread {


        private Socket socket;
        private BufferedReader bufferedReader;

        private HttpResponseHandler response;
        private HttpRequestHandler request;
        private String strRequest;


        /**
         * Constructor for multithreaded connection handler
         * @param s
         * @throws Exception
         */
        public ConnectionHandler(Socket s) throws Exception {

                socket = s;
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        }

        @Override
        public void run() {

                try {

                        //convert http request to readable string
                        strRequest = "";

                        while (bufferedReader.ready() || strRequest.length() == 0) {

                                strRequest += (char) bufferedReader.read();
                        }

                        //uncomment to print request in console
                        //System.out.println(strRequest);

                        //initialize new HttpRequest handler and pass request to it
                        request = new HttpRequestHandler(strRequest);

                        //initialize new HttpReponse handler and pass request handler to it
                        response = new HttpResponseHandler(request);

                        //Send the response to the client
                        response.sendResponse(socket.getOutputStream());

                        //close reader and socket after response sent
                        bufferedReader.close();
                        socket.close();


                } catch (Exception e) {
                        e.printStackTrace();
                }


        }

}
