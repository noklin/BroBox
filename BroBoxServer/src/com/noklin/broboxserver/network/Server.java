 
package com.noklin.broboxserver.network;

 
import com.noklin.broboxserver.Config;
import com.noklin.broboxserver.file.FileDownloader;
import com.noklin.broboxserver.file.FolderScanner; 
import com.noklin.files.BroFile;
import com.noklin.files.FileDonar;
import com.noklin.network.Connection;
import com.noklin.network.SimpleConnection;
import com.noklin.network.packets.BroBoxPacketFactory;
import com.noklin.network.packets.Packet;
import com.noklin.network.packets.PacketFactory;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors; 

/**
 *
 * @author noklin
 */

public class Server implements AutoCloseable{
    private final FileDownloader downloader;
    private final FolderScanner folderScanner;
    private final ExecutorService exec;
    private final ServerSocket serverSocket;
    private final PacketFactory packetFactory;
    
    public Server() throws IOException {
        this.serverSocket = new ServerSocket(Config.serverPort, 10 , InetAddress.getByName(Config.serverHost));
        this.exec = Executors.newFixedThreadPool(Config.threadPoolSize);
        this.packetFactory = new BroBoxPacketFactory();
        this.folderScanner = new FolderScanner();
        this.downloader = new FileDownloader();
    }

    
    public void start(){ 
        System.out.println("Brobox server started:");
        System.out.println(this);
        while(!Thread.currentThread().isInterrupted()){
            try{
                Socket clientSocket = serverSocket.accept();
                exec.execute(()->{
                    handleConnection(new SimpleConnection(clientSocket));
                });
            } catch(IOException ex){
                System.err.println("Conection problems: " + ex.getMessage());
            }
        }
    }
    private void handleConnection(Connection conn){
        try(Connection clientConnection = conn){
            Packet inPacket = packetFactory.asPacket(clientConnection.receiveBytes());
            if(inPacket == null){
                return; 
            } 
            switch(inPacket.getType()){
                 
                case GET_FILE_LIST :{
                    String folder = inPacket.readFileName();        
                    if(folder == null)
                        folder = Config.defaultFolder;
                    byte[] outPacket = packetFactory.createFileListPacket(folderScanner.scannFolder(folder));
                    clientConnection.sendBytes(outPacket); 
                    break; 
                }
                case MOVE_TO :{
                    String folder = inPacket.readFileName();        
                    if(folder == null)
                        folder = Config.defaultFolder;
                    if(new File(folder).isDirectory()){
                        byte[] outPacket = packetFactory.createMoveToPacket(folder); 
                        clientConnection.sendBytes(outPacket); 
                    }
                    break; 
                }
                case GET_FILE:{
                    String fileName = inPacket.readFileName(); 
                    File targetFile = new File(fileName); ;
                    if(targetFile.canRead()){ 
                        boolean[] fileMap = inPacket.readFileMap();
                        FileDonar sendableFile = new FileDonar(targetFile, Config.partSize, fileMap, packetFactory);
                        System.out.println("Start upload file: " + targetFile.getAbsolutePath()); 
                        byte[] outPart;
                        while((outPart = sendableFile.getPart()) != null){
                            clientConnection.sendBytes(outPart);
                        }
                        sendableFile.close();
                    } 
                    break;
                }
                case FILE_LIST:{
                    List<BroFile> list = inPacket.readFileList();
                    BroFile fileforDownload = null;
                    if(list != null && list.size() > 0){  
                        fileforDownload = list.get(0); 
                    }
                    try{
                        if(fileforDownload != null){
                            File newFile = new File(fileforDownload.getName()); 
                            if(newFile.createNewFile() && newFile.canWrite()){  
                                clientConnection.sendBytes(packetFactory.createGetFilePacket(null, null));
                                downloader.download(newFile, fileforDownload, clientConnection);  
                            }else{
                                throw new IOException("Can't write here");
                            } 
                        }
                        
                    }catch(IOException ex){
                        clientConnection.sendBytes(packetFactory.createFailPacket(ex.getMessage()));
                    }
                    break;
                }
            }
            
        }catch(IOException ex){
            System.err.println("Client connection handler problem: " + ex);
        } 
    }

    @Override
    public void close() throws Exception {
        serverSocket.close();
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Server address  - ").append(serverSocket.getInetAddress().getHostAddress()).append(":").append(serverSocket.getLocalPort());
        sb.append("\nRoot folder     - ").append(Config.defaultFolder);
        sb.append("\nMax connections - ").append(Config.threadPoolSize);
        sb.append("\nFile part size  - ").append(Config.partSize).append(" bytes").append("\n");
        return sb.toString();
    } 
}
