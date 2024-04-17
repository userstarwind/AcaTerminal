package com.example.acaterminal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messageList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemLongClick(Message message);
    }

    public MessageAdapter(List<Message> messageList, OnItemClickListener listener) {
        this.messageList = messageList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.left_text_message_row, parent, false);
                return new LeftTextMessageViewHolder(view, listener);
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.right_text_message_row, parent, false);
                return new RightTextMessageViewHolder(view, listener);
            case 3:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.left_image_message_row, parent, false);
                return new LeftImageMessageViewHolder(view, listener);
            case 4:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.right_image_message_row, parent, false);
                return new RightImageMessageViewHolder(view, listener);
            case 5:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.divider_row, parent, false);
                return new DividerMessageViewHolder(view, listener);
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }
    }

    public abstract class MessageViewHolder extends RecyclerView.ViewHolder {
        public MessageViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bind(Message message);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        String type = message.getMessageType();
        String direction = message.getMessageDirection();
        if ("Text".equals(type) && "Left".equals(direction)) {
            return 1;
        } else if ("Text".equals(type) && "Right".equals(direction)) {
            return 2;
        } else if ("Image".equals(type) && "Left".equals(direction)) {
            return 3;
        } else if ("Image".equals(type) && "Right".equals(direction)) {
            return 4;
        } else if ("Text".equals(type) && "Divider".equals(direction)) {
            return 5;
        } else {
            return -1;
        }
    }

    public class LeftTextMessageViewHolder extends MessageViewHolder {
        private TextView leftTextMessageContentTextView;
        private TextView leftTextMessageNameTextView;
        private ImageView leftTextMessageImageView;

        public LeftTextMessageViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            leftTextMessageContentTextView = itemView.findViewById(R.id.left_text_message_content_textview);
            leftTextMessageNameTextView = itemView.findViewById(R.id.left_text_message_name_textview);
            leftTextMessageImageView = itemView.findViewById(R.id.left_text_message_imageview);

            itemView.setOnLongClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemLongClick(messageList.get(getAdapterPosition()));
                }
                return true;
            });
        }

        public void bind(Message message) {
            Context context = itemView.getContext();
            int drawableId = context.getResources().getIdentifier(message.getAvatar(), "drawable", context.getPackageName());
            if (drawableId != 0) {
                Drawable drawable = ContextCompat.getDrawable(context, drawableId);
                Glide.with(context)
                        .load(drawable)
                        .apply(RequestOptions.circleCropTransform())
                        .into(leftTextMessageImageView);
            } else {
                leftTextMessageImageView.setImageResource(R.drawable.avatar_acacia);
            }
            leftTextMessageNameTextView.setText(message.getName());
            leftTextMessageContentTextView.setText(message.getContent());
        }
    }

    public class RightTextMessageViewHolder extends MessageViewHolder {
        private TextView rightTextMessageContentTextView;
        private TextView rightTextMessageNameTextView;
        private ImageView rightTextMessageImageView;

        public RightTextMessageViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            rightTextMessageContentTextView = itemView.findViewById(R.id.right_text_message_content_textview);
            rightTextMessageNameTextView = itemView.findViewById(R.id.right_text_message_name_textview);
            rightTextMessageImageView = itemView.findViewById(R.id.right_text_message_imageview);

            itemView.setOnLongClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemLongClick(messageList.get(getAdapterPosition()));
                }
                return true;
            });
        }

        public void bind(Message message) {
            Context context = itemView.getContext();
            int drawableId = context.getResources().getIdentifier(message.getAvatar(), "drawable", context.getPackageName());
            if (drawableId != 0) {
                Drawable drawable = ContextCompat.getDrawable(context, drawableId);
                Glide.with(context)
                        .load(drawable)
                        .apply(RequestOptions.circleCropTransform())
                        .into(rightTextMessageImageView);
            } else {
                rightTextMessageImageView.setImageResource(R.drawable.avatar_acacia);
            }
            rightTextMessageNameTextView.setText(message.getName());
            rightTextMessageContentTextView.setText(message.getContent());
        }
    }

    public static Bitmap decodeBase64ToBitmap(String base64String) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
    public class LeftImageMessageViewHolder extends MessageViewHolder {
        private ImageView leftImageMessageContentImageView;
        private TextView leftImageMessageNameTextView;
        private ImageView leftImageMessageImageView;

        public LeftImageMessageViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            leftImageMessageContentImageView= itemView.findViewById(R.id.left_image_message_content_imageview);
            leftImageMessageNameTextView = itemView.findViewById(R.id.left_image_message_name_textview);
            leftImageMessageImageView = itemView.findViewById(R.id.left_image_message_imageview);

            itemView.setOnLongClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemLongClick(messageList.get(getAdapterPosition()));
                }
                return true;
            });
        }

        public void bind(Message message) {
            Context context = itemView.getContext();
            int drawableAvatarId = context.getResources().getIdentifier(message.getAvatar(), "drawable", context.getPackageName());
            if (drawableAvatarId != 0) {
                Drawable drawable = ContextCompat.getDrawable(context, drawableAvatarId);
                Glide.with(context)
                        .load(drawable)
                        .apply(RequestOptions.circleCropTransform())
                        .into(leftImageMessageImageView);
            } else {
                leftImageMessageImageView.setImageResource(R.drawable.avatar_acacia);
            }
            Bitmap bitmap = decodeBase64ToBitmap(message.getContent());
            leftImageMessageContentImageView.setImageBitmap(bitmap);
            leftImageMessageNameTextView.setText(message.getName());
        }
    }
    public class RightImageMessageViewHolder extends MessageViewHolder {
        private ImageView rightImageMessageContentImageView;
        private TextView rightImageMessageNameTextView;
        private ImageView rightImageMessageImageView;

        public RightImageMessageViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            rightImageMessageContentImageView= itemView.findViewById(R.id.right_image_message_content_imageview);
            rightImageMessageNameTextView = itemView.findViewById(R.id.right_image_message_name_textview);
            rightImageMessageImageView = itemView.findViewById(R.id.right_image_message_imageview);

            itemView.setOnLongClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemLongClick(messageList.get(getAdapterPosition()));
                }
                return true;
            });
        }

        public void bind(Message message) {
            Context context = itemView.getContext();
            int drawableAvatarId = context.getResources().getIdentifier(message.getAvatar(), "drawable", context.getPackageName());
            if (drawableAvatarId != 0) {
                Drawable drawable = ContextCompat.getDrawable(context, drawableAvatarId);
                Glide.with(context)
                        .load(drawable)
                        .apply(RequestOptions.circleCropTransform())
                        .into(rightImageMessageImageView);
            } else {
                rightImageMessageImageView.setImageResource(R.drawable.avatar_acacia);
            }
            Bitmap bitmap = decodeBase64ToBitmap(message.getContent());
            rightImageMessageContentImageView.setImageBitmap(bitmap);
            rightImageMessageNameTextView.setText(message.getName());
        }
    }

    public class DividerMessageViewHolder extends MessageViewHolder {
        private TextView dividerTextMessageContentTextView;


        public DividerMessageViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            dividerTextMessageContentTextView = itemView.findViewById(R.id.divider_textview);

            itemView.setOnLongClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemLongClick(messageList.get(getAdapterPosition()));
                }
                return true;
            });
        }

        public void bind(Message message) {
            dividerTextMessageContentTextView.setText(message.getContent());
        }
    }
}

