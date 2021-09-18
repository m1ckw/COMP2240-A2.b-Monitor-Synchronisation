//*********************************************************************************************//
//  Class:  JobThread                                                                          //
//  Author: Mick Wiedermann                                                                    //
//  Course: COMP2240 | Assignment 2.2                                                          //
//  Date  : 2021-09-18                                                                         //
//  Description: Extension of the Job class executed as a Thread                               //
//***********************************************************************************************

public class JobThread extends Job implements Runnable {
    
    protected volatile boolean done = false;
    private Job job;
    private Buffer buffer;

    public JobThread(Job job, Buffer buffer) {
        this.job = job;
        this.buffer = buffer;
    }

    public void run() {
        while (!done) {
            // Returns truu if succsefully added. 
            if (buffer.add2ReadyQ(job)) {
                setDone();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                return;
            }
        }
    } 
    
    public void setDone() {
        this.done = true;
    }

}
