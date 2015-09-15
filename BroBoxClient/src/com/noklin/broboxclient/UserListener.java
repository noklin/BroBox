
package com.noklin.broboxclient;
 
import com.noklin.broboxclient.console.Console;
import com.noklin.broboxclient.network.ServerFileManager;
import com.noklin.files.BroFile;
import com.noklin.network.Connection;
import com.noklin.network.SimpleConnection;
import com.noklin.network.packets.BroBoxPacketFactory;
import com.noklin.network.packets.Packet;
import com.noklin.network.packets.PacketFactory; 
import com.noklin.network.packets.PacketType; 
import java.io.File;
import java.io.IOException;    
import java.util.Scanner;

/**
 *
 * @author noklin
 */

public class UserListener {  
    private final Console console = new Console();
    private final ServerFileManager fileManager = new ServerFileManager(); 
    private final PacketFactory packetFactory = new BroBoxPacketFactory();
    private final Scanner scanner = new Scanner(System.in); 
    
    public void start(){
        File downloadFolder = new File(Config.downloadFolder); 
        downloadFolder.mkdirs();  
        String[] command; 
        String serverHost = Config.serverHost;
        int serverPort = Config.serverPort;
        byte[] req;
        byte[] res;
        Packet data;
        try(Connection serverConnection = new SimpleConnection(serverHost, serverPort)){  
            req = packetFactory.createMoveToPacket(null); 
            serverConnection.sendBytes(req);
            res = serverConnection.receiveBytes();
            data = packetFactory.asPacket(res);
            if(data != null && data.getType() == PacketType.MOVE_TO){ 
                console.setCurrentPath(data.readFileName()); 
                System.out.println("Welcome to Brobox!");
                System.out.println("Current download directory: " + downloadFolder);
                System.out.println("Successful connection with server ");
                System.out.println(Config.HELP_INFO);
            }else{
                throw new IOException("Server not fount");
            }   
        }catch(IOException ex){ 
            System.out.println("Connection problem: " + ex.getMessage()); 
            System.exit(-1);
        }
        while(!Thread.currentThread().isInterrupted()){
            System.out.print(console.getCurrentPath() + ">");
            command = console.parseCommand(scanner.nextLine());
            command[1] = console.constructFileName(command[1]);  
            switch(command[0]){                 
                case "help":{
                    System.out.println(Config.HELP_INFO);
                    break; 
                } 
                case "where":{
                    System.out.println("Brobox: Current downliad directory: "+Config.downloadFolder);
                    break; 
                } 
                case "dir":  {
                    try(Connection serverConnection = new SimpleConnection(serverHost, serverPort)){ 
                        req = packetFactory.createGetFileListPacket(console.getCurrentPath()); 
                        serverConnection.sendBytes(req);
                        res = serverConnection.receiveBytes();
                        data = packetFactory.asPacket(res);  
                        if(data != null){
                            console.refresh(data.readFileList());    
                            System.out.println(console);
                        } 
                    }catch(IOException ex){
                        System.out.println("Brobox: Connection problems: " + ex.getMessage());  
                    }
                    break;   
                }
                case "exite":  { 
                    System.exit(0); 
                }
                case "cd":{    
                    if(command[1].isEmpty()){ 
                        System.out.println("Brobox: I can't move here ");
                        break;
                    } 
                    try(Connection serverConnection = new SimpleConnection(serverHost, serverPort)){ 
                        req = packetFactory.createMoveToPacket(command[1]); 
                        serverConnection.sendBytes(req);
                        res = serverConnection.receiveBytes();
                        data = packetFactory.asPacket(res);
                        if(data != null && data.getType() == PacketType.MOVE_TO){
                            console.setCurrentPath(data.readFileName());
                        }else{
                            System.out.println("Brobox: I can't move here "); 
                        } 
                    }catch(IOException ex){
                        System.out.println("Brobox: Connection problems: " + ex.getMessage());  
                    }
                    break;
                } 
                
                case "get": {  
                    if(command[1].isEmpty()){ 
                        System.out.println("Brobox: I can't pick it"); 
                        break;
                    } 
                    BroFile bf = console.serchFile(command[1]);
                    if(bf == null){
                        System.out.println("BroBox: File not found");
                        break;
                    } 
                    if(bf.isDirectory()){
                        System.out.println("BroBox: I can't download folders.");
                        break;
                    } 
                    try{
                        fileManager.download(bf);
                    }catch(IOException ex){ 
                        System.out.println("BroBox: Dowmload problem: " + ex.getMessage());  
                    }
                    break; 
                }
                
                case "change":{ 
                    if(command[1].isEmpty()){
                        System.out.println("Brobox: I can't choose it for default download folder");  
                    } 
                    File folder = new File(command[1]);
                    folder.mkdirs();
                    if(!folder.isDirectory() || !folder.canWrite()){
                        System.out.println("Brobox: I can't write here: " + command[1]);
                        break;
                    }   
                    Config.downloadFolder = command[1] ; 
                    System.out.println("Brobox: Current download directory: " + command[1]); 
                    break;  
                } 
                
                case "drop":{ 
                    if(command[1].isEmpty()){
                        System.out.println("Brobox: I can't dpor it");     
                        break;
                    } 
                    File uploadFile = new File(command[1]);
                    if(!uploadFile.canRead()){
                        System.out.println("Brobox: I can't read this file: " + command[1]); 
                    } 
                    try{ 
                        fileManager.upload(uploadFile, console.getCurrentPath());
                    }catch(IOException ex){
                        System.out.println("Brobox: Drop problem: " + ex.getMessage());  
                    }
                    break;  
                }    
                default:{
                    System.out.println("Brobox: \""+ command[0] +"\" invalid command ");    
                }
            } 
        }
    } 
} 