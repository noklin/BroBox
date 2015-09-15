 
package com.noklin.broboxclient.network;

import com.noklin.broboxclient.Config;
import com.noklin.files.BroFile;
import com.noklin.files.FileDonar;
import com.noklin.network.Connection;
import com.noklin.network.SimpleConnection;
import com.noklin.network.packets.BroBoxPacketFactory;
import com.noklin.network.packets.Packet;
import com.noklin.network.packets.PacketFactory;
import com.noklin.network.packets.PacketType;
import java.io.File; 
import java.io.IOException;
import java.io.RandomAccessFile; 
import java.nio.file.Paths;
import java.util.Arrays;

/**
 *
 * @author noklin
 */ 

public class ServerFileManager {
    private final PacketFactory packetFactory = new BroBoxPacketFactory();  
    
    public void download(BroFile bf) throws IOException{ 
        File futureFile = new File(Config.downloadFolder + File.separator + Paths.get(bf.getName()).getFileName());
        File newFile = new File(Config.downloadFolder + File.separator + Paths.get(bf.getName() + "._bf").getFileName()); 
        if(futureFile.length() == 0 && newFile.createNewFile()){  
            RandomAccessFile raf = new RandomAccessFile(newFile, "rw"); 
            boolean[] fileMap = new boolean[bf.getPartCount()];
            if(fileMap.length == 0)
                fileMap = new boolean[1];
            int counter = 0;
            raf.setLength(bf.getSize());
            try(Connection serverConnection = new SimpleConnection(Config.serverHost, Config.serverPort)){ 
                serverConnection.sendBytes(packetFactory.createGetFilePacket(bf.getName(), fileMap));
                Packet currentPacket;
                int partNumber = bf.getPartCount();
                byte[] currentPart;
                int currentPosition; 
                while(counter < partNumber){ 
                    currentPacket = packetFactory.asPacket(serverConnection.receiveBytes());
                    currentPart = currentPacket.readPart();
                    currentPosition = currentPacket.readPosition();
                    raf.seek(currentPosition * currentPart.length);
                    raf.write(currentPart);
                    fileMap[currentPosition] = true; 
                    counter++;
                    if(counter % 10 == 0 || counter == partNumber){
                        System.out.print("\rBrobox: "+bf.getName()+" downloaded " + counter * 100 / partNumber + "%"); 
                    }
                    
                }
                raf.setLength(bf.getSize());
                raf.close(); 
                newFile.renameTo(futureFile);  
                System.out.println("");
                
            }catch(IOException ex){
                newFile.delete();
                throw ex;
            }
        }
    }
    
    private int getPartNumber(File file){
        int partNumber = 0;
        partNumber = (int)(file.length() / Config.partSize);
        if(file.length() - Config.partSize * partNumber > 0)
            partNumber++;
        return partNumber;
    } 
    public void upload(File file , String currentFolder) throws IOException{  
        try(Connection serverConnection = new SimpleConnection(Config.serverHost, Config.serverPort)){  
            String targetFileName = currentFolder + File.separator + file.getName();
            long targetFileSize = file.length();
            int partsNumber = getPartNumber(file);
            int counter =0;
            BroFile bf = new BroFile(targetFileName, targetFileSize, partsNumber, false);
            byte[] req = packetFactory.createFileListPacket(Arrays.asList(bf)); 
            serverConnection.sendBytes(req); 
            Packet res = packetFactory.asPacket(serverConnection.receiveBytes());
            if(res != null){ 
                if(res.getType() == PacketType.GET_FILE){
                    try (FileDonar fd = new FileDonar(file, Config.partSize, new boolean[getPartNumber(file)], packetFactory)) {
                        byte[] filePart = null;
                        while((filePart = fd.getPart()) != null){
                            serverConnection.sendBytes(filePart);
                            counter++;
                            if(counter % 10 == 0 || counter == partsNumber){
                                System.out.print("\rBrobox: "+file.getName()+" uploaded " + counter * 100 / partsNumber + "%"); 
                            }
                        }
                    }
                    System.out.println("");
                }else if(res.getType() == PacketType.ERROR){
                    throw new IOException(res.readFileName());
                }
            }
        }catch(IOException ex){ 
            throw ex;
        }
    } 
}
