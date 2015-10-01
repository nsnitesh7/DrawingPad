/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cliente;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 *
 * @author oziel
 */
public class BrushTypeRound extends JPanel{

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.black);
        g2d.fillOval(10, 10, 10, 10);
    }

}
