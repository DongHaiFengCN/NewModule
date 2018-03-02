package doaing.statistics;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import java.util.Calendar;
import doaing.test.R;
import view.BaseToobarActivity;
/**
 * @author donghaifeng
 */
public class StatisticsActivity extends BaseToobarActivity {

    @Override
    protected int setMyContentView() {
        return (R.layout.activity_statistics);
    }

    @Override
    public void initData(Intent intent) {
        setToolbarName("统计");
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

                textView.setText(year + "/" + (month + 1) + "/" + dayOfMonth);

            }
        }, year, month, day);

        dp.show();
    }

}
