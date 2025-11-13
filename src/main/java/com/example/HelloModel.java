package com.example;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Model layer: encapsulates application data and business logic.
 */
public class HelloModel {

    private final ObservableList<NtfyMessageDto> messages = FXCollections.observableArrayList();
    private final NtfyConnection connection;
    private final StringProperty messageToSend = new SimpleStringProperty();


    public HelloModel(NtfyConnection connection) {
        this.connection = connection;
        receiveMessage();
    }

    public ObservableList<NtfyMessageDto> getMessages() {
        return messages;
    }

    public String getMessageToSend() {
        return messageToSend.get();
    }

    public StringProperty messageToSendProperty() {
        return messageToSend;
    }

    public void setMessageToSend(String message){
        messageToSend.set(message);
    }

    public void sendMessage(){
        connection.send(messageToSend.get());
        messageToSend.set("");
    }

    public void receiveMessage(){
        connection.receive(m -> RunOnFx.runOnFx(() -> messages.add(m)));
    }

}


