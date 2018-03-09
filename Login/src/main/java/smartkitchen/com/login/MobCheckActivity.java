package smartkitchen.com.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import smartkitchen.com.login.forgetPsw.NewPswActivity;
import smartkitchen.com.login.modifyPsw.ModifyPwdActivity;
import smartkitchen.com.login.rebindTel.NewMobCheckActivity;
import smartkitchen.com.login.rebindTel.RebindTelActivity;
import smartkitchen.com.login.register.RegisterActivity;
import tools.MyLog;

import static smartkitchen.com.login.globle.constant.MOBILE;
import static smartkitchen.com.login.globle.constant.OPENTYPE;


public class MobCheckActivity extends AppCompatActivity {



    TextView mTitle;
    EditText   mTelNum;
    TextView mResend;
    EditText mIdcode;
    TextView  mInfo1,mInfo2;

    private String    mobileNum;
    private String oldMobileNum;
    private MyCountDownTimer mc;
    private int openType;

    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_mobcheck);

        toolbar = findViewById(R.id.toolbar1);
        toolbar.setTitle("短信验证");
        setSupportActionBar(toolbar);
        //关键下面两句话，设置了回退按钮，及点击事件的效果
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitle = findViewById(R.id.login_tv_title);
        mTelNum = findViewById(R.id.telephone_edtTxt);

        mResend = findViewById(R.id.login_resendcode);

        mIdcode = findViewById(R.id.login_idcode_et);

        mInfo1 = findViewById(R.id.login_idcode_info1);
        mInfo2 = findViewById(R.id.login_idcode_info2);

        oldMobileNum = getIntent().getStringExtra(MOBILE);
        openType = getIntent().getIntExtra(OPENTYPE,-1);

       if(openType==3)
       {
           mTitle.setText("输入旧手机号码");
       }
       else if(openType==4)
       {
           mTitle.setText("输入新手机号码");
       }

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
                            mInfo1.setText("验证码已经发送至：");
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
                            if(openType==0)//忘记密码
                            {
                                Intent intent = new Intent(MobCheckActivity.this, NewPswActivity.class);
                                intent.putExtra(MOBILE,mobileNum);
                                startActivity(intent);
                            }
                            else if(openType==1)//用户注册
                            {
                                Intent intent = new Intent(MobCheckActivity.this, RegisterActivity.class);
                                intent.putExtra(MOBILE,mobileNum);
                                startActivity(intent);
                            }
                            else if(openType==2)//修改密码
                            {
                                Intent intent = new Intent(MobCheckActivity.this, ModifyPwdActivity.class);
                                intent.putExtra(MOBILE,mobileNum);
                                startActivity(intent);
                            }
                            else if(openType==3)

                             {
                                 Intent intent = new Intent(MobCheckActivity.this, NewMobCheckActivity.class);
                                 intent.putExtra(MOBILE,mobileNum);
                                 startActivity(intent);
                             }


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
       if(mc!=null)
       {
           mc.onFinish();
           mc.cancel();
           MyLog.e("mc!=null","not null");
       }

       boolean cancel = false;
       View focusView = null;

       mTelNum.setError(null);
       mIdcode.setError(null);
       mInfo1.setText("");
       mInfo2.setText("");

       mobileNum = mTelNum.getText().toString();
       // Check for a valid tel.
       if (TextUtils.isEmpty(mobileNum))
       {
           mTelNum.setError(getString(R.string.login_error_field_required));
           focusView = mTelNum;
           cancel = true;
       } else if (!isTelNumValid(mobileNum)) {
           mTelNum.setError(getString(R.string.login_error_invalid_tel));
           focusView = mTelNum;
           cancel = true;
       }
       if (cancel)
       {
           focusView.requestFocus();
       }
       else
       {
           mIdcode.setError(null);
           mIdcode.setText("");
           mResend.setText("获取验证码");
           sendCode("86",mobileNum);
       }
   }
    public void onClickNext(View v)
    {
        boolean cancel = false;
        View focusView = null;

        mTelNum.setError(null);
        mIdcode.setError(null);

        mobileNum = mTelNum.getText().toString();
        // Check for a valid tel.
        if (TextUtils.isEmpty(mobileNum))
        {
            mTelNum.setError(getString(R.string.login_error_field_required));
            focusView = mTelNum;
            cancel = true;
        } else if (!isTelNumValid(mobileNum)) {
            mTelNum.setError(getString(R.string.login_error_invalid_tel));
            focusView = mTelNum;
            cancel = true;
        }
        if(TextUtils.isEmpty(mIdcode.getText().toString()))
        {
            mIdcode.setError("验证码不能为空");
            focusView = mIdcode;
            cancel = true;

        }

        if (cancel)
        {
            focusView.requestFocus();
        }
        else
        submitCode("86",mobileNum,mIdcode.getText().toString());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //用完回调要注销掉，否则可能会出现内存泄露
        SMSSDK.unregisterAllEventHandler();
        try
        {
            if(mc!=null)
                mc.cancel();
        }
        catch ( Exception e)
        {
           e.printStackTrace();
        }

    }

    private boolean isTelNumValid(String num) {
        //TODO: Replace this with your own logic
        return num.length()==11;
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
            mResend.setText("获取验证码");
        }
        @Override
        public void onTick(long millisUntilFinished)
        {

            mResend.setText("获取验证码" + "（"+millisUntilFinished / 1000+"）");
        }
    }
}
