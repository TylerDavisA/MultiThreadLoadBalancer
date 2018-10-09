import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A server program which accepts requests from clients to
 * capitalize strings.  When clients connect, a new thread is
 * started to handle an interactive dialog in which the client
 * sends in a string and the server thread sends back the
 * capitalized version of the string.
 *
 * The program is runs in an infinite loop, so shutdown in platform
 * dependent.  If you ran it from a console window with the "java"
 * interpreter, Ctrl+C generally will shut it down.
 */
public class CapitalizeServer {
    public static final int BUSY_SERVER = -1;
    public static final int CLOSE_CLIENT = -2;

    public static Thread manager;
    public static ThreadPool pool;
    public static MyMonitor monitor;
    public static ServerSocket listener;
    private static boolean isDone;

    /**
     * Application method to run the server runs in an infinite loop
     * listening on port 9898.  When a connection is requested, it
     * spawns a new thread to do the servicing and immediately returns
     * to listening.  The server keeps a unique client number for each
     * client that connects just to show interesting logging
     * messages.  It is certainly not necessary to do this.
     */
    public static void main(String[] args) throws Exception {
        Queue<Capitalizer> capitalizers = new LinkedList<>();
        isDone = false;

        monitor = new MyMonitor();
        pool = new ThreadPool(monitor);
        manager = new Thread(new ThreadManager(pool, monitor));
        manager.start();
        System.out.println("The capitalization server is running.");
        int clientNumber = 0;
        listener = new ServerSocket(9898);
        try {
            while (!isDone) {
                Capitalizer c;
                c = new Capitalizer(listener.accept(), clientNumber++);
                capitalizers.add(c);
                c.start();
                System.out.println("Made new capitalizer");
            }

        }catch(Exception e){
            if(!isDone)
                System.out.println("Error from making capitalizer");
            else{
                System.out.println("Server terminating...");
                for(Capitalizer t: capitalizers){
                    t.getSocket().close();
                    t.join();
                }
            }
        }
        finally {
            listener.close();
        }
    }

    /*
     * A private thread to handle capitalization requests on a particular
     * socket.  The client terminates the dialogue by sending a single line
     * containing only a period.
     */
    private static class Capitalizer extends Thread {
        private Socket socket;
        private int clientNumber;

        public Capitalizer(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            log("New connection with client# " + clientNumber + " at " + socket);
        }

        /**
         * Services this thread's client by first sending the
         * client a welcome message then repeatedly reading strings
         * and sending back the capitalized version of the string.
         */
        public void run() {
            try {

                // Decorate the streams so we can send characters
                // and not just bytes.  Ensure output is flushed
                // after every newline.
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                // Send a welcome message to the client.
                out.println("Hello, you are client #" + clientNumber + ".");
                out.println("Enter a line with only a period to quit\n");

                // Get messages from the client, line by line; return them
                // capitalized
                while (true) {
                    if(isDone)
                        break;
                    int result;
                    String input = in.readLine();
                    //System.out.println(""+input);
                    if (input == null) {
                        break;
                    }

                    if(monitor.getSize() >= 50){
                        out.println("The server is currently busy, please connect later");
                    }
                    else {
                        result = monitor.setJob(new Job(input, clientNumber, socket, out));
                        if(result == CLOSE_CLIENT)
                        break;
                        if (input.equals("KILL")) {
                            try {
                                manager.join();
                                isDone = true;
                                break;
                            } catch (Exception e) {

                            }
                        }
                        //System.out.println("Hello client: "+clientNumber);
                    }
                }
            } catch (IOException e) {
                //log("Error handling client# " + clientNumber + ": " + e);
            }
            finally {
                try {
                    socket.close();
                    if(isDone)
                        listener.close();
                } catch (IOException e) {
                    log("Couldn't close a socket, what's going on?");
                }
                log("Connection with client# " + clientNumber + " closed");
            }
        }

        /**
         * Logs a simple message.  In this case we just write the
         * message to the server applications standard output.
         */
        private void log(String message) {
            System.out.println(message);
        }
        private Socket getSocket(){return socket;}
    }
}
