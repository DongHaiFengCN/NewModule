package doaing.order.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.MutableDocument;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.Vector;

import bean.kitchenmanage.kitchen.KitchenClientC;
import bean.kitchenmanage.order.GoodsC;
import bean.kitchenmanage.order.OrderC;
import bean.kitchenmanage.table.AreaC;
import bean.kitchenmanage.user.CompanyC;
import doaing.mylibrary.MyApplication;
import doaing.order.R;
import doaing.order.untils.BluetoothUtil;
import doaing.order.untils.MyBigDecimal;
import doaing.order.untils.PrintUtils;
import doaing.order.untils.Tool;
import doaing.order.view.adapter.ShowParticularsAdapter;
import tools.CDBHelper;
import tools.MyLog;

import static com.gprinter.service.GpPrintService.ACTION_CONNECT_STATUS;
import static tools.CDBHelper.getFormatDate;
import static tools.CDBHelper.getNianDate;

/*
*
 * Created by lenovo on 2017/12/13.
*/


public class ShowParticularsActivity extends Activity implements View.OnClickListener{

    ListView showListView;
    LinearLayout showButDc,showAllLin;
    LinearLayout showButMd;
    TextView showTvSl;
    TextView showTvArea;
    ImageView showImg;
    //EditText showEdBz;
    ShowParticularsAdapter adatper;
    private List<GoodsC> goodsCList;
    private MyApplication myapp;
    private float all = 0f;

    private BluetoothAdapter btAdapter;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private List<OrderC> orderCList;
    private String areaName, tableName;
    private int selActionId;
    private boolean isSupDishesCheck = false;
    private float supCount;
    private ProgressDialog proDialog =null;
    private Map<String, ArrayList<GoodsC>> allKitchenClientGoods = new HashMap<String, ArrayList<GoodsC>>();
    private Map<String, String> allKitchenClientPrintNames = new HashMap<String, String>();
    private GpService mGpService = null;
    private PrinterServiceConnection conn = null;
    private static final int MAIN_QUERY_PRINTER_STATUS = 0xfe;
    private static final int REQUEST_PRINT_LABEL = 0xfd;
    private static final int REQUEST_PRINT_RECEIPT = 0xfc;
    public static final String TAG = "ShowParticularsActivity";
    private int printerType = 58;
    private static String pIp = "192.168.2.249";
    private static int pPortNum = 9100;
    private EditText editText;
    private List<GoodsC> tmpList;
    private String hintDishes = "";
    PortParameters mPortParam;

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
                    proDialog.setMessage("分单打印失败");


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
    //追加菜品
    private void addDishes(int pos, float counts) {
        GoodsC oldGoods = goodsCList.get(pos);

        OrderC newOrderObj = new OrderC(myapp.getCompany_ID());
        String orderId = CDBHelper.createAndUpdate(getApplicationContext(), newOrderObj);
        newOrderObj.set_id(orderId);

        GoodsC newGoods = null;
        try {
            newGoods = (GoodsC) oldGoods.clone();//向下转型----P2没有被实例化
            newGoods.setDishesCount(counts);
            newGoods.setOrder(orderId);
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (orderCList.size() > 1) {
            newOrderObj.setOrderNum(orderCList.get(0).getOrderNum() + 1);
            newOrderObj.setSerialNum(orderCList.get(0).getSerialNum());
        } else {
            newOrderObj.setOrderNum(1);
            newOrderObj.setSerialNum(Tool.getOrderSerialNum(this));
        }
        newOrderObj.setAllPrice(MyBigDecimal.mul(newGoods.getPrice(), counts, 1));
        newOrderObj.setOrderState(1);//未买单
        newOrderObj.setOrderCType(0);//正常
        newOrderObj.setDeviceType(1);//点餐宝
        newOrderObj.setCreatedTime(getFormatDate());
        newOrderObj.setTableNo(myapp.getTable_sel_obj().getTableNum());
        newOrderObj.setTableName(tableName);
        newOrderObj.setAreaName(areaName);
        newOrderObj.setCreatedYear("2018");
        tmpList = new ArrayList<>();
        tmpList.add(newGoods);
        newOrderObj.setGoodsList(tmpList);
        CDBHelper.createAndUpdate(getApplicationContext(), newOrderObj);
    }
    //退菜
    private void retreatDishes(int pos, float counts) {
        GoodsC oldGoods = goodsCList.get(pos);
        Log.e("Show",""+oldGoods.getDishesCount());
        GoodsC newGoods = null;
        try {
            newGoods = (GoodsC) oldGoods.clone();//向下转型----P2没有被实例化

        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        newGoods.setDishesCount(counts);
        removeGoodsFromOrder(newGoods, 0);//修改正常老订单

        OrderC newOrderObj = new OrderC(myapp.getCompany_ID());
        String orderId = CDBHelper.createAndUpdate(getApplicationContext(), newOrderObj);
        newOrderObj.set_id(orderId);
        if (orderCList.size() > 1) {
            newOrderObj.setOrderNum(orderCList.get(0).getOrderNum() + 1);
            newOrderObj.setSerialNum(orderCList.get(0).getSerialNum());
        } else {
            newOrderObj.setOrderNum(1);
            newOrderObj.setSerialNum(Tool.getOrderSerialNum(this));
        }
        newOrderObj.setAllPrice(MyBigDecimal.mul(oldGoods.getPrice(), counts, 1));
        newOrderObj.setOrderState(1);//未买单
        newOrderObj.setOrderCType(1);//退菜订单
        newOrderObj.setDeviceType(1);//点餐宝
        newOrderObj.setCreatedTime(getFormatDate());
        newOrderObj.setTableNo(myapp.getTable_sel_obj().getTableNum());
        newOrderObj.setTableName(tableName);
        newOrderObj.setAreaName(areaName);
        newOrderObj.setCreatedYear(getNianDate());
        newGoods.setOrder(orderId);
        newGoods.setGoodsType(1);//置成退菜类型
        newGoods.setDishesName(oldGoods.getDishesName() + "(退)");
        tmpList = new ArrayList<>();
        tmpList.add(newGoods);
        newOrderObj.setGoodsList(tmpList);
        CDBHelper.createAndUpdate(getApplicationContext(), newOrderObj);

    }

    private void giveDishes(int pos, float counts) {

        GoodsC oldGoods = goodsCList.get(pos);
        GoodsC newGoods = null;
        try {
            newGoods = (GoodsC) oldGoods.clone();//向下转型----P2没有被实例化
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        newGoods.setDishesCount(counts);
        removeGoodsFromOrder(newGoods, 0);//修改正常老订单

        OrderC newOrderObj = new OrderC(myapp.getCompany_ID());
        String orderId = CDBHelper.createAndUpdate(getApplicationContext(), newOrderObj);
        newOrderObj.set_id(orderId);
        if (orderCList.size() > 1) {
            newOrderObj.setOrderNum(orderCList.get(0).getOrderNum() + 1);
            newOrderObj.setSerialNum(orderCList.get(0).getSerialNum());
        } else {
            newOrderObj.setOrderNum(1);
            newOrderObj.setSerialNum(Tool.getOrderSerialNum(this));
        }
        newOrderObj.setAllPrice(MyBigDecimal.mul(oldGoods.getPrice(), counts, 1));
        newOrderObj.setOrderState(1);//未买单
        newOrderObj.setOrderCType(2);//赠菜订单
        newOrderObj.setDeviceType(1);//点餐宝
        newOrderObj.setCreatedTime(getFormatDate());
        newOrderObj.setTableNo(myapp.getTable_sel_obj().getTableNum());
        newOrderObj.setTableName(tableName);
        newOrderObj.setAreaName(areaName);
        newOrderObj.setCreatedYear(getNianDate());
        newGoods.setOrder(orderId);
        newGoods.setGoodsType(2);//置成赠菜类型
        newGoods.setDishesName(oldGoods.getDishesName() + "(赠)");
        tmpList = new ArrayList<>();
        tmpList.add(newGoods);
        newOrderObj.setGoodsList(tmpList);
        CDBHelper.createAndUpdate(getApplicationContext(), newOrderObj);

    }

    private void backDishes(int pos, float counts) {
        GoodsC oldGoods = goodsCList.get(pos);
        GoodsC newGoods = null;
        try {
            newGoods = (GoodsC) oldGoods.clone();//向下转型----P2没有被实例化
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        newGoods.setDishesCount(counts);
        removeGoodsFromOrder(newGoods, 2);//修改赠菜老订单

        OrderC newOrderObj = new OrderC(myapp.getCompany_ID());
        String orderId = CDBHelper.createAndUpdate(getApplicationContext(), newOrderObj);
        newOrderObj.set_id(orderId);
        if (orderCList.size() > 1) {
            newOrderObj.setOrderNum(orderCList.get(0).getOrderNum() + 1);
            newOrderObj.setSerialNum(orderCList.get(0).getSerialNum());
        } else {
            newOrderObj.setOrderNum(1);
            newOrderObj.setSerialNum(Tool.getOrderSerialNum(this));
        }
        newOrderObj.setAllPrice(MyBigDecimal.mul(oldGoods.getPrice(), counts, 1));
        newOrderObj.setOrderState(1);//未买单
        newOrderObj.setOrderCType(0);//添菜订单
        newOrderObj.setDeviceType(1);//点餐宝
        newOrderObj.setCreatedTime(getFormatDate());
        newOrderObj.setTableNo(myapp.getTable_sel_obj().getTableNum());
        newOrderObj.setTableName(tableName);
        newOrderObj.setAreaName(areaName);
        newOrderObj.setCreatedYear(getNianDate());
        newGoods.setOrder(orderId);
        newGoods.setGoodsType(0);

        String dishesName = oldGoods.getDishesName();
        dishesName = dishesName.substring(0, dishesName.length() - 3);
        newGoods.setDishesName(dishesName);
        tmpList = new ArrayList<>();
        tmpList.add(newGoods);
        newOrderObj.setGoodsList(tmpList);
        CDBHelper.createAndUpdate(getApplicationContext(), newOrderObj);
    }

    private void retreatDishesFromZC(int pos, float counts) {
        GoodsC oldGoods = goodsCList.get(pos);
        GoodsC newGoods = null;
        try {
            newGoods = (GoodsC) oldGoods.clone();//向下转型----P2没有被实例化
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        newGoods.setDishesCount(counts);
        removeGoodsFromOrder(newGoods, 2);//修改老订单 ，2代表从赠菜中移除

        OrderC newOrderObj = new OrderC(myapp.getCompany_ID());
        String orderId = CDBHelper.createAndUpdate(getApplicationContext(), newOrderObj);
        newOrderObj.set_id(orderId);
        if (orderCList.size() > 1) {
            newOrderObj.setOrderNum(orderCList.get(0).getOrderNum() + 1);
            newOrderObj.setSerialNum(orderCList.get(0).getSerialNum());
        } else {
            newOrderObj.setOrderNum(1);
            newOrderObj.setSerialNum(Tool.getOrderSerialNum(this));
        }
        newOrderObj.setAllPrice(MyBigDecimal.mul(oldGoods.getPrice(), counts, 1));
        newOrderObj.setOrderState(1);//未买单
        newOrderObj.setOrderCType(1);//退菜订单
        newOrderObj.setDeviceType(1);//点餐宝
        newOrderObj.setCreatedTime(getFormatDate());
        newOrderObj.setTableNo(myapp.getTable_sel_obj().getTableNum());
        newOrderObj.setTableName(tableName);
        newOrderObj.setAreaName(areaName);
        newOrderObj.setCreatedYear(getNianDate());
        newGoods.setOrder(orderId);
        newGoods.setGoodsType(1);//置成退菜类型
        String dishesName = oldGoods.getDishesName();
        dishesName = dishesName.substring(0, dishesName.length() - 3);
        newGoods.setDishesName(dishesName + "(退)");
        tmpList = new ArrayList<>();
        tmpList.add(newGoods);
        newOrderObj.setGoodsList(tmpList);
        CDBHelper.createAndUpdate(getApplicationContext(), newOrderObj);
    }

    private void removeGoodsFromOrder(GoodsC retreateObj, int type)
    {
        float  retreateCounts = retreateObj.getDishesCount();//数量
        String retreateTaste  = retreateObj.getDishesTaste();//口味
        String retreate =  retreateObj.getDishesName();
        for (int i = 0; i < orderCList.size(); i++)
        {
            if(retreateCounts<=0)
                break;
            OrderC orderObj = orderCList.get(i);
            if (orderObj.getOrderCType() != type)
                continue;

            List<GoodsC> oldGoodsList = orderObj.getGoodsList();
            for (int j = 0; j < oldGoodsList.size(); j++)
            {
                if(retreateCounts<=0)
                    break;

                GoodsC oldGoods = oldGoodsList.get(j);

                if (retreate.equals(oldGoods.getDishesName()))//名字相同
                {
                    if (TextUtils.isEmpty(retreateTaste)&&TextUtils.isEmpty(oldGoods.getDishesTaste()))//口味都为空
                    {
                        if (retreateCounts >= oldGoods.getDishesCount())//退出菜品数量超出原有数量
                        {
                            float retreatePrice = MyBigDecimal.mul(oldGoods.getPrice(), oldGoods.getDishesCount(), 1);
                            orderObj.getGoodsList().remove(oldGoods);
                            j--;
                            if (orderObj.getGoodsList().size() == 0)
                            {
                                CDBHelper.deleteObj(getApplicationContext(), orderObj);
                            } else {
                                float lastPrice = MyBigDecimal.sub(orderObj.getAllPrice(), retreatePrice, 1);
                                orderObj.setAllPrice(lastPrice);
                                CDBHelper.createAndUpdate(getApplicationContext(), orderObj);
                            }
                            retreateCounts = MyBigDecimal.sub(retreateCounts,oldGoods.getDishesCount(),1);
                        } else //数量上有剩余菜品
                        {
                            float retreatePrice = MyBigDecimal.mul(retreateObj.getPrice(), retreateCounts, 1);
                            float lastPrice = MyBigDecimal.sub(orderObj.getAllPrice(), retreatePrice, 1);
                            orderObj.setAllPrice(lastPrice);

                            float  lastCount = MyBigDecimal.sub(oldGoods.getDishesCount(),retreateCounts,1);
                            oldGoods.setDishesCount(lastCount);
                            if (oldGoods.getCreatedTime() != null){
                                oldGoods.setCreatedTime(getFormatDate());
                            }
                            CDBHelper.createAndUpdate(getApplicationContext(), orderObj);
                            retreateCounts = 0;
                        }
                    } else  if (!TextUtils.isEmpty(retreateTaste)&&!TextUtils.isEmpty(oldGoods.getDishesTaste())&&retreateTaste.equals(oldGoods.getDishesTaste()))//口味都不为空且相等
                    {

                        if (retreateCounts >= oldGoods.getDishesCount())//退出菜品数量超出原有数量
                        {
                            float retreatePrice = MyBigDecimal.mul(oldGoods.getPrice(), oldGoods.getDishesCount(), 1);
                            orderObj.getGoodsList().remove(oldGoods);
                            j--;
                            if (orderObj.getGoodsList().size() == 0)
                            {
                                CDBHelper.deleteObj(getApplicationContext(), orderObj);
                            } else {
                                float lastPrice = MyBigDecimal.sub(orderObj.getAllPrice(), retreatePrice, 1);
                                orderObj.setAllPrice(lastPrice);
                                CDBHelper.createAndUpdate(getApplicationContext(), orderObj);
                            }
                            retreateCounts = MyBigDecimal.sub(retreateCounts,oldGoods.getDishesCount(),1);
                        }
                        else //数量上有剩余菜品
                        {
                            float retreatePrice = MyBigDecimal.mul(retreateObj.getPrice(), retreateCounts, 1);
                            float lastPrice = MyBigDecimal.sub(orderObj.getAllPrice(), retreatePrice, 1);
                            orderObj.setAllPrice(lastPrice);

                            float  lastCount = MyBigDecimal.sub(oldGoods.getDishesCount(),retreateCounts,1);
                            oldGoods.setDishesCount(lastCount);
                            if (oldGoods.getCreatedTime() != null){
                                oldGoods.setCreatedTime(getFormatDate());
                            }
                            CDBHelper.createAndUpdate(getApplicationContext(), orderObj);
                            retreateCounts = 0;
                        }
                    }
                }
            }
        }
    }

/**
     * 对正常菜品进行弹框处理
     *
     * @param pos
 **/
    private void normalDishesDialog(final int pos) {
        final int position = pos;
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ShowParticularsActivity.this);
        final View view1 = LayoutInflater.from(ShowParticularsActivity.this).inflate(R.layout.activity_tv_dialog, null);
        alertDialog.setView(view1);
        final AlertDialog dialog = alertDialog.create();
        dialog.setCancelable(false);
        final TextView title = view1.findViewById(R.id.dialog_dishesName);
        title.setText(goodsCList.get(position).getDishesName() + "(已点数量 " + goodsCList.get(pos).getDishesCount() + ")");
        final TextView dialog_count = view1.findViewById(R.id.dialog_count);
        editText = view1.findViewById(R.id.dialog_ed_sl);
        editText.setText(1.0 + "");
        editText.clearFocus();
        editText.setFocusableInTouchMode(false);
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    editText.setFocusableInTouchMode(true);
                    editText.requestFocus();
                    editText.selectAll();
                }
                return false;
            }
        });
        selActionId = R.id.dialog_add_zc;
        //根据ID找到RadioGroup实例
        RadioGroup group = (RadioGroup) view1.findViewById(R.id.dialog_radio);
        //绑定一个匿名监听器
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                selActionId = arg1;
                modificationUnit(view1,title,position,editText);
                if (selActionId == R.id.dialog_add_zc){
                    editText.setText(1.0 + "");
                    dialog_count.setText("追加数量");
                }else if (selActionId == R.id.dialog_delete_tc){

                    editText.setText(1.0 + "");
                    dialog_count.setText("退菜数量");
                }else if (selActionId == R.id.dialog_give_zc){

                    editText.setText(1.0 + "");
                    dialog_count.setText("赠送数量");
                }

            }
        });
        Button btnOk = view1.findViewById(R.id.dialog_tuicai_qd);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editText.getText())) {
                    Toast.makeText(getApplicationContext(), "数量不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (editText.getText().toString().equals(".")){
                    Toast.makeText(getApplicationContext(), "数量不能为.", Toast.LENGTH_SHORT).show();
                    return;
                }
                final float tmpCount = Float.parseFloat(editText.getText().toString());
                if (selActionId == R.id.dialog_add_zc) //添菜
                {

                    if (tmpCount <= 0) {
                        Toast.makeText(getApplicationContext(), "数量要求大于0", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    addDishes(position, tmpCount);

                    setAll();
                    hintDishes = "";
                    proDialog = new ProgressDialog( ShowParticularsActivity.this);
                    proDialog.setTitle("提示");
                    proDialog.setMessage("正在生成订单信息...");
                    proDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
                    proDialog.show();

                    uiHandler.obtainMessage(1).sendToTarget();

                } else if (selActionId == R.id.dialog_delete_tc)//退菜
                {

                    if (tmpCount <= 0) {
                        Toast.makeText(getApplicationContext(), "数量要求大于0", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (isSupDishesCheck){
                        if (tmpCount > supCount) {
                            Toast.makeText(getApplicationContext(), "退菜数量不能大于原始数量", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }else{

                        if (tmpCount > goodsCList.get(position).getDishesCount()) {
                            Toast.makeText(getApplicationContext(), "退菜数量不能大于原始数量", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    try {

                        CDBHelper.getDatabase().inBatch(new TimerTask() {
                                                 @Override
                                                 public void run() {

                                                     GoodsC obj = goodsCList.get(position);
                                                     if (isSupDishesCheck){
                                                         //1
                                                         //2
                                                         final Document doc = CDBHelper.getDocByID(getApplicationContext(),obj.getDishesId());
                                                         for (OrderC orderC : orderCList)
                                                         {
                                                             if (orderC.getOrderCType() != 0){
                                                                 continue;
                                                             }
                                                             for (GoodsC goodsObj : orderC.getGoodsList())
                                                             {
                                                                 if (goodsObj.getDishesId() ==null){
                                                                     continue;
                                                                 }
                                                                 if (goodsObj.getDishesId().equals(obj.getDishesId()))
                                                                 {
                                                                     if (TextUtils.isEmpty(goodsObj.getDishesTaste())&&TextUtils.isEmpty(obj.getDishesTaste())){

                                                                         goodsObj.setDishesCount(MyBigDecimal.mul(goodsObj.getDishesCount(),doc.getFloat("supCount"),1));
                                                                         goodsObj.setPrice(doc.getFloat("supPrice"));
                                                                         goodsObj.setDishesId(doc.getString("supDishesId"));
                                                                         goodsObj.setDishesName(doc.getString("supDishesName"));
                                                                         CDBHelper.createAndUpdate(getApplicationContext(),orderC);
                                                                     }else{
                                                                         if (!TextUtils.isEmpty(goodsObj.getDishesTaste())&&!TextUtils.isEmpty(obj.getDishesTaste())&&goodsObj.getDishesTaste().equals(obj.getDishesTaste())){
                                                                             goodsObj.setDishesCount(MyBigDecimal.mul(goodsObj.getDishesCount(),doc.getFloat("supCount"),1));
                                                                             goodsObj.setPrice(doc.getFloat("supPrice"));
                                                                             goodsObj.setDishesId(doc.getString("supDishesId"));
                                                                             goodsObj.setDishesName(doc.getString("supDishesName"));
                                                                             CDBHelper.createAndUpdate(getApplicationContext(),orderC);
                                                                         }
                                                                     }
                                                                 }
                                                             }
                                                         }
                                                         obj.setDishesName(doc.getString("supDishesName"));
                                                         obj.setPrice(doc.getFloat("supPrice"));
                                                         obj.setDishesCount(goodsCList.get(position).getDishesCount());
                                                         obj.setDishesId(doc.getString("supDishesId"));
                                                         isSupDishesCheck = false;

                                                     }

                                                     retreatDishes(position, tmpCount);

                                                 }
                                             }
                        );
                    } catch (CouchbaseLiteException e) {
                        e.printStackTrace();
                    }

                    setAll();
                    hintDishes = "—退";
                    proDialog = new ProgressDialog( ShowParticularsActivity.this);
                    proDialog.setTitle("提示");
                    proDialog.setMessage("正在生成订单信息...");
                    proDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
                    proDialog.show();

                    uiHandler.obtainMessage(1).sendToTarget();

                } else if (selActionId == R.id.dialog_give_zc){

                    if (tmpCount <= 0) {
                        Toast.makeText(getApplicationContext(), "数量要求大于0", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (tmpCount > goodsCList.get(position).getDishesCount()) {
                        Toast.makeText(getApplicationContext(), "退菜数量不能大于原始数量", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    giveDishes(position, tmpCount);


                    setAll();
                }
                dialog.dismiss();
                adatper.notifyDataSetChanged();

            }
        });

        Button btnCancel = view1.findViewById(R.id.dialog_tuicai_qx);//退菜

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                dialog.dismiss();
            }

        });



        dialog.show();

    }

    private void giveDishesDialog(int pos)//对赠菜的处理窗口
    {
        final int position = pos;

        final AlertDialog.Builder builder = new AlertDialog.Builder(ShowParticularsActivity.this);
        final View view1 = LayoutInflater.from(ShowParticularsActivity.this).inflate(R.layout.activity_tv_dialog, null);
        builder.setView(view1);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        final TextView title = view1.findViewById(R.id.dialog_dishesName);
        title.setText(goodsCList.get(position).getDishesName() + "(已点数量 " + goodsCList.get(pos).getDishesCount() + ")");
        final TextView dialog_count = view1.findViewById(R.id.dialog_count);
        editText = view1.findViewById(R.id.dialog_ed_sl);
        editText.setText(1.0 + "");
        editText.clearFocus();
        editText.setFocusableInTouchMode(false);
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    editText.setFocusableInTouchMode(true);
                    editText.requestFocus();
                    editText.selectAll();
                }
                return false;
            }
        });

        selActionId = R.id.dialog_add_zc;
        //根据ID找到RadioGroup实例
        RadioGroup group = (RadioGroup) view1.findViewById(R.id.dialog_radio);
        //绑定一个匿名监听器
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                selActionId = arg1;
                if (selActionId == R.id.dialog_add_zc){
                    editText.setText(1.0 + "");
                    dialog_count.setText("恢复数量");
                }else{
                    editText.setText(1.0 + "");
                    dialog_count.setText("退菜数量");
                }

            }
        });
        RadioButton rb1 = (RadioButton) view1.findViewById(R.id.dialog_add_zc);
        rb1.setText("恢复价格");
        RadioButton rb2 = (RadioButton) view1.findViewById(R.id.dialog_delete_tc);
        rb2.setVisibility(View.GONE);
        RadioButton rb3 = (RadioButton) view1.findViewById(R.id.dialog_give_zc);
        rb3.setText("退菜处理");

        Button btnOk = view1.findViewById(R.id.dialog_tuicai_qd);//
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(editText.getText())) {
                    Toast.makeText(getApplicationContext(), "数量不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (editText.getText().toString().equals(".")){
                    Toast.makeText(getApplicationContext(), "数量不能为.", Toast.LENGTH_SHORT).show();
                    return;
                }
                float tmpCount = Float.parseFloat(editText.getText().toString());
                if (selActionId == R.id.dialog_add_zc) //恢复价格
                {
                    if (tmpCount <= 0) {
                        Toast.makeText(getApplicationContext(), "数量要求大于0", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (tmpCount > goodsCList.get(position).getDishesCount()) {
                        Toast.makeText(getApplicationContext(), "不能大于原始数量", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    backDishes(position, tmpCount);
                    setAll();

                } else //退菜处理
                {
                    if (tmpCount <= 0) {
                        Toast.makeText(getApplicationContext(), "数量要求大于0", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (tmpCount > goodsCList.get(position).getDishesCount()) {
                        Toast.makeText(getApplicationContext(), "不能大于原始数量", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    retreatDishesFromZC(position, tmpCount);
                    setAll();
                    hintDishes = "—退";
                    proDialog = new ProgressDialog( ShowParticularsActivity.this);
                    proDialog.setTitle("提示");
                    proDialog.setMessage("正在生成订单信息...");
                    proDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
                    proDialog.show();

                    uiHandler.obtainMessage(1).sendToTarget();
                }

                dialog.dismiss();
                adatper.notifyDataSetChanged();
            }
        });

        Button btnCancel = view1.findViewById(R.id.dialog_tuicai_qx);//退菜
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        dialog.show();
    }

/*
*
     * 转换为辅助单位
*/


    private void modificationUnit(View view, final TextView title, final int position,final EditText editText){
        final CheckBox checkBox = view.findViewById(R.id.dialog_delete_supDishes);
        final float unitCount = goodsCList.get(position).getDishesCount();
        if (selActionId == R.id.dialog_add_zc){
            checkBox.setVisibility(View.GONE);
            checkBox.setChecked(false);
            editText.setText(""+1.0);
            title.setText(goodsCList.get(position).getDishesName() + "(已点数量 " + unitCount+ ")");
        }else if (selActionId == R.id.dialog_delete_tc)
        {
            GoodsC obj = goodsCList.get(position);
            if (obj.getDishesId() == null){
                return;
            }
            final Document doc = CDBHelper.getDocByID(getApplicationContext(),obj.getDishesId());

            if (doc.getBoolean("haveSupDishes"))
            {
                checkBox.setVisibility(View.VISIBLE);

                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            supCount = MyBigDecimal.mul(doc.getFloat("supCount"),unitCount,1);
                            title.setText(goodsCList.get(position).getDishesName() + "(已点数量 " + supCount + ")");
                            editText.setText(""+supCount);
                            isSupDishesCheck = true;
                        }else{
                            editText.setText(""+unitCount);
                            title.setText(goodsCList.get(position).getDishesName() + "(已点数量 " + unitCount+ ")");
                            isSupDishesCheck = false;
                        }
                    }
                });

            }else{
                checkBox.setVisibility(View.GONE);
            }
        }else{
            checkBox.setVisibility(View.GONE);
            checkBox.setChecked(false);
            editText.setText(""+1.0);
            title.setText(goodsCList.get(position).getDishesName() + "(已点数量 " + unitCount+ ")");
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        myapp = (MyApplication) getApplication();
        tableName = myapp.getTable_sel_obj().getTableName();
        AreaC areaC = CDBHelper.getObjById(getApplicationContext(), myapp.getTable_sel_obj().getAreaId(), AreaC.class);
        areaName = areaC.getAreaName();
        initView();
        goodsCList = new ArrayList<>();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        adatper = new ShowParticularsAdapter(this, goodsCList);
        showListView.setAdapter(adatper);
        initData();
        //连接打印机服务
        registerPrinterBroadcast();
        connectPrinter();
        showListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                //点击订单OrderC
                GoodsC obj = goodsCList.get(position);
                switch (obj.getGoodsType()) {
                    case 0:
                        normalDishesDialog(position);

                        break;
                    case 2:
                        giveDishesDialog(position);
                        break;
                    case 3:
                        normalDishesDialog(position);
                        break;
                    default:
                        break;
                }
                adatper.notifyDataSetChanged();

            }
        });

//        showEdBz.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                Log.e("ShowPart",""+hasFocus);
//                if (hasFocus){
//
//                }else{
//                    if (TextUtils.isEmpty(showEdBz.getText().toString())){
//                        return ;
//                    }
//                    for (OrderC orderC : orderCList){
//                        orderC.setDesc(showEdBz.getText().toString());
//                        CDBHelper.createAndUpdate(getApplicationContext(),orderC);
//                    }
//                }
//            }
//        });
//        showEdBz.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    showEdBz.setFocusable(true);
//                    showEdBz.setFocusableInTouchMode(true);
//                    showEdBz.requestFocus();
//                }
//
//                return false;
//            }
//        });
    }

    private void initView() {
         showListView = findViewById(R.id.show_listView);
         showButDc = findViewById(R.id.show_but_dc);
         showButMd = findViewById(R.id.show_but_md);
         showTvSl = findViewById(R.id.show_tv_sl);
         showTvArea = findViewById(R.id.show_tv_area);
         showImg = findViewById(R.id.show_img);
        showImg.setOnClickListener(this);
        showButMd.setOnClickListener(this);
        showButDc.setOnClickListener(this);
        findViewById(R.id.show_but_dy).setOnClickListener(this);
        findViewById(R.id.show_list_lin).setOnClickListener(this);
    }

    private void initData()
    {
        proDialog = new ProgressDialog( ShowParticularsActivity.this,R.style.CustomDialog);
        proDialog.setTitle("");
        proDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        proDialog.show();
        myapp.mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                goodsCList.clear();
                all = 0f;
                List<OrderC> orderCList0 =new ArrayList<>();//
                List<OrderC> orderCList1 =new ArrayList<>();//
                List<OrderC> orderCList2 =new ArrayList<>();//

                orderCList = CDBHelper.getObjByWhere(getApplicationContext(),
                        Expression.property("className").equalTo(Expression.string("OrderC"))
                                .and(Expression.property("tableNo").equalTo(Expression.string(myapp.getTable_sel_obj().getTableNum())))
                                .and(Expression.property("orderState").equalTo(Expression.intValue(1)))
                        , Ordering.property("createdTime").descending()
                        , OrderC.class);


                boolean flag = false;
                for (OrderC orderC : orderCList) {
                    Log.e("Show",""+orderC.getDesc());
                    if (orderC.getOrderCType() == 0)//0，正常菜订单
                    {
                        all = MyBigDecimal.add(all, orderC.getAllPrice(), 1);
                        orderCList0.add(orderC);

                    }
                    else if(orderC.getOrderCType() ==1)
                    {
                        orderCList1.add(orderC);
                    }
                    else
                        orderCList2.add(orderC);
                }
                orderCList.clear();
                orderCList.addAll(orderCList1);
                orderCList.addAll(orderCList2);
                orderCList.addAll(orderCList0);
                 //
                  for (OrderC orderC : orderCList)
                  {
                      List<GoodsC> goodsCList1 = orderC.getGoodsList();
                      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                      GoodsC temp ;

                      for (int i = 0 ; i < goodsCList1.size()-1 ;i++){
                          if (goodsCList1.get(i).getCreatedTime() == null){
                              break;
                          }
                          for (int j = i+1 ; j < goodsCList1.size() ;j++){
                              ParsePosition pos1 = new ParsePosition(0);
                              ParsePosition pos2 = new ParsePosition(0);
                              Date goodsTime1 = sdf.parse(goodsCList1.get(i).getCreatedTime(),pos1);
                              Date goodsTime2 = sdf.parse(goodsCList1.get(j).getCreatedTime(),pos2);
                              if(goodsTime1.before(goodsTime2)){
                                  temp = goodsCList1.get(i);
                                  goodsCList1.set(i,goodsCList1.get(j));
                                  goodsCList1.set(j,temp);
                              }
                          }
                      }
                    for (GoodsC goodsb : goodsCList1) {
                        flag = false;
                        for (GoodsC goodsC : goodsCList) {
                            if (goodsC.getDishesName().equals(goodsb.getDishesName())) {
                                if (goodsb.getDishesTaste() != null) {
                                    if (goodsb.getDishesTaste().equals(goodsC.getDishesTaste())) {
                                        float count = MyBigDecimal.add(goodsC.getDishesCount(), goodsb.getDishesCount(), 1);
                                        goodsC.setDishesCount(count);
                                        flag = true;
                                    }

                                } else {

                                    float count = MyBigDecimal.add(goodsC.getDishesCount(), goodsb.getDishesCount(), 1);
                                    goodsC.setDishesCount(count);

                                    flag = true;
                                }

                                break;
                            }


                        }
                        if (!flag) {
                            GoodsC objClone = null;
                            try {
                                objClone = (GoodsC) goodsb.clone();
                            } catch (CloneNotSupportedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            goodsCList.add(objClone);
                            Log.e(TAG,"goods--"+objClone.getCreatedTime());

                        }
                    }
                }

                uiHandler.obtainMessage(0).sendToTarget();
            }
            }
        );
    }
/*
*
    * 查询所有订单并合并
*/
    private void setAll() {
        goodsCList.clear();
        all = 0f;
        List<OrderC> orderCList0 =new ArrayList<>();//
        List<OrderC> orderCList1 =new ArrayList<>();//
        List<OrderC> orderCList2 =new ArrayList<>();//

        orderCList = CDBHelper.getObjByWhere(getApplicationContext(),
                Expression.property("className").equalTo(Expression.string("OrderC"))
                        .and(Expression.property("tableNo").equalTo(Expression.string(myapp.getTable_sel_obj().getTableNum())))
                        .and(Expression.property("orderState").equalTo(Expression.intValue(1)))
                , Ordering.property("createdTime").descending()
                , OrderC.class);

        boolean flag = false;
        for (OrderC orderC : orderCList) {
            if (orderC.getOrderCType() == 0)//0，正常菜订单
            {
                all = MyBigDecimal.add(all, orderC.getAllPrice(), 1);
                orderCList0.add(orderC);
            }
            else if(orderC.getOrderCType() ==1)
            {
                orderCList1.add(orderC);
            }
            else
                orderCList2.add(orderC);
        }
        orderCList.clear();
        orderCList.addAll(orderCList1);
        orderCList.addAll(orderCList2);
        orderCList.addAll(orderCList0);
        for (OrderC orderC : orderCList) {

            List<GoodsC> goodsCList1 = orderC.getGoodsList();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            GoodsC temp ;
            for (int i = 0 ; i < goodsCList1.size()-1 ;i++){
                if (goodsCList1.get(i).getCreatedTime() ==null)
                {
                    break;
                }
                for (int j = i+1 ; j < goodsCList1.size() ;j++){
                    ParsePosition pos1 = new ParsePosition(0);
                    ParsePosition pos2 = new ParsePosition(0);
                    Date goodsTime1 = sdf.parse(goodsCList1.get(i).getCreatedTime(),pos1);
                    Date goodsTime2 = sdf.parse(goodsCList1.get(j).getCreatedTime(),pos2);
                    if(goodsTime1.before(goodsTime2)){

                        temp = goodsCList1.get(i);
                        goodsCList1.set(i,goodsCList1.get(j));
                        goodsCList1.set(j,temp);
                    }
                }
            }

            for (GoodsC goodsb : goodsCList1) {
                flag = false;
                for (GoodsC goodsC : goodsCList) {

                    if (goodsC.getDishesName().equals(goodsb.getDishesName())) {
                        if (goodsb.getDishesTaste() != null) {
                            if (goodsb.getDishesTaste().equals(goodsC.getDishesTaste())) {
                                float count = MyBigDecimal.add(goodsC.getDishesCount(), goodsb.getDishesCount(), 1);
                                goodsC.setDishesCount(count);
                                flag = true;
                            }

                        } else {

                            float count = MyBigDecimal.add(goodsC.getDishesCount(), goodsb.getDishesCount(), 1);
                            goodsC.setDishesCount(count);

                            flag = true;
                        }

                        break;
                    }
                }
                if (!flag) {
                    GoodsC objClone = null;
                    try {
                        objClone = (GoodsC) goodsb.clone();
                    } catch (CloneNotSupportedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    goodsCList.add(objClone);
                }
            }
        }
        showTvArea.setText(areaName+",   "+ myapp.getTable_sel_obj().getTableName());
        showTvSl.setText(goodsCList.size() + "道菜，总计：" + all + "元");
    }

    public void onClick(View view) {
        Intent intent;
        int i = view.getId();
        if (i == R.id.show_but_dc) {
            intent = new Intent(ShowParticularsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        } else if (i == R.id.show_but_md) {
            intent = new Intent(ShowParticularsActivity.this, PayActivity.class);
            startActivity(intent);
            finish();

        } else if (i == R.id.show_img) {
            finish();

        } else if (i == R.id.show_but_dy) {
            if (Tool.isFastDoubleClick()) {
                Toast.makeText(ShowParticularsActivity.this, "点击太快，请稍候", Toast.LENGTH_LONG).show();
                return;
            } else {
                setPrintOrder();
            }

        }
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
        String waiter = "";
        if (myapp.getUsersC() != null){
            waiter = myapp.getUsersC().getEmployeeName();
        }
        List<CompanyC> companyCs = CDBHelper.getObjByClass(getApplicationContext(), CompanyC.class);
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
        PrintUtils.printText(PrintUtils.printTwoData("订单编号", orderCList.get(0).getSerialNum() + "\n"));
        PrintUtils.printText(PrintUtils.printTwoData("下单时间", getFormatDate() + "\n"));

        PrintUtils.printText(PrintUtils.printTwoData("人数：" + myapp.getTable_sel_obj().getCurrentPersions(), "收银员：" + waiter + "\n"));
        PrintUtils.printText("--------------------------------\n");
        PrintUtils.selectCommand(PrintUtils.BOLD);
        PrintUtils.printText(PrintUtils.printThreeData("项目", "数量", "金额\n"));
        PrintUtils.printText("--------------------------------\n");
        PrintUtils.selectCommand(PrintUtils.BOLD_CANCEL);

        for (int j = 0; j < goodsCList.size(); j++) {

            GoodsC goodsC = goodsCList.get(j);
            String taste = "";
            if (goodsC.getDishesTaste() != null) {
                taste = "(" + goodsC.getDishesTaste() + ")";
            }

            PrintUtils.printText(PrintUtils.printThreeData(goodsC.getDishesName() + taste, goodsC.getDishesCount() + "", MyBigDecimal.mul(goodsC.getPrice(),goodsC.getDishesCount(),1) + "\n"));


        }

        PrintUtils.printText("--------------------------------\n");
        PrintUtils.printText(PrintUtils.printTwoData("合计", all + "\n"));
        PrintUtils.printText("--------------------------------\n");
        PrintUtils.printText("\n\n\n\n");
        PrintUtils.closeOutputStream();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

/*
*
     * 厨房分单打印
*/
    private void printOrderToKitchen(List<GoodsC> list)
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

                    //3 for 该厨房下所应得商品
                    for (GoodsC goodsC : list) {

                        if (dishKindId.equals(goodsC.getDishesKindId())) {
                            findflag = true;
                            // g_printGoodsList.remove(goodsC);
                            // 为了降低循环次数，因为菜品只可能在一个厨房打印分发，故分发完后移除掉。
                            oneKitchenClientGoods.add(goodsC);
                        }
                    } //end for 3

            }//end for 2

            if (findflag)  //如果有所属菜品，就去打印
            {

                proDialog.setMessage("正在分发到厨房打印机");

                String clientKtname = "" + kitchenClientObj.getName()+hintDishes;//厨房名称
                String printname = "" + kitchenClientObj.getIndexPrinter();//打印机名称
                Log.e("Port",""+printname);
                int printerId = Integer.parseInt(printname);

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
                        Toast.makeText(ShowParticularsActivity.this,"打印机连接命令发送失败",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ShowParticularsActivity.this,"不属于厨房打印菜品",Toast.LENGTH_SHORT).show();
                proDialog.setMessage("不属于厨房打印菜品");
                uiHandler.obtainMessage(4).sendToTarget();//
            }

        }//end for1


        //2\判断厨房打印机状态是否连接
        //3\如果是连接状态  直接判断打印
        //4\如果未连接  ，连接打印机  并在打印机连接成功信息接收后打印
    }

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
            Toast.makeText(ShowParticularsActivity.this,"分单打印完成",Toast.LENGTH_SHORT).show();
            proDialog.setMessage("厨房打印失败");
            uiHandler.obtainMessage(4).sendToTarget();
        }
        else
        {
            MyLog.d("厨房打印失败");
            proDialog.setMessage("厨房打印失败");
            uiHandler.obtainMessage(4).sendToTarget();
            Toast.makeText(ShowParticularsActivity.this,"厨房打印失败",Toast.LENGTH_SHORT).show();
        }

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
        Log.e("ShowPart",""+clientname);
        //打印并换行
        esc.addPrintAndLineFeed();
        // 打印文字
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印左对齐

        if(printerType == 80)
        {

            // esc.addSetLeftMargin((short)10);
            esc.addText("流水号:" + orderCList.get(0).getSerialNum() + "\n");//流水号生成机制开发
            esc.addText("房间:" + areaName + "   " + "桌位：" + tableName + "\n");// 打印文字
            esc.addText("人数:" + myapp.getTable_sel_obj().getCurrentPersions() + "\n");//流水号生成机制开发
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
            esc.addText("流水号:" + orderCList.get(0).getSerialNum() + "\n");//流水号生成机制开发
            esc.addText("房间:" + areaName + "   " + "桌位：" + tableName + "\n");// 打印文字
            esc.addText("人数:" + myapp.getTable_sel_obj().getCurrentPersions() + "\n");//流水号生成机制开发
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
            esc.addText("--------------------------------\n");
            esc.addPrintAndLineFeed();
            for (OrderC orderC : orderCList) {
                if (orderC.getDesc() != null) {
                    esc.addText("备注信息：             " + orderC.getDesc() + "\n");
                    esc.addPrintAndLineFeed();
                    break;
                }
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

    private Handler uiHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {

            switch (msg.what)
            {
                case 0:
                    proDialog.dismiss();
                    showTvArea.setText(areaName+",   "+ myapp.getTable_sel_obj().getTableName());
                    showTvSl.setText(goodsCList.size() + "道菜，总计：" + all + "元");
                    adatper.notifyDataSetChanged();
                    break;

                case 1:
                    printOrderToKitchen(tmpList);

                    break;

                case 4:
                    proDialog.dismiss();
                    break;

                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

}
