package com.ormdb.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnDefinition {
    String name();
    DataType type();
    boolean primary_key();
    boolean auto_increment();
}
