package echoclient;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.ProtocolStrings;

public class EchoClient extends Thread {
    Socket socket;
    private int port;
    private InetAddress serverAddress;
    private Scanner input;
    private PrintWriter output;
    private List<EchoListener> listeners = new ArrayList();

    public void registerEchoListener(EchoListener l) {
        listeners.add(l);
    }

    public void unRegisterEchoListener(EchoListener l) {
        listeners.remove(l);
    }

    private void notifyListeners(String msg) {
        for (EchoListener el : listeners) {
            el.messageArrived(msg);
        }
    }

    public void connect(String address, int port) throws UnknownHostException, IOException {
        this.port = port;
        serverAddress = InetAddress.getByName(address);
        socket = new Socket(serverAddress, port);
        input = new Scanner(socket.getInputStream());
        output = new PrintWriter(socket.getOutputStream(), true);  //Set to true, to get auto flush behaviour
//        run();
    }

    public void send(String msg) {
        output.println(msg);
    }

    public void stop1() throws IOException {
        output.println(ProtocolStrings.STOP);
    }
    
    @Override
    public void run() {
        String msg = input.nextLine();
        while (!msg.equals(ProtocolStrings.STOP)) {
            notifyListeners(msg);
            msg = input.nextLine();
            
        }
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
//    public String receive() {
//        String msg = input.nextLine();
//        if (msg.equals(ProtocolStrings.STOP)) {
//            try {
//                socket.close();
//            } catch (IOException ex) {
//                Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        return msg;
//    }

    public static void main(String[] args) {
        int port = 9090;
        String ip = "localhost";
        String message = "'Hello World'";
        if (args.length == 3) {
            port = Integer.parseInt(args[0]);
            ip = args[1];
            message = args[2];
        }
        try {
            EchoClient tester = new EchoClient();
            
            //listener creation and register
            EchoListener l = new EchoListener() {

                @Override
                public void messageArrived(String data) {
                    System.out.println("Listener:" + data);
                    
                }
            };
            tester.registerEchoListener(l);
            
            
            tester.connect(ip, port);
            tester.start();
            System.out.println("Sending " + message);
            tester.send(message);
            System.out.println("Waiting for a reply");
            
//            System.out.println("Received: " + tester.receive()); //Important Blocking call         
            tester.stop1();
            //System.in.read();      
        } catch (UnknownHostException ex) {
            Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
