
package com.noklin.files; 
 
import java.io.File;
import java.net.URI;


/**
 *
 * @author noklin
 */

public class BroFile implements Comparable<Object>{
    private final String name;
    private final long size;
    private final int partCount;
    private final boolean isDirectory;
    
    public BroFile(String name, long size, int partCount, boolean isDirectory) {
        this.name = name;
        this.size = size;
        this.partCount = partCount;
        this.isDirectory = isDirectory;
    }

    public BroFile(File file, int partCount) { 
        this.name = file.getAbsolutePath();
        this.size = file.isFile() ? file.length() : 0;
        this.partCount = partCount;
        this.isDirectory = file.isDirectory();
    }

   
    
    
    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public int getPartCount() {
        return partCount;
    } 

    public boolean isDirectory() {
        return isDirectory;
    }
 
    @Override
    public int compareTo(Object obj) { 
        return isDirectory  ? 1 : -1; 
    }  
}
