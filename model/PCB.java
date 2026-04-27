package model;
public class PCB {

    int processID;
    int burstTime;
    int priority;
    int memory;

    int waitingTime;
    int turnaroundTime;

    String state;

    public PCB(int processID, int burstTime, int priority, int memory) {
        this.processID = processID;
        this.burstTime = burstTime;
        this.priority = priority;
        this.memory = memory;

        this.waitingTime = 0;
        this.turnaroundTime = 0;
        this.state = "NEW";
    }

    public void printProcess() {
        System.out.println("Process " + processID +
                " | Burst: " + burstTime +
                " | Priority: " + priority +
                " | Memory: " + memory);
    }
}