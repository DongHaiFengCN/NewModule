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

import com.couchbase.lite.Array;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDictionary;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Ordering;
import com.couchbase.litecore.fleece.MArray;
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

import bean.kitchenmanage.kitchen.KitchenClient;
import bean.kitchenmanage.order.Goods;
import bean.kitchenmanage.order.Order;
import bean.kitchenmanage.table.Area;
import bean.kitchenmanage.user.Company;
import doaing.mylibrary.MyApplication;
import doaing.order.R;
import doaing.order.untils.BluetoothUtil;
import doaing.order.untils.MyBigDecimal;
import doaing.order.untils.PrintUtils;
import doaing.order.untils.Tool;
import doaing.order.view.adapter.ShowParticularsAdapter;
import tools.CDBHelper;
import tools.MyLog;
import tools.ToolUtil;

import static com.gprinter.service.GpPrintService.ACTION_CONNECT_STATUS;
import static tools.Method.getFormatDate;
import static tools.Method.getNianDate;

/*
*
 * Created by lenovo on 2017/12/13.
*/


public class ShowParticularsActivity extends Activity implements View.OnClickListener{

    ListView showListView;
    LinearLayout showButDc;
    TextView showTvSl;
    TextView showTvArea;
    ImageView showImg;
    ShowParticularsAdapter adatper;
    private List<MutableDocument> goodsCList;
    private MyApplication myapp;
    private float all = 0f;

    private BluetoothAdapter btAdapter;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private List<Document> orderCList;
    private String areaName, tableName;
    private int selActionId;
    private EditText editText;

    //追加菜品
    private void addDishes(int pos, float counts) {
        Document oldGoods = goodsCList.get(pos);
        MutableDocument newGoods = new MutableDocument("MsgGoods."+ ToolUtil.getUUID());
        createGoodsMsg(newGoods,oldGoods,counts,0);
    }

    private void createGoodsMsg(MutableDocument goodsMsgDoc,Document goodsDoc,float counts,int type){
        goodsMsgDoc.setString("className","MsgGoods");
        goodsMsgDoc.setString("id",goodsMsgDoc.getId());
        goodsMsgDoc.setString("dishId",goodsDoc.getString("dishId"));
        goodsMsgDoc.setString("goodsTableMsgId",myapp.getTable_sel_obj().getId());
        goodsMsgDoc.setString("tasteId",goodsDoc.getString("tasteId"));
        goodsMsgDoc.setFloat("count",counts);
        goodsMsgDoc.setInt("status",goodsDoc.getInt("status"));//刚点餐 未生成订单
        goodsMsgDoc.setString("channelId",myapp.getCompany_ID());
        goodsMsgDoc.setInt("submitFlag",0);//打印提交
        goodsMsgDoc.setInt("waitCall",0);//叫启
        goodsMsgDoc.setInt("goodsType",type);
        goodsMsgDoc.setInt("goodsAlter",1);
        CDBHelper.saveDocument(goodsMsgDoc);
        MutableDocument newOrderDoc = new MutableDocument("Order."+ToolUtil.getUUID());
        newOrderDoc.setString("className","Order");
        newOrderDoc.setString("channelId",myapp.getCompany_ID());
        List<Document> orderCList = CDBHelper.getDocmentsByWhere(
                Expression.property("className").equalTo(Expression.string("Order"))
                        .and(Expression.property("state").equalTo(Expression.intValue(1)))
                        .and(Expression.property("tableMsgId").equalTo(Expression.string(myapp.getTable_sel_obj().getId())))
                ,null
        );
        if (orderCList.size() > 0) {
            newOrderDoc.setInt("orderNum",orderCList.get(0).getInt("orderNum") + 1);
            newOrderDoc.setString("serialNum",orderCList.get(0).getString("serialNum"));
        } else {
            newOrderDoc.setInt("orderNum",1);
            newOrderDoc.setString("serialNum",Tool.getOrderSerialNum(this));
        }
        MutableArray array = new MutableArray();
        array.addValue(goodsMsgDoc.toMap());
        newOrderDoc.setArray("goodsList",array);
        newOrderDoc.setInt("state",1);//未买单
        newOrderDoc.setInt("printFlag",0);
        newOrderDoc.setInt("orderType",type);
        newOrderDoc.setInt("deviceType",0);//前台
        newOrderDoc.setString("tableMsgId",myapp.getTable_sel_obj().getId());
        newOrderDoc.setString("operator",myapp.getEmployee().getId());
        Document doc = CDBHelper.getDocByID(goodsDoc.getString("dishId"));
        newOrderDoc.setFloat("totalPrice",MyBigDecimal.mul(doc.getFloat("price"),counts,1));
        CDBHelper.saveDocument(newOrderDoc);
        MyLog.e("----"+newOrderDoc.getId()+"----"+goodsMsgDoc.getId());
        setAll();
    }

    private void retreatDishes(int pos, float counts) {
        MutableDocument oldGoods = goodsCList.get(pos);
        if (oldGoods.getInt("goodsType") != 0){
            return;
        }
        MutableDocument goodsMsgDoc = new MutableDocument("MsgGoods."+ToolUtil.getUUID());;
        Document document = CDBHelper.getDocByID(oldGoods.getId());
        float goodsCount = document.getFloat("count");//现有的数量
        if (goodsCount >= counts){
            float count = MyBigDecimal.sub(goodsCount,counts,1);
            if (count == 0.0){
                createGoodsMsg(goodsMsgDoc, oldGoods, counts, 1);
                setOrder(oldGoods,1,true);
                MyLog.e("oldGoods----"+oldGoods.getId());
                CDBHelper.deleDocument(oldGoods);
            }else {
                createGoodsMsg(goodsMsgDoc, oldGoods, counts, 1);
                oldGoods.setFloat("count", count);
                CDBHelper.saveDocument(oldGoods);
                setOrder(oldGoods,1,false);
            }

            adatper.notifyDataSetChanged();
        }

    }

    private void  setOrder(Document msgGoodsDoc,int type,boolean isDelete){
        orderCList = CDBHelper.getDocmentsByWhere(
                Expression.property("className").equalTo(Expression.string("Order"))
                        .and(Expression.property("tableMsgId").equalTo(Expression.string(myapp.getTable_sel_obj().getId())))
                        .and(Expression.property("state").equalTo(Expression.intValue(1)))
                , null);

        for (Document doc : orderCList){
            float totalPrice = 0;
            MutableArray mutArray = new MutableArray();
            MutableArray array = doc.getArray("goodsList").toMutable();
            for (int i = 0; i< array.count(); i++){
                MutableDictionary dictionary = array.getDictionary(i).toMutable();
                if (msgGoodsDoc.getId().equals(dictionary.getString("id"))){
                    if (isDelete){
                        isDelete = false;
                        array.remove(i);
                        i--;
                        continue;
                    }else {
                        dictionary.setInt("goodsType", type);
                        dictionary.setFloat("count", msgGoodsDoc.getFloat("count"));
                    }

                }
                mutArray.addDictionary(dictionary);
                Document dishDoc = CDBHelper.getDocByID(dictionary.getString("dishId"));
                totalPrice = MyBigDecimal.add(totalPrice,MyBigDecimal.mul(dishDoc.getFloat("price"),dictionary.getFloat("count"),1),1);
                MutableDocument document = doc.toMutable();
                document.setArray("goodsList",mutArray);
                document.setFloat("totalPrice",totalPrice);
                CDBHelper.saveDocument(document);
            }
            if (doc.getInt("orderType") == 0) {
                if (array.count() == 0) {
                    CDBHelper.deleDocument(doc);
                }
            }
        }
    }
    //增菜
    private void giveDishes(int pos, float counts) {

        MutableDocument oldGoods = goodsCList.get(pos);
        if (oldGoods.getInt("goodsType") != 0){
            return;
        }
        MutableDocument goodsMsgDoc = new MutableDocument("MsgGoods."+ToolUtil.getUUID());;
        Document document = CDBHelper.getDocByID(oldGoods.getId());
        float goodsCount = document.getFloat("count");//现有的数量
        if (goodsCount > counts){
            createGoodsMsg(goodsMsgDoc,oldGoods,counts,2);
            oldGoods.setFloat("count",MyBigDecimal.sub(goodsCount,counts,1));
            CDBHelper.saveDocument(oldGoods);
            setOrder(oldGoods,2,false);

        }else if (goodsCount == counts){
            createGoodsMsg(goodsMsgDoc, oldGoods, counts, 2);
            setOrder(oldGoods,2,true);
            MyLog.e("oldGoods----"+oldGoods.getId());
            CDBHelper.deleDocument(oldGoods);

        }
        adatper.notifyDataSetChanged();
    }

    private void backDishes(int pos, float counts) {
        MutableDocument oldGoods = goodsCList.get(pos);
        if (oldGoods.getInt("goodsType") != 2){
            return;
        }
        MutableDocument goodsMsgDoc = new MutableDocument("MsgGoods."+ToolUtil.getUUID());;
        Document document = CDBHelper.getDocByID(oldGoods.getId());
        float goodsCount = document.getFloat("count");//现有的数量
        if (goodsCount > counts){
            createGoodsMsg(goodsMsgDoc,oldGoods,counts,0);
            oldGoods.setFloat("count",MyBigDecimal.sub(goodsCount,counts,1));
            CDBHelper.saveDocument(oldGoods);
        }else if (goodsCount == counts){
            oldGoods.setInt("goodsType",0);
            CDBHelper.saveDocument(oldGoods);
        }
        adatper.notifyDataSetChanged();
    }

    private void retreatDishesFromZC(int pos, float counts) {
        MutableDocument oldGoods = goodsCList.get(pos);
        if (oldGoods.getInt("goodsType") != 2){
            return;
        }
        MutableDocument goodsMsgDoc = new MutableDocument("MsgGoods."+ToolUtil.getUUID());;
        Document document = CDBHelper.getDocByID(oldGoods.getId());
        float goodsCount = document.getFloat("count");//现有的数量
        if (goodsCount > counts){
            createGoodsMsg(goodsMsgDoc,oldGoods,counts,1);
            oldGoods.setFloat("count",MyBigDecimal.sub(goodsCount,counts,1));
            oldGoods.setInt("goodsType",1);
            CDBHelper.saveDocument(oldGoods);
        }
        adatper.notifyDataSetChanged();
    }


/**
     * 对正常菜品进行弹框处理
     *
     * @param pos
 **/
    private void normalDishesDialog(final int pos) {
        final int position = pos;
        Document docDish = CDBHelper.getDocByID(goodsCList.get(position).getString("dishId"));
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ShowParticularsActivity.this);
        final View view1 = LayoutInflater.from(ShowParticularsActivity.this).inflate(R.layout.activity_tv_dialog, null);
        alertDialog.setView(view1);
        final AlertDialog dialog = alertDialog.create();
        dialog.setCancelable(false);
        final TextView title = view1.findViewById(R.id.dialog_dishesName);
        title.setText(docDish.getString("name") + "(已点数量 " + goodsCList.get(pos).getFloat("count") + ")");
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
//                }else if (selActionId == R.id.dialog_delete_tc){
//
//                    editText.setText(1.0 + "");
//                    dialog_count.setText("退菜数量");
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

                } else if (selActionId == R.id.dialog_give_zc){

                    if (tmpCount <= 0) {
                        Toast.makeText(getApplicationContext(), "数量要求大于0", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (tmpCount > goodsCList.get(position).getFloat("count")) {
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
        Document dishDoc = CDBHelper.getDocByID(goodsCList.get(position).getString("dishId"));
        final AlertDialog.Builder builder = new AlertDialog.Builder(ShowParticularsActivity.this);
        final View view1 = LayoutInflater.from(ShowParticularsActivity.this).inflate(R.layout.activity_tv_dialog, null);
        builder.setView(view1);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        final TextView title = view1.findViewById(R.id.dialog_dishesName);
        title.setText(dishDoc.getString("name") + "(已点数量 " + goodsCList.get(pos).getFloat("count") + ")");
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
//        RadioButton rb2 = (RadioButton) view1.findViewById(R.id.dialog_delete_tc);
//        rb2.setVisibility(View.GONE);
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
                    if (tmpCount > goodsCList.get(position).getFloat("count")) {
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
                    if (tmpCount > goodsCList.get(position).getFloat("count")) {
                        Toast.makeText(getApplicationContext(), "不能大于原始数量", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    retreatDishesFromZC(position, tmpCount);
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
        final float unitCount = goodsCList.get(position).getFloat("count");
        Document dishDoc = CDBHelper.getDocByID(goodsCList.get(position).getString("dishId"));
        if (selActionId == R.id.dialog_add_zc){
            checkBox.setVisibility(View.GONE);
            checkBox.setChecked(false);
            editText.setText(""+1.0);
            title.setText(dishDoc.getString("name") + "(已点数量 " + unitCount+ ")");
        } else{
            checkBox.setVisibility(View.GONE);
            checkBox.setChecked(false);
            editText.setText(""+1.0);
            title.setText(dishDoc.getString("name") + "(已点数量 " + unitCount+ ")");
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        myapp = (MyApplication) getApplication();

        initView();
        goodsCList = new ArrayList<>();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        adatper = new ShowParticularsAdapter(this, goodsCList);
        showListView.setAdapter(adatper);
        initData();
        //连接打印机服务
        showListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                //点击订单OrderC
                Document obj = goodsCList.get(position);
                switch (obj.getInt("goodsType")) {
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
    }

    private void initView() {
         showListView = findViewById(R.id.show_listView);
         showButDc = findViewById(R.id.show_but_dc);
         //showButMd = findViewById(R.id.show_but_md);
         showTvSl = findViewById(R.id.show_tv_sl);
         showTvArea = findViewById(R.id.show_tv_area);
         showImg = findViewById(R.id.show_img);
        showImg.setOnClickListener(this);
        //showButMd.setOnClickListener(this);
        showButDc.setOnClickListener(this);
        findViewById(R.id.show_but_dy).setOnClickListener(this);
        findViewById(R.id.show_list_lin).setOnClickListener(this);
    }

    private void initData()
    {

        myapp.mExecutor.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        goodsCList.clear();
                                        all = 0f;
                                        orderCList = CDBHelper.getDocmentsByWhere(
                                                Expression.property("className").equalTo(Expression.string("Order"))
                                                        .and(Expression.property("tableMsgId").equalTo(Expression.string(myapp.getTable_sel_obj().getId())))
                                                        .and(Expression.property("state").equalTo(Expression.intValue(1)))
                                                , null
                                        );


                                        boolean flag = false;
                                        for (Document orderC : orderCList) {
                                            MyLog.e("orderC===="+orderC.getId());
                                            if (orderC.getInt("orderType") == 0) {
                                                all = MyBigDecimal.add(all, orderC.getFloat("totalPrice"), 1);
                                            }
                                            Array goodsCList1 = orderC.getArray("goodsList");
                                            for (int i = 0; i < goodsCList1.count(); i++) {
                                                String goodsId = goodsCList1.getDictionary(i).getString("id");
                                                Document goodsDic = CDBHelper.getDocByID(goodsId);
                                                if (goodsDic != null){
                                                    goodsCList.add(goodsDic.toMutable());
                                                }
                                            }
                                        }
                                        adatper.notifyDataSetChanged();
                                        Document document = CDBHelper.getDocByID(orderCList.get(0).getString("tableMsgId"));
                                        Document MsgTableDoc = CDBHelper.getDocByID(document.getString("tableId"));
                                        tableName = MsgTableDoc.getString("name");
                                        Document areaDoc = CDBHelper.getDocByID( document.getString("msgTable_areaId"));
                                        areaName = areaDoc.getString("name");
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
        goodsCList.clear();
        all = 0f;
        orderCList = CDBHelper.getDocmentsByWhere(
                Expression.property("className").equalTo(Expression.string("Order"))
                        .and(Expression.property("tableMsgId").equalTo(Expression.string(myapp.getTable_sel_obj().getId())))
                        .and(Expression.property("state").equalTo(Expression.intValue(1)))
                , null
        );


        boolean flag = false;

        for (Document orderC : orderCList) {
            if (orderC.getInt("orderType") == 0) {
                all = MyBigDecimal.add(all, orderC.getFloat("totalPrice"), 1);
            }
            Array goodsCList1 = orderC.getArray("goodsList");
            for (int i = 0; i < goodsCList1.count(); i++) {
                Document goodsDic = CDBHelper.getDocByID(goodsCList1.getDictionary(i).getString("id"));
                if (goodsDic != null){
                    goodsCList.add(goodsDic.toMutable());
                }

            }
        }
        adatper.notifyDataSetChanged();
        showTvArea.setText(areaName+",   "+ tableName);
        showTvSl.setText(goodsCList.size() + "道菜，总计：" + all + "元");
    }

    public void onClick(View view) {
        Intent intent;
        int i = view.getId();
        if (i == R.id.show_but_dc) {
            intent = new Intent(ShowParticularsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else if (i == R.id.show_img) {
            finish();

        } else if (i == R.id.show_but_dy) {
            if (Tool.isFastDoubleClick()) {
                Toast.makeText(ShowParticularsActivity.this, "点击太快，请稍候", Toast.LENGTH_LONG).show();
                return;
            } else {
                //setPrintOrder();
                anewPrint();
            }

        }
    }

    private void anewPrint(){
        for (Document document : orderCList){
            MutableDocument orderDoc = document.toMutable();
            orderDoc.setInt("printFlag",0);
            CDBHelper.saveDocument(orderDoc);
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
        if (myapp.getEmployee() != null){
            Document document = CDBHelper.getDocByID(myapp.getEmployee().getId());
            waiter = document.getString("name");
        }
        List<Company> companyCs = CDBHelper.getObjByClass( Company.class);
        PrintUtils.selectCommand(PrintUtils.RESET);
        PrintUtils.selectCommand(PrintUtils.LINE_SPACING_DEFAULT);
        PrintUtils.selectCommand(PrintUtils.ALIGN_CENTER);
        if (companyCs.size() != 0) {
            PrintUtils.printText(companyCs.get(0).getName() + "\n\n");
        }
        PrintUtils.selectCommand(PrintUtils.DOUBLE_HEIGHT_WIDTH);
        PrintUtils.printText(areaName + "/" + tableName + "\n\n");
        PrintUtils.selectCommand(PrintUtils.NORMAL);
        PrintUtils.selectCommand(PrintUtils.ALIGN_LEFT);
        PrintUtils.printText(PrintUtils.printTwoData("订单编号", orderCList.get(0).getInt("serialNum") + "\n"));
        PrintUtils.printText(PrintUtils.printTwoData("下单时间", getFormatDate() + "\n"));

        PrintUtils.printText(PrintUtils.printTwoData("人数：" + myapp.getTable_sel_obj().getInt("currentPersons"), "收银员：" + waiter + "\n"));
        PrintUtils.printText("--------------------------------\n");
        PrintUtils.selectCommand(PrintUtils.BOLD);
        PrintUtils.printText(PrintUtils.printThreeData("项目", "数量", "金额\n"));
        PrintUtils.printText("--------------------------------\n");
        PrintUtils.selectCommand(PrintUtils.BOLD_CANCEL);

        for (int j = 0; j < goodsCList.size(); j++) {

            Document goods = goodsCList.get(j);
            String taste = "";
            if (goods.getString("tasteId") != null) {
                Document doc = CDBHelper.getDocByID(goods.getString("tasteId"));
                taste = "(" + doc.getString("name") + ")";
            }
            Document document = CDBHelper.getDocByID("dishId");
            PrintUtils.printText(PrintUtils.printThreeData(document.getString("name") + taste,
                    goods.getFloat("count") + "",
                    MyBigDecimal.mul(document.getFloat("price"),goods.getFloat("count"),1) + "\n"));


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




    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLog.e("Main activity onDestroy");
        EventBus.getDefault().unregister(this);

    }

    private Handler uiHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {

            switch (msg.what)
            {
                case 0:
                   // proDialog.dismiss();
                    showTvArea.setText(areaName+",   "+ tableName);
                    showTvSl.setText(goodsCList.size() + "道菜，总计：" + all + "元");
                    adatper.notifyDataSetChanged();
                    break;

                case 1:
                    //printOrderToKitchen(tmpList);

                    break;

                case 4:
                   // proDialog.dismiss();
                    break;

                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    

}
