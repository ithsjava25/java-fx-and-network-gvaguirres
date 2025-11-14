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
    private final ObservableList<NtfyMessageDto> unmodifiableMessages = FXCollections.unmodifiableObservableList(messages);


    public HelloModel(NtfyConnection connection) {
        this.connection = connection;
    }
    
    public void initialize() {
        receiveMessage();
    }

    public ObservableList<NtfyMessageDto> getMessages() {
        return unmodifiableMessages;
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
        String message = messageToSend.get();
        if (message == null || message.trim().isEmpty()) {
            return;
        }
        try {
            connection.send(message);
            messageToSend.set("");
            } catch (Exception e) {
                System.out.println("Error while sending message");
            }
    }

    public void receiveMessage(){
        connection.receive(m -> RunOnFx.runOnFx(() -> messages.add(m)));
    }
}



