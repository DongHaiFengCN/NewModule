package doaing.order.device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gprinter.aidl.GpService;
import com.gprinter.command.GpCom;
import com.gprinter.io.GpDevice;
import com.gprinter.io.PortParameters;
import com.gprinter.save.PortParamDataBase;
import com.gprinter.service.GpPrintService;
import com.journeyapps.barcodescanner.CaptureActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.kitchenmanage.qrcode.qrcodeC;
import butterknife.BindView;
import doaing.mylibrary.MyApplication;
import doaing.order.R;
import doaing.order.application.CDBHelper;
import view.BaseToobarActivity;

import static doaing.order.device.ListViewAdapter.MESSAGE_CONNECT;

/*
*
 * Created by lenovo on 2018/1/27.

*/


public class PrinterConnectDialog extends BaseToobarActivity {

    private final static String DEBUG_TAG = "SamleApp";
    private static final int INTENT_PORT_SETTINGS = 0;
    ListView list;
    Toolbar toolbar;
    ImageView btnWxaddPay;
    TextView qrcodeWxcontent;
    CheckBox ifqrcodeprint1;
    ImageView btnZfbaddPay;
    TextView qrcodeZfbcontent;
    CheckBox ifqrcodeprint2;
    TextView msgtxt1;
    EditText printnums;
    private ListViewAdapter mListViewAdapter = null;
    private List<Map<String, Object>> mList = null;
    private static int MAX_PRINTER_CNTMY = 4;//显示打印机有多少台
    private PortParameters mPortParam[] = new PortParameters[MAX_PRINTER_CNTMY];
    private int mPrinterId = 0;
    private GpService mGpService = null;
    private MyApplication myapp;
    private final static int SCANNIN_GREQUEST_CODE1 = 1;
    private final static int SCANNIN_GREQUEST_CODE2 = 2;
    private qrcodeC obj_qrcodepay;

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        registerBroadcast();
        Log.e(DEBUG_TAG, "onResume");
    }

    @Override
    protected int setMyContentView() {
        return R.layout.activity_printer;
    }

    @Override
    public void initData(Intent intent) {

        Log.e(DEBUG_TAG, "onCreate ");
        myapp = (MyApplication) getApplication();
        if (mGpService == null){
            mGpService = CDBHelper.getmGpService();
        }
        initPortParam();
        initView();
        initQrPay();
    }

    @Override
    protected Toolbar setToolBarInfo() {
        return toolbar;
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
                    Log.e(DEBUG_TAG, "printer connected");
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
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        Log.e(DEBUG_TAG, "onDestroy ");
        super.onDestroy();
        this.unregisterReceiver(PrinterStatusBroadcastReceiver);
    }

    private void initView() {
        list = findViewById(R.id.activity_printer_list);
        toolbar = findViewById(R.id.toolbar);
        btnWxaddPay = findViewById(R.id.btn_wxadd_pay);
        qrcodeWxcontent = findViewById(R.id.tv_wxqrcode_content);
        ifqrcodeprint1 = findViewById(R.id.cbox_qrcode_ifprint1);
        btnZfbaddPay = findViewById(R.id.btn_zfbadd_pay);
        qrcodeZfbcontent = findViewById(R.id.tv_zfbqrcode_content);
        ifqrcodeprint2 = findViewById(R.id.cbox_qrcode_ifprint2);
        msgtxt1 = findViewById(R.id.msgtxt1);
        printnums = findViewById(R.id.edt_print_nums);
        setToolbarName("蓝牙打印机");
        mList = getOperateItemData();
        mListViewAdapter = new ListViewAdapter(this, mList, mHandler);
        list.setAdapter(mListViewAdapter);
        list.setOnItemClickListener(new TitelItemOnClickLisener());
        list.setOnItemLongClickListener(new TitelItemOnLongClickLisener());
    }
    private void initQrPay()
    {
        List<qrcodeC> qrCodeDoc= CDBHelper.getObjByClass(getApplicationContext(),qrcodeC.class);
        if(qrCodeDoc.size()>0)
        {
            obj_qrcodepay = qrCodeDoc.get(0);

            qrcodeWxcontent.setText(obj_qrcodepay.getWxUrl());//
            qrcodeZfbcontent.setText(obj_qrcodepay.getZfbUrl());//


            if(obj_qrcodepay.isTbPrintFlag())
                ifqrcodeprint1.setChecked(true);
            else
                ifqrcodeprint1.setChecked(false);

            if(obj_qrcodepay.isZfbPrintFlag())
                ifqrcodeprint2.setChecked(true);
            else
                ifqrcodeprint2.setChecked(false);


            printnums.setText(""+obj_qrcodepay.getNums());
        }
        else
        {
            Log.e("test","find null");
        }



        qrcodeWxcontent.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable s)
            {
                if (qrcodeWxcontent.getText().toString() == null || "".equals(qrcodeWxcontent.getText().toString()))
                    return;
                SaveQrcodepay();
            }
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
                //s:变化前的所有字符； start:字符开始的位置； count:变化前的总字节数；after:变化后的字节数
                //Toast.makeText(getApplicationContext(), "变化前:" + s + ";" + start + ";" + count + ";" + after, Toast.LENGTH_SHORT).show();
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                //S：变化后的所有字符；start：字符起始的位置；before: 变化之前的总字节数；count:变化后的字节数
                //Toast.makeText(getApplicationContext(), "变化后:" + s + ";" + start + ";" + before + ";" + count, Toast.LENGTH_SHORT).show();
            }

        });

        qrcodeZfbcontent.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable s)
            {
                if (qrcodeZfbcontent.getText().toString() == null || "".equals(qrcodeZfbcontent.getText().toString()))
                    return;
                SaveQrcodepay();
            }
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
                //s:变化前的所有字符； start:字符开始的位置； count:变化前的总字节数；after:变化后的字节数
                //Toast.makeText(getApplicationContext(), "变化前:" + s + ";" + start + ";" + count + ";" + after, Toast.LENGTH_SHORT).show();
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                //S：变化后的所有字符；start：字符起始的位置；before: 变化之前的总字节数；count:变化后的字节数
                //Toast.makeText(getApplicationContext(), "变化后:" + s + ";" + start + ";" + before + ";" + count, Toast.LENGTH_SHORT).show();
            }

        });

        printnums.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable s)
            {
                if (printnums.getText().toString() == null || "".equals(printnums.getText().toString()))
                    return;
                SaveQrcodepay();
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
                //s:变化前的所有字符； start:字符开始的位置； count:变化前的总字节数；after:变化后的字节数
                //Toast.makeText(getApplicationContext(), "变化前:" + s + ";" + start + ";" + count + ";" + after, Toast.LENGTH_SHORT).show();
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                //S：变化后的所有字符；start：字符起始的位置；before: 变化之前的总字节数；count:变化后的字节数
                //Toast.makeText(getApplicationContext(), "变化后:" + s + ";" + start + ";" + before + ";" + count, Toast.LENGTH_SHORT).show();
            }

        });

        ifqrcodeprint1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked)
                {
                    if(qrcodeWxcontent.getText().toString()==null||"".equals(qrcodeWxcontent.getText().toString()))
                    {
                        Toast.makeText(getApplicationContext(), "请先扫描微信二维码", Toast.LENGTH_SHORT).show();
                        ifqrcodeprint1.setChecked(false);
                        return;
                    }
                    Log.e("check", "选中");
                }
                else
                {
                    Log.e("check", "取消选中");
                }

                SaveQrcodepay();
            }
        });

        ifqrcodeprint2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked)
                {
                    if(qrcodeZfbcontent.getText().toString()==null||"".equals(qrcodeZfbcontent.getText().toString()))
                    {
                        Toast.makeText(getApplicationContext(), "请先扫描支付宝二维码", Toast.LENGTH_SHORT).show();
                        ifqrcodeprint2.setChecked(false);
                        return;
                    }
                    Log.e("check", "选中");
                }
                else
                {
                    Log.e("check", "取消选中");
                }

                SaveQrcodepay();
            }
        });

    }


    public void OnAddTbQrcodepay(View view)
    {
        Intent intent = new Intent();
        intent.setClass(PrinterConnectDialog.this, CaptureActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, SCANNIN_GREQUEST_CODE1);
    }

    public void OnAddZfbQrcodepay(View view)
    {
        Intent intent = new Intent();
        intent.setClass(PrinterConnectDialog.this, CaptureActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, SCANNIN_GREQUEST_CODE2);
    }

    private void  SaveQrcodepay()
    {
        if(printnums.getText().toString()==null||"".equals(printnums.getText().toString()))
        {
            Toast.makeText(this, "打印份数不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        String printnumsstr=printnums.getText().toString();
        Integer nums=Integer.valueOf(printnumsstr).intValue();
        if(nums<0)
        {
            Toast.makeText(this, "打印份数不能为负数", Toast.LENGTH_SHORT).show();
            return;
        }
        SaveOrUpdateQRPay(nums);
    }

    private void SaveOrUpdateQRPay( Integer nums)
    {


        {
            if(obj_qrcodepay==null)
            {
                obj_qrcodepay = new qrcodeC(myapp.getCompany_ID());

            }


            obj_qrcodepay.setWxUrl(qrcodeWxcontent.getText().toString());
            obj_qrcodepay.setZfbUrl(qrcodeZfbcontent.getText().toString());
            obj_qrcodepay.setTbPrintFlag(ifqrcodeprint1.isChecked());
            obj_qrcodepay.setZfbPrintFlag(ifqrcodeprint2.isChecked());
            obj_qrcodepay.setNums(nums);


            CDBHelper.createAndUpdate(getApplicationContext(),obj_qrcodepay);

        }

    }
    private void registerBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(GpCom.ACTION_CONNECT_STATUS);
        this.registerReceiver(PrinterStatusBroadcastReceiver, filter);
    }

    private BroadcastReceiver PrinterStatusBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (GpCom.ACTION_CONNECT_STATUS.equals(intent.getAction())) {
                int type = intent.getIntExtra(GpPrintService.CONNECT_STATUS, 0);
                int id = intent.getIntExtra(GpPrintService.PRINTER_ID, 0);
                Log.d(DEBUG_TAG, "connect status " + type);

                if (type == GpDevice.STATE_CONNECTING)  //2 正在连接
                {
                    setProgressBarIndeterminateVisibility(true);
                    SetLinkButtonEnable(ListViewAdapter.DISABLE);
                    mPortParam[id].setPortOpenState(false);
                    Map<String, Object> map;
                    map = mList.get(id);
                    map.put(ListViewAdapter.STATUS, getString(R.string.connecting));
                    mList.set(id, map);
                    mListViewAdapter.notifyDataSetChanged();

                } else if (type == GpDevice.STATE_NONE)//0 没有连接
                {
                    setProgressBarIndeterminateVisibility(false);
                    SetLinkButtonEnable(ListViewAdapter.ENABLE);
                    mPortParam[id].setPortOpenState(false);
                    Map<String, Object> map;
                    map = mList.get(id);
                    map.put(ListViewAdapter.STATUS, getString(R.string.connect));
                    mList.set(id, map);
                    mListViewAdapter.notifyDataSetChanged();
                } else if (type == GpDevice.STATE_VALID_PRINTER)//5 连接成功
                {
                    setProgressBarIndeterminateVisibility(false);
                    SetLinkButtonEnable(ListViewAdapter.ENABLE);
                    mPortParam[id].setPortOpenState(true);
                    Map<String, Object> map;
                    map = mList.get(id);
                    map.put(ListViewAdapter.STATUS, getString(R.string.cut));
                    mList.set(id, map);
                    mListViewAdapter.notifyDataSetChanged();
                } else if (type == GpDevice.STATE_INVALID_PRINTER)//4 连接中断
                {
                    setProgressBarIndeterminateVisibility(false);
                    SetLinkButtonEnable(ListViewAdapter.ENABLE);
                    messageBox("Please use Gprinter!");
                }
            }
        }
    };

/*
private String getPortParamInfoString(PortParameters Param) {
        String info = new String();
        info = getString(R.string.port);
        int type = Param.getPortType();
        Log.d(DEBUG_TAG, "Param.getPortType() " + type);
        if (type == PortParameters.BLUETOOTH) {
            info += getString(R.string.bluetooth);
            info += "  " + getString(R.string.address);
            info += Param.getBluetoothAddr();
        } else if (type == PortParameters.USB) {
            info += getString(R.string.usb);
            info += "  " + getString(R.string.address);
            info += Param.getUsbDeviceName();
        } else if (type == PortParameters.ETHERNET) {
            info += getString(R.string.ethernet);
            info += "  " + getString(R.string.ip_address);
            info += Param.getIpAddr();
            info += "  " + getString(R.string.port_number);
            info += Param.getPortNumber();
        } else {
            info = getString(R.string.init_port_info);
        }

        return info;
    }
*/


    private String[] getPortParamInfoString(PortParameters Param) {
        String[] info = new String[2];
        String infotype = new String();
        String infoadress = new String();
        infotype = getString(R.string.port);
        int type = Param.getPortType();

        if (type == PortParameters.BLUETOOTH) {
            infotype = getString(R.string.bluetooth);
            infoadress = getString(R.string.address) + Param.getBluetoothAddr();

        } else if (type == PortParameters.USB) {
            infotype = getString(R.string.usb);
            infoadress = getString(R.string.address) + Param.getUsbDeviceName();

        } else if (type == PortParameters.ETHERNET) {
            infotype = getString(R.string.ethernet);
            infoadress = getString(R.string.ip_address) + Param.getIpAddr() + getString(R.string.port_number) + Param.getPortNumber();

        } else {
            infotype = "未设定";
            infoadress = "不详";
        }
        info[0] = infotype;
        info[1] = infoadress;
        return info;
    }

    void SetPortParamToView(PortParameters Param) {
        Map<String, Object> map;
        map = mList.get(mPrinterId);
        String[] info = getPortParamInfoString(Param);
        map.put(ListViewAdapter.INFO, info);
        mList.set(mPrinterId, map);
        mListViewAdapter.notifyDataSetChanged();
    }

    void SetLinkButtonEnable(String s) {
        Map<String, Object> map;
        for (int i = 0; i < MAX_PRINTER_CNTMY; i++) {
            map = mList.get(i);
            map.put(ListViewAdapter.BT_ENABLE, s);
            mList.set(i, map);
        }
        mListViewAdapter.notifyDataSetChanged();
    }

    private List<Map<String, Object>> getOperateItemData() {
        int[] PrinterID = new int[]{R.string.gprinter001, R.string.gprinter002, R.string.gprinter003, R.string.gprinter004};
        int[] PrinterImage = new int[]{R.drawable.ic_printer, R.drawable.ic_printer, R.drawable.ic_printer, R.drawable.ic_printer};
        Map<String, Object> map;
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < MAX_PRINTER_CNTMY; i++) {
            map = new HashMap<String, Object>();
            map.put(ListViewAdapter.IMG, PrinterImage[i]);
            map.put(ListViewAdapter.TITEL, getString(PrinterID[i]));
            if (mPortParam[i].getPortOpenState() == true)
                map.put(ListViewAdapter.STATUS, getString(R.string.connect));
            else
                map.put(ListViewAdapter.STATUS, getString(R.string.cut));
            String[] str = getPortParamInfoString(mPortParam[i]);
            map.put(ListViewAdapter.INFOTYPE, str[0]);
            map.put(ListViewAdapter.INFOADRESS, str[1]);
            map.put(ListViewAdapter.INFO, str);
            map.put(ListViewAdapter.BT_ENABLE, "enable");
            list.add(map);
        }
        return list;
    }

    class TitelItemOnLongClickLisener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            // TODO Auto-generated method stub
            Log.d(DEBUG_TAG, "TitelItemOnLongClickLisener " + arg2);
            Intent intent = new Intent(GpPrintService.ACTION_PRINT_TESTPAGE);
            intent.putExtra(GpPrintService.PRINTER_ID, arg2);
            sendBroadcast(intent);
            return true;
        }
    }

    class TitelItemOnClickLisener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            // TODO Auto-generated method stub]

            mPrinterId = arg2;
            Intent intent = new Intent(PrinterConnectDialog.this, PortConfigurationActivity.class);
            startActivityForResult(intent, INTENT_PORT_SETTINGS);
        }
    }

    void connectOrDisConnectToDevice(int PrinterId) {
        mPrinterId = PrinterId;
        int rel = 0;
        Log.e(DEBUG_TAG, String.valueOf(mPortParam[PrinterId].getPortOpenState()));
        if (mPortParam[PrinterId].getPortOpenState() == false) {
            if (CheckPortParamters(mPortParam[PrinterId])) {
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
                Log.e(DEBUG_TAG, "result :" + String.valueOf(r));
                if (r != GpCom.ERROR_CODE.SUCCESS) {
                    if (r == GpCom.ERROR_CODE.DEVICE_ALREADY_OPEN) {
                        mPortParam[PrinterId].setPortOpenState(true);
                        Map<String, Object> map = mList.get(PrinterId);
                        map.put(ListViewAdapter.STATUS, getString(R.string.cut));
                        mList.set(PrinterId, map);
                        mListViewAdapter.notifyDataSetChanged();
                    } else {
                        messageBox(GpCom.getErrorText(r));
                    }
                }
            } else {
                messageBox(getString(R.string.port_parameters_wrong));
            }
        } else {
            Log.d(DEBUG_TAG, "DisconnectToDevice ");
            setProgressBarIndeterminateVisibility(true);
            SetLinkButtonEnable(ListViewAdapter.DISABLE);
            Map<String, Object> map = mList.get(PrinterId);
            map.put(ListViewAdapter.STATUS, getString(R.string.cutting));
            mList.set(PrinterId, map);
            mListViewAdapter.notifyDataSetChanged();
            try {
                mGpService.closePort(PrinterId);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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
        // TODO Auto-generated method stub
        Log.d(DEBUG_TAG, "requestCode" + requestCode + '\n' + "resultCode" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_PORT_SETTINGS) {
            // getIP settings info from IP settings dialog
            if (resultCode == RESULT_OK) {
                Bundle bundle = new Bundle();
                bundle = data.getExtras();
                Log.d(DEBUG_TAG, "PrinterId " + mPrinterId);
                int param = bundle.getInt(GpPrintService.PORT_TYPE);
                mPortParam[mPrinterId].setPortType(param);
                Log.d(DEBUG_TAG, "PortType " + param);
                String str = bundle.getString(GpPrintService.IP_ADDR);
                mPortParam[mPrinterId].setIpAddr(str);
                Log.d(DEBUG_TAG, "IP addr " + str);
                param = bundle.getInt(GpPrintService.PORT_NUMBER);
                mPortParam[mPrinterId].setPortNumber(param);
                Log.d(DEBUG_TAG, "PortNumber " + param);
                str = bundle.getString(GpPrintService.BLUETOOT_ADDR);
                mPortParam[mPrinterId].setBluetoothAddr(str);
                Log.d(DEBUG_TAG, "BluetoothAddr " + str);
                str = bundle.getString(GpPrintService.USB_DEVICE_NAME);
                mPortParam[mPrinterId].setUsbDeviceName(str);
                Log.d(DEBUG_TAG, "USBDeviceName " + str);
                SetPortParamToView(mPortParam[mPrinterId]);
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
        else if(requestCode== SCANNIN_GREQUEST_CODE1)
        {
            if(resultCode == RESULT_OK)
            {
                Bundle bundle = data.getExtras();
                String temp=bundle.getString("resulturl");
                Log.e("dddd","temp result="+temp);
                //显示扫描到的内容
                if(temp!=null)
                    qrcodeWxcontent.setText(temp);

            }
        }
        else if(requestCode== SCANNIN_GREQUEST_CODE2)
        {
            if(resultCode == RESULT_OK)
            {
                Bundle bundle = data.getExtras();
                String temp=bundle.getString("resulturl");
                Log.e("dddd","temp result="+temp);
                //显示扫描到的内容
                if(temp!=null)
                    qrcodeZfbcontent.setText(temp);

            }
        }
    }

    private void messageBox(String err) {
        Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
    }
}
