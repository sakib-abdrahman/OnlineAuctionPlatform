package Client;

public class Item {
    String itemName;
    String itemDescription;
    double minBidPrice;
    double buyNowPrice;
    boolean sellingStatus = false;

    Item(String itemName, String itemDescription, double minBidPrice, double buyNowPrice){
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.minBidPrice = minBidPrice;
        this.buyNowPrice = buyNowPrice;
    }
}
