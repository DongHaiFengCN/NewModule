package doaing.order.view;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDictionary;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Ordering;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.gprinter.io.PortParameters;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import bean.kitchenmanage.member.Members;
import bean.kitchenmanage.order.CheckOrder;
import bean.kitchenmanage.order.Goods;
import bean.kitchenmanage.order.HangInfo;
import bean.kitchenmanage.order.Order;
import bean.kitchenmanage.order.PayDetail;
import bean.kitchenmanage.order.PromotionDiscountDetail;
import bean.kitchenmanage.promotion.Promotion;
import bean.kitchenmanage.promotion.PromotionDishes;
import bean.kitchenmanage.promotion.PromotionDishesKind;
import bean.kitchenmanage.promotion.PromotionRule;
import bean.kitchenmanage.qrcode.Qrcode;
import bean.kitchenmanage.table.Area;
import bean.kitchenmanage.table.Table;
import doaing.mylibrary.MyApplication;
import doaing.order.R;
import doaing.order.module.IDBManager;
import doaing.order.untils.BluetoothUtil;
import doaing.order.untils.MyBigDecimal;
import doaing.order.untils.ProgressBarasyncTask;
import doaing.order.untils.Tool;
import doaing.order.view.adapter.ActionListAdapter;
import doaing.order.view.adapter.MemberDishesListAdapter;
import tools.CDBHelper;
import tools.MyLog;
import tools.ToolUtil;

import static tools.Method.getFormatDate;
import static tools.Method.getNianDate;

/**
 * @author 董海峰
 * @date 2017/10/25
 */


public class PayActivity extends AppCompatActivity implements View.OnClickListener {

    String id;
    TextView factTv;
    TextView discountTv;
    TextView totalTv;
    TextView associatorTv;
    TextView actionTv;
    LinearLayout associator;
    LinearLayout discount;
    LinearLayout action;
    ImageView ivalipay;
    ImageView ivwechat;
    ImageView cash;
    ImageView gzIm;
    TextView tableNumber;
    public static boolean isGuaZ = false;
    public static String Margin = "";
    EditText nameEt;
    EditText contactWayEt;
    List<PromotionRule> promotionRuleCList;
    private float copy;
    private AlertDialog.Builder dialog;
    private AlertDialog dg;
    private Bitmap alipayBitmap = null;
    private Bitmap wechatBitmap = null;
    private static final int DISTCOUNT = 0;
    private static final int SALE = 1;
    private float total = 0.0f;
    private List<Document> orderList;
    private IDBManager idbManager;
    private MyApplication myApplication;
    private int isDiscount = 0;//1,营销、2会员、3、抹零
    //折扣率
    private int disrate;
    private boolean memberLogs =true;
    private Members members1;
    private boolean isMember = true;
    private int mode;
    private float discountDetail = 0;
    private float chargeTotal,chargePresentTotal,indexCharge,indexChargePresent;
    private List<PromotionDiscountDetail> promotionDiscountDetailList = new ArrayList<>();
    private List<Promotion> promotionCList;
    private List<PromotionRule> promotionRulesList;
    private float subCopy;
    private float discountCopy;
    private List<Float> discountsList = new ArrayList<>();
    private List<Float> subList = new ArrayList<>();
    private Table tableC;
    List<Goods> orderDishesList = new ArrayList<>();
    private CheckOrder checkOrder = new CheckOrder();
    private MutableDocument checkOrderDoc = new MutableDocument("CheckOrder."+ToolUtil.getUUID());
    //每增加一种支付方式创建一个支付详情，例如充值卡余额不足，剩下的部分用的现金。
    private List<PayDetail> payDetailList = new ArrayList<>();
    private float allTotal = 0;
    private TextView pay_marketing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pay);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        //取消分割阴影
        getSupportActionBar().setElevation(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0);
        }

        myApplication = (MyApplication) getApplication();
        //获取餐桌编号
        tableC = myApplication.getTable_sel_obj();
        Area areaCs = CDBHelper.getObjById(tableC.getAreaId(), Area.class);

        tableNumber.setText(areaCs.getName() + "桌/牌:" + tableC.getName());

        getAll();

        if (promotionCList.size() > 0) {

            //当前时间段有活动，显示活动的个数
            actionTv.setVisibility(View.VISIBLE);

            actionTv.setText(promotionCList.size() + "");
        }


        //创建打印dialog
        dialog = new AlertDialog.Builder(PayActivity.this);
        dialog.setView(getLayoutInflater().inflate(R.layout.view_print_dialog, null)).create();

        StringBuilder stringBuilder = new StringBuilder("实际支付：");

        //获取包含桌号xx的所有订单
        List<Order> orderCList = CDBHelper.getObjByWhere( Expression.property("className")
                        .equalTo(Expression.string("Order"))
                        .and(Expression.property("tableId").equalTo(Expression.string(tableC.getId())))
                        .and(Expression.property("state").equalTo(Expression.intValue(1)))
                , Ordering.property("createdTime").ascending()
                , Order.class);
        Log.e("DOAING", "订单个数：" + orderCList.size() + "");
        List<String> orderId = new ArrayList<>();
        MutableArray array = new MutableArray();
        for (Order orderC : orderCList) {
            Log.e("DOAING", "订单价格：" + orderC.getTotalPrice() + "");
            if (orderC.getOrderType() == 0){
                total = MyBigDecimal.add(total, orderC.getTotalPrice(), 1);
            }
            orderId.add(orderC.getId());
            array.addValue(orderC.getId());
            //获取当前订单下goods集合下所有的菜品
            for (Goods o : orderC.getGoodsList()) {

                orderDishesList.add(o);
            }

        }
        if (orderId.size() > 0){
            checkOrder.setOrderId(orderId);
            checkOrderDoc.setArray("orderId",array);
        }
        //显示原价
        totalTv.setText(total + "");
        //显示操作后价格
        factTv.setText(stringBuilder.append(total));
    }

    private void initView() {
        factTv = findViewById(R.id.fact_tv);
        discountTv = findViewById(R.id.discount_tv);
        totalTv = findViewById(R.id.total_tv);
        associatorTv = findViewById(R.id.associator_tv);
        actionTv = findViewById(R.id.action_tv);
        associator = findViewById(R.id.associator);
        discount = findViewById(R.id.discount);
        action = findViewById(R.id.action);
        ivalipay = findViewById(R.id.ivalipay);
        ivwechat = findViewById(R.id.ivwechat);
        cash = findViewById(R.id.cash);
        pay_marketing = findViewById(R.id.pay_marketing);
        tableNumber = findViewById(R.id.table_number);
        findViewById(R.id.gz).setOnClickListener(this);
        findViewById(R.id.tg).setOnClickListener(this);
        associator.setOnClickListener(this);
        discount.setOnClickListener(this);
        action.setOnClickListener(this);
        ivalipay.setOnClickListener(this);
        ivwechat.setOnClickListener(this);
        cash.setOnClickListener(this);
        findViewById(R.id.bankcard).setOnClickListener(this);
    }
/*
*
     * 准备所有的数据
     *
     * @param
*/
    private void getAll() {

        //支付宝收款码,网络获取**********
        String alipayId;

        //微信支付
        String wechatId;

        List<Qrcode> qrcodeList = CDBHelper.getObjByClass( Qrcode.class);

        if (!qrcodeList.isEmpty()) {


            alipayId = qrcodeList.get(0).getZfbUrl();
            wechatId = qrcodeList.get(0).getWxUrl();

            if (alipayId != null && !alipayId.isEmpty()) {

                alipayBitmap = encodeAsBitmap(alipayId);
            }
            if (wechatId != null && !wechatId.isEmpty()) {

                wechatBitmap = encodeAsBitmap(wechatId);
            }


            //   MyLog.e(wechatId);
            //转化二维码

        }


        //营销方式

        promotionCList = CDBHelper.getObjByClass( Promotion.class);
        //活动
        promotionRulesList = CDBHelper.getObjByClass(PromotionRule.class);

        Iterator iterator = promotionCList.iterator();

        //筛选活动时间
        while (iterator.hasNext()) {

            Promotion promotion = (Promotion) iterator.next();
            String start = promotion.getStartTime();
            String end = promotion.getEndTime();
            start = start.replaceAll("-", "");
            end = end.replaceAll("-", "");

            int s = Integer.valueOf(start);
            int e = Integer.valueOf(end);
            Date d = new Date();
            System.out.println(d);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String dateNowStr = sdf.format(d);
            int now = Integer.valueOf(dateNowStr);

            if (s > now || now > e) {

                iterator.remove();

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pay, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                turnMainActivity();

                finish();

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

/*
*
     * onActivityResult的方法获取
*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //账单抹零返回参数
        if (requestCode == DISTCOUNT && resultCode == RESULT_OK) {


            turnDiscount(data);


        }
        //会员账单返回
        else if (requestCode == SALE && resultCode == RESULT_OK) {

            turnRechange(data);

        }
    }

/*
*
     * 账单减免功能
     *
     * @param data
*/
    private void turnDiscount(Intent data) {


        //处理完成的总价
        total = data.getFloatExtra("Total", 0);
        Log.e("moling",""+total);
        //展示差额
        discountTv.setText("- " + data.getFloatExtra("Margin", 0) + "元");
        //设置抹零支付细节
        setPayDetail(7, data.getFloatExtra("Margin", 0));
        Margin = ""+data.getFloatExtra("Margin", 0);
        //界面展示实际处理后的价格
        factTv.setText("实际支付：" + total + "元");
        isDiscount = 3;
    }
/*
*
     * 充值卡扣款功能
     *
     * @param data
*/
    private void turnRechange(Intent data) {
        total = Float.parseFloat(totalTv.getText().toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.view_payactivity_memberdishes_rechange_dialog, null);
        String tel = data.getStringExtra("tel");
        //当前卡中余额
        chargeTotal = data.getFloatExtra("chargeTotal", 0f);
        //当前卡中赠送余额
        chargePresentTotal= data.getFloatExtra("chargePresentTotal",0f);
        final float r = MyBigDecimal.add(chargePresentTotal,chargeTotal,2);
        mode = data.getIntExtra("mode",0);

        final  float discounts = data.getFloatExtra("discounts",0f);
        final float counts = data.getFloatExtra("counts",0f);

        TextView remainderTv = view.findViewById(R.id.remainder_tv);

        remainderTv.setText(r + "");

        TextView rechangepayTv = view.findViewById(R.id.rechangepay_tv);

        rechangepayTv.setText(total + "");
        if (total >= counts) {
            if (mode == 1) {
                discountDetail = MyBigDecimal.mul(total, 1 - MyBigDecimal.div(discounts, 100, 1), 2);
                //会员打折优惠
                total = MyBigDecimal.mul(total, MyBigDecimal.div(discounts, 100, 1), 2);

            } else if (mode == 3) {
                discountDetail = discounts;
                //会员满减优惠
                total = MyBigDecimal.sub(total, discounts, 2);
            } else if (mode == 2) {
                Toast.makeText(PayActivity.this, "消费满"+counts
                        +"元，可赠送"+discounts
                        +"元券。",Toast.LENGTH_SHORT).show();

            }
        }
        builder.setTitle("扣款明细表");
        builder.setView(view);
        builder.setPositiveButton("确定扣款", null);

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


            }
        });

        final AlertDialog alertDialog = builder.show();

        //获取会员信息
        List<Document> documents = CDBHelper.getDocmentsByWhere(
                Expression.property("className").equalTo(Expression.string("Member"))
                        .and(Expression.property("mobile").equalTo(Expression.string(tel))),
                null);

        final MutableDocument members = documents.get(0).toMutable();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (r >= total) {//余额扣款
                    memberLogs = true;
                    //会员卡支付
                    setPayDetail(PayDetail.PAYPYTE_MEMBER, total);
                    if (chargeTotal >= total){
                        //更新本金余额
                        members.setFloat("chargeTotal", chargeTotal - total);
                        CDBHelper.saveDocument(members);
                        indexCharge = total;
                    }else if (chargeTotal == 0){
                        members.setFloat("chargePresentTotal",chargePresentTotal - total);
                        CDBHelper.saveDocument(members);
                        indexChargePresent = total;//增额扣款
                    } else {
                        total = MyBigDecimal.sub(total,chargeTotal,2);
                        indexCharge = chargeTotal;
                        indexChargePresent = total;
                        members.setFloat("chargeTotal",0);
                        members.setFloat("chargePresentTotal",chargePresentTotal - total);
                        CDBHelper.saveDocument(members);
                    }
                    Toast.makeText(PayActivity.this, "扣款成功！", Toast.LENGTH_SHORT).show();
                    members1 = CDBHelper.getObjById(members.getId(),Members.class);
                    checkOrderDoc.setValue("member",getDictionary());
                    if (members1.getPromotionId() != null) {
                        ObjectMapper mapper = new ObjectMapper();
                        //营销对象转换Map
                        Map<String, Object> map1 = mapper.convertValue(CDBHelper.getObjById(members1.getPromotionId(), Promotion.class), Map.class);
                        checkOrderDoc.setValue("promotion", map1);
                    }

                    if (mode == 1){
                        setPromotionDiscountDetail(1,discountDetail);
                    }else if (mode == 2){
                        setPromotionDiscountDetail(2,discountDetail);
                    }
                    //提交checkorder
                    try {
                        submitCheckOrderDoc();

                    } catch (CouchbaseLiteException e) {
                        e.printStackTrace();
                    }
                    setMemberLogs();
                    alertDialog.dismiss();


                } else if (total > r && r > 0) {

                    //支付价格大于卡内余额，且卡内余额大于零，显示使用卡内全部余额
                    AlertDialog.Builder a = new AlertDialog.Builder(PayActivity.this);
                    a.setTitle("余额不足！");
                    a.setMessage("使用卡内全部" + r + "元？");
                    a.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            memberLogs = false;
                            //会员剩余金额清零
                            members.setFloat("chargePresentTotal", 0f);
                            CDBHelper.saveDocument(members);
                            Toast.makeText(PayActivity.this,"扣款成功，请支付剩下余额。请选择支付方式",Toast.LENGTH_SHORT).show();
                            members1 = CDBHelper.getObjById(members.getId(),Members.class);
                            indexChargePresent = r;
                            //消费支付细节 6会员消费,计算剩余部分
                            total = total - r;
                            setPayDetail(PayDetail.PAYPYTE_MEMBER, r);
                            factTv.setText("实际支付：" + total + "元");
                            alertDialog.dismiss();

                        }
                    });
                    a.setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    a.show();


                } else {
                    members1 = CDBHelper.getObjById(members.getId(),Members.class);
                    isMember = false;
                    memberLogs = false;
                    if (mode == 1){
                        setPromotionDiscountDetail(1,discountDetail);
                    }else if (mode == 2){
                        setPromotionDiscountDetail(2,discountDetail);
                    }
                    Toast.makeText(PayActivity.this, "余额不足，请充值或者选择其他支付方式！", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
                isDiscount = 2;
                allTotal = total;
            }

        });
    }

    /*
     *
     * 营销优惠
     *
     * @param type 优惠类型
     * @param pay  优惠金额
     */
    private void setPromotionDiscountDetail(int type, float pay) {

        //营销优惠
        PromotionDiscountDetail p = new PromotionDiscountDetail();
        p.setType(type);
        p.setDiscounts(pay);
        promotionDiscountDetailList.add(p);

    }

    //生成会员的副本
    private MutableDictionary getDictionary(){

        MutableDictionary dictionary = new MutableDictionary();
        dictionary.setString("id",members1.getId());
        dictionary.setString("name",members1.getName());
        dictionary.setString("mobile",members1.getMobile());
        dictionary.setFloat("chargeTotal",members1.getChargeTotal());
        dictionary.setFloat("chargePresentTotal",members1.getChargePresentTotal());
        dictionary.setString("promotionId",members1.getPromotionId());
        return dictionary;
    }

    /*
    *
         * 打印账单

    */
    private void printOrder() {

        ProgressBarasyncTask progressBarasyncTask = new ProgressBarasyncTask(PayActivity.this);
        progressBarasyncTask.setDate(checkOrder);
        progressBarasyncTask.execute();


    }

    //携带参数返回到MainActivity
    public void turnMainActivity() {


        setResult(RESULT_OK, null);

        finish();
    }

    //跳转主界面
    public void turnDesk() {

        if (ifChangeTable()) {
            Table obj = myApplication.getTable_sel_obj();
            obj.setLastCheckOrderId(id);
            obj.setState(0);

            CDBHelper.createAndUpdate( tableC);
        } else {
            Toast.makeText(getApplicationContext(), "有未买单信息，不能改变桌位状态", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(PayActivity.this, DeskActivity.class);
        startActivity(intent);
        finish();
    }
    /*
    *
         * 跳转抹零功能界面
    */
    private void turnDiscount() {
        if (allTotal == 0) {
            total = Float.parseFloat(totalTv.getText().toString());
        }else{
            total = allTotal;
        }
        Intent discount = new Intent();
        discount.setClass(PayActivity.this, DiscountActivity.class);
        discount.putExtra("Total", total);
        startActivityForResult(discount, DISTCOUNT);
    }

/*
     * 跳转会员折扣界面
*/
    private void turnSale() {

        Intent sale = new Intent();
        sale.setClass(PayActivity.this, SaleActivity.class);
        startActivityForResult(sale, SALE);
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.associator) {

            if (isDiscount == 0){
                turnSale();
            }else{
                if(isDiscount != 2){

                    AlertDialog.Builder builder = new AlertDialog.Builder(PayActivity.this);
                    builder.setTitle("是否切换会员活动！！！");
                    builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            total = Float.parseFloat(totalTv.getText().toString());
                            discountTv.setText("");
                            pay_marketing.setText("活动");
                            factTv.setText(""+total);
                            turnSale();

                        }
                    });
                    builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }else{
                    total = Float.parseFloat(totalTv.getText().toString());
                    turnSale();
                }
            }


        } else if (i == R.id.discount) {


            //抹零
            turnDiscount();


        } else if (i == R.id.action) {
            //活动
            if (isDiscount == 0){
                setAction();
            }else{
                if(isDiscount != 1){

                    AlertDialog.Builder builder = new AlertDialog.Builder(PayActivity.this);
                    builder.setTitle("是否切换营销活动！！！");
                    builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            discountTv.setText("");
                            factTv.setText(""+total);
                            setAction();
                        }
                    });
                    builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }else{
                    setAction();
                }
            }

        } else if (i == R.id.ivalipay) {
            //支付宝支付

            if (alipayBitmap != null) {

                View alipayView = getLayoutInflater().inflate(R.layout.view_alipay_dialog, null);
                ImageView alipayIv = alipayView.findViewById(R.id.encode);
                alipayIv.setImageBitmap(alipayBitmap);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(PayActivity.this);
                alertDialog.setView(alipayView);
                alertDialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDialog.setNegativeButton("确定支付", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        setPayDetail(4, total);

                        try {
                            submitCheckOrderDoc();
                        } catch (CouchbaseLiteException e) {
                            e.printStackTrace();
                        }
                        isGuaZ = false;

                    }
                });

                alertDialog.show();
            } else {
                Toast.makeText(PayActivity.this, "没有添加二维码", Toast.LENGTH_SHORT).show();
            }

        } else if (i == R.id.ivwechat) {//微信支付

            if (wechatBitmap != null) {

                View wechatView = getLayoutInflater().inflate(R.layout.view_wechat_dialog, null);
                ImageView wechatIv = wechatView.findViewById(R.id.encode);
                wechatIv.setImageBitmap(wechatBitmap);

                AlertDialog.Builder wechatDialog = new AlertDialog.Builder(PayActivity.this);
                wechatDialog.setView(wechatView);
                wechatDialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                wechatDialog.setNegativeButton("确定支付", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        setPayDetail(3, total);

                        try {
                            submitCheckOrderDoc();
                        } catch (CouchbaseLiteException e) {
                            e.printStackTrace();
                        }
                        isGuaZ = false;

                    }
                });

                wechatDialog.show();

            } else {
                Toast.makeText(PayActivity.this, "没有添加二维码", Toast.LENGTH_SHORT).show();
            }


        } else if (i == R.id.cash) {
            //现金支付

            AlertDialog.Builder cashDialog = new AlertDialog.Builder(PayActivity.this);
            cashDialog.setTitle("现金支付");
            cashDialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            cashDialog.setNegativeButton("确定支付", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {


                    setPayDetail(1, total);

                    try {
                        submitCheckOrderDoc();
                    } catch (CouchbaseLiteException e) {
                        e.printStackTrace();
                    }
                    isGuaZ = false;

                }
            });

            cashDialog.show();


        } else if (i == R.id.bankcard) {//银行卡支付

            AlertDialog.Builder bankDialog = new AlertDialog.Builder(PayActivity.this);
            bankDialog.setTitle("银行卡支付");

            bankDialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            bankDialog.setNegativeButton("确定支付", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    setPayDetail(2, total);

                    try {
                        submitCheckOrderDoc();
                    } catch (CouchbaseLiteException e) {
                        e.printStackTrace();
                    }
                    isGuaZ = false;

                }
            });

            bankDialog.show();


        } else if (i == R.id.gz) {
            //TODO 挂账信息
            View view1 = getLayoutInflater().inflate(R.layout.view_gz_dialog, null);
            nameEt = view1.findViewById(R.id.order_name_tv);
            contactWayEt = view1.findViewById(R.id.order_contactway_tv);

            AlertDialog.Builder gzDialog = new AlertDialog.Builder(PayActivity.this);
            gzDialog.setView(view1);
            gzDialog.setTitle("挂帐");
            gzDialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            gzDialog.setNegativeButton("确定支付", null);

            final AlertDialog alertDialog = gzDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (nameEt.getText().length() == 0) {
                        nameEt.setError("名字能为空！");
                    } else if (contactWayEt.getText().length() == 0) {
                        contactWayEt.setError("联系方式不能为空！");

                    } else {
                        HangInfo hangInfo = new HangInfo();
                        hangInfo.setChannelId(myApplication.getCompany_ID());
                        hangInfo.setClassName("HangInfo");
                        hangInfo.setName(nameEt.getText().toString());
                        hangInfo.setMobile(contactWayEt.getText().toString());
                        Database database = CDBHelper.getDatabase();
                        for (String id : checkOrder.getOrderId()) {
                            Document document = database.getDocument(id);
                            MutableDocument mutableDocument = document.toMutable();
                            mutableDocument.setInt("state", 0);
                            try {
                                database.save(mutableDocument);
                            } catch (CouchbaseLiteException e) {
                                e.printStackTrace();
                            }
                        }
                        ObjectMapper mapper = new ObjectMapper();
                        Map<String ,Object> map = mapper.convertValue(hangInfo, Map.class);
                        checkOrderDoc.setValue("hangInfo",map);
                        setPayDetail(PayDetail.PAYPYTE_HANG, -total);
                        total = 0;
                        try {
                            submitCheckOrderDoc();
                        } catch (CouchbaseLiteException e) {
                            e.printStackTrace();
                        }
                        isGuaZ = true;
                    }
                    alertDialog.dismiss();
                }
            });
        }else if (i == R.id.tg){
            AlertDialog.Builder tgDialog = new AlertDialog.Builder(PayActivity.this);
            tgDialog.setTitle("团购支付");

            tgDialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            tgDialog.setNegativeButton("确定支付", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {


                    setPayDetail(11, total);

                    try {
                        submitCheckOrderDoc();
                    } catch (CouchbaseLiteException e) {
                        e.printStackTrace();
                    }
                    isGuaZ = false;

                }
            });

            tgDialog.show();
        }
    }

    /*
     *
     * 活动扣款
     */
    private void setAction() {
        total = Float.parseFloat(totalTv.getText().toString());
        final Promotion[] promotion = {null};
        List<Promotion> promotions = new ArrayList<>();
        for (Promotion p : promotionCList){
            if (p.getPromotionType() == 2){
                promotions.add(p);
            }
        }
        //加载活动
        View view = LayoutInflater.from(PayActivity.this).inflate(R.layout.view_payactivity_action_dialog,null);
        ListView listView = view.findViewById(R.id.action_lv);
        final TextView showTv = view.findViewById(R.id.show_tv);

        final ActionListAdapter adapter = new ActionListAdapter(promotions,PayActivity.this);
        listView.setAdapter(adapter);

        AlertDialog.Builder marketingDialog = new AlertDialog.Builder(PayActivity.this);
        marketingDialog.setView(view);

        marketingDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        marketingDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (promotion[0] != null) {

                    if (subCopy+discountCopy == 0f){
                        Toast.makeText(PayActivity.this,"不满足活动条件！",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (discountsList.size() != 0) {
                        discountCopy = Collections.max(discountsList);
                    }
                    if (subList.size() != 0){
                        subCopy = Collections.max(subList);
                    }


                    total =  MyBigDecimal.sub(total,subCopy+discountCopy,1);
                    factTv.setText("" + total);
                    isDiscount = 1;
                    pay_marketing.setText(promotion[0].getName());
                    if (discountCopy != 0f){
                        setPromotionDiscountDetail(1,discountCopy);
                    }
                    if (subCopy != 0f){
                        setPromotionDiscountDetail(2,subCopy);
                    }
                    allTotal = total;
                } else{
                    Toast.makeText(PayActivity.this, "请选择活动再按确定", Toast.LENGTH_SHORT).show();
                }
            }
        });
        marketingDialog.show();
        adapter.setCallback(new ActionListAdapter.Callback() {
            @Override
            public void click(int p,boolean flag) {
                //规则详情
                promotion[0] = promotionCList.get(p);

                //初始化优惠金额
                subCopy = 0f;
                discountCopy = 0f;
                //每次点击进来清空打折和满减金额
                discountsList.clear();
                subList.clear();
                //每次点击进来清空优惠方式
                promotionDiscountDetailList.clear();
                for (int ps = 0; ps < promotion[0].getPromotionRuleList().size(); ps++ ){
                    PromotionRule promotionRule = promotion[0].getPromotionRuleList().get(ps);
                    if (promotionRule.getMode() == 1){
                        discountCopy = 0f;

                        List<PromotionDishes> allDishes = new ArrayList<>();
                        List<PromotionDishesKind> dishesKindList ;

                        dishesKindList = promotion[0].getPromotionDishesKindList();

                        if (dishesKindList != null) {

                            for (int i = 0; i < dishesKindList.size(); i++) {

                                PromotionDishesKind promotionDishesKindC = dishesKindList.get(i);

                                //启用状态下
                                if (promotionDishesKindC.isChecked()) {

                                    List<PromotionDishes> list = promotionDishesKindC.getPromotionDishesList();
                                    //菜品不为空
                                    if (list != null) {

                                        for (int j = 0; j < list.size(); j++) {

                                            PromotionDishes promotionDishesC = list.get(j);

                                            //设置启用
                                            if (promotionDishesC.isChecked()) {

                                                //符合条件的全部筛选出来
                                                allDishes.add(promotionDishesC);

                                            }
                                        }
                                    }
                                }
                            }

                            //计算折扣菜品价格

                            for (int j = 0; j < orderDishesList.size(); j++) {
                                //获取订单下goods的菜品名称
                                Goods h = orderDishesList.get(j);
                                //遍历所活动菜品找匹配的打折菜品
                                for (int i = 0; i < allDishes.size(); i++) {
                                    //确保绝对是自点菜
                                    if (h.getGoodsType() == 3 && h.getDishesId() == null) {
                                        discountCopy = MyBigDecimal.add(discountCopy, MyBigDecimal.mul(h.getPrice(), h.getDishesCount(), 1), 1);// h.getAllPrice();
                                        break;
                                    } else if (h.getDishesId() != null && h.getDishesId().equals(allDishes.get(i).getDishesId())) {//找到打折的
                                        discountCopy = MyBigDecimal.add(discountCopy, MyBigDecimal.mul(h.getPrice(), h.getDishesCount(), 1), 1);// h.getAllPrice();
                                        break;
                                    } else if (h.getDishesId() == null) {
                                        new NullPointerException("菜品DishesId为空");
                                        break;
                                    }
                                }
                            }
                            if (discountCopy >= promotionRule.getCounts()) {
                                //折扣比率
                                disrate = promotionRule.getDiscounts();
                                //减去的价格
                                String f1;
                                f1 = MyBigDecimal.div(disrate + "", 100 + "", 3);
                                discountCopy = MyBigDecimal.mul(discountCopy, (1 - Float.valueOf(f1)), 3);
                                discountsList.add(discountCopy);
                            } else {

                                discountCopy = 0f;

                            }
                        }

                    }else if (promotionRule.getMode() == 3){
                        subCopy = 0f;
                        List<PromotionDishes> allDishes = new ArrayList<>();
                        List<PromotionDishesKind> dishesKindList ;

                        dishesKindList = promotion[0].getPromotionDishesKindList();

                        if (dishesKindList != null) {

                            for (int i = 0; i < dishesKindList.size(); i++) {

                                PromotionDishesKind promotionDishesKindC = dishesKindList.get(i);

                                //启用状态下
                                if (promotionDishesKindC.isChecked()) {

                                    List<PromotionDishes> list = promotionDishesKindC.getPromotionDishesList();
                                    //菜品不为空
                                    if (list != null) {

                                        for (int j = 0; j < list.size(); j++) {

                                            PromotionDishes promotionDishesC = list.get(j);

                                            //设置启用
                                            if (promotionDishesC.isChecked()) {

                                                //符合条件的全部筛选出来
                                                allDishes.add(promotionDishesC);

                                            }
                                        }
                                    }
                                }
                            }

                            //计算折扣菜品价格

                            for (int j = 0; j < orderDishesList.size(); j++) {
                                //获取订单下goods的菜品名称
                                Goods h = orderDishesList.get(j);
                                //遍历所活动菜品找匹配的打折菜品
                                for (int i = 0; i < allDishes.size(); i++) {
                                    //确保绝对是自点菜
                                    if (h.getGoodsType() == 3 && h.getDishesId() == null) {
                                        subCopy = MyBigDecimal.add(subCopy, MyBigDecimal.mul(h.getPrice(), h.getDishesCount(), 1), 1);// h.getAllPrice();
                                        break;
                                    } else if (h.getDishesId() != null && h.getDishesId().equals(allDishes.get(i).getDishesId())) {//找到打折的
                                        subCopy = MyBigDecimal.add(subCopy, MyBigDecimal.mul(h.getPrice(), h.getDishesCount(), 1), 1);// h.getAllPrice();
                                        break;
                                    } else if (h.getDishesId() == null) {
                                        new NullPointerException("菜品DishesId为空");
                                        break;
                                    }
                                }
                            }
                            if (subCopy >= promotionRule.getCounts()) {
                                //满减的价格
                                subCopy = promotionRule.getDiscounts();
                                subList.add(subCopy);
                            } else {

                                subCopy = 0f;

                            }
                        }

                    }else if (promotionRule.getMode() == 2){
                        if (total >= promotionRule.getCounts()){
                            Toast.makeText(PayActivity.this,"消费已满"+promotionRule.getCounts()
                                    +"元，可赠送"+promotionRule.getDiscounts()
                                    +"元券。",Toast.LENGTH_SHORT).show();
                        }

                    }
                }
                if (flag){
                    //展示当前的减免
                    showTv.setText("减免"+(subCopy+discountCopy)+ "元");
                }else{
                    //展示当前的减免
                    showTv.setText("减免xx元");
                }

            }

        });


    }



    private void associatorNotDisplay() {
        //设置会员按钮不可用
        associator.setEnabled(false);

        //未设置会员的优惠信息时展示不可用
        if (TextUtils.isEmpty(associatorTv.getText().toString())) {

            associatorTv.setText("不可选");
        }
    }

/*
*
     * 支付细节设置
     *
     * @param type 支付类型
     * @param pay  支付的钱数
*/


    private void setPayDetail(int type, float pay) {

        //支付细节
        PayDetail p = new PayDetail();
        p.setPayTypes(type);
        p.setSubtotal(pay);
        p.setCreatedTime(getFormatDate());
        payDetailList.add(p);

    }

     /*
    *
         * 提交结账信息
         * <p>
         * 设置order的状态为买单
    */


    public void submitCheckOrderDoc() throws CouchbaseLiteException {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        checkOrderDoc.setString("channelId",myApplication.getCompany_ID());
        checkOrderDoc.setString("checkTime",formatter.format(date));
        checkOrderDoc.setString("className","CheckOrder");
        checkOrderDoc.setFloat("lastPay",total);
        MyLog.e("total---"+total);
        checkOrderDoc.setFloat("needPay",Float.parseFloat(totalTv.getText().toString()));
        checkOrderDoc.setString("tableId",myApplication.getTable_sel_obj().getId());
        checkOrderDoc.setString("createdYear",getNianDate());
        checkOrderDoc.setString("operator",myApplication.getEmployee().getName());
        MutableArray arrayPay = new MutableArray();
        for (int a = 0;a < payDetailList.size();a++){
            PayDetail payDetail = payDetailList.get(a);
            ObjectMapper getMap = new ObjectMapper();
            Map<String ,Object> map = getMap.convertValue(payDetail, Map.class);
            arrayPay.addValue(map);
        }
        checkOrderDoc.setArray("payDetailList",arrayPay);

        MutableArray arrayPayDetail = new MutableArray();
        for (int a = 0;a < promotionDiscountDetailList.size();a++){
            PromotionDiscountDetail promotionDiscountDetail = promotionDiscountDetailList.get(a);
            ObjectMapper getMap = new ObjectMapper();
            Map<String ,Object> map = getMap.convertValue(promotionDiscountDetail, Map.class);
            arrayPayDetail.addValue(map);
        }
        checkOrderDoc.setArray("promotionDiscountDetailList",arrayPayDetail);

        Database database = CDBHelper.getDatabase();
        for (String orderC : checkOrder.getOrderId()) {

            Document document = database.getDocument(orderC);

            MutableDocument mutableDocument = document.toMutable();
            mutableDocument.setInt("state", 0);
            database.save(mutableDocument);

        }
        CDBHelper.saveDocument(checkOrderDoc);
        Table table = CDBHelper.getObjById(myApplication.getTable_sel_obj().getId(),Table.class);
        table.setLastCheckOrderId(checkOrderDoc.getId());
        CDBHelper.createAndUpdate(table);
        if (!memberLogs){
            indexCharge = total;
            setMemberLogs();
            memberLogs = true;
        }

        //turnDesk();

        //  show();
        //
        // changeTableState(); 有可能接着在这里吃饭，人还没走，所以不能置闲桌位
        BluetoothAdapter btAdapter = BluetoothUtil.getBTAdapter();
        if (!btAdapter.isEnabled()) {

            btAdapter.enable();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        BluetoothDevice device = BluetoothUtil.getDevice(btAdapter);


        if (device != null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("打印总账单");
            builder.setPositiveButton("打印", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    printOrder();
                }
            });
            builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {


                    //跳转主界面
                    turnDesk();

                }
            });
            builder.show();

        } else {


            turnDesk();


        }
    }

    private void setMemberLogs(){
        MutableArray array = new MutableArray();
        for (int a = 0;a < payDetailList.size();a++){
            PayDetail payDetail = payDetailList.get(a);
            ObjectMapper getMap = new ObjectMapper();
            Map<String ,Object> map = getMap.convertValue(payDetail, Map.class);
            array.addValue(map);
        }
        MutableDocument mutableDocument = new MutableDocument("MemberLogs."+ ToolUtil.getUUID());
        mutableDocument.setString("channelId",myApplication.getCompany_ID());
        mutableDocument.setString("className","MemberLogs");
        mutableDocument.setString("dataType","UserData");
        mutableDocument.setString("createdYear",getNianDate());
        mutableDocument.setInt("type",0);
        mutableDocument.setArray("payDetailList",array);
        mutableDocument.setString("createdTime",getFormatDate());
        if (indexCharge != 0){
            mutableDocument.setFloat("charge",indexCharge);
        }
        if (indexChargePresent != 0){
            mutableDocument.setFloat("chargePresent",indexChargePresent);
        }
        mutableDocument.setValue("member",getDictionary());
        CDBHelper.saveDocument(mutableDocument);
        MyLog.e("mutableDocument-----"+mutableDocument.getId());
    }
/*
*
     * 字符串生成二维码图片
     *
     * @param str 二维码字符串
     * @return Bitmap
*/


    private Bitmap encodeAsBitmap(String str) {
        Bitmap bitmap = null;
        BitMatrix result = null;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            result = multiFormatWriter.encode(str, BarcodeFormat.QR_CODE, 250, 250);
            // 使用 ZXing Android Embedded
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(result);

        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException iae) {
            return null;
        }
        return bitmap;
    }


    private boolean ifChangeTable() {
        List<Order> orderCList = CDBHelper.getObjByWhere(
                Expression.property("className").equalTo(Expression.string("Order"))
                        .and(Expression.property("state").equalTo(Expression.intValue(1)))
                        .and(Expression.property("tableNum").equalTo(Expression.intValue(myApplication.getTable_sel_obj().getNum())))
                , Ordering.property("createdTime").descending()
                , Order.class);

        if (orderCList.size() > 0) {
            return false;
        }
        return true;
    }
}

