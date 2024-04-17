package com.example.acaterminal;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder> {

    private List<Character> characterList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Character character);
    }

    public CharacterAdapter(List<Character> characterList, OnItemClickListener listener) {
        this.characterList = characterList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CharacterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.character_row, parent, false);
        return new CharacterViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull CharacterViewHolder holder, int position) {
        Character character = characterList.get(position);
        holder.bind(character, listener);
    }

    @Override
    public int getItemCount() {
        return characterList.size();
    }

    public static class CharacterViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private TextView name;

        public CharacterViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            avatar = itemView.findViewById(R.id.character_imageview);
            name = itemView.findViewById(R.id.character_textview);
        }

        public void bind(final Character character,final OnItemClickListener listener) {
            Context context = itemView.getContext();
            int drawableId = context.getResources().getIdentifier(character.getAvatar(), "drawable", context.getPackageName());
            if (drawableId != 0) {
                Drawable drawable = ContextCompat.getDrawable(context, drawableId);
                avatar.setImageDrawable(drawable);
            } else {
                avatar.setImageResource(R.drawable.avatar_acacia);
            }

            name.setText(character.getName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(character);
                }
            });
        }
    }
}

