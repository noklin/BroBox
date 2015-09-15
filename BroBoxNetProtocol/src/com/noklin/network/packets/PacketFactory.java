/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noklin.network.packets;  
 
import com.noklin.files.BroFile; 
import java.util.List;

/**
 *
 * @author noklin
 */

public interface PacketFactory { 
    Packet asPacket(byte[] raw);
    byte[] createGetFileListPacket(String folder);     
    byte[] createFileListPacket(List<BroFile> fileList);     
    byte[] createGetFilePacket(String fileName, boolean[] fileMap);  
    byte[] createFilePartPacket(int position, byte[] part); 
    byte[] createMoveToPacket(String folder); 
    byte[] createFailPacket(String error);   
    byte[] createOkPacket(); 
}
