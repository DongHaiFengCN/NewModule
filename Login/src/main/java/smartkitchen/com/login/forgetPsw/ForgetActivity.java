package smartkitchen.com.login.forgetPsw;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import smartkitchen.com.login.R;
import smartkitchen.com.login.modifyPsw.IdCodeActivity;

public class ForgetActivity extends AppCompatActivity {

    EditText   mTelNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_forget);
        mTelNum = findViewById(R.id.telephone_edtTxt);
    }
    public void onClickNext(View v)
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
    private boolean isTelNumValid(String num) {
        //TODO: Replace this with your own logic
        return num.length()==11;
    }

}
