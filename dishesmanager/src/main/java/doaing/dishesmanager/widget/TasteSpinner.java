package doaing.dishesmanager.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.LiveQueryChange;
import com.couchbase.lite.LiveQueryChangeListener;
import com.couchbase.lite.Query;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.util.ArrayList;
import java.util.List;

import module.MyApplication;

/**
 * 项目名称：new
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2018/1/22 14:20
 * 修改人：donghaifeng
 * 修改时间：2018/1/22 14:20
 * 修改备注：
 */

public class TasteSpinner extends android.support.v7.widget.AppCompatSpinner {

    public List<Document> getTasteList() {
        return tasteList;
    }

    private List<Document> tasteList = new ArrayList<>();
    ArrayAdapter<String> Adapter;

    public TasteSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        Adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item);
        Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setAdapter(Adapter);
        getTasteItems();
    }

    public TasteSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TasteSpinner(Context context) {
        super(context);


    }

    public void getTasteItems() {

        final Database database = ((MyApplication) getContext().getApplicationContext()).getDatabase();

        LiveQuery query = Query.select(SelectResult.expression(Expression.meta().getId()))
                .from(DataSource.database(database))
                .where(Expression.property("className").equalTo("DishesTasteC")).toLive();
        query.addChangeListener(new LiveQueryChangeListener() {
            @Override
            public void changed(LiveQueryChange change) {

              if (!tasteList.isEmpty()){
                  tasteList.clear();
              }
              if (Adapter.getCount()>0){
                  Adapter.clear();
              }
                ResultSet rows = change.getRows();
                Result row = null;
                while ((row = rows.next()) != null) {


                    String id = row.getString(0);
                    Document doc = database.getDocument(id);
                    tasteList.add(doc);

                    Adapter.add(doc.getString("tasteName").toString());
                }

            }
        });
        query.run();

    }
}
