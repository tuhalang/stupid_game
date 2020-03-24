package com.bigwanggang;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    public static final Pattern HITPATTER = Pattern.compile("hit:(\\d+):(\\d+)");
    public static final Pattern RESPONSEPATTERN = Pattern.compile("hitResponse:(\\d+):(\\d+):(\\d+)");

    public static boolean ifHitPlane(Plane plane, Point point) {
        Point headOfPlane = plane.getHead();
        Point tailOfPlane = plane.getTail();
        boolean result = false;
        if (headOfPlane.getX() == tailOfPlane.getX()) {
            if (point.getY() == headOfPlane.getY() + 1 && Math.abs(point.getX() - headOfPlane.getX()) <= 2)
                result = true;
            if (point.getY() == headOfPlane.getY() + 2 && Math.abs(point.getX() - headOfPlane.getX()) <= 0)
                result = true;
            if (point.getY() == headOfPlane.getY() + 3 && Math.abs(point.getX() - headOfPlane.getX()) <= 1)
                result = true;
        }
        if (headOfPlane.getY() == tailOfPlane.getY()) {
            if (point.getX() == headOfPlane.getX() + 1 && Math.abs(point.getY() - headOfPlane.getY()) <= 2)
                result = true;
            if (point.getX() == headOfPlane.getX() + 2 && Math.abs(point.getY() - headOfPlane.getY()) <= 0)
                result = true;
            if (point.getX() == headOfPlane.getX() + 3 && Math.abs(point.getY() - headOfPlane.getY()) <= 1)
                result = true;
        }
        return result;
    }

    public static boolean ifHitDownPlane(Plane plane, Point point) {
        return point.equals(plane.getHead());
    }


    public static boolean isHitAction(String info) {
        return HITPATTER.matcher(info).matches();
    }

    public static boolean isHitResponseAction(String info) {
        return RESPONSEPATTERN.matcher(info).matches();
    }

}
