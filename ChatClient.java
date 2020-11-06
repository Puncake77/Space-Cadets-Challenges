import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {

  public static void main(String[] args) {
    String hostName = null;
    String userName = null;
    int portNumber = -1;

    //Losing my Sanity
    if (args.length != 3) {
      System.err.println("USAGE: java ChatClient [HOSTNAME] [PORT_NUMBER] [USERNAME]");
      System.exit(1);
    } else {
      try {
        hostName = args[0];
        portNumber = Integer.parseInt(args[1]);
        userName = args[2];
      } catch (NumberFormatException e) {
        System.err.println("ERROR: Not a valid port number");
        System.exit(1);
      }
    }
    System.out.println("!!Commands: /exit,/e,/refresh,/r");

    try (
        Socket socket = new Socket(hostName, portNumber);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    ) {
      String fromServer;
      String fromUser;
      boolean updateClient = false;
      boolean running = true;

      out.println(userName);
      while (running) {
        while ((fromServer = in.readLine()) != null) {
          if (fromServer.equals("[Server]: Closing connection!")) {
            System.out.println(fromServer);
            running = false;
            break;
          } else if (fromServer.equals("[Server]: Updated client")) {
            updateClient = false;
            System.out.println("Type: ");
            while (true) {
              fromUser = System.console().readLine();
              if (fromUser != null) {
                out.println("[" + userName + "]: " + fromUser);
                break;
              }
            }
          } else if (fromServer.equals("[Server]: Updating client")) {
            updateClient = true;
          } else if (updateClient) {
            System.out.println(fromServer);
          } else {
            System.out.println(fromServer);
            System.out.print("Type: ");
            while (true) {
              fromUser = System.console().readLine();
              if (fromUser != null) {
                out.println("[" + userName + "]: " + fromUser);
                break;
              }
            }

          }

        }
      }

    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
