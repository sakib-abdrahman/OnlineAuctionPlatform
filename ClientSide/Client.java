package Client;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class Client implements Runnable{
    private String userName;
    private String password;
    public Socket clientSocket = new Socket("localhost", 4242);
    public PrintWriter toServer;
    public BufferedReader fromServer;
    public ArrayList<Item> auctionItems = new ArrayList<>();
    public ArrayList<String> auctionHistory = new ArrayList<>();
    public Boolean bidPlaced = false;
    public boolean sellingStatus = true;

    public String getUserName(){
        return userName;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }

    public Client(String userName) throws Exception{
        this.userName = userName;
            setupNetworking();
    }

    private void setupNetworking() throws Exception{
        System.out.println("Establishing Network with..."+clientSocket);
        fromServer = new BufferedReader(new InputStreamReader((clientSocket.getInputStream())));
        toServer = new PrintWriter(clientSocket.getOutputStream());
    }

    protected void sendToServer(String input){
        if(bidPlaced){
            String[] itemPart = input.split("->");
            Message bid = new Message("bid", userName, Double.parseDouble(itemPart[0]), itemPart[1]);
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            String sendNewUpdate = gson.toJson(bid);
            toServer.println(sendNewUpdate);
            toServer.flush();
            bidPlaced = false;
        }
        else if(sellingStatus){
            String[] itemPart = input.split("->");
            Message bid = new Message("buy", userName, Double.parseDouble(itemPart[0]), itemPart[1]);
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            String sendNewUpdate = gson.toJson(bid);
            toServer.println(sendNewUpdate);
            toServer.flush();
            sellingStatus = false;
        }
    }
    @Override
    public void run() {
        String input;
        try {
            while ((input = fromServer.readLine()) != null) {
                System.out.println("Info being sent from the server: " + input);
                try{
                    ClientInterface.updateServerItems(input);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
