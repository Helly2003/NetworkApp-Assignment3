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

	waitForConnection(serverIP, port);

    }
    
    private static void waitForConnection(String serverIP, int port)
    {
        System.out.println("Attempting to connect to server at " + serverIP + ":" + port);
        while (true) 
        {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(serverIP, port), 5000);
                System.out.println("Connection established.");
                return;
            } catch (IOException e) {
                System.out.println("Waiting for server connection.....");
                try {
                    Thread.sleep(5000); 
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    System.err.println("Client interrupted while waiting for connection.");
                    System.exit(1);
                }
            }
        }
    }
}