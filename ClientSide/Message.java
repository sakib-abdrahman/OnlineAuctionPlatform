package Client;
import java.util.ArrayList;

public class Message {
    String userName;
    String itemName;
    String biddingType;
    double biddingPrice;
    Item soldItem;
    ArrayList<Item> items;
    ArrayList<String> history;
    String input;
    boolean sellingStatus = false;

    protected Message(){
        this.biddingType = "";
        this.input = "";
        this.biddingPrice = 0;
        System.out.println("Messaging Established with Server");
    }

    protected Message(String biddingType, ArrayList<Item> items, ArrayList<String> history, Item soldItem, String userName){
        this.biddingType = biddingType;
        this.items = items;
        this.history = history;
        this.soldItem = soldItem;
        this.userName = userName;
    }

    protected Message(ArrayList<Item> items, ArrayList<String> history){
        this.items = items;
        this.history = history;
    }

    protected Message(String biddingType, String userName, double biddingPrice, String item){
        this.biddingType = biddingType;
        this.userName = userName;
        this.biddingPrice = biddingPrice;
        this.itemName = item;
    }

}
