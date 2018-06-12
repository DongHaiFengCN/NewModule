
package doaing.order.view;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
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
import com.couchbase.lite.QueryChange;
import com.couchbase.lite.QueryChangeListener;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.google.zxing.integration.android.IntentIntegrator;
import com.gprinter.aidl.GpService;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;


import bean.kitchenmanage.dish.DishKind;
import bean.kitchenmanage.kitchen.KitchenClient;
import bean.kitchenmanage.order.CheckOrder;
import bean.kitchenmanage.order.Order;
import bean.kitchenmanage.table.Table;
import bean.kitchenmanage.user.Employee;
import doaing.mylibrary.MyApplication;
import doaing.order.R;
import doaing.order.device.DeviceMain;
import doaing.order.device.kitchen.KitchenCfgActivity;
import doaing.order.service.NewOrderService;
import doaing.order.untils.Tool;
import doaing.order.view.adapter.AreaAdapter;
import doaing.order.view.adapter.LiveTableRecyclerAdapter;
import tools.CDBHelper;
import tools.MyLog;

import static doaing.order.device.ListViewAdapter.DEBUG_TAG;

@Route(path = "/order/DeskActivity")
public class DeskActivity extends AppCompatActivity {

    private Database db;
    private ListView listViewArea;
    private AreaAdapter areaAdapter;
    private RecyclerView listViewDesk;
    private LiveTableRecyclerAdapter tableadapter;
    private List<Document> freeTableList = new ArrayList<>();
    private String[] tablesNos,tablesName;
    public int pos = 0,mPos = 0;
    private Toolbar toolbar;
    private TextView msg_point;
    private ImageView msg_printer;
    List<DishKind> dishesKindCList;
    List<Document> dishes;
    private Map<String, List<Document>> dishesObjectCollection;


    private String Tag = "DeskActivity";
    private MyApplication myapp;
    private long mExitTime = 0;
    private long boo;

    private Handler uiHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            MyLog.e(Tag,"msg what="+msg.what);

            switch (msg.what) {
                case 1: //语音播放
                    String id = (String)msg.obj;
                    showDeskListView(id);
                    break;
                case 2: //没有订单
                    Toast.makeText(DeskActivity.this,"没有订单！",Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_desk);
        toolbar = findViewById(R.id.mytoolbar);
        toolbar.setTitle("区域桌位");

        setSupportActionBar(toolbar);
        //关键下面两句话，设置了回退按钮，及点击事件的效果
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((System.currentTimeMillis() - boo) > 2000) {
                    Toast.makeText(getApplicationContext(), "再按一次退出系统",
                            Toast.LENGTH_SHORT).show();
                    boo = System.currentTimeMillis();
                } else {

                    finish();
                   // System.exit(0);
                }

            }
        });
        msg_point = findViewById(R.id.msg_point);
        msg_point.setVisibility(View.INVISIBLE);
        msg_printer = findViewById(R.id.icon_printer);
        msg_printer.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
               Intent intent = new Intent(DeskActivity.this,KitchenCfgActivity.class);
                startActivity(intent);
            }
         });

        myapp= (MyApplication) getApplicationContext();
        String mobile = getIntent().getStringExtra("mobile");
        String channelId = getIntent().getStringExtra("channelId");
        Log.e("DeskActivity","mobile = "+mobile);
        Employee obj = new Employee();
        obj.setChannelId(channelId);
        obj.setName("管理员");
        myapp.setEmployee(obj);
         // myapp.initDishesData();
        initWidget();

        initDishesData();


    }



    @Override
    protected void onResume() {
        super.onResume();

        List<String> kitchenClientIds = CDBHelper.getIdsByClass( KitchenClient.class);
        if(kitchenClientIds.size()>0)
            msg_printer.setVisibility(View.VISIBLE);
        else
            msg_printer.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initDishesData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyLog.e(Tag,"  onPause");

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isFinishing())
        {
            MyLog.e(Tag," on Stop isFinishing");
            //releaseResource();
        }
        MyLog.e(Tag," on Stop");


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLog.e(Tag,"onDestroy");


    }
    private void releaseResource()
    {
        EventBus.getDefault().unregister(this);


    }
    //点击返回键
    @Override
    public void onBackPressed() {
        if((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        }
        else {
            finish();
            //System.exit(0);
        }

    }



    private void initWidget()
    {
        CDBHelper.getSharedInstance(getApplicationContext());
        db = CDBHelper.getDatabase();
        if(db == null) throw new IllegalArgumentException();
        areaAdapter = new AreaAdapter(this, db);
        listViewArea = findViewById(R.id.lv_area);
        listViewArea.setAdapter(areaAdapter);
        listViewArea.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {

                areaAdapter.setSelectItem(i);
                final String id = areaAdapter.getItem(i);
                Message msg = Message.obtain();
                msg.obj = id;
                msg.what = 1;
                uiHandler.sendMessage(msg);
            }
        });
        areaAdapter.setAreaLocation(new AreaAdapter.AreaLocation() {
            @Override
            public void setLocation(boolean location) {
                if (location){
                    if(areaAdapter.getCount()>0)
                    {
                        areaAdapter.setSelectItem(0);
                        showDeskListView(areaAdapter.getItem(0));
                    }
                }
            }
        });


    }

    public void showDeskListView(String areaId)
    {
        Log.e("Desk",""+areaId);
        if(tableadapter!=null)
            tableadapter.StopQuery();

        tableadapter=new LiveTableRecyclerAdapter(this,db,areaId);
        tableadapter.setOnItemClickListener(new LiveTableRecyclerAdapter.onRecyclerViewItemClickListener()
        {
            @Override
            public void onItemClick(View view,Object data)
            {


                String tableId= (String)data;
                MyLog.e("DeskTableID",""+tableId);
                final Table table =  CDBHelper.getObjById(tableId,Table.class);
                myapp.setTable_sel_obj(table);
                MyLog.e("DeskTableID",""+table.getId());
                if(table.getState()!=2)
                {
                    final EditText  editText = new EditText(DeskActivity.this);
                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
                    LinearLayout linearLayout =new LinearLayout(DeskActivity.this);

                    //设置控件居中显示
                    linearLayout.setGravity(Gravity.CENTER);

                    //设置子控件在线性布局下的参数设置对象（什么布局就用什么的）

                    LinearLayout.LayoutParams params =new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    //设置margins属性

                    params.setMargins(10,10,10,10);

                    //设置控件参数
                    editText.setLayoutParams(params);

                    //设置输入类型为数字
                    editText.setInputType((InputType.TYPE_CLASS_NUMBER));

                    //添加控件到布局
                    linearLayout.addView(editText);

                    editText.setHint("最多人数："+table.getMaxPersons()+"最小人数 : "+table.getMinConsum());

                    AlertDialog.Builder builder = new AlertDialog.Builder(DeskActivity.this);

                    builder.setTitle("设置就餐人数");
                    builder.setView(linearLayout);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(TextUtils.isEmpty(editText.getText().toString())){

                                editText.setError("人数不能为空");

                            }else if("0".equals(editText.getText().toString())){

                                editText.setError("人数不能为0");

                            }else {

                                //设置就餐人数，转跳

                                table.setState(2);
                                table.setCurrentPersons(Integer.valueOf(editText.getText().toString()));
                                //设置全局Table
                                myapp.setTable_sel_obj(table);
                                CDBHelper.createAndUpdate(table);
                                dialog.dismiss();
                                //转跳点餐界面
                                turnMainActivity();


                            }
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    final AlertDialog alertDialog = builder.create();

                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                        }
                    });

                    alertDialog.show();

                }else {
                    List<String> orderCList= CDBHelper.getIdsByWhere(
                            Expression.property("className").equalTo(Expression.string("Order"))
                                    .and(Expression.property("tableId").equalTo(Expression.string(table.getId())))
                                    .and(Expression.property("state").equalTo(Expression.intValue(1)))
                            ,null
                    );
                    Log.e("Desk","TableNum=="+table.getNum());
                    Log.e("Desk","orderCList=="+orderCList.size());

                    if (orderCList.size() > 0 )
                    {
                        //使用状态下跳到查看订单界面
                        Intent mainIntent = new Intent();
                        mainIntent.setClass(DeskActivity.this, ShowParticularsActivity.class);
                        startActivity(mainIntent);
                    }else {
                        //转跳点餐界面
                        turnMainActivity();

                    }

                }

            }
            @Override
            public void onItemLongClick(View view, final Object data)
            {
                final String tableId = (String)data;
                final Table table = CDBHelper.getObjById(tableId,Table.class);
                myapp.setTable_sel_obj(table);

                final AlertDialog.Builder alertLog = new AlertDialog.Builder(DeskActivity.this);

                //空闲状态下重置上一次未买单状态
                if(table.getState()==0){
                    alertLog.setTitle("是否重置？");
                    String[] an = new  String[1];
                    an[0] = "重置最近一次账单";
                    alertLog.setSingleChoiceItems(an, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            final AlertDialog alertDialog = alertLog.create();
                            final ProgressDialog proDialog = ProgressDialog.show(DeskActivity.this, "重置", "正在配置订单请稍等~");

                            myapp.mExecutor.execute(new Runnable() {
                                @Override
                                public void run() {

                                    try {
                                        CDBHelper.getDatabase().inBatch(new TimerTask() {
                                            @Override
                                            public void run() {
                                                CheckOrder checkOrder = null;
                                                //老数据没有字段遍历查询
                                                if(table.getLastCheckOrderId() == null || table.getLastCheckOrderId().isEmpty()){

                                                    Date date = new Date();
                                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                                    //查询当日的订单
                                                    List<CheckOrder> checkOrderCS = CDBHelper.getObjByWhere(
                                                             Expression.property("className").equalTo(Expression.string("CheckOrder"))
                                                                    .and(Expression.property("checkTime").like(Expression.string(formatter.format(date)+"%")))
                                                            , null, CheckOrder.class);

                                                    Iterator<CheckOrder> iterator = checkOrderCS.iterator();

                                                    //移除不是当前桌的订单
                                                    while (iterator.hasNext()){
                                                        CheckOrder c = iterator.next();
                                                        if(!c.getTableId().equals(table.getId())){
                                                            iterator.remove();
                                                        }
                                                    }
                                                    if(checkOrderCS.size() > 0){
                                                        List<String> dateList = new ArrayList<>();
                                                        //获取当前桌订单今日时间集合
                                                        for (int i1 = 0; i1 < checkOrderCS.size(); i1++) {
                                                            dateList.add(checkOrderCS.get(i1).getCheckTime());
                                                        }

                                                        //得到最近订单的坐标
                                                        int f =  Tool.getLastCheckOrder(dateList);
                                                        checkOrder = checkOrderCS.get(f);
                                                        for (int i = 0; i < checkOrder.getOrderId().size(); i++) {

                                                            Order order = CDBHelper.getObjById(checkOrder.getOrderId().get(i),Order.class);
                                                            order.setState(1);
                                                            CDBHelper.createAndUpdate( order);
                                                        }

                                                        //删除之前的checkorder记录
                                                        CDBHelper.deleDocumentById(checkOrder.getId());

                                                        table.setState(2);
                                                        CDBHelper.createAndUpdate(table);

                                                    }else {

                                                        Message msg = Message.obtain();
                                                        msg.what = 2;
                                                        uiHandler.sendMessage(msg);
                                                    }

                                                }else {

                                                    //新数据查询

                                                    checkOrder = CDBHelper.getObjById(table.getLastCheckOrderId(),CheckOrder.class);
                                                    if (checkOrder == null&&checkOrder.getOrderId().size()==0){
                                                        return;
                                                    }

                                                    for (int i = 0; i < checkOrder.getOrderId().size(); i++) {

                                                        Order order = CDBHelper.getObjById(checkOrder.getOrderId().get(i),Order.class);
                                                        order.setState(1);
                                                        CDBHelper.createAndUpdate( order);

                                                    }

                                                    //删除之前的checkorder记录
                                                    CDBHelper.deleDocumentById(checkOrder.getId());
                                                    table.setState(2);
                                                    CDBHelper.createAndUpdate( table);

                                                }

                                            }
                                        });
                                    } catch (CouchbaseLiteException e) {
                                        e.printStackTrace();
                                    }
                                    proDialog.dismiss();//关闭proDialog

                                }
                            });

                            //获取今天日期
                            alertDialog.dismiss();

                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();


                }
                else if(table.getState()==2)//开台中
                {
                    //使用&&预定状态
                 final   List<String> orderCList = CDBHelper.getIdsByWhere(
                         Expression.property("className").equalTo(Expression.string("Order"))
                                 .and(Expression.property("tableId").equalTo(Expression.string(table.getId())))
                                 .and(Expression.property("state").equalTo(Expression.intValue(1)))
                         ,null);

                    if(orderCList.size()>0)//有未买单订单，可以买单
                    {
                        final String[] an = new  String[3];
                        an[0] = "是否消台";
                        an[1] = "是否换台";
                        an[2] = "是否合台";
                        alertLog.setTitle("请选择操作");
                        alertLog.setSingleChoiceItems(an, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPos = which;
                            }
                        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (an[mPos].equals("是否消台"))
                                {

                                     cancelTableOrder(tableId,orderCList);
                                    //3、取消对话框
                                    dialog.dismiss();
                                }
                                else if(an[mPos].equals("是否换台")){
                                    tablesName = findFreeTable();
                                    String[] areaTable = new String[tablesName.length];
                                    for (int i = 0; i< freeTableList.size();i++){
                                        Document selectTable = freeTableList.get(i);
                                        Document document = CDBHelper.getDocByID(selectTable.getString("areaId"));
                                        String areaName = document.getString("name");
                                        areaTable[i] = areaName+" : "+tablesName[i];
                                    }
                                    AlertDialog.Builder builder = new AlertDialog.Builder(DeskActivity.this);
                                    builder.setTitle("请点击您要换的桌位号")
                                            .setSingleChoiceItems(areaTable, 0, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    pos = which;
//                                                    Document selectTable = freeTableList.get(pos);
//                                                    String tableName = selectTable.getString("tableName");
//                                                    Document document = CDBHelper.getDocByID(getApplication(),selectTable.getString("areaId"));
//                                                    String areaName = document.getString("areaName");
//                                                    Toast.makeText(DeskActivity.this,areaName+":   "+tableName,Toast.LENGTH_SHORT).show();
                                                }
                                            }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String tableNum = tablesNos[pos];
                                            //1\改变所选桌位对象状态为使用
                                            MutableDocument selectTable = freeTableList.get(pos).toMutable();
                                            selectTable.setInt("state",2);
                                            CDBHelper.saveDocument(selectTable);
                                            //2\改变原桌位对象状态为空闲
                                            changeOldTable(tableId);
                                            //3\改变原桌位下订单为该桌位下
                                            List<Document> documentList = CDBHelper.getDocmentsByWhere(
                                                    Expression.property("className").equalTo(Expression.string("Order"))
                                                            .and(Expression.property("state").equalTo(Expression.intValue(1))
                                                            .and(Expression.property("tableId").equalTo(Expression.string(table.getId())))),
                                                    null);
                                            for (Document doc : documentList)
                                            {
                                                MutableDocument mDoc = doc.toMutable();
                                                mDoc.setString("tableNum",tableNum);
                                                CDBHelper.saveDocument(mDoc);
                                            }
                                            dialog.dismiss();
                                            pos = 0;
                                        }
                                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();

                                    dialog.dismiss();
                                }
                                else if(an[mPos].equals("是否合台"))
                                {

                                }
                                mPos = 0;

                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                    }
                    else
                    {
                        String[] an = new  String[1];
                        an[0] = "是否消台";
                        alertLog.setTitle("是否消台？");
                        alertLog.setSingleChoiceItems(an, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPos = which;
                            }
                        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                table.setState(0);
                                CDBHelper.createAndUpdate(table);
                                myapp.setTable_sel_obj(table);
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                    }
                }
            }
        });

        listViewDesk = (RecyclerView)findViewById(R.id.lv_desk);
        listViewDesk.setItemAnimator(new DefaultItemAnimator());
        listViewDesk.setLayoutManager(new GridLayoutManager(this,2));
        listViewDesk.setAdapter(tableadapter);

    }

    private void changeOldTable(String tableId)
    {
        Document doc = CDBHelper.getDocByID(tableId);
        MutableDocument mDoc = doc.toMutable();
        mDoc.setInt("state",0);
        CDBHelper.saveDocument(mDoc);

    }
private void cancelTableOrder(String Id,List<String> orderList)
{
    final String tableId = Id;
    final List<String> orderCList = orderList;

    final EditText  reason = new EditText(this);
    final AlertDialog.Builder alertDlg = new AlertDialog.Builder(DeskActivity.this);
    alertDlg.setView(reason);
    alertDlg.setTitle("请输入消台原因");
    alertDlg.setPositiveButton("确定",new DialogInterface.OnClickListener(){
        @Override
        public void onClick(DialogInterface dialog1, int which)
        {
            final String input = reason.getText().toString();
            //1|
            try {
                CDBHelper.getDatabase().inBatch(new Runnable() {
                    @Override
                    public void run()
                    {
                        for(String  docId:orderCList)
                        {
                            Document doc = CDBHelper.getDocByID(docId);
                            if(doc!=null)
                            {
                                MutableDocument mDoc= doc.toMutable();
                                if(!TextUtils.isEmpty(input))
                                    mDoc.setString("cancelReason",input);

                                mDoc.setInt("orderState",2);
                                CDBHelper.saveDocument(mDoc);
                            }
                        }

                        Document tabDoc = CDBHelper.getDocByID(tableId);
                        MutableDocument tabmDoc = tabDoc.toMutable();
                        tabmDoc.setInt("state",0);
                        CDBHelper.saveDocument(tabmDoc);
                    }
                });
            } catch (CouchbaseLiteException e)
            {
                Log.e(Tag, e.toString());
            }
            dialog1.dismiss();
        }})
            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog1, int which)
                {
                    dialog1.dismiss();
                }
            }).show();


}
    private   String [] findFreeTable()
    {
        String[] freetables=null;
        freeTableList.clear();
        List<Document> tableDocList= CDBHelper.getDocmentsByWhere(
                Expression.property("className").equalTo(Expression.string("Table"))
                        .and(Expression.property("state").equalTo(Expression.intValue(0)))
                , Ordering.property("num").ascending()
        );
        Log.e("Desk","table---"+tableDocList.size());
        if(tableDocList.size()>0)
        {
            tablesNos=new String[tableDocList.size()];
            freetables = new String[tableDocList.size()];
            int i=0;
            for(Document doc:tableDocList)
            {

                freeTableList.add(doc);
                freetables[i] = doc.getString("name");
                tablesNos[i]=doc.getString("num");
                i++;

            }
        }

        return freetables;

    }

    private void turnMainActivity() {
        Intent mainIntent = new Intent();
        mainIntent.setClass(DeskActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_desk, menu);
        return true;
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {

        if (menu != null) {
            if (menu.getClass() == MenuBuilder.class) {

                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {

                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            this.finish(); // back button

        }  else if (i == R.id.action_dishes) {

            ARouter.getInstance().build("/dishesmanager/DishesManagerMainActivity").navigation();

        }else if (i == R.id.action_device) {

            Intent intent = new Intent(DeskActivity.this, DeviceMain.class);
            startActivity(intent);

        }else if (i == R.id.action_table){
            ARouter.getInstance().build("/tablemanager/TableManagerActivity").navigation();
        }else if (i == R.id.action_tj){
            ARouter.getInstance().build("/statistics/StatisticsActivity").navigation();
        }else if (i == R.id.action_gq){
            startActivity(new Intent(this,AssessmentActivity.class));


        }
        return true;
    }

    private void turnScan() {

        IntentIntegrator intentIntegrator =  new IntentIntegrator(this);

        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setPrompt("请扫描二维码");
        intentIntegrator.setCaptureActivity(ScanActivity.class); // 设置自定义的activity是ScanActivity
        intentIntegrator.initiateScan(); // 初始化扫描
    }

    public void initDishesData() {

        myapp.mExecutor.execute(new Runnable() {
            @Override
            public void run() {

                dishesObjectCollection = new HashMap<>();

                dishesKindCList = CDBHelper.getObjByWhere(
                        Expression.property("className").equalTo(Expression.string("DishKind"))
                        ,null, DishKind.class);
                Log.e("DeskA",""+dishesKindCList.size());

                //1、初始化菜品数量维护映射表
                for (DishKind dishesKind : dishesKindCList) {

                    dishes = CDBHelper.getDocmentsByWhere(
                            Expression.property("className").equalTo(Expression.string("Dish"))
                                    .and(Expression.property("kindId").equalTo(Expression.string(dishesKind.getId())))
                            ,null);

                    dishesObjectCollection.put(dishesKind.getId(),dishes);

                }
                myapp.setDishesKindCList(dishesKindCList);
                myapp.setDishesObjectCollection(dishesObjectCollection);

            }
        });

    }




}
