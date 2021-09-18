
//*********************************************************************************************//
//  Class:  Buffer                                                                             //
//  Author: Mick Wiedermann                                                                    //
//  Course: COMP2240 | Assignment 2.2                                                          //
//  Date  : 2021-09-18                                                                         //
//  Description: The "Monitor" class sitting between the JonThreads and the PrintHead Threads  //
//***********************************************************************************************

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public class Buffer {
    
    protected boolean flag;
    protected boolean signal;
    protected String type;
    protected volatile int count;
    protected int jobCount;
    protected ArrayBlockingQueue<Job> readyQm;
    protected ArrayBlockingQueue<Job> readyQc;
    private ReentrantLock bufferLock;
    protected volatile int clock;
    private int time;

    public Buffer(int jobCount) {
        this.jobCount = jobCount;
        this.flag = true;
        this.signal = false;
        this.type = "M";
        this.count = 0;
        this.readyQm = new ArrayBlockingQueue<>(jobCount);
        this.readyQc = new ArrayBlockingQueue<>(jobCount);
        this.bufferLock = new ReentrantLock(true);
        this.clock = 0;
        this.time = 0;
    }

    // Access control flag 1 
    protected void awaitFlag() throws InterruptedException {
        while (!flag) {
            wait();
        }
    }

    // Access control flag 2
    protected void awaitSignal() throws InterruptedException {
        while (!signal) {
            wait();
        }
    }

    // Sycned method for the threads to add jobs to the printer queue.
    public synchronized boolean add2ReadyQ(Job job) {
        try {
            awaitFlag();
        } catch (InterruptedException e) {
            //
        }
        this.flag = false;
        try {
            if (job.getType().equals("M")) {
                this.readyQm.add(job);
                count++;
                if (count%3 == 0 || countType(job.getType()) < 3) {
                    this.signal = true;
                }
                this.flag = true;
                notifyAll();
                return true;
            } else if (job.getType().equals("C")) {
                this.readyQc.add(job);
                count++;
                if (count%3 == 0 || countType(job.getType()) < 3) {
                    this.signal = true;
                }
                this.flag = true;
                notifyAll();
                return true;
            }
            return false;
        } finally {
            notifyAll();
        }
    }

    // Synced method for the threads to remove to print. 
    public synchronized Job popReadyQ() {
        try {
            awaitSignal();
        } catch (InterruptedException e) {
            //
        }
        this.bufferLock.lock();
        this.signal = false;
        reOrderMQ();
        reOrderCQ();
        try {
            if (type.equals("M") && !readyQm.isEmpty()) {
                this.count--;
                proposeTime(readyQm.peek().getNumPages()+ this.clock);
                updateClock();
                return readyQm.remove();
            } else {
                this.count--;
                proposeTime(readyQm.peek().getNumPages() + this.clock);
                updateClock();
                return readyQc.remove();
            }
        } finally {
            switchTime();
            this.signal = true;
            this.bufferLock.unlock();
            notifyAll();
        }
    }

    // Checks to see how many jobs available of a certain type
    public int countType(String type) {
        int size = 0;
        if (type.equals("M")) {
            size = readyQm.size();
        } else {
            size = readyQc.size();
        }
        return size;
    }

    // Reorders queue ready for printing 
    public void reOrderMQ() {
        int size = readyQm.size();
        boolean test = true;
        if (size > 0) {
            ArrayBlockingQueue<Job> tempQ = new ArrayBlockingQueue<>(size);
            while (test) {
                test = false;
                int lowest = 10;
                if (readyQm.size() > 1) {
                    for (int i=0; i<readyQm.size(); i++) {
                        if (readyQm.peek().getJobId() < lowest) {
                            lowest = readyQm.peek().getJobId();
                        }
                        readyQm.add(readyQm.remove());
                    }
                    for (int i=0; i<readyQm.size(); i++) {
                        if (readyQm.peek().getJobId() == lowest) {
                            tempQ.add(readyQm.remove());
                        } else {
                            readyQm.add(readyQm.remove());
                        }
                        test = true;
                    }
                } else {
                    tempQ.add(readyQm.remove());
                }
            }
            for (int i=0; i<size; i++) {
                readyQm.add(tempQ.remove());
            }
        }
    }

    // reorder queue ready fro printing.
    public void reOrderCQ() {
        int size = readyQc.size();
        boolean test = true;
        if (size > 0) {
            ArrayBlockingQueue<Job> tempQ = new ArrayBlockingQueue<>(size);
            while (test) {
                test = false;
                int lowest = 10;
                if (readyQc.size() > 1) {
                    for (int i=0; i<readyQc.size(); i++) {
                        if (readyQc.peek().getJobId() < lowest) {
                            lowest = readyQc.peek().getJobId();
                        }
                        readyQc.add(readyQc.remove());
                    }
                    for (int i=0; i<readyQc.size(); i++) {
                        if (readyQc.peek().getJobId() == lowest) {
                            tempQ.add(readyQc.remove());
                        } else {
                            readyQc.add(readyQc.remove());
                        }
                        test = true;
                    }
                } else {
                    tempQ.add(readyQc.remove());
                }
            }
            for (int i=0; i<size; i++) {
                readyQc.add(tempQ.remove());
            }
        }
    }

    public void proposeTime(int time) {
        if (this.time < time ) {
            this.time = time;
        }
    }

    public void setClock(int clock) {
        this.clock = clock;

    }

    public int getClock() {
        return this.clock;
    }

    public String getBufferType() {
        return this.type;
    }

    public void switchType() {
        if (this.type.equals("M")) {
            setType("C");
        } else {
            setType("M");
        }
    }

    public void switchTime() {
        if (count == 2 || count == 6) {
            switchType();
        }
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isMonQEmpty() {
        return this.readyQm.isEmpty();
    }

    public void updateClock() {
        if (count == 6 || count == 1) {
            this.clock = this.time;
        } else if (count == 2 ) {
            this.clock = this.time-1;
        }
    }
 
    public boolean isColQEmpty() {
        return this.readyQc.isEmpty();
    }

    public int getCount() {
        return this.count;
    }

    public int getJobCount() {
        return this.jobCount;
    }
}
