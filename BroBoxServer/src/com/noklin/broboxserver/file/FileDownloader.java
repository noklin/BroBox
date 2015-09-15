/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noklin.broboxserver.file;

import com.noklin.files.BroFile;
import com.noklin.network.Connection; 
import com.noklin.network.packets.BroBoxPacketFactory;
import com.noklin.network.packets.Packet;
import com.noklin.network.packets.PacketFactory;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile; 

/**
 *
 * @author noklin
 */
public class FileDownloader {
    private final PacketFactory packetFactory= new BroBoxPacketFactory(); 
    public void download(File newFile, BroFile bf, Connection clientConnection){ 
        try (RandomAccessFile raf = new RandomAccessFile(newFile, "rw")) { 
            raf.setLength(bf.getSize());
            int partCount = bf.getPartCount();
            byte[] part;
            Packet inPacket;
            int currentPosition;
            while(partCount > 0){
                inPacket = packetFactory.asPacket(clientConnection.receiveBytes());
                part = inPacket.readPart();
                currentPosition = inPacket.readPosition();
                raf.seek(currentPosition * part.length);
                raf.write(part);
                partCount--;
            }
            raf.setLength(bf.getSize());
            System.out.println("Downloaded : " + bf.getName());
        }catch(IOException ex){
            newFile.delete();
        }
    }
}
