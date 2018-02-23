package doaing.order.device.kitchen;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Array;
import com.couchbase.lite.Document;
import com.gprinter.aidl.GpService;
import com.gprinter.command.GpCom;
import com.gprinter.io.GpDevice;
import com.gprinter.io.PortParameters;
import com.gprinter.save.PortParamDataBase;
import com.gprinter.service.GpPrintService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bean.kitchenmanage.dishes.DishesKindC;
import bean.kitchenmanage.kitchen.KitchenClientC;
import butterknife.BindView;
import butterknife.OnClick;
import doaing.mylibrary.MyApplication;
import doaing.order.R;
import doaing.order.R2;
import doaing.order.application.CDBHelper;
import doaing.order.device.ListViewAdapter;
import doaing.order.device.PortConfigurationActivity;
import doaing.order.device.PrinterConnectDialog;
import doaing.order.view.DeskActivity;
import view.BaseToobarActivity;
import doaing.order.device.kitchen.AddKitchenAdapter.ViewHolder;

import static doaing.order.device.ListViewAdapter.MESSAGE_CONNECT;

/*
*
 * Created by lenovo on 2018/2/1.

*/


public class AddkitchenActivity extends BaseToobarActivity implements View.OnClickListener{
    ImageView addclientBtBack;
    EditText etClientname;
    Button spPrinter;
    ListView listViewCk;
    Toolbar toolbar;

    private List<String> allDishKindIdList;
    private List<String> listSelectedDocId;
    private MyApplication myapp;
    private int position = 0;
    private AddKitchenAdapter addKitchenAdapter;
    private String selectPrinterName,selectDocId;
    private static int MAX_PRINTER_CNTMY = 1;//默认为1台
    private PortParameters mPortParam[] ;
    private int mPrinterId = 0;
    private GpService mGpService = null;
    private static final int INTENT_PORT_SETTINGS = 0;
    @Override
    protected int setMyContentView() {
        return R.layout.activity_addkitchen;
    }

    @Override
    public void initData(Intent intent) {
        String dysl = intent.getStringExtra("MAX_PRINTER_CNTMY");//获取连接厨房打印机的数量
        if (dysl != null && !dysl.equals("")){
            MAX_PRINTER_CNTMY = Integer.parseInt(dysl)+1;
            mPrinterId = Integer.parseInt(dysl);
            Log.e("Addkitchen",dysl);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        myapp = (MyApplication) getApplication();
        if (mGpService == null){
            mGpService = DeskActivity.getmGpService();
        }
        initViewData();
        initView();
        mPortParam = new PortParameters[MAX_PRINTER_CNTMY];
        initPortParam();
    }

    private void initViewData() {
        toolbar = findViewById(R.id.toolbar);
        etClientname = findViewById(R.id.et_clientname);
        spPrinter = findViewById(R.id.spPrinter);
        listViewCk = findViewById(R.id.listView_ck);
        setToolbarName("添加厨房打印机");
        findViewById(R.id.selectall).setOnClickListener(this);
        findViewById(R.id.bt_deselectall).setOnClickListener(this);
        findViewById(R.id.btn_kcsave).setOnClickListener(this);
        spPrinter.setOnClickListener(this);
    }

    @Override
    protected Toolbar setToolBarInfo() {
        return toolbar;
    }

    public void onClick(View view) {
        int i1 = view.getId();
        if (i1 == R.id.selectall) {
            for (int i = 0; i < allDishKindIdList.size(); i++) {
                AddKitchenAdapter.isSelected.put(i, true);
            }
            addKitchenAdapter.notifyDataSetChanged();

        } else if (i1 == R.id.bt_deselectall) {
            for (int i = 0; i < allDishKindIdList.size(); i++) {
                if (addKitchenAdapter.getIsSelected().get(i)) {
                    addKitchenAdapter.getIsSelected().put(i, false);
                } else {
                    addKitchenAdapter.getIsSelected().put(i, true);
                }
            }
            addKitchenAdapter.notifyDataSetChanged();

        }  else if (i1 == R.id.btn_kcsave) {
            if (isNull(selectDocId)) {
                IpCheck();
            } else {
                UpDataIP(selectDocId);
            }


        } else if (i1 == R.id.spPrinter) {
            //dialog();
            Intent intent = new Intent(AddkitchenActivity.this, PortConfigurationActivity.class);
            startActivityForResult(intent, INTENT_PORT_SETTINGS);

        }
    }
    private boolean isNull(String str)
    {
        return str == null || "".equals(str);
    }

    private void IpCheck()
    {
        if (MAX_PRINTER_CNTMY > 0)
        {
            getcheckinfo();
            if(listSelectedDocId.size()<=0)
            {
                Toast.makeText(this, "请选择要接收的菜品类",Toast.LENGTH_LONG).show();
            }
            else
            {
                savecheckinfo();
                Intent intent = new Intent();
                //设置返回数据
                this.setResult(2015, intent);
                this.finish();
            }
        }
        else
        {
            Toast.makeText(this, "请选择打印机",Toast.LENGTH_LONG).show();
        }
    }

    //保存到数据库
    private void savecheckinfo() {

        KitchenClientC obj=new KitchenClientC(myapp.getCompany_ID());

        obj.setName(etClientname.getText().toString().trim());
        obj.setKitchenAdress(""+MAX_PRINTER_CNTMY);

        List<String> dishKindIDList=new ArrayList<String>();
        for(int i=0;i<listSelectedDocId.size();i++)
        {
            dishKindIDList.add(listSelectedDocId.get(i));
        }
        obj.setDishesKindIDList(dishKindIDList);
        CDBHelper.createAndUpdate(getApplicationContext(),obj);

    }

    //打勾的菜类添加到listSelectedDocId
    private void getcheckinfo()
    {
        for (int i = 0; i < allDishKindIdList.size(); i++)
        {
            if (addKitchenAdapter.getIsSelected().get(i))
            {

                listSelectedDocId.add(allDishKindIdList.get(i));
            }
        }
    }

    //编辑模式进来保存
    private void UpDataIP( String docId)
    {
        if (!isNull(spPrinter.getText().toString()))
        {
            getcheckinfo();
            if(listSelectedDocId.size()<=0)
            {
                Toast.makeText(this, "请选择要接收的菜品类",Toast.LENGTH_LONG).show();
            }
            else
            {
                Updatecheckinfo(docId);
                Intent intent = new Intent();
                //把返回数据存入Intent
                //intent.putExtra("result", "My name is xxx");
                //设置返回数据
                this.setResult(2015, intent);
                this.finish();
            }
        }

        else
        {
            Toast.makeText(this, "请正确输入IP地址",Toast.LENGTH_LONG).show();
        }
    }

    //保存数据到数据库
    private void Updatecheckinfo(String docId) {
        List<String> dishKindIDList=new ArrayList<String>();
        for(int i=0;i<listSelectedDocId.size();i++)
        {
            dishKindIDList.add(listSelectedDocId.get(i));
        }
        Document docKitchenClient=CDBHelper.getDocByID(getApplicationContext(),docId);
        if(docKitchenClient!=null)
        {
            docKitchenClient.setString("name",etClientname.getText().toString().trim());
            docKitchenClient.setString("kitchenAdress",""+MAX_PRINTER_CNTMY);
            docKitchenClient.setObject("dishesKindIDList",dishKindIDList);
            CDBHelper.saveDocument(getApplicationContext(),docKitchenClient);
        }

    }

    private void initView() {
        allDishKindIdList = new ArrayList<>();
        listSelectedDocId = new ArrayList<>();
        allDishKindIdList = CDBHelper.getIdsByClass(getApplicationContext(), DishesKindC.class);
        addKitchenAdapter = new AddKitchenAdapter(AddkitchenActivity.this, allDishKindIdList);
        listViewCk.setAdapter(addKitchenAdapter);
        listViewCk.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(AddkitchenActivity.this,""+position,Toast.LENGTH_SHORT).show();
                ViewHolder viewHolder = (ViewHolder) view.getTag();
                viewHolder.itemAddkitchenCb.toggle();
                // 将CheckBox的选中状况记录下来
                addKitchenAdapter.getIsSelected().put(position, viewHolder.itemAddkitchenCb.isChecked());
                addKitchenAdapter.notifyDataSetChanged();
            }
        });

        Intent intent=getIntent();
        selectPrinterName=intent.getStringExtra("ipcontent");//
        String strname=intent.getStringExtra("clientname");//厨房名字
        selectDocId=intent.getStringExtra("docId");

        etClientname= findViewById(R.id.et_clientname);

        if(selectPrinterName==null||"".equals(selectPrinterName))
        {}
        else
        {
            etClientname.setText(strname);
            MAX_PRINTER_CNTMY = Integer.parseInt(selectPrinterName);
        }
        //初始化 选中的数据
        if (selectDocId == null || "".equals(selectDocId)) {

            Log.e("test", "here1");

        } else {

            Initcpplfromdata(selectDocId);

        }
    }

    private void Initcpplfromdata(String docId)
    {
        Document docKC = CDBHelper.getDocByID(getApplicationContext(),docId);

        if(docKC!=null)
        {

            Array dishKindIDList=docKC.getArray("dishesKindIDList");
            for (int i = 0; i < allDishKindIdList.size(); i++)
            {
                for(int j=0;j<dishKindIDList.count();j++)
                {
                    String DishKindId=dishKindIDList.getString(j);

                    if (allDishKindIdList.get(i).equals(DishKindId))
                    {
                        addKitchenAdapter.getIsSelected().put(i, true);
                        break;
                    }
                }
            }
            // 刷新listview和TextView的显示
           addKitchenAdapter.notifyDataSetChanged();
        }


    }

    //初始化打印机
    private void initPortParam() {
        boolean[] state = new boolean[MAX_PRINTER_CNTMY];
        for (int i = 0; i < MAX_PRINTER_CNTMY; i++) {
            state[i] = false;
        }
        for (int i = 0; i < MAX_PRINTER_CNTMY; i++) {
            try {
                if (mGpService.getPrinterConnectStatus(i) == GpDevice.STATE_CONNECTED) {
                    state[i] = true;
                }
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        for (int i = 0; i < MAX_PRINTER_CNTMY; i++) {
            PortParamDataBase database = new PortParamDataBase(this);
            mPortParam[i] = new PortParameters();
            mPortParam[i] = database.queryPortParamDataBase("" + i);
            mPortParam[i].setPortOpenState(state[i]);
            if (mPortParam[i].getPortOpenState()== true){
                spPrinter.setText("连接成功");
            }
            if (selectPrinterName == null && selectPrinterName.equals("")){
                    spPrinter.setText("点击配置");
            }

        }
    }
    public Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case MESSAGE_CONNECT:
                    connectOrDisConnectToDevice(message.arg1);
            }
            return false;
        }
    });

    void connectOrDisConnectToDevice(int PrinterId) {
        mPrinterId = PrinterId;
        int rel = 0;
        if (mPortParam[PrinterId].getPortOpenState() == false) {
            if (CheckPortParamters(mPortParam[PrinterId]))
            {
                try {
                    mGpService.closePort(mPrinterId);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                switch (mPortParam[PrinterId].getPortType()) {
                    case PortParameters.USB:
                        try {

                            rel = mGpService.openPort(PrinterId, mPortParam[PrinterId].getPortType(),
                                    mPortParam[PrinterId].getUsbDeviceName(), 0);
                        } catch (RemoteException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        break;
                    case PortParameters.ETHERNET:
                        try {
                            rel = mGpService.openPort(PrinterId, mPortParam[PrinterId].getPortType(),
                                    mPortParam[PrinterId].getIpAddr(), mPortParam[PrinterId].getPortNumber());
                        } catch (RemoteException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        break;
                    case PortParameters.BLUETOOTH:
                        try {
                            rel = mGpService.openPort(PrinterId, mPortParam[PrinterId].getPortType(),
                                    mPortParam[PrinterId].getBluetoothAddr(), 0);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
                if (r != GpCom.ERROR_CODE.SUCCESS) {
                    if (r == GpCom.ERROR_CODE.DEVICE_ALREADY_OPEN) {
                        mPortParam[PrinterId].setPortOpenState(true);
                    } else {
                        messageBox(GpCom.getErrorText(r));
                    }
                }
            } else {
                messageBox(getString(R.string.port_parameters_wrong));
            }
        } else {
            setProgressBarIndeterminateVisibility(true);
            try {
                mGpService.closePort(PrinterId);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBroadcast();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        this.unregisterReceiver(PrinterStatusBroadcastReceiver);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_PORT_SETTINGS) {
            // getIP settings info from IP settings dialog
            if (resultCode == RESULT_OK) {
                Bundle bundle = new Bundle();
                bundle = data.getExtras();
                //Log.d(DEBUG_TAG, "PrinterId " + mPrinterId);
                int param = bundle.getInt(GpPrintService.PORT_TYPE);
                mPortParam[mPrinterId].setPortType(param);
                //Log.d(DEBUG_TAG, "PortType " + param);
                String str = bundle.getString(GpPrintService.IP_ADDR);
                mPortParam[mPrinterId].setIpAddr(str);
                //Log.d(DEBUG_TAG, "IP addr " + str);
                param = bundle.getInt(GpPrintService.PORT_NUMBER);
                mPortParam[mPrinterId].setPortNumber(param);
                //Log.d(DEBUG_TAG, "PortNumber " + param);
                str = bundle.getString(GpPrintService.BLUETOOT_ADDR);
                mPortParam[mPrinterId].setBluetoothAddr(str);
                //Log.d(DEBUG_TAG, "BluetoothAddr " + str);
                str = bundle.getString(GpPrintService.USB_DEVICE_NAME);
                mPortParam[mPrinterId].setUsbDeviceName(str);
                //Log.d(DEBUG_TAG, "USBDeviceName " + str);
                if (CheckPortParamters(mPortParam[mPrinterId])) {
                    PortParamDataBase database = new PortParamDataBase(this);
                    database.deleteDataBase("" + mPrinterId);
                    database.insertPortParam(mPrinterId, mPortParam[mPrinterId]);
                    Message message = new Message();
                    message.what = MESSAGE_CONNECT;
                    message.arg1 = mPrinterId;
                    mHandler.sendMessage(message);
                } else {
                    messageBox(getString(R.string.port_parameters_wrong));
                }


            } else {
                messageBox(getString(R.string.port_parameters_is_not_save));
            }
        }

    }
    private void messageBox(String err) {
        Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
    }


    //注册广播
    private void registerBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(GpCom.ACTION_CONNECT_STATUS);
        this.registerReceiver(PrinterStatusBroadcastReceiver, filter);
    }

    //打印机状态广播接收器
    private BroadcastReceiver PrinterStatusBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (GpCom.ACTION_CONNECT_STATUS.equals(intent.getAction())) {
                int type = intent.getIntExtra(GpPrintService.CONNECT_STATUS, 0);
                int id = intent.getIntExtra(GpPrintService.PRINTER_ID, 0);

                if (type == GpDevice.STATE_CONNECTING)  //2 正在连接
                {
                    setProgressBarIndeterminateVisibility(true);
                    mPortParam[id].setPortOpenState(false);

                } else if (type == GpDevice.STATE_NONE)//0 没有连接
                {
                    setProgressBarIndeterminateVisibility(false);
                    mPortParam[id].setPortOpenState(false);
                } else if (type == GpDevice.STATE_VALID_PRINTER)//5 连接成功
                {
                    setProgressBarIndeterminateVisibility(false);
                    mPortParam[id].setPortOpenState(true);

                } else if (type == GpDevice.STATE_INVALID_PRINTER)//4 连接中断
                {
                    setProgressBarIndeterminateVisibility(false);
                    messageBox("Please use Gprinter!");
                }
            }
        }
    };
}
