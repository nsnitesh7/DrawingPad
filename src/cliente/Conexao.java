/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import entity.Painel;
import java.awt.Color;
import java.awt.Graphics;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author oziel
 */
public class Conexao extends Thread {

    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private Graphics g;

    public Conexao(Graphics g) {
        this.g = g;
    }

    public void run() {
        try {
            socket = new Socket("localhost", 587);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            while (socket.isConnected()) {
                String s = input.readUTF();
                String[] sub = s.split(";");
                String[] subColor = sub[0].split(",");
                int[] color = new int[3];
                for (int i = 0; i < subColor.length; i++) {
                    color[i] = Integer.parseInt(subColor[i]);
                }
                g.setColor(new Color(color[0], color[1], color[2]));
                int type = Integer.parseInt(sub[1]);
                int size = Integer.parseInt(sub[2]);
                String[] subXY = sub[3].split(",");
                int x = Integer.parseInt(subXY[0]);
                int y = Integer.parseInt(subXY[1]);
                if (type == 1) {
                    g.fillOval(x, y, size, size);
                } else {
                    g.fillRect(x, y, size, size);
                }
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void close(){
        try {
            socket.close();
            this.interrupt();
        } catch (IOException ex) {
            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void atualizar(String action) {
        try {
            output.writeUTF(action);
        } catch (IOException ex) {
            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
