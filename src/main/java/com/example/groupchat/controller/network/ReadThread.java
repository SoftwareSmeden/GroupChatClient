package com.example.groupchat.controller.network;

import com.example.groupchat.entity.ChatClient;
import com.example.groupchat.usecase.MessageHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReadThread extends Thread {

    private BufferedReader bufferedReader;
    private Socket socket;
    private ChatClient client;
    private boolean listener;

    public ReadThread(Socket socket, ChatClient client, boolean listenerValue) {
        this.socket = socket;
        this.client = client;
        setListener(listenerValue);
        try {
            InputStream input = socket.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException ex) {
            MessageHandler.getInstance().recevieMessageFromServer("[ERROR]: Error getting input stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {
        while (listener) {
            try {
                String messageFromServer = bufferedReader.readLine();
                if (messageFromServer != null && !messageFromServer.contains("[" + client.getUserName() + "]")) {
                    MessageHandler.getInstance().recevieMessageFromServer(messageFromServer);
                }
            } catch (IOException e) {
                e.printStackTrace();
                MessageHandler.getInstance().recevieMessageFromServer("[ERROR]: Error receiving message from the server.");
                break;
            }
        }
        MessageHandler.getInstance().recevieMessageFromServer("[CONNECTION]: Lost connection to server.");
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public void setListener(boolean value) {
        if (value) {
            listener = true;
        } else {
            listener = false;
        }
    }

    //    public void receiveMessageFromServer(VBox vBox){
    //        new Thread(() -> {
    //            while (socket.isConnected()) {
    //                try {
    //                    String messageFromServer = bufferedReader.readLine();
    ////                    System.out.println("RECEIVE " + messageFromServer);
    ////                    System.out.println("CLIENT USERNAME " + client.getUserName());
    ////                    System.out.println("CONTAINS "+ messageFromServer.contains(client.getUserName()));
    //                    if (!messageFromServer.contains("["+client.getUserName()+"]")){
    //                        UI.addLabel(messageFromServer, vBox);
    //                    }
    //                } catch (IOException e) {
    //                    e.printStackTrace();
    //                    UI.addLabel("[ERROR]: Error receiving message from the server", vBox);
    //                    break;
    //                }
    //            }
    //        }).start();
    //    }
}