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


    /**
     * Create a HelloModel that manages sending and receiving messages over the provided connection.
     *
     * The model begins listening for incoming messages immediately after construction.
     *
     * @param connection the NtfyConnection used to send outgoing messages and receive incoming messages
     */
    public HelloModel(NtfyConnection connection) {
        this.connection = connection;
        receiveMessage();
    }

    /**
     * Provides the observable list of received chat messages.
     *
     * @return the {@link ObservableList} of {@link NtfyMessageDto} representing messages; changes to this list are observable by JavaFX bindings
     */
    public ObservableList<NtfyMessageDto> getMessages() {
        return messages;
    }

    /**
     * Gets the current outgoing message text.
     *
     * @return the current message to send, or `null` if none is set
     */
    public String getMessageToSend() {
        return messageToSend.get();
    }

    /**
     * Exposes the outgoing message property for UI binding.
     *
     * @return the StringProperty representing the message to send
     */
    public StringProperty messageToSendProperty() {
        return messageToSend;
    }

    /**
     * Updates the value of the outgoing message property.
     *
     * @param message the text to set as the message to send
     */
    public void setMessageToSend(String message){
        messageToSend.set(message);
    }

    /**
     * Sends the current outgoing message through the model's connection and clears the outgoing message property.
     *
     * After sending, the messageToSend property is set to the empty string.
     */
    public void sendMessage(){
        connection.send(messageToSend.get());
        messageToSend.set("");
    }

    /**
     * Subscribes to incoming messages and appends each received message to the model's messages list on the JavaFX Application Thread.
     *
     * This method registers a listener with the underlying connection so the model stays updated as messages arrive.
     */
    public void receiveMessage(){
        connection.receive(m -> RunOnFx.runOnFx(() -> messages.add(m)));
    }

}

