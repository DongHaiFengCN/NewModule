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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Ordering;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import bean.kitchenmanage.member.ConsumLogC;
import bean.kitchenmanage.order.CheckOrderC;
import bean.kitchenmanage.order.GoodsC;
import bean.kitchenmanage.order.OrderC;
import bean.kitchenmanage.order.PayDetailC;
import bean.kitchenmanage.order.PromotionDetailC;
import bean.kitchenmanage.promotion.PromotionC;
import bean.kitchenmanage.promotion.PromotionDishesC;
import bean.kitchenmanage.promotion.PromotionDishesKindC;
import bean.kitchenmanage.promotion.PromotionRuleC;
import bean.kitchenmanage.qrcode.qrcodeC;
import bean.kitchenmanage.table.AreaC;
import bean.kitchenmanage.table.TableC;
import butterknife.ButterKnife;
import doaing.mylibrary.MyApplication;
import doaing.order.R;
import doaing.order.untils.ProgressBarasyncTask;
import doaing.order.view.adapter.ActionListAdapter;
import doaing.order.view.adapter.MemberDishesListAdapter;
import doaing.order.untils.BluetoothUtil;
import doaing.order.module.DatabaseSource;
import doaing.order.module.IDBManager;
import doaing.order.untils.MyBigDecimal;
import doaing.order.untils.MyLog;
import doaing.order.untils.Tool;
import tools.CDBHelper;
import tools.ToolUtil;

/**
 * @author 董海峰
 * @date 2017/10/25
 */



public class PayActivity extends AppCompatActivity implements View.OnClickListener{

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
    TextView tableNumber;

    List<PromotionRuleC> promotionRuleCList;
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

    //折扣率
    private int disrate;

    private List<PromotionC> promotionCList;
    private TableC tableC;
    List<GoodsC> orderDishesList = new ArrayList<>();
    private CheckOrderC checkOrder = new CheckOrderC();

    //营销细节
    PromotionDetailC promotionD = new PromotionDetailC();


    //每增加一种支付方式创建一个支付详情，例如充值卡余额不足，剩下的部分用的现金。
    private List<PayDetailC> payDetailList = new ArrayList<>();


    ConsumLogC consumLogC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pay);

        ButterKnife.bind(this);

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
        AreaC areaCs = CDBHelper.getObjById(getApplicationContext(), tableC.getAreaId(), AreaC.class);

        tableNumber.setText(areaCs.getAreaName() + "桌/牌:" + tableC.getTableName());

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
        List<OrderC> orderCList = CDBHelper.getObjByWhere(getApplicationContext(), Expression.property("className")
                        .equalTo(Expression.string("OrderC"))
                        .and(Expression.property("tableNo").equalTo(Expression.string(tableC.getTableNum())))
                        .and(Expression.property("orderState").equalTo(Expression.intValue(1)))
                , Ordering.property("createdTime").ascending()
                , OrderC.class);

        for (OrderC orderC : orderCList) {
            if (orderC.getOrderCType() == 0)//正常订单
            {
                total = MyBigDecimal.add(total, orderC.getAllPrice(), 1);
            }
            checkOrder.addOrder(orderC);
            //获取当前订单下goods集合下所有的菜品
            for (GoodsC o : orderC.getGoodsList()) {

                orderDishesList.add(o);
            }

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
         tableNumber = findViewById(R.id.table_number);
        associator.setOnClickListener(this);
        discount.setOnClickListener(this);
        action.setOnClickListener(this);
        ivalipay.setOnClickListener(this);
        ivwechat.setOnClickListener(this);
        cash.setOnClickListener(this);
        findViewById(R.id.bankcard).setOnClickListener(this);
    }


    //测试提交数据～～～～～～～～～～～～

    public void show() {

        List<CheckOrderC> l = CDBHelper.getObjByClass(getApplicationContext(), CheckOrderC.class);

        Iterator i = l.iterator();

        CheckOrderC checkOrderC = null;

        while (i.hasNext()) {

            checkOrderC = (CheckOrderC) i.next();

            if (checkOrderC.getTableNo().equals(myApplication.getTable_sel_obj().getTableNum())) {

                //订单提交时间
                MyLog.e("订单提交时间: " + checkOrderC.getCheckTime());
                //桌号
                MyLog.e("桌号: " + checkOrderC.getTableNo());
                //实收
                MyLog.e("实收: " + checkOrderC.getNeedPay());
                //应收
                MyLog.e("应收: " + checkOrderC.getPay());

                //菜
                List<OrderC> f = checkOrderC.getOrderList();

                if (f != null) {
                    int i1 = 0;

                    for (OrderC orderC : f) {


                        i1++;
                        MyLog.e("订单~~~~~~" + i1);

                        for (GoodsC goodsC : orderC.getGoodsList()) {

                            MyLog.e(goodsC.getDishesName());
                        }


                    }
                }


                //支付详情
                MyLog.e(" PromotionDetailC 支付详情~~~~~~~~~~~ ");
                PromotionDetailC promotionDetail = checkOrderC.getPromotionDetail();
                MyLog.e("优惠金额： " + promotionDetail.getDiscounts());
                MyLog.e("折扣率： " + promotionDetail.getDisrate());

                MyLog.e("PayDetailC 支付细节~~~~~~~~~~~ ");

                List<PayDetailC> payDetailList = promotionDetail.getPayDetailList();

                for (int j = 0; j < payDetailList.size(); j++) {

                    MyLog.e("第 " + j + " 支付方式");

                    PayDetailC p = payDetailList.get(j);

                    MyLog.e("支付类型 " + p.getPayTypes());

                    MyLog.e("支付钱数 " + p.getSubtotal());

                }

            }

        }

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

        List<qrcodeC> qrcodeList = CDBHelper.getObjByClass(getApplicationContext(), qrcodeC.class);

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

        promotionCList = CDBHelper.getObjByClass(getApplicationContext(), PromotionC.class);

        Iterator iterator = promotionCList.iterator();

        //筛选活动时间
        while (iterator.hasNext()) {

            PromotionC promotion = (PromotionC) iterator.next();
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

    public void showDialog() {

        dg = dialog.show();
    }

    public void closeDialog() {

        dg.dismiss();
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


        } else if (requestCode == SALE && resultCode == RESULT_OK) {//会员账单返回

            int flag = data.getIntExtra("CardTypeFlag", 3);

            //充值
            if (flag == 2) {

                turnRechange(data);

                Log.e("DOAING", "充值卡");

            } else if (flag == 1) {

                turnSale(data);
                Log.e("DOAING", "折扣卡");

            } else {

                Toast.makeText(PayActivity.this, "其他 ", Toast.LENGTH_SHORT).show();

            }

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

        //展示差额
        discountTv.setText("- " + data.getFloatExtra("Margin", 0) + "元");

        //设置抹零支付细节
        setPayDetail(7, data.getFloatExtra("Margin", 0));

        //界面展示实际处理后的价格
        factTv.setText("实际支付：" + total + "元");

        //设置会员按钮不可用
        associatorNotDisplay();
        //活动不可用
        actionNotDisplay();

    }

    private void actionNotDisplay() {
        //设置活动不可用
        action.setEnabled(false);
        if (TextUtils.isEmpty(actionTv.getText().toString())) {

            actionTv.setText("不可选");
        }
    }

/*
*
     * 会员折扣卡处理逻辑
     *
     * @param data
*/


    private void turnSale(Intent data) {

        //打折时初始化实际支付总价设置为 0

        total = 0f;

        //1 菜品的名称，2当前价格，3折扣价格，4是否折扣

        List<SparseArray> list = new ArrayList<>();


        //返回的会员菜品

        List<String> stringList = data.getStringArrayListExtra("DishseList");

        //会员电话
        final String tel = data.getStringExtra("tel");

        List<String> memberDishes = new ArrayList<>();

        //初始化会员菜名
        for (String id : stringList) {

            Document document = (Document) idbManager.getById(id);
            memberDishes.add(document.getString("dishesName"));
        }

        //获取折扣率
        disrate = data.getIntExtra("disrate", 3);

        MyLog.e("折扣率：" + disrate);

        //遍历订单中包含的会员菜品

        for (int j = 0; j < orderDishesList.size(); j++) {

            boolean isSale = false;

            SparseArray<Object> s = new SparseArray<>();

            //获取订单下goods的菜品名称
            GoodsC h = orderDishesList.get(j);

            String name = h.getDishesName();
            float sum = MyBigDecimal.mul(h.getPrice(), h.getDishesCount(), 1);
            //1 设置菜品的名称

            s.put(1, name);
            s.put(2, sum);

            //遍历所会员菜品找匹配的打折菜品

            for (int i = 0; i < memberDishes.size(); i++) {

                //找到打折的
                if (name.equals(memberDishes.get(i))) {

                    MyLog.e("折后前价格：" + sum);

                    float d = Tool.divide(disrate, 100f);

                    sum = Tool.multiply(sum, d);
                    s.put(3, sum);
                    //是打折的直接添加到折扣总价中

                    total += sum;

                    isSale = true;

                    //4 设置菜品的打折

                    s.put(4, true);

                    break;
                }
            }

            //不是打折菜品的时候直接将价格加到总价

            if (!isSale) {

                s.put(3, 0f);

                total += sum;

                s.put(4, false);
            }
            list.add(s);

        }

        //获取会员
        final MutableDocument members = idbManager.getMembers(tel).toMutable();

        //展示享受折扣的列表

        StringBuilder total_sb = new StringBuilder("折扣价：￥");

        View view = getLayoutInflater().inflate(R.layout.view_payactivity_memberdishes_sale_dialog, null);

        final TextView balance = view.findViewById(R.id.balance);

        final float remainder = members.getFloat("remainder");

        balance.setText("余额：" + remainder);

        //支付价格大于卡内余额，且卡内余额大于零，显示使用卡内全部余额

        final TextView t = view.findViewById(R.id.saletotalprice_tv);

        ListView listView = view.findViewById(R.id.memberdisheslist_lv);

        MemberDishesListAdapter memberDishesListAdapter = new MemberDishesListAdapter(list, this);

        listView.setAdapter(memberDishesListAdapter);

        t.setText(total_sb.append(total));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("折扣明细表");

        builder.setView(view);
        builder.setPositiveButton("确定", null);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //factTv.setText("实际支付：" + total + "元");

            }
        });

        final AlertDialog builder1 = builder.show();

        builder1.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //余额不足状态下

                if (total > remainder && remainder > 0) {

                    //支付价格大于卡内余额，且卡内余额大于零，显示使用卡内全部余额
                    AlertDialog.Builder a = new AlertDialog.Builder(PayActivity.this);
                    a.setTitle("卡内余额不足！");
                    a.setMessage("使用卡内全部" + remainder + "元？");
                    a.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            try {
                                //会员剩余金额清零
                                members.setFloat("remainder", 0f);
                                idbManager.save(members);

                            } catch (CouchbaseLiteException e) {
                                e.printStackTrace();
                            }

                            //会员消费记录
                            setConsumLog(members, remainder);

                            //消费支付细节 6会员消费,计算剩余部分
                            total = total - remainder;

                            setPayDetail(6, remainder);
                            builder1.dismiss();

                            //使用会员后活动不可选
                            action.setEnabled(false);
                            actionTv.setText("不可选");

                            factTv.setText("实际支付：" + total + "元");
                            // turnMainActivity();

                        }
                    });
                    a.setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    a.show();

                } else if (remainder >= total) {//余额充足,转跳主界面


                    members.setFloat("remainder", Tool.substrct(remainder, total));
                    try {
                        idbManager.save(members);
                    } catch (CouchbaseLiteException e) {
                        e.printStackTrace();
                    } finally {
                        Toast.makeText(PayActivity.this, "支付成功！", Toast.LENGTH_SHORT);
                    }

                    //会员消费记录
                    setConsumLog(members, total);

                    //消费支付细节 6会员消费
                    setPayDetail(6, total);

                    //结单

                    try {
                        submitCheckOrder();
                    } catch (CouchbaseLiteException e) {
                        e.printStackTrace();
                    }
                    builder1.dismiss();

                    //turnMainActivity();

                    // turnDesk();
                } else {

                    Toast.makeText(PayActivity.this, "请充值！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

/*
*
     * 设置会员消费记录
     *
     * @param members 会员
     * @param consum  消费金额
*/



    private void setConsumLog(Document members, float consum) {

        consumLogC = new ConsumLogC();
        consumLogC.setClassName("ConsumLogC");
        consumLogC.setChannelId(myApplication.getCompany_ID());
        consumLogC.setMembersId(members.getId());
        consumLogC.setCardNo(members.getString("cardNum"));

        consumLogC.setCardConsum(consum);
        consumLogC.setTime(new Date());

    }

/*
*
     * 充值卡扣款功能
     *
     * @param data
*/



    private void turnRechange(Intent data) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.view_payactivity_memberdishes_rechange_dialog, null);

        //当前卡中余额的数量
        final float r = data.getFloatExtra("remainder", 0f);

        final String tel = data.getStringExtra("tel");

        TextView remainderTv = view.findViewById(R.id.remainder_tv);

        remainderTv.setText(r + "");

        TextView rechangepayTv = view.findViewById(R.id.rechangepay_tv);

        rechangepayTv.setText(total + "");

        builder.setTitle("扣款明细表");
        builder.setView(view);
        builder.setPositiveButton("确定扣款", null);

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


            }
        });

        final AlertDialog alertDialog = builder.show();

        final MutableDocument members = idbManager.getMembers(tel).toMutable();
        alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (r >= total) {//余额扣款

                    //更新余额
                    members.setFloat("remainder", Tool.substrct(r, total));

                    try {
                        idbManager.save(members);

                    } catch (CouchbaseLiteException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(PayActivity.this, "扣款成功！", Toast.LENGTH_SHORT).show();

                    //设置消费记录
                    setConsumLog(members, total);

                    //会员卡支付
                    setPayDetail(6, total);

                    //提交checkorder
                    try {
                        submitCheckOrder();

                    } catch (CouchbaseLiteException e) {
                        e.printStackTrace();
                    }

                    alertDialog.dismiss();


                } else if (total > r && r > 0) {

                    //支付价格大于卡内余额，且卡内余额大于零，显示使用卡内全部余额
                    AlertDialog.Builder a = new AlertDialog.Builder(PayActivity.this);
                    a.setTitle("余额不足！");
                    a.setMessage("使用卡内全部" + r + "元？");
                    a.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            try {
                                //会员剩余金额清零
                                members.setFloat("remainder", 0f);
                                idbManager.save(members);

                            } catch (CouchbaseLiteException e) {
                                e.printStackTrace();
                            }

                            //会员消费记录
                            setConsumLog(members, r);

                            //使用会员后活动不可选
                            action.setEnabled(false);
                            actionTv.setText("不可选");
                            //消费支付细节 6会员消费,计算剩余部分
                            total = total - r;

                            factTv.setText("实际支付：" + total + "元");

                            setPayDetail(6, r);
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

                    Toast.makeText(PayActivity.this, "请充值！", Toast.LENGTH_SHORT).show();
                }
            }

        });
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
            TableC obj = myApplication.getTable_sel_obj();
            obj.setLastCheckOrderId(id);
            obj.setState(0);

            CDBHelper.createAndUpdate(getApplicationContext(), tableC);
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


    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.associator) {
            turnSale();


        } else if (i == R.id.discount) {//抹零

            turnDiscount();


        } else if (i == R.id.action) {//活动
            setAction();


        } else if (i == R.id.ivalipay) {//支付宝支付

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
                            submitCheckOrder();
                        } catch (CouchbaseLiteException e) {
                            e.printStackTrace();
                        }


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
                            submitCheckOrder();
                        } catch (CouchbaseLiteException e) {
                            e.printStackTrace();
                        }


                    }
                });

                wechatDialog.show();

            } else {
                Toast.makeText(PayActivity.this, "没有添加二维码", Toast.LENGTH_SHORT).show();
            }


        } else if (i == R.id.cash) {//现金支付

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
                        submitCheckOrder();
                    } catch (CouchbaseLiteException e) {
                        e.printStackTrace();
                    }


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
                        submitCheckOrder();
                    } catch (CouchbaseLiteException e) {
                        e.printStackTrace();
                    }


                }
            });

            bankDialog.show();


        } else {
        }
    }


/*
*
     * 活动扣款
*/


    private void setAction() {


        final PromotionC[] promotion = {null};
        //加载活动

        final ActionListAdapter a = new ActionListAdapter(promotionCList, PayActivity.this);
        View v = getLayoutInflater().inflate(R.layout.view_payactivity_action_dialog, null);

        final TextView showTv = v.findViewById(R.id.show_tv);

        ListView l = v.findViewById(R.id.action_lv);
        a.setListView(l);
        l.setAdapter(a);


        AlertDialog.Builder d = new AlertDialog.Builder(this);
        d.setView(v);
        d.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        d.setPositiveButton("确定", null);

        final AlertDialog dialog = d.show();
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (promotion[0] != null) {

                    if (promotion[0].getPromotionType() == 1) {     //打折状态

                        //计算出减免的金额
                       // MyLog.e("当前总价 " + total);
                      //  MyLog.e("减去的部分 " + copy);

                        total = MyBigDecimal.sub(total, copy, 1);//Tool.substrct(total,copy);
                     //   MyLog.e("减后的部分 " + total);
                        factTv.setText("实际支付：" + total + "元");

                        dialog.dismiss();


                    } else if (promotion[0].getPromotionType() == 2) {//赠券


                        //赠券
                        setPayDetail(8, total - copy);

                        total = copy;
                        //界面展示
                        factTv.setText("实际支付：" + total + "元");

                    }

                    //营销设置活动选择的活动
                    promotionD.setPromotion(promotion[0]);

                    dialog.dismiss();

                } else {
                    Toast.makeText(PayActivity.this, "请选择活动再按确定", Toast.LENGTH_SHORT).show();
                }

            }
        });


        a.setCallback(new ActionListAdapter.Callback() {
            @Override
            public void click(int p) {


                //规则详情
                promotion[0] = promotionCList.get(p);

                promotionRuleCList = Tool.Sort(promotion[0].getPromotionRuleList());


                if (promotion[0].getPromotionType() == 1) {//折扣

                    copy = total;
                    if (promotion[0].getCountMode() == 2) {//菜品金额

                        copy = 0f;

                        List<PromotionDishesC> allDishes = new ArrayList<>();

                        List<PromotionDishesKindC> dishesKindList = promotion[0].getPromotionDishesKindList();

                        if (dishesKindList != null) {

                            for (int i = 0; i < dishesKindList.size(); i++) {

                                PromotionDishesKindC promotionDishesKindC = dishesKindList.get(i);

                                //启用状态下
                                if (promotionDishesKindC.getIschecked() == 1) {

                                    List<PromotionDishesC> list = promotionDishesKindC.getPromotionDishesList();

                                  //  MyLog.e("所有活动菜品个数 " + list.size());
                                    //菜品不为空
                                    if (list != null) {

                                        for (int j = 0; j < list.size(); j++) {

                                            PromotionDishesC promotionDishesC = list.get(j);

                                            //设置启用
                                            if (promotionDishesC.getIschecked() == 1) {

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
                                GoodsC h = orderDishesList.get(j);

                                String name = h.getDishesName();
                             //   MyLog.e("查找的菜" + name);
                                //遍历所活动菜品找匹配的打折菜品

                                for (int i = 0; i < allDishes.size(); i++) {

                                    //确保绝对是自点菜
                                    if (h.getGoodsType() == 3 && h.getDishesId() == null){
                                        copy = MyBigDecimal.add(copy, MyBigDecimal.mul(h.getPrice(), h.getDishesCount(), 1), 1);// h.getAllPrice();
                                        break;
                                    }else  if (h.getDishesId() != null && h.getDishesId().equals(allDishes.get(i).getDishesId())) {//找到打折的
                                            //  MyLog.e("打折的菜" + name);

                                            copy = MyBigDecimal.add(copy, MyBigDecimal.mul(h.getPrice(), h.getDishesCount(), 1), 1);// h.getAllPrice();
                                            break;
                                    }else if (h.getDishesId() == null){

                                        new NullPointerException("菜品DishesId为空");

                                        break;
                                    }

                                }

                            }

                            //计算折扣
                         //   MyLog.e("规则长度 " + promotionRuleCList.size());

                          //  MyLog.e("当前总价 " + total);

                           // MyLog.e("符合减免部分的总价 " + copy);

                            for (int i = 0; i < promotionRuleCList.size(); i++) {

                               // MyLog.e("满 " + promotionRuleCList.get(i).getCounts());

                                if (total >= promotionRuleCList.get(i).getCounts()) {

                                    //折扣比率
                                    disrate = promotionRuleCList.get(i).getDiscounts();

                                    //计算减免

                                   // MyLog.e("减免的标准 " + disrate);


                                    //减去的价格
                                    String f1 ;
                                    f1 = MyBigDecimal.div(disrate+"",100+"",3);

                                   // MyLog.e("处理后的折扣率： " + f1);
;
                                    copy = MyBigDecimal.mul(copy,(1-Float.valueOf(f1)),3);

                                   // copy = copy*(1-Float.valueOf(f1));

                                   // MyLog.e("需要减掉的： " + copy);


                                    //展示当前的减免

                                    showTv.setText("减免" + copy + "元");

                                    break;
                                } else {

                                    copy = 0f;
                                    showTv.setText("不满足活动条件！");
                                }

                            }

                        }
                    }
                } else {

                    //赠券

                    final AlertDialog.Builder builder = new AlertDialog.Builder(PayActivity.this);

                    View view1 = getLayoutInflater().inflate(R.layout.view_payactivity_promotion_dialog, null);

                    final EditText promtionEt = view1.findViewById(R.id.promotion_et);

                    builder.setTitle("输入优惠金额");
                    builder.setView(view1);
                    builder.setPositiveButton("使用", null);
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    final AlertDialog builder1 = builder.show();

                    //使用优惠券减免账单

                    builder1.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (TextUtils.isEmpty(promtionEt.getText().toString())) {

                                promtionEt.setError("不能为空");

                            } else {


                                float promotionPrice = Float.valueOf(promtionEt.getText().toString());

                                //优惠金额大于支付金额
                                if (promotionPrice > total) {

                                    promtionEt.setText("");
                                    promtionEt.setError("请输入小于等于总价的金额！");

                                } else if (promotionPrice == 0f) {

                                    promtionEt.setText("");
                                    promtionEt.setError("请输入大于0的金额！");

                                } else {

                                    copy = 0f;

                                    //设置实际支付的价格
                                    copy = Tool.substrct(total, promotionPrice);
                                    showTv.setText("减免" + promotionPrice + "元");
                                    builder1.dismiss();
                                    associatorNotDisplay();


                                }
                            }
                        }
                    });
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
        PayDetailC p = new PayDetailC();
        p.setClassName("PayDetailC");
        p.setChannelId(myApplication.getCompany_ID());
        p.setPayTypes(type);
        p.setSubtotal(pay);
        p.setCreatedYear("2018");
        CDBHelper.createAndUpdate(getApplicationContext(), p);
        payDetailList.add(p);

    }

//    private void changeTableState()
//    {
//        tableC.setState(0);
//        CDBHelper.createAndUpdate(getApplicationContext(),tableC);
//        myApplication.setTable_sel_obj(tableC);
//    }

/*
*
     * 提交结账信息
     * <p>
     * 设置order的状态为买单
*/


    public void submitCheckOrder() throws CouchbaseLiteException {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        float all = Float.valueOf(totalTv.getText().toString());
        checkOrder.setChannelId(myApplication.getCompany_ID());
        checkOrder.setCheckTime(formatter.format(date));
        checkOrder.setClassName("CheckOrderC");
        checkOrder.setPay(all);
        checkOrder.setNeedPay(total);
        checkOrder.setTableNo(myApplication.getTable_sel_obj().getTableNum());
        checkOrder.setCreatedYear("2018");
        for (OrderC orderC : checkOrder.getOrderList()) {

            orderC.setOrderState(0);

            CDBHelper.createAndUpdate(getApplicationContext(), orderC);
        }

        //营销细节

        promotionD.setChannelId(myApplication.getCompany_ID());
        promotionD.setClassName("PromotionDetailC");


        // promotionD.setDiscounts(A.subtract(T).floatValue());
        promotionD.setDiscounts(Tool.substrct(all, total));
        //支付方式集合
        promotionD.setPayDetailList(payDetailList);

        //折扣率
        promotionD.setDisrate(disrate);

        checkOrder.setPromotionDetail(promotionD);
        promotionD.setCreatedYear("2018");
        CDBHelper.createAndUpdate(getApplicationContext(), promotionD);
        id = CDBHelper.createAndUpdate(getApplicationContext(), checkOrder);

        //设置会员消费记录的checkorder ID
        if (consumLogC != null) {
            consumLogC.setOrderNo(id);
            consumLogC.setCreatedYear("2018");
            CDBHelper.createAndUpdate(myApplication, consumLogC);
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
        List<OrderC> orderCList = CDBHelper.getObjByWhere(getApplicationContext(),
                Expression.property("className").equalTo(Expression.string("OrderC"))
                        .and(Expression.property("orderState").equalTo(Expression.intValue(1)))
                        .and(Expression.property("tableNo").equalTo(Expression.string(myApplication.getTable_sel_obj().getTableNum())))
                , Ordering.property("createdTime").descending()
                , OrderC.class);

        if (orderCList.size() > 0) {
            return false;
        }
        return true;
    }
}

