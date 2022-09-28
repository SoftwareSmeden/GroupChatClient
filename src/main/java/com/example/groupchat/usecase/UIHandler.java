package com.example.groupchat.usecase;

import com.example.groupchat.controller.UI;

public class UIHandler {

    private static UIHandler uiHandler;

    private UIHandler(){

    }

    public static UIHandler getInstance() {
        if (uiHandler == null) {
            uiHandler = new UIHandler();
        }
        return uiHandler;
    }

    public void changeConnectionStatus(){

    }
}
