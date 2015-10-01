/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package servidor;

import entity.Painel;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author oziel
 */
public class ConnectionHandler extends Thread {

    private DataInputStream input;
    private DataOutputStream output;
    private Socket cliente;
    private Servidor server;

    public ConnectionHandler(Servidor server,Socket cliente){
        this.server = server;
        this.cliente = cliente;
        try {
            input = new DataInputStream(cliente.getInputStream());
            output = new DataOutputStream(cliente.getOutputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run(){
        String s;
        while(cliente.isConnected()&&(!this.isInterrupted())){
            try {
                s = input.readUTF();
                server.msgReceived(s, this);
                System.out.println(s);
                if(s.equals("quit")){
                    this.interrupt();
                    System.out.println(toString()+": Interrupted");
                }
            } catch (IOException ex) {
                System.out.println("Erro: "+toString());
                Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void sendMsg(String s){
        try {
            output.writeUTF(s);
        } catch (IOException ex) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
