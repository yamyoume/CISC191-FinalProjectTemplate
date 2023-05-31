package edu.sdccd.cisc191.template;

import java.net.*;
import java.io.*;
import java.sql.*;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This program is a server that takes connection requests on
 * the port specified by the constant LISTENING_PORT.  When a
 * connection is opened, the program sends the current time to
 * the connected socket.  The program will continue to receive
 * and process connections until it is killed (by a CONTROL-C,
 * for example).  Note that this server processes each connection
 * as it is received, rather than creating a separate thread
 * to process the connection.
 */
public class Server {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void start(int port) throws Exception {
        serverSocket = new ServerSocket(port);
        System.out.println("Server listening on port 4444");

        clientSocket = serverSocket.accept();

        while (true) {
            // Accept client connection
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress());

            // Handle client request
            handleClientRequest(clientSocket);
        }
    }

    private static void handleClientRequest(Socket clientSocket) {

        try (Connection connection = Database.getConnection();) {
            // Create a statement
            String sql = "SELECT * FROM entries";
            PreparedStatement ps = connection.prepareStatement(sql);
            // Execute the SQL query
            ResultSet resultSet = ps.executeQuery();

            // Convert the ResultSet to a JSON array
            JSONArray jsonArray = convertResultSetToJson(resultSet);

            // Convert the JSON array to string
            String jsonResponse = jsonArray.toString();

            // Send JSON response to the client
            OutputStream outputStream = clientSocket.getOutputStream();
            outputStream.write(jsonResponse.getBytes());

            // Close the client connection
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JSONArray convertResultSetToJson(ResultSet resultSet) throws SQLException {
        JSONArray jsonArray = new JSONArray();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (resultSet.next()) {
            JSONObject jsonObject = new JSONObject();

            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Object columnValue = resultSet.getObject(i);
                jsonObject.put(columnName, columnValue);
            }

            jsonArray.put(jsonObject);
        }

        return jsonArray;
    }

    public void stop() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }

    public static void main(String[] args) {

        Server server = new Server();
        try {
            server.start(4444);
            server.stop();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
} //end class Server
