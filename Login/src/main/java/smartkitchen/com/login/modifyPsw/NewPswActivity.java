package smartkitchen.com.login.modifyPsw;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import smartkitchen.com.login.R;

import static smartkitchen.com.login.modifyPsw.IdCodeActivity.MOBILE;


public class NewPswActivity extends AppCompatActivity {
    private String    mobileNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_new_psw);
        mobileNum = getIntent().getStringExtra(MOBILE);
        if(TextUtils.isEmpty(mobileNum))
        {
            Toast.makeText(getApplicationContext(),"手机号码为空",Toast.LENGTH_SHORT).show();
            return;
        }

    }
}
