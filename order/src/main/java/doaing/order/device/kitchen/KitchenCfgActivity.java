package doaing.order.device.kitchen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bean.kitchenmanage.kitchen.KitchenClientC;
import butterknife.OnClick;
import doaing.order.R;
import tools.CDBHelper;
import view.BaseToobarActivity;

/*
*
 * Created by lenovo on 2018/1/31.

*/


public class KitchenCfgActivity extends BaseToobarActivity implements View.OnClickListener{
    ListView listViewKC;
    private List<String> infomations;
    private MyListAdapter myListAdapter;

    @Override
    protected int setMyContentView() {
        return R.layout.activity_kitchencfg;
    }

    @Override
    public void initData(Intent intent) {
        myListAdapter = new MyListAdapter(KitchenCfgActivity.this, infomations);
        listViewKC = findViewById(R.id.listViewKC);
        findViewById(R.id.activity_kitchen_back).setOnClickListener(this);
        findViewById(R.id.activity_kitchen_tj).setOnClickListener(this);
        UpdateListView();
    }

    @Override
    protected Toolbar setToolBarInfo() {
        return null;
    }


    private void loadfromdb() {
        if (infomations == null) {
            infomations = new ArrayList<>();
        }
        infomations.clear();
        infomations = CDBHelper.getIdsByClass(getApplicationContext(), KitchenClientC.class);
        Log.e("loadformdb", "size=" + infomations.size());

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2015) {
            UpdateListView();
        }

    }

    private void UpdateListView() {
        loadfromdb();
        myListAdapter.refreshItem(infomations);
        myListAdapter.notifyDataSetChanged();
        listViewKC.setAdapter(myListAdapter);
    }


    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.activity_kitchen_back) {
            finish();

        } else if (i == R.id.activity_kitchen_tj) {
            Bundle bundle = new Bundle();
            infomations = CDBHelper.getIdsByClass(getApplicationContext(), KitchenClientC.class);
            Intent intent = new Intent(KitchenCfgActivity.this, AddkitchenActivity.class);
            bundle.putSerializable("infomations", (Serializable) infomations);
            intent.putExtras(bundle);
            startActivityForResult(intent, 2015);

        }else{

        }
    }
}
