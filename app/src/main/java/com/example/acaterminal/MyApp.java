package com.example.acaterminal;

import android.app.Application;

public class MyApp extends Application {
    private DatabaseHelper db;

    private Character currentCharacter;
    @Override
    public void onCreate() {
        super.onCreate();
        db = DatabaseHelper.getInstance(this);
        currentCharacter=new Character("@drawable/avatar_me","分析员");
    }

    public void setCurrentCharacter(Character currentCharacter) {
        this.currentCharacter = currentCharacter;
    }

    public Character getCurrentCharacter() {
        return currentCharacter;
    }

    public DatabaseHelper getDb() {
        return db;
    }
}
