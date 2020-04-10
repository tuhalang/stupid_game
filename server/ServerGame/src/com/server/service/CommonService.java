/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.service;

import java.net.Socket;

/**
 *
 * @author tuhalang
 */
public interface CommonService {
    void handel(Socket socket, String message);
}
