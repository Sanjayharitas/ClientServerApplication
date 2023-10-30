package com.printserver.server;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class PrintServantImplementation extends UnicastRemoteObject implements PrintService {
    private Map<String, List<String>> printQueues;
    private Map<String, String> configParameters;
    private boolean isRunning = true;
    private String sessionIdGenerated;

    public PrintServantImplementation() throws RemoteException {
//        super();
        this.printQueues = new HashMap<>();
        this.configParameters = new HashMap<>();
    }

    @Override
    public void print(String fileName, String printer, String sessionId) throws RemoteException {
        if (sessionId.equals(sessionIdGenerated)) {
            List<String> printerQueue = printQueues.get(printer);
            if (printerQueue == null) {
                printerQueue = new ArrayList<>();
                printQueues.put(printer, printerQueue);
            }
            printerQueue.add(fileName);

            System.out.println("Print job received: File [" + fileName + "] on Printer [" + printer + "]");
        } else {
            System.exit(0);
        }
    }

    @Override
    public String printQueue(String printer, String sessionId) throws RemoteException {
        if (sessionId.equals(sessionIdGenerated)) {
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
        } else {
            System.exit(0);
        }
        return null;
    }

    @Override
    public void topQueue(String printer, int job, String sessionId) throws RemoteException {
        if (sessionId.equals(sessionIdGenerated)) {
            List<String> printerQueue = printQueues.get(printer);

            if (printerQueue != null && job >= 1 && job <= printerQueue.size()) {
                String jobToMove = printerQueue.remove(job - 1);
                printerQueue.add(0, jobToMove);
            } else {
                System.out.println("Error: Invalid job or printer not found.");
            }
        } else {
            System.exit(0);
        }
    }

    @Override
    public void start(String sessionId) throws RemoteException {
        if (sessionId.equals(sessionIdGenerated)) {
            if (isRunning) {
                System.out.println("Print server is already running!");
            } else {
                isRunning = true;
                System.out.println("Print server is now running.");
            }
        } else {
            System.exit(0);
        }
    }

    @Override
    public void stop(String sessionId) throws RemoteException {

        if (sessionId.equals(sessionIdGenerated)) {
            if (!isRunning) {
                System.out.println("Print server is already stopped!");
            } else {
                isRunning = false;
                System.out.println("Print server is now stopped.");
            }
        } else {
            System.exit(0);
        }
    }

    @Override
    public void restart(String sessionId) throws RemoteException {

        if (sessionId.equals(sessionIdGenerated)) {
            if (isRunning) {
                stop(sessionId);
                printQueues.clear();
                start(sessionId);
            } else {
                printQueues.clear();
                start(sessionId);
            }
        } else {
            System.exit(0);
        }
    }

    @Override
    public String status(String printer, String sessionId) throws RemoteException {
        if (sessionId.equals(sessionIdGenerated)) {
            List<String> printerQueue = printQueues.get(printer);

            if (printerQueue != null) {
                int jobCount = printerQueue.size();
                return jobCount + "Job(s) in the queue for Printer [" + printer + "]";
            } else {
                return "Error: Printer [" + printer + "] not found.";
            }
        } else {
            System.exit(0);
        }
        return null;
    }

    @Override
    public String readConfig(String parameter, String sessionId) throws RemoteException {
        if (sessionId.equals(sessionIdGenerated)) {
            String value = configParameters.get(parameter);
            return Objects.requireNonNullElseGet(value, () -> "Error: Parameter " + parameter + " not found.");
        } else {
            System.exit(0);
        }
        return null;
    }

    @Override
    public void setConfig(String parameter, String value, String sessionId) throws RemoteException {
        if (sessionId.equals(sessionIdGenerated)) {
            configParameters.put(parameter, value);
        } else {
            System.exit(0);
        }
    }

    @Override
    public String auth(String userName, String password) {
        String hashPassword = PasswordProtection.PBKDF2(userName, password);
        boolean res = DBConnection.authUser(userName, hashPassword);
        if (res) {
            byte[] array = new byte[7];
            new Random().nextBytes(array);
            sessionIdGenerated = new String(array, Charset.forName("UTF-8"));
            return sessionIdGenerated;
        }
        return null;
    }

}

