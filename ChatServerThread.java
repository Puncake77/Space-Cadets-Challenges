import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class ChatServerThread extends Thread {
  private final ArrayList<ChatServerThread> clientServerThreads;
  private final Socket socket;
  private boolean beingUsed;
  private ArrayList<String> displayList;
  private String userName;

  public ChatServerThread(Socket clientSocket, ArrayList<ChatServerThread> clientServerThreads) {
    super("ChatServerThread");
    this.socket = clientSocket;
    this.beingUsed = true;
    this.displayList = new ArrayList<String>();
    this.clientServerThreads = clientServerThreads;
  }

  public void displayMessage(String formattedMessage) {
    displayList.add(formattedMessage);
  }

  public void sendToAll(String formattedMessage) {
    for (ChatServerThread thread : clientServerThreads) {
      thread.displayMessage(formattedMessage);
    }
  }

  public void run() {

    try (
        PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    ) {
      String inputLine;
      String outputLine;

      userName = in.readLine();
      System.out.println("[CONNECTION ACCEPTED, USER: " + userName + "]");
      out.println("[CONNECTION TO SERVER STABLE]");

      while ((inputLine = in.readLine()) != null) {
        //Code after connection accepted
        if (inputLine.equals("[" + userName + "]: /exit") || inputLine.equals("[" + userName + "]: /e")) {
          out.println("[Server]: Closing connection!");
          System.out.println("User [" + userName + "] has cleanly disconnected");
          clientServerThreads.remove(this);
          break;
        } else if (inputLine.equals("[" + userName + "]: /refresh") || inputLine.equals("[" + userName + "]: /r")) {
          ;
        } else {
          System.out.println(inputLine);
          this.sendToAll(inputLine);
        }
        //Checks for new messages n prints em
        if (this.displayList.size() > 0) {
          out.println("[Server]: Updating client");
          for (String message : displayList) {
            out.println(message);
          }
          out.println("[Server]: Updated client");
          displayList.clear();
        } else {
          out.println("");
        }
      }
    } catch (SocketException e) {
      System.err.println("User [" + userName + "] has forcibly disconnected");
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
