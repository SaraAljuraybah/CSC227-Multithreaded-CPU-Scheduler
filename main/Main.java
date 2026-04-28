package main;

import model.PCB;
import java.util.*;
import threads.JobReaderThread;
import threads.LoaderThread;
import scheduling.Scheduler;

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
     Scanner scanner = new Scanner(System.in);
System.out.println("\nChoose Scheduling Algorithm:");
System.out.println("  1. Shortest Job First (SJF)");
System.out.println("  2. Round Robin (RR, q=5ms)");
System.out.println("  3. Priority Scheduling (Non-Preemptive)");
System.out.print("Enter choice: ");
int choice = scanner.nextInt();

Scheduler scheduler = new Scheduler(readyQueue);
scheduler.run(choice);   
    }
}