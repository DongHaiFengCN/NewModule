package doaing.order.device.kitchen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

import bean.kitchenmanage.dishes.DishesKindC;
import bean.kitchenmanage.kitchen.KitchenClientC;
import butterknife.BindView;
import butterknife.OnClick;
import doaing.mylibrary.MyApplication;
import doaing.order.R;
import doaing.order.R2;
import doaing.order.application.CDBHelper;
import view.BaseToobarActivity;
import doaing.order.device.kitchen.AddKitchenAdapter.ViewHolder;

/*
*
 * Created by lenovo on 2018/2/1.

*/


public class AddkitchenActivity extends BaseToobarActivity implements View.OnClickListener{
    ImageView addclientBtBack;
    EditText etClientname;
    Button spPrinter;
    ListView listViewCk;

    private List<String> allDishKindIdList;
    private List<String> listSelectedDocId;
    private MyApplication myapp;
    private String[] PrinterNames;
    private int position = 0;
    private AddKitchenAdapter addKitchenAdapter;
    private String selectPrinterName,selectDocId;
    @Override
    protected int setMyContentView() {
        return R.layout.activity_addkitchen;
    }

    @Override
    public void initData(Intent intent) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        myapp = (MyApplication) getApplication();
        addclientBtBack = findViewById(R.id.addclient_bt_back);
        initViewData();
        InitSpinnerData();
        initView();
    }

    private void initViewData() {
        addclientBtBack = findViewById(R.id.addclient_bt_back);
        etClientname = findViewById(R.id.et_clientname);
        spPrinter = findViewById(R.id.spPrinter);
        listViewCk = findViewById(R.id.listView_ck);
        addclientBtBack.setOnClickListener(this);
        findViewById(R.id.selectall).setOnClickListener(this);
        findViewById(R.id.bt_deselectall).setOnClickListener(this);
        findViewById(R.id.cancelselectall).setOnClickListener(this);
        findViewById(R.id.btn_kcsave).setOnClickListener(this);
        spPrinter.setOnClickListener(this);
    }

    @Override
    protected Toolbar setToolBarInfo() {
        return null;
    }

    public void onClick(View view) {
        int i1 = view.getId();
        if (i1 == R.id.addclient_bt_back) {
            finish();

        } else if (i1 == R.id.selectall) {
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

        } else if (i1 == R.id.cancelselectall) {
            for (int i = 0; i < allDishKindIdList.size(); i++) {
                if (addKitchenAdapter.getIsSelected().get(i)) {
                    addKitchenAdapter.getIsSelected().put(i, false);
                }
            }
            addKitchenAdapter.notifyDataSetChanged();

        } else if (i1 == R.id.btn_kcsave) {
            if (isNull(selectDocId)) {
                IpCheck();
            } else {
                UpDataIP(selectDocId);
            }


        } else if (i1 == R.id.spPrinter) {
            dialog();

        }
    }

    private void InitSpinnerData()
    {
        PrinterNames=new String[4];
        PrinterNames[0] = getString(R.string.gprinter001);
        PrinterNames[1] = getString(R.string.gprinter002);
        PrinterNames[2] = getString(R.string.gprinter003);
        PrinterNames[3] = getString(R.string.gprinter004);
        spPrinter.setText(PrinterNames[position]);
    }
    private boolean isNull(String str)
    {
        return str == null || "".equals(str);
    }

    private void IpCheck()
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
        obj.setKitchenAdress(spPrinter.getText().toString());

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
            docKitchenClient.setString("kitchenAdress",spPrinter.getText().toString());
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

        Intent intent = getIntent();
        selectPrinterName = intent.getStringExtra("ipcontent");//Printer 002
        String strname = intent.getStringExtra("clientname");//厨房名字
        selectDocId = intent.getStringExtra("docId");

        if (selectPrinterName == null || "".equals(selectPrinterName)) {
        } else {
            int position = 0;
            for (int i = 0; i < PrinterNames.length; i++) {
                if (PrinterNames[i].equals(selectPrinterName))
                    position = i;
            }
            spPrinter.setText(PrinterNames[position]);
            etClientname.setText(strname);
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

    //自定义弹窗
    public void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddkitchenActivity.this);
        builder.setCancelable(true);
        builder.setTitle("选择打印机");
        builder.setSingleChoiceItems(PrinterNames, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                position = which;
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                spPrinter.setText(PrinterNames[position]);
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
