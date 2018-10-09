
import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class MultiClient {






    class SenderThread implements Runnable{
        private BufferedReader in;
        private PrintWriter out;
        private String command;
        private Random rand = new Random();
        public SenderThread(){
            try {
                connectToServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run() {
            getCommand();


            String response = "";
            out.println(command);
            try {


                while((response = in.readLine()) == null){
                    //System.out.println("waiting for line: "+response);
                    Thread.sleep(50);
                }
                if (response == null || response.equals("")) {
                    System.out.println("client to terminate.");
                    System.exit(0);
                }
                System.out.println("response is: "+response);




            } catch (IOException ex) {
                response = "Error: " + ex;
                System.out.println("" + response + "\n");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        private void getCommand(){
            int temp = rand.nextInt(5);
            if(temp == 0){
                command = "ADD,5,6";
            }
            if(temp == 1)
                command = "SUB,10,5";
            if(temp == 2)
                command = "MUL,2,6";
            if(temp == 3)
                command = "DIV,10,2";
            if(temp == 4)
                command = "spam";

        }
        public void connectToServer() throws IOException {

            // Get the server address from a dialog box.


            // Make connection and initialize streams
            String serverAddress = "localhost";
            Socket socket = new Socket(serverAddress, 9898);
            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            /*for (int i = 0; i < 3; i++) {
                System.out.println(in.readLine() + "\n");
            }*/
        }
    }

    private Thread threads[];
    private Thread thread;

    public void init(){
        int max = 100;
        threads = new Thread[max];
        for(int i = 0; i < max; i++){
            threads[i]= new Thread(new SenderThread());
        }
        for(int i =0; i < max; i++){
            threads[i].start();
        }
        for(int i =0; i < max; i++){
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        /*thread = new Thread(new SenderThread());
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    public static void main(String[] args) throws Exception {
        MultiClient clients = new MultiClient();
        clients.init();

    }
}
