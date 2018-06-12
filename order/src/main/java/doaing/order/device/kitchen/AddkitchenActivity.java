package doaing.order.device.kitchen;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.couchbase.lite.Array;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Ordering;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.gprinter.aidl.GpService;
import com.gprinter.command.GpCom;
import com.gprinter.io.GpDevice;
import com.gprinter.io.PortParameters;
import com.gprinter.save.PortParamDataBase;
import com.gprinter.service.GpPrintService;

import java.util.ArrayList;
import java.util.List;

import bean.kitchenmanage.dish.DishKind;
import bean.kitchenmanage.kitchen.KitchenClient;
import doaing.mylibrary.MyApplication;
import doaing.order.R;
import doaing.order.device.PortConfigurationActivity;
import doaing.order.untils.GlobalConstant;
import doaing.order.view.DeskActivity;
import tools.CDBHelper;
import tools.MyLog;
import view.BaseToobarActivity;
import doaing.order.device.kitchen.AddKitchenAdapter.ViewHolder;

import static doaing.order.device.ListViewAdapter.MESSAGE_CONNECT;
/*
*
 * Created by lenovo on 2018/2/1.

*/


public class AddkitchenActivity extends BaseToobarActivity implements View.OnClickListener{
    Toolbar toolbar;
    //传递过来的值
    private String  selectDocId ;
    private KitchenClient gObj;
    private int mPrinterId = 0;
    //厨房名称
    EditText etClientname;
    //打印机按钮
    Button btnConnectPrinter;
    private PortParameters mPortParam;
    private GpService mGpService = null;
    private static final int MAIN_QUERY_PRINTER_STATUS = 0xfe;
    //支持的菜类
    ListView listViewDishKind;
    private AddKitchenAdapter addKitchenAdapter;
    private List<String> allDishKindIdList = new ArrayList<>();
    private List<String> listSelectedDocId;
    private static final int INTENT_PORT_SETTINGS = 0;

    private MyApplication myapp;
    private PrinterServiceConnection conn;


    @Override
    protected int setMyContentView() {
        return R.layout.activity_addkitchen;
    }

    @Override
    public void initData(Intent intent) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        myapp = (MyApplication)getApplicationContext();

        registerBroadcast();
        this.sendBroadcast(new Intent(GlobalConstant.printer_msg_pause));
        bindPrinterService();

        mPortParam = new PortParameters();
        listSelectedDocId = new ArrayList<>();

        initView();

        Intent intent1=getIntent();
        selectDocId=intent1.getStringExtra("docId");
        if(selectDocId!=null){
            gObj = CDBHelper.getObjById(selectDocId,KitchenClient.class);
            editKitchenData();
        }
        else{

              gObj =new KitchenClient();
              gObj.setChannelId(myapp.getCompany_ID());
              addKitchenData();
        }
    }

    private void bindPrinterService() {
        conn = new PrinterServiceConnection();
        Intent intent = new Intent("com.gprinter.aidl.GpPrintService");
        intent.setPackage(getPackageName());
        boolean ret = bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    //打印机初始化
    class PrinterServiceConnection implements ServiceConnection {
        @Override
        public void onServiceDisconnected(ComponentName name) {

            MyLog.e("DEBUG_TAG", "onServiceDisconnected() called");
            mGpService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mGpService = GpService.Stub.asInterface(service);
        }
    }
    private void addKitchenData()
    {
        List<Document> documentList = CDBHelper.getDocmentsByWhere(Expression.property("className").equalTo(Expression.string("KitchenClient"))
        , Ordering.property("printerId").descending());
        if (documentList.size() == 0){
            mPrinterId = 0;
        }else {
            int pos = documentList.get(0).getInt("printerId");
            mPrinterId = pos+1;//下一个打印机id
        }
    }
    private void editKitchenData()
    {
        etClientname.setText(gObj.getName());
        mPrinterId = gObj.getPrinterId();
        PortParamDataBase database = new PortParamDataBase(this);
        mPortParam = database.queryPortParamDataBase("" +mPrinterId);

        try {
            if (mGpService.getPrinterConnectStatus(0) == GpDevice.STATE_CONNECTED)
            {
                btnConnectPrinter.setText("连接成功");
                mPortParam.setPortOpenState(true);
            }else {
                btnConnectPrinter.setText("点击配置");
                mPortParam.setPortOpenState(false);
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Initcpplfromdata(selectDocId);
    }
    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        etClientname = findViewById(R.id.et_clientname);
        btnConnectPrinter = findViewById(R.id.spPrinter);
        listViewDishKind = findViewById(R.id.listView_ck);
        setToolbarName("添加厨房打印机");
        findViewById(R.id.selectall).setOnClickListener(this);
        findViewById(R.id.bt_deselectall).setOnClickListener(this);
        findViewById(R.id.btn_kcsave).setOnClickListener(this);
        btnConnectPrinter.setOnClickListener(this);
        Database db = CDBHelper.getDatabase();
        Query query = QueryBuilder.select(SelectResult.expression(Meta.id)).from(DataSource.database(db))
                .where(Expression.property("className").equalTo(Expression.string("DishKind")));

        try {
            ResultSet resultSet = query.execute();
            Result row;
            while ((row = resultSet.next()) != null)
            {

                String id = row.getString(0);
                //documentList.add(id);
                allDishKindIdList.add(id);

            }
        } catch (CouchbaseLiteException e) {
            Log.e("getDocmentsByClass", "Exception=", e);
        }
        addKitchenAdapter = new AddKitchenAdapter(AddkitchenActivity.this, allDishKindIdList);
        listViewDishKind.setAdapter(addKitchenAdapter);
        listViewDishKind.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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


    }

    @Override
    protected Toolbar setToolBarInfo() {
        return toolbar;
    }

@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;

    }
    @Override
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

        }  else if (i1 == R.id.btn_kcsave)
        {

            addOrUpdateObject();



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

    private void addOrUpdateObject()
    {

        if (CheckPortParamters(mPortParam))
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



        gObj.setName(etClientname.getText().toString().trim());

        gObj.setPrinterId(mPrinterId);

        //保存一下打印机状态，实时反馈到状态界面上
        boolean state = mPortParam.getPortOpenState();
        gObj.setStatePrinter(state);

        List<String> dishKindIDList=new ArrayList<String>();
        dishKindIDList.addAll(listSelectedDocId);

        gObj.setKindIds(dishKindIDList);
        CDBHelper.createAndUpdate(gObj);

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

//    //编辑模式进来保存
//    private void UpDataIP( String docId)
//    {
//
//            getcheckinfo();
//            if(listSelectedDocId.size()<=0)
//            {
//                Toast.makeText(this, "请选择要接收的菜品类",Toast.LENGTH_LONG).show();
//            }
//            else
//            {
//                Updatecheckinfo(docId);
//                Intent intent = new Intent();
//                this.setResult(2015, intent);
//                this.finish();
//            }
//
//
//    }

//    //保存数据到数据库
//    private void Updatecheckinfo(String docId) {
//
//        MutableArray dishKindIdArray = new MutableArray();
//        for(int i=0;i<listSelectedDocId.size();i++)
//        {
//            dishKindIdArray.addString(listSelectedDocId.get(i));
//        }
//        Document docKitchenClient=CDBHelper.getDocByID(docId);
//        if(docKitchenClient!=null)
//        {
//            MutableDocument muDoc = docKitchenClient.toMutable();
//            muDoc.setString("name",etClientname.getText().toString().trim());
//
//
//            //保存一下打印机状态，实时反馈到状态界面上
//            boolean state = mPortParam.getPortOpenState();
//
//            muDoc.setInt("printerId",mPrinterId);
//            //muDoc.setBoolean("statePrinter",state);
//            muDoc.setValue("kindIds",dishKindIdArray);
//            CDBHelper.saveDocument(muDoc);
//        }
//
//    }




    private void Initcpplfromdata(String docId)
    {
        Document docKC = CDBHelper.getDocByID(docId);

        if(docKC!=null)
        {


            Array dishKindIDList=docKC.getArray("kindIds");
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



    public Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case MESSAGE_CONNECT:
                    connectOrDisConnectToDevice();
            }
            return false;
        }
    });

    void connectOrDisConnectToDevice() {
      
        int rel = 0;
        if (mPortParam.getPortOpenState() == false)
        {
            if (CheckPortParamters(mPortParam))
            {
                try {
                    mGpService.closePort(mPrinterId);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                switch (mPortParam.getPortType()) {
                    case PortParameters.USB:
                        try {

                            rel = mGpService.openPort(mPrinterId, mPortParam.getPortType(),
                                    mPortParam.getUsbDeviceName(), 0);
                        } catch (RemoteException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        break;
                    case PortParameters.ETHERNET:
                        try {
                            rel = mGpService.openPort(mPrinterId, mPortParam.getPortType(),
                                    mPortParam.getIpAddr(), mPortParam.getPortNumber());
                        } catch (RemoteException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        break;
                    case PortParameters.BLUETOOTH:
                        try {
                            rel = mGpService.openPort(mPrinterId, mPortParam.getPortType(),
                                    mPortParam.getBluetoothAddr(), 0);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
                if (r != GpCom.ERROR_CODE.SUCCESS) {
                    if (r == GpCom.ERROR_CODE.DEVICE_ALREADY_OPEN) {
                        mPortParam.setPortOpenState(true);
                    } else {
                        messageBox(GpCom.getErrorText(r));
                    }
                }
            } else {
                messageBox(getString(R.string.port_parameters_wrong));
            }
        }
//        else {
//            setProgressBarIndeterminateVisibility(true);
//            try {
//                mGpService.closePort(mPrinterId);
//            } catch (RemoteException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.sendBroadcast(new Intent(GlobalConstant.printer_msg_pause));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing()){
            this.unregisterReceiver(PrinterStatusBroadcastReceiver);
            this.sendBroadcast(new Intent(GlobalConstant.printer_msg_resum));
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }


    //检查是哪个打印机
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

                int param = bundle.getInt(GpPrintService.PORT_TYPE);
                mPortParam.setPortType(param);

                String str = bundle.getString(GpPrintService.IP_ADDR);
                mPortParam.setIpAddr(str);
                param = bundle.getInt(GpPrintService.PORT_NUMBER);
                mPortParam.setPortNumber(param);


                str = bundle.getString(GpPrintService.BLUETOOT_ADDR);
                mPortParam.setBluetoothAddr(str);

                str = bundle.getString(GpPrintService.USB_DEVICE_NAME);
                mPortParam.setUsbDeviceName(str);

                if (CheckPortParamters(mPortParam))
                {
                    PortParamDataBase database = new PortParamDataBase(this);
                    database.deleteDataBase("" + mPrinterId);
                    database.insertPortParam(mPrinterId, mPortParam);
                    Message message = new Message();
                    message.what = MESSAGE_CONNECT;
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
            if (GpCom.ACTION_CONNECT_STATUS.equals(intent.getAction()))
            {
                int type = intent.getIntExtra(GpPrintService.CONNECT_STATUS, 0);

                MyLog.e("ACTION_CONNECT_STATUS","type="+type);
                int id = intent.getIntExtra(GpPrintService.PRINTER_ID, 0);
                if(id!=mPrinterId)
                    return;
                if (type == GpDevice.STATE_CONNECTING)  //2 正在连接
                {
                    setProgressBarIndeterminateVisibility(true);
                    mPortParam.setPortOpenState(false);
                    btnConnectPrinter.setText("正在连接");

                } else if (type == GpDevice.STATE_NONE)//0 没有连接
                {
                    List<Document>    docList  = CDBHelper.getDocmentsByWhere(
                            Expression.property("className").equalTo(Expression.string("KitchenClient"))
                                    .and(Expression.property("printerId").equalTo(Expression.intValue(id))),
                            null);

                    if(docList.size()>0)
                    {
                        Document doc  = docList.get(0);
                        MutableDocument mutableDocument = doc.toMutable();
                        mutableDocument.setBoolean("statePrinter",false);
                        CDBHelper.saveDocument(mutableDocument);
                    }

                    setProgressBarIndeterminateVisibility(false);
                    mPortParam.setPortOpenState(false);
                     btnConnectPrinter.setText("未连接");

                } else if (type == GpDevice.STATE_VALID_PRINTER)//5 连接成功
                {

                    List<Document>    docList  = CDBHelper.getDocmentsByWhere(
                            Expression.property("className").equalTo(Expression.string("KitchenClient"))
                                    .and(Expression.property("printerId").equalTo(Expression.intValue(id))),
                            null);

                    if(docList.size()>0)
                    {
                        Document doc  = docList.get(0);
                        MutableDocument mutableDocument = doc.toMutable();
                        mutableDocument.setBoolean("statePrinter",true);
                        CDBHelper.saveDocument(mutableDocument);
                    }

                    setProgressBarIndeterminateVisibility(false);
                    mPortParam.setPortOpenState(true);
                    btnConnectPrinter.setText("连接成功");

                } else if (type == GpDevice.STATE_INVALID_PRINTER)//4 连接中断
                {
                    setProgressBarIndeterminateVisibility(false);
                    mPortParam.setPortOpenState(false);
                    messageBox("Please use Gprinter!");
                    btnConnectPrinter.setText("连接中断");
                }else if ((intent.getAction()).equals(GpCom.ACTION_DEVICE_REAL_STATUS)) {

                    // 业务逻辑的请求码，对应哪里查询做什么操作

                    int requestCode = intent.getIntExtra(GpCom.EXTRA_PRINTER_REQUEST_CODE, -1);

                    // 判断请求码，是则进行业务操作

                    if (requestCode == MAIN_QUERY_PRINTER_STATUS) {


                        int status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16);

                        String str;

                        if (status == GpCom.STATE_NO_ERR) {

                            str = "打印机正常";

                            //printerSat = true;

                        } else {

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
                            btnConnectPrinter.setText(str);
                        }
                    }

                }
            }

        }
    };

}
