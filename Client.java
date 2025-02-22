import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    public static void main(String[] args) {
    
        if (args.length < 3) {
            System.err.println("Usage: java LoggingClient <serverIP> <port> <manual|auto|test|abuse>");
            System.exit(1);
        }
    
        String serverIP = args[0];
        int port;
        String mode = args[2].toLowerCase(); 
        String appName = "JavaClient";
    
       
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid port provided. Exiting.");
            return;
        }
    }
    
}