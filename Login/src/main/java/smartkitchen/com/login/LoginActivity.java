package smartkitchen.com.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
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
import com.couchbase.lite.ArrayFunction;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import bean.kitchenmanage.user.Company;
import bean.kitchenmanage.user.Employee;
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

import smartkitchen.com.login.globle.constant;
import smartkitchen.com.login.model.responseModle;
import tools.CDBHelper;
import tools.MyLog;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * @date 2018/6/7
 * @autor loongsun
 * @email 125736964@qq.com
 * @description
 * @vertion
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    // UI references.
    private AutoCompleteTextView mNameView;
    private EditText mPasswordView;
    private TextView login_tv_type;
   // private TextInputLayout login_text_inputlayou;

    private CheckBox saveloginstatueChk;

    private Button mBtnLogin;
    //1:管理员手机号登录 2：员工登录
    private int loginType = 1;
    private String lastChannelId;
    private String lastPwd;
    private boolean isLastRemeber;

    private View mProgressView;
    private TextView progressInfo;
    private View mLoginFormView;
    private SharedPreferences pRemberLogin;

    private MyApplication myapp;
    private Toolbar toolbar;
   // private LocalBroadcastManager localBroadcastManager;
    private MyBroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        myapp = (MyApplication) getApplicationContext();
        toolbar = findViewById(R.id.toolbar1);
        toolbar.setTitle("肴点点");
        setSupportActionBar(toolbar);
        //关键下面两句话，设置了回退按钮，及点击事件的效果
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //2\控件初始化
        initWidget();
        //3\数据初始化
        String mobile = getIntent().getStringExtra("mobile");
        if (TextUtils.isEmpty(mobile)) {
            getShareData();
        } else {
            mNameView.setText(mobile);
        }
        //4\判断以什么模式登录
        myapp.mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                CDBHelper.getSharedInstance(getApplicationContext());
                List<Company> companyList = CDBHelper.getObjByClass(Company.class);
                if(companyList.size()>0){
                    lastChannelId = companyList.get(0).getChannelId();
                    lastPwd  =companyList.get(0).getPwd();
                    if(!isLastRemeber){//没有记忆的话
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                login_tv_type.performClick();
                            }
                        });
                    }
                }
            }
        });


    }
public void initWidget(){
    // Set up the login form.
    mNameView = findViewById(R.id.telephone);
   // mNameView.addTextChangedListener(textWatcher);

    // Drawable drawable1 = getResources().getDrawable(R.drawable.login_tel);
    // drawable1.setBounds(0, 0, 45, 45);
    // mNameView.setCompoundDrawables(drawable1, null, null, null);


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
//        Drawable drawable2 = getResources().getDrawable(R.drawable.login_psw);
//        drawable2.setBounds(0, 0, 45, 45);
//        mPasswordView.setCompoundDrawables(drawable2, null, null, null);

    pRemberLogin = getSharedPreferences("kitchenlogin", MODE_PRIVATE);
    saveloginstatueChk = findViewById(R.id.saveLoginStatue_chk);
    saveloginstatueChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            if (isChecked) {

            } else {
                pRemberLogin.edit()
                        .putString("pwd", null)
                        .commit();
            }
        }
    });

    login_tv_type = findViewById(R.id.login_tv_type);
    //login_text_inputlayou = findViewById(R.id.login_text_inputlayou);

    mBtnLogin = findViewById(R.id.btn_login);
    mBtnLogin.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View view) {
            attemptLogin();
        }
    });

    mLoginFormView = findViewById(R.id.login_form);
    mProgressView = findViewById(R.id.login_progress);

    progressInfo = findViewById(R.id.login_sync_info);
}
    public void onSetLoginType(View v) {

        mNameView.setText("");
        mPasswordView.setText("");
        if (loginType == 1) {
            //切换到离线员工模式
            //login_text_inputlayou.setHintEnabled(false);
            mNameView.setHint("员工号(员工登录)");
            loginType = 2;
            login_tv_type.setText("员工登录模式");
            Drawable drawable = getResources().getDrawable(R.drawable.ic_person_black_24dp);
            // 找到资源图片
            // 这一步必须要做，否则不会显示。
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 设置图片宽高
            mNameView.setCompoundDrawables(drawable, null, null, null);// 设置到控件中
        } else {
            //切换到管理员在线模式
            //login_text_inputlayou.setHintEnabled(true);
            mNameView.setHint("手机号码(管理员)");
            loginType = 1;
            login_tv_type.setText("管理员登录模式");
            Drawable drawable = getResources().getDrawable(R.drawable.ic_supervisor_account_black_24dp);
            // 找到资源图片
            // 这一步必须要做，否则不会显示。
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 设置图片宽高
            mNameView.setCompoundDrawables(drawable, null, null, null);// 设置到控件中


        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_menu_users, menu);
        return true;
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {

        if (menu != null) {
            if (menu.getClass() == MenuBuilder.class) {

                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {

                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_modify) {
            Intent intent = new Intent(this, MobCheckActivity.class);
            intent.putExtra("type", 2);//密码修改
            startActivity(intent);
        } else if (i == R.id.action_rebind) {
            Intent intent = new Intent(this, MobCheckActivity.class);
            intent.putExtra("type", 3);//重绑手机号
            startActivity(intent);
        }
        return true;
    }

//    private void getShareData() {
//        if (pRemberLogin != null) {
//            if (pRemberLogin.getBoolean("isrmb", false) == true) {
//                String mobile = pRemberLogin.getString("mobile", "");
//                String pwd = pRemberLogin.getString("pwd", "");
//                if (!TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(pwd)) {
//                    mNameView.setText(mobile);
//                    mPasswordView.setText(pwd);
//                    saveloginstatueChk.setChecked(true);
//                }
//            }
//        }
//    }

    private void getShareData()
    {
        if(pRemberLogin!=null)
        {
            if (pRemberLogin.getBoolean("isrmb", false) == true)
            {
                isLastRemeber = true;
                String name = pRemberLogin.getString("name","");
                mNameView.setText(name);

                loginType = pRemberLogin.getInt("loginType",1);
                if(loginType==1){
                    login_tv_type.setText("管理员登录模式");
                }
                else{
                    login_tv_type.setText("员工登录模式");
                }

                String pwd = pRemberLogin.getString("pwd","");
                if(!TextUtils.isEmpty(pwd))
                {
                    mPasswordView.setText(pwd);
                    saveloginstatueChk.setChecked(true);

                    MyLog.e("getShareData","setCheck True");
                }
                else{
                    saveloginstatueChk.setChecked(false);
                }

            }
        }
    }

    /**
     * 实时监听用户输入的手机号，输入至最后一位后
     */
//    TextWatcher textWatcher = new TextWatcher() {
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//        }
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count,
//                                      int after) {
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            if (s.length() == 11) {
//                mBtnLogin.setEnabled(true);
//            } else {
//                mBtnLogin.setEnabled(false);
//            }
//
//        }
//    };

    private void LoginOnLine(String name,String password){
        showProgress(true);

        OkHttpClient   okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("mobile", name)
                .add("pwd", password)
                .build();
        Request request = new Request.Builder()
                .url(constant.loginUrl)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    private boolean startReplicationOnOffLine() {

        if(lastChannelId!=null&&lastPwd!=null){
            myapp.setCompany_ID(lastChannelId);
            CDBHelper.startPushAndPullReplicationForCurrentUser(lastChannelId,lastPwd);
            return true;
        }
        else {
            return false;
        }
    }

    private void LoginOffLine(String name,String password) {
        showProgress(true);

        List<Employee> employeeList =CDBHelper.getObjByWhere(
                Expression.property("className").equalTo(Expression.string("Employee"))
                        .and(Expression.property("userName").equalTo(Expression.string(name)))
                        .and(Expression.property("pwd").equalTo(Expression.string(password))),null,Employee.class
        );

        if(employeeList.size()>0) {
            if(!startReplicationOnOffLine()){
                showProgress(false);
                //BuglyLog.e("LoginActivity", "公司信息不存在,不能正常离线登录");
                Toast.makeText(getApplicationContext(),"公司信息不存在",Toast.LENGTH_SHORT).show();
                return;
            }
            //是否记住密码
            saveShareParame(name,password);
            //全局用户
            myapp.setEmployee(employeeList.get(0));
        }
        else {

            showProgress(false);
            mPasswordView.setError(getString(R.string.login_error_incorrect_password));
            mPasswordView.requestFocus();

        }
    }

    private void saveShareParame(String name,String password)
    {
        if(saveloginstatueChk.isChecked())
        {
            pRemberLogin.edit()
                    .putBoolean("isrmb", true)
                    .putString("name",  mNameView.getText().toString())
                    .putString("pwd", password)
                    .putInt("loginType",loginType)
                    .commit();
            MyLog.e("saveShareParame","ok");
        }
        else
        {

            pRemberLogin.edit()
                    .putBoolean("isrmb", true)
                    .putString("name",  mNameView.getText().toString())
                    .putString("pwd", null)
                    .putInt("loginType",loginType)
                    .commit();

        }
    }
    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {


        // Reset errors.
        mNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String name = mNameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.login_error_field_required));
            focusView = mNameView;
            cancel = true;
        }
        else if (TextUtils.isEmpty(password))
        {
            mPasswordView.setError(getString(R.string.login_error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        else{
            if(loginType==1){
                if (!isMobileValid(name)) {
                    mNameView.setError(getString(R.string.login_error_invalid_tel));
                    focusView = mNameView;
                    cancel = true;
                }
                if(!isPasswordValid(password)){
                    mPasswordView.setError(getString(R.string.login_error_invalid_password));
                    focusView = mPasswordView;
                    cancel = true;
                }
            }
        }


        if (cancel) {
            focusView.requestFocus();
            return;
        }
        MyLog.e("loginType=",""+loginType);

        if(loginType==1){
            LoginOnLine(name,password);
        }
        else{
            LoginOffLine(name,password);
        }

//        // Reset errors.
//        mNameView.setError(null);
//        mPasswordView.setError(null);
//
//        // Store values at the time of the login attempt.
//        String telephone = mNameView.getText().toString();
//        String password = mPasswordView.getText().toString();
//
//        boolean cancel = false;
//        View focusView = null;
//
//        // Check for a valid password, if the user entered one.
//        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
//            mPasswordView.setError(getString(R.string.login_error_invalid_password));
//            focusView = mPasswordView;
//            cancel = true;
//        }
//        // Check for a valid email address.
//        if (TextUtils.isEmpty(telephone)) {
//            mNameView.setError(getString(R.string.login_error_field_required));
//            focusView = mNameView;
//            cancel = true;
//        } else if (!isEmailValid(telephone)) {
//            mNameView.setError(getString(R.string.login_error_invalid_tel));
//            focusView = mNameView;
//            cancel = true;
//        }
//
//        if (cancel) {
//            focusView.requestFocus();
//        } else {
//            // Show a progress spinner, and kick off a background task to
//            // perform the user login attempt.
//            showProgress(true);
//
//            OkHttpClient okHttpClient = new OkHttpClient();
//            RequestBody requestBody = new FormBody.Builder()
//                    .add("mobile", telephone)
//                    .add("pwd", password)
//                    .build();
//            Request request = new Request.Builder()
//                    .url(constant.loginUrl)
//                    .post(requestBody)
//                    .build();
//            okHttpClient.newCall(request).enqueue(callback);
//        }
    }

    //请求后的回调接口
    private Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            setResult(e.getMessage(), false);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
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
                if(flag)
                {
                    Gson gson = new Gson();
                    responseModle obj = gson.fromJson(data, new TypeToken<responseModle>() {}.getType());
                    if("ok".equals(obj.getStatus()))//
                    {
                        String userName = obj.getData().getChannelId();
                        String pwd =  mPasswordView.getText().toString();

                        if(lastChannelId!=null&&!lastChannelId.equals(userName)){//与上次的数据库不一样
                            CDBHelper.deleteDB();
                            MyLog.e("db delete over");

                            CDBHelper.reCreateDB();
                        }


                        //开始同步

                        CDBHelper.startPushAndPullReplicationForCurrentUser(userName,pwd);

                        myapp.setCompany_ID(userName);

                        MyLog.e("login*********","channeldId="+myapp.getCompany_ID());

                        //是否记住密码
                        saveShareParame(userName,pwd);
                        //
                      //  createAdminStation();


                    }
                    else {

                        showProgress(false);
                        mPasswordView.setError(getString(R.string.login_error_incorrect_password));
                        mPasswordView.requestFocus();
                        Toast.makeText(getApplicationContext(), obj.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    showProgress(false);
                    Toast.makeText(getApplicationContext(),"接口无法访问",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

//    private void createAdminStation()
//    {
//        myapp.setAdmin(true);
//    }

//    private void setResult(String jsonData, final boolean flag) {
//        final String data = jsonData;
//        Log.e("result", "json= " + jsonData);
//
//        //更新ui
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//                if (flag) {
//                    Gson gson = new Gson();
//                    responseModle obj = gson.fromJson(data, new TypeToken<responseModle>() {
//                    }.getType());
//                    //  if(!TextUtils.isEmpty(obj.getData()))
//                    if ("ok".equals(obj.getStatus())) {
//
//                        String userName = obj.getData().getChannelId();
//                        String pwd = mPasswordView.getText().toString();
//
//
//                        CDBHelper.dbName = userName;
//                        //开始同步
//                        CDBHelper.getSharedInstance(getApplicationContext());
//                        CDBHelper.startPushAndPullReplicationForCurrentUser(userName, pwd);
//                        myapp.setCompany_ID(userName);
//                        progressInfo.setText("数据更新中...");
//                        MyLog.e("login*********", "channeldId=" + myapp.getCompany_ID());
//
//                        //是否记住密码
//                        if (saveloginstatueChk.isChecked()) {
//                            pRemberLogin.edit()
//                                    .putBoolean("isrmb", true)
//                                    .putString("mobile", mNameView.getText().toString())
//                                    .putString("pwd", pwd)
//                                    .commit();
//                        } else {
//                            pRemberLogin.edit()
//                                    .putBoolean("isrmb", false)
//                                    .putString("mobile", null)
//                                    .putString("pwd", null)
//                                    .commit();
//                        }
//                        //跳转界面
//
//                    } else {
//
//                        showProgress(false);
//                        mPasswordView.setError(getString(R.string.login_error_incorrect_password));
//                        mPasswordView.requestFocus();
//                        Toast.makeText(getApplicationContext(), obj.getMsg(), Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    showProgress(false);
//                    Toast.makeText(getApplicationContext(), "接口无法访问", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });
//
//
//    }

    @Override
    protected void onResume() {
        super.onResume();

        // 1. 实例化BroadcastReceiver子类 &  IntentFilter
        mBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();

        // 2. 设置接收广播的类型
        intentFilter.addAction("sync_complete");

        // 3. 动态注册：调用Context的registerReceiver（）方法
         registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //销毁在onResume()方法中的广播
       unregisterReceiver(mBroadcastReceiver);
        MyLog.e("LoginActivity", "onPause");
    }

    @Override
    protected void onDestroy() {
        MyLog.e("LoginActivity", "onDestroy");
        super.onDestroy();
    }

    // 继承BroadcastReceivre基类
    public class MyBroadcastReceiver extends BroadcastReceiver {

        // 复写onReceive()方法
        // 接收到广播后，则自动调用该方法
        @Override
        public void onReceive(Context context, Intent intent) {
            //写入接收广播后的操作
            String action = intent.getAction();
            if (action.equals("sync_complete")) {
                showProgress(false);
                ARouter
                        .getInstance()
                        .build("/order/DeskActivity")
                        .withString("mobile", mNameView.getText().toString())
                        .withString("channelId", myapp.getCompany_ID())
                        .navigation();
                //finish();
            }
        }
    }

    private boolean isMobileValid(String email) {
        //TODO: Replace this with your own logic
        return email.length() == 11;
    }

    private boolean isPasswordValid(String password) {

        return password.length() >= 6;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

//            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//                }
//            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressInfo.setVisibility(show ? View.VISIBLE : View.GONE);

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

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mNameView.setAdapter(adapter);
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

    public void onClickRegister(View v) {
        Intent intent = new Intent(this, MobCheckActivity.class);
        intent.putExtra("type", 1);//用户注册
        startActivity(intent);
    }

    public void onClickForget(View v) {
        Intent intent = new Intent(this, MobCheckActivity.class);
        intent.putExtra("type", 0);//用户忘记密码
        startActivity(intent);
    }

}

