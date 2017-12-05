package com.yueding.chatrobot.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yueding.chatrobot.R;
import com.yueding.chatrobot.chat.ChatItem;

import java.util.List;

/**
 * Created by yueding on 2017/12/5.
 */

public class VoiceChatAdapter extends BaseMultiItemQuickAdapter <ChatItem, BaseViewHolder>{

    public VoiceChatAdapter(List<ChatItem> data) {
        super(data);
        addItemType(ChatItem.LEFT, R.layout.left_text);
        addItemType(ChatItem.RIGHT, R.layout.right_text);
    }

    @Override
    protected void convert(BaseViewHolder helper, ChatItem item) {
        switch (helper.getItemViewType()) {
            case ChatItem.LEFT:
                helper.setText(R.id.leftText, item.getMessage());
                break;
            case ChatItem.RIGHT:
                helper.setText(R.id.rightText, item.getMessage());
                break;
        }
    }
}
