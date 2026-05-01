package scheduling; 

import model.PCB;

import java.util.*;

/**
 *  Supported algorithms
 *  ─────────────────────
 *  1. Shortest Job First   (SJF)  – Non-preemptive
 *  2. Round Robin          (RR)   – q = 5 ms
 *  3. Priority Scheduling         – Non-preemptive, with aging
 *
 *  Contract with the rest of the team
 *  ────────────────────────────────────
 *  Input  : Queue<PCB> readyQueue   (filled by LoaderThread – Member 2)
 *  Output : Console only            (Gantt chart + table + metrics)
 *
 *  How to call (from Main.java):
 *      Scheduler scheduler = new Scheduler(readyQueue);
 *      scheduler.run(1);   // 1=SJF  2=RR  3=Priority
 */
public class Scheduler {

    // ── Constants ────────────────────────────────────────────
    private static final int TIME_QUANTUM   = 5;   // ms  (Round Robin)
    private static final int AGING_INTERVAL = 4;   // ms  (Priority aging tick)

    // ── Internal helpers ─────────────────────────────────────
    private static class ProcessInfo {
        final PCB    pcb;
        final int    originalBurst;
        int          remaining;
        int          arrivalOrder;
        int          startTime  = -1;
        int          endTime    =  0;
        int          waitTime   =  0;
        int          priority;
        int          waitedSince = 0;

        ProcessInfo(PCB pcb, int order) {
            this.pcb           = pcb;
            this.originalBurst = pcb.getBurstTime();
            this.remaining     = pcb.getBurstTime();
            this.arrivalOrder  = order;
            this.priority      = pcb.getPriority();
        }
    }

    private record GanttSlice(int pid, int start, int end) {}

    // ── State ─────────────────────────────────────────────────
    private final List<ProcessInfo> processes;

    // ─────────────────────────────────────────────────────────
    //  Constructor
    // ─────────────────────────────────────────────────────────
    public Scheduler(Queue<PCB> readyQueue) {
        processes = new ArrayList<>();
        int order = 0;
        for (PCB p : readyQueue) {
            processes.add(new ProcessInfo(p, order++));
        }
    }

    // ─────────────────────────────────────────────────────────
    //  Public entry point
    // ─────────────────────────────────────────────────────────
    public void run(int choice) {
        if (processes.isEmpty()) {
            System.out.println("Ready queue is empty – nothing to schedule.");
            return;
        }

        for (ProcessInfo pi : processes) {
            pi.remaining   = pi.originalBurst;
            pi.startTime   = -1;
            pi.endTime     = 0;
            pi.waitTime    = 0;
            pi.priority    = pi.pcb.getPriority();
            pi.waitedSince = 0;
        }

        switch (choice) {
            case 1 -> runSJF();
            case 2 -> runRR();
            case 3 -> runPriority();
            default -> System.out.println("Invalid choice. Use 1, 2, or 3.");
        }
    }

    // =========================================================
    //  ALGORITHM 1 – Shortest Job First (Non-preemptive)
    // =========================================================
    private void runSJF() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║   Scheduling Algorithm: SJF          ║");
        System.out.println("╚══════════════════════════════════════╝");

        List<GanttSlice>  gantt     = new ArrayList<>();
        List<ProcessInfo> remaining = new ArrayList<>(processes);
        int time = 0;

        while (!remaining.isEmpty()) {
            ProcessInfo chosen = remaining.stream()
                .min(Comparator.comparingInt((ProcessInfo pi) -> pi.originalBurst)
                               .thenComparingInt(pi -> pi.arrivalOrder))
                .orElseThrow();

            chosen.startTime = time;
            chosen.endTime   = time + chosen.originalBurst;
            chosen.waitTime  = time;
            time             = chosen.endTime;

            gantt.add(new GanttSlice(chosen.pcb.getProcessID(), chosen.startTime, chosen.endTime));
            remaining.remove(chosen);
        }

        printResults("SJF", gantt, false, Collections.emptyList());
    }

    // =========================================================
    //  ALGORITHM 2 – Round Robin (q = 5 ms)
    // =========================================================
    private void runRR() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║   Scheduling Algorithm: Round Robin  ║");
        System.out.println("║   Time Quantum = " + TIME_QUANTUM + " ms              ║");
        System.out.println("╚══════════════════════════════════════╝");

        List<GanttSlice>   gantt  = new ArrayList<>();
        Queue<ProcessInfo> queue  = new LinkedList<>(processes);
        int time = 0;

        while (!queue.isEmpty()) {
            ProcessInfo pi = queue.poll();

            if (pi.startTime == -1) pi.startTime = time;

            int slice = Math.min(TIME_QUANTUM, pi.remaining);
            gantt.add(new GanttSlice(pi.pcb.getProcessID(), time, time + slice));

            time           += slice;
            pi.remaining   -= slice;

            if (pi.remaining > 0) {
                queue.add(pi);
            } else {
                pi.endTime  = time;
                pi.waitTime = pi.endTime - pi.originalBurst;
            }
        }

        printResults("Round Robin (q=" + TIME_QUANTUM + ")", gantt, false, Collections.emptyList());
    }

    // =========================================================
    //  ALGORITHM 3 – Non-Preemptive Priority with Aging
    // =========================================================
    private void runPriority() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║  Scheduling Algorithm: Priority      ║");
        System.out.println("║  (Non-Preemptive + Aging)            ║");
        System.out.println("╚══════════════════════════════════════╝");

        List<GanttSlice>  gantt       = new ArrayList<>();
        List<ProcessInfo> pending     = new ArrayList<>(processes);
        List<String>      starvLog    = new ArrayList<>();
        Set<Integer>      starvedPIDs = new LinkedHashSet<>();

        int time           = 0;
        int N              = pending.size();
        int starvThreshold = N * 5;

        for (ProcessInfo pi : pending) pi.waitedSince = 0;

         int lastAgingTime = 0;

        while (!pending.isEmpty()) {
         
           while (lastAgingTime + AGING_INTERVAL <= time) {
                lastAgingTime += AGING_INTERVAL;   
                
               for (ProcessInfo pi : pending) {
                    int waited = lastAgingTime - pi.waitedSince;
                         if (waited >= starvThreshold) {
                         
                         String msg = String.format(
                                "  ⚠  Process P%d detected as STARVED at t=%d ms " +
                                "(waited %d ms, threshold=%d ms)",
                                pi.pcb.getProcessID(), lastAgingTime, waited, starvThreshold);
                            starvLog.add(msg);
                            System.out.println(msg);  
                              }
                    }
                       
                        if (pi.priority > 1) {
                        pi.priority--;
                        String msg = String.format(
                            "  ↑  Process P%d priority improved to %d at t=%d ms (aging)",
                            pi.pcb.getProcessID(), pi.priority, lastAgingTime);
                        starvLog.add(msg);
                        System.out.println(msg); 
                            }
                }
            }

            // Pick highest priority (smallest number); tie → arrival order
            ProcessInfo chosen = pending.stream()
                .min(Comparator.comparingInt((ProcessInfo pi) -> pi.priority)
                               .thenComparingInt(pi -> pi.arrivalOrder))
                .orElseThrow();

            chosen.startTime = time;
            chosen.endTime   = time + chosen.originalBurst;
            chosen.waitTime  = time;
            time             = chosen.endTime;

            gantt.add(new GanttSlice(chosen.pcb.getProcessID(), chosen.startTime, chosen.endTime));
            pending.remove(chosen);

            // Reset starvation clock for remaining processes from this dispatch point
            for (ProcessInfo pi : pending) pi.waitedSince = time;
        }

        printResults("Priority (Non-Preemptive)", gantt, true, starvLog);

        // ── Starvation summary ────────────────────────────────
        System.out.println("\n─── Starvation Summary ──────────────────────────────");
        if (starvedPIDs.isEmpty()) {
            System.out.println("  No processes suffered from starvation.");
        } else {
            System.out.print("  Processes that suffered starvation: ");
            for (int pid : starvedPIDs) System.out.print("P" + pid + " ");
            System.out.println();
            System.out.println("  Aging was applied every " + AGING_INTERVAL +
                               " ms (priority number decremented by 1).");
        }
    }

    // =========================================================
    //  OUTPUT – shared by all algorithms
    // =========================================================
    private void printResults(String algoName,
                              List<GanttSlice> gantt,
                              boolean showAging,
                              List<String> agingLog) {

        // ── 1. Gantt Chart ────────────────────────────────────
        System.out.println("\n─── Gantt Chart ─────────────────────────────────────");
        StringBuilder topBar = new StringBuilder();
        StringBuilder labels  = new StringBuilder();
        StringBuilder botBar  = new StringBuilder();

        for (GanttSlice s : gantt) {
            String label  = " P" + s.pid() + " ";
            int    width  = Math.max(label.length(), String.valueOf(s.end()).length() + 1);
            label         = centerPad(label, width);

            topBar .append("+").append("─".repeat(width));
            labels .append("|").append(label);
            botBar .append("+").append("─".repeat(width));
        }
        topBar.append("+");
        labels.append("|");
        botBar.append("+");

        StringBuilder times = new StringBuilder();
        times.append(gantt.get(0).start());
        for (GanttSlice s : gantt) {
            String t      = String.valueOf(s.end());
            int    width  = Math.max((" P" + s.pid() + " ").length(), t.length() + 1);
            times.append(" ".repeat(width - t.length() + 1)).append(t);
        }

        System.out.println(topBar);
        System.out.println(labels);
        System.out.println(botBar);
        System.out.println(times);

        // ── 2. Aging / starvation log (Priority only) ─────────
        if (showAging && !agingLog.isEmpty()) {
            System.out.println("\n─── Aging & Starvation Log ──────────────────────────");
            agingLog.forEach(System.out::println);
        }

        // ── 3. Build per-process results ──────────────────────
        Map<Integer, Integer> firstStart = new LinkedHashMap<>();
        Map<Integer, Integer> lastEnd    = new LinkedHashMap<>();

        for (GanttSlice s : gantt) {
            firstStart.putIfAbsent(s.pid(), s.start());
            lastEnd.put(s.pid(), s.end());
        }

        // ── 4. Process Table ──────────────────────────────────
        System.out.println("\n─── Process Table ───────────────────────────────────");
        System.out.printf("%-6s %-12s %-12s %-16s %-14s %-16s%n",
            "PID", "Burst(ms)", "Start(ms)", "Termination(ms)", "Waiting(ms)", "Turnaround(ms)");
        System.out.println("─".repeat(78));

        double totalWT = 0, totalTAT = 0;
        int    n       = processes.size();

        for (ProcessInfo pi : processes) {
            int pid   = pi.pcb.getProcessID();
            int start = firstStart.getOrDefault(pid, 0);
            int end   = lastEnd  .getOrDefault(pid, 0);
            int burst = pi.originalBurst;
            int wt    = start;
            int tat   = end;

            totalWT  += wt;
            totalTAT += tat;

            System.out.printf("%-6d %-12d %-12d %-16d %-14d %-16d%n",
                pid, burst, start, end, wt, tat);
        }

        // ── 5. Performance Metrics ────────────────────────────
        System.out.println("─".repeat(78));
        System.out.printf("  Average Waiting Time    : %.2f ms%n", totalWT  / n);
        System.out.printf("  Average Turnaround Time : %.2f ms%n", totalTAT / n);
        System.out.println("─".repeat(78));
    }

    // ─────────────────────────────────────────────────────────
    //  Utility
    // ─────────────────────────────────────────────────────────
    private static String centerPad(String s, int width) {
        int pad   = width - s.length();
        int left  = pad / 2;
        int right = pad - left;
        return " ".repeat(left) + s + " ".repeat(right);
    }
}

/*
 * ═══════════════════════════════════════════════════════════════
 *  Main.java INTEGRATION  (add after the Ready Queue is printed)
 * ───────────────────────────────────────────────────────────────
 *
 *  import scheduling.Scheduler;
 *  import java.util.Scanner;
 *
 *  Scanner scanner = new Scanner(System.in);
 *  System.out.println("\nChoose Scheduling Algorithm:");
 *  System.out.println("  1. Shortest Job First (SJF)");
 *  System.out.println("  2. Round Robin (RR, q=5ms)");
 *  System.out.println("  3. Priority Scheduling (Non-Preemptive)");
 *  System.out.print("Enter choice: ");
 *  int choice = scanner.nextInt();
 *
 *  Scheduler scheduler = new Scheduler(readyQueue);
 *  scheduler.run(choice);
 *
 * ═══════════════════════════════════════════════════════════════
 */
