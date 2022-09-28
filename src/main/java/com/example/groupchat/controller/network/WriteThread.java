package com.example.groupchat.controller.network;

import com.example.groupchat.entity.ChatClient;
import com.example.groupchat.usecase.MessageHandler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class WriteThread extends Thread {
    
        private BufferedWriter bufferedWriter;
        private Socket socket;
        private ChatClient client;
    
        public WriteThread(Socket socket, ChatClient client) {
            this.socket = socket;
            this.client = client;
            try {
                OutputStreamWriter output = new OutputStreamWriter(socket.getOutputStream());
                bufferedWriter = new BufferedWriter(output);
            } catch (IOException ex) {
                MessageHandler.getInstance().recevieMessageFromServer("[ERROR]: Getting output stream: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    
        public void sendMessageToServer(String messageToServer){
            try {
                bufferedWriter.write(messageToServer);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
                MessageHandler.getInstance().recevieMessageFromServer("[ERROR]: Sending message to the server.");
            }
        }
    
        public BufferedWriter getBufferedWriter(){
            return bufferedWriter;
        }
    }