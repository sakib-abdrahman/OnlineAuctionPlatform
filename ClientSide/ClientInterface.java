package Client;
import com.google.gson.Gson;
import com.sun.security.ntlm.NTLMException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static javafx.scene.input.DataFormat.URL;

public class ClientInterface extends Application{
    private static TextArea log = new TextArea();
    private static TextArea basicInfo = new TextArea();
    private static TextArea biddingHistory = new TextArea();
    private Stage myStage = new Stage();
    private boolean answer = true;
    private static Client client = null;
    public static ChoiceBox<String> itemChoices;
    public static ChoiceBox<String> itemsHIS;
    private static Label selectionStatus = new Label();

    @Override
    public void start(Stage primaryStage){
        //try Application.init() instead
        basicInfo.setPrefHeight(150);
        basicInfo.setPrefWidth(500);
        log.setPrefHeight(300);
        log.setPrefWidth(400);
        biddingHistory.setPrefWidth(350);
        biddingHistory.setPrefHeight(300);

        //====================CLIENT INTERFACE WINDOW (POST-LONGIN)====================
        primaryStage.setTitle("Client Interface");

        TabPane tabPane = new TabPane();
        Tab menu = new Tab("Console Bidder");
        Tab stats = new Tab("Bidding Log");
        Tab params = new Tab("Show Detailed History");

        GridPane consoleGrid = new GridPane();
        consoleGrid.setPadding(new Insets(10, 10, 10, 10));
        consoleGrid.setVgap(5);
        consoleGrid.setHgap(3);

        GridPane logGrid = new GridPane();
        logGrid.setPadding(new Insets(10, 10, 10, 10));
        logGrid.setVgap(5);
        logGrid.setHgap(3);

        GridPane historyGrid = new GridPane();
        historyGrid.setPadding(new Insets(10, 10, 10, 10));
        historyGrid.setVgap(5);
        historyGrid.setHgap(3);

        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            closeSimulation();
        });

        ChoiceBox<String> backgroundColor = new ChoiceBox<>();
        backgroundColor.getItems().addAll("White","Yellow", "Blue", "Pink", "Aqua", "Maroon", "Salmon", "Light Green");
        GridPane.setConstraints(backgroundColor, 1, 1);
        Button selectColor = new Button("Select Background Color");
        selectColor.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(backgroundColor.getValue() != null) {
                    String color = backgroundColor.getValue();

                    if(color.equals("Yellow")) {
                        consoleGrid.setStyle("-fx-background-color: #fffacd;");
                    }
                    else if(color.equals("Blue")) {
                        consoleGrid.setStyle("-fx-background-color: #6495ed;");
                    }
                    else if(color.equals("Pink")) {
                        consoleGrid.setStyle("-fx-background-color: #ffc0cb;");
                    }
                    else if(color.equals("Aqua")) {
                        consoleGrid.setStyle("-fx-background-color: #00ffff;");
                    }
                    else if(color.equals("White")) {
                        consoleGrid.setStyle("-fx-background-color: #f8f8ff;");
                    }
                    else if(color.equals("Maroon")) {
                        consoleGrid.setStyle("-fx-background-color: #800000;");
                    }
                    else if(color.equals("Salmon")) {
                        consoleGrid.setStyle("-fx-background-color: #fa8072;");
                    }
                    else if(color.equals("Light Green")) {
                        consoleGrid.setStyle("-fx-background-color: #9acd32;");
                    }
                }
            }
        });
        GridPane.setConstraints(selectColor, 2, 1, 5,1);

        //====================CONSOLE BIDDER====================
        Label itemRow = new Label("Pick an Item");
        itemRow.setFont(Font.font("Garamond", 14));
        GridPane.setConstraints(itemRow, 1, 5);

        itemChoices = new ChoiceBox<>();
        itemChoices.setPrefWidth(100);
        //items will be directly taken from the method and be sent to the dropdown menu
        GridPane.setConstraints(itemChoices, 2, 5);

        TextField price = new TextField();
        price.setPromptText("Enter Price");
        price.setPrefWidth(100);
        GridPane.setConstraints(price, 4, 5);

        Button bid = new Button("Place Bid!");
        GridPane.setConstraints(bid, 5, 5);
        bid.setOnAction(event -> {
            if(!itemChoices.getSelectionModel().isEmpty()){
                client.bidPlaced = true;
                String bidStatus = price.getText()+"->"+itemChoices.getValue();
                client.sendToServer(bidStatus);
            }
        });

        Button justBuy = new Button("Just Buy!");
        GridPane.setConstraints(justBuy, 6, 5);
        justBuy.setOnAction(event -> {
            if(!itemChoices.getSelectionModel().isEmpty()){
                client.sellingStatus = true;
                String justBuyStatus = price.getText()+"->"+itemChoices.getValue();
                client.sendToServer(justBuyStatus);
                //consoleGrid.add(selectionStatus, 0, 15, 10, 1);
/*
                GridPane statusGrid = new GridPane();
                Stage statusStage = new Stage();
                statusStage.setTitle("Client Interface: Login Window");

                selectionStatus.setFont(Font.font("Garamond", 14));
                GridPane.setConstraints(selectionStatus, 1, 0);

                statusGrid.getChildren().addAll(selectionStatus);

                Scene statusScene = new Scene(statusGrid, 400, 200);
                statusStage.setScene(statusScene);
                statusStage.show();
 */
            }});

        GridPane.setConstraints(basicInfo, 1, 6,10,1);
        GridPane.setConstraints(log, 0, 1,10,1);
        GridPane.setConstraints(biddingHistory, 2, 1,10,1);

        Label consoleWindow = new Label("                             [Figure: Basic Information about the Items in Real Time]");
        consoleWindow.setFont(Font.font("Garamond", 14));
        GridPane.setConstraints(consoleWindow, 1, 7, 7, 5);

        //GridPane.setConstraints(selectionStatus, 2, 10, 10, 2);

        Button log_off = new Button("LOG OFF");
        log_off.setFont(Font.font("Courier New"));
        log_off.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setConstraints(log_off, 10, 25,1,2);
        log_off.setOnAction(e -> closeSimulation());

        //====================BIDDING LOG====================
        ChoiceBox<String> backgroundColor_log = new ChoiceBox<>();
        backgroundColor_log.getItems().addAll("White","Yellow", "Blue", "Pink", "Aqua", "Maroon", "Salmon", "Light Green");
        GridPane.setConstraints(backgroundColor_log, 0, 0);
        Button selectColor_log = new Button("Select Background Color");
        selectColor_log.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(backgroundColor_log.getValue() != null) {
                    String color = backgroundColor_log.getValue();

                    if(color.equals("Yellow")) {
                        logGrid.setStyle("-fx-background-color: #fffacd;");
                    }
                    else if(color.equals("Blue")) {
                        logGrid.setStyle("-fx-background-color: #6495ed;");
                    }
                    else if(color.equals("Pink")) {
                        logGrid.setStyle("-fx-background-color: #ffc0cb;");
                    }
                    else if(color.equals("Aqua")) {
                        logGrid.setStyle("-fx-background-color: #00ffff;");
                    }
                    else if(color.equals("White")) {
                        logGrid.setStyle("-fx-background-color: #f8f8ff;");
                    }
                    else if(color.equals("Maroon")) {
                        logGrid.setStyle("-fx-background-color: #800000;");
                    }
                    else if(color.equals("Salmon")) {
                        logGrid.setStyle("-fx-background-color: #fa8072;");
                    }
                    else if(color.equals("Light Green")) {
                        logGrid.setStyle("-fx-background-color: #9acd32;");
                    }
                }
            }
        });
        GridPane.setConstraints(selectColor_log, 1, 0, 5,1);

        Label logwindow = new Label("                                [Figure: View your bidding log]");
        logwindow.setFont(Font.font("Garamond", 14));
        GridPane.setConstraints(logwindow, 0, 10);

        Button log_off_LOG = new Button("LOG OFF");
        log_off_LOG.setFont(Font.font("Courier New"));
        log_off_LOG.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setConstraints(log_off_LOG, 10, 5,1,2);
        log_off_LOG.setOnAction(e -> closeSimulation());

        //====================BIDDING HISTORY====================
        ChoiceBox<String> backgroundColor_his = new ChoiceBox<>();
        backgroundColor_his.getItems().addAll("White","Yellow", "Blue", "Pink", "Aqua", "Maroon", "Salmon", "Light Green");
        GridPane.setConstraints(backgroundColor_his, 2, 1);
        Button selectColor_his = new Button("Select Background Color");
        selectColor_his.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(backgroundColor_his.getValue() != null) {
                    String color = backgroundColor_his.getValue();

                    if(color.equals("Yellow")) {
                        historyGrid.setStyle("-fx-background-color: #fffacd;");
                    }
                    else if(color.equals("Blue")) {
                        historyGrid.setStyle("-fx-background-color: #6495ed;");
                    }
                    else if(color.equals("Pink")) {
                        historyGrid.setStyle("-fx-background-color: #ffc0cb;");
                    }
                    else if(color.equals("Aqua")) {
                        historyGrid.setStyle("-fx-background-color: #00ffff;");
                    }
                    else if(color.equals("White")) {
                        historyGrid.setStyle("-fx-background-color: #f8f8ff;");
                    }
                    else if(color.equals("Maroon")) {
                       historyGrid.setStyle("-fx-background-color: #800000;");
                    }
                    else if(color.equals("Salmon")) {
                        historyGrid.setStyle("-fx-background-color: #fa8072;");
                    }
                    else if(color.equals("Light Green")) {
                        historyGrid.setStyle("-fx-background-color: #9acd32;");
                    }
                }
            }
        });
        GridPane.setConstraints(selectColor_his, 3, 1, 18,1);

        itemsHIS = new ChoiceBox<>();
        //items will be directly taken from the method and be sent to the dropdown menu
        GridPane.setConstraints(itemsHIS, 0, 1);

        Button buttonHIS = new Button("Show Detailed History");
        GridPane.setConstraints(buttonHIS, 1, 1);

        GridPane.setConstraints(biddingHistory, 1, 3);

        Label hisWindow = new Label("      [Figure: Detailed bidding and buying history of all client]");
        hisWindow.setFont(Font.font("Garamond", 14));
        GridPane.setConstraints(hisWindow, 1, 4, 10, 5);

        Button log_off_HIS = new Button("LOG OFF");
        log_off_HIS.setFont(Font.font("Courier New"));
        log_off_HIS.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setConstraints(log_off_HIS, 20, 22,1,2);
        log_off_HIS.setOnAction(e -> closeSimulation());

        //====================USERNAME STUFF====================
        GridPane userNameGrid = new GridPane();
        Stage userNameStage = new Stage();
        userNameStage.setTitle("Client Interface: Login Window");

        Label userNameLabel = new Label("Enter your User Name Below");
        userNameLabel.setFont(Font.font("Garamond", 14));
        GridPane.setConstraints(userNameLabel, 1, 0);

        TextField userName = new TextField();
        userName.setPromptText("User Name");
        GridPane.setConstraints(userName, 1, 1);

        TextField password = new TextField();
        password.setPromptText("Password");
        GridPane.setConstraints(password, 1, 2);

        Button userNameButton = new Button("Proceed to Bidding");
        GridPane.setConstraints(userNameButton, 1, 3);
        userNameButton.setOnAction(event -> {
            //connect();
            userNameStage.close();
            String name = userName.getText();
            try {
                client = new Client(name);
//updateHistroy();
                Thread threadClientInterface = new Thread(client);
                threadClientInterface.start();

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            Label clientNameScreen = new Label("Client Name: " + name);
            GridPane.setConstraints(clientNameScreen, 5, 0, 10,1);
            clientNameScreen.setFont(Font.font("Garamond", 18));

            consoleGrid.getChildren().addAll(itemRow, itemChoices, price, bid, log_off, selectColor, backgroundColor,
                    basicInfo, consoleWindow, justBuy,clientNameScreen);
            logGrid.getChildren().addAll(log,logwindow,log_off_LOG,backgroundColor_log, selectColor_log);
            historyGrid.getChildren().addAll(biddingHistory, log_off_HIS, itemsHIS, buttonHIS, hisWindow, backgroundColor_his, selectColor_his);

            stats.setContent(logGrid);
            menu.setContent(consoleGrid);
            params.setContent(historyGrid);

            tabPane.getTabs().addAll(menu, stats, params);
/*
            try {
                setItemsOnScreen();
            } catch (FileNotFoundException | NTLMException e) {
                e.printStackTrace();
            }

 */
            Scene scene = new Scene(tabPane, 575, 400);
            primaryStage.setScene(scene);
            primaryStage.show();
        });

        userNameGrid.getChildren().addAll(userName, userNameButton, userNameLabel, password);

        Scene logInScene = new Scene(userNameGrid, 400, 200);
        userNameStage.setScene(logInScene);
        userNameStage.show();

//++++++++++++++StatusScene++++++++++++++++++

    }
    private void closeSimulation() {
        Boolean decision = ConfirmBox("Logging off...", "Do you want to end the bidding?");
        if(decision) {
            Platform.exit();
            System.exit(0);
        }
    }
    private boolean ConfirmBox(String title, String message) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);
        Label label = new Label();
        label.setText(message);

        Button yes = new Button("Yes");
        yes.setOnAction(e -> {
            try {
                client.clientSocket.close();
            } catch (IOException exception) {
                System.out.println("Logged Off Successfully!");
            }

            System.out.println("Network Disconnected");
            answer = true;
            window.close();
        });
        javafx.scene.control.Button no = new Button("No");
        no.setOnAction(e -> {
            answer = false;
            window.close();
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, yes, no);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

        return answer;
    }
    private static void setAuctionItems(ArrayList<Item> items) throws FileNotFoundException{
        for(Item i: items){
            itemChoices.getItems().add(i.itemName);
            itemsHIS.getItems().add(i.itemName);
        }
    }

    private static void setItemsOnScreen(ArrayList<Item> items) throws FileNotFoundException, NTLMException {
        ObservableList<String> itemNames = FXCollections.observableArrayList();
        ObservableList<String> itemDescription = FXCollections.observableArrayList();
        ObservableList<Double> itemMinBidPrice = FXCollections.observableArrayList();
        ObservableList<Double> itemBuyNowPrice = FXCollections.observableArrayList();
        basicInfo.clear();

        basicInfo.appendText("Item Name   ->   Item Description   ->   Min Bid Price   ->   Buy Now Price"+"\n");
        basicInfo.setFont(Font.font("Garamond"));

        for(Item i: items){
            String name = i.itemName;
            itemNames.add(name);
            String description = i.itemDescription;
            itemDescription.add(description);
            double minBidPrice = i.minBidPrice;
            itemMinBidPrice.add(minBidPrice);
            double buyNowPrice = i.buyNowPrice;
            itemBuyNowPrice.add(buyNowPrice);
            basicInfo.appendText(name +"->"+description+"->   "+minBidPrice+"   ->   "+buyNowPrice +"\n");
        }
    }
    public static void updateServerItems(String input) throws FileNotFoundException, NTLMException, NullPointerException {
        Gson gson = new Gson();
        Message message = gson.fromJson(input, Message.class);
        System.out.println("Bidding type: "+message.biddingType);
        if(message.biddingType.equals("update")){
            setItemsOnScreen(message.items);
            setAuctionItems(message.items);
            updateLog();
            updateDetailedHistroy();
        }
        else if(message.biddingType.equals("price too low to bid")){
            System.out.println("Price you entered for "+input+" is too low.");
            selectionStatus = new Label("Buying/Bidding Status: Price you entered is too low.");
            GridPane.setConstraints(selectionStatus, 0, 0, 12, 2);
        }
        else if(message.biddingType.equals("item can't be bid on")){
            System.out.println(input+" can't be bid on");
            selectionStatus = new Label("Buying/Bidding Status: Item can't be bid on.");
            GridPane.setConstraints(selectionStatus, 0, 0, 12, 2);
        }
        else if(message.biddingType.equals("buy successful: bidding won")){
            client.auctionHistory = message.history;
            updateLog();
            updateDetailedHistroy();
            setItemsOnScreen(message.items);
            setAuctionItems(message.items);
            buyingNotification(message.soldItem.itemName);
            selectionStatus = new Label("Buying/Bidding Status: You just bought an item successfully!");
            GridPane.setConstraints(selectionStatus, 0, 0, 12, 2);
        }
        else if(message.biddingType.equals("bidding successful")){
            client.auctionHistory = message.history;
            updateLog();
            updateDetailedHistroy();
            setItemsOnScreen(message.items);
            setAuctionItems(message.items);
            buyingNotification(message.soldItem.itemName);
            selectionStatus = new Label("Buying/Bidding Status: You just bought an item successfully!");
            GridPane.setConstraints(selectionStatus, 0, 0, 12, 2);
        }
        else if(message.biddingType.equals("price is too low to buy")){
            System.out.println("Price you entered for "+input+" is too low.");
            selectionStatus = new Label("Buying/Bidding Status: Price you entered is too low.");
            GridPane.setConstraints(selectionStatus, 0, 0, 12, 2);
        }
        else if(message.biddingType.equals("buy fail")){
            System.out.println("Buy Now failed for "+input);
            selectionStatus = new Label("Buying/Bidding Status: Attempt Failed");
            GridPane.setConstraints(selectionStatus, 0, 0, 12, 2);
        }
        else if(message.biddingType.equals("buy successful")){
            client.auctionHistory = message.history;
            updateDetailedHistroy();
            updateLog();
            setItemsOnScreen(message.items);
            setAuctionItems(message.items);
            buyingNotification(message.soldItem.itemName);
            selectionStatus = new Label("Buying/Bidding Status: You just bought an item successfully!");
            GridPane.setConstraints(selectionStatus, 0, 0, 12, 2);
        }
        else if(message.biddingType.equals("Auction is over")){
            try{
                System.out.println("Disconnecting with the Server...");
                client.clientSocket.close();
                Thread.sleep(4000);
                System.out.println("Network ended with the server");
                System.exit(0);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
    private static void updateDetailedHistroy(){
        biddingHistory.clear();
        for(String str : client.auctionHistory){
            biddingHistory.appendText(str);
            biddingHistory.appendText("\n");
        }
    }
    private static void updateLog(){
        log.clear();
        for(String str : client.auctionHistory){
            if(str.contains(client.getUserName())){
                log.appendText(str);
                log.appendText("\n");
            }
        }
    }
    private static void buyingNotification(String input){
        System.out.println("You just bought the "+input);
    }
}
