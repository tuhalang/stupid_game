/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.client.gui;

import java.awt.Container;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;

/**
 *
 * @author tuhalang
 */
public class Game extends JFrame implements KeyListener {

    private ShotPlaneDisplayConponent gameDisplayComponent;
    private Plane plane = new Plane(36, 25, 36, 28);

    public Game() {
        super();
        addController();
        addKeyListener(this);
    }

    private void addController() {
        Container con = getContentPane();
        gameDisplayComponent = new ShotPlaneDisplayConponent(plane);
        con.add(gameDisplayComponent);

    }

    public void showWindow() {
        this.setSize(805, 630);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        Game test = new Game();
        test.showWindow();
    }

    @Override
    public void keyTyped(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            System.out.println("Right key typed");
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            System.out.println("Left key typed");
        }

    }

    private void move(double x1, double y1, double x2, double y2) {
        plane = new Plane(x1, y1, x2, y2);
        gameDisplayComponent.setPlane(plane);
        gameDisplayComponent.repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            Point head = plane.getHead();
            Point tail = plane.getTail();
            boolean e1 = head.getX() == tail.getX() && head.getX() > 2;
            boolean e2 = head.getY() == tail.getY() && head.getX() > 0 && tail.getX() > 0;
            if (e1 | e2) {
                move(head.getX() - 1, head.getY(), tail.getX() - 1, tail.getY());
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            Point head = plane.getHead();
            Point tail = plane.getTail();
            boolean e1 = head.getX() == tail.getX() && head.getX() < 36;
            boolean e2 = head.getY() == tail.getY() && head.getX() < 36 && tail.getX() < 36;
            if (e1 | e2) {
                move(head.getX() + 1, head.getY(), tail.getX() + 1, tail.getY());
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            Point head = plane.getHead();
            Point tail = plane.getTail();
            boolean e1 = head.getX() == tail.getX() && head.getY() > 0 && tail.getY() > 0;
            boolean e2 = head.getY() == tail.getY() && head.getY() > 2;
            if (e1 | e2) {
                move(head.getX(), head.getY() - 1, tail.getX(), tail.getY() - 1);
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            Point head = plane.getHead();
            Point tail = plane.getTail();
            boolean e1 = head.getX() == tail.getX() && head.getY() < 28 && tail.getY() < 28;
            boolean e2 = head.getY() == tail.getY() && head.getY() < 28;
            if (e1 | e2) {
                move(head.getX(), head.getY() + 1, tail.getX(), tail.getY() + 1);
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            shot();
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }

    private void shot() {
        System.out.println("shot !");
        gameDisplayComponent.shot();
        gameDisplayComponent.repaint();
    }
}
