package doaing.order.view;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Array;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.Meta;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDictionary;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Ordering;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.QueryChange;
import com.couchbase.lite.QueryChangeListener;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import bean.kitchenmanage.kitchen.KitchenClient;
import bean.kitchenmanage.order.OrderNum;
import doaing.mylibrary.MyApplication;
import doaing.order.R;
import doaing.order.module.DishesMessage;
import doaing.order.untils.MyBigDecimal;
import tools.CDBHelper;
import tools.MyLog;
import tools.ToolUtil;

import static com.mob.commons.eventrecoder.EventRecorder.clear;

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
    public OrderAdapter orderAdapter;
    private int point = 0;
    private TextView point_tv;
    private float total = 0.0f;
    private Fragment seekT9Fragment;
    private Fragment orderFragment;
    private List<MutableDocument> t9GoodsList;
    private SeekT9Adapter seekT9Adapter;
    private OrderClassifyAdapter orderDragAdapter;
    private OrderFragment.DishesKindAdapter dishesKindAdapter;
    private FragmentManager fm;//获得Fragment管理器
    private FragmentTransaction ft; //开启一个事务
    private boolean isFlag = true;
    private MenuItem menuItem;
    private List<Document> goodsList = new ArrayList<>();
    Toolbar toolbar;
    private Database db;
    private Query listsLiveQuery = null;
    private MutableDocument newOrderDoc;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            //1.修改购物车金额
            orderTotal();
            //2.修改界面金额
            total_tv.setText(total+"元");
            setPoint(getGoodsList().size());
            orderAdapter.notifyDataSetChanged();
            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        EventBus.getDefault().register(this);
        toolbar = findViewById(R.id.toolbar);
        activityFrame = findViewById(R.id.activity_main_frame);
        toolbar.setTitle("");
        db = CDBHelper.getDatabase();
        if(db == null) throw new IllegalArgumentException();
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
        getGoodsList().clear();
        SharedPreferences sharedPreferences = getSharedPreferences("T9andOrder", 0);

        isFlag = sharedPreferences.getBoolean("isFlag",true);
        initView();
        Listener();
        select(isFlag);

        MyLog.d("onCreate");
    }

    public void setOrderDragAdapter(OrderClassifyAdapter orderDragAdapter){
        this.orderDragAdapter = orderDragAdapter;

    }

    public void setDishesKindAdapter(OrderFragment.DishesKindAdapter dishesKindAdapter){
        this.dishesKindAdapter = dishesKindAdapter;
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

    public void setT9GoodsList(List<MutableDocument> t9GoodsList) {
        this.t9GoodsList = t9GoodsList;
    }

    public List<MutableDocument> getT9GoodsList() {
        return t9GoodsList;
    }

    public OrderAdapter getOrderAdapter() {
        return orderAdapter;
    }
    public List<Document> getGoodsList() {
        return goodsList;
    }
    public void changeOrderGoodsByT9(Document goodsObj) {
        boolean isName = false;
        for (int i = 0; i<goodsList.size();i++)//+for
        {
            Document goodsListDish = CDBHelper.getDocByID(goodsList.get(i).getString("dishId"));
            Document goodsObjDish = CDBHelper.getDocByID(goodsObj.getString("dishId"));
            MutableDocument goodsMutableDoc = CDBHelper.getDocByID(goodsList.get(i).getId()).toMutable();
            if (goodsObjDish.getString("name").equals(goodsListDish.getString("name")))//名称相等
            {
                if(goodsList.get(i).getString("tasteId")!=null)//口味不为空
                {
                    if(goodsList.get(i).getString("tasteId").equals(goodsObj.getString("tasteId")))//口味相等
                    {
                        float tmp = MyBigDecimal.mul(goodsObjDish.getFloat("price"),goodsObj.getFloat("count"),1);
                        goodsMutableDoc.setFloat("count",MyBigDecimal.add(goodsObj.getFloat("count"),goodsList.get(i).getFloat("count"),1));
                        total =  getTotal();
                        total = MyBigDecimal.add(total,tmp,1);
                        setTotal(total);
                        isName = true;
                        break;
                    }
                }//口味为空
                else
                {
                    float tmp = MyBigDecimal.mul(goodsObjDish.getFloat("price"),goodsObj.getFloat("count"),1);
                    goodsMutableDoc.setFloat("count",MyBigDecimal.add(goodsObj.getFloat("count"),goodsList.get(i).getFloat("count"),1));
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
            Document dishDoc = CDBHelper.getDocByID(goodsObj.getString("dishId"));
            total = MyBigDecimal.add(total,MyBigDecimal.mul(goodsObj.getFloat("count"),dishDoc.getFloat("price"),1),1);

            setTotal(total);

        }

    }

    public void setTotal(float total) {

        this.total = total;
        if (total == 0f){
            return;
        }
        if (total_tv == null){
            return;
        }
        total_tv.setText(total + "元");

    }

    public float getTotal() {
        return total;
    }
    public void setPoint(int point) {

        this.point = point;
        if (point_tv == null){
            return;
        }
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

    private Query listsLiveQuery()
    {
        return QueryBuilder.select(SelectResult.expression(Meta.id),
                SelectResult.expression(Expression.property("count")))
                .from(DataSource.database(db))
                .where(Expression.property("goodsTableMsgId").equalTo(Expression.string(myApp.getTable_sel_obj().getId())));
    }

    public void Listener(){
        listsLiveQuery = listsLiveQuery();
        listsLiveQuery.addChangeListener(new QueryChangeListener() {
            @Override
            public void changed(QueryChange change) {
                clear();
                ResultSet rs = change.getResults();
                Result row;
                getGoodsList().clear();
                total = 0;
                while ((row = rs.next()) != null)
                {
                    Document document = CDBHelper.getDocByID(row.getString(0));
                    if (document == null){
                        continue;
                    }
                    if (document.getInt("status") == 2) {
                        getGoodsList().add(document.toMutable());
                    }
                }
//                Message message = Message.obtain();
//                mHandler.sendMessage(message);
                for (int i = 0 ; i <  getGoodsList().size();i++){
                    if (getGoodsList().get(i).getInt("goodsType") == 0) {
                        Document document1 = CDBHelper.getDocByID(getGoodsList().get(i).getString("dishId"));
                        total = MyBigDecimal.add(total, MyBigDecimal.mul(document1.getFloat("price"),
                                getGoodsList().get(i).getFloat("count"), 1), 1);
                    }
                }
                total_tv.setText(total+"元");
                setPoint(getGoodsList().size());
                orderAdapter.notifyDataSetChanged();
                if (orderDragAdapter != null) {
                    orderDragAdapter.dishCount(getGoodsList());
                    orderDragAdapter.notifyDataSetChanged();
                }
                if (dishesKindAdapter != null){
                    dishesKindAdapter.dishKindCount(getGoodsList());
                    dishesKindAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void orderTotal(){
        for (int i = 0 ; i <  getGoodsList().size();i++){
            if (getGoodsList().get(i).getInt("goodsType") == 0) {
                Document document1 = CDBHelper.getDocByID(getGoodsList().get(i).getString("dishId"));
                total = MyBigDecimal.add(total, MyBigDecimal.mul(document1.getFloat("price"),
                        getGoodsList().get(i).getFloat("count"), 1), 1);
            }
        }
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

                final MutableDocument goods = getGoodsList().get(position).toMutable();

                if (goods.getInt("goodsType") == 2){

                    return ;

                }

                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

                alert.setMessage("是否赠菜")

                        .setPositiveButton("是", new DialogInterface.OnClickListener() {

                            @Override

                            public void onClick(DialogInterface dialog, int which) {
                                //2\

                                goods.setInt("goodsType",2);
                                Document document = CDBHelper.getDocByID(goods.getString("dishId"));
                                total = MyBigDecimal.sub(total, MyBigDecimal.mul(document.getFloat("price"), goods.getFloat("count"), 1), 1);
                                setTotal(total);
                                CDBHelper.saveDocument(goods);
                                Listener();
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    View view1 = getLayoutInflater().inflate(R.layout.view_pay_dialog, null);
                    final EditText view_pay_ed = view1.findViewById(R.id.view_pay_ed);
                    builder.setView(view1);
                    builder.setCancelable(true);
                    final AlertDialog dialog = builder.create();
                    Button fou = view1.findViewById(R.id.view_pay_fou);
                    fou.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {

                            dialog.dismiss();
                        }
                    });

                    Button dy = view1.findViewById(R.id.view_pay_dy);
                    dy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            //deductMaterial();
                            for (int i = 0; i < getGoodsList().size(); i++ ){
                                Document goodsDoc = CDBHelper.getDocByID(getGoodsList().get(i).getId());
                                if (goodsDoc.getInt("status") != 2){
                                    getGoodsList().remove(i);
                                    i--;
                                }
                            }
                            saveOrder(view_pay_ed.getText().toString());
//                            Intent intent = new Intent(MainActivity.this, ShowParticularsActivity.class);
//                            startActivity(intent);
//                            finish();

                            //cleanCount();
                            if(hasPrinterGoods()){
                                waitForPrinter();
                            }
                            else{
                                Intent intent = new Intent(MainActivity.this, ShowParticularsActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            dialog.dismiss();

                        }
                    });

                    dialog.show();

                } else {

                    Toast.makeText(MainActivity.this, "订单为空！", Toast.LENGTH_SHORT).show();
                }

            }

        });
    }
    ListenerToken listenerToken;
    Query myquery;
    AlertDialog  printerDialog;
    private TextView printerLab;
    private void waitForPrinter(){
        final StringBuilder printErrorDish = new StringBuilder();
        myquery = listsLiveQuery(myApp.getTable_sel_obj().getId());
        QueryChangeListener queryChangeListener = new QueryChangeListener() {
            @Override
            public void changed(QueryChange change)
            {
                ResultSet rs = change.getResults();
                Result result;
                while ((result = rs.next()) != null)
                {
                    String id=result.getString(0);

                    MutableDocument obj = CDBHelper.getDocByID(id).toMutable();
                    int flag = obj.getInt("printFlag");
                    MyLog.e("printerFlag------"+flag);
                    obj.setInt("printFlag",-1);
                    CDBHelper.saveDocument(obj);

                    if(flag==2){
                        stopTimer();
                        cancelChangeListener();
                        printerLab.setText("*打印完成*");
                        Message message = new Message();
                        message.what = 2;
                        handler.sendMessageDelayed(message,1000);
                    }
                    else if(flag==1){
                        stopTimer();
                        cancelChangeListener();
                        printErrorDish.append("*打印失败菜品*"+"\n");
                        Array array = obj.getArray("goodsList");
                        for(int i = 0; i< array.count(); i++){
                            Dictionary goodsDic = array.getDictionary(i);
                            if(goodsDic.getInt("submitFlag")==1){
                                Document dishDoc = CDBHelper.getDocByID(goodsDic.getString("dishId"));
                                printErrorDish.append(dishDoc.getString("name")+"\n");
                            }
                        }
                        printerLab.setText(printErrorDish.toString());
                        printerLab.setTextSize(22);
                    }

                }
            }
        };
        listenerToken =  myquery.addChangeListener(queryChangeListener);


        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.order_dialog_printer, null);

        printerLab=(TextView) layout.findViewById(R.id.lab_msg2);

        printerDialog= new  AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_printer)
                .setTitle("正在打印，请等待...")
                .setView(layout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                            Intent intent = new Intent(MainActivity.this, ShowParticularsActivity.class);
                            startActivity(intent);
                            finish();

                    }
                })
                .create();

        printerDialog.show();

        timer.schedule(timerTask,200,1000);
    }
    private Query listsLiveQuery(String tableId) {
        return QueryBuilder.select(SelectResult.expression(Meta.id))
                .from(DataSource.database(CDBHelper.getDatabase()))
                .where(Expression.property("className").equalTo(Expression.string("Order"))
                        .and(Expression.property("printFlag").greaterThan(Expression.intValue(0)))//未打印
                        // .and(Expression.property("deviceType").equalTo(Expression.intValue(0)))//前台机器
                        .and(Expression.property("tableMsgId").equalTo(Expression.string(tableId)))
                        .and(Expression.property("state").equalTo(Expression.intValue(1))));//未结账)
    }
    private  void stopTimer(){
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }
    private void cancelChangeListener(){
        myquery.removeChangeListener(listenerToken);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                printerLab.setText(" "+timerNum);
                MyLog.e("timerNum=",""+timerNum);

                timerNum--;
                if(timerNum<0){
                    stopTimer();
                    cancelChangeListener();
                    printerLab.setText("*打印失败*");
                }
            }
            else if(msg.what ==2){

                printerDialog.cancel();

                  Intent intent = new Intent(MainActivity.this, ShowParticularsActivity.class);
                    startActivity(intent);
                    finish();


            }
            super.handleMessage(msg);
        }
    };

    Timer timer = new Timer();
    int   timerNum = 5;
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };

    private boolean hasPrinterGoods(){
        boolean flag = false;

        List<KitchenClient> kitchenClientList = CDBHelper.getObjByWhere(
                Expression.property("className").equalTo(Expression.string("KitchenClient"))
                        .and(Expression.property("positionType").equalTo(Expression.intValue(1)))
                ,null
                ,KitchenClient.class
        );
        if (kitchenClientList.size() <= 0)
        {
            return flag;
        }

        List<String> dishKindIds = new ArrayList<>();
        for (KitchenClient kitchenClientObj : kitchenClientList)//1 for 遍历所有厨房
        {
            dishKindIds.addAll(kitchenClientObj.getKindIds());
        }
        for(Document obj:goodsList){
            Document dishDoc = CDBHelper.getDocByID(obj.getString("dishId"));
            String dishesKindId = dishDoc.getString("kindId");
            for(String kindId:dishKindIds){
                if(dishesKindId.equals(kindId)){
                    flag=true;
                    break;
                }
            }
            if(flag)
                break;
        }
        return flag;
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


        for (int i = 0;i < getGoodsList().size();i++){
            if (getGoodsList().get(i).getInt("status") == 2){
                CDBHelper.deleDocumentById(getGoodsList().get(i).getId());
                getGoodsList().remove(i);
                i--;
            }else{
                if (getGoodsList().get(i).getInt("goodsType") == 0) {
                    Document document1 = CDBHelper.getDocByID(getGoodsList().get(i).getString("dishId"));
                    total = MyBigDecimal.add(total, MyBigDecimal.mul(document1.getFloat("price"),
                            getGoodsList().get(i).getFloat("count"), 1), 1);
                }
            }
        }
        point = 0;

        point_tv.setVisibility(View.INVISIBLE);

        total_tv.setText("0.0元");

        total = 0;

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

        List<OrderNum> orderNumList = CDBHelper.getObjByWhere(

                Expression.property("className").equalTo(Expression.string("OrderNum"))

                , null

                , OrderNum.class);

        if (orderNumList.size() <= 0)//第一次使用

        {

            OrderNum obj = new OrderNum();
            obj.setChannelId(myApp.getCompany_ID());

            String time = formatter.format(new Date());

            obj.setDate(time);

            obj.setNum(1);

            CDBHelper.createAndUpdate( obj);

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

                CDBHelper.createAndUpdate(obj);

                orderNum = "001";

            } else//同一天

            {

                int newNum = num + 1;

                obj.setNum(newNum);

                CDBHelper.createAndUpdate( obj);

                orderNum = String.format("%3d", newNum).replace(" ", "0");

            }

        }



        return orderNum;

    }

    /**
     * 生成order
     */
    private void saveOrder(String description)
    {
        MutableArray zcArray = new MutableArray();
        boolean isTotal = false;
        total = 0;
        newOrderDoc = new MutableDocument("Order."+ToolUtil.getUUID());
        MyLog.e("newOrderDoc----"+newOrderDoc.getId());
        newOrderDoc.setString("className","Order");
        newOrderDoc.setString("channelId",myApp.getCompany_ID());
        newOrderDoc.setString("tableMsgId",myApp.getTable_sel_obj().getId());
        List<Document> orderCList = CDBHelper.getDocmentsByWhere(
                Expression.property("className").equalTo(Expression.string("Order"))
                        .and(Expression.property("state").equalTo(Expression.intValue(1)))
                        .and(Expression.property("tableMsgId").equalTo(Expression.string(myApp.getTable_sel_obj().getId())))
                ,null

        );
        if (orderCList.size() > 0) {
            newOrderDoc.setInt("orderNum",orderCList.get(0).getInt("orderNum") + 1);
            newOrderDoc.setString("serialNum",orderCList.get(0).getString("serialNum"));
        } else {
            newOrderDoc.setInt("orderNum",1);
            newOrderDoc.setString("serialNum",getOrderSerialNum());
        }
        MutableArray goodsArray = new MutableArray();
        for (int i = 0; i < getGoodsList().size(); i++) {
            MutableDocument document = getGoodsList().get(i).toMutable();
            document.setInt("status",1);
            if (document.getInt("goodsType") == 2) {
                zcArray.addDictionary(getGoodsDic(document));
                getGoodsList().remove(i);
                i--;
                continue;
            }
            if (document.getInt("goodsType") == 0){
                isTotal = true;
                Document dishDoc = CDBHelper.getDocByID(document.getString("dishId"));
                total = MyBigDecimal.add(total,
                        MyBigDecimal.mul(dishDoc.getFloat("price"),document.getFloat("count"),1),1);
            }
            CDBHelper.saveDocument(document);
        }
        for (Document document : getGoodsList()){
            goodsArray.addDictionary(getGoodsDic(document));
        }
        newOrderDoc.setArray("goodsList",goodsArray);
        if (isTotal) {
            newOrderDoc.setFloat("totalPrice",total);
        }
        newOrderDoc.setInt("state",1);//未买单
        newOrderDoc.setInt("printFlag",0);
        newOrderDoc.setInt("orderType",0);
        newOrderDoc.setInt("deviceType",0);//前台
        newOrderDoc.setString("operator",myApp.getEmployee().getId());
        if (description != null){
            newOrderDoc.setString("description",description);
        }
        CDBHelper.saveDocument(newOrderDoc);
        MyLog.e("存储成功");
        if (zcArray.count() > 0){
            for (int i = 0; i < zcArray.count(); i++){
                MutableDictionary dictionary = zcArray.getDictionary(i);
                MutableDocument document = CDBHelper.getDocByID(dictionary.getString("id")).toMutable();
                document.setInt("status",1);
                CDBHelper.saveDocument(document);
            }
            MutableDocument zcOrderDoc = new MutableDocument("Order."+ToolUtil.getUUID());
            zcOrderDoc.setString("className","Order");
            zcOrderDoc.setString("channelId",myApp.getCompany_ID());
            zcOrderDoc.setString("tableMsgId",myApp.getTable_sel_obj().getId());
            zcOrderDoc.setInt("state",1);//未买单
            zcOrderDoc.setInt("printFlag",0);
            zcOrderDoc.setInt("orderType",2);
            zcOrderDoc.setInt("deviceType",0);//前台
            zcOrderDoc.setFloat("totalPrice",0);
            zcOrderDoc.setArray("goodsList",zcArray);
            zcOrderDoc.setString("operator",myApp.getEmployee().getId());
            if (orderCList.size() > 0) {
                zcOrderDoc.setInt("orderNum",orderCList.get(0).getInt("orderNum") + 1);
                zcOrderDoc.setString("serialNum",orderCList.get(0).getString("serialNum"));
            } else {
                zcOrderDoc.setInt("orderNum",1);
                zcOrderDoc.setString("serialNum",getOrderSerialNum());
            }
            CDBHelper.saveDocument(zcOrderDoc);
        }

    }

    /**
     * 生成Goods副本
     * @param goodsDoc
     * @return
     */
    private MutableDictionary getGoodsDic(Document goodsDoc){
        MutableDictionary goodsDic = new MutableDictionary();
        goodsDic.setString("id",goodsDoc.getId());
        goodsDic.setString("goodsTableMsgId",goodsDoc.getString("goodsTableMsgId"));
        goodsDic.setString("tasteId",goodsDoc.getString("tasteId"));//口味ID
        MyLog.e("dishId-------"+goodsDoc.getString("dishId"));
        goodsDic.setString("dishId",goodsDoc.getString("dishId"));
        goodsDic.setFloat("count",goodsDoc.getFloat("count"));///dsadsa
        goodsDic.setInt("goodsType",goodsDoc.getInt("goodsType"));
        goodsDic.setInt("status",goodsDoc.getInt("status"));//刚点餐 未生成订单
        goodsDic.setInt("submitFlag",goodsDoc.getInt("submitFlag"));
        return goodsDic;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);





        if (resultCode == RESULT_OK && requestCode == 1) {



            //document = CDBHelper.getDocByID(getApplicationContext(),gOrderId);

//            Log.e("document", "" + document.getId());


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
            if (!dishesMessage.isOperation()) {
                upOrderData(dishesMessage);
                isDishes = true;
            }else{
                isDishes = false;
            }
        }
        //没找到菜品，添加菜品

        if (!isDishes && dishesMessage.isOperation()) {

            MutableDocument goodsDoc = new MutableDocument("MsgGoods."+ToolUtil.getUUID());
            goodsDoc.setString("className","MsgGoods");
            goodsDoc.setString("id",goodsDoc.getId());
            goodsDoc.setString("channelId",myApp.getCompany_ID());
            goodsDoc.setString("goodsTableMsgId",myApp.getTable_sel_obj().getId());
            MyLog.e("dishId----"+dishesMessage.getDishes().getId());
            goodsDoc.setString("tasteId",dishesMessage.getDishesTaste());//口味ID
            goodsDoc.setString("dishId",dishesMessage.getDishes().getId());
            goodsDoc.setFloat("count",dishesMessage.getCount());///dsadsa
            goodsDoc.setInt("goodsType",0);
            goodsDoc.setInt("status",2);//刚点餐 未生成订单
            goodsDoc.setString("createdYear","2018");
            goodsDoc.setString("dataType","BaseData");
            getGoodsList().add(goodsDoc);
            CDBHelper.saveDocument(goodsDoc);
        }
        updataTotal();
        updataPoint();
        orderAdapter.notifyDataSetChanged();
        MyLog.e(" Main activity 执行");
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
                Document document = CDBHelper.getDocByID(goodsList.get(i).getString("dishId"));
                t += (document.getFloat("price")*getGoodsList().get(i).getFloat("count"));
            }
            setTotal(t);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        MyLog.e("Main activity onStop");
        EventBus.getDefault().unregister(this);

    }

    //更新订单goodsList数据
    private void upOrderData(DishesMessage dishesMessage) {

        if (!dishesMessage.isOperation()) {
            for (int i = 0; i < goodsList.size();i++) {
                Document docGood = goodsList.get(i);
                if (docGood.getInt("status") == 2){
                    if (docGood.getString("dishId").equals(dishesMessage.getDishes().getId())){
                        if (docGood.getString("tasteId") == null && dishesMessage.getDishesTaste() == null) {
                            if ((goodsList.get(i).getFloat("count") - dishesMessage.getCount()) == 0) {
                                goodsList.remove(docGood);
                                CDBHelper.deleDocument(docGood);
                            } else {
                                MutableDocument document = docGood.toMutable();
                                document.setFloat("count",
                                        MyBigDecimal.sub(docGood.getFloat("count"), dishesMessage.getCount(), 1));
                                MyLog.e(""+document.getFloat("count"));
                                CDBHelper.saveDocument(document);
                            }
                            break;
                        }else if (docGood.getString("tasteId").equals(dishesMessage.getDishesTaste())){
                            if ((goodsList.get(i).getFloat("count") - dishesMessage.getCount()) == 0) {
                                goodsList.remove(docGood);
                                CDBHelper.deleDocument(docGood);

                            } else {
                                MutableDocument document = docGood.toMutable();
                                document.setFloat("count",
                                        MyBigDecimal.sub(docGood.getFloat("count"), dishesMessage.getCount(), 1));
                                CDBHelper.saveDocument(document);
                            }
                            break;
                        }

                    }
                }
            }
            Listener();
        }
    }
}