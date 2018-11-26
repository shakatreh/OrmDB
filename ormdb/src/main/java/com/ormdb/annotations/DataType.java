package com.ormdb.annotations;

public enum DataType {
    Integer("INTEGER"),
    Float("REAL"),
    Double("REAL"),
    Long("INTEGER"),
    String("TEXT"),
    Image("BLOB");

    private final String name;
    private DataType(String name) {
        this.name = name;
    }
    public String getValue() {
        return name;
    }
}
