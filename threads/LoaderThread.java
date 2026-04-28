package threads;

import model.PCB;
import java.util.Queue;

public class LoaderThread extends Thread {

    private Queue<PCB> jobQueue;
    private Queue<PCB> readyQueue;

    private int availableMemory;

    public LoaderThread(Queue<PCB> jobQueue, Queue<PCB> readyQueue, int totalMemory) {
        this.jobQueue = jobQueue;
        this.readyQueue = readyQueue;
        this.availableMemory = totalMemory;
    }

    @Override
    public void run() {

        while (!jobQueue.isEmpty()) {

            PCB process = jobQueue.peek();

            if (process.getMemory() <= availableMemory) {

                synchronized (jobQueue) {
                    jobQueue.remove();
                }

                synchronized (readyQueue) {
                    readyQueue.add(process);
                }

                availableMemory -= process.getMemory();
                process.setState("READY");

                System.out.println("Moved Process " + process.getProcessID() + " to Ready Queue");

            } else {
                System.out.println("Not enough memory for Process " + process.getProcessID());
                break; // نوقف إذا ما فيه مساحة
            }
        }

        System.out.println("Loader Thread finished.");
    }
}