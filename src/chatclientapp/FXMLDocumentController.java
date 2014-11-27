/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclientapp;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import message.ChatMessage;

/**
 *
 * @author Ohjelmistokehitys
 */
public class FXMLDocumentController implements Initializable {
    
    ClientBackend backend;
    
    @FXML
    private TextField txtChatMessage;
    
    @FXML
    private Button btnSend;
    
    @FXML
    private Button btnChangeUsername;

    @FXML
    private TextField txtUsername;
    
    @FXML
    private ListView listUsers;
    
    @FXML
    private CheckBox chkPrivate;
    
    @FXML
    private WebView chatMessageArea;
    //private TextArea chatMessageArea;
    
    private String chatHistory = "";
    WebEngine webEngine;
    private String myUsername = "";
    
    @FXML
    private void handleTextChanged(KeyEvent ke) {
        if (txtChatMessage.getLength() > 0)
            btnSend.setDisable(false);
        else
            btnSend.setDisable(true);
        
        if(ke.getCode().equals(KeyCode.ENTER)) {
            send();
        }
    }
    
    private void send() {
        ChatMessage chatM = new ChatMessage();
        chatM.setChatMessage(txtChatMessage.getText());
        txtChatMessage.clear();
        chatM.setUsernameChange(false);
        if (chkPrivate.isSelected()) {
            //int index = listUsers.getSelectionModel().getSelectedIndex();
            //System.out.println(index);
            chatM.setIsPrivate(true);
            String s = listUsers.getSelectionModel().getSelectedItem().toString();
            chatM.setPrivateName(s);
            System.out.println("Private name: " + s);
        }
        
        backend.sendMessage(chatM);
    }
    
    @FXML
    private void handleBtnSend(ActionEvent event) {
        send();
    }
    
    @FXML
    private void handleBtnChangeUsername(ActionEvent event) {
        changeUserName();
    }
    
    @FXML
    private void handleUsernameFieldChanged(KeyEvent ke) {
        if (txtUsername.getLength() > 0)
            btnChangeUsername.setDisable(false);
        else
            btnChangeUsername.setDisable(true);
        
        if(ke.getCode().equals(KeyCode.ENTER)) {
            changeUserName();
        }
    }
    
    private void changeUserName() {
        if (txtUsername.getText().isEmpty())
            return;
        
        ChatMessage chatM = new ChatMessage();
        chatM.setUsername(txtUsername.getText());
        chatM.setUsernameChange(true);
        backend.sendMessage(chatM);
        myUsername = txtUsername.getText();
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                    txtChatMessage.requestFocus();
            }
        });
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        backend = new ClientBackend(this);
        Thread backThread = new Thread(backend);
        backThread.setDaemon(true);
        backThread.start();
        
        // Put this in EDT queue and run it whe EDT is ready
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                    txtUsername.setText("Anonymous");
                    txtUsername.requestFocus();
                    btnSend.setDisable(true);
                    myUsername = txtUsername.getText();
            }
        });
        
        webEngine = chatMessageArea.getEngine();
        chatHistory = "<font size=\"2\" face=\"verdana\" color=\"black\">";
    }    
    
    public void updateTextArea(ChatMessage cm) {
        if (cm.getUsername().equals(myUsername)) {
            chatHistory = chatHistory + "<font color=\"green\">";
            chatHistory = chatHistory + cm.getUsername() + ": ";
            chatHistory = chatHistory + cm.getChatMessage() + "<br>";
            chatHistory = chatHistory + "</font>";
        } else if (cm.isIsPrivate()) {
            chatHistory = chatHistory + "<font color=\"purple\"><i>";
            chatHistory = chatHistory + cm.getUsername() + ": ";
            chatHistory = chatHistory + cm.getChatMessage() + "<br>";
            chatHistory = chatHistory + "</i></font>";
        } else {
            chatHistory = chatHistory + cm.getUsername() + ": ";
            chatHistory = chatHistory + cm.getChatMessage() + "<br>";
        }
        
        webEngine.loadContent(chatHistory);
    }
    
    public void updateUserList(String userlistString) {
        ObservableList<String> obList = FXCollections.observableArrayList();
        String[] tokens = userlistString.split(";");
        System.out.println(tokens.length);
        for (String name : tokens) {
            System.out.println(name);
        }
        obList.addAll(Arrays.asList(tokens));
        listUsers.setItems(obList); 
    }
}
