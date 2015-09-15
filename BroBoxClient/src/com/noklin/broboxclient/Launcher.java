
package com.noklin.broboxclient; 
 

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
        new UserListener().start();
    } 
}