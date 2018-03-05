package smartkitchen.com.login.modifyPsw;

import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import smartkitchen.com.login.R;


public class IdCodeActivity extends AppCompatActivity {

    public static  String MOBILE = "mobileNum";
    TextView mResend;
    EditText mIdcode;
    TextView  mInfo1,mInfo2;
    private String    mobileNum;
    private MyCountDownTimer mc;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_id_code);

        mResend = findViewById(R.id.login_resendcode);
        mResend.setText("重新发送");
        mIdcode = findViewById(R.id.login_idcode_et);
        mInfo1 = findViewById(R.id.login_idcode_info1);
        mInfo2 = findViewById(R.id.login_idcode_info2);
        mobileNum = getIntent().getStringExtra(MOBILE);

        if(TextUtils.isEmpty(mobileNum))
        {
            return;
        }
        sendCode("86",mobileNum);
    }
    // 请求验证码，其中country表示国家代码，如“86”；phone表示手机号码，如“13800138000”
    public void sendCode(String country, String phone) {
        // 注册一个事件回调，用于处理发送验证码操作的结果
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE)
                {
                    //更新ui
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            mInfo2.setText(mobileNum);
                            mc = new MyCountDownTimer(60000, 1000);
                            mc.start();
                        }});
                }
                else
                {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            mInfo1.setText("验证码未能发送至：");
                            mInfo2.setText(mobileNum);
                        }});
                }

            }
        });
        // 触发操作
        SMSSDK.getVerificationCode(country, phone);
    }
    // 提交验证码，其中的code表示验证码，如“1357”
    public void submitCode(String country, String phone, String code) {
        // 注册一个事件回调，用于处理提交验证码操作的结果
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            Intent intent = new Intent(getApplicationContext(), NewPswActivity.class);
                            intent.putExtra(IdCodeActivity.MOBILE,mobileNum);
                            startActivity(intent);
                        }});
                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                           mIdcode.setError("验证失败");
                        }});
                }

            }
        });
        // 触发操作
        SMSSDK.submitVerificationCode(country, phone, code);
    }


   public void onClickResend(View v)
   {
       mIdcode.setError(null);
       mIdcode.setText("");
       mResend.setText("重新发送");
       sendCode("86",mobileNum);
   }
    public void onClickNext(View v)
    {
        if(TextUtils.isEmpty(mIdcode.getText().toString()))
        {
            mIdcode.setError("验证码不能为空");
            return;
        }
        submitCode("86",mobileNum,mIdcode.getText().toString());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //用完回调要注销掉，否则可能会出现内存泄露
        SMSSDK.unregisterAllEventHandler();
        mc.cancel();
    }


    /**
     * 继承 CountDownTimer 防范
     *
     * 重写 父类的方法 onTick() 、 onFinish()
     */

    class MyCountDownTimer extends CountDownTimer {
        /**
         *
         * @param millisInFuture
         *      表示以毫秒为单位 倒计时的总数
         *
         *      例如 millisInFuture=1000 表示1秒
         *
         * @param countDownInterval
         *      表示 间隔 多少微秒 调用一次 onTick 方法
         *
         *      例如: countDownInterval =1000 ; 表示每1000毫秒调用一次onTick()
         *
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish()
        {
            mResend.setText("重新发送");
        }
        @Override
        public void onTick(long millisUntilFinished)
        {

            mResend.setText("" + millisUntilFinished / 1000 + "秒...");
        }
    }
}
