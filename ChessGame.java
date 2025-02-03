/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package chessgame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author diegogiron
 */
public class ChessGame {

    /**
     * @param args the command line arguments
     */
    
       public static void main(String[] args) throws IOException {
        LinkedList<Piece> ps=new LinkedList<>();
        BufferedImage all=ImageIO.read(new File(System.getProperty("user.home") + "/Downloads/chess.png"));
        Image imgs[]=new Image[12];
       int ind=0;
        for(int y=0;y<400;y+=200){
        for(int x=0;x<1200;x+=200){
            imgs[ind]=all.getSubimage(x, y, 200, 200).getScaledInstance(64, 64, BufferedImage.SCALE_SMOOTH);
       ind++;
        }
        }
        //white pieces
        Piece whiteking = new Piece(4, 7, true, "king", ps);
        Piece whitequeen = new Piece(3, 7, true, "queen", ps);
        Piece whiterook = new Piece(0, 7, true, "rook", ps);
        Piece whiteknight = new Piece(1, 7, true, "knight", ps);
        Piece whitebishop = new Piece(2, 7, true, "bishop", ps);
        Piece whitebishop2 = new Piece(5, 7, true, "bishop", ps);
        Piece whiteknight2 = new Piece(6, 7, true, "knight", ps);
        Piece whiterook2 = new Piece(7, 7, true, "rook", ps);
        Piece whitepawn = new Piece(0, 6, true, "pawn", ps);
        Piece whitepawn2 = new Piece(1, 6, true, "pawn", ps);
        Piece whitepawn3= new Piece(2, 6, true, "pawn", ps);
        Piece whitepawn4 = new Piece(3, 6, true, "pawn", ps);
        Piece whitepawn5 = new Piece(4, 6, true, "pawn", ps);
        Piece whitepawn6 = new Piece(5, 6, true, "pawn", ps);
        Piece whitepawn7= new Piece(6, 6, true, "pawn", ps);
        Piece whitepawn8 = new Piece(7, 6, true, "pawn", ps);
        //black pieces
        Piece blackking = new Piece(4, 0, false, "king", ps);
        Piece blackqueen = new Piece(3, 0, false, "queen", ps);
        Piece blackbishop = new Piece(2, 0, false, "bishop", ps);
        Piece blackknight = new Piece(1, 0, false, "knight", ps);
        Piece blackrook = new Piece(0, 0, false, "rook", ps);
        Piece blackbishop2 = new Piece(5, 0, false, "bishop", ps);
        Piece blackknight2 = new Piece(6, 0, false, "knight", ps);
        Piece blackrook2 = new Piece(7, 0, false, "rook", ps);
        Piece blackpawn = new Piece(0, 1, false, "pawn", ps);
        Piece blackpawn2 = new Piece(1, 1, false, "pawn", ps);
        Piece blackpawn3 = new Piece(2, 1, false, "pawn", ps);
        Piece blackpawn4 = new Piece(3, 1, false, "pawn", ps);
        Piece blackpawn5= new Piece(4, 1, false, "pawn", ps);
        Piece blackpawn6 = new Piece(5, 1, false, "pawn", ps);
        Piece blackpawn7= new Piece(6, 1, false, "pawn", ps);
        Piece blackpawn8 = new Piece(7, 1, false, "pawn", ps);
        
        
        
        
        
        JFrame frame = new JFrame();
        frame.setBounds(10, 10, 512, 512);
        JPanel pn = new JPanel() {
            @Override
            public void paint(Graphics g) {
                boolean white = true;
//logic to create and cut the piece, this will take the image and cut it, then the index number is assigned for each piece
                for (int y = 0; y < 8; y++) {
                    for (int x = 0; x < 8; x++) {
                        if(white){ //color of board
                    g.setColor(new Color(235,235, 208));
                     } 
                        else
                        {
                        g.setColor(new Color(119, 148, 85));
                    
                }
                        g.fillRect(x * 64, y * 64, 64, 64);
                        white = !white;
                    }
                    white = !white;
                    for(Piece p: ps){
                        int ind = 0;
                    if(p.name.equalsIgnoreCase("king")){
                        ind=0;
                    }
                    if(p.name.equalsIgnoreCase("queen")){
                        ind=1;
                    }
                    if(p.name.equalsIgnoreCase("bishop")){
                        ind=2;
                    }
                    if(p.name.equalsIgnoreCase("knight")){
                    ind=3;
                    }
                    if(p.name.equalsIgnoreCase("rook")){
                        ind=4;
                    }
                    if(p.name.equalsIgnoreCase("pawn")){
                        ind=5;
                    }
                    if(!p.isWhite){
                        ind+=6;
                    }
                    g.drawImage(imgs[ind], p.xp*64, p.yp*64, this);
                    }
                }
            }
        };

        frame.add(pn);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    void setVisible(boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
    
