package sample;

import java.io.*;
import java.net.*;

public class FileServer {
    private ServerSocket serverSocket = null;

    public FileServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void fileRequests() throws IOException {
        System.out.println("Server initialized on port!");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientConnectionHandler handler =
                    new ClientConnectionHandler(clientSocket);
            handler.start();
        }
    }

    public static void main(String[] args) {
        int port = 8080;

        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        try {
            FileServer server = new FileServer(port);
            server.fileRequests();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
