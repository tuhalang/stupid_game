/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.client.model;

import java.io.Serializable;

/**
 *
 * @author tuhalang
 */
public class MessageCommand implements Serializable{
    
    private String actionCode;
    private String errorCode;
    private Object object;

    public MessageCommand() {
    }
    

    public MessageCommand(String actionCode, String errorCode, Object object) {
        this.actionCode = actionCode;
        this.errorCode = errorCode;
        this.object = object;
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
    
    
}
