package com.printserver.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

// Common Interface that is accessible for both server and client

public interface PrintService extends Remote {
    void print(String fileName, String printer, String token) throws RemoteException;

    String printQueue(String printer, String token) throws RemoteException;

    void topQueue(String printer, int job, String token) throws RemoteException;

    void start(String token) throws RemoteException;

    void stop(String token) throws RemoteException;

    void restart(String token) throws RemoteException;

    String status(String printer, String token) throws RemoteException;

    String readConfig(String parameter, String token) throws RemoteException;

    void setConfig(String parameter, String value, String token) throws RemoteException;

    String auth(String userName, String password) throws RemoteException;
}

