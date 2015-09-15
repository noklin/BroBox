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
public interface Packet { 
    List<BroFile> readFileList(); 
    PacketType getType();  
    String readFileName();
    int readPosition();  
    byte[] readPart();  
    boolean[] readFileMap();  
}
