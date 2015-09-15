package com.noklin.files; 


import com.noklin.network.packets.PacketFactory;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author noklin
 */
public class FileDonar implements Closeable{
    private final PacketFactory packetFactory;
    private final RandomAccessFile raf; 
    private final byte[] partFile;
    private final boolean[] fileMap; 
    
    public FileDonar(
            File file, 
            int partSize,
            boolean[] fileMap,
            PacketFactory packetFactory) throws FileNotFoundException {
        
        this.raf = new RandomAccessFile(file , "r");
        this.partFile = new byte[partSize];
        this.fileMap = fileMap;
        this.packetFactory = packetFactory;
        
    } 
    
    public byte[] getPart() throws IOException{
        byte[] outPacket = null;
        for(int i = 0 ; i < fileMap.length ; i++){ 
            if(!fileMap[i]){
                raf.seek(i * partFile.length);
                raf.read(partFile); 
                outPacket =  packetFactory.createFilePartPacket(i, partFile); 
                fileMap[i] = true;
                break;
            }
        } 
        return outPacket;
    } 

    @Override
    public void close() throws IOException {
        if(raf != null)
            raf.close();
    }
}
