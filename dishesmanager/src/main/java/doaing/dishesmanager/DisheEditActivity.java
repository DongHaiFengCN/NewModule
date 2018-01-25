package doaing.dishesmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.couchbase.lite.Array;
import com.couchbase.lite.Blob;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.LiveQueryChange;
import com.couchbase.lite.LiveQueryChangeListener;
import com.couchbase.lite.Log;
import com.couchbase.lite.Query;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import butterknife.BindView;
import doaing.dishesmanager.widget.DishesKindSpinner;
import doaing.dishesmanager.widget.TasteSelectAdapter;
import module.MyApplication;
import view.BaseToobarActivity;

/**
 * @author donghaifeng
 */
public class DisheEditActivity extends BaseToobarActivity {

    private List<Document> tasteList;
    private List<Document> tasteAllList;
    private TasteSelectAdapter tasteSelectAdapter;
    private int kindPosition;
    private String[] strings;
    @BindView(R.id.dishe_im)
    ImageView disheIm;
    @BindView(R.id.dishe_name)
    EditText disheName;
    @BindView(R.id.dishe_price_et)
    EditText dishePriceEt;
    @BindView(R.id.reyview)
    RecyclerView reyview;
    @BindView(R.id.taste_im_bt)
    ImageView tasteImBt;
    @BindView(R.id.disheKind_sp)
    DishesKindSpinner disheKindSp;
    @BindView(R.id.dishe_submit_bt)
    Button disheSubmitBt;
    private Database database;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected int setMyContentView() {
        return R.layout.activity_dishe_edit;
    }

    @Override
    public void initData(Intent intent) {

        database = ((MyApplication) getApplicationContext()).getDatabase();

        initTasteSpinnerDate();
        Document document = database.getDocument(intent.getStringExtra("dishes"));
        kindPosition = intent.getIntExtra("position", 0);
        attachData(document);


    }

    /**
     * 初始化所有口味的集合
     */
    private void initTasteSpinnerDate() {


        tasteAllList = new ArrayList<>();
        LiveQuery query = Query.select(SelectResult.expression(Expression.meta().getId()))
                .from(DataSource.database(database))
                .where(Expression.property("className").equalTo("DishesTasteC")).toLive();

        query.addChangeListener(new LiveQueryChangeListener() {
            @Override
            public void changed(LiveQueryChange change) {

                if (!tasteAllList.isEmpty()) {
                    tasteAllList.clear();
                }
                ResultSet rows = change.getRows();
                Result row = null;
                while ((row = rows.next()) != null) {

                    String id = row.getString(0);
                    Document doc = database.getDocument(id);
                    tasteAllList.add(doc);

                }

                strings = new String[tasteAllList.size()];
                for (int i = 0; i < strings.length; i++) {
                    strings[i] = tasteAllList.get(i).getString("tasteName");
                }
            }
        });
        query.run();
        tasteImBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(DisheEditActivity.this);
                builder.setMultiChoiceItems(strings, new boolean[strings.length], new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            tasteList.add(tasteList.get(which));

                        } else {
                            tasteList.remove(which);

                        }
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tasteSelectAdapter.notifyDataSetChanged();

                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
            }
        });
    }

    /**
     * 绑定附加Document信息到控件
     *
     * @param document
     */
    private void attachData(Document document) {

        //1.添加图片
        Blob blob = document.getBlob("image");
        if (blob != null) {
            Glide.with(DisheEditActivity.this).load(blob.getContent()).into(disheIm);
        }

        //2.添加名称
        disheName.setText(document.getString("dishesName"));

        //3.添加价格
        dishePriceEt.setText(String.valueOf(document.getFloat("price")));

        //4.添加口味
        Array array = document.getArray("tasteList");
        initTasteAdapter(array);


        //5.指定选择种类

        disheKindSp.setSelection(kindPosition);


    }

    /**
     * 初始化编辑菜品的口味列表
     *
     * @param array
     */
    private void initTasteAdapter(Array array) {

        tasteList = new ArrayList<>();
        if(array != null){

            List<Object> objects = array.toList();
            for (Object s : objects) {

                Document document = database.getDocument(s.toString());
                tasteList.add(document);

            }
        }



        tasteSelectAdapter = new TasteSelectAdapter(tasteList, getApplicationContext());

        RecyclerView recyclerView = findViewById(R.id.reyview);

        LinearLayoutManager ms = new LinearLayoutManager(this);

        ms.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerView.setLayoutManager(ms);

        reyview.setAdapter(tasteSelectAdapter);
    }

    @Override
    protected Toolbar setToolBarInfo() {
        return toolbar;
    }


}
