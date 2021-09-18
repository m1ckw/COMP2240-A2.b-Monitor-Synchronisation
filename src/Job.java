//*********************************************************************************************//
//  Class:  Job                                                                                //
//  Author: Mick Wiedermann                                                                    //
//  Course: COMP2240 | Assignment 2.2                                                          //
//  Date  : 2021-09-18                                                                         //
//  Description: Job Class to hold the required information about each printing Job            //
//***********************************************************************************************

public class Job {

    private String type;
    private int id;
    private int pages;
    private int startTime;
    private int head;

    public Job() {
        this.type = "";
        this.id = 0;
        this.pages = 0;
        this.startTime = -1;
        this.head = 0;
    }
    public Job(String type, int id, int pages) {
        this.type = type;
        this.id = id;
        this.pages = pages;
        this.startTime = -1;
        this.head = 0;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getJobId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumPages() {
        return this.pages;
    }

    public void setNumPages(int pages) {
        this.pages = pages;
    }

    public int getStart() {
        return this.startTime;
    }

    public void setStart(int startTime) {
        this.startTime = startTime;
    }

    public int getHead() {
        return this.head;
    }

}
