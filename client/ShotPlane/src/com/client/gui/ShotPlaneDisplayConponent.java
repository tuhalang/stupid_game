package com.client.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShotPlaneDisplayConponent extends JComponent {
    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 291;
    private List<Line2D> line2DList = null;
    private Map<Rectangle2D, Color> squares;
    private PrintWriter pw;
    Graphics2D g2;
    private Plane plane;
    private boolean displayPlane = true;
    private boolean turn = false;

    public ShotPlaneDisplayConponent(Plane plane) {
        this.plane = plane;
        line2DList = new ArrayList<>();
        squares = new HashMap<>();
        for (int i = 0; i < 15; i++) {
            line2DList.add(new Line2D.Double(10, 10 + i * 20, 290, 10 + i * 20));
            line2DList.add(new Line2D.Double(10 + i * 20, 10, 10 + i * 20, 290));
        }

        addMouseListener(new ShotPlaneHandler());
    }

    public void paintComponent(Graphics g) {
        g2 = (Graphics2D) g;

        for (Line2D line : line2DList)
            g2.draw(line);

        for (Rectangle2D rectangle2D : squares.keySet()) {
            g2.setPaint(squares.get(rectangle2D));
            g2.fill(rectangle2D);
            g2.setPaint(Color.BLACK);
            g2.draw(rectangle2D);
        }
        if (displayPlane) {
            drawPlane(plane);
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    private class ShotPlaneHandler extends MouseAdapter {
        public void mouseClicked(MouseEvent event) {
            Point point = event.getPoint();
            if (event.getClickCount() >= 2 && turn) {
                int x = (int) point.getX();
                int y = (int) point.getY();
                if (x > 10 && x < 290 && y > 10 && y < 290) {
                    int xN = (x - 10) / 20;
                    int yN = (y - 10) / 20;
                    turn = false;
                    System.out.println("turn:" + turn);
                    pw.println("hit:" + xN + ":" + yN);
                }
            }
        }
    }

    public void addSquare(Rectangle2D rectangle2D, Color color) {
        squares.put(rectangle2D, color);
    }

    private void drawPlane(Plane plane) {
        List<Rectangle2D> list = new ArrayList<>();
        Point head = plane.getHead();
        Point tail = plane.getTail();
        if (head.getX() == tail.getX() && head.getY() < tail.getY()) {
            list.add(new Rectangle2D.Double(20 * head.getX() + 10, 20 * head.getY() + 10, 20, 20));
            for (int j = (int) (head.getX() - 2); j <= head.getX() + 2; j++) {
                list.add(new Rectangle2D.Double(20 * j + 10, 20 * head.getY() + 30, 20, 20));
            }
            list.add(new Rectangle2D.Double(20 * head.getX() + 10, 20 * head.getY() + 50, 20, 20));
            for (int j = (int) (head.getX() - 1); j <= head.getX() + 1; j++) {
                list.add(new Rectangle2D.Double(20 * j + 10, 20 * head.getY() + 70, 20, 20));
            }
        } else if (head.getX() == tail.getX() && head.getY() > tail.getY()) {
            list.add(new Rectangle2D.Double(20 * head.getX() + 10, 20 * head.getY() + 10, 20, 20));
            for (int j = (int) (head.getX() - 2); j <= head.getX() + 2; j++) {
                list.add(new Rectangle2D.Double(20 * j + 10, 20 * head.getY() - 10, 20, 20));
            }
            list.add(new Rectangle2D.Double(20 * head.getX() + 10, 20 * head.getY() - 30, 20, 20));
            for (int j = (int) (head.getX() - 1); j <= head.getX() + 1; j++) {
                list.add(new Rectangle2D.Double(20 * j + 10, 20 * head.getY() - 50, 20, 20));
            }
        } else if (head.getY() == tail.getY() && head.getX() < tail.getX()) {
            list.add(new Rectangle2D.Double(20 * head.getX() + 10, 20 * head.getY() + 10, 20, 20));
            for (int j = (int) (head.getY() - 2); j <= head.getY() + 2; j++) {
                list.add(new Rectangle2D.Double(20 * head.getX() + 30, 20 * j + 10, 20, 20));
            }
            list.add(new Rectangle2D.Double(20 * head.getX() + 50, 20 * head.getY() + 10, 20, 20));
            for (int j = (int) (head.getY() - 1); j <= head.getY() + 1; j++) {
                list.add(new Rectangle2D.Double(20 * head.getX() + 70, 20 * j + 10, 20, 20));
            }
        }
        if (head.getY() == tail.getY() && head.getX() > tail.getX()) {
            list.add(new Rectangle2D.Double(20 * head.getX() + 10, 20 * head.getY() + 10, 20, 20));
            for (int j = (int) (head.getY() - 2); j <= head.getY() + 2; j++) {
                list.add(new Rectangle2D.Double(20 * head.getX() - 10, 20 * j + 10, 20, 20));
            }
            list.add(new Rectangle2D.Double(20 * head.getX() - 30, 20 * head.getY() + 10, 20, 20));
            for (int j = (int) (head.getY() - 1); j <= head.getY() + 1; j++) {
                list.add(new Rectangle2D.Double(20 * head.getX() - 50, 20 * j + 10, 20, 20));
            }
        }
        for (Rectangle2D r : list) {
            g2.setPaint(Color.BLUE);
            g2.fill(r);
            g2.setPaint(Color.WHITE);
        }
    }

    public void disablePlane() {
        displayPlane = false;
    }

    public void setPlane(Plane plane) {
        this.plane = plane;
    }

    public void disableComponent() {
        turn = false;
    }

    public void enableComponent() {
        turn = true;
    }

    public void addPrintWirter(PrintWriter pw) {
        this.pw = pw;
    }

    public void putRectangle(Rectangle2D rectangle2D, Color color) {
        squares.put(rectangle2D, color);
    }
}
