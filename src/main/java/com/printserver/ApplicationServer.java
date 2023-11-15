package com.printserver;

import com.printserver.interfaces.IAuthService;
import com.printserver.server.AuthService;
import com.printserver.server.PrintServantImplementation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

@SpringBootApplication
public class ApplicationServer {
    private static final Logger LOGGER = LogManager.getLogger(ApplicationServer.class);

    public static void main(String[] args) {
        SpringApplication.run(ApplicationServer.class, args);
        try {
            IAuthService authService = new AuthService();
            Registry registry = LocateRegistry.createRegistry(5099);
            registry.rebind("PrintService", new PrintServantImplementation(authService));
            System.out.println(java.time.LocalDateTime.now() + "	Print Server Is Running!");
            LOGGER.info(java.time.LocalDateTime.now() + "	Print Server Is Running!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
