/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.model;

/**
 *
 * @author tuhalang
 */
public enum Direction {
    LEFT(0),
    RIGHT(1),
    UP(2),
    DOWN(3);

    private int key;
    
    private Direction(int key) {
        this.key = key;
    }
    
    public int getKey(){
        return this.key;
    }
    
}
