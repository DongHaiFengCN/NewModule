package doaing.order.view;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.LiveQueryChange;
import com.couchbase.lite.LiveQueryChangeListener;
import com.couchbase.lite.Ordering;
import com.couchbase.lite.Query;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.gprinter.aidl.GpService;
import com.gprinter.command.GpCom;
import com.gprinter.service.GpPrintService;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.kitchenmanage.dishes.DishesKindC;
import bean.kitchenmanage.user.UsersC;
import doaing.mylibrary.ISharedPreferences;
import doaing.mylibrary.MyApplication;
import doaing.order.R;
import doaing.order.application.CDBHelper;
import doaing.order.module.ILoginView;
import doaing.order.presenter.ILoginPresenter;
import doaing.order.presenter.LoginPresentImpl;
import doaing.order.untils.MyLog;
import tools.ToolUtil;

import static com.gprinter.command.GpCom.ACTION_CONNECT_STATUS;
import static doaing.order.device.ListViewAdapter.DEBUG_TAG;


/**
 * @author 董海峰
 * @date 2017/10/25
 */
public class LoginActivity extends AppCompatActivity implements ILoginView, ISharedPreferences {
    private EditText name;
    private EditText password;
    private Button submit;
    private CheckBox saveloginstatueChk;
    private MyApplication myApplication;

    private String userNumber, userPsw;
    private InputMethodManager inputMethodManager;
    private Intent intent;
    private List<UsersC> usersCList;
    List<DishesKindC> dishesKindCList;
    private UsersC usersC;
    private Map<String, List<Document>> dishesObjectCollection;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        myApplication = (MyApplication) getApplication();
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        intent = new Intent(this, DeskActivity.class);

//        Document document = new Document("UsersC."+ ToolUtil.getUUID());
//        document.setString("className","UsersC");
//        document.setString("channelId",myApplication.getCompany_ID());
//        document.setString("userName","001");
//        document.setString("passwd","123456");
//        document.setString("employeeName","王博11");
//        db = myApplication.getDatabase();
//        try {
//            db.save(document);
//        } catch (CouchbaseLiteException e) {
//            e.printStackTrace();
//        }
//        CDBHelper.saveDocument(getApplicationContext(),document);
//        Document doc = db.getDocument(document.getId());
//        Log.e("Log",""+doc.getString("employeeName"));

        usersCList = CDBHelper.getObjByWhere(getApplicationContext(),
                Expression.property("className")
                        .equalTo("UsersC"), null, UsersC.class);

        Log.e("Login",""+usersCList.size());
//        for (int i = 0; i < usersCList.size(); i++) {
//
//            //Log.e("Login", usersCList.get(i).getUserName().toString() + "_" + usersCList.get(i).getEmployeeName());
//
//        }



        //查看是否有缓存
        userNumber = myApplication.getSharePreferences().getString("name", "");
        userPsw = myApplication.getSharePreferences().getString("password", "");


        for (UsersC u : usersCList) {
            if (u.getUserName().equals(userNumber)) {
                usersC = u;
                break;
            }
        }

        //Log.e("Log",listsLiveQuery().toString());

//       LiveQuery liveQuery = listsLiveQuery();
//        liveQuery.addChangeListener(new LiveQueryChangeListener() {
//            @Override
//            public void changed(LiveQueryChange change) {
//                ResultSet rs = change.getRows();
//                Result row;
//                while ((row = rs.next()) != null)
//                {
//                     MyLog.e("liveQuery change Id="+row.getString(0));
//                }
//            }
//        });
//        liveQuery.run();

        List<Document> doc = CDBHelper.getDocmentsByClass(getApplicationContext(),UsersC.class);

        Log.e("Login",""+doc.size());

        //无缓存
        if ("".equals(userNumber)) {

            initView();

        } else {

            //有缓存
            initView();
            name.setText(userNumber);
            password.setText(userPsw);
            saveloginstatueChk.setChecked(true);
            password.clearFocus();
            password.setFocusableInTouchMode(false);

        }

    }

    private LiveQuery listsLiveQuery()
    {
        return Query.select(SelectResult.expression(Expression.meta().getId()))
                .from(DataSource.database(db))
                .where(Expression.property("className").equalTo("UsersC")
                      )
                .toLive();
    }


    private void initView() {

        name =  findViewById(R.id.loginName_edtTxt);

        password = findViewById(R.id.loginPassword_edtTxt);

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.length() == 6) {

                    if (inputMethodManager.isActive()) {
                        inputMethodManager.hideSoftInputFromWindow(LoginActivity.this.getCurrentFocus().getWindowToken(), 0);
                    }
                }
            }
        });

        saveloginstatueChk =  findViewById(R.id.saveLoginStatue_chk);

        submit = findViewById(R.id.login_bt);

        final ILoginPresenter iLoginPresenter = new LoginPresentImpl(this, getApplicationContext());

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.length() == 3) {

                    password.setFocusable(true);
                    password.setFocusableInTouchMode(true);
                    password.requestFocus();
                }

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iLoginPresenter.doLogin();



            }
        });


    }

    @Override
    public String[] getLoginInfo() {

        String[] info = new String[2];
        info[0] = name.getText().toString();
        info[1] = password.getText().toString();
        userNumber = info[0];

        return info;
    }

    @Override
    public void showError(String error) {

        LinearLayout constraintLayout = findViewById(R.id.linearLayout);

        Snackbar.make(constraintLayout, error, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void success() {

       myApplication.setUsersC(usersC);

        //initDishesData();
        startActivity(intent);
    }

    @Override
    public boolean isSave() {

        return saveloginstatueChk.isChecked();
    }


    @Override
    public SharedPreferences getSharePreferences() {

        return myApplication.getSharePreferences();
    }

    @Override
    public boolean cancleSharePreferences() {

        return myApplication.cancleSharePreferences();
    }

}