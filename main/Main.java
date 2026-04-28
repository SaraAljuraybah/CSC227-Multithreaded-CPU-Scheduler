package main;

import model.PCB;
import java.util.*;
import threads.JobReaderThread;
import threads.LoaderThread;

public class Main {

    public static void main(String[] args) {

        Queue<PCB> jobQueue = new LinkedList<>();
        Queue<PCB> readyQueue = new LinkedList<>();

        JobReaderThread readerThread = new JobReaderThread("job.txt", jobQueue);
        readerThread.start();

        try {
            readerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n=== Job Queue After Thread 1 ===");
        for (PCB p : jobQueue) {
            p.printProcess();
        }

        int totalMemory = 2048;

        LoaderThread loader = new LoaderThread(jobQueue, readyQueue, totalMemory);
        loader.start();

        try {
            loader.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n=== Ready Queue After Thread 2 ===");
        for (PCB p : readyQueue) {
            p.printProcess();
        }

        System.out.println("\n=== Job Queue After Thread 2 ===");
        if (jobQueue.isEmpty()) {
            System.out.println("Job Queue is empty.");
        } else {
            for (PCB p : jobQueue) {
                p.printProcess();
            }
        }
    }
}