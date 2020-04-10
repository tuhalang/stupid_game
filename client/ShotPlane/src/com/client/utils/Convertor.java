/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.client.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author tuhalang
 */
public class Convertor {
    
    public static String objectToJson(Object object){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static  <T> T jsonToObject(String json, Class<T> t){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, t);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
