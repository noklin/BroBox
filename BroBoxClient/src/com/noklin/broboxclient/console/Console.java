
package com.noklin.broboxclient.console;


import com.noklin.files.BroFile;  
import java.io.File;
import java.io.IOException; 
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author noklin
 */

public class Console {
    private final List<BroFile> fileList = new LinkedList<>(); 
    private String currentPath;
    
    public void refresh(List<BroFile> list){
        fileList.clear();
        if(list != null && list.size() > 0){ 
            fileList.clear();  
            currentPath = parseParent(list.get(0).getName());
            for(BroFile bf : list){ 
                fileList.add(bf);
            }
            fileList.sort((a,b) -> b.compareTo(a));
        } 
    }
    
    public BroFile serchFile(String name){ 
        BroFile file = null; 
        for(BroFile bf: fileList){  
            if(bf.getName().equals(name))
                file = bf;
        }
        return file;
    } 
    
    @Override
    public String toString() { 
        String name;
        StringBuilder sb = new StringBuilder();
        for(BroFile bf: fileList){
            try{
                name = Paths.get(bf.getName()).getFileName().toString();
                if(bf.isDirectory()){
                    sb.append(" <DIR>  ").append(name);  
                }else{
                    sb.append("<FILE>  ").append(name).append(" ").append(String.format("%.2f",bf.getSize()/1024d)).append("Kb");  
                }
                sb.append("\n");
            }catch(InvalidPathException ignore){/*NOP*/}
        }
        if(sb.length() == 0)
            sb.append("<EMPTY>"); 
        return sb.toString();
    }
    
    public String parseParent(String fileName){
        return fileName != null ? Paths.get(fileName).getParent().toString(): "";
    }
    
    public String[] parseCommand(String command){
        String[] tmp = new String[2]; 
        String[] raw = command.trim().split(" ", 2);
        tmp[1] = raw.length > 1 ? raw[1].trim() : "";
        tmp[0] = raw[0];
        return tmp;
    }
    public String constructFileName(String raw){
        if(raw.contains(":")){
            return raw; 
        }
        
        if(raw.equals(".")){
            return "";
        } 
        
        if(raw.equals("..")){
            Path parent = Paths.get(currentPath).getParent();
            return parent == null ? currentPath : parent.toString(); 
        }
         
        String separator = currentPath.endsWith(File.separator) ? "" : File.separator;
        String fullName = currentPath + separator + raw;  
        return fullName;
    }

     
    
    public void setCurrentPath(String currentPath) { 
        this.currentPath = Paths.get(currentPath).normalize().toAbsolutePath().toString();
    }
 
    public String getCurrentPath() {
        return currentPath;
    }  
    public void cereatFolder(File folder) throws IOException{ 
        try{
            folder.createNewFile();
        }catch(IOException ignore){/*NOP*/}
    } 
}
