package com.printserver.server;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

    public static HashMap<String, List<String>> readFile() {
        String fileName = "roles.txt";
        HashMap<String, List<String>> permissionsMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = br.readLine()) != null) {
                // read the role name from the first line
                String role = line.trim();

                // read the task list from the second line
                line = br.readLine();
                if (line != null) {
                    // split the tasks by commas
                    String[] parts = line.split(",");

                    // create a new list to store the tasks
                    List<String> permissions = new ArrayList<>();

                    // copy the tasks from the parts array to the list
                    for (String part : parts) {
                        permissions.add(part.trim());
                    }

                    // put the role and tasks in the hashmap
                    permissionsMap.put(role, permissions);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return permissionsMap;
    }


}
