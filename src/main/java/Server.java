import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

    @Override
    public void run() {
        try {
            ServerSocket server = new ServerSocket(80); // establish a new server and listen on port 80
            Socket client = server.accept(); // when the server accepts a connection a new client socket is created
        } catch (IOException e) {
            // TODO: implement a shutdown function
        }

    }

    class ConnenctionHandler implements Runnable {

        private Socket client; // the client socket will provide input and output streams
        private BufferedReader in; // input stream passed to BufferedReader
        private PrintWriter out; // output stream passed to PrintWriter

        // constructor to handle when a new user connects
        public ConnenctionHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                // establish stream reading and writing from the client
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                // prompt client for a nickname
                
            } catch (IOException e) {
                // TODO: handle it, shutdown?
            }
        }
    }
}
