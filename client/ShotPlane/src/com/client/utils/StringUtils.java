/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.client.utils;

/**
 *
 * @author tuhalang
 */
public class StringUtils {
    
    public static Boolean isEmpty(String s){
        return s==null || s.equals("") || s.trim().equals("");
    }
}
