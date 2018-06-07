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
import com.couchbase.lite.Expression;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bean.kitchenmanage.promotion.Promotion;
import bean.kitchenmanage.promotion.PromotionRule;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;
import doaing.order.R;
import doaing.order.module.DatabaseSource;
import doaing.order.module.IDBManager;
import doaing.order.untils.Tool;
import tools.CDBHelper;
import tools.MyBigDecimal;
import tools.MyLog;


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
    TextView tv_discounts;
    TextView type;
    TextView status;
    TextView tv_mode;
    Button submitArea;
    TextView balance;
    private float chargeTotal,chargePresentTotal;
    private boolean STATUS;
    private Array array;
    private IDBManager idbManager;
    private int mode_type;
    private  float discounts;
    private float counts;
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
        tv_discounts = findViewById(R.id.discounts);
         type = findViewById(R.id.type);
         status = findViewById(R.id.status);
        tv_mode = findViewById(R.id.mode);
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
        List<Document> documents = CDBHelper.getDocmentsByWhere(
                Expression.property("className").equalTo(Expression.string("Member"))
                        .and(Expression.property("mobile").equalTo(Expression.string(etAmountphone.getText().toString().trim()))),
                null);

        if (documents.size() == 0){
            Toast.makeText(SaleActivity.this,"会员不存在",Toast.LENGTH_SHORT).show();
            return;
        }
        Document members = documents.get(0);
        if (Tool.isNotEmpty(members)) {

            Tool.bindView(name, members.getString("name"));
            Tool.bindView(type, "充值卡");
            //充值卡金额返回支付界面
            chargeTotal = members.getFloat("chargeTotal");//本金
            chargePresentTotal = members.getFloat("chargePresentTotal");//增额
            Tool.bindView(balance, MyBigDecimal.add(chargeTotal,chargePresentTotal,2)+"元");
            STATUS = members.getBoolean("valid");
            if (STATUS) {

                Tool.bindView(status, "正常");

            } else {

                Tool.bindView(status, "已销卡");
            }

            if (members.getString("promotionId") == null){
                return;
            }
            String promotionId = members.getString("promotionId");
            Promotion promotion = CDBHelper.getObjById(promotionId, Promotion.class);

            PromotionRule promotionRule = promotion.getPromotionRuleList().get(0);

            mode_type = promotionRule.getMode();
            discounts = promotionRule.getDiscounts();
            counts = promotionRule.getCounts();
            if (mode_type == 1){
                Tool.bindView(tv_mode,"满折");
                Tool.bindView(tv_discounts,"满"+promotionRule.getCounts()+"元，打"+promotionRule.getDiscounts()+"折");
            }else if (mode_type  == 2){
                Tool.bindView(tv_mode,"满赠");
                Tool.bindView(tv_discounts,"满"+promotionRule.getCounts()+"元，赠"+promotionRule.getDiscounts());
            }else if (mode_type  == 3){
                Tool.bindView(tv_mode,"满减");
                Tool.bindView(tv_discounts,"满"+promotionRule.getCounts()+"元，减"+promotionRule.getDiscounts());
            }



        } else {

            Toast.makeText(SaleActivity.this, "会员不存在！", Toast.LENGTH_SHORT).show();

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
            if (STATUS) {
                Intent intent = new Intent();
                if(TextUtils.isEmpty(etAmountphone.getText().toString())){
                    Toast.makeText(SaleActivity.this,"手机号不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etAmountphone.getText().toString().length() != 11){
                    Toast.makeText(SaleActivity.this,"手机号错误",Toast.LENGTH_SHORT).show();
                    return;
                }
                intent.putExtra("tel", etAmountphone.getText().toString());
                intent.putExtra("chargeTotal", chargeTotal);
                intent.putExtra("chargePresentTotal",chargePresentTotal);
                intent.putExtra("mode",mode_type);
                intent.putExtra("discounts",discounts);
                intent.putExtra("counts",counts);
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
