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

public class ChatRoom extends AppCompatActivity {

    private Button btn_send_msg;
    private EditText input_msg;
    private String temp_key;
    //переменные для аутентификации
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private RecyclerView mRecyclerView;
    private DatabaseReference root;
    private DatabaseReference mDatabaseUsers;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<Massege, MassegeViewHolder> mFirebaseAdapter;
    private String userNewImage, userNewName, chatTitle;
    private String currentChannel;
    private String defaultImage= "https://firebasestorage.googleapis.com/v0/b/chatapplication-7aeb9.appspot.com/o/Profile_image%2Fimage_user.png?alt=media&token=ce12634c-b618-44e5-890b-67fd9211fe8e";
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        //2201
        //mAuth = FirebaseAuth.getInstance();
        //выскакивает ошибка
        //final String newUser = mAuth.getCurrentUser().getUid();

        btn_send_msg = (Button)findViewById(R.id.SendChatMsg);
        input_msg = (EditText)findViewById(R.id.editTextMsg);

        //2201

        //mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(newUser);

        mRecyclerView = (RecyclerView)findViewById(R.id.rv);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Storage reference
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //2201
        final String user_name = getIntent().getExtras().getString("user_name");

        final String chat_name = getIntent().getExtras().getString("chat_name");
        final String chat_title = getIntent().getExtras().getString("chat_title");
        currentChannel = getIntent().getExtras().getString("currentChannel");


        //инициализация linetLayoutManeger
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        setTitle(chat_title);

        //090117
        //root = FirebaseDatabase.getInstance().getReference().child("Chat").child(chat_name);
        root = FirebaseDatabase.getInstance().getReference().child("Chat").child(currentChannel).child(chat_name);

        // получаем картинку пользователя
        //2201
        // выскакивает ошибка
//        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                //15.01
//                //проверка есть ли у пользователя аватар
//                    if(dataSnapshot.child("image").getValue() == null){
//                        userNewImage = defaultImage;
//                    } else {
//                        userNewImage = dataSnapshot.child("image").getValue().toString();
//                    }
//
//                userNewName = dataSnapshot.child("name").getValue().toString();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Massege, MassegeViewHolder>(
                Massege.class,
                R.layout.massege_row,
                MassegeViewHolder.class,
                root
        ) {
            @Override
            protected void populateViewHolder(MassegeViewHolder viewHolder, Massege model, int position) {

                viewHolder.setName(model.getName());
                viewHolder.setMsg(model.getMsg());
                viewHolder.setImage(getApplicationContext(), model.getImage());
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

                //2201
                //авторизация если пользователь жмет кнопку отправить
                mAuth = FirebaseAuth.getInstance();
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

                Map<String,Object>map = new HashMap<String, Object>();
                temp_key = root.push().getKey();
                root.updateChildren(map);

                DatabaseReference message_root = root.child(temp_key);
                Map<String, Object> map2 = new HashMap<String, Object>();
                map2.put("name",userNewName);
                map2.put("msg",input_msg.getText().toString());
                map2.put("image", userNewImage);

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

        public void setName(String name){
            TextView massege_name = (TextView)nView.findViewById(R.id.massege_name);
            massege_name.setText(name);
        }

        public void setMsg(String msg){
            TextView massege_msg = (TextView)nView.findViewById(R.id.massege_text);
            massege_msg.setText(msg);
        }

        public void setImage(Context ctx, String image){
            ImageView post_image = (ImageView)nView.findViewById(R.id.userImage);
            Picasso.with(ctx).load(image).into(post_image);
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

            if(!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password)){

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