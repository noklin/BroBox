/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noklin.broboxserver;
 

/**
 *
 * @author noklin
 */
public class Config { 
    public static int partSize = 55295;
    public static String serverHost = "localhost";
    public static int serverPort = 8765;
    public static int threadPoolSize = 5;
    public static String defaultFolder = System.getProperty("user.dir"); 
}
