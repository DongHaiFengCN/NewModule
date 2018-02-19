package doaing.order.view;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
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
import com.couchbase.lite.Log;
import com.couchbase.lite.Ordering;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.Vector;

import bean.kitchenmanage.dishes.DishesC;
import bean.kitchenmanage.kitchen.KitchenClientC;
import bean.kitchenmanage.order.GoodsC;
import bean.kitchenmanage.order.OrderC;
import bean.kitchenmanage.order.OrderNum;
import bean.kitchenmanage.table.AreaC;
import bean.kitchenmanage.user.CompanyC;
import butterknife.BindView;
import butterknife.ButterKnife;
import doaing.mylibrary.MyApplication;
import doaing.order.R;
import doaing.order.untils.BluetoothUtil;
import doaing.order.application.CDBHelper;
import doaing.order.module.DishesMessage;
import doaing.order.untils.MyBigDecimal;
import doaing.order.untils.MyLog;
import doaing.order.untils.PrintUtils;
import doaing.order.untils.Tool;

import static com.gprinter.command.GpCom.ACTION_CONNECT_STATUS;
import static doaing.order.application.CDBHelper.getFormatDate;

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
    private BluetoothAdapter btAdapter;
    private BluetoothDevice device;
    private BluetoothSocket socket;
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

    private List<GoodsC> goodsList = new ArrayList<>();
    private List<GoodsC> zcGoodsList = new ArrayList<>();
    private String gOrderId;
    private Document document;
    private Handler mHandler;

    //打印机连接
    private PrinterServiceConnection conn = null;
    private GpService mGpService = null;
    private Map<String, ArrayList<GoodsC>> allKitchenClientGoods = new HashMap<String, ArrayList<GoodsC>>();
    private Map<String, String> allKitchenClientPrintNames = new HashMap<String, String>();
    //  private static String pIp = "192.168.1.249";
    private static String pIp = "192.168.2.249";
    private static int pPortNum = 9100;
    private String tableName, areaName, currentPersions, serNum;
    private static final int MAIN_QUERY_PRINTER_STATUS = 0xfe;
    private static final int REQUEST_PRINT_LABEL = 0xfd;
    private static final int REQUEST_PRINT_RECEIPT = 0xfc;
    //private boolean printerSat = false;
    private int changeFlag = 0;
    private ProgressDialog proDialog =null;
    private int printerType = 58;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("点餐");
        activityFrame = findViewById(R.id.activity_frame);
        setSupportActionBar(toolbar);
        hideNavigationBar();
        //关键下面两句话，设置了回退按钮，及点击事件的效果
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        //连接打印机服务
        registerPrinterBroadcast();
        connectPrinter();
        select(isFlag);
        MyLog.d("onCreate");
    }

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
        unregisterReceiver(PrinterStatusBroadcastReceiver);
        // 2、注销打印消息
        if (conn != null) {
            unbindService(conn); // unBindService
        }
    }

    private void registerPrinterBroadcast() {
        registerReceiver(PrinterStatusBroadcastReceiver, new IntentFilter(ACTION_CONNECT_STATUS));
        // 注册实时状态查询广播
        registerReceiver(PrinterStatusBroadcastReceiver, new IntentFilter(GpCom.ACTION_DEVICE_REAL_STATUS));
//*
//         * 票据模式下，可注册该广播，在需要打印内容的最后加入addQueryPrinterStatus()，在打印完成后会接收到
//         * action为GpCom.ACTION_DEVICE_STATUS的广播，特别用于连续打印，
//         * 可参照该sample中的sendReceiptWithResponse方法与广播中的处理
//         *

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
                    proDialog.setMessage("分单打印失败");


                }
                else if (type == GpDevice.STATE_VALID_PRINTER)//连接成功 5
                {
                    MyLog.d("打印机连接成功");

                    //1、程序连接上厨房端打印机后要进行分厨房打印
                    if (goodsList == null || goodsList.size() <= 0)
                        return;
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
                proDialog.setMessage("分单打印完成");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                uiHandler.obtainMessage(4).sendToTarget();

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

                        uiHandler.obtainMessage(4).sendToTarget();
                    }


                }
            }
        }
    };


    public void setOrderItem(SparseArray<Object> sparseArray) {
        orderItem.add(sparseArray);
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

    //    public void setOrderAdapter(OrderAdapter o) {
//        this.orderAdapter = o;
//
//    }
    public OrderAdapter getOrderAdapter() {
        return orderAdapter;
    }

    public List<SparseArray<Object>> getOrderItem() {
        return orderItem;
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

        total_tv = (TextView) findViewById(R.id.total_tv);

        point_tv = (TextView) findViewById(R.id.point);

        car_iv = (ImageView) findViewById(R.id.car);

        ok_tv = (TextView) findViewById(R.id.ok_tv);
        order_lv =  findViewById(R.id.order_lv);
        final ImageView imageView = (ImageView) findViewById(R.id.shade);

        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.orderList);
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
                if (Tool.isFastDoubleClick()) {
                    Toast.makeText(MainActivity.this, "点击太快，请稍候", Toast.LENGTH_LONG).show();
                    return;
                } else {

                    if (getGoodsList().size() > 0) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        View view1 = getLayoutInflater().inflate(R.layout.view_pay_dialog, null);
                        builder.setView(view1);
                        builder.setCancelable(true);
                        final AlertDialog dialog = builder.create();
                        Button shi = view1.findViewById(R.id.view_pay_shi);
                        Button fou = view1.findViewById(R.id.view_pay_fou);
                        shi.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {

                                changeFlag = 0;
                                proDialog = new ProgressDialog( MainActivity.this);
                                proDialog.setTitle("提示");
                                proDialog.setMessage("正在生成订单信息...");
                                proDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
                                proDialog.show();

                                uiHandler.obtainMessage(0).sendToTarget();

                                dialog.dismiss();


                            }
                        });
                        fou.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                changeFlag = 1;

                                proDialog = new ProgressDialog( MainActivity.this);
                                proDialog.setTitle("提示");
                                proDialog.setMessage("正在生成订单信息...");
                                proDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
                                proDialog.show();

                                uiHandler.obtainMessage(0).sendToTarget();

                                dialog.dismiss();


                            }
                        });

                        Button dy = view1.findViewById(R.id.view_pay_dy);
                        dy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                changeFlag = 2;
                                proDialog = new ProgressDialog( MainActivity.this);
                                proDialog.setTitle("提示");
                                proDialog.setMessage("正在生成订单信息...");
                                proDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
                                proDialog.show();

                                uiHandler.obtainMessage(1).sendToTarget();//

                                dialog.dismiss();

                            }
                        });

                        dialog.show();

                    } else {

                        Toast.makeText(MainActivity.this, "订单为空！", Toast.LENGTH_SHORT).show();
                    }


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


    // 厨房分单打印
    private void printOrderToKitchen()
    {
        //1\ 查询出所有厨房,并分配菜品
        List<KitchenClientC> kitchenClientList = CDBHelper.getObjByClass(getApplicationContext(), KitchenClientC.class);
        if (kitchenClientList.size() <= 0)
        {
            Toast.makeText(getApplicationContext(), "未配置厨房数据", Toast.LENGTH_SHORT).show();
            uiHandler.obtainMessage(4).sendToTarget();
            return;
        }

        allKitchenClientGoods.clear();
        allKitchenClientPrintNames.clear();
        proDialog.setMessage("正在整理数据");
        for (KitchenClientC kitchenClientObj : kitchenClientList)//1 for 遍历所有厨房
        {
            boolean findflag = false;
            ArrayList<GoodsC> oneKitchenClientGoods = new ArrayList<GoodsC>();

            for (String dishKindId : kitchenClientObj.getDishesKindIDList())//2 for 遍历厨房下所含菜系
            {

                for (GoodsC goodsC : goodsList)//3 for 该厨房下所应得商品
                {
                    if (dishKindId.equals(goodsC.getDishesKindId())) {
                        findflag = true;
                        // g_printGoodsList.remove(goodsC);
                        // 为了降低循环次数，因为菜品只可能在一个厨房打印分发，故分发完后移除掉。
                        oneKitchenClientGoods.add(goodsC);
                    }

                }//end for 3

                if (zcGoodsList.size() > 0) {
                    for (GoodsC obj : zcGoodsList) {
                        if (dishKindId.equals(obj.getDishesKindId())) {
                            findflag = true;
                            oneKitchenClientGoods.add(obj);
                        }
                    }
                }
            }//end for 2


            if (findflag)  //如果有所属菜品，就去打印
            {

                proDialog.setMessage("正在分发到厨房打印机");

                String clientKtname = "" + kitchenClientObj.getName();//厨房名称
                String printname = "" + kitchenClientObj.getKitchenAdress();//打印机名称

                int printerId = 0;//Integer.parseInt(printId)-1;

                allKitchenClientGoods.put("" + printerId, oneKitchenClientGoods);
                allKitchenClientPrintNames.put("" + printerId, clientKtname);
                if (!isPrinterConnected(printerId)) // 未连接
                {
                    proDialog.setMessage(""+clientKtname+"厨房打印机未连接，正在连接");
                    if (connectClientPrint(printerId) == 0)
                    {
                        MyLog.d("***********打印机连接命令发送成功");
                        //proDialog.setMessage("打印机连接命令发送成功");
                        //uiHandler.obtainMessage(4).sendToTarget();
                    } else {
                        MyLog.d("***********打印机连接命令发送失败");
                        proDialog.setMessage("打印机连接命令发送失败");
                        uiHandler.obtainMessage(4).sendToTarget();
                    }
                }
                else//已连接
                {
                    proDialog.setMessage(""+clientKtname+"厨房打印机已连接");
                    printGoodsAtRomoteByIndex(printerId);
                }
            }
            else//不分发打印，就直接跳转
            {
                proDialog.setMessage("不属于厨房打印菜品");
                uiHandler.obtainMessage(5).sendToTarget();//
            }

        }//end for1


        //2\判断厨房打印机状态是否连接
        //3\如果是连接状态  直接判断打印
        //4\如果未连接  ，连接打印机  并在打印机连接成功信息接收后打印
    }

    private void printGoodsAtRomoteByIndex(int printerId)
    {
        proDialog.setMessage("正在打印，请稍候");
        //1、程序连接上厨房端打印机后要进行分厨房打印
        ArrayList<GoodsC> myshangpinlist = allKitchenClientGoods.get("" + printerId);

        //2、获得该打印机内容 打印机名称
        String printname = allKitchenClientPrintNames.get("" + printerId);
        String printcontent = getPrintContentforClient(myshangpinlist, printname);
        if (printContent(printcontent, printerId) == 0)//打印成功，使用打印完成回调
        {
            MyLog.d(printname + "分单打印完成");

        }
        else
        {
            MyLog.d("厨房打印失败");
            proDialog.setMessage("厨房打印失败");
            uiHandler.obtainMessage(4).sendToTarget();
        }
        setOrderPrintState(gOrderId);

    }

    private void setOrderPrintState(String orderId) {

        OrderC obj = CDBHelper.getObjById(getApplicationContext(), orderId, OrderC.class);
        obj.setPrintFlag(1);
        CDBHelper.createAndUpdate(getApplicationContext(), obj);
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

    private String getPrintContentforClient(ArrayList<GoodsC> myshangpinlist, String clientname)
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
            esc.addText("人数:" + currentPersions + "\n");//流水号生成机制开发
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
            esc.addText("人数:" + currentPersions + "\n");//流水号生成机制开发
            esc.addText("时间:" + date + " " + endtime + "\n"); // 时间
            esc.addText("--------------------------------\n"); //32横线==16个汉字
            esc.addText("菜品名称                数量    \n"); // 菜品名称+16个空格即占12个汉字长度；  数量+4个空格即占4个汉字长度 )
            esc.addText("\n");

            esc.addSetHorAndVerMotionUnits((byte)8, (byte) 0);//设置移动单位

            for (int i = 0; i < myshangpinlist.size(); i++)
            {
                String dishesName = myshangpinlist.get(i).getDishesName();
                float num = myshangpinlist.get(i).getDishesCount();
                String temp = myshangpinlist.get(i).getDishesTaste();
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
            esc.addText("--------------------------------------------\n");
            esc.addPrintAndLineFeed();

        }


        // 加入查询打印机状态，打印完成后，此时会接收到GpCom.ACTION_DEVICE_STATUS广播
        esc.addQueryPrinterStatus();

        Vector<Byte> datas = esc.getCommand();
        // 发送数据
        Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
        byte[] bytes = ArrayUtils.toPrimitive(Bytes);
        String str = Base64.encodeToString(bytes, Base64.DEFAULT);
        return str;

    }

    private int connectClientPrint(int index) {
        if (mGpService != null) {
            try {
                PortParamDataBase database = new PortParamDataBase(this);
                PortParameters mPortParam = new PortParameters();
                mPortParam = database.queryPortParamDataBase(""+index);
//                mPortParam.setPortType(PortParameters.ETHERNET);
//                mPortParam.setIpAddr(pIp);
//                mPortParam.setPortNumber(pPortNum);
                Log.e("MainActivity",""+mPortParam.getPortNumber()+"----"+mPortParam.getIpAddr());
                int rel = -1;

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

                database.close();
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


     /*打印机连接状态判断
     *
     * @param index
     * @return
     * */


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

    private void connectPrinter() {
        conn = new PrinterServiceConnection();
        Intent intent = new Intent("com.gprinter.aidl.GpPrintService");
        intent.setPackage(getPackageName());
        boolean ret = bindService(intent, conn, Context.BIND_AUTO_CREATE);
        MyLog.e("connectPrinter ret=" + ret);
    }

    /*
     * @author  loongsun
     * @Time    0104
     * @version v2  去掉实时状态判断，这个功能不准确
     */


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

        List<OrderNum> orderNumList = CDBHelper.getObjByWhere(getApplicationContext(), Expression.property("className").equalTo("OrderNum")
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

    private String setPrintOrder() {

        btAdapter = BluetoothUtil.getBTAdapter();
        if (btAdapter != null) {

            device = BluetoothUtil.getDevice(btAdapter);
            if (device != null) {
                try {
                    socket = BluetoothUtil.getSocket(device);
                    PrintUtils.setOutputStream(socket.getOutputStream());

                } catch (IOException e) {
                    e.printStackTrace();
                }

                onPrint();
                return "打印成功";
            }
        }
        return "";
    }

    private void onPrint() {
        List<CompanyC> companyCs = CDBHelper.getObjByClass(getApplicationContext(), CompanyC.class);
        String waiter = myApp.getUsersC().getEmployeeName();

        PrintUtils.selectCommand(PrintUtils.RESET);
        PrintUtils.selectCommand(PrintUtils.LINE_SPACING_DEFAULT);
        PrintUtils.selectCommand(PrintUtils.ALIGN_CENTER);
        if (companyCs.size() != 0) {
            PrintUtils.printText(companyCs.get(0).getPointName() + "\n\n");
        }
        PrintUtils.selectCommand(PrintUtils.DOUBLE_HEIGHT_WIDTH);
        PrintUtils.printText(areaName + "/" + tableName + "\n\n");
        PrintUtils.selectCommand(PrintUtils.NORMAL);
        PrintUtils.selectCommand(PrintUtils.ALIGN_LEFT);
        PrintUtils.printText(PrintUtils.printTwoData("流 水 号", serNum + "\n"));
        PrintUtils.printText(PrintUtils.printTwoData("下单时间", getFormatDate() + "\n"));
        PrintUtils.printText(PrintUtils.printTwoData("人数：" + myApp.getTable_sel_obj().getCurrentPersions(), "收银员：" + waiter + "\n"));
        PrintUtils.printText("--------------------------------\n");
        PrintUtils.selectCommand(PrintUtils.BOLD);
        PrintUtils.printText(PrintUtils.printThreeData("菜品", "数量", "金额\n"));
        PrintUtils.printText("--------------------------------\n");
        PrintUtils.selectCommand(PrintUtils.BOLD_CANCEL);

        List<GoodsC> goodsCList = getGoodsList();

        for (int j = 0; j < goodsCList.size(); j++) {

            GoodsC goodsC = goodsCList.get(j);
            String allPrice = "" + MyBigDecimal.mul(goodsC.getPrice(), goodsC.getDishesCount(), 1);
            String taste = "";
            if (goodsC.getDishesTaste() != null) {
                taste = "(" + goodsC.getDishesTaste() + ")";
            }
            Document document = CDBHelper.getDocByID(getApplicationContext(),goodsC.getDishesId());
            if (document.getBoolean("setMenu")){
                Array array = document.getArray("dishesIdList");
                for (int i = 0; i < array.count(); i++){

                }
                PrintUtils.printText(PrintUtils.printThreeData(goodsC.getDishesName()+taste, goodsC.getDishesCount() + "", allPrice + "\n"));

            }{
                PrintUtils.printText(PrintUtils.printThreeData(goodsC.getDishesName()+taste, goodsC.getDishesCount() + "", allPrice + "\n"));
            }

        }
        if (zcGoodsList.size() > 0) {
            for (GoodsC obj : zcGoodsList)
                PrintUtils.printText(PrintUtils.printThreeData(obj.getDishesName(), obj.getDishesCount() + "", 0 + "\n"));
        }

        PrintUtils.printText("--------------------------------\n");
        total = MyBigDecimal.add(total,0,1);
        PrintUtils.printText(PrintUtils.printTwoData("合计", total + "\n"));
        PrintUtils.printText("--------------------------------\n");
        PrintUtils.printText("\n\n\n\n");
        PrintUtils.closeOutputStream();

    }

    private void saveOrder()
    {

        try {
            CDBHelper.db.inBatch(new TimerTask() {
                @Override
                public void run() {
                    zcGoodsList.clear();
                    OrderC newOrderObj = new OrderC(myApp.getCompany_ID());
                    OrderC zcOrderObj = new OrderC(myApp.getCompany_ID());
                    gOrderId = CDBHelper.createAndUpdate(getApplicationContext(), newOrderObj);
                    newOrderObj.set_id(gOrderId);
                    List<Document> orderCList = CDBHelper.getDocmentsByWhere(getApplicationContext(),
                            Expression.property("className").equalTo("OrderC")
                                    .and(Expression.property("orderState").equalTo(1))
                                    .and(Expression.property("tableNo").equalTo(myApp.getTable_sel_obj().getTableNum()))
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
                    newOrderObj.setTableNo(myApp.getTable_sel_obj().getTableNum());
                    newOrderObj.setTableName(myApp.getTable_sel_obj().getTableName());
                    AreaC areaC = CDBHelper.getObjById(getApplicationContext(), myApp.getTable_sel_obj().getAreaId(), AreaC.class);
                    newOrderObj.setAreaName(areaC.getAreaName());

                    CDBHelper.createAndUpdate(getApplicationContext(), newOrderObj);

                    if (zcGoodsList.size() > 0) {
                        zcOrderObj.setSerialNum(newOrderObj.getSerialNum());
                        zcOrderObj.setOrderState(1);//未买单
                        zcOrderObj.setOrderCType(2);//赠菜
                        zcOrderObj.setDeviceType(1);//点餐宝
                        zcOrderObj.setCreatedTime(newOrderObj.getCreatedTime());
                        zcOrderObj.setTableNo(newOrderObj.getTableNo());
                        zcOrderObj.setTableName(newOrderObj.getTableName());
                        zcOrderObj.setAreaName(newOrderObj.getAreaName());
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
            });
        } catch (CouchbaseLiteException e) {

            e.printStackTrace();
        }


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
                ft.add(R.id.activity_frame, seekT9Fragment);

            } else {
                ft.show(seekT9Fragment);

            }
            isFlag = false;
        } else if (isTrue == false) {
            if (orderFragment == null) {
                orderFragment = new OrderFragment();
                ft.add(R.id.activity_frame, orderFragment);
            } else {
                ft.show(orderFragment);
            }
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
        return true;
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
    private Handler uiHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {

            switch (msg.what)
            {
                case 0:
                    saveOrder();
                    printOrderToKitchen();
                    break;
                case 1:
                    saveOrder();
                    if (setPrintOrder().equals(""))
                    {
                        Toast.makeText(MainActivity.this, "没有链接蓝牙打印机", Toast.LENGTH_LONG).show();
                    }
                    printOrderToKitchen();

                    break;
                case 4:
                    proDialog.dismiss();
                    uiHandler.obtainMessage(5).sendToTarget();//跳转界面
                    break;
                case 5:
                    if(changeFlag==0)
                    {
                        Intent intent = new Intent(MainActivity.this, PayActivity.class);
                        startActivityForResult(intent, 1);
                        finish();
                    }
                    else if(changeFlag ==1)
                    {
                        Intent intent = new Intent(MainActivity.this, DeskActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else if(changeFlag ==2)
                    {
                        Intent intent = new Intent(MainActivity.this, DeskActivity.class);
                        startActivity(intent);
                        finish();

                    }
                    break;

                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

}
