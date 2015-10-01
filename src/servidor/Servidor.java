package servidor;

import entity.Painel;
import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author oziel
 */
public class Servidor {

    private ServerSocket server;
    private Socket client;
    private ArrayList<ConnectionHandler> list;

    public void start() {
        list = new ArrayList<ConnectionHandler>();
        try {
            server = new ServerSocket(587);
            while (true) {
                System.out.println("Servidor em espera...");
                client = server.accept();
                System.out.println(client.getInetAddress() + " conectado.");
                list.add(new ConnectionHandler(this, client));
                list.get(list.size()-1).start();
                
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void msgReceived(String msg, ConnectionHandler sender) {
        if(msg.equals("quit")){
            sender.interrupt();
            boolean remove = list.remove(sender);
        }
        else{
            for (int i = 0; i < list.size(); i++) {
                if (!list.get(i).equals(sender)) {
                    list.get(i).sendMsg(msg);
                }
            }
        }
    }

    public static void main(String[] args) {
        Servidor s = new Servidor();
        s.start();
    }
}
