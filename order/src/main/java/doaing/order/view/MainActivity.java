package doaing.order.view;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
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
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Ordering;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gprinter.aidl.GpService;
import com.gprinter.command.EscCommand;
import com.gprinter.command.GpCom;
import com.gprinter.io.GpDevice;
import com.gprinter.io.PortParameters;
import com.gprinter.save.PortParamDataBase;
import com.gprinter.service.GpPrintService;

import org.apache.commons.lang.ArrayUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import bean.kitchenmanage.kitchen.KitchenClient;
import bean.kitchenmanage.order.Goods;
import bean.kitchenmanage.order.Order;
import bean.kitchenmanage.order.OrderNum;
import bean.kitchenmanage.table.Area;
import bean.kitchenmanage.table.Table;
import doaing.mylibrary.MyApplication;
import doaing.order.R;
import doaing.order.module.DishesMessage;
import doaing.order.untils.MyBigDecimal;
import tools.CDBHelper;
import tools.MyLog;
import tools.ToolUtil;


import static com.gprinter.service.GpPrintService.ACTION_CONNECT_STATUS;
import static tools.Method.getFormatDate;
import static tools.Method.getNewFormatDate;
import static tools.Method.getNianDate;

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

    private List<Goods> t9GoodsList;

    private SeekT9Adapter seekT9Adapter;

    private FragmentManager fm;//获得Fragment管理器

    private FragmentTransaction ft; //开启一个事务

    private boolean isFlag = true;


    private MenuItem menuItem;
    private List<Goods> goodsList = new ArrayList<>();

    private List<Goods> zcGoodsList = new ArrayList<>();

    private String gOrderId;
    private EditText editText;
    private Document document;

    private String tableName, areaName, currentPersions, serNum;
    private Map<String, ArrayList<Goods>> allKitchenClientGoods = new HashMap<String, ArrayList<Goods>>();
    private Map<String, String> allKitchenClientPrintNames = new HashMap<String, String>();
    private GpService mGpService = null;
    private PrinterServiceConnection conn = null;
    private static final int MAIN_QUERY_PRINTER_STATUS = 0xfe;
    private static final int REQUEST_PRINT_LABEL = 0xfd;
    private static final int REQUEST_PRINT_RECEIPT = 0xfc;
    //private boolean printerSat = false;
    private String hintDishes = "";
    PortParameters mPortParam;
    private int printerType = 58;
    private Order newOrderObj;
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
        initView();
        select(isFlag);
        //连接打印机服务
        registerPrinterBroadcast();
        connectPrinter();
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

    public void setT9GoodsList(List<Goods> t9GoodsList) {

        this.t9GoodsList = t9GoodsList;

    }



    public List<Goods> getT9GoodsList() {

        return t9GoodsList;

    }


    public OrderAdapter getOrderAdapter() {

        return orderAdapter;

    }

    public List<Goods> getGoodsList() {

        return goodsList;

    }



    public void changeOrderGoodsByT9(Goods goodsObj)

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

                final Goods goods = goodsList.get(position);

                if (goods.getGoodsType() == 2){

                    return ;

                }

                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

                alert.setMessage("是否赠菜")

                        .setPositiveButton("是", new DialogInterface.OnClickListener() {

                            @Override

                            public void onClick(DialogInterface dialog, int which) {
                                //2\

                                goods.setGoodsType(2);

                                goods.setDishesName(goods.getDishesName() + "(赠)");
                                total = MyBigDecimal.sub(total, MyBigDecimal.mul(goods.getPrice(), goods.getDishesCount(), 1), 1);
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    View view1 = getLayoutInflater().inflate(R.layout.view_pay_dialog, null);
                    builder.setView(view1);
                    builder.setCancelable(true);
                    final AlertDialog dialog = builder.create();
                    editText = view1.findViewById(R.id.view_pay_ed);
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
                            saveOrder();
                            printOrderToKitchen(goodsList);
                            Intent intent = new Intent(MainActivity.this, ShowParticularsActivity.class);
                            startActivity(intent);
                            finish();
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
    private void printGoodsAtRomoteByIndex(int printerId)
    {
        //1、程序连接上厨房端打印机后要进行分厨房打印
        ArrayList<Goods> myshangpinlist = allKitchenClientGoods.get("" + printerId);

        //2、获得该打印机内容 打印机名称
        String printname = allKitchenClientPrintNames.get("" + printerId);
        String printcontent = getPrintContentforClient(myshangpinlist, printname);
        if (printContent(printcontent, printerId) == 0)//打印成功，使用打印完成回调
        {
            MyLog.d(printname + "分单打印完成");
            Toast.makeText(MainActivity.this,"分单打印完成",Toast.LENGTH_SHORT).show();

            PortParamDataBase database = new PortParamDataBase(this);
            PortParameters mp =  database.queryPortParamDataBase(""+printerId);
            if(mp.getPortType()==PortParameters.ETHERNET){
                try {
                    mGpService.closePort(printerId);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        }
        else
        {
            MyLog.d("厨房打印失败");
            Toast.makeText(MainActivity.this,"厨房打印失败",Toast.LENGTH_SHORT).show();
        }

    }

    private void printOrderToKitchen(List<Goods> list)
    {
        //1\ 查询出所有厨房,并分配菜品
        List<KitchenClient> kitchenClientList = CDBHelper.getObjByClass( KitchenClient.class);
        if (kitchenClientList.size() <= 0)
        {
            Toast.makeText(getApplicationContext(), "未配置厨房数据", Toast.LENGTH_SHORT).show();
            return;
        }

        allKitchenClientGoods.clear();
        allKitchenClientPrintNames.clear();
        for (KitchenClient kitchenClientObj : kitchenClientList)//1 for 遍历所有厨房
        {
            boolean findflag = false;

            ArrayList<Goods> oneKitchenClientGoods = new ArrayList<Goods>();

            List<String> dishIds =new ArrayList<>();
            List<String> dishKindIds = kitchenClientObj.getKindIds();
            for(String kindId:dishKindIds){
                List<String> dishesIds = CDBHelper.getIdsByWhere(
                        Expression.property("className").equalTo(Expression.string("Dish"))
                                .and(Expression.property("kindId").equalTo(Expression.string( kindId)))
                        , null);

                dishIds.addAll(dishesIds);
            }


            for (String dishId : dishIds)//2 for 遍历厨房下所含菜系
            {
                //3 for 该厨房下所应得商品
                for (Goods goods : list) {

                    if (dishId.equals(goods.getDishesId())) {
                        findflag = true;
                        oneKitchenClientGoods.add(goods);
                    }
                } //end for 3

            }//end for 2

            if (findflag)  //如果有所属菜品，就去打印
            {


                String clientKtname = "" + kitchenClientObj.getName();//厨房名称
                int printerId  =  kitchenClientObj.getPrinterId();//打印机名称


                allKitchenClientGoods.put("" + printerId, oneKitchenClientGoods);
                allKitchenClientPrintNames.put("" + printerId, clientKtname);

                if (!isPrinterConnected(printerId)) // 未连接
                {
                    if (connectClientPrint(printerId) == 0)
                    {
                        MyLog.d("***********打印机连接命令发送成功");
                        //proDialog.setMessage("打印机连接命令发送成功");
                        //uiHandler.obtainMessage(4).sendToTarget();
                    } else {
                        MyLog.d("***********打印机连接命令发送失败");
                        Toast.makeText(MainActivity.this,"打印机连接命令发送失败",Toast.LENGTH_SHORT).show();
                    }
                }
                else//已连接
                {

                    printGoodsAtRomoteByIndex(printerId);


                }
            }
            else//不分发打印，就直接跳转
            {
                //Toast.makeText(MainActivity1.this,"不属于厨房打印菜品",Toast.LENGTH_SHORT).show();
            }

        }//end for1


        //2\判断厨房打印机状态是否连接
        //3\如果是连接状态  直接判断打印
        //4\如果未连接  ，连接打印机  并在打印机连接成功信息接收后打印
    }
//    private void printOrderToKitchen(List<Goods> list)
//    {
//        //1\ 查询出所有厨房,并分配菜品
//        List<KitchenClient> kitchenClientList = CDBHelper.getObjByClass( KitchenClient.class);
//        if (kitchenClientList.size() <= 0)
//        {
//            Toast.makeText(getApplicationContext(), "未配置厨房数据", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        allKitchenClientGoods.clear();
//        allKitchenClientPrintNames.clear();
//        for (KitchenClient kitchenClientObj : kitchenClientList)//1 for 遍历所有厨房
//        {
//            boolean findflag = false;
//            ArrayList<Goods> oneKitchenClientGoods = new ArrayList<Goods>();
//            List<String> dishesIds = CDBHelper.getIdsByWhere(
//                    Expression.property("className").equalTo(Expression.string("Dishes").
//                            add(Expression.string("kindId").equalTo(Expression.string(kitchenClientObj.getId())))),
//                    null);
//
//            for (String dishKindId : dishesIds)//2 for 遍历厨房下所含菜系
//            {
//                //3 for 该厨房下所应得商品
//                for (Goods goods : list) {
//
//                    if (dishKindId.equals(goods.getDishesKindId())) {
//                        findflag = true;
//                        // g_printGoodsList.remove(goods);
//                        // 为了降低循环次数，因为菜品只可能在一个厨房打印分发，故分发完后移除掉。
//                        oneKitchenClientGoods.add(goods);
//                    }
//                } //end for 3
//
//            }//end for 2
//
//            if (findflag)  //如果有所属菜品，就去打印
//            {
//
//
//                String clientKtname = "" + kitchenClientObj.getName()+hintDishes;//厨房名称
//                String printname = "" + kitchenClientObj.getPrinterId();//打印机名称
//                Log.e("Port",""+printname);
//                int printerId = Integer.parseInt(printname);
//
//                allKitchenClientGoods.put("" + printerId, oneKitchenClientGoods);
//                allKitchenClientPrintNames.put("" + printerId, clientKtname);
//
//                if (!isPrinterConnected(printerId)) // 未连接
//                {
//                    if (connectClientPrint(printerId) == 0)
//                    {
//                        MyLog.d("***********打印机连接命令发送成功");
//                        //proDialog.setMessage("打印机连接命令发送成功");
//                        //uiHandler.obtainMessage(4).sendToTarget();
//                    } else {
//                        MyLog.d("***********打印机连接命令发送失败");
//                        Toast.makeText(MainActivity.this,"打印机连接命令发送失败",Toast.LENGTH_SHORT).show();
//                    }
//                }
//                else//已连接
//                {
//
//                    printGoodsAtRomoteByIndex(printerId);
//
//
//                }
//            }
//            else//不分发打印，就直接跳转
//            {
//                Toast.makeText(MainActivity.this,"不属于厨房打印菜品",Toast.LENGTH_SHORT).show();
//            }
//
//        }//end for1
//
//
//        //2\判断厨房打印机状态是否连接
//        //3\如果是连接状态  直接判断打印
//        //4\如果未连接  ，连接打印机  并在打印机连接成功信息接收后打印
//    }

    private int connectClientPrint(int index) {
        if (mGpService != null) {
            try {
                PortParamDataBase database = new PortParamDataBase(this);
                mPortParam = new PortParameters();
                mPortParam = database.queryPortParamDataBase(""+index);
//                mPortParam.setPortType(PortParameters.ETHERNET);
//                mPortParam.setIpAddr(pIp);
//                mPortParam.setPortNumber(pPortNum);
                int rel = -1;

                if (CheckPortParamters(mPortParam)) {
                    try {
                        mGpService.closePort(index);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    switch (mPortParam.getPortType())

                    {
                        case PortParameters.USB:
                            rel = mGpService.openPort(index, mPortParam.getPortType(),
                                    mPortParam.getUsbDeviceName(), 0);
                            break;
                        case PortParameters.ETHERNET:

                            try {
                                rel = mGpService.openPort(index, mPortParam.getPortType(),
                                        mPortParam.getIpAddr(), mPortParam.getPortNumber());
                            } catch (RemoteException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            break;
                        case PortParameters.BLUETOOTH:
                            try {
                                rel = mGpService.openPort(index, mPortParam.getPortType(),
                                        mPortParam.getBluetoothAddr(), 0);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }

                            break;
                    }
                }

                //database.close();
                GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
                if (r != GpCom.ERROR_CODE.SUCCESS) {
                    if (r == GpCom.ERROR_CODE.DEVICE_ALREADY_OPEN) {
                        return 0;
                    } else {
                        return -1;
                    }
                } else
                    return 0;

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return -1;
            }
        } else
            return -1;
    }

    Boolean CheckPortParamters(PortParameters param) {
        boolean rel = false;
        int type = param.getPortType();
        if (type == PortParameters.BLUETOOTH) {
            if (!param.getBluetoothAddr().equals("")) {
                rel = true;
            }
        } else if (type == PortParameters.ETHERNET) {
            if ((!param.getIpAddr().equals("")) && (param.getPortNumber() != 0)) {
                rel = true;
            }
        } else if (type == PortParameters.USB) {
            if (!param.getUsbDeviceName().equals("")) {
                rel = true;
            }
        }
        return rel;
    }

/*
*
     * 打印机连接状态判断
     *
     * @param index
     * @return
*/


    private Boolean isPrinterConnected(int index) {
//        if (!printerSat)
//            return false;
        // 一上来就先连接蓝牙设备
        int status = 0;
        if (mGpService == null)
            return false;
        try {
            status = mGpService.getPrinterConnectStatus(index);
            MyLog.d("printer statue=" + status);
        } catch (RemoteException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return status == GpDevice.STATE_CONNECTED;
    }




    private void setOrderPrintState(String orderId) {

        Order obj = CDBHelper.getObjById( orderId, Order.class);
        obj.setPrintFlag(1);
        CDBHelper.createAndUpdate( obj);
    }

    private int printContent(String content, int printIndex)//0发送数据到打印机 成功 其它错误
    {
        int rel = 0;
        try {
            rel = mGpService.sendEscCommand(printIndex, content);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return -2;
        }
        GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
        if (r != GpCom.ERROR_CODE.SUCCESS) {
            //Toast.makeText(getApplicationContext(), GpCom.getErrorText(r), Toast.LENGTH_SHORT).show();
            return -2;
        } else
            return 0;//把数据发送打印机成功
    }


    private String getPrintContentforClient(ArrayList<Goods> myshangpinlist, String clientname)
    {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
        String date = df.format(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");// 设置日期格式
        String endtime = sdf.format(new Date());
        EscCommand esc = new EscCommand();
        // 打印标题居中
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
        // 设置字体宽高增倍
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF); // 设置为倍高倍宽
        esc.addText(clientname + "\n");// 打印文字
        Log.e("ShowPart",""+clientname);
        //打印并换行
        esc.addPrintAndLineFeed();
        // 打印文字
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印左对齐

        if(printerType == 80)
        {

            // esc.addSetLeftMargin((short)10);
            esc.addText("流水号:" + serNum + "\n");//流水号生成机制开发
            esc.addText("房间:" + areaName + "   " + "桌位：" + tableName + "\n");// 打印文字
            esc.addText("人数:" + myApp.getTable_sel_obj().getCurrentPersons() + "\n");//流水号生成机制开发
            esc.addText("时间:" + date + " " + endtime + "\n"); // 时间
            esc.addText("--------------------------------\n");
            esc.addText("------------------------------------------\n");
            esc.addText("菜品名称         单价     数量    金额 \n"); // 菜品名称(14) 单价(6) 数量(5) 金额(7)
            esc.addText("\n");


            for (int i = 0; i < myshangpinlist.size(); i++) {
                float num = 1; // 数量 默认为1
                num = myshangpinlist.get(i).getDishesCount();
                esc.addText(myshangpinlist.get(i).getDishesName().toString());
                String temp = myshangpinlist.get(i).getDishesTaste();
                if (temp == null || "".equals(temp)) {
                    try {
                        for (int j = 0; j < (18 - myshangpinlist.get(i).getDishesName().toString().getBytes("gbk").length); j++)
                            esc.addText(" ");
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    esc.addText("(" + temp + ")");
                    try {
                        for (int j = 0; j < (18 - myshangpinlist.get(i).getDishesName().toString().getBytes("gbk").length
                                - temp.getBytes("gbk").length - 2); j++)
                            esc.addText(" ");
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                // 查找菜品的单价

                String strprice = "" + myshangpinlist.get(i).getPrice();//""+ MyBigDecimal.div(myshangpinlist.get(i).getAllPrice(),myshangpinlist.get(i).getDishesCount(),2);//myshangpinlist.get(i).getSinglePrice;
                esc.addText(strprice);
                for (int j = 0; j < 9 - strprice.length(); j++)
                    esc.addText(" ");

                esc.addText("" + num);
                for (int j = 0; j < 7 - ("" + num).length(); j++)
                    esc.addText(" ");

                esc.addText("" + (MyBigDecimal.mul(myshangpinlist.get(i).getPrice(), myshangpinlist.get(i).getDishesCount(), 1)) + "\n");
                esc.addPrintAndLineFeed();

            }
            esc.addText("--------------------------------------------\n");
            esc.addPrintAndLineFeed();

            byte len = 0x01;
            esc.addCutAndFeedPaper(len);

        }
        else //58型打印机
        {
            esc.addText("流水号:" + serNum + "\n");//流水号生成机制开发
            esc.addText("房间:" + areaName + "   " + "桌位：" + tableName + "\n");// 打印文字
            esc.addText("人数:" + myApp.getTable_sel_obj().getCurrentPersons() + "\n");//流水号生成机制开发
            esc.addText("时间:" + date + " " + endtime + "\n"); // 时间
            esc.addText("--------------------------------\n"); //32横线==16个汉字
            esc.addText("菜品名称                数量    \n"); // 菜品名称+16个空格即占12个汉字长度；  数量+4个空格即占4个汉字长度 )
            esc.addText("\n");

            esc.addSetHorAndVerMotionUnits((byte)8, (byte) 0);//设置移动单位

            for (int i = 0; i < myshangpinlist.size(); i++)
            {
                String dishesName  ,temp  ;
                float num = myshangpinlist.get(i).getDishesCount();
                temp = myshangpinlist.get(i).getDishesTaste();
                dishesName = myshangpinlist.get(i).getDishesName();
                Document doc = CDBHelper.getDocByID(myshangpinlist.get(i).getDishesId());
                Array array = doc.getArray("dishesIds");
                if (array != null ){
                    for (int d = 0; d < array.count();d++){
                        Document document = CDBHelper.getDocByID(array.getString(d));
                        dishesName = document.getString("name");
                        esc.addSetAbsolutePrintPosition((short) 0);
                        if (temp == null || "".equals(temp))//无口味
                        {
                            esc.addText(dishesName);
                        }
                        else//有口味
                        {
                            esc.addText(dishesName+"("+temp+")");
                        }
                        esc.addSetAbsolutePrintPosition((short) 13);
                        esc.addText("" + num+"\n");
                        //换行
                        esc.addPrintAndLineFeed();
                    }
                }else{
                    esc.addSetAbsolutePrintPosition((short) 0);
                    if (temp == null || "".equals(temp))//无口味
                    {
                        esc.addText(dishesName);
                    }
                    else//有口味
                    {
                        esc.addText(dishesName+"("+temp+")");
                    }
                    esc.addSetAbsolutePrintPosition((short) 13);
                    esc.addText("" + num+"\n");
                    //换行
                    esc.addPrintAndLineFeed();
                }

            }
            esc.addText("--------------------------------\n");
            esc.addPrintAndLineFeed();
            if (newOrderObj.getDescription() != null) {
                esc.addText("备注信息：             " + newOrderObj.getDescription() + "\n");
                esc.addPrintAndLineFeed();
            }

        }

        esc.addText("--------------------------------------------\n");
        esc.addPrintAndLineFeed();
        // 加入查询打印机状态，打印完成后，此时会接收到GpCom.ACTION_DEVICE_STATUS广播
        esc.addQueryPrinterStatus();

        Vector<Byte> datas = esc.getCommand();
        // 发送数据
        Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
        byte[] bytes = ArrayUtils.toPrimitive(Bytes);
        String str = Base64.encodeToString(bytes, Base64.DEFAULT);
        return str;

    }
    private void saveOrder()
    {

        zcGoodsList.clear();

        newOrderObj = new Order();
        newOrderObj.setChannelId(myApp.getCompany_ID());
        Order zcOrderObj = new Order();
        zcOrderObj.setChannelId(myApp.getCompany_ID());
        gOrderId = CDBHelper.createAndUpdate( newOrderObj);
        newOrderObj.setId(gOrderId);

        List<Document> orderCList = CDBHelper.getDocmentsByWhere(
                Expression.property("className").equalTo(Expression.string("Order"))
                        .and(Expression.property("state").equalTo(Expression.intValue(1)))
                        .and(Expression.property("tableId").equalTo(Expression.string(myApp.getTable_sel_obj().getId())))
                , Ordering.property("createdTime").descending()

        );
        if (orderCList.size() > 0) {
            newOrderObj.setOrderNum(orderCList.get(0).getInt("orderNum") + 1);
            newOrderObj.setSerialNum(orderCList.get(0).getString("serialNum"));
        } else {
            newOrderObj.setOrderNum(1);
            newOrderObj.setSerialNum(getOrderSerialNum());
        }

        for (int i = 0; i < goodsList.size(); i++) {
            Goods obj = goodsList.get(i);
            if (obj.getGoodsType() == 2) {
                zcGoodsList.add(obj);
                goodsList.remove(i);
                i--;
                continue;
            }
        }
        newOrderObj.setGoodsList(goodsList);
        newOrderObj.setTotalPrice(total);
        newOrderObj.setState(1);//未买单
        newOrderObj.setOrderType(0);//正常
        newOrderObj.setDeviceType(1);//点餐宝
        newOrderObj.setCreatedTime(getNewFormatDate());
        newOrderObj.setCreatedYear(getNianDate());
        newOrderObj.setTableId(myApp.getTable_sel_obj().getId());
        if (!TextUtils.isEmpty(editText.getText().toString())){
            newOrderObj.setDescription(editText.getText().toString());
        }
        CDBHelper.createAndUpdate(newOrderObj);
        if (zcGoodsList.size() > 0) {
            zcOrderObj.setSerialNum(newOrderObj.getSerialNum());
            zcOrderObj.setState(1);//未买单
            zcOrderObj.setOrderType(2);//赠菜zcOrderObj.setDeviceType(1);//点餐宝
            zcOrderObj.setCreatedTime(newOrderObj.getCreatedTime());
            zcOrderObj.setTableId(newOrderObj.getTableId());
            zcOrderObj.setCreatedYear("2018");
            String id = CDBHelper.createAndUpdate( zcOrderObj);
            zcOrderObj.setGoodsList(zcGoodsList);
            zcOrderObj.setId(id);
            CDBHelper.createAndUpdate( zcOrderObj);
        }
        Table table = CDBHelper.getObjById(newOrderObj.getTableId(), Table.class);
        Area area = CDBHelper.getObjById(table.getAreaId(),Area.class);
        areaName = area.getName();
        tableName = table.getName();
        currentPersions = "" + myApp.getTable_sel_obj().getCurrentPersons();
        if (newOrderObj.getOrderNum() == 1)//第一次下单
            serNum = newOrderObj.getSerialNum();//流水号
        else //多次下单
            serNum = newOrderObj.getSerialNum() + "_" + newOrderObj.getOrderNum();
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



                if (goodsList.get(i).getDishesName().equals(dishesMessage.getDishes().getName())) {



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

            Goods goods = new Goods();
            goods.setDishesKindId(dishesMessage.getDishKindId());
            goods.setDishesTaste(dishesMessage.getDishesTaste());

            goods.setDishesName(dishesMessage.getName());

            goods.setDishesCount(dishesMessage.getCount());

            goods.setDishesId(dishesMessage.getDishes().getId());

            goods.setGoodsType(0);

            goods.setCreatedTime(getFormatDate());

            goods.setPrice(dishesMessage.getDishes().getPrice());

            goodsList.add(goods);

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
    private void registerPrinterBroadcast() {
        registerReceiver(PrinterStatusBroadcastReceiver, new IntentFilter(GpCom.ACTION_CONNECT_STATUS));
        // 注册实时状态查询广播
        registerReceiver(PrinterStatusBroadcastReceiver, new IntentFilter(GpCom.ACTION_DEVICE_REAL_STATUS));
        /**
         * 票据模式下，可注册该广播，在需要打印内容的最后加入addQueryPrinterStatus()，在打印完成后会接收到
         * action为GpCom.ACTION_DEVICE_STATUS的广播，特别用于连续打印，
         * 可参照该sample中的sendReceiptWithResponse方法与广播中的处理
         **/
        registerReceiver(PrinterStatusBroadcastReceiver, new IntentFilter(GpCom.ACTION_RECEIPT_RESPONSE));
    }
    private BroadcastReceiver PrinterStatusBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //  MyLog("NavigationMain--PrinterStatusBroadcastReceiver= " + action);
            if (action.equals(ACTION_CONNECT_STATUS))//连接状态
            {
                int type = intent.getIntExtra(GpPrintService.CONNECT_STATUS, 0);
                int id = intent.getIntExtra(GpPrintService.PRINTER_ID, 0);
                Log.e("**********", "connect status " + type);
                if (type == GpDevice.STATE_CONNECTING)//2
                {
                    MyLog.d("打印机正在连接");
                } else if (type == GpDevice.STATE_NONE)//0
                {
                    MyLog.d("打印机未连接");

                    try {

                        mGpService.queryPrinterStatus(0, 500, MAIN_QUERY_PRINTER_STATUS);
                    } catch (RemoteException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }


                }
                else if (type == GpDevice.STATE_VALID_PRINTER)//连接成功 5
                {
                    MyLog.d("打印机连接成功");
                    printGoodsAtRomoteByIndex(id);
                }
                else if (type == GpDevice.STATE_INVALID_PRINTER)
                {
                    MyLog.e("打印机不能连接");

                }
            }
            else if (action.equals(GpCom.ACTION_RECEIPT_RESPONSE))//本地打印完成回调
            {
                Log.e("Main","-----");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } else if (action.equals(GpCom.ACTION_DEVICE_REAL_STATUS)) {

                // 业务逻辑的请求码，对应哪里查询做什么操作
                int requestCode = intent.getIntExtra(GpCom.EXTRA_PRINTER_REQUEST_CODE, -1);
                // 判断请求码，是则进行业务操作
                if (requestCode == MAIN_QUERY_PRINTER_STATUS) {

                    int status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16);
                    String str;
                    if (status == GpCom.STATE_NO_ERR) {
                        str = "打印机正常";
                        //printerSat = true;
                    }
                    else {
                        str = "打印机 ";
                        if ((byte) (status & GpCom.STATE_OFFLINE) > 0) {
                            str += "脱机";
                        }
                        if ((byte) (status & GpCom.STATE_PAPER_ERR) > 0) {
                            str += "缺纸";
                        }
                        if ((byte) (status & GpCom.STATE_COVER_OPEN) > 0) {
                            str += "打印机开盖";
                        }
                        if ((byte) (status & GpCom.STATE_ERR_OCCURS) > 0) {
                            str += "打印机出错";
                        }
                        if ((byte) (status & GpCom.STATE_TIMES_OUT) > 0) {
                            str += "查询超时";
                        }
                        //printerSat = false;

                        Toast.makeText(getApplicationContext(), "厨房打印机：" + " 状态：" + str, Toast.LENGTH_SHORT)
                                .show();

                    }


                }
            }
        }
    };

    private void connectPrinter() {
        conn = new PrinterServiceConnection();
        Intent intent = new Intent("com.gprinter.aidl.GpPrintService");
        intent.setPackage(getPackageName());
        boolean ret = bindService(intent, conn, Context.BIND_AUTO_CREATE);
        MyLog.e("connectPrinter ret=" + ret);
    }
/*
*
     * @author  loongsun
     * @Time    0104
     * @version v2  去掉实时状态判断，这个功能不准确*/


    class PrinterServiceConnection implements ServiceConnection {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            MyLog.e("PrinterServiceConnection onServiceDisconnected() called");
            mGpService = null;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mGpService = GpService.Stub.asInterface(service);
            //myapp.setmGpService(mGpService);
            MyLog.e("PrinterServiceConnection onServiceConnected() called");

//            try {
//
//                mGpService.queryPrinterStatus(0, 500, MAIN_QUERY_PRINTER_STATUS);
//            } catch (RemoteException e1) {
//                // TODO Auto-generated catch block
//                e1.printStackTrace();
//            }


        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyLog.e("Main activity onStop");
        EventBus.getDefault().unregister(this);
        unregisterReceiver(PrinterStatusBroadcastReceiver);
        // 2、注销打印消息
        if (conn != null) {
            unbindService(conn); // unBindService
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