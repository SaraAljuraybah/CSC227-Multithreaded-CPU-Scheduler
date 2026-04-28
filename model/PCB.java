package model;

public class PCB {

    private int processID;
    private int burstTime;
    private int priority;
    private int memory;

    private int waitingTime;
    private int turnaroundTime;

    private String state;

    public PCB(int processID, int burstTime, int priority, int memory) {
        this.processID = processID;
        this.burstTime = burstTime;
        this.priority = priority;
        this.memory = memory;

        this.waitingTime = 0;
        this.turnaroundTime = 0;
        this.state = "NEW";
    }

    public int getProcessID() {
        return processID;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getPriority() {
        return priority;
    }

    public int getMemory() {
        return memory;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void printProcess() {
        System.out.println("Process " + processID +
                " | Burst: " + burstTime +
                " | Priority: " + priority +
                " | Memory: " + memory +
                " | State: " + state);
    }
}