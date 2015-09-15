/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noklin.broboxclient;

import java.io.File;
import java.io.UnsupportedEncodingException;



/**
 *
 * @author noklin
 */
public class Config { 
    public static int partSize = 55295;
    public static String serverHost = "localhost";
    public static int serverPort = 8765;  
    public static String downloadFolder = System.getProperty("user.dir") + File.separator + "downloads";  
    public final static String HELP_INFO = 
            
              "\n  change [directory]  - Change default download directory" 
            + "\n    drop [filename]   - Upload file to current directory" 
            + "\n     get [filename]   - Download file from current directory" 
            + "\n      cd [directory]  - Change current directory"
            + "\n                      - For previous directory enter: \"cd ..\""
            + "\n     dir              - Show content current directory"
            + "\n    help              - Show help information" 
            + "\n   where              - Show current download folder"
            + "\n   exite              - Exite\n";  
     
}

