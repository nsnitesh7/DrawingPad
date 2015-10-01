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
public class BrushSize extends JPanel{

    private int size;

    public BrushSize(int size){
        super();
        this.size = size;
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.black);
        if(size%2==1)
            g2d.fillOval(15-(size+1)/2, 15-(size+1)/2, size+1, size+1);
        else
            g2d.fillOval(15-(size/2), 15-(size/2), size, size);
    }

}
