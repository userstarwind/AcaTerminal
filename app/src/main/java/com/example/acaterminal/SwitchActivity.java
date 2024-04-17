package com.example.acaterminal;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SwitchActivity extends AppCompatActivity {
    private JSONArray characterArray;
    private List<Character> characterList;
    private List<Character> filteredList;
    private EditText searchEditText;
    private RecyclerView recyclerView;
    private CharacterAdapter characterAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch);
        Toolbar toolbar = findViewById(R.id.switch_toolbar);
        MyApp myApp = (MyApp) getApplicationContext();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchEditText = findViewById(R.id.search_name_edittext);
        recyclerView = findViewById(R.id.character_recyclerview);

        characterList = new ArrayList<>();
        filteredList = new ArrayList<>();

        loadCharacters();
        setupSearchFilter();

        characterAdapter = new CharacterAdapter(filteredList, character -> {
            myApp.setCurrentCharacter(character);
            finish();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(characterAdapter);

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadCharacters() {
        try {
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open("characters.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer, "UTF-8");
            characterArray = new JSONArray(json);
            for (int i = 0; i < characterArray.length(); i++) {
                JSONObject jsonObject = characterArray.getJSONObject(i);
                Character character = new Character(
                        jsonObject.getString("avatar"),
                        jsonObject.getString("name")
                );
                characterList.add(character);
            }
            filteredList.addAll(characterList);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupSearchFilter() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() > 0) {
                    filteredList.clear();
                    for (Character character : characterList) {
                        if (character.getName().contains(s.toString())) {
                            filteredList.add(character);
                        }
                    }
                } else {
                    filteredList.clear();
                    filteredList.addAll(characterList);
                }
                characterAdapter.notifyDataSetChanged();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    filteredList.clear();
                    for (Character character : characterList) {
                        if (character.getName().contains(s.toString())) {
                            filteredList.add(character);
                        }
                    }
                } else {
                    filteredList.clear();
                    filteredList.addAll(characterList);
                }
                characterAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    filteredList.clear();
                    for (Character character : characterList) {
                        if (character.getName().contains(s.toString())) {
                            filteredList.add(character);
                        }
                    }
                } else {
                    filteredList.clear();
                    filteredList.addAll(characterList);
                }
                characterAdapter.notifyDataSetChanged();
            }
        });
    }
}
