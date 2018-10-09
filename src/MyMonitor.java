import java.util.LinkedList;
import java.util.Queue;

public class MyMonitor{
    private Queue<Job> q;
    private boolean writeable = true;
    private boolean readable = false;
    private int MAX_SIZE = 50;
    private boolean threadFinished = false;
    private int numRunning=0;
    private boolean isTimeToFinish = false;
    //private int patternCounter = 0;
    private Job job;
    public MyMonitor(){
        q = new LinkedList<Job>();

    }

    public synchronized int setJob(Job j){
        while ( !writeable) {
            try {
                if(isThreadFinished()) {
                    //System.out.println("THREAD FINISHED IN SET JOB");
                    return 0;
                }
                wait();
                //System.out.println("THREAD WOKE UP FROM SETJOB");
            }
            catch ( InterruptedException e ) {
                System.err.println( e.toString() );
            }
        }
        if(q.size() >= MAX_SIZE){
            return CapitalizeServer.BUSY_SERVER;
        }
        if (j.toString().equals(".")) {
            return CapitalizeServer.CLOSE_CLIENT;
        }
        readable = true;
        q.add(j);
        if(q.size() >= MAX_SIZE){
            writeable = false;

        }
        notifyAll();
        return 0;
    }

    public synchronized Job getJob(){
        while(!readable){
            try {
                wait();
            }
            catch ( InterruptedException e ) {
                return null;
            }
        }
        writeable = true;
        job = q.remove();
      if(q.size() == 0){
         readable = false;
      }
        notifyAll();
        return job;


    }
    public synchronized void setThreadsFinished(){
        writeable = false;
        readable = false;
        notifyAll();
        threadFinished = true;
    }
    public synchronized boolean isThreadFinished(){

        return threadFinished;
    }
    public synchronized int getSize(){
        return q.size();
    }

    public synchronized void incrementNumRunning(){
        numRunning++;
    }
    public synchronized void decrementNumRunning(){
        numRunning--;
    }
    public synchronized int getNumRunning(){return numRunning;}
    public synchronized void setIsTimeToFinish(){
        isTimeToFinish = true;
        writeable = false;
         }
    public synchronized boolean isTimeToFinished(){
        return (isTimeToFinish && q.isEmpty());
    }
    public synchronized void wakeUpAll(){notifyAll();}

}