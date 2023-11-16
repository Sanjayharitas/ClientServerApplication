package com.printserver.server;
import java.io.*;
import java.util.HashMap;

public class Roles {

    public static void createRolesFile(){
        String[] roles = {"admin", "superuser", "poweruser", "user"};
        String[][] tasks = {
                {"print", "print_queue", "top_queue", "start", "stop", "restart", "status", "read_config", "set_config", "register_user", "delete_user", "role_management", "get_user", "get_users_ist", "get_roles_list"},
                {"start", "stop", "restart", "status", "read_config", "set_config"},
                {"print", "print_queue", "top_queue","restart"},
                {"print", "print_queue"}
        };
        String fileName = "roles.txt";

        try {
            File file =new File(fileName);

            if(file.exists()){
                return;
            }

            FileWriter writer = new FileWriter(fileName);

            for (int i = 0; i < roles.length; i++) {
                writer.write(roles[i] + "\n");

                for (int j = 0; j < tasks[i].length; j++) {
                    writer.write(tasks[i][j]);
                    if (j < tasks[i].length - 1) {
                        writer.write(",");
                    }
                }
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, String[]> readFile() {
        String fileName = "roles.txt";
        HashMap<String, String[]> roleMap = new HashMap<>();

        try {
            FileReader reader = new FileReader(fileName);

            BufferedReader buffer = new BufferedReader(reader);

            String line = buffer.readLine();
            while (line != null) {
                if (line.contains(",")) {
                    String[] taskArray = line.split(",");
                    String role = buffer.readLine();
                    roleMap.put(role, taskArray);
                }
                line = buffer.readLine();
            }
            buffer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return roleMap;
    }

}
