package com.example.groupchat.usecase;

import com.example.groupchat.controller.UI;
import com.example.groupchat.entity.ChatClient;
import javafx.application.Platform;
import javafx.print.PageLayout;

public class MessageHandler {

    private static MessageHandler messageHandler;
    private UI controller;

    private MessageHandler() {
    }

    public static MessageHandler getInstance(){
        if (messageHandler == null){
            messageHandler = new MessageHandler();
        }
            return messageHandler;
    }

    public void recevieMessageFromServer(String errorMessage){
        Platform.runLater(() -> controller.addMessageToChat(errorMessage));
    }


    public void setController(UI controller) {
        this.controller = controller;
    }


    public void sendMessageToServer(String messageToSend, ChatClient client) {
        client.sendMessageToServer(messageToSend);
    }
}
