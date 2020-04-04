package com.client.gui;

import java.awt.*;

public class Plane {
    private Point head;
    private Point tail;

    public Plane(double x1, double y1, double x2, double y2) {
        this.head = new Point((int) x1, (int) y1);
        this.tail = new Point((int) x2, (int) y2);
    }

    public Plane(Point head, Point tail) {
        this.head = head;
        this.tail = tail;
    }

    public Point getHead() {
        return head;
    }

    public Point getTail() {
        return tail;
    }
}
