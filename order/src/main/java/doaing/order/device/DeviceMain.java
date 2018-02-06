package doaing.order.device;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;

import doaing.order.R;
import doaing.order.device.kitchen.KitchenCfgActivity;
import view.BaseToobarActivity;

public class DeviceMain extends BaseToobarActivity implements View.OnClickListener{

    private Toolbar toolbar;
    @Override
    protected int setMyContentView() {
        return R.layout.activity_device;
    }

    @Override
    public void initData(Intent intent) {

        setToolbarName("厨房管理");
        toolbar = findViewById(R.id.toolbar);
        findViewById(R.id.activity_device_printer).setOnClickListener(this);
        findViewById(R.id.activity_device_kitchen).setOnClickListener(this);

    }

    @Override
    protected Toolbar setToolBarInfo() {
        return toolbar;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        int i = v.getId();
        if (i == R.id.activity_device_printer){
            intent = new Intent(DeviceMain.this,KitchenCfgActivity.class);
            startActivity(intent);
        }else if(i == R.id.activity_device_kitchen){
            intent = new Intent(DeviceMain.this,PrinterConnectDialog.class);

            startActivity(intent);

        }else{

    }
    }
}
