import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable {

    private ArrayList<ConnectionHandler> connections;
    @Override
    public void run() {
        try {
            ServerSocket server = new ServerSocket(80); // establish a new server and listen on port 80
            Socket client = server.accept(); // when the server accepts a connection a new client socket is created
            ConnectionHandler handler = new ConnectionHandler(client);
            connections.add(handler);
        } catch (IOException e) {
            // TODO: implement a shutdown function
        }

    }

    public void broadcast(String message) {
        // shout a message to all clients in the list of connections
        for (ConnectionHandler ch : connections) {
            if (ch != null) {
                ch.sendMessage(message);
            }
        }
    }

    class ConnectionHandler implements Runnable {

        private Socket client; // the client socket will provide input and output streams
        private BufferedReader in; // input stream passed to BufferedReader
        private PrintWriter out; // output stream passed to PrintWriter

        private String nickname;

        // constructor to handle when a new user connects
        public ConnectionHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                // establish stream reading and writing from the client
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                // prompt client for a nickname
                out.println("Please enter a nickname: ");
                nickname = in.readLine();
                System.out.println(nickname + " connected");

            } catch (IOException e) {
                // TODO: handle it, shutdown?
            }
        }

        // simple send a message function
        public void sendMessage(String message) {
            out.println(message);
        }
    }
}
