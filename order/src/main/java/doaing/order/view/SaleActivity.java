package doaing.order.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Array;
import com.couchbase.lite.Document;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;
import doaing.order.R;
import doaing.order.module.DatabaseSource;
import doaing.order.module.IDBManager;
import doaing.order.untils.MyLog;
import doaing.order.untils.Tool;


/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/10/27
 * 修改人：donghaifeng
 * 修改时间：2017/10/27
 * 修改备注：会员折扣界面
 **/




public class SaleActivity extends AppCompatActivity implements View.OnClickListener {

    EventHandler eventHandler;
    Button submitphone;
    Button submitcode;
    EditText etAmountphone;
    EditText etcode;
    TextView name;
    TextView number;
    TextView type;
    TextView status;
    TextView discount;
    Button submitArea;
    TextView balance;

    private int STATUS;
    private Array array;
    private IDBManager idbManager;

    private int CardTypeFlag;

    private int disrate = 0;
    private float remainder;

    private ArrayList DishesIdList = (ArrayList<? extends Parcelable>) new ArrayList();
    private InputMethodManager inputMethodManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        setContentView(R.layout.activity_sale);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
      //  setData();
        // 创建EventHandler对象
        eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, final int result, final Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    MyLog.e("回调完成");
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交并验证验证码成功！
                        //读取数据库操作

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                setData();

                            }
                        });


                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(SaleActivity.this, "获取验证码成功！", Toast.LENGTH_SHORT).show();
                            }
                        });

                        //  MyLog.e("获取验证码成功");
                    }

                } else if (data instanceof Throwable) {


                    try {
                        ((Throwable) data).printStackTrace();
                        Throwable throwable = (Throwable) data;

                        JSONObject object = new JSONObject(throwable.getMessage());
                        final String des = object.optString("detail");
                        if (!TextUtils.isEmpty(des)) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Toast.makeText(SaleActivity.this, des, Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }
                    } catch (Exception e) {
                        SMSLog.getInstance().w(e);
                    }
                }

            }

        };

        SMSSDK.registerEventHandler(eventHandler);

    }

    private void initView() {
         submitphone = findViewById(R.id.submitphone);
         submitcode = findViewById(R.id.submitcode);
         etAmountphone = findViewById(R.id.etAmountphone);
         etcode = findViewById(R.id.etcode);
         name = findViewById(R.id.name);
         number = findViewById(R.id.number);
         type = findViewById(R.id.type);
         status = findViewById(R.id.status);
         discount = findViewById(R.id.discount);
         submitArea = findViewById(R.id.submit_area);
         balance = findViewById(R.id.balance);

        submitArea.setOnClickListener(this);
        submitphone.setOnClickListener(this);
        submitcode.setOnClickListener(this);
    }

/*

*
     * 绑定数据到控件
*/


    public void setData() {


        etcode.setCursorVisible(false);

        //获取会员信息
          Document members = idbManager.getMembers(etAmountphone.getText().toString());

        if (Tool.isNotEmpty(members)) {

            Tool.bindView(name, members.getString("name"));

            Tool.bindView(number, members.getString("cardNum"));

            if (!TextUtils.isEmpty(members.getString("cardTypeId"))) {

                Document card = idbManager.getCard(members.getString("cardTypeId"));

                //获取卡类型数据
                if (members != null) {


                    //cardType 1,折扣卡,2,充赠卡

                    CardTypeFlag = card.getInt("cardType");

                    if (CardTypeFlag == 1) {

                        Tool.bindView(type, "折扣卡");

                        disrate = card.getInt("disrate");

                        //充值卡金额返回支付界面
                        remainder = members.getFloat("remainder");
                        Tool.bindView(balance,remainder+"元");


                        array = card.getArray("cardDishesKindList");

                        List<Object> list = array.toList();

                        //便利当前会员包含的会员菜类
                        for (int i = 0; i < list.size(); i++) {

                            HashMap c = (HashMap) list.get(i);

                            //菜类启用

                            if ("1".equals(c.get("ischecked").toString())) {

                                List<HashMap> cardDishesList = (List<HashMap>) c.get("cardDishesList");

                                //遍历当前菜类下的菜品

                                for (HashMap cardDishesC : cardDishesList) {

                                    //当前菜品启用

                                    if ("1".equals(cardDishesC.get("ischecked").toString())) {

                                        DishesIdList.add(cardDishesC.get("dishesId").toString());


                                    }

                                }

                            }
                        }

                    } else if (CardTypeFlag == 2) {


                        Tool.bindView(type, "充值卡");

                        //充值卡金额返回支付界面
                        remainder = members.getFloat("remainder");
                        Tool.bindView(balance,remainder+"元");

                    }

                    Tool.bindView(discount, card.getInt("disrate") + "/折");

                }

            }

            STATUS = members.getInt("status");
            if (STATUS == 1) {

                Tool.bindView(status, "正常");

            } else if (STATUS == 2) {

                Tool.bindView(status, "已挂失");

            } else if (STATUS == 3) {

                Tool.bindView(status, "已销卡");

            }

        } else {

            Toast.makeText(SaleActivity.this, "用户不存在！", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.submitphone) {
            if (TextUtils.isEmpty(etAmountphone.getText().toString())) {

                etAmountphone.setError("号码不能为空");

            } else {

                SMSSDK.getVerificationCode("86", etAmountphone.getText().toString());

                if (inputMethodManager.isActive()) {
                    inputMethodManager.hideSoftInputFromWindow(SaleActivity.this.getCurrentFocus().getWindowToken(), 0);
                }
            }


        } else if (i == R.id.submitcode) {
            if (TextUtils.isEmpty(etcode.getText().toString())) {

                etcode.setError("验证码不能为空！");

            } else {

                SMSSDK.submitVerificationCode("+86", etAmountphone.getText().toString(), etcode.getText().toString());
                if (inputMethodManager.isActive()) {
                    inputMethodManager.hideSoftInputFromWindow(SaleActivity.this.getCurrentFocus().getWindowToken(), 0);
                }
                //etcode.setCursorVisible(false);
            }

        }else if (i == R.id.submit_area) {
            if (STATUS == 1) {

                Intent intent = new Intent();

                //设置会员卡类型

                intent.putExtra("CardTypeFlag", CardTypeFlag);

                if(TextUtils.isEmpty(etAmountphone.getText().toString())){

                    etAmountphone.setError("数据为空");

                    return;

                }

                intent.putExtra("tel", etAmountphone.getText().toString());
                // intent.putExtra("tel", "8888");
                //折扣卡返回折扣率及支持的菜品列表

                if (CardTypeFlag == 1) {

                    MyLog.e("折扣");

                    intent.putExtra("disrate", disrate);

                    intent.putParcelableArrayListExtra("DishseList", DishesIdList);


                } else if (CardTypeFlag == 2) {


                    MyLog.e("充值");

                    intent.putExtra("remainder", remainder);

                }


                //返回支持打折菜品id

                setResult(RESULT_OK, intent);

                finish();

            } else {
                Toast.makeText(SaleActivity.this, "当前会员无效", Toast.LENGTH_SHORT).show();
            }
        } else {
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();

                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
