package com.yueding.chatrobot.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yueding.chatrobot.R;
import com.yueding.chatrobot.chat.ChatItem;

import java.util.List;

/**
 * Created by yueding on 2017/11/30.
 */

public class ChatAdapter extends BaseMultiItemQuickAdapter <ChatItem, BaseViewHolder>{

    Context mContext;

    public ChatAdapter(List<ChatItem> data, Context context) {
        super(data);
        mContext = context;
        addItemType(ChatItem.LEFT, R.layout.left_chat_item);
        addItemType(ChatItem.RIGHT, R.layout.right_chat_item);
        addItemType(ChatItem.IMAGE, R.layout.image_chat_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, ChatItem item) {
        switch (helper.getItemViewType()) {
            case ChatItem.LEFT:
                helper.setText(R.id.textTimeL, item.getTime())
                        .setText(R.id.textMessageL, item.getMessage())
                        .setText(R.id.textNameL, item.getName());
                break;
            case ChatItem.RIGHT:
                helper.setText(R.id.textTimeR, item.getTime())
                        .setText(R.id.textMessageR, item.getMessage())
                        .setText(R.id.textNameR, item.getName());
                break;
            case ChatItem.IMAGE:
                ImageView view = helper.getView(R.id.imageMsg);
                helper.setText(R.id.textTimeI, item.getTime())
                        .setText(R.id.textMessageI, item.getMessage())
                        .setText(R.id.textNameI, item.getName());
                Glide.with(mContext).load(Uri.parse(item.getImage())).into(view);
                Log.i("yueding", "convert: " + item.getImage());
                break;
        }
    }
}
