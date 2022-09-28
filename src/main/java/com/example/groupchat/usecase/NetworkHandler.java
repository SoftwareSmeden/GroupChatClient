package com.example.groupchat.usecase;

import com.example.groupchat.entity.ChatClient;

public class NetworkHandler {

    public void disconnectFromServer(ChatClient client){
        client.disconnectFromServer();
    }
}
