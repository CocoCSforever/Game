package game;

import graphicsLib.G;
import graphicsLib.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Snake extends Window implements ActionListener {
    public static Color cFood = Color.green, cSnake = Color.blue, cBad = Color.red;
    public static Cell food = new Cell();
    public static Cell.List snake = new Cell.List();
    public static Cell crash = null;
    public static Timer timer; //swing.Timer

    public Snake(){
        super("Snake", 1000, 700);
        startGame();
        timer = new Timer(200, this);//half a second, this means the ActionListener
        timer.start();
    }

    public void startGame(){
        crash = null;
        snake.clear(); //clear the snake list
        snake.iHead = 0;
        snake.growList();
        food.rndLoc();
    }

    public void keyPressed(KeyEvent ke){//virtual key
        int vk = ke.getKeyCode();
        if(vk == KeyEvent.VK_LEFT){snake.direction = G.LEFT;}
        if(vk == KeyEvent.VK_RIGHT){snake.direction = G.RIGHT;}
        if(vk == KeyEvent.VK_UP){snake.direction = G.UP;}
        if(vk == KeyEvent.VK_DOWN){snake.direction = G.DOWN;}
        if (vk == KeyEvent.VK_SPACE){
            startGame();
//            moveSnake();
//            snake.move();
        }
//        for test:
//        if (vk == KeyEvent.VK_A){snake.growList();}
        repaint();

    }
    public static void moveSnake(){
        if(crash != null){return;}
        snake.move();
        Cell head = snake.head();
        if(head.hits(food)){snake.growList(); food.rndLoc(); return;} // eat food
        if(!head.inBoundary()){crash = head; snake.stop(); return;} // out bounds
        if(snake.hits(head)){crash = head; return;} //tail to head
    }

    public void paintComponent(Graphics g){
        G.clear(g);  //clear background
//        g.setColor(Color.red);
//        g.fillRect(100, 100, 100, 100);
        g.setColor(cSnake); snake.show(g);
        g.setColor(cFood); food.show(g);
        if(crash != null){g.setColor(cBad); crash.show(g);} //highlight where the crash happened
        Cell.drawBoundary(g);
    }
    public static void main(String[] args){
        (PANEL = new Snake()).launch();
    }

    @Override
    public void actionPerformed(ActionEvent e) {moveSnake();repaint();}

    //----------Cell----------//
    public static class Cell extends G.V {//Snake.Cell
        public static final int xM = 35, yM = 35, nX = 30, nY = 20, W = 30; // x margin
        public Cell(){
            super(G.rnd(nX), G.rnd(nY));
        }
        public Cell(Cell c){
            super(c.x, c.y);
        }
        public void rndLoc(){
            set(G.rnd(nX), G.rnd(nY));
        }
        public void show(Graphics g){
            g.fillRect(xM + x * W, yM + y * W, W, W);
        }
        public boolean hits(Cell c){
            return c.x == x && c.y == y;
        }
        public boolean inBoundary(){return x >= 0 && x < nX && y >= 0 && y < nY;}
        public static void drawBoundary(Graphics g){
            int xMax = xM + nX * W, yMax = yM + nY * W;
            g.setColor(Color.black);
            g.drawLine(xM,yM, xM, yMax); //vertical line 1
            g.drawLine(xMax,yM, xMax, yMax);
            g.drawLine(xM,yM, xMax, yM); //horizontal line 1
            g.drawLine(xM,yMax, xMax, yMax);
        }
        //----------List----------//
        public static class List extends ArrayList<Cell>{//Snake.Cell.List
            public static G.V STOPPED = new G.V(0, 0);
            public G.V direction = STOPPED;
            public int iHead = 0;

            public void show(Graphics g){
                for(Cell c: this){
                    c.show(g);
                }
            }

            public void growList(){
                Cell cell = (size() == 0)? new Cell(): new Cell(get(0));
                add(cell);
            }

            public void move(){
                if(direction == STOPPED){return;}
                int iTail = (iHead + 1)%size();// (if iTail >= size, it will fall back down within size);
                Cell tail = get(iTail); // set tail to be the new head
                tail.set(get(iHead)); // move tail up to head
                tail.add(direction);
                iHead = iTail; // new head is the former tail
            }

            public Cell head(){return get(iHead);}
            public void stop(){direction = STOPPED;}
            public boolean hits(Cell head){
                for(Cell c: this){
                    if(c != head && c.hits(head)){return true;} // we found a cell which is not the head but hits the head
                }
                return false;
            }

        }

    }
}
