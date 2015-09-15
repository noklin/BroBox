/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noklin.network;
 
import java.io.Closeable;
import java.io.IOException;

/**
 *
 * @author noklin
 */
public interface Connection extends Closeable{
    void sendBytes(byte...bytes) throws IOException;
    byte[] receiveBytes() throws IOException; 
}
