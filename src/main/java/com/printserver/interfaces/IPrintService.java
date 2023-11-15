package com.printserver.interfaces;

import com.printserver.models.Role;
import com.printserver.models.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

// Common Interface that is accessible for both server and client

public interface IPrintService extends Remote {
    String print(String fileName, String printer, String token) throws RemoteException;
    String printQueue(String printer, String token) throws RemoteException;
    String topQueue(String printer, int job, String token) throws RemoteException;
    String start(String token) throws RemoteException;
    String stop(String token) throws RemoteException;
    String restart(String token) throws RemoteException;
    String status(String printer, String token) throws RemoteException;
    String readConfig(String parameter, String token) throws RemoteException;
    String setConfig(String parameter, String value, String token) throws RemoteException;
    String auth(String userName, String password) throws RemoteException;
    String getRole(String token)throws RemoteException;
    User getUser(String username, String token)throws RemoteException;
    List<User> getUsers(String token)throws RemoteException;
    List<Role> getRolesList(String token)throws RemoteException;
    String registerUser(User user, String token) throws RemoteException;
}

