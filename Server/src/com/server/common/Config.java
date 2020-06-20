/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.common;

/**
 *
 * @author tuhalang
 */
public class Config {
    public static final String ACCESS_CODE = "0";
    public static final String LOGIN_CODE = "0";
    public static final String REGISTER_CODE = "1";
    public static final String JOIN_ROOM = "2";
    public static final String START_GAME = "3";
    
    
    public static final String LOGIN_SUCCESS = "000";
    public static final String LOGIN_FAILED = "001";
    public static final String REGISTER_SUCCESS = "010";
    public static final String REGISTER_FAILED = "011";
    public static final String JOIN_ROOM_SUCCESS = "020";
    public static final String JOIN_ROOM_FAILED = "021";
    public static final String START_GAME_SUCCESS = "030";
    public static final String START_GAME_FAILED = "031";
    
    
    public static final String CONTROL_CODE = "1";
    public static final String MOVE_CODE = "2";
    
    public static final Boolean AUTO_ASSIGN_ROOM = Boolean.FALSE;
    public static final int MAX_POOL = 30;
    public static final int FREQ_GEN_FLY = 30;
    
    public static final int SIZE_BATCH = 5;
    
    public static final int PORT = 8888;
    
    
}
