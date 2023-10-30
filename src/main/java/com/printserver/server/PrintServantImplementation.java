package com.printserver.server;

import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class PrintServantImplementation extends UnicastRemoteObject implements PrintService {
    private Map<String, List<String>> printQueues;
    private Map<String, String> configParameters;
    private boolean isRunning = true;

    public PrintServantImplementation() throws RemoteException {
//        super();
        this.printQueues = new HashMap<>();
        this.configParameters = new HashMap<>();
    }

    @Override
    public void print(String fileName, String printer) throws RemoteException {
        List<String> printerQueue = printQueues.get(printer);
        if (printerQueue == null) {
            printerQueue = new ArrayList<>();
            printQueues.put(printer, printerQueue);
        }
        printerQueue.add(fileName);

        System.out.println("Print job received: File [" + fileName + "] on Printer [" + printer + "]");
    }

    @Override
    public String printQueue(String printer) throws RemoteException {
        List<String> printerQueue = printQueues.get(printer);
        if (printerQueue != null) {
            System.out.println("Printing jobs for printer [" + printer + "] are:");
            StringBuilder queueInfo = new StringBuilder();
            for (int i = 0; i < printerQueue.size(); i++) {
                queueInfo.append((i + 1)).append(". ").append(printerQueue.get(i)).append("\n");
            }
            return queueInfo.toString();
        } else {
            return "Printer not found in the queue.";
        }
    }

    @Override
    public void topQueue(String printer, int job) throws RemoteException {
        List<String> printerQueue = printQueues.get(printer);

        if (printerQueue != null && job >= 1 && job <= printerQueue.size()) {
            String jobToMove = printerQueue.remove(job - 1);
            printerQueue.add(0, jobToMove);
        } else {
            System.out.println("Error: Invalid job or printer not found.");
        }
    }

    @Override
    public void start() throws RemoteException {
        if (isRunning) {
            System.out.println("Print server is already running!");
        } else {
            isRunning = true;
            System.out.println("Print server is now running.");
        }
    }

    @Override
    public void stop() throws RemoteException {
        if (!isRunning) {
            System.out.println("Print server is already stopped!");
        } else {
            isRunning = false;
            System.out.println("Print server is now stopped.");
        }
    }

    @Override
    public void restart() throws RemoteException {
        if (isRunning) {
            stop();
            printQueues.clear();
            start();
        } else {
            printQueues.clear();
            start();
        }
    }

    @Override
    public String status(String printer) throws RemoteException {
        List<String> printerQueue = printQueues.get(printer);

        if (printerQueue != null) {
            int jobCount = printerQueue.size();
            return jobCount + "Job(s) in the queue for Printer [" + printer + "]";
        } else {
            return "Error: Printer [" + printer + "] not found.";
        }
    }

    @Override
    public String readConfig(String parameter) throws RemoteException {
        String value = configParameters.get(parameter);
        return Objects.requireNonNullElseGet(value, () -> "Error: Parameter " + parameter + " not found.");
    }

    @Override
    public void setConfig(String parameter, String value) throws RemoteException {
        configParameters.put(parameter, value);
    }

    @Override
    public boolean auth(String userName, String password) {
        String hashPassword = PasswordProtection.PBKDF2(userName, password);
        boolean res = DBConnection.authUser(userName, hashPassword);
        return res;
    }

}

