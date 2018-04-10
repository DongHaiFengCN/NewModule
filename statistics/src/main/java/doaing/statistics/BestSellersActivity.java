package doaing.statistics;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.couchbase.lite.Array;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import doaing.test.R;
import tools.CDBHelper;
import tools.MyBigDecimal;
import view.BaseToobarActivity;

public class BestSellersActivity extends BaseToobarActivity {
    private Database database;
    private ListView listView;
    private BestSellersAdapter bestSellersAdapter;
    private Calendar calendar;
    private int year;
    private int month;
    private int day;
    @SuppressLint("DefaultLocale") String month1;
    @SuppressLint("DefaultLocale") String day1;

    static final String TODAY ="销量排行--今日";

    static final String WEEK ="销量排行--本周";
    static final String MONTH ="销量排行--本月";
    @Override
    protected int setMyContentView() {
        return R.layout.activity_best_sellers;
    }

    @Override
    public void initData(Intent intent) {

        setToolbarName(TODAY);

        database = CDBHelper.getDatabase();

        listView = findViewById(R.id.my_lv);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month1 = String.format("%02d", (month + 1));
        day1 = String.format("%02d", day);

        StringBuilder stringBuilder = new StringBuilder();
        getStartSingleDateInfo(stringBuilder.append(year).append("-").append(month1).append("-").append(day1).toString());
    }

    @Override
    protected Toolbar setToolBarInfo() {
        return findViewById(R.id.toolbar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bestseseller, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {



        if (item.getItemId() == R.id.action_today) {

            getSupportActionBar().setTitle(TODAY);
            StringBuilder stringBuilder = new StringBuilder();
            getStartSingleDateInfo(stringBuilder.append(year).append("-").append(month1).append("-").append(day1).toString());

        } else if (item.getItemId() == R.id.action_week) {
            getSupportActionBar().setTitle(WEEK);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            getStartSingleDateInfo(formatter.format(calendar.getTime()));

        } else if (item.getItemId() == R.id.action_month) {
            getSupportActionBar().setTitle(MONTH);
            StringBuilder stringBuilder = new StringBuilder();
            getStartSingleDateInfo( stringBuilder.append(year).append("-").append(month1).append("-").append("01").toString());

        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * @param date 开始日期
     */
    private void getStartSingleDateInfo(String date) {

        Query query = QueryBuilder.select(SelectResult.expression(Expression.property("goodsList")))
                .from(DataSource.database(database)).where(Expression.property("className")
                        .equalTo(Expression.string("OrderC")).and(Expression.property("orderState").equalTo(Expression.intValue(0)))
                        .and(Expression.property("createdTime")
                                .greaterThanOrEqualTo(Expression.string(date)))


                );
        queryBody(query);
    }

    private void queryBody(Query orderQuery) {

        HashMap<String, Float> hmap = new HashMap<>();
        ResultSet resultSet = null;

        try {
            resultSet = orderQuery.execute();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        List<Result> list = resultSet.allResults();

        int size = list.size();

        for (int i = 0; i < size; i++) {

            Result result = list.get(i);

            Array array = result.getArray("goodsList");

            for (int j = 0; j < array.count(); j++) {

                Dictionary dictionary = array.getDictionary(j);


                if (hmap.containsKey(dictionary.getString("dishesName"))) {

                    hmap.put(dictionary.getString("dishesName")
                            , hmap.get(dictionary.getString("dishesName"))
                                    + dictionary.getFloat("dishesCount"));
                } else {
                    hmap.put(dictionary.getString("dishesName")
                            , dictionary.getFloat("dishesCount"));
                }
            }
        }
        List<Map.Entry<String, Float>> mapList = new ArrayList<>(hmap.entrySet());
        Collections.sort(mapList, new Comparator<Map.Entry<String, Float>>() {
            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        if (bestSellersAdapter == null) {

            bestSellersAdapter = new BestSellersAdapter(BestSellersActivity.this, mapList);
            listView.setAdapter(bestSellersAdapter);
        } else {
            bestSellersAdapter.setMapList(mapList);
        }


    }

}
