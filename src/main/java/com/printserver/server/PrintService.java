package com.printserver.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

public interface PrintService extends Remote {
    void print(String fileName, String printer, String sessionId) throws RemoteException;
    String printQueue(String printer, String sessionId) throws RemoteException;
    void topQueue(String printer, int job, String sessionId) throws RemoteException;
    void start(String sessionId) throws RemoteException;
    void stop(String sessionId) throws RemoteException;
    void restart(String sessionId) throws RemoteException;
    String status(String printer, String sessionId) throws RemoteException;
    String readConfig(String parameter, String sessionId) throws RemoteException;
    void setConfig(String parameter, String value, String sessionId) throws RemoteException;
    String auth(String userName, String password) throws RemoteException;
}

