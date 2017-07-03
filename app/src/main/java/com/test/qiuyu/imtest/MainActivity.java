package com.test.qiuyu.imtest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;

public class MainActivity extends AppCompatActivity {
    private static final String USER_ID1 = "111111";
    private static final String USER_NAME1 = "qiuyu";
    private static final String TOKEN1 = "LheS6UcgW7e2Htd8thvv+gW50qHwF1vMDnCQJzYCXlrjgcekpT3rKhIEHUW592GHwQlX2IAIYyiO1B5jjd6iBw==";

    private static final String USER_ID2 = "222222";
    private static final String USER_NAME2 = "qiuyu1";
    private static final String TOKEN2 = "JlP6k1oPxegLbSPeuf97A8DA5P2ltjjStojBRmwHUpaUSFgcl1jceFCapCMomkXlWxGjZqUX1m1Zv9OIDsPuEw==";

    private ListView messageTxt;
    private Button user1Login;
    private Button user2Login;
    private EditText content;
    private Button send;
    private Button sendImg;
    private MyReceiveMessageListener receiveMessageListener;
    boolean user1Flag = true;

    private static final int MY_PERMISSION_REQUEST_CODE = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>22){
            Log.d("qiuyu","Build.VERSION.SDK_INT>22");
            if (!checkPermissionAllGranted(Permissions)){
                requesetPermission(Permissions);
            }
        }
        setContentView(R.layout.activity_main);
        messageTxt = (ListView)findViewById(R.id.message);
        user1Login = (Button)findViewById(R.id.user1);
        user2Login = (Button)findViewById(R.id.user2);
        content = (EditText)findViewById(R.id.edit);
        send = (Button)findViewById(R.id.send);
        sendImg = (Button)findViewById(R.id.sendImg);

        user1Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                receiveMessageListener = new MyReceiveMessageListener();
                RongIMClient.setOnReceiveMessageListener(receiveMessageListener);
                connect(TOKEN1);
                user1Flag = true;
            }
        });

        user2Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                receiveMessageListener = new MyReceiveMessageListener();
                RongIMClient.setOnReceiveMessageListener(receiveMessageListener);
                connect(TOKEN2);
                user1Flag = false;
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user1Flag){
                    sendMessage(USER_ID2, content.getText().toString());
                }else{
                    sendMessage(USER_ID1, content.getText().toString());
                }
            }
        });

        sendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user1Flag){
                    sendImg(USER_ID2);
                }else{
                    sendImg(USER_ID1);
                }
            }
        });

    }

    /**
     * 建立与融云服务器的连接
     *
     * @param token
     */
    private void connect(String token) {

        if (getApplicationInfo().packageName.equals(MyApplication.getCurProcessName(getApplicationContext()))) {

            /**
             * IMKit SDK调用第二步,建立与服务器的连接
             */
            RongIMClient.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
                 */
                @Override
                public void onTokenIncorrect() {
                    Log.d("LoginActivity", "--onTokenIncorrect");
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token
                 */
                @Override
                public void onSuccess(String userid) {
                    Log.d("LoginActivity", "--onSuccess---" + userid);
                    getLeft();
                    if (user1Flag){
                        getMessage(USER_ID2);
                    }else{
                        getMessage(USER_ID1);
                    }
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Log.d("LoginActivity", "--onError" + errorCode);
                }
            });
        }
    }

    private class MyReceiveMessageListener implements RongIMClient.OnReceiveMessageListener {

        /**
         * 收到消息的处理。
         * @param message 收到的消息实体。
         * @param left 剩余未拉取消息数目。
         * @return
         */
        @Override
        public boolean onReceived(Message message, int left) {
            //开发根据自己需求自行处理
            Log.i("qiuyu","left="+left);
            return false;
        }
    }

    private void sendMessage(final String toUserId, final String content){
        /**
         * 发送消息。
         * @param conversationType  会话类型
         * @param targetId          会话ID
         */
        RongIMClient.getInstance().sendMessage(Conversation.ConversationType.PRIVATE, toUserId,
                TextMessage.obtain(content), null, null, new RongIMClient.SendMessageCallback() {
                    @Override
                    public void onSuccess(Integer integer) {
                        Log.d("qiuyu", "发送成功:"+toUserId+"   "+content);
                    }

                    @Override
                    public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                        Log.d("qiuyu", "发送失败");
                    }
                }, null);
    }

    private void getLeft(){
        RongIMClient.getInstance().getTotalUnreadCount(new RongIMClient.ResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                int totalUnreadCount = integer;
                //开发者根据自己需求自行处理接下来的逻辑
                Log.d("qiuyu","getTotalUnreadCount="+totalUnreadCount);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    private void getMessage(String TargetId){
        Log.d("qiuyu","getMessage");
        RongIMClient.getInstance().getLatestMessages(Conversation.ConversationType.PRIVATE, TargetId, 50, new RongIMClient.ResultCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messages) {
                MessageAdapter adapter = new MessageAdapter(messages);
                messageTxt.setAdapter(adapter);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Log.d("qiuyu","onError===="+errorCode);
            }
        });
    }

    private String[] Permissions = {
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.RECORD_AUDIO,
    };

    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    /**
     * 第 3 步: 申请权限结果返回处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;

            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted) {
                // 如果所有的权限都授予了, 则执行备份代码
                Log.v("qiuyu","有权限");

            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                Log.v("qiuyu","没权限");
            }
        }
    }

    private void requesetPermission(String[] permissions){
        ActivityCompat.requestPermissions(this,permissions,MY_PERMISSION_REQUEST_CODE);
    }

    class MessageAdapter extends BaseAdapter{
        private List<Message> list;
        public MessageAdapter(List<Message> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Message message = list.get(i);
            Log.v("qiuyu","message.getSenderUserId()="+message.getSenderUserId());
            String userID;
            View view1;
            if (user1Flag){
                userID = USER_ID1;
            }else{
                userID = USER_ID2;
            }
            if (message.getSenderUserId().equals(userID)){
                view1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.message_item_right, null);
            }else{
                view1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.message_item_left, null);
            }
            TextView messageitem = (TextView)view1.findViewById(R.id.messageItem);
            ImageView imgItem = (ImageView)view1.findViewById(R.id.imgItem);

            String msgtype = message.getObjectName();
            if(msgtype.equals("RC:TxtMsg")){
                messageitem.setText(((TextMessage)message.getContent()).getContent());
                imgItem.setVisibility(View.GONE);
                messageitem.setVisibility(View.VISIBLE);
            }else if (msgtype.equals("RC:ImgMsg")){
                imgItem.setVisibility(View.VISIBLE);
                messageitem.setVisibility(View.GONE);
                final ImageMessage imgMsg = (ImageMessage)message.getContent();
                Log.e("qiuyu", "Img.Uri="+imgMsg.getThumUri());
                imgItem.setImageBitmap(getBitmapFromUri(imgMsg.getThumUri()));
                imgItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this,ImageScan.class);
                        intent.putExtra("Uri",imgMsg.getRemoteUri().toString());
                        startActivity(intent);
                    }
                });
            }
            return view1;
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            // 读取uri所在的图片
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
            Log.e("[Android]", e.getMessage());
            Log.e("[Android]", "目录为：" + uri);
            e.printStackTrace();
            return null;
        }
    }

    private void sendImg(String targetId){
        //发送图片消息
        File imageFileSource = new File(getCacheDir(), "source.jpg");
        File imageFileThumb = new File(getCacheDir(), "thumb.jpg");

        try {
            // 读取图片。
            InputStream is = getAssets().open("emmy.jpg");

            Bitmap bmpSource = BitmapFactory.decodeStream(is);

            imageFileSource.createNewFile();

            FileOutputStream fosSource = new FileOutputStream(imageFileSource);

            // 保存原图。
            bmpSource.compress(Bitmap.CompressFormat.JPEG, 100, fosSource);

            // 创建缩略图变换矩阵。
            Matrix m = new Matrix();
            m.setRectToRect(new RectF(0, 0, bmpSource.getWidth(), bmpSource.getHeight()), new RectF(0, 0, 160, 160), Matrix.ScaleToFit.CENTER);

            // 生成缩略图。
            Bitmap bmpThumb = Bitmap.createBitmap(bmpSource, 0, 0, bmpSource.getWidth(), bmpSource.getHeight(), m, true);

            imageFileThumb.createNewFile();

            FileOutputStream fosThumb = new FileOutputStream(imageFileThumb);

            // 保存缩略图。
            bmpThumb.compress(Bitmap.CompressFormat.JPEG, 60, fosThumb);

        } catch (IOException e) {
            e.printStackTrace();
        }

        ImageMessage imgMsg = ImageMessage.obtain(Uri.fromFile(imageFileThumb), Uri.fromFile(imageFileSource));

    /**
     * 发送图片消息。
     *
     * @param conversationType         会话类型。
     * @param targetId                 会话目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
     * @param imgMsg                   消息内容。
     * @param pushContent              接收方离线时需要显示的push消息内容。
     * @param pushData                 接收方离线时需要在push消息中携带的非显示内容。
     * @param SendImageMessageCallback 发送消息的回调。
     */
        RongIMClient.getInstance().sendImageMessage(Conversation.ConversationType.PRIVATE, targetId, imgMsg, "", "", new RongIMClient.SendImageMessageCallback() {

            @Override
            public void onAttached(Message message) {
                //保存数据库成功
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode code) {
                //发送失败
                Log.v("qiuyu","发送图片失败");
            }

            @Override
            public void onSuccess(Message message) {
                //发送成功
                Log.v("qiuyu","发送图片成功");
            }

            @Override
            public void onProgress(Message message, int progress) {
                Log.v("qiuyu","发送图片进度"+progress);
            }
        });
    }
}
