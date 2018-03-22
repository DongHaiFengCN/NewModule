package doaing.dishesmanager;

import android.content.Intent;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.couchbase.lite.Array;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;


import bean.kitchenmanage.dishes.DishesC;
import tools.CDBHelper;
import view.BaseToobarActivity;

public class SecondPackageActivity extends BaseToobarActivity {


    private String firstId;
    private Database database;
    private Array array;
    private MyAdapter myAdapter;
    private ListView listView;

    @Override
    protected int setMyContentView() {

        return R.layout.activity_second_package;
    }

    @Override
    public void initData(Intent intent) {

        firstId = intent.getExtras().get("id").toString();

        database = CDBHelper.getDatabase();

        listView = findViewById(R.id.secondpackage_lv);
        setToolbarName("二级套餐");

    }

    @Override
    protected void onStart() {
        super.onStart();
        Document document = database.getDocument(firstId);

        array = document.getArray("dishesListId");


        if (myAdapter == null) {

            myAdapter = new MyAdapter();
            listView.setAdapter(myAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    Intent intent = new Intent(SecondPackageActivity.this,PackageEditActivity.class);
                    intent.putExtra("disheId",array.getString(position));
                    intent.putExtra("kindId",firstId);
                    startActivity(intent);
                }
            });


        } else {
            myAdapter.notifyDataSetChanged();
        }


    }

    @Override
    protected Toolbar setToolBarInfo() {
        return findViewById(R.id.toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.packge_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {

            Intent intent = new Intent(SecondPackageActivity.this, PackageAddActivity.class);
            intent.putExtra("id", firstId);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }

    class MyAdapter extends BaseAdapter {

        int p1 = 0;
        @Override
        public int getCount() {
            return array == null ? 0 : array.count();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Document document = null;

            //防止多次读库
            if(p1<array.count()){

                document = database.getDocument(array.getString(position));

                p1++;
                Log.e("DOAING",position+"");

            }

            Holder holder;
            if (convertView == null) {

                holder = new Holder();
                convertView = getLayoutInflater().inflate(R.layout.second_item_lv, null);
                holder.secondNameTv = convertView.findViewById(R.id.name_tv);
                holder.secondPriceTv = convertView.findViewById(R.id.price_tv);
                convertView.setTag(holder);

            } else {

                holder = (Holder) convertView.getTag();
            }
            if(document!=null){
                holder.secondNameTv.setText(document.getString("dishesName"));
                holder.secondPriceTv.setText(String.valueOf(document.getFloat("price"))+"/元");
            }



            return convertView;
        }


        class Holder {

            TextView secondNameTv;

            TextView secondPriceTv;


        }
    }
}
