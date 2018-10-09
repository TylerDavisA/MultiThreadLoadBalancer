import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ThreadPool{
    //TODO MAKE A JOB CLASS TO CALL job.process() FROM WORKER THREAD.
    int maxCapacity;
    Thread holders[];	//stores the worker thread references
    int idCount = 0;
    MyMonitor jobQueue;	//shared by all WorkerThread in the pool and ThreadManager
//and the main server thread
    private DateTimeFormatter dtf;

    public ThreadPool(MyMonitor monitor){
        dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        this.jobQueue = monitor;
        holders = new Thread[40];
        for(int i = 0; i < 5; i++){
            holders[i] = new Thread(new WorkerThread());
        }
        maxCapacity = 5;
        startPool();
    }
    class WorkerThread implements Runnable{
//each Worker will grab a job in the jobQueue for
//processing if there are available jobs in the jobQueue.
        private int id;
        public WorkerThread(){
            id = idCount;
            idCount++;
        }
        public void run(){
		    while(true){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
                jobQueue.incrementNumRunning();

                Job job = jobQueue.getJob();
                LocalDateTime now = LocalDateTime.now();
                if(job != null)
                    System.out.println("Worker thread id="+id+" processed service request "+job.toString()+" at the time "+dtf.format(now));
                if(job == null)
                    return;

                if(job.process()== -1) {
                    //System.out.println("RETURN -1 IN WORKERTHREAD");
                    jobQueue.setIsTimeToFinish();//in manager, if interrupt, call
                }
                jobQueue.decrementNumRunning();

            }
        }
    }

    public void startPool() {
//start all available threads in the pool and Worker
//threads start to process jobs

        for(int i = 0; i < holders.length; i++){
            if(holders[i] != null){
                holders[i].start();
            }
        }
    }

    public void increaseThreadsInPool() {
    //double the threads in pool according to threshold
        LocalDateTime now = LocalDateTime.now();

        int newCapacity = maxCapacity * 2;
        for(int i = maxCapacity; i < newCapacity; i++){
            holders[i] = new Thread(new WorkerThread());
            holders[i].start();
        }
        maxCapacity = newCapacity;
        System.out.println("ThreadManager doubled number of threads in the pool at "+dtf.format(now)+" now total running threads in pool is "+maxCapacity);
    }

    public void decreaseThreadsInPool() throws InterruptedException {
//halve the threads in pool according to threshold
        LocalDateTime now = LocalDateTime.now();
        //System.out.println("trying to decrease num threads");
        int size = maxCapacity / 2;
        System.out.println();
        for(int i = size; i< holders.length; i++){
            if (holders[i] != null)
                holders[i].interrupt();

        }
        for(int i = size; i< holders.length; i++){
            if(holders[i] != null) {
                //System.out.println(""+i);
                holders[i].join();
                holders[i] = null;
                //System.out.println("finish "+i);
            }
        }
        maxCapacity = size;
        System.out.println("ThreadManager halved number of threads in the pool at "+dtf.format(now)+" now total running threads in pool is "+maxCapacity);

    }

    public void stopPool()  {
//terminate all threads in the pool gracefully
//all threads in pool terminate when a command KILL is sent through the client //	to the server.
        LocalDateTime now = LocalDateTime.now();
        System.out.println("Stopping threads in thread pool at the time "+dtf.format(now));

        for (Thread q:holders
             ) {
            if(q != null)
                q.interrupt();
        }
        jobQueue.wakeUpAll();
        for(Thread q:holders){
            try {
                if(q != null)
                    q.join();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

    }


    public int maxCapacity() {
		return maxCapacity;
    }


}