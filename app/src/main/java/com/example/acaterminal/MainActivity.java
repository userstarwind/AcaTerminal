package com.example.acaterminal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Chat> chatList;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyApp myApp = (MyApp) getApplicationContext();
        db = myApp.getDb();
        chatList = db.getAllChats();
        changeBackground();
        RecyclerView recyclerView = findViewById(R.id.chat_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ChatAdapter(chatList, new ChatAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Chat chat) {
                Intent intent=new Intent(MainActivity.this,EditChatActivity.class);
                intent.putExtra("CHAT_ID",chat.getId());
                startActivity(intent);
                finish();
            }

            @Override
            public void onItemLongClick(Chat chat) {
                final CharSequence[] options = {"Rename", "Delete"};

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Choose an option");


                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            renameChat(chat);
                        } else if (which == 1) {
                            deleteChat(chat);
                        }
                    }
                });

                builder.show();
            }
        }));
    }

    private void changeBackground() {
        if (chatList.isEmpty()) {
            findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
            findViewById(R.id.chat_recyclerview).setVisibility(View.GONE);
        } else {
            findViewById(R.id.empty_view).setVisibility(View.GONE);
            findViewById(R.id.chat_recyclerview).setVisibility(View.VISIBLE);
        }
    }

    private void renameChat(Chat chat) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rename chat");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Enter new chat title");
        builder.setView(input);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newTitle = input.getText().toString();
                if (!newTitle.isEmpty()) {
                    chat.setTitle(newTitle);
                    db.updateChat(chat.getId(),newTitle);
                    RecyclerView recyclerView = findViewById(R.id.chat_recyclerview);
                    RecyclerView.Adapter adapter = recyclerView.getAdapter();
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void deleteChat(Chat chat) {
        if (db.deleteChat(chat.getId())) {
            chatList.remove(chat);
            db.deleteMessagesByChatId(chat.getId());
            changeBackground();
            RecyclerView recyclerView = findViewById(R.id.chat_recyclerview);
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void addChat(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create new chat");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Enter chat title");
        builder.setView(input);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = input.getText().toString();
                if (!title.isEmpty()) {
                    Chat chat = db.addChat(title);
                    chatList.add(chat);
                    changeBackground();
                    RecyclerView recyclerView = findViewById(R.id.chat_recyclerview);
                    RecyclerView.Adapter adapter = recyclerView.getAdapter();
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

}