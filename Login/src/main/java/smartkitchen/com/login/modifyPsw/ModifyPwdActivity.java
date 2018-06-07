package smartkitchen.com.login.modifyPsw;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import smartkitchen.com.login.LoginActivity;
import smartkitchen.com.login.R;
import smartkitchen.com.login.globle.constant;
import smartkitchen.com.login.model.responseModle;

import static smartkitchen.com.login.globle.constant.MOBILE;


public class ModifyPwdActivity extends AppCompatActivity {
    private String    mobileNum;
    private EditText mPsw0,mPsw1,mPsw2;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_modify_psw);
        toolbar = findViewById(R.id.toolbar1);
        toolbar.setTitle("密码修改");
        setSupportActionBar(toolbar);
        //关键下面两句话，设置了回退按钮，及点击事件的效果
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mobileNum = getIntent().getStringExtra(MOBILE);
        if(TextUtils.isEmpty(mobileNum))
        {
            Toast.makeText(getApplicationContext(),"手机号码为空",Toast.LENGTH_SHORT).show();
            return;
        }

        mPsw0 = findViewById(R.id.password_old);
        mPsw1 = findViewById(R.id.password_new_1);
        mPsw2 = findViewById(R.id.password_new_2);
    }
    public void onClickChange(View v)
    {
        boolean cancel = false;
        View focusView = null;
        // Reset errors.
        mPsw1.setError(null);
        mPsw2.setError(null);
        //取值
        String psw0 = mPsw0.getText().toString();
        String psw1 = mPsw1.getText().toString();
        String psw2 = mPsw2.getText().toString();
        //验证数据正确性
        // Check for psw.
        if (TextUtils.isEmpty(psw0) | PasswordInValid(psw0))
        {
            mPsw0.setError(getString(R.string.login_error_invalid_password));
            focusView = mPsw0;
            cancel = true;
        }

        if (TextUtils.isEmpty(psw1)|PasswordInValid(psw1))
        {
            mPsw1.setError(getString(R.string.login_error_invalid_password));
            focusView = mPsw1;
            cancel = true;
        }
        if (TextUtils.isEmpty(psw2)|PasswordInValid(psw2))
        {
            mPsw2.setError(getString(R.string.login_error_invalid_password));
            focusView = mPsw2;
            cancel = true;
        }
        if(!psw1.equals(psw2))
        {
            mPsw2.setError(getString(R.string.login_error_inconsistent_password));
            focusView = mPsw2;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else
        {
            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("mobile", mobileNum)
                    .add("newPassword", psw1)
                    .add("oldPassword",psw0)
                    .build();
            Request request = new Request.Builder()
                    .url(constant.modifyPswUrl)
                    .post(requestBody)
                    .build();
            okHttpClient.newCall(request).enqueue(callback);
        }
    }
    //请求后的回调接口
    private Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e)
        {
            setResult(e.getMessage(), false);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException
        {
            setResult(response.body().string(), true);

        }
    };
    private void setResult (String jsonData, final boolean flag)
    {
        final String data = jsonData;
        Log.e("result","info="+jsonData);
        //更新ui
        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {

                if(flag)
                {
                    Gson gson = new Gson();
                    responseModle obj = gson.fromJson(data, new TypeToken<responseModle>() {}.getType());
                    if("ok".equals(obj.getStatus()))
                    {

                        Intent intent = new Intent(ModifyPwdActivity.this, LoginActivity.class);
                        intent.putExtra("mobile",mobileNum);
                        startActivity(intent);

                        finish();
                    }
                    else
                    {

                        Toast.makeText(getApplicationContext(),""+ obj.getMsg(), Toast.LENGTH_SHORT).show();
                    }



                }
                else
                {
                    Toast.makeText(getApplicationContext(),"接口无法访问",Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
    private boolean PasswordInValid(String password)
    {
        return password.length() < 6;
    }
}
