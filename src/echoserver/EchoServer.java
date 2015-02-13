package echoserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Utils;

public class EchoServer {

    private static boolean keepRunning = true;
    private static ServerSocket serverSocket;
    private static final Properties properties = Utils.initProperties("server.properties");
    private List<ClientHandler> chList = new ArrayList(); 

    public static void stopServer() {
        keepRunning = false;
    }
    
    private void addHandler(ClientHandler ch){
        chList.add(ch);
    }
    
    public void removeHandler(ClientHandler ch){
        chList.remove(ch);
    }
    
    private void runServer() {
        int port = Integer.parseInt(properties.getProperty("port"));
        String ip = properties.getProperty("serverIp");

        Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, "Sever started. Listening on: " + port + ", bound to: " + ip);
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(ip, port));
            do {
                Socket socket = serverSocket.accept(); //Important Blocking call
                Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, "Connected to a client");
                ClientHandler ch = new ClientHandler(socket,this);
                ch.start();
                addHandler(ch);
            } while (keepRunning);
        } catch (IOException ex) {
            Logger.getLogger(EchoServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void send(String msg){
        for(ClientHandler ch:chList){
                ch.send(msg);
        }
    }

    public static void main(String[] args) {

        String logFile = properties.getProperty("logFile");
        Utils.setLogFile(logFile, EchoServer.class.getName());
        try {
            new EchoServer().runServer();
        } catch (Exception e) {
            System.out.println("Some error occurred when trying to run EchoServer: " + e);
        } finally {
            Utils.closeLogger(EchoServer.class.getName());
        }

    }
}
