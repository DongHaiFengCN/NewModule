package doaing.order.view;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.couchbase.lite.Array;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Ordering;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import bean.kitchenmanage.dishes.DishesC;
import bean.kitchenmanage.dishes.DishesKindC;
import bean.kitchenmanage.order.GoodsC;
import bean.kitchenmanage.order.OrderC;
import bean.kitchenmanage.order.OrderNum;
import bean.kitchenmanage.table.AreaC;
import doaing.mylibrary.MyApplication;
import doaing.order.R;
import doaing.order.module.DishesMessage;
import doaing.order.untils.MyBigDecimal;
import doaing.order.untils.Tool;
import tools.CDBHelper;
import tools.MyLog;
import tools.ToolUtil;


import static tools.CDBHelper.getFormatDate;
import static tools.CDBHelper.getNianDate;

public class MainActivity extends AppCompatActivity {



    FrameLayout activityFrame;

    public MyApplication getMyApp() {

        return myApp;

    }
    private MyApplication myApp;

    private ListView order_lv;

    private TextView ok_tv;

    private TextView total_tv;

    private ImageView car_iv;

    private ImageButton delet_bt;

    public List<SparseArray<Object>> orderItem = new ArrayList<>();

    public OrderAdapter orderAdapter;

    private int point = 0;

    private TextView point_tv;

    private float total = 0.0f;

    private Fragment seekT9Fragment;

    private Fragment orderFragment;

    private List<GoodsC> t9GoodsList;

    private SeekT9Adapter seekT9Adapter;

    private FragmentManager fm;//获得Fragment管理器

    private FragmentTransaction ft; //开启一个事务

    private boolean isFlag = true;


    private MenuItem menuItem;
    private List<GoodsC> goodsList = new ArrayList<>();

    private List<GoodsC> zcGoodsList = new ArrayList<>();

    private String gOrderId;

    private Document document;

    private String tableName, areaName, currentPersions, serNum;

    //private boolean printerSat = false;
    private int changeFlag = 0;
    private ProgressDialog proDialog = null;

    private int printerType = 58;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        EventBus.getDefault().register(this);
        toolbar = findViewById(R.id.toolbar);
        activityFrame = findViewById(R.id.activity_main_frame);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        hideNavigationBar();
        //关键下面两句话，设置了回退按钮，及点击事件的效果
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.icon_zuo_jt);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                finish();

            }

        });

        myApp = (MyApplication) getApplicationContext();
        SharedPreferences sharedPreferences = getSharedPreferences("T9andOrder", 0);

        isFlag = sharedPreferences.getBoolean("isFlag",true);
        proDialog = new ProgressDialog( MainActivity.this);
        initView();
        select(isFlag);

        MyLog.d("onCreate");
        Handler mHandler = new Handler();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //yaliceshi();
                //shanchu();
            }
        });

    }
    //全屏
    private void hideNavigationBar() {

        int systemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
        // Navigation bar hiding:  Backwards compatible to ICS.

        if (Build.VERSION.SDK_INT >= 14) {

            systemUiVisibility ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

        }

        // 全屏展示

        if (Build.VERSION.SDK_INT >= 16) {

            systemUiVisibility ^= View.SYSTEM_UI_FLAG_FULLSCREEN;

        }

        if (Build.VERSION.SDK_INT >= 18) {

            systemUiVisibility ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        }
        getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            return true;

        }

        return false;

    }

    @Override

    protected void onStart() {

        super.onStart();

    }



    @Override

    protected void onDestroy() {

        super.onDestroy();

        MyLog.e("Main activity onDestroy");

        EventBus.getDefault().unregister(this);

    }


    public void setSeekT9Adapter(SeekT9Adapter seekT9Adapter) {

        this.seekT9Adapter = seekT9Adapter;

    }

    public SeekT9Adapter getSeekT9Adapter() {

        return seekT9Adapter;

    }

    public void setT9GoodsList(List<GoodsC> t9GoodsList) {

        this.t9GoodsList = t9GoodsList;

    }



    public List<GoodsC> getT9GoodsList() {

        return t9GoodsList;

    }


    public OrderAdapter getOrderAdapter() {

        return orderAdapter;

    }

    public List<GoodsC> getGoodsList() {

        return goodsList;

    }



    public void changeOrderGoodsByT9(GoodsC goodsObj)

    {

        boolean isName = false;

        for (int i = 0; i<goodsList.size();i++)//+for

        {

            if (goodsList.get(i).getDishesName().equals(goodsObj.getDishesName()))//名称相等

            {

                if(goodsList.get(i).getDishesTaste()!=null)//口味不为空

                {

                    if(goodsList.get(i).getDishesTaste().equals(goodsObj.getDishesTaste()))//口味相等

                    {



                        float tmp = MyBigDecimal.mul(goodsObj.getPrice(),goodsObj.getDishesCount(),1);

                        goodsList.get(i).setDishesCount(MyBigDecimal.add(goodsObj.getDishesCount(),goodsList.get(i).getDishesCount(),1));

                        total =  getTotal();

                        total = MyBigDecimal.add(total,tmp,1);

                        setTotal(total);

                        isName = true;

                        break;

                    }



                }//口味为空

                else

                {

                    float tmp = MyBigDecimal.mul(goodsObj.getPrice(),goodsObj.getDishesCount(),1);

                    goodsList.get(i).setDishesCount(MyBigDecimal.add(goodsObj.getDishesCount(),goodsList.get(i).getDishesCount(),1));

                    total =  getTotal();

                    total = MyBigDecimal.add(total,tmp,1);

                    setTotal(total);

                    isName = true;

                    break;



                }



            }

        }//-for



        if (!isName){

            goodsList.add(goodsObj);

            //购物车计数器数据更新

            point = getPoint();

            point++;

            setPoint(point);



            //计算总价

            total =getTotal();

            total = MyBigDecimal.add(total,MyBigDecimal.mul(goodsObj.getDishesCount(),goodsObj.getPrice(),1),1);

            setTotal(total);



        }

    }

    public void setTotal(float total) {

        this.total = total;

        String to = MyBigDecimal.round(total + "", 1);

        total_tv.setText(to + "元");

    }

    public float getTotal() {
        return total;
    }
    public void setPoint(int point) {

        this.point = point;

        if (point > 0) {

            point_tv.setText(point + "");

            point_tv.setVisibility(View.VISIBLE);

        } else {

            point_tv.setVisibility(View.GONE);

        }

    }

    public int getPoint() {

        return point;

    }



    public void initView() {
        total_tv =   findViewById(R.id.total_tv);
        point_tv =  findViewById(R.id.point);
        car_iv =  findViewById(R.id.car);
        ok_tv =  findViewById(R.id.ok_tv);
        order_lv =  findViewById(R.id.order_lv);
        final ImageView imageView =   findViewById(R.id.shade);
        final LinearLayout linearLayout = findViewById(R.id.orderList);
        //获取屏幕尺寸
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int w = dm.widthPixels;

        int h = dm.heightPixels;



        //设置表单的容器的长度为视窗的一半高，由父类的节点获得



        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) linearLayout

                .getLayoutParams();

        layoutParams.width = w;

        layoutParams.height = h / 2;

        linearLayout.setLayoutParams(layoutParams);

        orderAdapter = new OrderAdapter(getGoodsList(), MainActivity.this);

        order_lv.setAdapter(orderAdapter);

        orderAdapter.setListener(new OrderAdapter.setOnItemListener() {

            @Override

            public void setListener(final int position) {

//1\

                final GoodsC goodsC = goodsList.get(position);

                if (goodsC.getGoodsType() == 2){

                    return ;

                }

                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

                alert.setMessage("是否赠菜")

                        .setPositiveButton("是", new DialogInterface.OnClickListener() {

                            @Override

                            public void onClick(DialogInterface dialog, int which) {
                                //2\

                                goodsC.setGoodsType(2);

                                goodsC.setDishesName(goodsC.getDishesName() + "(赠)");
                                total = MyBigDecimal.sub(total, MyBigDecimal.mul(goodsC.getPrice(), goodsC.getDishesCount(), 1), 1);
                                setTotal(total);
                                orderAdapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }

                        });

                alert.setNegativeButton("否", new DialogInterface.OnClickListener() {

                    @Override

                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }

                });

                alert.create().show();

            }

        });

        car_iv.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                //初始化订单的数据，绑定数据源的信息。

                //o.notifyDataSetChanged();

                if (getGoodsList().size() > 0) {

                    setOrderDialog();

                }

                //监听orderItem的增加删除，设置总价以及总数量, flag ？+ ：-,price 单价 ,sum 当前item的个数。



                orderAdapter.setOnchangeListener(new OrderAdapter.OnchangeListener() {

                    @Override

                    public void onchangeListener(boolean flag, float allPrice, float sum) {



                        if (flag) {//点加号

                            total = MyBigDecimal.add(total, allPrice, 1);

                            total_tv.setText(total + "元");

                        } else {



                            total = MyBigDecimal.sub(total, allPrice, 1);

                            total_tv.setText(total + "元");



                            if (sum == 0) {

                                point--;

                                point_tv.setText(point + "");

                                if (point == 0) {

                                    point_tv.setVisibility(View.INVISIBLE);

                                }

                            }

                        }

                        EventBus.getDefault().postSticky(new String());

                    }





                });



            }

        });





        //清空按钮

        delet_bt = (ImageButton) findViewById(R.id.delet);



        delet_bt.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {



                clearOrder();

            }

        });

        //提交按钮

        ok_tv.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {
                if (getGoodsList().size() > 0) {

                    saveOrder();
                    Intent intent = new Intent(MainActivity.this, ShowParticularsActivity.class);
                    startActivity(intent);
                    finish();
                } else {

                    Toast.makeText(MainActivity.this, "订单为空！", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private void setOrderDialog() {

        Dialog dialog = new Dialog(MainActivity.this, R.style.ActionSheetDialogStyle);

        View orderDialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_dialog_order, null);

        ListView listView = orderDialog.findViewById(R.id.order_lv);

        ImageView delet = orderDialog.findViewById(R.id.delet);

        listView.setAdapter(orderAdapter);

        delet.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                clearOrder();

            }

        });

        dialog.setContentView(orderDialog);

        Window windowDialog = dialog.getWindow();

        windowDialog.setGravity(Gravity.BOTTOM);

        WindowManager m = getWindowManager();

        Display d = m.getDefaultDisplay();//为获取屏幕宽、高

        // 获取对话框当前的参数值

        WindowManager.LayoutParams lp = windowDialog.getAttributes();

        lp.y = 80;

        lp.height = (int) (d.getHeight() * 0.6);

        windowDialog.setAttributes(lp);

        dialog.show();

    }
//     * 清空订单列表

    private void clearOrder() {



        point = 0;

        point_tv.setVisibility(View.INVISIBLE);

        total_tv.setText("0.0元");

        total = 0;

        getGoodsList().clear();

        orderAdapter.notifyDataSetChanged();

        if (seekT9Adapter != null){

            if (seekT9Adapter.getGoodsList().size() != 0){

                seekT9Adapter.getGoodsList().clear();
            }
            seekT9Adapter.notifyDataSetChanged();
        }

        EventBus.getDefault().postSticky("1");
    }



    private String getOrderSerialNum() {

        String orderNum = null;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        List<OrderNum> orderNumList = CDBHelper.getObjByWhere(getApplicationContext(),

                Expression.property("className").equalTo(Expression.string("OrderNum"))

                , null

                , OrderNum.class);

        if (orderNumList.size() <= 0)//第一次使用

        {

            OrderNum obj = new OrderNum(myApp.getCompany_ID());

            String time = formatter.format(new Date());

            obj.setDate(time);

            obj.setNum(1);

            CDBHelper.createAndUpdate(getApplicationContext(), obj);

            orderNum = "001";

        } else//有数据，判断是不是当天

        {

            OrderNum obj = orderNumList.get(0);

            String olderDate = obj.getDate();

            String newDate = formatter.format(new Date());

            int num = obj.getNum();

            if (!newDate.equals(olderDate))//不是一天的，

            {

                obj.setNum(1);

                obj.setDate(newDate);

                CDBHelper.createAndUpdate(getApplicationContext(), obj);

                orderNum = "001";

            } else//同一天

            {

                int newNum = num + 1;

                obj.setNum(newNum);

                CDBHelper.createAndUpdate(getApplicationContext(), obj);

                orderNum = String.format("%3d", newNum).replace(" ", "0");

            }

        }



        return orderNum;

    }

    private void saveOrder1(){
        String newId = "OrderC."+ ToolUtil.getUUID();
        MutableDocument newOrderDoc = new MutableDocument(newId);
        Log.e("Main",newId);
        String zcId = "OrderC."+ ToolUtil.getUUID();
        MutableDocument zcOrderDoc = new MutableDocument(zcId);
        List<Document> orderCList = CDBHelper.getDocmentsByWhere(getApplicationContext(),
                Expression.property("className").equalTo(Expression.string("OrderC"))
                        .and(Expression.property("orderState").equalTo(Expression.intValue(1)))
                        .and(Expression.property("tableNo").equalTo(Expression.string(myApp.getTable_sel_obj().getTableNum())))
                , Ordering.property("createdTime").descending()

        );
        if (orderCList.size() > 0) {
            newOrderDoc.setInt("orderNum",orderCList.get(0).getInt("orderNum") + 1);
            newOrderDoc.setString("serialNum",orderCList.get(0).getString("serialNum"));
        } else {
            newOrderDoc.setInt("orderNum", 1);
            newOrderDoc.setString("serialNum",getOrderSerialNum());
        }

        for (int i = 0; i < goodsList.size(); i++) {
            GoodsC obj = goodsList.get(i);
            if (obj.getGoodsType() == 2) {
                zcGoodsList.add(obj);
                goodsList.remove(i);
                i--;
                continue;
            }
            obj.setOrder(newId);
        }

        MutableArray array = new MutableArray();
        for (int i = 0;i < goodsList.size();i++){
            GoodsC obj = goodsList.get(i);
            ObjectMapper m = new ObjectMapper();
            Map<String, Object> props = m.convertValue(obj, Map.class);
            array.addValue(props);
        }
        newOrderDoc.setString("className", "OrderC");
        newOrderDoc.setString("channelId",myApp.getCompany_ID());
        newOrderDoc.setString("dataType", "UserData");
        newOrderDoc.setArray("goodsList",array);
       // newOrderDoc.setValue("goodsList",goodsList);
        newOrderDoc.setFloat("allPrice",total);
        newOrderDoc.setInt("orderState",1);//未买单
        newOrderDoc.setInt("orderCType",0);//正常
        newOrderDoc.setInt("deviceType",1);//点餐宝
        newOrderDoc.setString("createdTime",getFormatDate());
        newOrderDoc.setString("tableNo",myApp.getTable_sel_obj().getTableNum());
        newOrderDoc.setString("tableName",myApp.getTable_sel_obj().getTableName());
        AreaC areaC = CDBHelper.getObjById(getApplicationContext(), myApp.getTable_sel_obj().getAreaId(), AreaC.class);
        newOrderDoc.setString("areaName",areaC.getAreaName());
        try {
            if (CDBHelper.getDatabase() != null){
                CDBHelper.getDatabase().save(newOrderDoc);
            }else{
                Log.e("Main","数据库为空");
            }

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        if (zcGoodsList.size() > 0) {
            newOrderDoc.setString("className", "OrderC");
            newOrderDoc.setString("channelId",myApp.getCompany_ID());
            newOrderDoc.setString("dataType", "UserData");
            zcOrderDoc.setString("serialNum",newOrderDoc.getString("serialNum"));
            zcOrderDoc.setInt("orderState",1);//未买单
            zcOrderDoc.setInt("orderCType",2);//赠菜
            zcOrderDoc.setInt("deviceType",1);//点餐宝
            zcOrderDoc.setString("createdTime",newOrderDoc.getString("createdTime"));
            zcOrderDoc.setString("tableNo",newOrderDoc.getString("tableNo"));
            zcOrderDoc.setString("tableName",newOrderDoc.getString("tableName"));
            zcOrderDoc.setString("areaName",newOrderDoc.getString("areaName"));
            MutableArray zcArray = new MutableArray();
            for (GoodsC obj : zcGoodsList) {
                obj.setOrder(zcId);
                zcArray.addValue(obj);
            }

            zcOrderDoc.setArray("goodsList",zcArray);
            try {
                if (CDBHelper.getDatabase() != null){
                    CDBHelper.getDatabase().save(zcOrderDoc);
                }else{
                    Log.e("Main","数据库为空");
                }

            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
        }
        areaName = newOrderDoc.getString("areaName");
        tableName = newOrderDoc.getString("tableName");
        currentPersions = "" + myApp.getTable_sel_obj().getCurrentPersions();
        if (newOrderDoc.getInt("orderNum") == 1)//第一次下单
            serNum = newOrderDoc.getString("serialNum");//流水号
        else //多次下单
            serNum = newOrderDoc.getString("serialNum") + "_"
                    + newOrderDoc.getString("orderNum");
        proDialog.setMessage("订单已生成，真准备打印");
    }



    private void saveOrder()
    {

        zcGoodsList.clear();

        OrderC newOrderObj = new OrderC(myApp.getCompany_ID());
        OrderC zcOrderObj = new OrderC(myApp.getCompany_ID());
        gOrderId = CDBHelper.createAndUpdate(getApplicationContext(), newOrderObj);
        newOrderObj.set_id(gOrderId);

        List<Document> orderCList = CDBHelper.getDocmentsByWhere(getApplicationContext(),
                Expression.property("className").equalTo(Expression.string("OrderC"))
                        .and(Expression.property("orderState").equalTo(Expression.intValue(1)))
                        .and(Expression.property("tableNo").equalTo(Expression.string(myApp.getTable_sel_obj().getTableNum())))
                , Ordering.property("createdTime").descending()

        );
        if (orderCList.size() > 0) {
            newOrderObj.setOrderNum(orderCList.get(0).getInt("orderNum") + 1);
            newOrderObj.setSerialNum(orderCList.get(0).getString("serialNum"));
        } else {
            newOrderObj.setOrderNum(1);
            newOrderObj.setSerialNum(getOrderSerialNum());
        }
        Log.e("Main","--"+newOrderObj.getOrderNum());

        for (int i = 0; i < goodsList.size(); i++) {
            GoodsC obj = goodsList.get(i);
            if (obj.getGoodsType() == 2) {
                zcGoodsList.add(obj);
                goodsList.remove(i);
                i--;
                continue;
            }
            obj.setOrder(gOrderId);
        }
        newOrderObj.setGoodsList(goodsList);
        newOrderObj.setAllPrice(total);
        newOrderObj.setOrderState(1);//未买单
        newOrderObj.setOrderCType(0);//正常
        newOrderObj.setDeviceType(1);//点餐宝
        newOrderObj.setCreatedTime(getFormatDate());
        newOrderObj.setCreatedYear(getNianDate());
        newOrderObj.setTableNo(myApp.getTable_sel_obj().getTableNum());
        newOrderObj.setTableName(myApp.getTable_sel_obj().getTableName());
        AreaC areaC = CDBHelper.getObjById(getApplicationContext(), myApp.getTable_sel_obj().getAreaId(), AreaC.class);
        newOrderObj.setAreaName(areaC.getAreaName());
        CDBHelper.createAndUpdate(getApplicationContext(), newOrderObj);
        Document document = CDBHelper.getDocByID(getApplicationContext(),gOrderId);
        Log.e("Main",""+document.getString("orderNum"));
        if (zcGoodsList.size() > 0) {
            zcOrderObj.setSerialNum(newOrderObj.getSerialNum());
            zcOrderObj.setOrderState(1);//未买单
            zcOrderObj.setOrderCType(2);//赠菜zcOrderObj.setDeviceType(1);//点餐宝
            zcOrderObj.setCreatedTime(newOrderObj.getCreatedTime());
            zcOrderObj.setTableNo(newOrderObj.getTableNo());
            zcOrderObj.setTableName(newOrderObj.getTableName());
            zcOrderObj.setAreaName(newOrderObj.getAreaName());
            zcOrderObj.setCreatedYear("2018");
            String id = CDBHelper.createAndUpdate(getApplicationContext(), zcOrderObj);
            for (GoodsC obj : zcGoodsList) {
                obj.setOrder(id);
            }
            zcOrderObj.setGoodsList(zcGoodsList);
            zcOrderObj.set_id(id);
            CDBHelper.createAndUpdate(getApplicationContext(), zcOrderObj);
        }
        Log.e("id", gOrderId);
        areaName = newOrderObj.getAreaName();
        tableName = newOrderObj.getTableName();
        currentPersions = "" + myApp.getTable_sel_obj().getCurrentPersions();
        if (newOrderObj.getOrderNum() == 1)//第一次下单
            serNum = newOrderObj.getSerialNum();//流水号
        else //多次下单
            serNum = newOrderObj.getSerialNum() + "_" + newOrderObj.getOrderNum();
        proDialog.setMessage("订单已生成，真准备打印");

    }



    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);





        if (resultCode == RESULT_OK && requestCode == 1) {



            //document = CDBHelper.getDocByID(getApplicationContext(),gOrderId);

            Log.e("document", "" + document.getId());



        }





    }





    //隐藏所有Fragment

    private void hidtFragment(FragmentTransaction fragmentTransaction) {
        if (seekT9Fragment != null) {

            fragmentTransaction.hide(seekT9Fragment);

        }

        if (orderFragment != null) {

            fragmentTransaction.hide(orderFragment);

        }

    }



    private void select(boolean isTrue) {

        fm = getFragmentManager();

        ft = fm.beginTransaction();

        hidtFragment(ft);

        if (isTrue == true) {

            if (seekT9Fragment == null) {

                seekT9Fragment = new SeekT9Fragment();

                ft.add(R.id.activity_main_frame, seekT9Fragment);



            } else {

                ft.show(seekT9Fragment);



            }
            isFlag = false;

        } else if (isTrue == false) {

            if (orderFragment == null) {

                orderFragment = new OrderFragment();

                ft.add(R.id.activity_main_frame, orderFragment);

            } else {

                ft.show(orderFragment);

            }
            //menuItem.setTitle("搜索点餐");
            isFlag = true;

        }

        ft.commit();



        SharedPreferences settings = getSharedPreferences("T9andOrder", 0);

        SharedPreferences.Editor editor = settings.edit();

        editor.clear();

        editor.commit();

        editor.putBoolean("isFlag",isTrue);

        editor.commit();

    }



//*

//     * 模拟原始数据

//     *

//     * @return
    @Override

    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuItem = menu.findItem(R.id.action_n);
        if (isFlag){
            menuItem.setTitle("搜索点餐");
        }else{
            menuItem.setTitle("分类点餐");
        }
        return super.onCreateOptionsMenu(menu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will

        // automatically handle clicks on the Home/Up button, so long

        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_search) {

            select(isFlag);
            if (isFlag){
                menuItem.setTitle("搜索点餐");
            }else{
                menuItem.setTitle("分类点餐");
            }
        }

        return super.onOptionsItemSelected(item);

    }



//    *

//     * @param dishesMessage

//     * @author 董海峰

//     * @date 2017/12/22 14:58





    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)

    public void setMessage(DishesMessage dishesMessage) {

        boolean isDishes = true;



        // TODO 处理数据



        //没有菜默认添加

        if (goodsList.size() == 0) {



            isDishes = false;



            if (orderAdapter == null) {



                orderAdapter = new OrderAdapter(goodsList, MainActivity.this);



                if (order_lv == null){

                    order_lv = findViewById(R.id.order_lv);

                }



                order_lv.setAdapter(orderAdapter);



            }





        } else {



            for (int i = 0; i < goodsList.size(); i++) {



                if (goodsList.get(i).getDishesName().equals(dishesMessage.getDishesC().getDishesName())) {



                    if (goodsList.get(i).getDishesTaste() == null &&

                            dishesMessage.getDishesTaste() == null) {



                        upOrderData(dishesMessage, i);



                        isDishes = true;

                        break;



                    } else if (goodsList.get(i).getDishesTaste().equals(dishesMessage.getDishesTaste())) {



                        upOrderData(dishesMessage, i);

                        isDishes = true;

                        break;



                    } else {





                        isDishes = false;



                    }



                } else {



                    isDishes = false;

                }



            }



        }



        //没找到菜品，添加菜品

        if (!isDishes && dishesMessage.isOperation()) {

            GoodsC goodsC = new GoodsC();

            goodsC.setChannelId(myApp.getCompany_ID());

            goodsC.setDishesKindId(dishesMessage.getDishKindId());

            goodsC.setDishesTaste(dishesMessage.getDishesTaste());

            goodsC.setDishesName(dishesMessage.getName());

            goodsC.setDishesCount(dishesMessage.getCount());

            goodsC.setDishesId(dishesMessage.getDishesC().get_id());

            goodsC.setGoodsType(0);

            goodsC.setCreatedTime(getFormatDate());

            goodsC.setPrice(dishesMessage.getDishesC().getPrice());

            goodsList.add(goodsC);



        }

        updataTotal();

        updataPoint();



        orderAdapter.notifyDataSetChanged();

    }



    private void updataPoint() {



        setPoint(orderAdapter.getCount());

    }



    private void updataTotal() {



        if (goodsList.size() == 0) {



            setTotal(0.00f);



        } else {



            float t = 0f;



            for (int i = 0; i < goodsList.size(); i++) {



                t += (goodsList.get(i).getPrice()*getGoodsList().get(i).getDishesCount());





            }

            setTotal(t);



        }

    }



    //更新订单goodsList数据
    private void upOrderData(DishesMessage dishesMessage, int i) {

        if (dishesMessage.isOperation()) {

            goodsList.get(i).setDishesCount(goodsList.get(i).getDishesCount() + dishesMessage.getCount());

        } else {
            if ((goodsList.get(i).getDishesCount() - dishesMessage.getCount()) == 0) {

                goodsList.remove(i);

            } else {

                goodsList.get(i).setDishesCount(goodsList.get(i).getDishesCount() - dishesMessage.getCount());

            }

        }
    }
}