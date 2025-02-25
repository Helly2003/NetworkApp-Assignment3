/*
    Name        : Jay Patel  (8888384)  
                  Helly Shah (8958841)  
    Project Name: Client.java  
    Date        : 25th February, 2025  
    Description : A logging client that sends log messages to a server in different modes.  
*/

import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    public static void main(String[] args) {
        // Ensure server IP and port are provided along with the mode
        if (args.length < 3) {
            System.err.println("Usage: java LoggingClient <serverIP> <port> <manual|auto|test|abuse>");
            System.exit(1);
        }
    
        String serverIP = args[0];
        int port;
        String mode = args[2].toLowerCase(); 
        String appName = "JavaClient";
    
        // Parse the port number
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid port provided. Exiting.");
            return;
        }

	    waitForConnection(serverIP, port);
	
        // Choose execution mode based on command-line input
	    switch (mode) 
	    {
            case "manual":
                runManual(serverIP, port, appName);
                break;
            case "auto":
                runAutomated(serverIP, port, appName);
                break;
            case "test":
                runAutomatedTesting(serverIP, port, appName);
                break;
            case "abuse":
                runAbuseTest(serverIP, port, appName);
                break;
            default:
                System.err.println("Invalid mode! Use: manual, auto, test, or abuse.");
                System.exit(1);
        }

    }
    
    /*
      * FUNCTION : waitForConnection
      * DESCRIPTION : Keeps trying to connect to the server until successful.
      * PARAMETERS : serverIP - Server's IP address, port - Server's port number.
      * RETURNS : void
    */

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
                    Thread.sleep(5000); // Retry every 5 seconds
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    System.err.println("Client interrupted while waiting for connection.");
                    System.exit(1);
                }
            }
        }
    }

    /*
      * FUNCTION : runManual
      * DESCRIPTION : Lets the user enter log messages manually and send them to the server.
      * PARAMETERS : serverIP - Server's IP, port - Server's port, appName - Client app name.
      * RETURNS : void
    */

    private static void runManual(String serverIP, int port, String appName)
    { 
       Scanner scanner = new Scanner(System.in);
       System.out.println("Manual mode. Enter log level and message separately.");

        while (true) 
        {
       
            System.out.print("Enter log level (DEBUG, INFO, WARN, ERROR, FATAL) or 'exit' to quit: ");
            String logLevel = scanner.nextLine().trim().toUpperCase();
         
            
            if (logLevel.equalsIgnoreCase("EXIT")) 
            {
                break;
            }

            // Validate log level
            if (!logLevel.matches("DEBUG|INFO|WARN|ERROR|FATAL"))
            {
                System.out.println("Invalid log level! Please enter a valid log level.");
                continue;
            }

            // Get log message
            System.out.print("Enter log message: ");
            String logMessage = scanner.nextLine().trim();

            if (logMessage.isEmpty()) 
            {
                System.out.println("Log message cannot be empty!");
                continue;
            }

        
            String jsonPayload = createJsonPayload(logLevel, logMessage, appName);
            sendLog(serverIP, port, jsonPayload);
        }
       scanner.close();
    }

    /*
      * FUNCTION : runAutomated
      * DESCRIPTION : Sends 100 automated log messages to the server.
      * PARAMETERS : serverIP - Server's IP, port - Server's port, appName - Client app name.
      * RETURNS : void
    */

    private static void runAutomated(String serverIP, int port, String appName) 
    {
        System.out.println("Automated mode. Sending test log messages.");
        String[] levels = {"DEBUG", "INFO", "WARN", "ERROR"};

        for (int i = 0; i < 100; i++) 
        {
            String level = levels[i % levels.length];
            String message = "Automated test message " + i;
            String jsonPayload = createJsonPayload(level, message, appName);
            sendLog(serverIP, port, jsonPayload);

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread was interrupted: " + e.getMessage());
            }
        }
    }

    /*
      * FUNCTION : runAutomatedTesting
      * DESCRIPTION : Simulates multiple clients sending test log messages.
      * PARAMETERS : serverIP - Server's IP, port - Server's port, appName - Client app name.
      * RETURNS : void
    */

    private static void runAutomatedTesting(String serverIP, int port, String appName) 
    {
        System.out.println("Running full automated tests...");
        String[] levels = {"DEBUG", "INFO", "WARN", "ERROR", "FATAL"};
        String[] testMessages = {
            "System initialized successfully.",
            "User login attempt successful.",
            "Disk space running low.",
            "Unexpected database error occurred!",
            "Critical system failure! Immediate attention needed."
        };
    
        int numTests = 5; // Define the total number of test logs to be sent
    
        ExecutorService executor = Executors.newFixedThreadPool(2); // Simulating multiple clients
    
        for (int i = 0; i < numTests; i++) 
        {
            final int index = i % testMessages.length; // Ensure we do not go out of bounds
            executor.execute(() -> {
                String jsonPayload = createJsonPayload(levels[index % levels.length], testMessages[index], appName);
                sendLog(serverIP, port, jsonPayload);
            });
        }
    
        executor.shutdown();
        System.out.println("Automated test completed.");
    }
    /*
      * FUNCTION : runAbuseTest
      * DESCRIPTION : Simulates rapid log spam to test server limits.
      * PARAMETERS : serverIP - Server's IP, port - Server's port, appName - Client app name.
      * RETURNS : void
    */

    private static void runAbuseTest(String serverIP, int port, String appName)
    {
      System.out.println("Running abuse prevention test...");

      ExecutorService executor = Executors.newFixedThreadPool(100); // Simulating multiple abusive clients
      Random random = new Random();

      for (int i = 0; i < 20; i++) // Simulate 20 rapid log requests
      { 
         executor.execute(() -> {
         String jsonPayload = createJsonPayload("ERROR", "Spam log message " + random.nextInt(1000), appName);
         sendLog(serverIP, port, jsonPayload);
         });
      }

        executor.shutdown();
        System.out.println("Abuse test completed. Check server logs for rate limiting.");
    }
    /*
      * FUNCTION : escapeJson
      * DESCRIPTION : Escapes quotes in a string for JSON formatting.
      * PARAMETERS : s - The input string.
      * RETURNS : Escaped string.
    */

    private static String escapeJson(String s) 
    {
        if (s == null) return "";
        return s.replace("\"", "\\\"");
    }

    /*
      * FUNCTION : createJsonPayload
      * DESCRIPTION : Formats log data as a JSON string.
      * PARAMETERS : logLevel - Log severity, logMessage - Log content, appName - Client app name.
     * RETURNS : JSON-formatted log string.
    */

    private static String createJsonPayload(String logLevel, String logMessage, String appName) 
    {
        return "{\"logLevel\":\"" + escapeJson(logLevel) + "\", " +
               "\"logMessage\":\"" + escapeJson(logMessage) + "\", " +
               "\"appName\":\"" + escapeJson(appName) + "\"}";
    }

    /*
      * FUNCTION : sendLog
      * DESCRIPTION : Sends a log message to the server and prints the response.
      * PARAMETERS : serverIP - Server's IP, port - Server's port, jsonPayload - Log message in JSON format.
      * RETURNS : void
    */

    private static void sendLog(String serverIP, int port, String jsonPayload) 
    {
        try (Socket socket = new Socket(serverIP, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
    
            out.println(jsonPayload);
            String response = in.readLine();
            
            System.out.println("Server Response:");
            System.out.println(response);
            
        } catch (IOException e) {
            System.err.println("Error communicating with server: " + e.getMessage());
        }
    }
}