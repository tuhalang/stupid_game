/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.client.ui;

/**
 *
 * @author nhanlebka
 */
public class Config {
    // login, register
    public static final int LOGIN_SUCCESS = 3;
    public static final int LOGIN_FAIL = 2;
    public static final int REGISTER_SUCCESS = -1;
    public static final int REGISTER_FAIL = -2;
    
    // after login
    public static final int WAIT_PLAY = 4;
    public static final int PLAY = 5;
    
    public static final String RES_LOGIN_SUCCESS = "000";
    public static final String RES_LOGIN_FAILED = "001";
    public static final String RES_REGISTER_SUCCESS = "010";
    public static final String RES_REGISTER_FAILED = "011";
    public static final String RES_JOIN_ROOM_SUCCESS = "020";
    public static final String RES_JOIN_ROOM_FAILED = "021";
    public static final String RES_START_GAME_SUCCESS = "030";
    public static final String RES_START_GAME_FAILED = "031";
    public static final String RES_FINISH_GAME = "040";
}
