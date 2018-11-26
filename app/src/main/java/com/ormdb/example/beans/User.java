package com.ormdb.example.beans;


import com.ormdb.OrmEntity;
import com.ormdb.annotations.ColumnDefinition;
import com.ormdb.annotations.DataType;
import com.ormdb.annotations.TableName;

@TableName(value = "users")
public class User extends OrmEntity {
    @ColumnDefinition(name = "id", type = DataType.Integer, primary_key = true, auto_increment = true)
    private int id;
    @ColumnDefinition(name = "name", type = DataType.String, primary_key = false, auto_increment = false)
    private String name;
    @ColumnDefinition(name = "salary", type = DataType.Integer, primary_key = false, auto_increment = false)
    private int salary;

    public User() {
    }

    public User(String name, int salary) {
        this.name = name;
        this.salary = salary;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }
}
