# CSC227 Multithreaded CPU Scheduler

## Project Description
This project is a Java-based multithreaded CPU scheduling simulator for the CSC227 Operating Systems course.

The simulator models the behavior of a single-CPU system using multiple threads. It supports different CPU scheduling algorithms, manages process admission based on memory availability, calculates performance metrics, and detects starvation in priority scheduling.

---

## Scheduling Algorithms
The simulator supports the following algorithms:

1. Shortest Job First (SJF)
2. Round Robin (RR) with time quantum = 5 ms
3. Non-Preemptive Priority Scheduling with starvation detection and aging

---

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

---

## Input File Format
The input file should be named `job.txt`.

Each process should be written in the following format:


ProcessID:BurstTime:Priority;MemoryRequired


Example:


1:25:4;500
2:13:3;700
3:20:3;100


---

## System Components

The system is divided into the following main components:

- **PCB (Process Control Block):** Stores all process-related information  
- **Job Queue:** Holds all processes read from the input file  
- **Ready Queue:** Holds processes ready for CPU execution  
- **Memory Manager:** Ensures processes are loaded only if sufficient memory is available  
- **Scheduler:** Selects processes based on the chosen scheduling algorithm  

### Threads
- **Thread 1:** Reads input file and creates processes  
- **Thread 2:** Loads processes into ready queue based on memory availability  
- **Main Thread:** Executes scheduling algorithm  

---

## Assumptions
- All processes arrive at time 0  
- Processes are executed in the order they are read if needed  
- Total available memory is **2048 MB**  
- Context switching time is **0 ms**  
- Time unit for simulation is **1 ms**  

---

## Output

For each scheduling algorithm, the program will display:

### Gantt Chart
- Shows execution order of processes over time  

### Process Table
- Process ID  
- Burst Time  
- Start Time  
- Termination Time  
- Waiting Time  
- Turnaround Time  

### Performance Metrics
- Average Waiting Time  
- Average Turnaround Time  

### Priority Scheduling Only
- Identify starved processes  
- Apply aging mechanism  

---

## Project Structure


project/
│
├── input/ # Reading job.txt and creating PCBs
├── memory/ # Memory management and job admission
├── scheduling/ # Scheduling algorithms (SJF, RR, Priority)
├── output/ # Gantt chart and metrics
├── model/ # PCB class and shared data structures
├── main/ # Main execution
│
└── job.txt


---

## Team Members

| Name | Student ID | Role |
|------|-----------|------|
| Sara |           | Team Leader |
| Member 2 |       | PCB & Input Manager |
| Member 3 |       | Thread & Memory Manager |
| Member 4 |       | Scheduling & Output |

---

## Task Distribution

| Member | Responsibility |
|--------|--------------|
| Sara | Integration, review, coordination |
| Member 2 | Input handling, PCB creation |
| Member 3 | Threading, memory, queues |
| Member 4 | Scheduling logic, output, metrics |

---

## Tools Used
- Java  
- Visual Studio Code  
- Git & GitHub  

---

## How to Run

1. Open the project in Visual Studio Code  
2. Make sure Java is installed  
3. Place `job.txt` in the root directory  
4. Compile the project  
5. Run the main program  
6. Choose a scheduling algorithm  
7. View results in the console  

---

## Course Information
- Course: CSC227 Operating Systems  
- Project: Multithreaded CPU Scheduling Simulator  
- Semester: Second Semester 1447  
