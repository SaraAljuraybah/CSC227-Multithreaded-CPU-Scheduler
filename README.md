# CSC227 Multithreaded CPU Scheduler

## Project Description
This project is a Java-based multithreaded CPU scheduling simulator for the CSC227 Operating Systems course.

The simulator models the behavior of a single-CPU system using multiple threads. It supports different CPU scheduling algorithms, manages process admission based on memory availability, calculates performance metrics, and detects starvation in priority scheduling.

## Scheduling Algorithms
The simulator supports the following algorithms:

1. Shortest Job First (SJF)
2. Round Robin (RR) with time quantum = 5 ms
3. Non-Preemptive Priority Scheduling with starvation detection and aging

## Main Features
- Reads process information from `job.txt`
- Creates Process Control Blocks (PCB)
- Uses job queue and ready queue
- Simulates limited main memory of 2048 MB
- Uses multithreading for process loading and admission
- Displays Gantt chart
- Displays process scheduling table
- Calculates average waiting time
- Calculates average turnaround time
- Detects starved processes in priority scheduling
- Applies aging to reduce starvation

## Input File Format
The input file should be named `job.txt`.

Each process should be written in the following format:

```text
ProcessID:BurstTime:Priority;MemoryRequired
