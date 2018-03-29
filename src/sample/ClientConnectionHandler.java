package sample;
import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

public class ClientConnectionHandler extends Thread {
    private boolean DEBUG = true;
    private Socket socket = null;
    private BufferedReader input = null;
    private PrintWriter responseOut = null;
    private String serverDirPath = null;

    public ClientConnectionHandler(Socket socket, String path) {
        super();
        this.socket = socket;
        this.serverDirPath = path;

        try {
            input = new BufferedReader(
                    new InputStreamReader(this.socket.getInputStream()));
            responseOut = new PrintWriter(
                    this.socket.getOutputStream(),true);
        } catch (IOException e) {
            System.err.println("ERROR: " +
                    "IOException while initiating read/write connection");
            e.printStackTrace();
        }
    }
    public void run() {
        String request = null;

        try {
            request = input.readLine();
            if (DEBUG) System.out.println("Received " + request);
            getRequest(request);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
                responseOut.close();
                socket.close();
                if (DEBUG) System.out.println("closed Successfully");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void getRequest(String req) {
        // split up string to get request
        StringTokenizer tokenizer = new StringTokenizer(req);
        String command = tokenizer.nextToken();
        String fileName = "";
        if (command.equals("DIR")) {
            handleDIR();
        }
        else if (command.equals("UPLOAD")) {
            fileName = tokenizer.nextToken();
            handleUP(fileName);
        }
        else {
            fileName = tokenizer.nextToken();
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
        if (DEBUG) System.out.println("Sending files: " + fileNames);
        responseOut.println(fileNames);
    }
    private void handleUP(String filename) {
        // upload file from client to server
        try {
            PrintWriter writer = new PrintWriter(serverDirPath + filename);
            String buffer = input.readLine();
            while (null != buffer) {
                writer.println(buffer);
                buffer = input.readLine();
            }
            System.out.println("UPLOAD Complete");
            writer.close();
        } catch (IOException e) {
            System.err.println("ERROR: cannot upload file");
        }
    }
    private void handleDOWN(String filename) {
        // open file reader for server file
        try {
            if (DEBUG) System.out.println(serverDirPath + filename);
            BufferedReader readFile = new BufferedReader(new FileReader(
                    new File(serverDirPath + filename)));
            String buffer = null;
            // read from file and write to socket
            try {
                buffer = readFile.readLine();
            } catch (IOException e) {
                System.err.println("ERROR: " +
                        "IOException trying to read file");
            }
            while (null != buffer) {
                responseOut.println(buffer);
                try {
                    buffer = readFile.readLine();
                } catch (IOException e) {
                    System.err.println("ERROR: " +
                            "IOException trying to read file");
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: " + filename+ "does not exist?");
        }
    }
}
