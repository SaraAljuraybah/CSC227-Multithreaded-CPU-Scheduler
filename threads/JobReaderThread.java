package threads;

import input.JobReader;
import model.PCB;
import java.util.List;
import java.util.Queue;

public class JobReaderThread extends Thread {

    private String filename;
    private Queue<PCB> jobQueue;

    public JobReaderThread(String filename, Queue<PCB> jobQueue) {
        this.filename = filename;
        this.jobQueue = jobQueue;
    }

    @Override
    public void run() {

        List<PCB> processes = JobReader.readJobs(filename);

        synchronized (jobQueue) {
            for (PCB p : processes) {
                jobQueue.add(p);
                System.out.println("Thread 1: Added Process " + p.getProcessID() + " to Job Queue");
            }
        }

        System.out.println("Thread 1 finished reading jobs.");
    }
}