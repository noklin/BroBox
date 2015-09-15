package com.noklin.network.packets; 
 
 
import com.noklin.files.BroFile;
import com.noklin.network.security.Crypter;
import com.noklin.network.security.XorCrypter;   

import java.nio.charset.Charset; 
import java.util.ArrayList;
import java.util.Arrays;  
import java.util.List;

/**
 *
 * @author noklin
 */

public class BroBoxPacketFactory implements PacketFactory{   
    
    private final Charset charset;
    private final Crypter crypter;
    
    public BroBoxPacketFactory(){
        this(new XorCrypter() , Charset.forName("cp866"));
    }
    
    public BroBoxPacketFactory(Crypter crypter, Charset charset){ 
        this.charset = charset;
        this.crypter = crypter;
    } 
    
    @Override
    public Packet asPacket(byte... raw) {   
        return raw!= null && raw.length > 2 ? new SimplePacket(crypter.deCrypt(raw)) : null; 
    } 
    
    private byte[] constructRaw(PacketType type, byte[]... bytes) {    
        int size = 5; 
        byte[] body;
        if(bytes != null && bytes[0] != null){ 
            size += bytes.length * 2;
            for(byte[] mb : bytes){
                size += mb.length;
            }
            body = new byte[size];  
            body[3] = (byte)(bytes.length & 0xff);
            body[4] = (byte)((bytes.length >> 8) & 0xff);
            int currentPosition = 5; 
            for(byte[] mb : bytes){
                body[currentPosition++] = (byte)(mb.length & 0xff);
                body[currentPosition++] = (byte)((mb.length >> 8) & 0xff); 
                System.arraycopy(mb, 0, body, currentPosition, mb.length);
                currentPosition += mb.length;
            }
        }else 
            body = new byte[size]; 
        
        body[0] = ((byte)(size & 0xff));
        body[1] = ((byte)((size >> 8) & 0xff));
        body[2] = ((byte)type.asId()); 
        
        return crypter.enCrypt(body); 
    } 
    private byte[] longToBytes(long l){
        return new byte[]{
            (byte)l , (byte)(l >> 8), (byte)(l >> 16), (byte)(l >> 24), 
            (byte)(l >> 32), (byte)(l >> 40), (byte)(l >> 48), (byte)(l >> 56) 
        };
    }
    private byte[] intToBytes(int i){
        return new byte[]{
            (byte)i , (byte)(i >> 8), (byte)(i >> 16), (byte)(i >> 24) 
        }; 
    } 
    
    private byte[] stringToBytes(String str){
        byte[] tmp = null;
            if(str != null)
                tmp = str.getBytes(charset);
        return tmp;
    } 
    
    private String bytesToString(byte... bytes){
        return new String(bytes, charset);
    }
    
    private byte[] fileMapToBytes(boolean... map){
        byte[] tmp = null;
        if(map != null && map.length > 0){
            tmp = new byte[map.length];
            for(int i = 0; i < map.length ; i++) 
                tmp[i] = map[i] ? (byte) 1 : (byte) 0 ; 
        }
        return tmp;
    }
    
    private byte[] boolenToBytes(boolean bool){
        if(bool)
            return new byte[]{1};
        else
            return new byte[]{0};
    }
    private boolean bytesToBoolean(byte... raw){
        return raw[0] != 0;
    }
    
    private boolean[] bytesToFileMap(byte... raw){
        boolean[] tmp = null;
        if(raw != null && raw.length > 0){
            tmp = new boolean[raw.length];
            for(int i = 0; i < raw.length ; i++){
                if(raw[i] == 1)
                    tmp[i] = true;
            }
        }
        return tmp;
    }
    private byte[][] parseRaw(byte...raw){
        byte[][] tmp;
            int currentPosition = 5;
            int blockSize;
            int ItemsCount = (raw[4] & 0xff) << 8;
                ItemsCount |= (raw[3] & 0xff);
            tmp = new byte[ItemsCount][];
            for(int i = 0 ; i < ItemsCount ; i++ ){
                blockSize = raw[currentPosition++] & 0xff; 
                blockSize |= (raw[currentPosition++] & 0xff) << 8; 
                tmp[i] = Arrays.copyOfRange(raw, currentPosition, currentPosition + blockSize);
                currentPosition += blockSize;
            }  
        return tmp;
    }
     
    private long bytesToLong(byte...raw){
        long tmp = 0;
        if(raw.length > 7){
            tmp    =  ((long)raw[7] & 0xff) << 54; 
            tmp   |=  ((long)raw[6] & 0xff) << 48;  
            tmp   |=  ((long)raw[5] & 0xff) << 40;  
            tmp   |=  ((long)raw[4] & 0xff) << 32;  
            tmp   |=  ((long)raw[3] & 0xff) << 24;  
            tmp   |=  ((long)raw[2] & 0xff) << 16;  
            tmp   |=  ((long)raw[1] & 0xff) << 8;  
            tmp   |=  ((long)raw[0] & 0xff);  
        }
        return tmp;
    }
    private int bytesToInt(byte...raw){
        int tmp = 0;
        if(raw.length > 3){
            tmp   =  (raw[3] & 0xff) << 24;    
            tmp  |=  (raw[2] & 0xff) << 16;    
            tmp  |=  (raw[1] & 0xff) << 8;    
            tmp  |=  (raw[0] & 0xff);  
        }
        return tmp;
    } 

     
     
    @Override
    public byte[] createGetFileListPacket(String folder) { 
        return constructRaw(PacketType.GET_FILE_LIST , stringToBytes(folder));
    }
 
    @Override
    public byte[] createGetFilePacket(String fileName, boolean[] fileMap) {
        return constructRaw(PacketType.GET_FILE, stringToBytes(fileName), fileMapToBytes(fileMap));
    }

    @Override
    public byte[] createFilePartPacket(int position, byte[] part) {
        return constructRaw(PacketType.FILE_PART, intToBytes(position), part);
    }
 
    @Override
    public byte[] createMoveToPacket(String folder) {
        return constructRaw(PacketType.MOVE_TO, stringToBytes(folder));
    }

    @Override
    public byte[] createFailPacket(String error) {
        return constructRaw(PacketType.ERROR, stringToBytes(error));
    }

    @Override
    public byte[] createFileListPacket(List<BroFile> fileList) {
        byte[][] tmp = null; 
        if(fileList != null){
            tmp = new byte[fileList.size() * 4][];
            for(int i = 0; i < fileList.size(); i++ ){
                tmp[i * 4] = stringToBytes(fileList.get(i).getName());
                tmp[i * 4 + 1] = longToBytes(fileList.get(i).getSize());
                tmp[i * 4 + 2] = intToBytes(fileList.get(i).getPartCount());  
                tmp[i * 4 + 3] = boolenToBytes(fileList.get(i).isDirectory());  
            }  
        }
        return constructRaw(PacketType.FILE_LIST, tmp);
    } 

    @Override
    public byte[] createOkPacket() {
        return constructRaw(PacketType.OK);
    }

    
    private class SimplePacket implements Packet{ 
        private final PacketType type;
        private byte[][] data; 
        
        private SimplePacket(byte...raw){  
            if(raw.length > 3){
                type = PacketType.asType(raw[2]);
                data = parseRaw(raw);
            }else{
                type = PacketType.UNKNOWN;
            }
        }  
        
        @Override
        public PacketType getType() {
            return type;
        } 
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(); 
            for(byte[] bytes : data){
                sb.append(Arrays.toString(bytes));
            }
            return sb.toString();
        }
        
        @Override
        public List<BroFile> readFileList() {
            List<BroFile> tmp = null;
            if(data != null && data.length > 2){
                tmp = new ArrayList<>(data.length / 4);
                for(int i = 0 ;  i < data.length / 4; i++){
                    tmp.add(new BroFile(
                            bytesToString(data[i * 4]),
                            bytesToLong(data[i * 4 + 1]),
                            bytesToInt(data[i * 4 + 2]),
                            bytesToBoolean(data[i * 4 + 3]))
                    );
                } 
            }
            return tmp;
        } 

        @Override
        public int readPosition() {
            return data != null && data.length > 0 ? bytesToInt(data[0]) : null;
        }

        @Override
        public byte[] readPart() {
            return data != null && data.length > 1 ? data[1] : null;
        }

        @Override
        public boolean[] readFileMap() {
            return data != null && data.length > 1 ? bytesToFileMap(data[1]) : null;
        } 

        @Override
        public String readFileName() {
            return data != null && data.length > 0 ? bytesToString(data[0]) : null;
        } 
    }   
} 