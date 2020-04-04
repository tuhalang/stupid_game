package com.client.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Hello world!
 *
 */
public class ShotPlaneApp
{
    
    public static void main(String[] args)
    {
        EventQueue.invokeLater(() ->
        {
//            ShotPlaneFrame frame = new ShotPlaneFrame();
            PlayTab frame = new PlayTab();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
