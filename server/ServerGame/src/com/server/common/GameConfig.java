/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.common;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author tuhalang
 */
public class GameConfig {
    
    private static final String SOURCE_PROPERTIES = "etc/application.properties";
    private static final Logger LOGGER = Logger.getLogger(GameConfig.class);
    
    public static Integer SERVER_PORT;
    public static Integer NUM_OF_THREAD;
    public static Integer FPS;
    public static String LOGIN_CODE;
    public static String REGISTER_CODE;
    public static String JOIN_ROOM_CODE;
    public static String CONTROL_GAME_CODE;
    public static Boolean AUTO_ASSIGN_ROOM;
    public static String MASTER_NAME;
    public static String IP_ADDRESS;
    
    
    static{
        FileReader reader = null;
        Properties p = null;
        try {
            LOGGER.info("LOADING CONFIG ...");
            
            reader = new FileReader(SOURCE_PROPERTIES);
            p = new Properties();
            p.load(reader);
            SERVER_PORT = Integer.parseInt(p.getProperty("SERVER_PORT", "20"));
            NUM_OF_THREAD = Integer.parseInt(p.getProperty("NUM_OF_THREAD", "8888"));
            FPS = Integer.parseInt(p.getProperty("FPS", "30"));
            LOGIN_CODE = p.getProperty("LOGIN_CODE", "1");
            REGISTER_CODE = p.getProperty("REGISTER_CODE", "2");
            JOIN_ROOM_CODE = p.getProperty("JOIN_ROOM_CODE", "3");
            CONTROL_GAME_CODE = p.getProperty("CONTROL_GAME_CODE", "4");
            AUTO_ASSIGN_ROOM = Boolean.valueOf(p.getProperty("AUTO_ASSIGN_ROOM", "true"));
            MASTER_NAME = p.getProperty("MASTER_NAME", "mymaster");
            IP_ADDRESS = p.getProperty("IP_ADDRESS");
            
            LOGGER.info("FINISH LOAD CONFIG !");
        } catch (IOException e) {
            LOGGER.error(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.error(e);
                }
            }
        }
    }
}
