package doaing.statistics;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.Ordering;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.util.Calendar;

import doaing.test.R;
import tools.CDBHelper;
import view.BaseToobarActivity;

/**
 * @author donghaifeng
 */
@Route(path = "/statistics/StatisticsActivity")
public class StatisticsActivity extends BaseToobarActivity {

    private Database database;
    @Override
    protected int setMyContentView() {
        return (R.layout.activity_statistics);
    }

    @Override
    public void initData(Intent intent) {

        setToolbarName("统计");
        database = CDBHelper.getDatabase();
    }

    @Override
    protected Toolbar setToolBarInfo() {
        return findViewById(R.id.toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_statistics, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_statistics) {

            Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            View view = getLayoutInflater().inflate(R.layout.statistics_selectdate_dialog, null);
            final TextView startTv = view.findViewById(R.id.statistics_startdate_tv);
            final TextView endTv = view.findViewById(R.id.statistics_enddate_tv);
            startTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createDatePickerDialog(year, month, day, startTv, 0);
                }
            });
            endTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createDatePickerDialog(year, month, day, endTv, 1);
                }
            });
            alertDialog.setView(view);
            alertDialog.setTitle("设置搜索时间");
            alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (startTv.getText().length() > 0 && endTv.getText().length() > 0) {


                        Log.e("DOAING","全有");

                    } else if (startTv.getText().length() > 0 && endTv.getText().length() == 0) {
                        Log.e("DOAING","有开始时间");

                    } else if (startTv.getText().length() == 0 && endTv.getText().length() > 0) {
                        Log.e("DOAING","有结束时间");

                    }else {

                        Database database = CDBHelper.getDatabase();

                        Query query = QueryBuilder.select(SelectResult.expression(Meta.id))
                                .from(DataSource.database(database)).where(Expression.property("className")
                                        .equalTo(Expression.string("CheckOrderC")));
                        try {
                            ResultSet resultSet = query.execute();
                            Result row;
                            while ((row = resultSet.next()) != null) {
                                String id = row.getString(0);

                                Log.e("DOAING",id);


                            }
                        } catch (CouchbaseLiteException e) {
                            android.util.Log.e("getDocmentsByClass", "Exception=", e);
                        }


                    }

                }
            });
            alertDialog.show();

        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void createDatePickerDialog(int year, int month, int day, final TextView textView, int position) {
        //日历控件
        DatePickerDialog dp = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                String Month=String.format("%02d",(month + 1));
                String Day=String.format("%02d", dayOfMonth);
                textView.setText(year + "-" + Month + "-" + Day);

            }
        }, year, month, day);

        dp.show();
    }

}
