package Server;
import com.google.gson.Gson;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Scanner;

public class Server extends Observable {
    public static ArrayList<Item> auctionItems = new ArrayList<>();
    public static ArrayList<String> history = new ArrayList<>();
    public ServerSocket serverSocket;

    public ArrayList<Item> getAuctionItems(){
        return auctionItems;
    }
    public ArrayList<String> getHistory(){
        return history;
    }

    public static void main(String[] args) throws FileNotFoundException {
        File itemFile = new File("src/Server/auctionItems");
        Scanner scanner = new Scanner(itemFile);
        while (scanner.hasNext()){
            String[] itemPart = scanner.nextLine().split("->");
            Item newItem = new Item(itemPart[0], itemPart[1], Double.parseDouble(itemPart[2]), Double.parseDouble(itemPart[3]));
            auctionItems.add(newItem);
        }

        try {//this gets the Server running
            new Server().setUpNetworking();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void setUpNetworking() throws Exception {
        //@SuppressWarnings("resource")     //this isn't necessary because of the serverSocket being made at the top
        serverSocket = new ServerSocket(4242);
        while (true) {
            System.out.println("Looking for clients to Establish Network...");
            Socket socketClient = serverSocket.accept();
            System.out.println("Network Established with " + socketClient);
            ClientHandler clientHandler = new ClientHandler(this, socketClient);

            Thread thread = new Thread(clientHandler);  //threads for each clients
            thread.start();
            this.addObserver(clientHandler);    //keeping track of all clients
            //socketClient.close();     //can't do this here, will need to do that at logout in client
        }
    }

    protected synchronized void bidHandling(String input){
        boolean lowPriceAlert = false;
        boolean buyNowAlert = false;
        boolean auctionEndAlert = false;
        Message eventOutput = null;
        Gson gson = new Gson();
        Message message = gson.fromJson(input, Message.class);
        try{
            if(message.biddingType.equals("bid")){
                Item bidOn = null;
                for(Item auctionItem : auctionItems){
                    if(auctionItem.itemName.equals(message.itemName)){
                        if(message.biddingPrice <= auctionItem.currentPrice || message.biddingPrice < auctionItem.minBidPrice){
                            lowPriceAlert = true;
                            break;
                        }
                        if(message.biddingPrice >= auctionItem.buyNowPrice){
                            buyNowAlert = true;
                            bidOn = auctionItem;
                            String currentBid = message.userName + " just bought the " + message.itemName + " for $"+
                                    message.biddingPrice+"!";
                            history.add(currentBid);
                            break;
                        }
                        bidOn = auctionItem;
                        auctionItem.currentPrice = message.biddingPrice;
                        auctionItem.sellingStatus = true;
                        String currentBid = message.userName + " just bought the " + message.itemName + " for $"+
                                message.biddingPrice+"!";
                        history.add(currentBid);
                    }
                }
                if(bidOn == null){
                    if(lowPriceAlert) eventOutput = new Message("price too low to bid", auctionItems, history, null, message.userName);
                    else eventOutput = new Message("item can't be bid on", auctionItems, history, null, message.userName);
                }
                else{
                    if(lowPriceAlert) eventOutput = new Message("price too low to bid", auctionItems, history, null, message.userName);
                    if(buyNowAlert) {
                        auctionItems.remove(bidOn);
                        eventOutput = new Message("buy successful: bidding won",auctionItems, history, bidOn, message.userName);
                    }
                    eventOutput = new Message("bidding successful", auctionItems, history, bidOn, message.userName);
                }
            }
            else if(message.biddingType.equals("buy")){
                lowPriceAlert = false;
                Item itemSold = null;
                for(Item auctionItem : auctionItems){
                    if(auctionItem.itemName.equals(message.itemName)){
                        if(message.biddingPrice < auctionItem.buyNowPrice){
                            lowPriceAlert = true;
                            break;
                        }
                        itemSold = auctionItem;
                        auctionItem.currentPrice = message.biddingPrice;
                        auctionItem.sellingStatus = true;
                        String currentBid = message.userName + " just bought the " + message.itemName + "for $"+
                                message.biddingPrice+"!";
                        history.add(currentBid);
                    }
                }
                if(itemSold == null){
                    if(lowPriceAlert) eventOutput = new Message("price too low to buy", auctionItems, history, null, message.userName);
                    else eventOutput = new Message("buy fail", auctionItems, history, null, message.userName);
                }
                else{
                    auctionItems.remove(itemSold);
                    eventOutput = new Message("buy successful",auctionItems, history, itemSold, message.userName);
                    if(auctionItems.isEmpty()){
                        auctionEndAlert = true;
                    }
                }
            }
            this.setChanged();      //marks that the Server has changed and has been updated with new information
            this.notifyObservers(eventOutput);

            if(auctionEndAlert){
                System.out.println("Auction is over");
                eventOutput = new Message("Auction is over", auctionItems, history, null, message.userName);
                this.setChanged();
                this.notifyObservers(eventOutput);
                Thread.sleep(10000);
                serverSocket.close();
                System.exit(0);
            }
        }
        catch (Exception exception){
            exception.printStackTrace();
        }
    }
}
