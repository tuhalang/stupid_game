/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.service;

import com.server.common.GameConfig;

/**
 *
 * @author tuhalang
 */
public class HandleCommandFactory {

    public CommonService getService(String commandType) {
        if (GameConfig.LOGIN_CODE.equalsIgnoreCase(commandType)) {
            return new LoginService();
        } else if (GameConfig.REGISTER_CODE.equalsIgnoreCase(commandType)) {
            return new RegisterService();
        }
        return null;
    }
}
