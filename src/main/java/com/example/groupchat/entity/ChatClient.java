package com.example.groupchat.entity;

import com.example.groupchat.controller.network.ReadThread;
import com.example.groupchat.controller.network.WriteThread;
import com.example.groupchat.usecase.MessageHandler;

import java.io.IOException;
import java.net.Socket;

public class ChatClient {

    private String hostname;
    private int port;
    private String userName;
    private Socket socket;
    private WriteThread writeThread;
    private ReadThread readThread;
    private boolean isConnectedToServer;


    public ChatClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public boolean execute(boolean listenerValue) {
        this.isConnectedToServer = listenerValue;
        try {
            socket = new Socket(hostname, port);
            MessageHandler.getInstance().recevieMessageFromServer("[CONNECTION]: Connected to server on port " + port);
            readThread = new ReadThread(socket, this, listenerValue);
            readThread.start();
            writeThread = new WriteThread(socket, this);
            return true;
        } catch (IOException e) {
            MessageHandler.getInstance().recevieMessageFromServer("[ERROR]: Server not found.");
            return false;
        }
    }

    public void sendMessageToServer(String messageToServer) {
        if (isConnectedToServer) {
            writeThread.sendMessageToServer(messageToServer);
        }
    }

    /**
     * Disconnect klient from server.
     */
    public void disconnectFromServer(){
        writeThread.sendMessageToServer("[UserQuitServer]");
        readThread.setListener(false);
        closeEverything();
        isConnectedToServer = false;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return this.userName;
    }

    public void closeEverything() {
        try {
            readThread.getBufferedReader().close();
            writeThread.getBufferedWriter().close();
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}