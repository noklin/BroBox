/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noklin.network.security; 

/**
 *
 * @author noklin
 */
public class XorCrypter implements Crypter{ 
    
    @Override
    public byte[] enCrypt(byte...raw){
//        for(int i = 2; i < raw.length ; i++){
//            raw[i] ^= i * 10;
//        }
        return raw;
    }
    @Override
    public byte[] deCrypt(byte...raw){ 
//        for(int i = 2; i < raw.length ; i++){
//            raw[i] ^= i * 10;
//        }
        return raw;
    }
}
