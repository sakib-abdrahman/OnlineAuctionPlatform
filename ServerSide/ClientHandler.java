package Server;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Observer;
import java.util.Observable;

class ClientHandler implements Runnable, Observer {
    String userName;
    private Server server;
    private Socket clientSocket;
    private BufferedReader fromClient;
    private PrintWriter toClient;

    protected ClientHandler(Server server, Socket clientSocket) throws IOException{
        this.server = server;
        this.clientSocket = clientSocket;
        this.userName = "";

        Message itemsInformation = new Message("update",Server.auctionItems, Server.history, null, null);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        toClient = new PrintWriter(this.clientSocket.getOutputStream());
        fromClient = new BufferedReader((new InputStreamReader(this.clientSocket.getInputStream())));
        sendToClient(gson.toJson(itemsInformation));
    }

    protected void sendToClient(String itemsInformation) {
        System.out.println("Info being sent to the client: " + itemsInformation);
        toClient.println(itemsInformation);
        toClient.flush();
    }

    @Override
    public void run() {
        String input;
            try {
               while ((input = fromClient.readLine()) != null) {
                   if(userName.equals("")){
                       Gson gson = new Gson();
                       Message message = gson.fromJson(input, Message.class);
                       userName = message.userName;
                   }
                        System.out.println("Info being sent from the client: " + input);
                        server.bidHandling(input);
                    }
                }
            catch (IOException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void update(Observable obj, Object arg) {
        Message message = null;
        Message myMessage = null;
        if(arg instanceof Message){//making arg into a Message-type object
            message = (Message) arg;
        }
        myMessage = new Message(message.biddingType, message.items, message.history, message.soldItem, message.userName);
        if(!myMessage.userName.equals(this.userName)){
            myMessage.biddingType = "update";
            System.out.println("message user name is "+myMessage.userName);
            System.out.println("this user name is "+this.userName);
        }
        /*if(message.biddingType.equals("price too low to bid") && !message.userName.equals(this.userName)){
            message.biddingType = "update";
            System.out.println("My username is "+ this.userName);;
        }
        else if(message.biddingType.equals("item can't be bid on") && !message.userName.equals(this.userName)){
            message.biddingType = "update";
            System.out.println("My username is "+ this.userName);;
        }
        else if(message.biddingType.equals("buy successful: bidding won") && !message.userName.equals(this.userName)){
            message.biddingType = "update";
            System.out.println("My username is "+ this.userName);;
        }
        else if(message.biddingType.equals("bidding successful") && !message.userName.equals(this.userName)){
            message.biddingType = "update";
            System.out.println("My username is "+ this.userName);;
        }
        else if(message.biddingType.equals("price is too low to buy") && !message.userName.equals(this.userName)){
            message.biddingType = "update";
            System.out.println("My username is "+ this.userName);;
        }
        else if(message.biddingType.equals("buy fail") && !message.userName.equals(this.userName)){
            message.biddingType = "update";
            System.out.println("My username is "+ this.userName);;
        }
        else if(message.biddingType.equals("buy successful") && !message.userName.equals(this.userName)){
            message.biddingType = "update";
            System.out.println("My username is "+ this.userName);;
        }
         */
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        sendToClient(gson.toJson(myMessage));
    }
}
