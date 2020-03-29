package com.bigwanggang;

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
            ShotPlaneFrame frame = new ShotPlaneFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
