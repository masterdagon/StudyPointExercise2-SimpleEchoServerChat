import echoclient.EchoClient;
import echoclient.EchoListener;
import echoserver.EchoServer;
import java.io.IOException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Lars Mortensen
 */
public class TestClient {
  
  public TestClient() {
  }
  
  @BeforeClass
  public static void setUpClass() {
    new Thread(new Runnable(){
      @Override
      public void run() {
        EchoServer.main(null);
      }
    }).start();
  }
  
  @AfterClass
  public static void tearDownClass() {
    EchoServer.stopServer();
  }
  
  @Before
  public void setUp() {
  }
  
  @Test
  public void send() throws IOException{
    EchoClient client = new EchoClient();
    client.connect("localhost",9090);
    
    client.start();
    
    EchoListener l = new EchoListener() {

                @Override
                public void messageArrived(String data) {
                    System.out.println("Listener:" + data);
                    assertEquals("HELLO", data);
                }
            };
            client.registerEchoListener(l);
    client.send("Hello");
  }
  
}
