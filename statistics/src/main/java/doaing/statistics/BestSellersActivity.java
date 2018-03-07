package doaing.statistics;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import doaing.test.R;
import tools.CDBHelper;
import view.BaseToobarActivity;

public class  BestSellersActivity extends BaseToobarActivity {
    private Database database;


    @Override
    protected int setMyContentView() {
        return R.layout.activity_best_sellers;
    }

    @Override
    public void initData(Intent intent) {
        setToolbarName("热卖排行");
        database = CDBHelper.getDatabase();
/*        String companyID = ((MyApplication) getApplicationContext()).getCompany_ID();
        MutableDocument mutableDocument = new MutableDocument("CheckOrderC." + ToolUtil.getUUID());
        mutableDocument.setString("className", "CheckOrderC");
        mutableDocument.setString("channelId", companyID);
        try {
            database.save(mutableDocument);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }*/
        checkOrderQuery();
        ListView listView = findViewById(R.id.my_lv);


    }

    @Override
    protected Toolbar setToolBarInfo() {
        return findViewById(R.id.toolbar);
    }

    private class myAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 0;
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
            return null;
        }


    }

    private void checkOrderQuery() {
        //动态监听DishesKind信息
        Query query = QueryBuilder.select(SelectResult.expression(Meta.id))
                .from(DataSource.database(this.database))
                .where(Expression.property("className").equalTo(Expression.string("CheckOrderC")));

        try {
            ResultSet rows = query.execute();
            Result row;
            while ((row = rows.next()) != null) {
                String id = row.getString(0);

                Log.e("DOAING", id);
            }


        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

    }
}
