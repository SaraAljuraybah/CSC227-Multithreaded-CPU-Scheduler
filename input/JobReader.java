
package input;

import model.PCB;
import java.io.*;
import java.util.*;
public class JobReader {

    public static List<PCB> readJobs(String filename) {

        List<PCB> processes = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));

            String line;

            while ((line = reader.readLine()) != null) {

                // نتأكد السطر مو فاضي
                if (line.trim().isEmpty()) continue;

                // نقسم عند ;
                String[] parts = line.split(";");
                
                String leftPart = parts[0]; // 1:25:4
                int memory = Integer.parseInt(parts[1]); // 500

                // نقسم الجزء الأول عند :
                String[] values = leftPart.split(":");

                int id = Integer.parseInt(values[0]);
                int burst = Integer.parseInt(values[1]);
                int priority = Integer.parseInt(values[2]);

                // نسوي PCB
                PCB process = new PCB(id, burst, priority, memory);

                processes.add(process);
            }

            reader.close();

        } catch (Exception e) {
            System.out.println("Error reading file");
        }

        return processes;
    }
}