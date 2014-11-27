/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclientapp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
//import java.io.OutputStream;
import java.net.Socket;
import javafx.application.Platform;
import message.ChatMessage;

/**
 *
 * @author Ohjelmistokehitys
 */
public class ClientBackend implements Runnable {

    private Socket clientSocket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private FXMLDocumentController controller;
    //private OutputStream out;
    
    public ClientBackend(FXMLDocumentController controller) {
        try {
            clientSocket = new Socket("localhost", 3030);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        this.controller = controller;
    }
    
    @Override
    public void run() {
        
        if (clientSocket == null)
            return;
        try {
            // Output stream must be opened first
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            input = new ObjectInputStream(clientSocket.getInputStream());
            //out = clientSocket.getOutputStream();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        // read and write from socket until user closes the app
        while(true) {
            try {
                final ChatMessage cm = (ChatMessage)input.readObject();
                Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        if (cm.isUserListUpdate()) {
                            controller.updateUserList(cm.getChatMessage());
                        }
                        else
                            controller.updateTextArea(cm);
                    }
                });
                
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void sendMessage(ChatMessage cm) {
        try {
            output.writeObject(cm);
            output.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
