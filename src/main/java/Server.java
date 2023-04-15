import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {

    private ArrayList<ConnectionHandler> connections; // a list of connected clients
    private ServerSocket server; // socket representing the server
    private boolean done; // for checking if the server was shutdown
    private ExecutorService pool; // this is the Thread-pool everything will run on

    public Server() {
        connections = new ArrayList<>();
        done = false;
        pool = Executors.newCachedThreadPool();
    }
    @Override
    public void run() {
        try {
            server = new ServerSocket(80); // establish a new server and listen on port 80
//            pool = Executors.newCachedThreadPool();

            while (!done) {

                Socket client = server.accept(); // when the server accepts a connection a new client socket is created
                ConnectionHandler handler = new ConnectionHandler(client);
                connections.add(handler);
                pool.execute(handler); // this will run the ConnectionHandler run() function;
            }
        } catch (IOException e) {
            shutdown();
        }

    }

    public void broadcast(String message) {
        // server needs to broadcast a message to all clients in the list of connections
        for (ConnectionHandler ch : connections) {
            if (ch != null) {
                ch.sendMessage(message);
            }
        }
    }

    public void shutdown() {
        try {
            done = true;
            pool.shutdown();
            if (!server.isClosed()) {
                server.close();
            }
        } catch (IOException d) {
            // the d is for DEAL WITH IT!
        }
        for (ConnectionHandler ch : connections) {
            ch.shutdown();
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
                broadcast(nickname + " joined the chat!");

                // use a while loop to listen for messages from the client
                String message;
                while ((message = in.readLine()) != null) {
                    if(message.startsWith("/nick")) {

                        // /nick command will allow user to change nickname
                        String[] nickChange = message.split(" ", 2);
                        if (nickChange.length == 2) {
                            broadcast(nickname + " changed their handle to " + nickChange[1]);
                            System.out.println(nickname + " changed their handle to " + nickChange[1]);
                            nickname = nickChange[1];
                            out.println("Successfully changed handle to " + nickname);
                        } else {
                            out.println("No nickname provided!");
                        }

                    } else if (message.startsWith("/quit")) {
                        // /quit command will allow user to quit the application
                        broadcast(nickname + " left the chat!");
                    } else {
                        broadcast(nickname + ": " + message);
                    }
                }
            } catch (IOException e) {
                shutdown();
            }
        }

        // simple send a message function
        public void sendMessage(String message) {
            out.println(message);
        }

        public void shutdown() {
            try {
                in.close();
                out.close();
                if (!client.isClosed()) {
                    client.close();
                }
            } catch (IOException d) {
                // don't worry about it
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}
