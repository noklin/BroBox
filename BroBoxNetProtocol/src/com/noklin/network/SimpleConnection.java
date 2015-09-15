
package com.noklin.network;

 
import java.io.IOException; 
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;      

/**
 *
 * @author noklin
 */

public class SimpleConnection implements Connection{   
    private final Socket clientSocket; 
    
    public SimpleConnection(Socket serverSocket){ 
        this.clientSocket = serverSocket; 
    } 
    public SimpleConnection(String host, int port) throws IOException{
        this(new Socket(host,port));
    } 
     
     
    @Override
    public void sendBytes(byte...bytes) throws IOException{  
        OutputStream out = clientSocket.getOutputStream(); 
        out.write(bytes);
        out.flush(); 
    }

    @Override
    public byte[] receiveBytes() throws IOException {
        InputStream in = clientSocket.getInputStream(); 
        byte[] tmp = null; 
        int size;
        int sizePartTwo = 0; 
        if((size = in.read()) != -1 && (sizePartTwo = in.read()) != -1){ 
            size |= sizePartTwo << 8 ;  
            if(size > 1){
                tmp = new byte[size];
                tmp[0] = (byte)size;
                tmp[1] = (byte)sizePartTwo;
                int data;
                for(int i = 2 ; i < size ; i++){
                    data = in.read();
                    if(data != -1){
                        tmp[i] = (byte)data;
                    }else break;
                }   
            }
        }   
        return tmp; 
    }  

    @Override
    public void close() throws IOException {
        clientSocket.close();
    }
}
