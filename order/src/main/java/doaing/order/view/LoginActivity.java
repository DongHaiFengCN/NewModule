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

import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Ordering;
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
    private GpService mGpService;
    private PrinterServiceConnection conn = null;
    private static final int MAIN_QUERY_PRINTER_STATUS = 0xfe;
    private static final int REQUEST_PRINT_LABEL = 0xfd;
    private static final int REQUEST_PRINT_RECEIPT = 0xfc;
    private Integer printnums = 1;
    //全部打印内容
    private String gPrintContentAll=null;

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

        //连接打印机服务
        registerPrinterBroadcast();
        myApplication.mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                connectBTPrinter();
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(PrinterStatusBroadcastReceiver);
        // 注销打印消息
        if (conn != null) {
            unbindService(conn); // unBindService
        }
    }

    private void connectBTPrinter()
    {
        conn = new PrinterServiceConnection();
        Intent intent = new Intent("com.gprinter.aidl.GpPrintService");
        intent.setPackage(getPackageName());
        boolean ret = bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }
    //打印机初始化
    class PrinterServiceConnection implements ServiceConnection {
        @Override
        public void onServiceDisconnected(ComponentName name) {

            Log.i(DEBUG_TAG, "onServiceDisconnected() called");
            mGpService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mGpService = GpService.Stub.asInterface(service);
            CDBHelper.setmGpService(mGpService);
        }
    }
    //打印机消息注册
    private void registerPrinterBroadcast() {
        registerReceiver(PrinterStatusBroadcastReceiver, new IntentFilter(ACTION_CONNECT_STATUS));
        // 注册实时状态查询广播
        registerReceiver(PrinterStatusBroadcastReceiver, new IntentFilter(GpCom.ACTION_DEVICE_REAL_STATUS));
        /**
         * 票据模式下，可注册该广播，在需要打印内容的最后加入addQueryPrinterStatus()，在打印完成后会接收到
         * action为GpCom.ACTION_DEVICE_STATUS的广播，特别用于连续打印，
         * 可参照该sample中的sendReceiptWithResponse方法与广播中的处理
         **/
        registerReceiver(PrinterStatusBroadcastReceiver, new IntentFilter(GpCom.ACTION_RECEIPT_RESPONSE));
    }


    private BroadcastReceiver PrinterStatusBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            //  MyLog("NavigationMain--PrinterStatusBroadcastReceiver= " + action);
            if (action.equals(ACTION_CONNECT_STATUS))//连接状态
            {
                int type = intent.getIntExtra(GpPrintService.CONNECT_STATUS, 0);
                int id = intent.getIntExtra(GpPrintService.PRINTER_ID, 0);
                MyLog.e("************************connect status " + type+"---index="+id);
            }
            else if (action.equals(GpCom.ACTION_RECEIPT_RESPONSE))//本地打印完成回调
            {

                int count = --printnums;//打印份数
                if (count > 0)
                {
                    printContentLX(gPrintContentAll, 0);
                }
                else if (count == 0)//本地打印完毕，置打印标志
                {

//                    for(Order obj:orderList)
//                    {
//                        obj.setPrintFlag(1);
//                        ModelHelper.createAndUpdate(getApplicationContext(),obj);
//                    }

                    //   speak(RoomName  + TableName + "下单成功");

//                    new Thread(new Runnable()
//                    {
//                        public void run()
//                        {
//                            print2Client();
//                        }
//                    }).start();
                }
            }
        }
    };

    private int printContentLX(String content, int printIndex)//0发送数据到打印机 成功 其它错误
    {
        int rel = 0;
        try {
            rel = mGpService.sendEscCommand(printIndex, content);
        } catch (RemoteException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return -2;
        }
        GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
        if (r != GpCom.ERROR_CODE.SUCCESS)
        {

            return -2;
        }
        else
            return 0;//把数据发送打印机成功
    }


}