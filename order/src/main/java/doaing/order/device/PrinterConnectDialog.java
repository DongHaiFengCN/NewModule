package doaing.order.device;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.journeyapps.barcodescanner.CaptureActivity;

import java.util.List;

import bean.kitchenmanage.qrcode.qrcodeC;
import doaing.mylibrary.MyApplication;
import doaing.order.R;
import tools.CDBHelper;
import view.BaseToobarActivity;

/*
*
 * Created by lenovo on 2018/1/27.

*/


public class PrinterConnectDialog extends BaseToobarActivity {

    private final static String DEBUG_TAG = "SamleApp";
    Toolbar toolbar;
    TextView qrcodeWxcontent;
    CheckBox ifqrcodeprint1;
    TextView qrcodeZfbcontent;
    CheckBox ifqrcodeprint2;
    EditText printnums;
    CheckBox cboxWxYao;
    private MyApplication myapp;
    private final static int SCANNIN_GREQUEST_CODE1 = 1;
    private final static int SCANNIN_GREQUEST_CODE2 = 2;
    private qrcodeC obj_qrcodepay;

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
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
        initView();
        initQrPay();
    }

    @Override
    protected Toolbar setToolBarInfo() {
        return toolbar;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        Log.e(DEBUG_TAG, "onDestroy ");
        super.onDestroy();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        qrcodeWxcontent = findViewById(R.id.tv_wxqrcode_content);
        ifqrcodeprint1 = findViewById(R.id.cbox_qrcode_ifprint1);
        qrcodeZfbcontent = findViewById(R.id.tv_zfbqrcode_content);
        ifqrcodeprint2 = findViewById(R.id.cbox_qrcode_ifprint2);
        printnums = findViewById(R.id.edt_print_nums);
        cboxWxYao = findViewById(R.id.cbox_wx_yao);
        setToolbarName("打印小票");
        cboxWxYao.setChecked(false);

    }
    private void initQrPay()
    {
        final List<qrcodeC> qrCodeDoc= CDBHelper.getObjByClass(getApplicationContext(),qrcodeC.class);
        if(qrCodeDoc.size()>0)
        {
            obj_qrcodepay = qrCodeDoc.get(0);

            qrcodeWxcontent.setText(obj_qrcodepay.getWxUrl());//
            qrcodeZfbcontent.setText(obj_qrcodepay.getZfbUrl());//


            if(obj_qrcodepay.isWxPrintFlag()){
                ifqrcodeprint1.setChecked(true);
            } else {
                ifqrcodeprint1.setChecked(false);
            }
            if(obj_qrcodepay.isZfbPrintFlag()) {
                ifqrcodeprint2.setChecked(true);
            }else {
                ifqrcodeprint2.setChecked(false);
            }

            if (obj_qrcodepay.isWxReceiveFlag()){
                cboxWxYao.setChecked(true);
            }else{
                cboxWxYao.setChecked(false);
            }
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

        cboxWxYao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    obj_qrcodepay.setWxReceiveFlag(true);
                }else{
                    obj_qrcodepay.setWxReceiveFlag(false);
                }
                CDBHelper.createAndUpdate(getApplicationContext(),obj_qrcodepay);
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
            obj_qrcodepay.setWxPrintFlag(ifqrcodeprint1.isChecked());
            obj_qrcodepay.setZfbPrintFlag(ifqrcodeprint2.isChecked());
            obj_qrcodepay.setNums(nums);
            CDBHelper.createAndUpdate(getApplicationContext(),obj_qrcodepay);

        }

    }
}
