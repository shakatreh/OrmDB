/*
 * Created by Belal Shakatreh on 26/11/18 12:33
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 26/11/18 10:22
 * Contact info: email: shakatreh.belal@gmail.com, mobile: 00962799155039
 */

package com.ormdb.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ormdb.OrmDb;
import com.ormdb.example.adapters.UsersAdapter;
import com.ormdb.example.beans.User;

public class UsersActivity extends AppCompatActivity {

    RecyclerView rvUsers;
    UsersAdapter usersAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        setTitle("Users");
        rvUsers = findViewById(R.id.rvUsers);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        usersAdapter = new UsersAdapter(this);
        rvUsers.setAdapter(usersAdapter);

        fillUsers();
    }

    public void addNewUser(View view) {
        String name = ((EditText) findViewById(R.id.etName)).getText().toString().trim();
        String salary = ((EditText) findViewById(R.id.etSalary)).getText().toString().trim();
        if (name.length() == 0 || salary.length() == 0) {
            Toast.makeText(this, "Fill data correctly!", Toast.LENGTH_SHORT).show();
            return;
        }
        int rows = OrmDb.getInstance(this).addOrUpdate(new User(name, Integer.parseInt(salary)), null);
        if (rows > 0) {
            Toast.makeText(this, "Added successfully", Toast.LENGTH_SHORT).show();
            fillUsers();
        }
    }


    private void fillUsers() {
        usersAdapter.setUsers(OrmDb.getInstance(this).<User>getOrmObj(User.class, null));
    }
}
