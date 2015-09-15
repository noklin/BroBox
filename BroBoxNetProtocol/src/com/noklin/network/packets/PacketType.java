 
package com.noklin.network.packets;

/**
 *
 * @author noklin
 */

public enum PacketType {
    MOVE_TO(1), GET_FILE(2), FILE_PART(3), ERROR(4), UNKNOWN(5), GET_FILE_LIST(6), FILE_LIST(7), OK(8);
    private final int id; 
     
    private PacketType(int id){
        this.id = id;
    }
    
    public int asId(){
        return id;
    }
    
    public static PacketType asType(int id){ 
        for(PacketType val : values()) {
            if(val.id == id){
                return val;
            }  
        }
        return UNKNOWN;
    } 
}