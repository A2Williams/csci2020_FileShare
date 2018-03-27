package sample;
import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

public class ClientConnectionHandler extends Thread {
    private Socket socket = null;
    private BufferedReader input = null;
    private PrintWriter responseOut = null;
    private String serverDirPath = null;
    private String clientDirPath = null;
    public ClientConnectionHandler(Socket socket, String path) {
        super();
        this.socket = socket;
        this.serverDirPath = path;

        try {
            input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            responseOut = new PrintWriter(
                    socket.getOutputStream(),true);
        } catch (IOException e) {
            System.err.println("ERROR: " +
                    "IOException while initiating read/write connection");
            e.printStackTrace();
        }
    }

    public void run() {
        String request = null;

        try {
            clientDirPath = input.readLine();
            request = input.readLine();
            getRequest(request);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
                responseOut.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getRequest(String req) {
        // split up string to get request
        StringTokenizer tokenizer = new StringTokenizer(req);
        String command = tokenizer.nextToken();
        String fileName = tokenizer.nextToken();
        if (command.equals("DIR")) {
            handleDIR();
        }
        else if (command.equals("UPLOAD")) {
            handleUP(fileName);
        }
        else {
            handleDOWN(fileName);
        }
    }

    private void handleDIR() {
        // send list of files on server to client
        File[] serverFiles = new File(serverDirPath).listFiles();
        String fileNames = "";
        for (File f: serverFiles) {
            fileNames += f.getName() + " ";
        }
        responseOut.write(fileNames);
    }

    private void handleUP(String filename) {
        // upload file from client to server
    }

    private void handleDOWN(String filename) {
        // client downloads file from server
    }
}
