/*
 * Created by Belal Shakatreh on 26/11/18 12:51
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 26/11/18 12:51
 * Contact info: email: shakatreh.belal@gmail.com, mobile: 00962799155039
 */

package com.ormdb.example.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ormdb.OrmDb;
import com.ormdb.example.R;
import com.ormdb.example.beans.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by b.shakatreh on 26,November,2018
 * email: shakatreh.belal@gmail.com
 * phone: +962799155039
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private List<User> users;
    private Context context;
    private LayoutInflater inflater;

    public UsersAdapter(Context context) {
        this.users = new ArrayList<>();
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        try {
            final User user = users.get(position);
            holder.tvName.setText(user.getName());
            holder.tvSalary.setText(user.getSalary() + "");
            holder.viewRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeUser(position, user.getId());
                }
            });
        } catch (Exception ex) {
            Log.d("", "");
        }

    }

    private void removeUser(final int position, final int userId) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (OrmDb.getInstance(context).remove(User.class, "id=" + userId) > 0) {
                            users.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Removed successfully", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure to remove this user?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSalary;
        View viewRow;

        public ViewHolder(View v) {
            super(v);
            viewRow = v;
            tvName = v.findViewById(R.id.tvName);
            tvSalary = v.findViewById(R.id.tvSalary);
        }
    }
}
