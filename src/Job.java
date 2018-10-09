import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.*;

public class Job{
    String cmd;
    Socket socket;
    PrintWriter out;
    String[] parts;
    int clientNum;
    public Job(String command, int clientNum, Socket socket, PrintWriter printWriter) {
        this.cmd = command;
        this.out = printWriter;
        this.clientNum = clientNum;
        this.socket = socket;
    }
    public int process(){
        //System.out.println("Processing Command: "+cmd+" to printwriter: "+out );

        if(Pattern.matches("^(ADD)[,][-]?[\\d]+[,]([-]?[\\d]+)$", cmd)){

            parts = cmd.split(",");
            int a = Integer.parseInt(parts[1]);
            int b = Integer.parseInt(parts[2]);
            out.println("Client "+clientNum+" result: "+a+" + "+b+" = "+(a+b));
            System.out.println("Client "+clientNum+" result: "+a+" + "+b+" = "+(a+b));
        }
        else if(Pattern.matches("^(SUB)[,][-]?[\\d]+[,]([-]?[\\d]+)$", cmd)){
            parts = cmd.split(",");
            int a = Integer.parseInt(parts[1]);
            int b = Integer.parseInt(parts[2]);
            out.println("Client "+clientNum+" result: "+a+" - "+b+" = "+(a-b));
            System.out.println("Client "+clientNum+" result: "+a+" - "+b+" = "+(a-b));
        }
        else if(Pattern.matches("^(MUL)[,][-]?[\\d]+[,]([-]?[\\d]+)$", cmd)){
            parts = cmd.split(",");
            int a = Integer.parseInt(parts[1]);
            int b = Integer.parseInt(parts[2]);
            out.println("Client "+clientNum+" result: "+a+" * "+b+" = "+(a*b));
            System.out.println("Client "+clientNum+" result: "+a+" * "+b+" = "+(a*b));
        }
        else if(Pattern.matches("^(DIV)[,][-]?[\\d]+[,]([-]?[\\d]+)$", cmd)){
            parts = cmd.split(",");
            int a = Integer.parseInt(parts[1]);
            int b = Integer.parseInt(parts[2]);
            out.println("Client "+clientNum+" result: "+a+" / "+b+" = "+((double)a/(double)b));
            System.out.println("Client "+clientNum+" result: "+a+" / "+b+" = "+((double)a/(double)b));

        }
        else if(Pattern.matches("^(KILL)$", cmd)){
            return -1;
        }
        else {
            out.println(cmd.toUpperCase());
            System.out.println("Client "+clientNum+" result: "+cmd.toUpperCase());
        }
        return 0;
    }
    public String toString(){
        return cmd;
    }
}