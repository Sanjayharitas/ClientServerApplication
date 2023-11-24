package com.printserver.server;

import com.printserver.interfaces.IAuthService;
import com.printserver.interfaces.IPrintService;
import com.printserver.interfaces.IUserService;
import com.printserver.models.Role;
import com.printserver.models.User;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.List;

import java.security.Key;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrintServantImplementation extends UnicastRemoteObject implements IPrintService {
    private static final Logger LOGGER = LogManager.getLogger(PrintServantImplementation.class);
    private Map<String, List<String>> printQueues;
    private Map<String, String> configParameters;
    private boolean isRunning = true;
    public static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private final IAuthService authService;
    private final IUserService userService;

    public PrintServantImplementation(IAuthService authService, IUserService userService) throws RemoteException {
//        super();
        this.authService = authService;
        this.userService = userService;
        this.printQueues = new HashMap<>();
        this.configParameters = new HashMap<>();
    }

    @Override
    public String print(String fileName, String printer, String token) throws RemoteException {
        if (authService.validateToken(token)) {
            if (checkAuthority("print", token)) {
                List<String> printerQueue = printQueues.get(printer);
                if (printerQueue == null) {
                    printerQueue = new ArrayList<>();
                    printQueues.put(printer, printerQueue);
                }
                printerQueue.add(fileName);
                System.out.println(java.time.LocalDateTime.now() + "    " + authService.getUsernameFromJWT(token) + " - Print job received: File [" + fileName + "] on Printer [" + printer + "]");
                LOGGER.info(java.time.LocalDateTime.now() + "    " + authService.getUsernameFromJWT(token) + " - Print job received: File [" + fileName + "] on Printer [" + printer + "]");
                return "Success.";
            }
            return "No Access";
        } else {
            LOGGER.error("Token validation failed!!");
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public String printQueue(String printer, String token) throws RemoteException {
        if (authService.validateToken(token)) {
            if (checkAuthority("print_queue", token)) {
                List<String> printerQueue = printQueues.get(printer);
                if (printerQueue != null) {
                    System.out.println(java.time.LocalDateTime.now() + "    " + authService.getUsernameFromJWT(token) + " - Printing jobs for printer [" + printer + "] are:");
                    StringBuilder queueInfo = new StringBuilder();

                    for (int i = 0; i < printerQueue.size(); i++) {
                        queueInfo.append((i + 1)).append(". ").append(printerQueue.get(i)).append("\n");
                    }
                    return queueInfo.toString();
                } else {
                    return authService.getUsernameFromJWT(token) + " - Printer not found in the queue.";
                }
            }
            return "NOT AUTHORIZED!";
        } else {
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public String topQueue(String printer, int job, String token) throws RemoteException {
        if (authService.validateToken(token)) {
            if (checkAuthority("top_queue", token)) {
                List<String> printerQueue = printQueues.get(printer);

                if (printerQueue != null && job >= 1 && job <= printerQueue.size()) {
                    String jobToMove = printerQueue.remove(job - 1);
                    printerQueue.add(0, jobToMove);
                    return "Success";
                } else {
                    LOGGER.error(java.time.LocalDateTime.now() + "    " + authService.getUsernameFromJWT(token) + " - Error: Invalid job or printer not found.");
                    System.out.println(java.time.LocalDateTime.now() + "    " + authService.getUsernameFromJWT(token) + " - Error: Invalid job or printer not found.");
                    return "Error!. Invalid job or printer not found.";
                }
            }
            return "You are not Authorized!";
        } else {
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public String start(String token) throws RemoteException {
        if (authService.validateToken(token)) {
            if (checkAuthority("start", token)) {
                if (authService.getUsernameFromJWT(token).equals("admin")) {
                    if (isRunning) {
                        LOGGER.info(java.time.LocalDateTime.now() + "    " + authService.getUsernameFromJWT(token) + " - Print server is already running!");
                        System.out.println(java.time.LocalDateTime.now() + "    " + authService.getUsernameFromJWT(token) + " - Print server is already running!");
                    } else {
                        isRunning = true;
                        LOGGER.info(java.time.LocalDateTime.now() + "    " + authService.getUsernameFromJWT(token) + " - Print server is now running.");
                        System.out.println(java.time.LocalDateTime.now() + "    " + authService.getUsernameFromJWT(token) + " - Print server is now running.");
                    }
                    return "Success";
                } else {
                    LOGGER.warn("Unauthorized access");
                    return "Access Denied!";
                }
            }
            return "You are not Authorized!";
        } else {
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public String stop(String token) throws RemoteException {
        if (authService.validateToken(token)) {
            if (checkAuthority("stop", token)) {
                if (!isRunning) {
                    LOGGER.info(java.time.LocalDateTime.now() + "    " + authService.getUsernameFromJWT(token) + " - Print server is already stopped!");
                    System.out.println(java.time.LocalDateTime.now() + "    " + authService.getUsernameFromJWT(token) + " - Print server is already stopped!");
                } else {
                    isRunning = false;
                    LOGGER.info(java.time.LocalDateTime.now() + "    " + authService.getUsernameFromJWT(token) + " - Print server is now stopped.");
                    System.out.println(java.time.LocalDateTime.now() + "    " + authService.getUsernameFromJWT(token) + " - Print server is now stopped.");
                }
                return "Success";
            }
            return "You are not Authorized!";
        } else {
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public String restart(String token) throws RemoteException {
        if (authService.validateToken(token)) {
            if (checkAuthority("restart", token)) {
                if (isRunning) {
                    stop(token);
                    printQueues.clear();
                    start(token);
                } else {
                    printQueues.clear();
                    start(token);
                }
                return "Success";
            }
            return "You are not Authorized!";
        } else {
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public String status(String printer, String token) throws RemoteException {
        if (authService.validateToken(token)) {
            if (checkAuthority("status", token)) {
                List<String> printerQueue = printQueues.get(printer);

                if (printerQueue != null) {
                    int jobCount = printerQueue.size();
                    return authService.getUsernameFromJWT(token) + " - " + jobCount + " Job(s) in the queue for Printer [" + printer + "]";
                } else {
                    return authService.getUsernameFromJWT(token) + " - Error: Printer [" + printer + "] not found.";
                }
            }
            return "You are not Authorized!";
        } else {
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public String readConfig(String parameter, String token) throws RemoteException {
        if (authService.validateToken(token)) {
            if (checkAuthority("read_config", token)) {
                String value = configParameters.get(parameter);
                return Objects.requireNonNullElseGet(value, () -> authService.getUsernameFromJWT(token) + " - Error: Parameter " + parameter + " not found.");
            }
            return "You are not Authorized!";
        } else {
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public String setConfig(String parameter, String value, String token) throws RemoteException {
        if (authService.validateToken(token)) {
            if (checkAuthority("set_config", token)) {
                configParameters.put(parameter, value);
                return "Success";
            }
            return "You are not Authorized!";
        } else {
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public String auth(String userName, String password) {
        return authService.auth(userName, password);
    }

    @Override
    public String getRole(String token) throws RemoteException {
        if (authService.validateToken(token)) {
            return authService.getRoleFromToken(token);
        } else {
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public User getUser(String username, String token) throws RemoteException {
        if (authService.validateToken(token)) {
            if (checkAuthority("get_user", token)) {
                User user = userService.getUser(username);
                return user;
            }
            return null;
        } else {
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public List<User> getUsers(String token) throws RemoteException {
        if (authService.validateToken(token)) {
            if (checkAuthority("get_users_list", token)) {
                return userService.getUsers();
            }
            return null;
        } else {
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public List<Role> getRolesList(String token) throws RemoteException {
        if (authService.validateToken(token)) {
            if (checkAuthority("get_roles_list", token)) {
                return userService.getRolesList();
            }
            return null;
        } else {
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public String registerUser(User user, String token) throws RemoteException {
        List<Role> roles = userService.getRolesList();
        if (authService.validateToken(token)) {
            if (checkAuthority("register_user", token)) {
                String regexPattern = "^[a-z0-9]{10,}$";
                String DOB = "^(0[1-9]|[12][0-9]|3[01])[- .](0[1-9]|1[012])[- .](19|20)\\d\\d$";
                Pattern pattern = Pattern.compile(regexPattern);
                Pattern pattern2 = Pattern.compile(DOB);
                Matcher matcher = pattern.matcher(user.password);
                Matcher matcher2 = pattern2.matcher(user.dob);
                if (!matcher.matches()) {
                    return "Password rejected. Password shpuld be minimum of 10 characters and should include letters and numbers";
                }
                if (!matcher2.matches()) {
                    return "Invalid DOB";
                }
                List<String> stringList = new ArrayList<>();
                for (Role obj : roles) {
                    stringList.add(obj.toString());
                }
                if (!stringList.contains(user.role)) {
                    return "Invalid user role!";
                }
                user.password = PasswordProtection.hashPassword(user.password);
                userService.register(user);
                return "Success";
            }
            return "NOT AUTHORIZED!";
        } else {
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public String deleteUser(String username, String token) throws RemoteException {
        if (authService.validateToken(token)) {
            if (checkAuthority("delete_user", token)) {
                userService.delete(username);
                return "Success";
            }
            return "NOT AUTHORIZED!";
        } else {
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public String updateRole(int userId, String newRole, String token) throws RemoteException {
        if (authService.validateToken(token)) {
            if (checkAuthority("role_management", token)) {
                userService.updateRole(userId, newRole);
                return "Success";
            }
            return "NOT AUTHORIZED!";
        } else {
            throw new RemoteException("Token validation failed.");
        }
    }

    @Override
    public boolean checkAuthority(String task, String token) throws RemoteException {
        String user = authService.getUsernameFromJWT(token);
        String role = authService.getRole(authService.getUsernameFromJWT(token));
        List<String> tasks = DBConnection.getAccessListForRole(role);
        if (tasks.contains(task)) {
            LOGGER.info("User " + user + " accessing task- " + task);
            return true;
        } else {
            LOGGER.warn("User " + user + " tried to access task- " + task);
        }
        return false;
    }
}

