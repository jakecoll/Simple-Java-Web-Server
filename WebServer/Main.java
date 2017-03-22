package WebServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by jakecoll on 2/1/17.
 */

public class Main {

    private ServerSocket serverSocket;
    private Socket socket;
    private ConnectionHandler connectionHandler;

    private Boolean bAcceptingRequests = true;

    public static void main(String[] argv) throws Exception {

        //Server port
        int port = -1;

        //parse port number from command line
        for (String arg : argv) {
            String[] splitArg = arg.split("=");
            if (splitArg.length == 2 && splitArg[0].equals("--serverPort")) {
                port = Integer.parseInt(splitArg[1]);
            } else {
                System.err.println("Usage: java Server --serverPort=<port>");
                return;
            }
        }

        //run server
        new Main().runServer(port);
    }

    /**
     * Method creates socket
     * @param port
     * @throws Exception
     */
    public void runServer(int port) throws Exception {


        System.out.println("Server has started");

        //create server socket bound to port
        serverSocket = new ServerSocket(port);

        acceptRequests();
    }

    /**
     * Method sets socket to listen for requests and opens a thread for new requests
     * @throws Exception
     */
    private void acceptRequests() throws Exception {

        while(bAcceptingRequests) {

            //socket will listen for requests
            socket = serverSocket.accept();

            //the thread
            connectionHandler = new ConnectionHandler(socket);
            connectionHandler.start();

        }
    }
}
