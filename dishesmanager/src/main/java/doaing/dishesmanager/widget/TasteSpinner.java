package doaing.dishesmanager.widget;

import android.content.Context;
import android.util.AttributeSet;

import android.widget.ArrayAdapter;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.util.ArrayList;
import java.util.List;

import doaing.MyApplication;

/**
 * 项目名称：new
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2018/1/22 14:20
 * 修改人：donghaifeng
 * 修改时间：2018/1/22 14:20
 * 修改备注：
 *
 * @author donghaifeng
 */
public class TasteSpinner extends android.support.v7.widget.AppCompatSpinner {

    public List<Document> getTasteList() {
        return tasteList;
    }

    private List<Document> tasteList = new ArrayList<>();
    ArrayAdapter tasteAdapter;

    public TasteSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        tasteAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item);
        tasteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        getTasteItems();
        setAdapter(tasteAdapter);

    }

    public TasteSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TasteSpinner(Context context) {
        super(context);


    }

    public void getTasteItems() {

        final Database database = ((MyApplication) getContext().getApplicationContext()).getDatabase();

        Query query = QueryBuilder.select(SelectResult.expression(Meta.id))
                .from(DataSource.database(database))
                .where(Expression.property("className").equalTo(Expression.string("DishesTasteC")));

        ResultSet results = null;
        try {
            results = query.execute();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        Result row;
        while ((row = results.next()) != null) {
            String id = row.getString(0);
            Document doc = database.getDocument(id);
            tasteList.add(doc);
            tasteAdapter.add(doc.getString("tasteName"));


  /*      query.addChangeListener(new LiveQueryChangeListener() {
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
        });*/


        }
    }
}
