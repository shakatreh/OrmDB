/*
 * Created by Belal Shakatreh on 26/11/18 11:07
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 26/11/18 11:06
 * Contact info: email: shakatreh.belal@gmail.com, mobile: 00962799155039
 */

package com.ormdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.ormdb.annotations.ColumnDefinition;
import com.ormdb.annotations.DataType;
import com.ormdb.annotations.TableName;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class OrmDb extends SQLiteOpenHelper {

    private static OrmDb instance = null;
    private static String DATABASE_NAME = "orm_db.db";
    private Context context;
    private OrmEntity ormEntity;

    private OrmDb(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    /**
     * @param context: context of activity or fragment
     * @return returns singleton object from OrmDb
     */
    public static OrmDb getInstance(Context context) {
        if (instance == null)
            instance = new OrmDb(context);
        return instance;
    }

    public void clearDB() {
        context.deleteDatabase(DATABASE_NAME);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }


    /**
     * Add new orm object or update if where condition in not null
     *
     * @param ormClass:       for example: new Employee(5, "Belal")
     * @param whereCondition: for example: "id=1 and name='belal'"
     * @return returns effected rows which inserted or updated
     */
    public int addOrUpdate(OrmEntity ormClass, String whereCondition) {
        try {
            Class object = ormClass.getClass();
            String tableName = createTableIfNotExist(object);

            ContentValues contentValues = new ContentValues();
            for (Field field : object.getDeclaredFields()) {
                if (field.isAnnotationPresent(ColumnDefinition.class)) {
                    try {
                        ColumnDefinition ann = field.getAnnotation(ColumnDefinition.class);
                        String name = ann.name();
                        DataType type = ann.type();
                        boolean isAutoIncrement = ann.auto_increment();
                        field.setAccessible(true);
                        if (isAutoIncrement)
                            continue;
                        switch (type) {
                            case Integer:
                                contentValues.put(name, field.getInt(ormClass));
                                break;
                            case Float:
                                contentValues.put(name, field.getFloat(ormClass));
                                break;
                            case Double:
                                contentValues.put(name, field.getDouble(ormClass));
                                break;
                            case Long:
                                contentValues.put(name, field.getLong(ormClass));
                                break;
                            case String:
                                contentValues.put(name, field.get(ormClass).toString());
                                break;
                            case Image:
                                contentValues.put(name, (byte[]) field.get(ormClass));
                                break;
                        }
                    } catch (Exception ex) {
                        return 0;
                    }
                }
            }

            if (whereCondition == null)
                return (int) getReadableDatabase().insert(tableName, null, contentValues);
            else
                return getReadableDatabase().update(tableName, contentValues, whereCondition, null);
        } catch (Exception ex) {
            return -1;
        }
    }

    /**
     * @param ormClass:       for example: Employee.class
     * @param whereCondition: for example: "id=1 and name='belal'"
     * @return returns list of orm objects, or null if not found data
     */
    @SuppressWarnings("unchecked")
    public <T extends OrmEntity> List<T> getOrmObj(Class ormClass, String whereCondition) {
        try {
            createTableIfNotExist(ormClass);
            List<T> list = new ArrayList<T>();
            String tableName = getTableNameFromClass(ormClass);
            String where = "";
            if (whereCondition != null)
                where = " where " + whereCondition;
            SQLiteDatabase db = getReadableDatabase();
            Cursor c = db.rawQuery("select * from " + tableName + where, null);
            if (c.moveToFirst()) {
                do {
                    int index = 0;
                    T ormObject = (T) ormClass.newInstance();
                    for (Field field : ormObject.getClass().getDeclaredFields()) {
                        if (field.isAnnotationPresent(ColumnDefinition.class)) {
                            ColumnDefinition ann = field.getAnnotation(ColumnDefinition.class);
                            String name = ann.name();
                            DataType type = ann.type();
                            field.setAccessible(true);
                            switch (type) {
                                case Integer:
                                    int valInt = c.getInt(index);
                                    field.setInt(ormObject, valInt);
                                    break;
                                case Float:
                                    float valFloat = c.getFloat(index);
                                    field.setFloat(ormObject, valFloat);
                                    break;
                                case Double:
                                    double valDouble = c.getDouble(index);
                                    field.setDouble(ormObject, valDouble);
                                    break;
                                case Long:
                                    long valLong = c.getLong(index);
                                    field.setLong(ormObject, valLong);
                                    break;
                                case String:
                                    String valString = c.getString(index);
                                    field.set(ormObject, valString);
                                    break;
                                case Image:
                                    byte[] valBlob = c.getBlob(index);
                                    field.set(ormObject, valBlob);
                                    break;
                            }
                            index++;
                        }
                    }
                    list.add(ormObject);

                } while (c.moveToNext());
            }
            c.close();
            db.close();
            if (list.size() == 0)
                return null;
            return list;
        } catch (Exception ex) {
            return null;
        }
    }


    /**
     * @param ormClass:       for example: Employee.class
     * @param whereCondition: for example: "id=1 and name='belal'"
     * @return returns the number of deleted rows
     */
    public int remove(Class ormClass, String whereCondition) {
        try {
            String tableName = createTableIfNotExist(ormClass);
            return getReadableDatabase().delete(tableName, whereCondition, null);
        } catch (Exception ex) {
            return -1;
        }
    }


    private String createTableIfNotExist(Class object) {
        String tableName = getTableNameFromClass(object);
        StringBuilder sqlStatement = new StringBuilder();
        sqlStatement.append("CREATE TABLE IF NOT EXISTS ");
        sqlStatement.append(tableName + " (");

        if (object.getDeclaredFields().length == 0)
            throw new IllegalArgumentException("The " + object.getSimpleName() + " no have a fields annotations; must have at least one!");
        boolean firstColumn = true;
        for (Field field : object.getDeclaredFields()) {
            if (field.isAnnotationPresent(ColumnDefinition.class)) {
                ColumnDefinition ann = field.getAnnotation(ColumnDefinition.class);
                String name = ann.name();
                String type = ann.type().getValue();
                boolean primary_key = ann.primary_key();
                boolean auto_increment = ann.auto_increment();
                if ((primary_key || auto_increment) && ann.type() != DataType.Integer)
                    throw new IllegalArgumentException("The " + field.getName() + " must be integer");
                if (firstColumn)
                    firstColumn = false;
                else
                    sqlStatement.append(", ");
                sqlStatement.append(name + " " + type);
                if (primary_key)
                    sqlStatement.append(" PRIMARY KEY");
                if (auto_increment)
                    sqlStatement.append(" AUTOINCREMENT");
            }
        }
        if (firstColumn)
            new IllegalArgumentException("You should define at least one column instance of @ColumnDefinition");
        sqlStatement.append(")");

        try {
            getReadableDatabase().execSQL(sqlStatement.toString());
        } catch (Exception ex) {
            throw new SQLException(ex.toString());
        }
        return tableName;
    }

    private String getTableNameFromClass(Class object) {
        String tableName;
        Annotation[] annotations = object.getDeclaredAnnotations();
        if (annotations.length == 0)
            throw new IllegalArgumentException("The " + object.getSimpleName() + " must has 'TableName' annotation!");
        Annotation classNameAnnotation = annotations[0];
        if (classNameAnnotation instanceof TableName) {
            TableName tableNameObj = (TableName) classNameAnnotation;
            tableName = tableNameObj.value();
        } else
            throw new IllegalArgumentException("The annotation for the " + object.getSimpleName() + " must be instance of @TableName!");

        return tableName;
    }

}
