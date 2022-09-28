package com.example.groupchat.controller;

import com.example.groupchat.entity.ChatClient;
import com.example.groupchat.usecase.MessageHandler;
import com.example.groupchat.usecase.NetworkHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class UI implements Initializable {

    @FXML
    private TextField hostname_tf, port_tf, input_tf, user_tf;
    @FXML
    private Label status_label;
    @FXML
    private VBox vBox;
    @FXML
    private Button send_btn;
    @FXML
    private ScrollPane chat_sp;
    private ChatClient client;
    @FXML
    private Button clearChat_btn, disconnect_btn;
    private NetworkHandler networkHandler = new NetworkHandler();

    public void connectToServer(){
        String hostname = hostname_tf.getText().trim(); //Host sættes her med locolhost eller en IP adresse.
        int port = Integer.parseInt(port_tf.getText().trim()); //Port-nummer sættes her.
        client = new ChatClient(hostname, port); //Opret klient.
        setUIController();
        client.setUserName(user_tf.getText().trim()); //Sætter UserName - Skal bruges til at sætte UserName på serveren, der forbindes til.
        boolean status = client.execute(true); //Forbind til serveren.
        connectionStatus(status); //Status sættes efter returværdien for execute().

        if (status){
            client.sendMessageToServer(user_tf.getText().trim()); //Midlertidig måde at sætte UserName på.
        }
    }

    public void setUIController(){
        MessageHandler.getInstance().setController(this);
    }

    public void connectionStatus(boolean status) { //Sætter status labellen for forbindelsen. 1 = aktiv forbindelse / 2 = Ingen forbindelse til server.
        if (status){
            status_label.setText("Forbundet");
            status_label.setTextFill(Color.web("green"));
        } else {
            status_label.setText("Ingen forbindelse");
            status_label.setTextFill(Color.web("red"));
        }
    }

    private void clearChatBox(){
        vBox.getChildren().clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        vBox.heightProperty().addListener(new ChangeListener<Number>() { //Hver gang der tilføjes en ny besked til chatten, hopper vbox ned til den nyeste besked. (Her undgås det at skulle scroll nedad, hver gang en ny besked ankom).
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                chat_sp.setVvalue((Double) newValue);
            }
        });
        chat_sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); //Hide scrollbar in chat window.

        clearChat_btn.setOnAction(event -> clearChatBox()); //TODO TESTKNAP! RYDDER CHATTEN!

        disconnect_btn.setOnAction(event -> {
            networkHandler.disconnectFromServer(client);
            client.closeEverything();
            connectionStatus(false);
        });

        send_btn.setOnAction(event -> {
            String messageToSend = input_tf.getText(); //Getter teksten fra inputfeltet.
            if (!messageToSend.isEmpty()){
                HBox hBox = new HBox(); //Skaber en hBox, hvor beskeden skal være i.
                hBox.setAlignment(Pos.CENTER_RIGHT); //Her bestemmes, hvilken side teksten skal vises i af hBox'en.
                hBox.setPadding(new Insets(5,5,5,10)); //CSS.

                Text text = new Text(messageToSend); //Her indsættes "messageToSend" i Text objektet.
                TextFlow textFlow = new TextFlow(text); //Text tilføjes til et TextFlow objekt for at opnå styling.

                //CSS.
                textFlow.setStyle("-fx-color: rgb(239,242,255); " +
                        "-fx-background-color: rgb(15,125,242);" +
                        "-fx-background-radius: 20px;");
                textFlow.setPadding(new Insets(5,10,5,10));
                text.setFill(Color.color(0.934,0.945,0.996));
                //CSS.

                //Timestamp label.
                Text timeStamp = new Text(timeStamp());

                hBox.getChildren().add(timeStamp); //Tilføjer timestamp til beskeden.
                hBox.getChildren().add(textFlow); //TextFlow tilføjes til hBox.
                vBox.getChildren().add(hBox); //hBox tilføjes til vbox_msg vinduet.

                if (client != null){
                   // client.sendMessageToServer(messageToSend);
                    MessageHandler.getInstance().sendMessageToServer(messageToSend, client); //Besked-arg sendes til en metode inde i klassen Client.
                }
                input_tf.clear(); //Her ryddes inputfeltet fra UI.
            }
        });
    }

    //Metoden er static så den kan kaldes direkte inde fra Client klassen.
    public void addMessageToChat(String msgFromServer){
        HBox hBox = new HBox(); //Skaber en hBox, hvor beskeden skal være i.
        hBox.setAlignment(Pos.CENTER_LEFT); //Her bestemmes, hvilken side teksten skal vises i af hBox'en.
        hBox.setPadding(new Insets(5,5,5,10)); //CSS.

        Text text = new Text(msgFromServer); //Beskeden fra serveren.
        TextFlow textFlow = new TextFlow(text); //Bruges til styling.

        //CSS.
        if (msgFromServer.contains("[ERROR]:")){
            text.setFill(Color.color(1,0.1,0.1)); //Farve bliver sat til rød, hvis det er en fejlbesked.
            textFlow.setStyle("-fx-background-color: rgb(0,0,0);" + "-fx-background-radius: 20px");
        } else if (msgFromServer.contains("[CONNECTION]:")) {
            text.setFill(Color.color(0.2,0.7,0.1)); //Farve bliver sat til grøn, hvis det omhandler forbindelsen.
            textFlow.setStyle("-fx-background-color: rgb(0,0,0);" + "-fx-background-radius: 20px");
        } else {
            textFlow.setStyle("-fx-background-color: rgb(208,208,208);" + "-fx-background-radius: 20px");
        }
        textFlow.setPadding(new Insets(5,10,5,10));
        //CSS.

        //Timestamp label.
        Text timeStamp = new Text(timeStamp());

        hBox.getChildren().add(textFlow); //Tilføjer textFlow til hBox.
        hBox.getChildren().add(timeStamp); //Tilføjer timestamp til beskeden.
        vBox.getChildren().add(hBox); //hBox tilføjes til vBox gennem GUI tråden (main tråden).
    }

    private String timeStamp(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" dd-MM-yyyy HH:mm ");
        LocalDateTime now = LocalDateTime.now();
        return now.format(formatter);
    }

}
