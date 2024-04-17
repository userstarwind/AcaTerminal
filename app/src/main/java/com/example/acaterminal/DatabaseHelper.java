package com.example.acaterminal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper instance;
    private static final String DATABASE_NAME = "chatApp.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_CHATS = "chats";
    public static final String TABLE_MESSAGES = "messages";
    public static final String CHAT_ID = "id";
    public static final String CHAT_DATE = "date";

    public static final String CHAT_TITLE = "title";

    public static final String MESSAGE_ID = "id";
    public static final String MESSAGE_CHAT_ID = "chat_id";
    public static final String MESSAGE_TYPE = "type";
    public static final String MESSAGE_CONTENT = "content";
    public static final String MESSAGE_AVATAR = "avatar";
    public static final String MESSAGE_DATE = "date";
    public static final String MESSAGE_DIRECTION = "direction";
    public static final String MESSAGE_NAME = "name";


    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private static final String CREATE_TABLE_CHATS = "CREATE TABLE "
            + TABLE_CHATS + "(" + CHAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + CHAT_TITLE + " TEXT,"
            + CHAT_DATE + " TEXT);";


    private static final String CREATE_TABLE_MESSAGES = "CREATE TABLE "
            + TABLE_MESSAGES + "(" + MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + MESSAGE_CHAT_ID + " INTEGER,"
            + MESSAGE_TYPE + " TEXT,"
            + MESSAGE_DATE + " TEXT,"
            + MESSAGE_DIRECTION + " TEXT,"
            + MESSAGE_CONTENT + " TEXT,"
            + MESSAGE_AVATAR + " TEXT,"
            + MESSAGE_NAME + " TEXT,"
            + "FOREIGN KEY(" + MESSAGE_CHAT_ID + ") REFERENCES " + TABLE_CHATS + "(" + CHAT_ID + "));";

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CHATS);
        db.execSQL(CREATE_TABLE_MESSAGES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHATS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        onCreate(db);
    }


    public Chat addChat(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String currentDate = LocalDate.now().toString();
        values.put(CHAT_TITLE, title);
        values.put(CHAT_DATE, currentDate);

        long chatId = -1;
        try {
            chatId = db.insert(TABLE_CHATS, null, values);
            if (chatId == -1) {
                return null;
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
            return null;
        } finally {
            db.close();
        }
        return new Chat((int) chatId, title, currentDate);
    }


    public List<Chat> getAllChats() {
        List<Chat> chats = new ArrayList<>();

        try (SQLiteDatabase db = this.getReadableDatabase(); Cursor cursor = db.query(TABLE_CHATS, new String[]{CHAT_ID, CHAT_TITLE, CHAT_DATE}, null, null, null, null, null)) {
            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(CHAT_ID);
                int titleIndex = cursor.getColumnIndex(CHAT_TITLE);
                int dateIndex = cursor.getColumnIndex(CHAT_DATE);

                if (idIndex == -1 || titleIndex == -1 || dateIndex == -1) {
                    throw new IllegalArgumentException("Column not found");
                }

                do {
                    int id = cursor.getInt(idIndex);
                    String title = cursor.getString(titleIndex);
                    String date = cursor.getString(dateIndex);
                    chats.add(new Chat(id, title, date));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return chats;
    }

    public boolean deleteChat(int chatId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = CHAT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(chatId)};

        int deletedRows = 0;
        try {
            deletedRows = db.delete(TABLE_CHATS, selection, selectionArgs);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }

        return deletedRows > 0;
    }

    public boolean updateChat(int chatId, String newTitle) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CHAT_TITLE, newTitle);

        String selection = CHAT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(chatId)};

        int updatedRows = 0;
        try {
            updatedRows = db.update(TABLE_CHATS, values, selection, selectionArgs);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }

        return updatedRows > 0;
    }

    public Message insertMessage(int chatId, String type, String direction, String content, String avatar, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String currentDate = sdf.format(new Date());
        values.put(MESSAGE_CHAT_ID, chatId);
        values.put(MESSAGE_TYPE, type);
        values.put(MESSAGE_DATE, currentDate);
        values.put(MESSAGE_DIRECTION, direction);
        values.put(MESSAGE_CONTENT, content);
        values.put(MESSAGE_AVATAR, avatar);
        values.put(MESSAGE_NAME, name);

        long messageId = db.insert(TABLE_MESSAGES, null, values);
        db.close();

        if (messageId == -1) {
            return null;
        }

        if ("Text".equals(type) && "Left".equals(direction)) {
            return new LeftMessage((int) messageId, name, avatar, content, currentDate);
        } else if ("Text".equals(type) && "Right".equals(direction)) {
            return new RightMessage((int) messageId, name, avatar, content, currentDate);
        } else if ("Image".equals(type) && "Left".equals(direction)) {
            return new LeftImageMessage((int) messageId, name, avatar, content, currentDate);
        } else if ("Image".equals(type) && "Right".equals(direction)) {
            return new RightImageMessage((int) messageId, name, avatar, content, currentDate);
        } else if ("Text".equals(type) && "Divider".equals(direction)) {
            return new DividerMessage((int) messageId, content, currentDate);
        } else {
            return null;
        }
    }

    public List<Message> getAllMessagesByChatId(int chatId) {
        List<Message> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM " + TABLE_MESSAGES + " WHERE " + MESSAGE_CHAT_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(chatId)});

            int idIndex = cursor.getColumnIndex(MESSAGE_ID);
            int chatIdIndex = cursor.getColumnIndex(MESSAGE_CHAT_ID);
            int typeIndex = cursor.getColumnIndex(MESSAGE_TYPE);
            int dateIndex = cursor.getColumnIndex(MESSAGE_DATE);
            int directionIndex = cursor.getColumnIndex(MESSAGE_DIRECTION);
            int contentIndex = cursor.getColumnIndex(MESSAGE_CONTENT);
            int avatarIndex = cursor.getColumnIndex(MESSAGE_AVATAR);
            int nameIndex = cursor.getColumnIndex(MESSAGE_NAME);
            if (idIndex == -1 || chatIdIndex == -1 || typeIndex == -1 || dateIndex == -1 ||
                    directionIndex == -1 || contentIndex == -1 || avatarIndex == -1 || nameIndex == -1) {
                throw new IllegalArgumentException("One of the column indices is invalid");
            }

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(idIndex);
                    String type = cursor.getString(typeIndex);
                    String date = cursor.getString(dateIndex);
                    String direction = cursor.getString(directionIndex);
                    String content = cursor.getString(contentIndex);
                    String avatar = cursor.getString(avatarIndex);
                    String name = cursor.getString(nameIndex);
                    Message message = null;
                    if ("Text".equals(type) && "Left".equals(direction)) {
                        message = new LeftMessage(id, name, avatar, content, date);
                    } else if ("Text".equals(type) && "Right".equals(direction)) {
                        message = new RightMessage(id, name, avatar, content, date);
                    } else if ("Image".equals(type) && "Left".equals(direction)) {
                        message = new LeftImageMessage(id, name, avatar, content, date);
                    } else if ("Image".equals(type) && "Right".equals(direction)) {
                        message = new RightImageMessage(id, name, avatar, content, date);
                    } else if ("Text".equals(type) && "Divider".equals(direction)) {
                        message = new DividerMessage(id, content, date);
                    }

                    if (message != null) {
                        messages.add(message);
                    }
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return messages;
    }

    public int deleteMessage(int messageId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            return db.delete(TABLE_MESSAGES, MESSAGE_ID + " = ?", new String[]{String.valueOf(messageId)});
        } finally {
            db.close();
        }
    }

    public int deleteMessagesByChatId(int chatId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            int rowsAffected = db.delete(TABLE_MESSAGES, MESSAGE_CHAT_ID + " = ?", new String[]{String.valueOf(chatId)});
            return rowsAffected;
        } finally {
            db.close();
        }
    }
}

