package threads;

import model.PCB;
import java.util.Queue;

public class LoaderThread extends Thread {
private Queue<PCB> jobQueue;
private Queue<PCB> readyQueue;
private int availableMemory;
private Thread schedulerThread; // ← أضفنا هذا
public LoaderThread(Queue<PCB> jobQueue,
Queue<PCB> readyQueue,
int totalMemory,
Thread schedulerThread) { // ← أضفنا هذا
this.jobQueue = jobQueue;
this.readyQueue = readyQueue;
this.availableMemory = totalMemory;
this.schedulerThread = schedulerThread; // ← أضفنا هذا
}

    @Override
    public void run() {
while (!jobQueue.isEmpty()) {
PCB process = jobQueue.peek();
if (process.getMemory() <= availableMemory) {
// ... ينقل الـ process ...
} else {
jobQueue.poll(); // ← يتجاوز هذي ويكمل
System.out.println("Not enough memory — skipping P"
+ process.getProcessID());
}
}
System.out.println("All processes loaded. Waiting for scheduling to finish...");
try {
schedulerThread.join(); // ← ينتظر الـ scheduling ينتهي
} catch (InterruptedException e) {
e.printStackTrace();
}
System.out.println("Loader Thread finished — all execution complete."); // ← ينتهي هنا
}
}
