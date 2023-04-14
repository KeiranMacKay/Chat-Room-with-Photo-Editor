package com.example.wschatserverdemo;

//Imports
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

//Overall class
public class UserImageDrawingProgram extends JFrame implements ActionListener, MouseListener, MouseMotionListener {
    
    //Variable declaration
    private BufferedImage image;
    private Graphics2D g2;
    private int prevX, prevY;
    private int currX, currY;
    private boolean isDrag;
    private JButton saveButt;
    private JButton clearButt;

    //Primary function
    public UserImageDrawingProgram(BufferedImage image) {
        this.image = image;
        this.g2 = image.createGraphics();
        this.isDrag = false;

        //Setting draw color
        //(Will code for this to be a user choice later)
        g2.setColor(Color.RED);

        //Setting up drawing 
        JPanel canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, null);
            }
        };

        //Setting size for drawing canvas
        canvas.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        
        //Adding mouse motion listener
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);

        //Button to save picture
        saveButt = new JButton("Save");
        saveButt.addActionListener(this);

        //Button to clear picture
        clearButt = new JButton("Clear");
        clearButt.addActionListener(this);

        //Adding panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButt);
        buttonPanel.add(clearButt);

        //Boarder setup
        Container contentPane = getContentPane();
        
        contentPane.setLayout(new BorderLayout());
        contentPane.add(canvas, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    //Save picture
    @Override
    public void actionPerformed(ActionEvent e) {
        
        //If save button is clicked
        if (e.getSource() == saveButt) {

            //Saves as PNG file 
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                
                //Try to save image, else display error
                try {
                    ImageIO.write(image, "png", file);
                    JOptionPane.showMessageDialog(this, "Image saved.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: We fucked up: " + ex.getMessage());
                }
            }
        //Clears image of all changes
        //WARNING: Clear include the image itself, as it just pastes a white background onto the image
        //Havent figured out how to preserve the image while still losing the alterations
        } else if (e.getSource() == clearButt) {
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, image.getWidth(), image.getHeight());
            repaint();
        }
    }

    //MOUSE CONTROLS 
    //Some of these are redundent though it may break without them for some reason
    //Regardless they shouldnt need be touched for main program to work, I think

    //Click
    @Override
    public void mouseClicked(MouseEvent e) {}

    //Pressed
    @Override
    public void mousePressed(MouseEvent e) {
        prevX = e.getX();
        prevY = e.getY();
        isDrag = true;
    }

    //Released
    @Override
    public void mouseReleased(MouseEvent e) {
        isDrag = false;
    }

    //Entered
    @Override
    public void mouseEntered(MouseEvent e) {}

    //Exited
    @Override
    public void mouseExited(MouseEvent e) {}

    //Dragged
    @Override
    public void mouseDragged(MouseEvent e) {
        if (isDrag) {
            currX = e.getX();
            currY = e.getY();
            g2.drawLine(prevX, prevY, currX, currY);
            prevX = currX;
            prevY = currY;
            repaint();
        }
    }

    //Moved
    @Override
    public void mouseMoved(MouseEvent e) {}

    //Main function
    public static void main(String[] args) {
        //Image chooser
        JFileChooser fileChooser = new JFileChooser();
        
        //Confirming file is acceptable, else display error
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            //User entered image
            try {
                BufferedImage image = ImageIO.read(fileChooser.getSelectedFile());
                new UserImageDrawingProgram(image);
            //If not image display error
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: You fucked up, choose an image dumbass: " + e.getMessage());
            }
        }
    }
}
