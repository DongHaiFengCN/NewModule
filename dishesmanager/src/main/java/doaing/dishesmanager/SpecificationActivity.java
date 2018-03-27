package doaing.dishesmanager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.QueryChange;
import com.couchbase.lite.QueryChangeListener;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import bean.kitchenmanage.dishes.DishesC;
import butterknife.BindView;
import doaing.dishesmanager.adapter.ListAdapter;
import doaing.mylibrary.MyApplication;
import tools.CDBHelper;
import tools.ToolUtil;
import view.BaseToobarActivity;


/**
 * @author donghaifeng
 * @Data 2018/1/19
 */

public class SpecificationActivity extends BaseToobarActivity {

    ListAdapter listAdapter;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    private AutoCompleteTextView speciflcation_maindishes,speciflcation_deputydishes;
    private ListView guige_list;
    private List<String> dishesList;
    private ArrayAdapter dishesAdapter;
    private String supDishesId,dishesKindId;
    private float supPrice;
    private List<Document> HaveDishesSupList;
    private List<Document> dishesCList;
    private MutableDocument document;
    private Handler mHandle = null;
    @Override
    protected int setMyContentView() {
        return R.layout.activity_dishes_kind;
    }

    @Override
    protected Toolbar setToolBarInfo() {
        return toolbar;
    }

    @Override
    public void initData(Intent intent) {
        setToolbarName("规格管理");
        guige_list = findViewById(R.id.disheskind_lv);
        mHandle = new Handler();
        mHandle.post(new Runnable() {
            @Override
            public void run() {
                dishesCList = CDBHelper.getDocmentsByClass(getApplicationContext(),DishesC.class);
                HaveDishesSupList = CDBHelper.getDocmentsByWhere(getApplicationContext(),
                        Expression.property("className").equalTo(Expression.string("DishesC"))
                                .and(Expression.property("haveSupDishes").like(Expression.booleanValue(true)))
                        ,null);

                listAdapter  = new ListAdapter(SpecificationActivity.this,HaveDishesSupList);
                guige_list.setAdapter(listAdapter);
            }
        });
    }
    /**
     * 查询主菜品,并添加副菜品
     */
    private void setGuiGeDishes(String guiGeDishes){
        List<Document> dishesList = CDBHelper.getDocmentsByWhere(getApplicationContext(),
                Expression.property("className").equalTo(Expression.string("DishesC"))
                        .and(Expression.property("dishesName").like(Expression.string(guiGeDishes)))
                ,null);
        if (dishesList.size() == 0){
            Toast.makeText(this,"输入主菜品不存在",Toast.LENGTH_LONG).show();
            return;
        }
        Document doc = dishesList.get(0);
        document = doc.toMutable();
        dishesKindId = document.getString("dishesKindId");

    }


    /**
     * 查询副菜品
     */
    private void setGuiGeSupDishes(String guiGeSupDishes,String dishesSupCount){
        List<Document> dishesList = CDBHelper.getDocmentsByWhere(getApplicationContext(),
                Expression.property("className").equalTo(Expression.string("DishesC"))
                        .and(Expression.property("dishesName").equalTo(Expression.string(guiGeSupDishes)))
                        .and(Expression.property("dishesKindId").equalTo(Expression.string(dishesKindId)))
                ,null);

        if (dishesList.size() != 0){
            Document dishesObj = dishesList.get(0);
            supDishesId = dishesObj.getId();
            supPrice = dishesObj.getFloat("price");
        }else{
            Toast.makeText(this,"输入副菜品不匹配",Toast.LENGTH_LONG).show();
            return;
        }
        document.setBoolean("haveSupDishes",true);
        document.setString("supDishesId",supDishesId);
        document.setFloat("supPrice",supPrice);
        document.setFloat("supCount",Float.parseFloat(dishesSupCount));
        document.setString("supDishesName",guiGeSupDishes);
        CDBHelper.saveDocument(getApplicationContext(),document);

    }

    /**
     * 查找HaveSupDishes是否为true
     */
    private void findHaveSupDishes(){
        HaveDishesSupList = CDBHelper.getDocmentsByWhere(getApplicationContext(),
                Expression.property("className").equalTo(Expression.string("DishesC"))
                        .and(Expression.property("haveSupDishes").like(Expression.booleanValue(true)))
                ,null);

        listAdapter  = new ListAdapter(this,HaveDishesSupList);
        guige_list.setAdapter(listAdapter);
        Log.e("GuiGeActivity",""+HaveDishesSupList.size());
        if (speciflcation_deputydishes == null){
            return;
        }
        if (dishesList == null){
            dishesList = new ArrayList<>();
        }else{
            dishesList.clear();
        }

        for (Document dishesObj : dishesCList) {
            if (dishesObj.getString("dishesName") !=null)
                dishesList.add(dishesObj.getString("dishesName"));

        }
        Log.e("GuiGeActivity",""+dishesList.size()+"----"+dishesCList.size());
        dishesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, dishesList);
        speciflcation_maindishes.setAdapter(dishesAdapter);
        speciflcation_deputydishes.setAdapter(dishesAdapter);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.packge_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add){
            Dialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void Dialog(){
        View view = LinearLayout.inflate(this,R.layout.specification_dialog,null);
        speciflcation_maindishes = view.findViewById(R.id.dialog_speciflcation_maindishes);
        speciflcation_deputydishes = view.findViewById(R.id.dialog_speciflcation_deputydishes);
        final EditText speciflcation_multiple = view.findViewById(R.id.dialog_speciflcation_multiple);
        Button specification_ok = view.findViewById(R.id.dialog_speciflcation_ok);
        Button specification_cancel = view.findViewById(R.id.dialog_speciflcation_cancel);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setView(view);
        findHaveSupDishes();
        final AlertDialog alertDialog = dialog.create();
        specification_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (speciflcation_multiple.getText() == null&& speciflcation_multiple.getText().toString().equals("")){
                    Toast.makeText(SpecificationActivity.this,"倍数不能为空",Toast.LENGTH_LONG).show();
                    return;
                }
                if (speciflcation_maindishes.getText() == null&&speciflcation_maindishes.getText().toString().equals("")){
                    Toast.makeText(SpecificationActivity.this,"主菜品不能为空",Toast.LENGTH_LONG).show();
                    return;
                }
                if (speciflcation_deputydishes.getText() == null&&speciflcation_deputydishes.getText().toString().equals("")){
                    Toast.makeText(SpecificationActivity.this,"副菜品不能为空",Toast.LENGTH_LONG).show();
                    return;
                }
                setGuiGeDishes(speciflcation_maindishes.getText().toString());
                setGuiGeSupDishes(speciflcation_deputydishes.getText().toString(),speciflcation_multiple.getText().toString());
                findHaveSupDishes();
                alertDialog.dismiss();
            }
        });
        specification_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
