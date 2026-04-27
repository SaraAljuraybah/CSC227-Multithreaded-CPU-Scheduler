package main;

import input.JobReader;
import model.PCB;
import java.util.*;
public class Main {

    public static void main(String[] args) {

        List<PCB> processes = JobReader.readJobs("job.txt");

        for (PCB p : processes) {
            p.printProcess();
        }
    }
}