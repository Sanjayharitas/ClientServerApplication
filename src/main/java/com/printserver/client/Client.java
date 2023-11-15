package com.printserver.client;

import com.google.common.hash.Hashing;
import com.printserver.interfaces.IPrintService;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

// This is the client Class that establish the connection with server and only the common interface is accessible from this class.

public class Client {

    public static void main(String[] args) throws MalformedURLException, NotBoundException, RemoteException {
        IPrintService service = (IPrintService) Naming.lookup("rmi://localhost:5099/PrintService");
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
            token = service.auth(userName, sha256hex);

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
            serverTasks(isRunning, scanner, service, token);
        }
    }

    private static void serverTasks(boolean isRunning, Scanner scanner, IPrintService service, String token) throws RemoteException {
        while (isRunning) {
            printTaskList();
            String taskInput = scanner.nextLine();

            switch (taskInput) {
                case "1":
                    System.out.print("Enter filename: ");
                    String fileName = scanner.nextLine();
                    System.out.print("Enter printer: ");
                    String printer1 = scanner.nextLine();
                    System.out.println(service.print(fileName, printer1, token));
                    break;
                case "2":
                    System.out.print("Enter printer name: ");
                    String printer2 = scanner.nextLine();
                    System.out.println("Printing jobs for printer [" + printer2 + "] are:");
                    System.out.println(service.printQueue(printer2, token));
                    break;
                case "3":
                    System.out.print("Enter printer name: ");
                    String printer3 = scanner.nextLine();
                    System.out.print("Enter job number: ");
                    int jobId = Integer.parseInt(scanner.nextLine());
                    System.out.println(service.topQueue(printer3, jobId, token));
                    break;
                case "4":
                    System.out.println(service.start(token));
                    break;
                case "5":
                    System.out.println(service.stop(token));
                    break;
                case "6":
                    System.out.println(service.restart(token));
                    break;
                case "7":
                    System.out.print("Enter printer name: ");
                    String printer4 = scanner.nextLine();
                    System.out.println(service.status(printer4, token));
                    break;
                case "8":
                    System.out.print("Enter parameter name: ");
                    String parameter1 = scanner.nextLine();
                    System.out.println(service.readConfig(parameter1, token));
                    break;
                case "9":
                    System.out.print("Enter parameter name: ");
                    String parameter2 = scanner.nextLine();
                    System.out.print("Enter value: ");
                    String value = scanner.nextLine();

                    System.out.println(service.setConfig(parameter2, value, token));
                    break;
                case "10":
                    isRunning = false;
                    break;
                case "x":
                    isRunning = false;
                    break;
                default:
                    System.out.println("Invalid option, please choose a correct option from the list: ");
            }
        }
    }

    private static String getSha256hex(String password) {
        return Hashing.sha256()
                .hashString(password, StandardCharsets.UTF_8)
                .toString();
    }

    private static void printTaskList() {
        System.out.println("\n1. add a print job");
        System.out.println("2. print the job list");
        System.out.println("3. move a job to the top of the queue");
        System.out.println("4. start the print server");
        System.out.println("5. stop the print server");
        System.out.println("6. restart the print server");
        System.out.println("7. print the status of the printer");
        System.out.println("8. read the parameters");
        System.out.println("9. set the parameters");
        System.out.println("10. exit -> (x)");
        System.out.print("\nWhat can I do for you: ");
    }

}