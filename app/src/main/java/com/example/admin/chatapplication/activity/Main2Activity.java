package com.example.admin.chatapplication.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.chatapplication.R;
import com.example.admin.chatapplication.entity.Post;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class Main2Activity extends AppCompatActivity {

    private RecyclerView mPostList;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseLike;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String currentChannel = null;
    private Query mQueryCurrentRequest;
    private boolean mProcessLike = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //добавление кнопки назад
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        // идентификация пользователя
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null){
                    Intent loginIntent = new Intent(Main2Activity.this, ChatRoom.LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };

        //получение данных из активити выбора каналов
        Intent intent = getIntent();
        //получение данных из активити каналов
        int position = intent.getExtras().getInt("newId");
        //получение данных при возврате из
        currentChannel = intent.getExtras().getString("currentChannel");

        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Post");

        mDatabaseLike.keepSynced(true);
        mDatabase.keepSynced(true);

        //выбор канала
        if(currentChannel == null) {
            switch (position) {
                case 0:
                    currentChannel = "ОРТ";
                    break;
                case 1:
                    currentChannel = "Россия1";
                    break;
                case 2:
                    currentChannel = "ТНТ";
                    break;
                case 3:
                    currentChannel = "НТВ";
                    break;
                case 4:
                    currentChannel = "СТС";
                    break;
                case 5:
                    currentChannel = "ТВЦ";
                    break;
                case 6:
                    currentChannel = "Рентв";
                    break;
                case 7:
                    currentChannel = "5 канал";
                    break;
                case 8:
                    currentChannel = "Россия 24";
                    break;
                case 9:
                    currentChannel = "ТВ3";
                    break;
                case 10:
                    currentChannel = "Домашний";
                    break;
                case 11:
                    currentChannel = "2x2";
                    break;
                case 12:
                    currentChannel = "Пятница";
                    break;
                case 13:
                    currentChannel = "Звезда";
                    break;
                case 14:
                    currentChannel = "Дисней";
                    break;
                case 15:
                    currentChannel = "Че";
                    break;
                case 16:
                    currentChannel = "Ю";
                    break;
            }
        }

        //090117
        //mQueryCurrentRequest = mDatabase.orderByChild("channel").equalTo(currentChannel);
        mQueryCurrentRequest = mDatabase.child(currentChannel);

        //090117
        //set Title on actionBar
        actionBar.setTitle(currentChannel);

        mPostList = (RecyclerView)findViewById(R.id.post_list);
        mPostList.setHasFixedSize(true);
        mPostList.setLayoutManager(new LinearLayoutManager(this));

        //изменение порядка отображения ленты постоы чтобы последние посты
        //публиковались вверху списка
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mPostList.setLayoutManager(layoutManager);

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<Post, Main2Activity.PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, Main2Activity.PostViewHolder>(

                Post.class,
                R.layout.post_row,
                Main2Activity.PostViewHolder.class,
                mQueryCurrentRequest

        ) {
            @Override
            protected void populateViewHolder(Main2Activity.PostViewHolder viewHolder, final Post model, int position) {

                //получение имени пользователя и ид чата для отправки в следующую активити
                final String newString = model.getChatId();
                final String user_name = model.getUsername();
                final String chat_title = model.getTitle();
                final String post_key = getRef(position).getKey();

                viewHolder.setChannel(model.getChannel());
                viewHolder.setTitle(model.getTitle());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setLikeBtn(post_key);
                viewHolder.setPostDate(model.getPostDate());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent chatRoomIntent = new Intent(Main2Activity.this, ChatRoom.class);
                        chatRoomIntent.putExtra("chat_name", newString);
                        chatRoomIntent.putExtra("user_name", user_name);
                        chatRoomIntent.putExtra("chat_title", chat_title);
                        chatRoomIntent.putExtra("currentChannel", currentChannel);
                        startActivity(chatRoomIntent);

                    }
                });

                // установка слушателя на share
                viewHolder.mSharebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String shareBody = model.getTitle() + ". Приглашаю обсудить на http://uchu24.ru";
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));

                    }
                });

                // установка слушателя на лайк
                viewHolder.mLikebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mProcessLike = true;

                            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(mProcessLike) {

                                        if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser()
                                                .getUid())) {

                                            mDatabaseLike.child(post_key).child(mAuth
                                                    .getCurrentUser()
                                                    .getUid()).removeValue();

                                            mProcessLike = false;

                                        } else {

                                            mDatabaseLike.child(post_key).child(mAuth.getCurrentUser()
                                                    .getUid()).setValue("RandomValue");

                                            mProcessLike = false;

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                    }
                });

            }
        };

        mPostList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class PostViewHolder extends RecyclerView.ViewHolder{

        View mView;
        ImageButton mLikebtn;
        ImageButton mSharebtn;
        DatabaseReference mDatabaseLike;
        FirebaseAuth mAuth;

        public PostViewHolder(View itemView) {

            super(itemView);
            mView = itemView;

            mLikebtn = (ImageButton) mView.findViewById(R.id.likeBtn);
            mSharebtn = (ImageButton) mView.findViewById(R.id.shareBtn);

            mDatabaseLike =FirebaseDatabase.getInstance().getReference().child("Likes");
            mAuth = FirebaseAuth.getInstance();

            mDatabaseLike.keepSynced(true);
        }

        //при нажатии лайка
        public void setLikeBtn(final String post_key){

           mDatabaseLike.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {

                   if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){

                       mLikebtn.setImageResource(R.mipmap.like_red);

                   }else{

                       mLikebtn.setImageResource(R.mipmap.like);

                   }

               }

               @Override
               public void onCancelled(DatabaseError databaseError) {

               }
           });
        }

        public void setChannel(String channel){

            TextView post_channel = (TextView)mView.findViewById(R.id.channel_title);
            post_channel.setText(channel);
        }

        public void setTitle(String title){

            TextView post_title = (TextView)mView.findViewById(R.id.post_title);
            post_title.setText(title);

        }

        public void setImage(Context ctx, String image){
            ImageView post_image = (ImageView)mView.findViewById(R.id.imagePost);
            Picasso.with(ctx).load(image).into(post_image);
        }

        public void setPostDate(String postDate){
            TextView post_date = (TextView)mView.findViewById(R.id.postDate);
            post_date.setText(postDate);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_add){

            startActivity(new Intent(Main2Activity.this, PostActivity.class ));
            Intent newPostIntent = new Intent(Main2Activity.this,
                    PostActivity.class);
            newPostIntent.putExtra("currentChannel", currentChannel);
            startActivity(newPostIntent);

        }

        if(item.getItemId() == R.id.action_logout){

            logout();

        }

        if(item.getItemId() == R.id.action_setup){

            startActivity(new Intent(Main2Activity.this, SetupActivity.class ));

        }

        //обработка нажатия кнопки назад
        switch (item.getItemId()) {
            case android.R.id.home:
                //Write your logic here
                Intent backIntent = new Intent(Main2Activity.this, MainActivity.class);
                startActivity(backIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout(){

        mAuth.signOut();

    }
}
