/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.tudd.productivity.integration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import se.kth.id1212.tudd.productivity.model.Processes;

/**
 *
 * @author udde
 */
public class ProcessReader {

    private final String CONSOLE_STRING = "console";

    public ArrayList<String> readProcesses() {
        ArrayList<String> categories = new ArrayList<>();
        boolean gamingSet = false;
        boolean productiveSet = false;
        boolean browsingSet = false;
        
        try {
            String line;
            Process p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");
            BufferedReader input
                    = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String[] parsedLine;
            while ((line = input.readLine()) != null) {
                if (line.toLowerCase().contains(CONSOLE_STRING)) {
                    for(String processName : Processes.gaming){
                        if(line.toLowerCase().contains(processName)&&!gamingSet){
                            categories.add("gaming");
                            gamingSet = true;
                        }
                    }
                    for(String processName : Processes.productive){
                        if(line.toLowerCase().contains(processName)&&!productiveSet){
                            categories.add("productive");
                            productiveSet = true;
                        }
                    }
                    for(String processName : Processes.browsing){
                        if(line.toLowerCase().contains(processName)&&!browsingSet){
                            categories.add("browsing");
                            browsingSet = true;
                        }
                    }
                    System.out.println(line);
                }
            }
            input.close();
        } catch (Exception err) {
            err.printStackTrace();
        }
        return categories;
    }
}
