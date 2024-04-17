package com.example.acaterminal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditChatActivity extends AppCompatActivity {
    private Character currentCharacter;
    private List<Message> messageList;
    private EditText messageEditText;
    private int currentChatId;
    private MyApp myApp;
    private DatabaseHelper db;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    private String emoji;

    private RecyclerView messageRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_chat);
        Intent intent = getIntent();
        currentChatId = intent.getIntExtra("CHAT_ID", -1);
        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        myApp = (MyApp) getApplicationContext();
        db = myApp.getDb();
        currentCharacter = myApp.getCurrentCharacter();
        messageList = db.getAllMessagesByChatId(currentChatId);
        messageEditText = findViewById(R.id.message_edittext);
        messageRecyclerView= findViewById(R.id.message_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        messageRecyclerView.setLayoutManager(layoutManager);
        Collections.sort(messageList, Comparator.comparing(Message::getDate));
        emoji = "";
        messageRecyclerView.setAdapter(new MessageAdapter(messageList, new MessageAdapter.OnItemClickListener() {
            @Override
            public void onItemLongClick(Message message) {
                new AlertDialog.Builder(EditChatActivity.this)
                        .setTitle("Confirm Delete")
                        .setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.deleteMessage(message.getId());
                                messageList.remove(message);
                                RecyclerView recyclerView = findViewById(R.id.message_recyclerview);
                                RecyclerView.Adapter adapter = recyclerView.getAdapter();
                                if (adapter != null) {
                                    adapter.notifyDataSetChanged();
                                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        }));
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (result.getData() != null && result.getData().getData() != null) {
                            Uri sourceUri = result.getData().getData();
                            startCrop(sourceUri);
                        }
                    }
                });
    }

    private String convertToBase64(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] bytes = getBytes(inputStream);
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (IOException e) {
            Log.e("Error", "Failed to convert image to Base64", e);
            return null;
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteStream.write(buffer, 0, len);
        }
        return byteStream.toByteArray();
    }

    private void startCrop(Uri sourceUri) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "CROPPED_" + timeStamp + ".jpg";
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), fileName));

        UCrop uCrop = UCrop.of(sourceUri, destinationUri);
        uCrop.withAspectRatio(1, 1);
        uCrop.withMaxResultSize(1000, 1000);
        uCrop.withOptions(getUCropOptions());
        uCrop.start(this);
    }

    private UCrop.Options getUCropOptions() {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(80);
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(false);
        return options;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_switch) {
            Intent intent = new Intent(EditChatActivity.this, SwitchActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_back) {
            Intent intent = new Intent(EditChatActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void sendMessage(View view) {
        String messageContent = messageEditText.getText().toString().trim();
        if (messageContent.isEmpty()) {
            Toast.makeText(this, "Please enter a message to send", Toast.LENGTH_SHORT).show();
        } else {
            String direction;
            currentCharacter = myApp.getCurrentCharacter();
            if (currentCharacter.getName().equals("分析员")) {
                direction = "Right";
            } else if (currentCharacter.getName().equals("分割线")) {
                direction = "Divider";
            } else {
                direction = "Left";
            }
            Message message = db.insertMessage(currentChatId, "Text", direction, messageContent, currentCharacter.getAvatar(), currentCharacter.getName());
            messageList.add(message);
            messageEditText.setText("");
            RecyclerView recyclerView = findViewById(R.id.message_recyclerview);
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }
        }
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            emoji = convertToBase64(resultUri);
            String direction;
            currentCharacter = myApp.getCurrentCharacter();
            if (currentCharacter.getName().equals("分析员")) {
                direction = "Right";
            } else if (currentCharacter.getName().equals("分割线")) {
                Toast.makeText(this, "Divider can not send image", Toast.LENGTH_SHORT).show();
                return;
            } else {
                direction = "Left";
            }
            Message message = db.insertMessage(currentChatId, "Image", direction, emoji, currentCharacter.getAvatar(), currentCharacter.getName());
            messageList.add(message);
            messageEditText.setText("");
            messageRecyclerView = findViewById(R.id.message_recyclerview);
            RecyclerView.Adapter adapter = messageRecyclerView.getAdapter();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
                messageRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Log.e("EditChatActivity", "Crop error: " + cropError);
        }
    }

    public void sendImage(View view) {
        pickFromGallery();
    }
}