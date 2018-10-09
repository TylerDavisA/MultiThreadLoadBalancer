import static java.lang.Thread.sleep;

public class ThreadManager implements Runnable{
    //This class examines jobqueue's workload and will control the threadpool

    private final int T2 = 20;
    private final int T1 = 10;
    private ThreadPool threadPool;
    private MyMonitor jobQueue;

    public ThreadManager(ThreadPool threadPool, MyMonitor jobQueue ){
        this.threadPool = threadPool;
        this.jobQueue = jobQueue;
    }

    public void run(){
        while(true){
            //System.out.println("Hello from manager1");
            try{
                sleep(100);
                //System.out.println("Hello from manager");
                if(jobQueue.isTimeToFinished()){
                    System.out.println("KILLING OFF THREADS");
                    threadPool.stopPool();
                    jobQueue.setThreadsFinished();
                    //have server be done when this thread ends
                    System.out.println("FINISHED KILLING OFF THREADS");
                    return;
                }
                //System.out.println("Hello from manager2");
                int capacity = threadPool.maxCapacity();
                int numJobs = jobQueue.getSize();
                if(numJobs < T1 && capacity > 5){
                    //System.out.println("if1");

                    threadPool.decreaseThreadsInPool();
                }
                else if(numJobs >T1 && numJobs < T2){
                    //System.out.println("if2");
                    if(capacity < 10)
                        threadPool.increaseThreadsInPool();
                    else if(capacity > 10)
                        threadPool.decreaseThreadsInPool();
                }
                else if(numJobs >T2 && capacity < 20){
                    //System.out.println("if3");
                    threadPool.increaseThreadsInPool();
                }
                else if(numJobs ==50 && capacity == 20){
                    //System.out.println("if4");
                    threadPool.increaseThreadsInPool();
                }
                else if(numJobs <50 && capacity == 40){
                    //System.out.println("if5");
                    threadPool.decreaseThreadsInPool();
                }
                else
                {
                    //System.out.println("nothing");
                }
                //System.out.println("Hello from 3");

            }
            catch(InterruptedException e){
                System.out.println("INTERRUPT EXCEPTION FROM THREAD MANAGER");
            }
        }

    }
}