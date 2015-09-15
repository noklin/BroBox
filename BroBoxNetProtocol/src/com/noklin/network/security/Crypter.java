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
public interface Crypter {
    byte[] enCrypt(byte...raw);
    byte[] deCrypt(byte...raw);
}