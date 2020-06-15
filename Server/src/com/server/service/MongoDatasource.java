/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.service;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

/**
 *
 * @author tuhalang
 */
public class MongoDatasource {
    
    private MongoClient mongo;
    private DB db;
    
    private static MongoDatasource mongoDatasource;
    
    private static final Object MUTEX = new Object();
    
    /**
     * url = "mongodb://localhost:27017" 
     * @param url 
     */
    private MongoDatasource(String url, String dbName){
        mongo = new MongoClient(new MongoClientURI(url));
        db = mongo.getDB(dbName);
    }
    
    public static MongoDatasource getIntance(String url,  String dbName){
        MongoDatasource localRef = mongoDatasource;
        if(localRef == null){
            synchronized(MUTEX){
                localRef = mongoDatasource;
                if(localRef == null){
                    localRef = mongoDatasource = new MongoDatasource(url, dbName);
                }
            }
        }
        return localRef;
    }
    
    public DBCollection getCollection(String name){
        return db.getCollection(name);
    }
}
