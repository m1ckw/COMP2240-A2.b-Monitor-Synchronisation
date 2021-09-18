//*********************************************************************************************//
//  Class:  P2 - Main                                                                          //
//  Author: Mick Wiedermann                                                                    //
//  Course: COMP2240 | Assignment 2.2                                                          //
//  Date  : 2021-09-18                                                                         //
//  Description: Job Class to hold the required information about each printing Job            //
//***********************************************************************************************

import java.io.*;
import java.util.Scanner;

public class P2 {
    private static Buffer buffer;
    public static void main(String[] args) {

        File file = new File(args[0]); // replace file with "args[0]" to allow the input to follow the run command.
        try (Scanner scanner = new Scanner(new FileReader(file))) { 
            int numOfJobs = scanner.nextInt();
            buffer = new Buffer(numOfJobs);
            
            while (scanner.hasNext()) {
                String nextLine = scanner.next();
                String type = Character.toString(nextLine.charAt(0));
                String id = Character.toString(nextLine.charAt(1));
                nextLine = scanner.next();
                String pages = Character.toString(nextLine.charAt(0));
                String name = type + id;
                
                // Creating each job as it is read in from the file + create and start the thred.
                Job job = new Job(type, Integer.parseInt(id), Integer.parseInt(pages));
                JobThread jThread = new JobThread(job, buffer);
                Thread thread = new Thread(jThread, name);
                thread.setPriority(setPriority(job));
                thread.start();
            } 
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Three Threads representing each of the three printer heads.
        Printer printHead1 = new Printer(buffer);
        Thread pH1 = new Thread(printHead1, "head 1");
        pH1.start();
        Printer printHead2 = new Printer(buffer);
        Thread pH2 = new Thread(printHead2, "head 2");
        pH2.start();
        Printer printHead3 = new Printer(buffer);
        Thread pH3 = new Thread(printHead3, "head 3");
        pH3.start();

    }

    // Little helper to calculate the therad priority number. 
    public static int setPriority(Job job) {
        int p = 0;
        if ((10 - job.getJobId()) < 0) {
            p = 0;
        }
        p = 10 - job.getJobId();
        return p;
    }
}
