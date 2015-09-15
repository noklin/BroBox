 
package com.noklin.broboxserver.file;

import com.noklin.broboxserver.Config;
import com.noklin.files.BroFile;
import java.io.File;  
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;  
import java.util.List; 

/**
 *
 * @author noklin
 */

public class FolderScanner { 
    public List<BroFile> scannFolder(String folderName){
        List<BroFile> list = null;
        File folder = new File(folderName);   
        File[] files = folder.listFiles((f)-> Files.isReadable(Paths.get(f.toURI())));
        if(files != null && files.length > 0){
            list = new ArrayList<>();
            for(File f : files){ 
                list.add(new BroFile(f, getPartNumber(f)));  
            } 
        } 
        return list;
    } 
    
    private int getPartNumber(File file){
        int partNumber = 0;
        partNumber = (int)(file.length() / Config.partSize);
        if(file.length() - Config.partSize * partNumber > 0)
            partNumber++;
        return partNumber;
    } 
} 