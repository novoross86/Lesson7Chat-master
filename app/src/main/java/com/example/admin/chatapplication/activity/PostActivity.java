package com.example.admin.chatapplication.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.admin.chatapplication.R;
import com.firebase.client.core.Context;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;

public class PostActivity extends AppCompatActivity{

    private EditText mPostTitle;
    private EditText mPostDesc;
    private Spinner mPostChannel;
    private DatabaseReference mDatabase;
    private DatabaseReference chDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUer;
    private DatabaseReference mDatabaseUser;
    private StorageReference mStorage;
    private ImageButton mSelectImage;
    private static final int GALLERY_REQUEST = 1;
    private Uri mImageUri = null;
    private ProgressDialog mProgress;
    private String currentChannel;  //переменная хранит канал из которого пользователь перешел

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mAuth = FirebaseAuth.getInstance();

        mCurrentUer = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("post");
        chDatabase = FirebaseDatabase.getInstance().getReference().child("comments");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(mCurrentUer.getUid());

        mPostTitle = (EditText)findViewById(R.id.mPostTitle);
       // mPostDesc = (EditText)findViewById(R.id.mPostDesc);
        mPostChannel = (Spinner)findViewById(R.id.mSp);
        //mSubmitBtn = (Button)findViewById(R.id.mBtn);
        mSelectImage = (ImageButton)findViewById(R.id.imageButton1);
        mStorage = FirebaseStorage.getInstance().getReference();

        //получение данных из какой активити мы пришли
        Intent intentActivity = getIntent();
        currentChannel = intentActivity.getExtras().getString("currentChannel");


        mProgress = new ProgressDialog(this);

        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);
            }
        });

    }

    private void startPosting(){

        mProgress.setMessage("Posting ...");
        mProgress.show();

        final String title_val = mPostTitle.getText().toString().trim();
        final String channel_val = mPostChannel.getSelectedItem().toString().trim();


//        if(!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(channel_val)){
//
//            //загрузка нового файла с изображением
//            final StorageReference filepath = mStorage.child("Blog_Image").child(mImageUri.getLastPathSegment());
//            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();
//                    //090117
//                    final DatabaseReference newPost = mDatabase.child(currentChannel).push();
//                    // получение идентификатора пользователя
//                    final String newName = mCurrentUer.getUid();
//                    // получение уникальной строки для названия чата
//                    final String chatName = channel_val + title_val + newName;
//                    // устанавливаем название уникальной строки
//                    //090117
//                    String chatKey = newPost.getKey();
//                    final DatabaseReference newChat = chDatabase.child(currentChannel).child(chatKey).push();
//
//                    mDatabaseUser.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//
//                            final String user_name = dataSnapshot.child("name").getValue().toString();
//                            //создание ид в чатах
//                            newChat.child("msg").setValue(title_val);
//                            newChat.child("name").setValue(user_name);
//                            newChat.child("image").setValue(dataSnapshot.child("image").getValue());
//
//                            newPost.child("title").setValue(title_val);
//                            //2101
//                            //newPost.child("channel").setValue(currentChannel);
//
//                            newPost.child("uid").setValue(mCurrentUer.getUid());
//                            newPost.child("username").setValue(dataSnapshot.child("name").getValue());
//                            newPost.child("chat_id").setValue(chatName);
//                            newPost.child("image").setValue(downloadUrl.toString());
//
//                            //получение даты и времени
//                            long date = System.currentTimeMillis();
//                            SimpleDateFormat sdf = new SimpleDateFormat("MMM MM dd, yyyy h:mm a");
//                            String dateString = sdf.format(date);
//                            newPost.child("postDate").setValue(dateString);
//
//                            mProgress.dismiss();
//
//                            Intent chatRoomIntent = new Intent(PostActivity.this, ChatRoom.class);
//                            chatRoomIntent.putExtra("user_name", user_name);
//                            chatRoomIntent.putExtra("chat_name", chatName);
//                            chatRoomIntent.putExtra("chat_title", title_val);
//                            chatRoomIntent.putExtra("currentChannel", currentChannel);
//                            startActivity(chatRoomIntent);
//
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//
//                }
//            });
//
//        }

        //else
            if(!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(channel_val)) {

            //090117
            final DatabaseReference newPost = mDatabase.child(currentChannel).push();
            // получение идентификатора пользователя
            final String newName = mCurrentUer.getUid();
            // получение уникальной строки для названия чата
            //final String chatName = channel_val + title_val + newName;
            final String chatName = title_val + newName;
            // устанавливаем название уникальной строки
            //090117
            final DatabaseReference newChat = chDatabase.child(currentChannel).child(chatName).push();

            mDatabaseUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    final String user_name = dataSnapshot.child("name").getValue().toString();
                    //создание ид в чатах
                    newChat.child("txt").setValue(title_val);
                    newChat.child("t_profile_fullname").setValue(user_name);
                    newChat.child("t_profile_photo_50").setValue(dataSnapshot.child("image").getValue());
                    //2101
                    //newPost.child("channel").setValue(currentChannel);
                    newPost.child("comments").setValue("1");
                    newPost.child("groups_name").setValue(dataSnapshot.child("name").getValue());
                    newPost.child("groups_photo_50").setValue(dataSnapshot.child("image").getValue());
                    newPost.child("likes").setValue("0");
                    newPost.child("txt").setValue(title_val);
                    //получение даты и времени
                    long date = System.currentTimeMillis();
                    SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
                    String dateString = sdf.format(date);
                    newPost.child("postDate").setValue(dateString);

                    mProgress.dismiss();

                    Intent chatRoomIntent = new Intent(PostActivity.this, ChatRoom.class);
                    chatRoomIntent.putExtra("user_name", user_name);
                    chatRoomIntent.putExtra("chat_name", chatName);
                    chatRoomIntent.putExtra("chat_title", title_val);
                    chatRoomIntent.putExtra("currentChannel", currentChannel);
                    startActivity(chatRoomIntent);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){

            mImageUri = data.getData();
            mSelectImage.setImageURI(mImageUri);

        }
    }

    //отображение меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.post_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    //обработка нажатий на пункты меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_add){

            startPosting();

        } else if(item.getItemId() == R.id.home){
            Intent i = new Intent(PostActivity.this,
                    Main2Activity.class);
            i.putExtra("currentChannel", currentChannel);
            startActivity(i);
        }
                return super.onOptionsItemSelected(item);
        }


}
