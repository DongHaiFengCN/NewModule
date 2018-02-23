package smartkitchen.com.login.modifyPsw;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;
import smartkitchen.com.login.R;

public class ModifyPswActivity extends AppCompatActivity {
    private AutoCompleteTextView mTelNum;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_modify_psw);

        mTelNum = findViewById(R.id.modify_mobile_tv);
    }

    public void onClickOk(View v)
    {
        boolean cancel = false;
        View focusView = null;
        mTelNum.setError(null);
        String mobile = mTelNum.getText().toString();
        // Check for a valid tel.
        if (TextUtils.isEmpty(mobile)) {
            mTelNum.setError(getString(R.string.login_error_field_required));
            focusView = mTelNum;
            cancel = true;
        } else if (!isTelNumValid(mobile)) {
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

            //发送验证码
            Intent intent = new Intent(this, IdCodeActivity.class);
            intent.putExtra(IdCodeActivity.MOBILE,mobile);
            startActivity(intent);
        }
    }


    public void sendCode(Context context)
    {
        RegisterPage page = new RegisterPage();
        page.setRegisterCallback(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 处理成功的结果
                    HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
                    String country = (String) phoneMap.get("country"); // 国家代码，如“86”
                    String phone = (String) phoneMap.get("phone"); // 手机号码，如“13800138000”
                    // TODO 利用国家代码和手机号码进行后续的操作
                } else{
                    // TODO 处理错误的结果
                }
            }
        });
        page.show(context);
    }

    private boolean isTelNumValid(String num) {
        //TODO: Replace this with your own logic
        return num.length()==11;
    }

}
