package com.example.admin.chatapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.admin.chatapplication.R;
import com.example.admin.chatapplication.adapter.ImageAdapter;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends Activity {

        private TextView mSelectText;
        private FirebaseAuth mAuth;
        private FirebaseAuth.AuthStateListener mAuthListener;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            // идентификация пользователя
            mAuth = FirebaseAuth.getInstance();
            mAuthListener = new FirebaseAuth.AuthStateListener(){

                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                    if(firebaseAuth.getCurrentUser() == null){
                        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(loginIntent);
                    }
                }
            };


            mSelectText = (TextView) findViewById(R.id.info);
            GridView gridview = (GridView) findViewById(R.id.gridView1);
            gridview.setAdapter(new ImageAdapter(this));

            gridview.setOnItemClickListener(gridviewOnItemClickListener);
        }

        private GridView.OnItemClickListener gridviewOnItemClickListener = new GridView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position,
                                    long id) {

                    // Sending image id to FullScreenActivity
                    Intent i = new Intent(MainActivity.this,
                            Main2Activity.class);
//                    // passing array index
                    i.putExtra("newId", position);
                    startActivity(i);
                }

        };

    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    }









