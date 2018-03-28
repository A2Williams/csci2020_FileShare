package sample;

import java.io.*;
import java.net.*;

public class FileServer {
    private ServerSocket serverSocket = null;
    private File dir = null;

    public FileServer(int port, String dir) throws IOException {
        // initializes server socket connection and sharing directory
        // throws @ socket initialization
        if (new File(dir).isDirectory()) {
            this.dir = new File(dir);
            serverSocket = new ServerSocket(port);
            System.out.println("Server initialized on port!");
        } else {
            System.err.println("ERROR: shared folder has not been given");
        }
    }

    public void fileRequests() throws IOException {
        // accepts client connections
        // throws @ accepting socket
        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientConnectionHandler handler =
                    new ClientConnectionHandler(
                            clientSocket, dir.getPath());
            handler.start();
        }
    }

    public static void main(String[] args) {
        int port = 49000;

        try {
            FileServer server = new FileServer(port, args[0]);
            server.fileRequests();
        } catch (IOException e) {
            System.err.println("ERROR: " +
                    "IOException while creating server connection");
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
