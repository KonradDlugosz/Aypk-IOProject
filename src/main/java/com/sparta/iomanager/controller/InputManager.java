package com.sparta.iomanager.controller;

import com.sparta.iomanager.model.Employee;
import com.sparta.iomanager.model.util.UtilManager;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InputManager{
    public class ThreadedRead implements Runnable{

        String inFile;
        int start, end;
        public ThreadedRead(String inFile, int start, int end){
            this.inFile = inFile;
            this.start = start;
            this.end = end;
        }
        @Override
        public void run() {
            try (BufferedReader inp = new BufferedReader(new FileReader(inFile))){
                if (start == 0){
                    inp.readLine();
                }
                for (int i = 0; i < start; i++){
                    inp.readLine();
                }
                String lineOfText;
                for (int i = start; i < end; i++) {
                    lineOfText = inp.readLine();
                    String[] tfields = lineOfText.split(",");
                    for (int j = 0; j < tfields.length; j++) {
                        fields[i][j] = tfields[j];
                    }
                }
            } catch (IOException e){
                System.out.println("Unable to open file!");
                e.printStackTrace();
            }
            threadsFinished++;
        }
    }
    final int ID = 0, toc = 1, firstName = 2, middleInitial = 3, lastName = 4, gender = 5,
            email = 6, dob = 7, doj = 8, salary = 9;
    private Map<Integer, Employee> employeeHashMap = new HashMap<>();
    private Map<Integer, Employee> duplicateValues = new HashMap<>();

    private String[][] fields = new String[0][0];
    private int threadsFinished = 0;

    public String[][] readFile(String inFile){
        try (BufferedReader inp = new BufferedReader(new FileReader(inFile))){
            inp.readLine();
            int lines = 0;
            while (inp.readLine() != null) lines++;
            fields = new String[lines][10];
            int thread1, thread2, thread3;

            thread1 = (int)(lines*0.25);
            thread2 = (int)(lines*0.5);
            thread3 = (int)(lines*0.75);

            ThreadedRead threadedRead1 = new ThreadedRead(inFile, 0, thread1);
            Thread t1 = new Thread(threadedRead1);
            t1.start();

            ThreadedRead threadedRead2 = new ThreadedRead(inFile, thread1, thread2);
            Thread t2 = new Thread(threadedRead2);
            t2.start();

            ThreadedRead threadedRead3 = new ThreadedRead(inFile, thread2, thread3);
            Thread t3 = new Thread(threadedRead3);
            t3.start();

            ThreadedRead threadedRead4 = new ThreadedRead(inFile, thread3, lines);
            Thread t4 = new Thread(threadedRead4);
            t4.start();
        } catch (IOException e){
            e.printStackTrace();
        }
        while(threadsFinished < 4){
            System.out.println(threadsFinished);
        }
        return fields;
    }

    public Map insertion(String[][] fields) {
        ArrayList<Employee> employeesList = new ArrayList<>();
        for (int i = 0; i < fields.length; i++) {
            Employee employee = new Employee();
            int id = 0;
            if (UtilManager.checkInteger(fields[i][ID])) {
                id = Integer.parseInt(fields[i][ID]);
                employee.setEmployeeID(id);
            }
            if (UtilManager.checkInteger(fields[i][salary])) employee.setSalary(Integer.parseInt(fields[i][salary]));

            if (UtilManager.checkCharacter(fields[i][middleInitial]))
                employee.setMiddleInitial(fields[i][middleInitial].charAt(0));
            if (UtilManager.checkCharacter(fields[i][gender])) employee.setMiddleInitial(fields[i][gender].charAt(0));

            employee.setToc(fields[i][toc]);
            employee.setFirstName(fields[i][firstName]);
            employee.setLastName(fields[i][lastName]);
            employee.setEmail(fields[i][email]);

            try {
                employee.setDob(UtilManager.setDateFormat(fields[i][dob]));
                employee.setDoJ(UtilManager.setDateFormat(fields[i][doj]));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            if (employeeHashMap.containsKey(id)) {
                duplicateValues.put(id, employee);
            }
            employeeHashMap.put(id, employee);
        }
        return employeeHashMap;
    }

    public Map<Integer, Employee> getEmployeeHashMap() {
        return employeeHashMap;
    }

    public Map<Integer, Employee> getDuplicateValues() {
        return duplicateValues;
    }
}