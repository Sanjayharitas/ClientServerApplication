package com.printserver;

import com.printserver.server.PrintServantImplementation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

@SpringBootApplication
public class ApplicationServer {

	public static void main(String[] args) {
		SpringApplication.run(ApplicationServer.class, args);
		try {
			Registry registry = LocateRegistry.createRegistry(5099);
			registry.rebind("PrintService", new PrintServantImplementation());
			System.out.println("Print Server Is Running!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
