package com.printserver.client;

import com.google.common.hash.Hashing;
import com.printserver.interfaces.IPrintService;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Objects;
import java.util.Scanner;

// This is the client Class that establish the connection with server and only the common interface is accessible from this class.

public class Client {

    public static void main(String[] args) throws MalformedURLException, NotBoundException, RemoteException {
        IPrintService printService = (IPrintService) Naming.lookup("rmi://localhost:5099/PrintService");
        int counter = 0;
        String token = null;
        boolean isRunning = false;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Good to see you! \t I am your agent! \n Please login with your username and password.");

//        Max number of attempts
        while (counter < 3) {

            System.out.print("Username: ");
            String userName = scanner.nextLine();
//            For testing purpose we are using scanner but in real time the application uses console.readpassword to mask the password.
//            Console console = System.console();
//            char[] passwordChar = console.readPassword("Enter your password: ");
//            String password = new String(passwordChar);
            System.out.print("Password: ");
            String password = scanner.nextLine();
            String sha256hex = getSha256hex(password);
            token = printService.auth(userName, sha256hex);

            if (token != null) {
                isRunning = true;
                break;
            } else {
                System.out.print("Invalid username or password!! Try again.\n");
                counter++;
                if (counter > 3) {
                    System.out.print("Too many invalid attempts!... Bye :)");
                    System.exit(0);
                }
            }
        }

        if(isRunning){
            serverTasks(isRunning, scanner, printService, token);
        }
    }
    private static void serverTasks(boolean isRunning, Scanner scanner, IPrintService service, String token) throws RemoteException {
        while (isRunning) {

            String role = service.getRole(token);
            printTaskList(role);
            
            String taskInput = scanner.nextLine();

            if(Objects.equals(role, "admin")) isRunning = admin_perform_tasks(isRunning, scanner, service, token, taskInput);
            else if(Objects.equals(role, "superuser")) isRunning = superuser_perform_tasks(isRunning, scanner, service, token, taskInput);
            else if(Objects.equals(role, "poweruser")) isRunning = poweruser_perform_tasks(isRunning, scanner, service, token, taskInput);
            else if(Objects.equals(role, "user")) isRunning = ordinaryuser_perform_tasks(isRunning, scanner, service, token, taskInput);
        }
    }

    private static boolean admin_perform_tasks(boolean isRunning, Scanner scanner, IPrintService service, String token, String taskInput) throws RemoteException {
        switch (taskInput) {
            case "1" -> task_print(scanner, service, token);
            case "2" -> task_printQueue(scanner, service, token);
            case "3" -> task_topQueue(scanner, service, token);
            case "4" -> task_start(service, token);
            case "5" -> task_stop(service, token);
            case "6" -> task_restart(service, token);
            case "7" -> task_status(scanner, service, token);
            case "8" -> task_readConfig(scanner, service, token);
            case "9" -> task_setConfig(scanner, service, token);
            case "10" -> task_registerUser(scanner, service, token);
            case "11" -> task_deleteUser(scanner, service, token);
            case "12" -> task_roleManagement(scanner, service, token);
            case "13" -> isRunning = false;
            case "x" -> isRunning = false;
            default -> System.out.println("Invalid option, please choose a correct option from the list: ");
        }
        return isRunning;
    }
    private static boolean superuser_perform_tasks(boolean isRunning, Scanner scanner, IPrintService service, String token, String taskInput) throws RemoteException {
        switch (taskInput) {
            case "1" -> task_start(service, token);
            case "2" -> task_stop(service, token);
            case "3" -> task_restart(service, token);
            case "4" -> task_status(scanner, service, token);
            case "5" -> task_readConfig(scanner, service, token);
            case "6" -> task_setConfig(scanner, service, token);
            case "7" -> isRunning = false;
            case "x" -> isRunning = false;
            default -> System.out.println("Invalid option, please choose a correct option from the list: ");
        }
        return isRunning;
    }
    private static boolean poweruser_perform_tasks(boolean isRunning, Scanner scanner, IPrintService service, String token, String taskInput) throws RemoteException {
        switch (taskInput) {
            case "1" -> task_print(scanner, service, token);
            case "2" -> task_printQueue(scanner, service, token);
            case "3" -> task_topQueue(scanner, service, token);
            case "4" -> task_restart(service, token);
            case "5" -> isRunning = false;
            case "x" -> isRunning = false;
            default -> System.out.println("Invalid option, please choose a correct option from the list: ");
        }
        return isRunning;
    }
    private static boolean ordinaryuser_perform_tasks(boolean isRunning, Scanner scanner, IPrintService service, String token, String taskInput) throws RemoteException {
        switch (taskInput) {
            case "1" -> task_print(scanner, service, token);
            case "2" -> task_printQueue(scanner, service, token);
            case "5" -> isRunning = false;
            case "x" -> isRunning = false;
            default -> System.out.println("Invalid option, please choose a correct option from the list: ");
        }
        return isRunning;
    }

    private static void task_setConfig(Scanner scanner, IPrintService service, String token) throws RemoteException {
        System.out.print("Enter parameter name: ");
        String parameter2 = scanner.nextLine();
        System.out.print("Enter value: ");
        String value = scanner.nextLine();
        System.out.println(service.setConfig(parameter2, value, token));
    }

    private static void task_readConfig(Scanner scanner, IPrintService service, String token) throws RemoteException {
        System.out.print("Enter parameter name: ");
        String parameter1 = scanner.nextLine();
        System.out.println(service.readConfig(parameter1, token));
    }

    private static void task_status(Scanner scanner, IPrintService service, String token) throws RemoteException {
        System.out.print("Enter printer name: ");
        String printer4 = scanner.nextLine();
        System.out.println(service.status(printer4, token));
    }

    private static void task_restart(IPrintService service, String token) throws RemoteException {
        System.out.println(service.restart(token));
    }

    private static void task_stop(IPrintService service, String token) throws RemoteException {
        System.out.println(service.stop(token));
    }

    private static void task_start(IPrintService service, String token) throws RemoteException {
        System.out.println(service.start(token));
    }

    private static void task_topQueue(Scanner scanner, IPrintService service, String token) throws RemoteException {
        System.out.print("Enter printer name: ");
        String printer3 = scanner.nextLine();
        System.out.print("Enter job number: ");
        int jobId = Integer.parseInt(scanner.nextLine());
        System.out.println(service.topQueue(printer3, jobId, token));
    }

    private static void task_printQueue(Scanner scanner, IPrintService service, String token) throws RemoteException {
        System.out.print("Enter printer name: ");
        String printer2 = scanner.nextLine();
        System.out.println("Printing jobs for printer [" + printer2 + "] are:");
        System.out.println(service.printQueue(printer2, token));
    }

    private static void task_print(Scanner scanner, IPrintService service, String token) throws RemoteException {
        System.out.print("Enter filename: ");
        String fileName = scanner.nextLine();
        System.out.print("Enter printer: ");
        String printer1 = scanner.nextLine();
        System.out.println(service.print(fileName, printer1, token));
    }
    private static void task_registerUser(Scanner scanner, IPrintService service, String token) throws RemoteException {
        // write code for user registration
    }
    private static void task_roleManagement(Scanner scanner, IPrintService service, String token) throws RemoteException {
        // write code for user deletion
    }
    private static void task_deleteUser(Scanner scanner, IPrintService service, String token) throws RemoteException {
        // write code for user deletion
    }

    private static String getSha256hex(String password) {
        return Hashing.sha256()
                .hashString(password, StandardCharsets.UTF_8)
                .toString();
    }

    private static void printTaskList(String role) {
        if(Objects.equals(role, "admin")) adminTasksList();
        else if(Objects.equals(role, "superuser")) superUserTasksList();
        else if(Objects.equals(role, "poweruser")) powerUserTasksList();
        else if(Objects.equals(role, "user")) ordinaryUserTasksList();
        else System.out.println("Invalid Role or Token");
    }

    private static void adminTasksList(){
        System.out.println("\n1. add a print job");
        System.out.println("2. print the job list");
        System.out.println("3. move a job to the top of the queue");
        System.out.println("4. start the print server");
        System.out.println("5. stop the print server");
        System.out.println("6. restart the print server");
        System.out.println("7. print the status of the printer");
        System.out.println("8. read the parameters");
        System.out.println("9. set the parameters");
        System.out.println("10. Register New User");
        System.out.println("11. Delete User");
        System.out.println("12. Role Management");
        System.out.println("13. exit -> (x)");
        System.out.print("\nWhat can I do for you: ");
    }
    private static void superUserTasksList(){
        System.out.println("1. start the print server");
        System.out.println("2. stop the print server");
        System.out.println("3. restart the print server");
        System.out.println("4. print the status of the printer");
        System.out.println("5. read the parameters");
        System.out.println("6. set the parameters");
        System.out.println("7. exit -> (x)");
        System.out.print("\nWhat can I do for you: ");
    }
    private static void powerUserTasksList(){
        System.out.println("\n1. add a print job");
        System.out.println("2. print the job list");
        System.out.println("3. move a job to the top of the queue");
        System.out.println("4. restart the print server");
        System.out.println("5. exit -> (x)");
        System.out.print("\nWhat can I do for you: ");
    }
    private static void ordinaryUserTasksList(){
        System.out.println("\n1. add a print job");
        System.out.println("2. print the job list");
        System.out.println("3. exit -> (x)");
        System.out.print("\nWhat can I do for you: ");
    }

}