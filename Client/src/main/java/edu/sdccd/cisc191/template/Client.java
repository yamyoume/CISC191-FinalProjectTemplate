package edu.sdccd.cisc191.template;

import java.net.*;
import java.io.*;

/**
 * This program opens a connection to a computer specified
 * as the first command-line argument.  If no command-line
 * argument is given, it prompts the user for a computer
 * to connect to.  The connection is made to
 * the port specified by LISTENING_PORT.  The program reads one
 * line of text from the connection and then closes the
 * connection.  It displays the text that it read on
 * standard output.  This program is meant to be used with
 * the server program, DateServer, which sends the current
 * date and time on the computer where the server is running.
 */

public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }


    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
    public static void main(String[] args) {
        try (Socket socket = new Socket("127.0.0.1", 4444)) {
            // Receive JSON response from the server
            InputStream inputStream = socket.getInputStream();
            byte[] buffer = new byte[10240];
            int bytesRead = inputStream.read(buffer);

            String[] jsonResponse = null;
            if (bytesRead > 0) {
                jsonResponse = new String[]{new String(buffer, 0, bytesRead)};
//                System.out.println("Received JSON response:\n" + jsonResponse);
            }

            JavaFX javaFX = new JavaFX();
            javaFX.main(jsonResponse);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
} //end class Client

