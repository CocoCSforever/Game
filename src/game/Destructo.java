package game;

import graphicsLib.G;
import graphicsLib.Window;

import javax.swing.*; // java.swing.Timer
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

public class Destructo extends Window implements ActionListener {
    public static Timer timer;
    public Grid grid = new Grid();

    public Destructo(){
        super("Destructo",1000, 700);
        timer = new Timer(300, this);
        timer.start();
    }

    public static void main(String[] args){
        (PANEL = new Destructo()).launch();
    }

    @Override
    public void paintComponent(Graphics g){
        G.clear(g);
        grid.show(g);
        g.setColor(Color.BLACK);
        g.drawString("Replay", 10, 20);
        String msg = "Bricks Remaining: "+ grid.bricksRemaining;
        msg += grid.noMoreMoves()? "Game Over ": "";
        g.drawString(msg, 600, 30);
    }

    //mouseClicked() occurs when the user presses and releases the mouse button.
    // A mouse action will not result in a click if the user moves the mouse before releasing the button.
    public void mouseClicked(MouseEvent me){
        int x = me.getX(), y = me.getY();
        if(x < grid.xM/2 && y < grid.yM/2){replayGame();}
        if(grid.contains(x, y)){
            grid.action();
            repaint();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        grid.bubbleSort();
        if(grid.slideCol()){grid.xM += grid.W/2;}
        repaint();
    }

    public void replayGame(){
        grid.rndColors(G.rnd(3)+3);
        grid.bricksRemaining = (grid.nR * grid.nC);
        grid.xM = 100;
    }

    //-----Grid------//
    public static class Grid{
        public static final int nR = 15, nC = 13, W = 50, H = 30;
        public static int xM = 100, yM = 100, bricksRemaining = (nR * nC);
        public static Color[] color = {Color.WHITE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED, Color.PINK};
        public static int iBk = 0; // index of background

        int[][] a = new int[nR][nC];
        int R, C; // set by contains(x, y)

        public Grid(){
            rndColors(5); // 5 is the max value
        }
        public void rndColors(int k){
            for(int r = 0; r < nR; r++){
                for(int c = 0; c < nC; c++){
                    a[r][c] = G.rnd(k)+1; //0 - k-1 -> 1-k
                }
            }
        }
        public void show(Graphics g){
            for(int r = 0; r < nR; r++){
                for(int c = 0; c < nC; c++){
                    g.setColor(color[a[r][c]]);
                    g.fillRect(x(c), y(r), W, H);
                }
            }
        }
        public int x(int c){return xM + c*W;}
        public int y(int r){return yM + r*H;}

        public int c(int x){return (x-xM)/W;}
        public int r(int y){return (y-yM)/H;}

        public boolean contains(int x, int y){
            if(x < xM || y < yM){return false;}
            R = r(y); C = c(x);
            if(R >= nR || C >= nC){return false;}
            return true;
        }
        public void action(){
//            a[R][C] = iBk;
            if(infectable(R, C)){infect(R, C, a[R][C]);}
        }

        public void infect(int r, int c, int v){// v is the color of guiding
            if(a[r][c] != v){return;}
            a[r][c] = iBk; // delete one brick
            bricksRemaining--;
            if(r > 0){infect(r-1, c, v);}
            if(c > 0){infect(r, c-1, v);}
            if(r < nR-1){infect(r+1, c, v);}
            if(c < nC-1){infect(r, c+1, v);}
        }

        public boolean infectable(int r, int c){
            int v = a[r][c];
            if(v == iBk){return false;}
            if(r > 0){if(a[r-1][c] == v){return true;}}
            if(c > 0){if(a[r][c-1] == v){return true;}}
            if(r < nR-1){if(a[r+1][c] == v){return true;}}
            if(c < nC-1){if(a[r][c+1] == v){return true;}}
            return false;
        }

        public void bubbleSort(){
            for(int c = 0; c < nC; c++){
                while(bubble(c)){break;}
            }
        }

        public boolean bubble(int c){
            boolean res = false;
            for(int r = nR-1; r > 0; r--){
                if(a[r][c] == iBk && a[r-1][c] != iBk){
                    a[r][c] = a[r-1][c];
                    a[r-1][c] = iBk;
                    res = true;
                }
            }
            return res;
        }

        public boolean colIsEmpty(int c){
            for(int r = 0; r < nR; r++){
                if(a[r][c] != iBk){return false;}
            }
            return true;
        }

        public void swapCol(int c){// c is the non-empty col, c-1 is empty
            for(int r = 0; r < nR; r++){
                a[r][c-1] = a[r][c];
                a[r][c] = iBk;
            }
        }

        public boolean slideCol(){
            boolean res = false;
            for(int c = 1; c < nC; c++){
                if(colIsEmpty(c-1) && !colIsEmpty(c)){swapCol(c); res = true;}
            }
            return res;
        }

        public boolean noMoreMoves(){
            for(int r = 0; r < nR; r++){
                for(int c = 0; c < nC; c++){
                    if(infectable(r, c)){
                        return false;
                    }
                }
            }
            return true;
        }
    }
}
