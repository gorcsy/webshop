package com.codecool.buyourstuff.dao;

public enum DaoType {
    MEMORY, DATABASE, FILE;

    public static DaoType get(String name) {
        switch(name){
            case "DATABASE": return DATABASE;
            case "MEMORY":return MEMORY;
            case "FILE": return FILE;
        }
        throw new IllegalStateException("No such type");
    }

}
