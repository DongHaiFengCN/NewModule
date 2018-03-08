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

import com.alibaba.android.arouter.facade.annotation.Route;
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

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import doaing.test.R;
import tools.CDBHelper;
import tools.MyBigDecimal;
import view.BaseToobarActivity;

/**
 * @author donghaifeng
 */
@Route(path = "/statistics/StatisticsActivity")
public class StatisticsActivity extends BaseToobarActivity {
    private Database database;

    private float monetary;
    private float realIncome;
    private float ml;
    private float cash;
    private float alipay;
    private float wechat;
    private float bank;
    private float member;
    private float elm;
    private float mt;
    private float gz;


    private TextView dateTv;
    private TextView monetaryTv;
    private TextView realIncomeTv;
    private TextView mlTv;
    private TextView cashTv;
    private TextView alipayTv;
    private TextView wechatTv;
    private TextView bankTv;
    private TextView memberTv;
    private TextView elmTv;
    private TextView mtTv;
    private TextView gzTv;


    @Override
    protected int setMyContentView() {
        return (R.layout.activity_statistics);
    }

    @Override
    public void initData(Intent intent) {
        setToolbarName("统计");
        database = CDBHelper.getDatabase();

        dateTv = findViewById(R.id.statistics_date_tv);
        monetaryTv = findViewById(R.id.satistics_monetary_tv);
        realIncomeTv = findViewById(R.id.satistics_real_income_tv);
        mlTv = findViewById(R.id.statistics_ml_tv);
        cashTv = findViewById(R.id.statistics_cash_tv);
        alipayTv = findViewById(R.id.statistics_alipay_tv);
        wechatTv = findViewById(R.id.statistics_wechat_tv);
        bankTv = findViewById(R.id.statistics_bank_tv);
        elmTv = findViewById(R.id.statistics_elm_tv);
        mtTv = findViewById(R.id.statistics_mt_tv);
        memberTv = findViewById(R.id.statistics_member_tv);
        gzTv = findViewById(R.id.statistics_gz_tv);
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
                    database = CDBHelper.getDatabase();

                    if (monetary != 0 && realIncome != 0) {
                        monetary = 0f;
                        realIncome = 0f;
                        cash = 0f;
                        alipay = 0f;
                        wechat = 0f;
                        ml = 0f;
                        gz = 0f;

                    }

                    if (startTv.getText().length() > 0 && endTv.getText().length() > 0) {

                        getDoubleDateInfo(startTv.getText().toString(), endTv.getText().toString());

                    } else if (startTv.getText().length() > 0 && endTv.getText().length() == 0) {

                        getStartSingleDateInfo(startTv.getText().toString());

                    } else if (startTv.getText().length() == 0 && endTv.getText().length() > 0) {

                        getEndSingleDateInfo(endTv.getText().toString());

                    }

                }
            });
            alertDialog.show();

        }
        return true;
    }

    /**
     * 起始日期都设置进行搜索账单
     *
     * @param start 开始日期
     * @param end   结束日期
     */
    private void getDoubleDateInfo(String start, String end) {
        dateTv.setText((new StringBuilder()).append(start).append("----").append(end));
        Query query = QueryBuilder.select(SelectResult.expression(Meta.id))
                .from(DataSource.database(database)).where(Expression.property("className")
                        .equalTo(Expression.string("CheckOrderC"))
                        .and(Expression.property("checkTime")
                                .greaterThanOrEqualTo(Expression.string(start).lessThanOrEqualTo(Expression.string(end))))
                );
        queryBody(query);

    }

    /**
     * 只设置初始日期进行搜索账单
     *
     * @param date 开始日期
     */
    private void getStartSingleDateInfo(String date) {
        dateTv.setText((new StringBuilder()).append(date).append("---").append("今日"));

        Query query = QueryBuilder.select(SelectResult.expression(Meta.id))
                .from(DataSource.database(database)).where(Expression.property("className")
                        .equalTo(Expression.string("CheckOrderC"))
                        .and(Expression.property("checkTime")
                                .greaterThanOrEqualTo(Expression.string(date)))
                );

        queryBody(query);
    }

    /**
     * 只设置结束时间进行搜索账单
     *
     * @param date 结束日期
     */
    private void getEndSingleDateInfo(String date) {
        dateTv.setText(date);
        Query query = QueryBuilder.select(SelectResult.expression(Meta.id))
                .from(DataSource.database(database)).where(Expression.property("className")
                        .equalTo(Expression.string("CheckOrderC"))
                        .and(Expression.property("checkTime")
                                .like(Expression.string(date + "%")))
                );
        queryBody(query);
    }

    /**
     * 获取账单数据处理并展示
     *
     * @param query
     */
    private void queryBody(Query query) {
        try {
            ResultSet resultSet = query.execute();
            Result row;
            while ((row = resultSet.next()) != null) {
                String id = row.getString(0);
                Document document = database.getDocument(id);
                monetary = MyBigDecimal.add(monetary, document.getFloat("pay"), 2);
                realIncome = MyBigDecimal.add(realIncome, document.getFloat("needPay"), 2);
                Dictionary promotionDetail = document.getDictionary("promotionDetail");
                Array payDetailList = promotionDetail.getArray("payDetailList");
                for (int i = 0; i < payDetailList.count(); i++) {
                    Dictionary paydetailc = payDetailList.getDictionary(i);
                    int payType = paydetailc.getInt("payTypes");
                    float subtotal = paydetailc.getFloat("subtotal");
                    //  支付方式 支付类型名称 1\现金、2\银行卡、3\微信、4\支付宝、5\美团、6\会员卡 7\抹零 8\赠卷\9\饿了吗 10\挂账
                    switch (payType) {
                        case 1:
                            cash = MyBigDecimal.add(cash, subtotal, 2);
                            break;
                        case 2:
                            bank = MyBigDecimal.add(bank, subtotal, 2);
                            break;
                        case 3:
                            wechat = MyBigDecimal.add(wechat, subtotal, 2);
                            break;
                        case 4:
                            alipay = MyBigDecimal.add(alipay, subtotal, 2);
                            break;
                        case 5:
                            mt = MyBigDecimal.add(mt, subtotal, 2);
                            break;
                        case 6:
                            member = MyBigDecimal.add(member, subtotal, 2);
                            break;
                        case 7:
                            ml = MyBigDecimal.add(ml, subtotal, 2);
                            break;
                        case 8:
                            break;
                        case 9:
                            elm = MyBigDecimal.add(elm, subtotal, 2);
                            break;
                        case 10:
                            gz = MyBigDecimal.add(gz, subtotal, 2);
                            break;
                        default:
                            break;

                    }
                }

            }
        } catch (CouchbaseLiteException e) {
            Log.e("getDocmentsByClass", "Exception=", e);
        }

        mlTv.setText(String.valueOf(ml));
        monetaryTv.setText(String.valueOf(monetary));
        realIncomeTv.setText(String.valueOf(realIncome));
        alipayTv.setText(String.valueOf(alipay));
        cashTv.setText(String.valueOf(cash));
        wechatTv.setText(String.valueOf(wechat));
        memberTv.setText(String.valueOf(member));
        elmTv.setText(String.valueOf(elm));
        mtTv.setText(String.valueOf(mt));
        bankTv.setText(String.valueOf(bank));
        gzTv.setText(String.valueOf(gz));
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void createDatePickerDialog(int year, int month, int day, final TextView textView, int position) {
        //日历控件
        DatePickerDialog dp = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                String Month = String.format("%02d", (month + 1));
                String Day = String.format("%02d", dayOfMonth);
                textView.setText(year + "-" + Month + "-" + Day);

            }
        }, year, month, day);

        dp.show();
    }

}
