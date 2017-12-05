package com.yueding.chatrobot.activity;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.yueding.chatrobot.R;
import com.yueding.chatrobot.adapter.ChatAdapter;
import com.yueding.chatrobot.chat.ChatItem;
import com.yueding.chatrobot.chat.NewsItem;
import com.yueding.chatrobot.chat.ReceiveMsg;
import com.yueding.chatrobot.chat.RequestMsg;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int TEXT_TYPE = 100000;
    private static final int URL_TYPE = 200000;
    private static final int NEWS_TYPE = 302000;
    private static final int COOKBOOK_TYPE = 308000;

    private RecyclerView chatView;
    private EditText editMessage;
    private ImageView sendButton;
    private List<ChatItem> chatList = new ArrayList<>();
    private ChatItem chatItem;
    private ChatAdapter adapter;
    private ConstraintLayout layout;
    private String name = "yueding";
    private String sendMsgText;
    private String userId = "111";
    private chatInterface chat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        bindView();
        String text = "你好啊！我是小丫，很高兴为你服务。";
        robotMsg(text);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        adapter = new ChatAdapter(chatList, this);
        chatView.setLayoutManager(manager);
        chatView.setAdapter(adapter);
        sendButton.setOnClickListener(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.tuling123.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        chat = retrofit.create(chatInterface.class);
    }




    /**
     * 显示得到的文字聊天内容
     * @param txt
     */
    private void robotMsg(String txt) {
        chatItem = new ChatItem(ChatItem.LEFT);
        chatItem.setName("小丫");
        chatItem.setMessage(txt);
        chatList.add(chatItem);
    }

    private void bindView() {
        chatView = findViewById(R.id.recyclerChat);
        editMessage = findViewById(R.id.messageEdit);
        sendButton = findViewById(R.id.btSend);
        layout = findViewById(R.id.chatWindow);
        layout.getBackground().setAlpha(150);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btSend:
                displayMsg(); // 显示消息
                sendMsg(); // 发送消息
                break;
        }
    }

    /**
     * 封装请求内容
     */
    private void sendMsg() {
        RequestMsg requestMsg = new RequestMsg();
        requestMsg.setKey(getString(R.string.robot_key));
        requestMsg.setInfo(sendMsgText);
        requestMsg.setUserid(userId);
        chat(requestMsg);
    }

    /**
     * 请求聊天
     * @param requestMsg
     */
    private void chat(RequestMsg requestMsg) {
        Call<ReceiveMsg> call = chat.chat(requestMsg);
        call.enqueue(new Callback<ReceiveMsg>() {
            @Override
            public void onResponse(Call<ReceiveMsg> call, Response<ReceiveMsg> response) {
                int code = response.body().getCode();
                switch (code) {
                    case TEXT_TYPE:
                        //处理文字消息
                        handTextMsg(response.body());
                        break;
                    case URL_TYPE:
                        //处理URL消息
                        handUrlMsg(response.body());
                        break;
                    case NEWS_TYPE:
                        //处理新闻消息
                        handNewsMsg(response.body());
                        break;
                    case COOKBOOK_TYPE:
                        //处理菜谱消息
                        break;
                }
            }

            @Override
            public void onFailure(Call<ReceiveMsg> call, Throwable t) {

            }
        });
    }

    /**
     * 处理新闻消息
     */
    private void handNewsMsg(ReceiveMsg body) {
        List<NewsItem> newsItems = body.getList();
        Random random = new Random();
        int index = random.nextInt(newsItems.size());
        NewsItem item = newsItems.get(index);
        robotMsg(item.getArticle() + "\n" + item.getDetailurl());
        adapterDataUpdate();
    }

    /**
     * 处理URL消息
     * @param body
     */
    private void handUrlMsg(ReceiveMsg body) {
        robotMsg(body.getText()+"\n"+body.getUrl());
        adapterDataUpdate();
    }

    /**
     * 处理文字消息
     * @param body
     */
    private void handTextMsg(ReceiveMsg body) {
        robotMsg(body.getText());
        adapterDataUpdate();
    }

    /**
     * 更新adapter数据和滚动到底部
     */
    private void adapterDataUpdate() {
        adapter.notifyDataSetChanged();
        chatView.smoothScrollToPosition(adapter.getItemCount());
    }

    /**
     * 获取发出消息并显示
     */
    private void displayMsg() {
        chatItem = new ChatItem(ChatItem.RIGHT);
        chatItem.setName(name);
        sendMsgText = editMessage.getText().toString();
        chatItem.setMessage(sendMsgText);
        chatList.add(chatItem);
        adapterDataUpdate();
        editMessage.setText(""); // 清空输入框
    }

    /**
     * post接口
     */
    public interface chatInterface {

        @POST("openapi/api")
        Call<ReceiveMsg> chat(@Body RequestMsg request);
    }

}
