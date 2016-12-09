package uq.deco2800.singularity.server.messaging;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.net.InetAddress;
 
public class ThreadServer {
    public static ServerSocket server;

	public static void main(String[] args){
      
        try{
           
            server = new ServerSocket(8080);
    
            System.out.println("Waiting for server");
            Socket socket = server.accept();
            
            InetAddress addr = socket.getInetAddress();    
           
            String ip = addr.getHostAddress();
            System.out.println(ip + "just connected");
            
            
        }catch(IOException e){
        
        	System.out.println("Server is not prepared" + e.getMessage());
        }
        
    }
}
