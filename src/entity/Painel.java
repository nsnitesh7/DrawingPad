/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.JPanel;

/**
 *
 * @author oziel
 */
public class Painel extends JPanel implements Serializable{

    Graphics g;

    public Painel(JPanel jPanel2) {
        g = super.getGraphics();
    }

}
