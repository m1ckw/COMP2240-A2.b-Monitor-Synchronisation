//*********************************************************************************************//
//  Class:  Printer                                                                            //
//  Author: Mick Wiedermann                                                                    //
//  Course: COMP2240 | Assignment 2.2                                                          //
//  Date  : 2021-09-18                                                                         //
//  Description: Printer Thread class operates as the three printing heads                     //
//***********************************************************************************************

public class Printer implements Runnable {

    private Buffer buffer;
    protected volatile boolean done; 
    private String type;
    private int clock;
    private int counter;

    public Printer(Buffer buffer) {
        this.buffer = buffer;
        this.done = false;
        this.type = "M";
        this.clock = 0;
        this.counter = 0;
    }

    // Prints the jobs as the thread returns the job object.
    public void print(Job job) {
        counter++;
        if (counter == 1) {
            type = job.getType();
            job.setStart(clock);
            clock = clock + job.getNumPages();
        } else {
            job.setStart(buffer.getClock());
            clock = clock + job.getNumPages();
        }
        System.out.println("(" + job.getStart() + ") " + job.getType()
            + job.getJobId() + " uses " + Thread.currentThread().getName() + " (time: "
            + job.getNumPages() + ")");
    }

    public void run() {
        while (!done) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                return;
            }
            if (buffer.isMonQEmpty() && buffer.isColQEmpty()) {
                setDone();
            } else {
                print(buffer.popReadyQ());
            } 
        }
    } 
    
    // run completion flag
    public void setDone() {
        this.done = true;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String getType() {
        return this.type;
    }

    public void setClock(int bufferClock) {
        if (bufferClock > this.clock) {
            this.clock = bufferClock;
        }
    }

}
