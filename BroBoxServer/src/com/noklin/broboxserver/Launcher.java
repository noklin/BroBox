
package com.noklin.broboxserver;

import com.noklin.broboxserver.network.Server;

/**
 *
 * @author noklin
 */

public class Launcher {
    public static void main(String[] args) { 
        if(args.length > 0) 
            Config.serverHost = args[0].trim();
        if(args.length > 1){
            try{
                Config.serverPort = Integer.parseInt(args[1].trim());
            }catch(NumberFormatException ex){
                System.out.println("Whong number as port. Using default");
            } 
        }
        try(Server server = new Server()){
            server.start();
            System.out.println("Server shutdown!"); 
        }catch(Exception ex){
            System.out.println("Server shutdown: " + ex.getMessage());
        }
    }
} 