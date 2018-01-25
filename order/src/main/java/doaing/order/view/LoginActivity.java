package doaing.order.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Ordering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.kitchenmanage.dishes.DishesKindC;
import bean.kitchenmanage.user.UsersC;
import doaing.order.R;
import doaing.order.application.CDBHelper;
import doaing.order.presenter.ILoginPresenter;
import doaing.order.module.ILoginView;
import doaing.order.application.ISharedPreferences;
import doaing.order.presenter.LoginPresentImpl;
import doaing.order.application.MyApplication;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        myApplication = (MyApplication) getApplication();
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        intent = new Intent(this, DeskActivity.class);
        usersCList = CDBHelper.getObjByWhere(getApplicationContext(),
                Expression.property("className")
                        .equalTo("UsersC"), null, UsersC.class);

        for (int i = 0; i < usersCList.size(); i++) {

            Log.e("Login", usersCList.get(i).getUserName().toString() + "_" + usersCList.get(i).getEmployeeName());

        }

        //  myApplication.cancleSharePreferences();

        //查看是否有缓存

        userNumber = myApplication.getSharePreferences().getString("name", "");
        userPsw = myApplication.getSharePreferences().getString("password", "");

        for (UsersC u : usersCList) {
            if (u.getUserName().equals(userNumber)) {

                usersC = u;

                break;
            }
        }



        dishesKindCList = CDBHelper.getObjByWhere(getApplicationContext()
                , Expression.property("className").equalTo("DishesKindC")
                        .and(Expression.property("isSetMenu").equalTo(false))
                , Ordering.property("kindName")
                        .ascending(), DishesKindC.class);


        dishesObjectCollection = new HashMap<>();

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


    private void initView() {

        name = (EditText) findViewById(R.id.loginName_edtTxt);

        password = (EditText) findViewById(R.id.loginPassword_edtTxt);

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

        saveloginstatueChk = (CheckBox) findViewById(R.id.saveLoginStatue_chk);

        submit = (Button) findViewById(R.id.login_bt);

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

        initDishesData();
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


    public void initDishesData() {
        myApplication.mExecutor.execute(new Runnable() {
            @Override
            public void run() {

                //初始化菜品数量维护映射表
                for (DishesKindC dishesKindC : dishesKindCList) {

                    int count = dishesKindC.getDishesListId().size();

                    List<String> disheList = dishesKindC.getDishesListId();

                    List<Document> dishesCS = new ArrayList<>();

                    for (int i = 0; i < count; i++) {

                        Document dishesC = CDBHelper.getDocByID(getApplicationContext(), disheList.get(i));

                            dishesCS.add(dishesC);
                    }

                    //初始化disheKind对应的dishes实体类映射
                    dishesObjectCollection.put(dishesKindC.get_id(), dishesCS);
                }
                myApplication.setDishesKindCList(dishesKindCList);
                myApplication.setDishesObjectCollection(dishesObjectCollection);

            }
        });

    }


}