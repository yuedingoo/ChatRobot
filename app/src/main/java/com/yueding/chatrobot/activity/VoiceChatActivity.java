package com.yueding.chatrobot.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.yueding.chatrobot.R;
import com.yueding.chatrobot.Utils.YdUtil;
import com.yueding.chatrobot.Voice.Record;
import com.yueding.chatrobot.Voice.Speech;
import com.yueding.chatrobot.adapter.VoiceChatAdapter;
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

public class VoiceChatActivity extends AppCompatActivity implements View.OnClickListener {

    //请求到的聊天内容类型
    private static final int TEXT_TYPE = 100000;
    private static final int URL_TYPE = 200000;
    private static final int NEWS_TYPE = 302000;
    private static final int COOKBOOK_TYPE = 308000;

    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private TextView tipsText;
    private TextView textChat;
    private TextView clearChat;
    private VoiceChatAdapter adapter;
    private List<ChatItem> chatList = new ArrayList<>();

    //是否第一次聊天
    public boolean isFirstChat = true;
    //是否播放语音完毕
    private boolean isSpeakCompleted = true;
    //是否录音结束
    private boolean isRecordComplete = false;

    //语音
    private Speech speech = null;
    private Record record = null;

    //权限相关
    private String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
    private List<String> permissionList = new ArrayList<>();

    //聊天相关
    private chatInterface chatService;
    private String userId = "111";
    private ChatItem chatItem;
    private String sendText;
    private String robotText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_chat);
        //全屏（状态栏透明）
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //让状态栏颜色变成深色
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        /**
         * 判断哪些权限未授予
         */
        permissionList.clear();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(VoiceChatActivity.this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permissions[i]);
            }
        }
        /**
         * 判断是否为空
         */
        if (permissionList.isEmpty()) {//未授予的权限为空，表示都授予了
            initVoice();
        } else {//请求权限方法
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);//将List转为数组
            ActivityCompat.requestPermissions(VoiceChatActivity.this, permissions, 1);
        }

        initView();

        RecyclerView.LayoutManager manager = new LinearLayoutManager(VoiceChatActivity.this);
        adapter = new VoiceChatAdapter(chatList);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.tuling123.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        chatService = retrofit.create(chatInterface.class);

    }

    /**
     * 初始化控件
     */
    private void initView() {
        recyclerView = findViewById(R.id.voiceRecyclerView);
        fab = findViewById(R.id.floatingActionButton);
        tipsText = findViewById(R.id.tipsText);
        textChat = findViewById(R.id.textChat);
        clearChat = findViewById(R.id.clearText);

        fab.setOnClickListener(this);
        textChat.setOnClickListener(this);
        clearChat.setOnClickListener(this);

    }

    /**
     * 初始化语音
     */
    private void initVoice() {
        speech = new Speech(VoiceChatActivity.this);
        record = new Record(VoiceChatActivity.this);
    }

    /**
     * post接口
     */
    public interface chatInterface {

        @POST("openapi/api")
        Call<ReceiveMsg> chat(@Body RequestMsg request);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floatingActionButton:
                isRecordComplete = false;
                if (!isSpeakCompleted) {
                    speech.stopSpeech();
                }
                record.startRecord(mRecognizerListener);
                break;
            case R.id.textChat:
                Intent intent = new Intent(VoiceChatActivity.this, ChatActivity.class);
                startActivity(intent);
                break;
            case R.id.clearText:
                chatList.clear();
                adapter.notifyDataSetChanged();
                isFirstChat = true;
                tipsText.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 语音识别监听器
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

        }

        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onEndOfSpeech() {
            isRecordComplete = true;
            if (isFirstChat) {
                tipsText.setVisibility(View.GONE);
                isFirstChat = false;
            }
        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            sendText = record.printResult(recognizerResult).toString();
            if (isRecordComplete && !"".equals(sendText)) {
                startChat(sendText);
            }
        }

        @Override
        public void onError(SpeechError speechError) {
            YdUtil.showToast("你好像没说话", VoiceChatActivity.this);
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    /**
     * 语音合成监听器
     */
    private com.iflytek.cloud.SynthesizerListener mSynthesizerListener = new com.iflytek.cloud.SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
            isSpeakCompleted = false;
        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {

        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {

        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {

        }

        @Override
        public void onCompleted(SpeechError speechError) {
            if (speechError == null) {
                isSpeakCompleted = true;
            } else {
                YdUtil.showToast("播放失败", VoiceChatActivity.this);
            }
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    /**
     * 开始发送消息聊天
     */
    private void startChat(String msg) {
        disSendMsg(msg);
        sendMsg(msg);
    }

    /**
     * 显示发出的消息
     */
    private void disSendMsg(String msg){
        chatItem = new ChatItem(ChatItem.RIGHT);
        chatItem.setMessage(msg);
        chatList.add(chatItem);
        adapterDataUpdate();
    }

    /**
     * 显示接收到的文字消息
     * @param txt
     */
    private void disRobotMsg(String txt) {
        chatItem = new ChatItem(ChatItem.LEFT);
        chatItem.setMessage(txt);
        chatList.add(chatItem);
        adapterDataUpdate();
    }

    /**
     * 更新adapter数据和滚动到底部
     */
    private void adapterDataUpdate() {
        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(adapter.getItemCount());
    }

    /**
     * 封装请求内容
     */
    private void sendMsg(String msg) {
        RequestMsg requestMsg = new RequestMsg();
        requestMsg.setKey(getString(R.string.robot_key));
        requestMsg.setInfo(msg);
        requestMsg.setUserid(userId);
        chat(requestMsg);
    }

    /**
     * 请求聊天
     * @param requestMsg
     */
    private void chat(RequestMsg requestMsg) {
        Call<ReceiveMsg> call = chatService.chat(requestMsg);
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
                YdUtil.showToast("哎呀，糟糕！连接不上服务器！", VoiceChatActivity.this);
            }
        });
    }

    /**
     * 处理文字消息
     * @param body
     */
    private void handTextMsg(ReceiveMsg body) {
        robotText = body.getText();
        speech.startSpeech(robotText, mSynthesizerListener);
        disRobotMsg(robotText);
    }

    /**
     * 处理URL消息
     * @param body
     */
    private void handUrlMsg(ReceiveMsg body) {
        speech.startSpeech(body.getText(), mSynthesizerListener);
        disRobotMsg(body.getText()+"\n"+body.getUrl());
    }

    /**
     * 处理新闻消息
     */
    private void handNewsMsg(ReceiveMsg body) {
        speech.startSpeech(body.getText(), mSynthesizerListener);
        List<NewsItem> newsItems = body.getList();
        Random random = new Random();
        int index = random.nextInt(newsItems.size());
        NewsItem item = newsItems.get(index);
        disRobotMsg(item.getArticle() + "\n" + item.getDetailurl());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initVoice();
                } else {
                    YdUtil.showToast("no permission", VoiceChatActivity.this);
                }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        record.destroy();
        speech.destroy();
    }

}
