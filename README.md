# OrmDB

OrmDB using OOP concept to access to SQLLite database.

## How to install it

Step 1. Add the JitPack repository to your build file
```sh
allprojects {
    repositories {
    ...
    maven { url 'https://jitpack.io' }
    }
}
```
Step 2. Add the dependency
```sh
dependencies {
    implementation 'com.github.shakatreh:OrmDB:v1.0.0'
}
```


## How to use it
1. Create you custom object or class and it should has these rules:
   - extend from **OrmEntity**
   - should has default constructor
   - implement set and get for all fields
   - add @TableName annotation for class header
   - add @ColumnDefinition annotation for fields header
   
2. You can get, add, update or remove objects as this examples:
   - **get all objects:** List\<CustomObject\> objects = OrmDb.getInstance(context).<CustomObject>getOrmObj(CustomObject.class, null)
   - **get specific objects:** List\<CustomObject\> objects = OrmDb.getInstance(context).<CustomObject>getOrmObj(CustomObject.class, "column_name='some value'")
   - **add or insert object:** OrmDb.getInstance(context).addOrUpdate(new CustomObject(params..), null);
   - **update or replace object:** OrmDb.getInstance(context).addOrUpdate(new CustomObject(params..),  "column_name='some value'");
   - **Remove all objects:** OrmDb.getInstance(context).remove(CustomObject.class,  null);
   - **Remove specific objects:** OrmDb.getInstance(context).remove(CustomObject.class, "column_name='some value'");
   - **clear all database:** OrmDb.getInstance(context).clearDB();


   
        
