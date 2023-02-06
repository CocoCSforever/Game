package game;

import graphicsLib.Window;
import graphicsLib.G;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;


public class Tetris extends Window implements ActionListener {
    public static Timer timer;
    public static final int H = 20, W = 10, C = 25; //size of well: 20 cells high
    public static final int xM = 50, yM = 50;
    public static Color[]  color = {Color.RED, Color.GREEN, Color.BLUE,
                                    Color.ORANGE, Color.CYAN, Color.YELLOW,
                                    Color.MAGENTA, Color.BLACK};
    public static Shape[] shapes = {Shape.Z, Shape.S, Shape.J, Shape.L, Shape.I, Shape.O, Shape.T};
    public static Shape shape;
    public static int iBack = 7;
    public static int[][] well = new int[W][H]; // grid of "dead" shapes
    public static int time = 1, iShape = 0;

    public Tetris() {
        super("Tetris", 1000, 700);
        shape = shapes[G.rnd(7)]; // get a random single shape
        timer = new Timer(30, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    public void paintComponent(Graphics g){
        G.clear(g);
        time++;
        if(time == 60){time = 0; iShape = (iShape + 1)%7;}
        if(time == 30){shapes[iShape].rot();}
        shapes[iShape].show(g);
    }

    public void KeyPressed(KeyEvent ke){
        int vk = ke.getKeyCode();
        if(vk == KeyEvent.VK_LEFT){shape.slide(G.LEFT);}
        if(vk == KeyEvent.VK_RIGHT){shape.slide(G.RIGHT);}
        if(vk == KeyEvent.VK_UP){shape.rot();}
        if(vk == KeyEvent.VK_DOWN){shape.drop(G.DOWN);}
        repaint();
    }

    //clear well
    public static void clearWell(){
        for(int x = 0; x < W; x++){
            for(int y = 0; y < H; y++){
                well[x][y] = iBack;
            }
        }
    }
    //show well
    public static void showWell(Graphics g){
        for(int x = 0; x < W; x++){
            for(int y = 0; y < H; y++){
                g.setColor(color[well[x][y]]);
            }
        }
    }
    public static void main(String[] args){
        //static variable in window, J PANEL, must have it to do swing things
        (PANEL = new Tetris()).launch();
    }
    //-------Shape--------//
    public static class Shape{
        public static Shape Z, S, J, L, I, O, T;
        public G.V[] a = new G.V[4]; // array that holds the 4 squares for each shape
        public int iColor; // index of color
        public static G.V temp = new G.V(0, 0);
        public static Shape cds = new Shape(new int[]{0,0, 0,0, 0,0, 0,0}, 0); // collision detection

        public Shape(int[] xy, int iC){
            for(int i = 0; i < 4; i++){
                a[i] = new G.V(xy[i*2], xy[i*2+1]); // x/y coord is even/odd number
            }
            iColor = iC;
        }
        public void show(Graphics g){
            g.setColor(color[iColor]);
            for(int i = 0; i < 4; i++){
                g.fillRect(x(i), y(i), C, C); // interior color of squares
            }
            g.setColor(Color.BLACK);
            for(int i = 0; i < 4; i++){
                g.drawRect(x(i), y(i), C, C); // interior color of squares
            }
        }

        public int x(int i){return xM + C * a[i].x;}
        public int y(int i){return yM + C * a[i].y;}

        public void rot(){
            temp.set(0, 0);
            for(int i = 0; i < 4; i++){
                a[i].set(-a[i].y, a[i].x); // (x, y) -> (-y, x)
                //mirror of vertical and mirror about 45 axis
                if(temp.x > a[i].x){temp.x = a[i].x;}
                if(temp.y > a[i].y){temp.y = a[i].y;}
            }
            temp.set(-temp.x, -temp.y);
            for(int i = 0; i < 4; i++){
                a[i].add(temp);
            }
        }
        public static boolean collisionDetected(){
            for(int i = 0; i < 4; i++){ // make sure we are not outside boundary of array
                G.V v = cds.a[i]; // local way to refer to collision item
                if (v.x < 0 || v.x >= W || v.y < 0 || v.y >= H) {
                    return true; // beap boop collision detected
                }
            }
            return false;
        }

        public void cdsSet(){ // call on an existing shape: object -> cds.a[i]
            for(int i = 0; i < 4; i++){cds.a[i].set(a[i]);}
        }

        public void cdsGet(){ // call on an existing shape cds.a[i] -> object
            for(int i = 0; i < 4; i++){a[i].set(cds.a[i]);}
        }

        public void cdsAdd(G.V v){ // call on an existing shape // adds vector to each element in cd
            for(int i = 0; i < 4; i++){cds.a[i].add(v);}
        }

        public void slide(G.V dx){
            cdsSet();
            cdsAdd(dx);
            if(collisionDetected()){return;}else{cdsGet();}
        }
        public void drop(G.V dx){}
        static{
            Z = new Shape(new int[]{0,0, 1,0, 1,1, 2,1}, 0);
            S = new Shape(new int[]{0,1, 1,0, 1,1, 2,0}, 1);
            J = new Shape(new int[]{0,0, 0,1, 1,1, 2,1}, 2);
            L = new Shape(new int[]{0,1, 1,1, 2,1, 2,0}, 3);
            I = new Shape(new int[]{0,0, 1,0, 2,0, 3,0}, 4);
            O = new Shape(new int[]{0,0, 1,0, 0,1, 1,1}, 5);
            T = new Shape(new int[]{0,1, 1,0, 1,1, 2,1}, 6);
        }


    }

}
