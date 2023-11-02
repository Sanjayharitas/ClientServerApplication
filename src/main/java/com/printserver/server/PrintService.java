package com.printserver.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.security.Key;

public interface PrintService extends Remote {
    void print(String fileName, String printer, String token, Key key) throws RemoteException;
    String printQueue(String printer, String token, Key key) throws RemoteException;
    void topQueue(String printer, int job, String token, Key key) throws RemoteException;
    void start(String token, Key key) throws RemoteException;
    void stop(String token, Key key) throws RemoteException;
    void restart(String token, Key key) throws RemoteException;
    String status(String printer, String token, Key key) throws RemoteException;
    String readConfig(String parameter, String token, Key key) throws RemoteException;
    void setConfig(String parameter, String value, String token, Key key) throws RemoteException;
    String auth(String userName, String password) throws RemoteException;
}

