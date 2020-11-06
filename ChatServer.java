import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer {

  public static void main(String[] args) {
    int PORTNUMBER = -1;

    //Losing my Sanity
    if (args.length != 1) {
      System.out.println("USAGE: java ChatServer [Port_To_Listen_On]");
      System.exit(1);
    } else {
      try {
        PORTNUMBER = Integer.parseInt(args[0]);
        if (PORTNUMBER < 1024 || PORTNUMBER > 65535) {
          throw new NumberFormatException("Port must be in range 1024:65535 inc.");
        }
      } catch (NumberFormatException e) {
        System.out.println("Port to listen to must be a valid port number");
        System.out.println(e.getMessage());
        System.exit(1);
      }
    }
    //At this point we are certain that a valid port number argument has been provided.
    ArrayList<ChatServerThread> clientServerThreads = new ArrayList<ChatServerThread>();
    boolean listening = true;

    try (ServerSocket serverSocket = new ServerSocket(PORTNUMBER)) {
      while (listening) {
        ChatServerThread tempThread = new ChatServerThread(serverSocket.accept(), clientServerThreads);
        clientServerThreads.add(tempThread);
        tempThread.start();
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
