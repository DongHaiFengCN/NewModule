package smartkitchen.com.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;
import doaing.mylibrary.MyApplication;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import smartkitchen.com.login.forgetPsw.ForgetActivity;
import smartkitchen.com.login.globle.constant;
import smartkitchen.com.login.model.responseModle;
import smartkitchen.com.login.register.RegisterActivity;
import tools.CDBHelper;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */

    private static final int REQUEST_READ_CONTACTS = 0;


    // UI references.
    private AutoCompleteTextView mTelView;
    private EditText mPasswordView;


    private CheckBox saveloginstatueChk;

    private Button mBtnLogin;

    private View mProgressView;
    private View mLoginFormView;
    private SharedPreferences pRemberLogin;

    private MyApplication myapp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        myapp = (MyApplication)getApplicationContext();
        // Set up the login form.
        mTelView = findViewById(R.id.telephone);
        mTelView.addTextChangedListener(textWatcher);
        Drawable drawable1 = getResources().getDrawable(R.drawable.login_tel);
        drawable1.setBounds(0,0,45,45);
        mTelView.setCompoundDrawables(drawable1,null,null,null);

       // populateAutoComplete();

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        Drawable drawable2 = getResources().getDrawable(R.drawable.login_psw);
        drawable2.setBounds(0,0,45,45);
        mPasswordView.setCompoundDrawables(drawable2,null,null,null);


        pRemberLogin   = getSharedPreferences("kitchenlogin",MODE_PRIVATE);



        saveloginstatueChk = findViewById(R.id.saveLoginStatue_chk);
        saveloginstatueChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked)
                {

                }
                else
                {
                    pRemberLogin.edit()
                            .putBoolean("isrmb", false)
                            .putString("mobile", null)
                            .putString("pwd", null)
                            .commit();
                }
            }
        });
        mBtnLogin = findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        getShareData();

    }

    private void getShareData()
    {
        if(pRemberLogin!=null)
        {
            if (pRemberLogin.getBoolean("isrmb", false) == true)
            {
                   String mobile = pRemberLogin.getString("mobile","");
                   String pwd = pRemberLogin.getString("pwd","");
                   if(!TextUtils.isEmpty(mobile)&&!TextUtils.isEmpty(pwd))
                   {
                       mTelView.setText(mobile);
                       mPasswordView.setText(pwd);
                       saveloginstatueChk.setChecked(true);

                   }

            }
        }
    }
    /**
     * 实时监听用户输入的手机号，输入至最后一位后
     */
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }
        @Override
        public void afterTextChanged(Editable s)
        {
            if(s.length() == 11)
            {
               mBtnLogin.setEnabled(true);
            }
            else
                mBtnLogin.setEnabled(false);
        }
    };
//
//    private void populateAutoComplete() {
//        if (!mayRequestContacts()) {
//            return;
//        }
//        getLoaderManager().initLoader(0, null, this);
//    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS))
        {
            Snackbar.make(mTelView, R.string.login_permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        }
        else
        {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_READ_CONTACTS) {
//            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                populateAutoComplete();
//            }
//        }
//    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mTelView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String telephone = mTelView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password))
        {
            mPasswordView.setError(getString(R.string.login_error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(telephone)) {
            mTelView.setError(getString(R.string.login_error_field_required));
            focusView = mTelView;
            cancel = true;
        } else if (!isEmailValid(telephone)) {
            mTelView.setError(getString(R.string.login_error_invalid_tel));
            focusView = mTelView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            OkHttpClient   okHttpClient = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("mobile", telephone)
                    .add("pwd", password)
                    .build();
            Request request = new Request.Builder()
                    .url(constant.loginUrl)
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
        Log.e("result","json= "+jsonData);

        //更新ui
        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                showProgress(false);

                if(flag)
                {
                    Gson gson = new Gson();
                    responseModle obj = gson.fromJson(data, new TypeToken<responseModle>() {}.getType());
                    if(TextUtils.isEmpty(obj.getStatusCode()))//
                    {

                        String userName = obj.getData();
                        String pwd =  mPasswordView.getText().toString();
                        //开始同步
                        CDBHelper.getSharedInstance(getApplicationContext());
                        CDBHelper.startPushAndPullReplicationForCurrentUser(userName,pwd);

                        myapp.setCompany_ID(userName);

                        //是否记住密码
                        if(saveloginstatueChk.isChecked())
                        {
                            pRemberLogin.edit()
                                    .putBoolean("isrmb", true)
                                    .putString("mobile",  mTelView.getText().toString())
                                    .putString("pwd", pwd)
                                    .commit();
                        }
                        else
                        {
                            pRemberLogin.edit()
                                    .putBoolean("isrmb", false)
                                    .putString("mobile", null)
                                    .putString("pwd", null)
                                    .commit();
                        }

                        //跳转界面
                        ARouter
                                .getInstance()
                                .build("/order/DeskActivity")
                                .withString("mobile",mTelView.getText().toString())
                                .withString("channelId",userName)
                                .navigation();
                        finish();
                    }//r
                    else {

                        mPasswordView.setError(getString(R.string.login_error_incorrect_password));
                        mPasswordView.requestFocus();
                        Toast.makeText(getApplicationContext(), obj.getMessage(), Toast.LENGTH_SHORT).show();
                    }



                }
                else
                {
                    Toast.makeText(getApplicationContext(),"接口无法访问",Toast.LENGTH_SHORT).show();
                }

            }
        });



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

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.length()==11;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() == 6;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
        {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
           // mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI, ContactsContract.Contacts.Data.CONTENT_DIRECTORY),
                ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Phone
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Log.e("tag","num="+ProfileQuery.num);
            emails.add(cursor.getString(ProfileQuery.num));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mTelView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
        };

        int id = 0;
        int IS_PRIMARY = 1;
        int num = 2;
    }

public void onClickRegister(View v)
{
    Intent intent = new Intent(this, RegisterActivity.class);
    startActivity(intent);
}
public void onClickForget(View v)
{
    Intent intent = new Intent(this, ForgetActivity.class);
    startActivity(intent);
}

}

