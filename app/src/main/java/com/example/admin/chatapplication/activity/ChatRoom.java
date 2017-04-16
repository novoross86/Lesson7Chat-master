package com.example.admin.chatapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.chatapplication.R;
import com.example.admin.chatapplication.entity.Massege;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

public class ChatRoom extends AppCompatActivity {

    private Button btn_send_msg;
    private EditText input_msg;
    private String temp_key;

    //переменные для аутентификации
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private RecyclerView mRecyclerView;
    private DatabaseReference root;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<Massege, MassegeViewHolder> mFirebaseAdapter;
    private String userNewImage, userNewName;
    private String currentChannel;
    private String defaultImage= "https://firebasestorage.googleapis.com/v0/b/chatapplication-7aeb9.appspot.com/o/Profile_image%2Fimage_user.png?alt=media&token=ce12634c-b618-44e5-890b-67fd9211fe8e";
    private String сUser;
    private DatabaseReference mCurentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        btn_send_msg = (Button)findViewById(R.id.SendChatMsg);
        input_msg = (EditText)findViewById(R.id.editTextMsg);
        mRecyclerView = (RecyclerView)findViewById(R.id.rv);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //2201
//      final String user_name = getIntent().getExtras().getString("user_name");
        final String chat_name = getIntent().getExtras().getString("chat_name");
        final String chat_title = getIntent().getExtras().getString("chat_title");
        currentChannel = getIntent().getExtras().getString("currentChannel");


        //авторизация если пользователь жмет кнопку отправить
        mAuth = FirebaseAuth.getInstance();

        //получение объекта пользователя если он есть
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //проветка авторизирован ли пользователь
        if (user != null) {

            // получение id пользователя
            сUser = mAuth.getCurrentUser().getUid();

            //получение ссылки на пользователя
            mCurentUser = FirebaseDatabase.getInstance().getReference().child("Users").child(сUser);

            mCurentUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //15.01
                    //проверка есть ли у пользователя аватар
                    if (dataSnapshot.child("image").getValue() == null) {
                        userNewImage = defaultImage;
                    } else {
                        userNewImage = dataSnapshot.child("image").getValue().toString();
                    }

                    userNewName = dataSnapshot.child("name").getValue().toString();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        //инициализация linetLayoutManeger
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        //установка заголовка страницы
        setTitle(chat_title);

        //090117
        root = FirebaseDatabase.getInstance().getReference().child("comments").child(currentChannel).child(chat_name);


        mFirebaseAdapter = new FirebaseRecyclerAdapter<Massege, MassegeViewHolder>(
                Massege.class,
                R.layout.massege_row,
                MassegeViewHolder.class,
                root
        ) {
            @Override
            protected void populateViewHolder(MassegeViewHolder viewHolder, Massege model, int position) {

                viewHolder.setT_profile_fullname(model.getT_profile_fullname());
                viewHolder.setTxt(model.getTxt());
                viewHolder.setT_profile_photo_50(getApplicationContext(), model.getT_profile_photo_50());
            }
        };

        //перемещение на последнюю позицию
        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

                if(lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))){
                    mRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mFirebaseAdapter);

        //отправка сообщения
        btn_send_msg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mAuthListener = new FirebaseAuth.AuthStateListener(){

                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                    if(firebaseAuth.getCurrentUser() == null){
                        Intent loginIntent = new Intent(ChatRoom.this, LoginActivity.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(loginIntent);
                    }
                }
            };

                mAuth.addAuthStateListener(mAuthListener);

                    Map<String, Object> map = new HashMap<String, Object>();
                    temp_key = root.push().getKey();
                    root.updateChildren(map);
                    DatabaseReference message_root = root.child(temp_key);
                    Map<String, Object> map2 = new HashMap<String, Object>();
                    map2.put("t_profile_fullname", userNewName);
                    map2.put("txt", input_msg.getText().toString());
                    map2.put("t_profile_photo_50", userNewImage);

                    message_root.updateChildren(map2);

                    input_msg.setText("");

            }
        });
        mRecyclerView.scrollToPosition(mFirebaseAdapter.getItemCount());
    }

    public static class MassegeViewHolder extends RecyclerView.ViewHolder{

        View nView;

        public  MassegeViewHolder(View itemView){
            super(itemView);
            nView = itemView;
        }

        public void setT_profile_fullname(String t_profile_fullname){
            TextView massege_name = (TextView)nView.findViewById(R.id.massege_name);
            massege_name.setText(t_profile_fullname);
        }

        public void setTxt(String txt){
            TextView massege_msg = (TextView)nView.findViewById(R.id.massege_text);
            massege_msg.setText(txt);
        }

        public void setT_profile_photo_50(Context ctx, String t_profile_photo_50){
            ImageView post_image = (ImageView)nView.findViewById(R.id.userImage);
            Picasso.with(ctx).load(t_profile_photo_50).into(post_image);
        }
    }

    public static class LoginActivity extends AppCompatActivity {

        private EditText mLoginEmailField;
        private EditText mLoginPasswordField;
        private Button mLoginBtn;
        private Button mRegistrationBtn;

        private FirebaseAuth mAuth;
        private DatabaseReference mDatabase;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

            mLoginEmailField = (EditText)findViewById(R.id.loginEmailField);
            mLoginPasswordField = (EditText)findViewById(R.id.loginPasswordField);
            mLoginBtn = (Button)findViewById(R.id.loginBtn);
            mRegistrationBtn = (Button)findViewById(R.id.registrationBtn);

            mLoginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    checkLogin();

                }


            });

            mRegistrationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent regIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                    regIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(regIntent);
                }
            });
        }

        private void checkLogin() {

            String email = mLoginEmailField.getText().toString().trim();
            String password = mLoginPasswordField.getText().toString().trim();

            if(!isEmpty(email)&&!isEmpty(password)){

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            checkUserExist();

                        } else{

                            Toast.makeText(LoginActivity.this, "Error Login", Toast.LENGTH_LONG).show();

                        }

                    }
                });

            }

        }

        private void checkUserExist() {

            final String user_id = mAuth.getCurrentUser().getUid();
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(user_id)){

                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);

                    } else{

                        Toast.makeText(LoginActivity.this, "You need to setup your account", Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    // обработка кнопки назад
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Write your logic here
                Intent i = new Intent(ChatRoom.this,
                        Main2Activity.class);
                i.putExtra("currentChannel", currentChannel);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ChatRoom.this,
                Main2Activity.class);
        i.putExtra("currentChannel", currentChannel);
        startActivity(i);
    }
}