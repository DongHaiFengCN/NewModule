
package doaing.dishesmanager;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.couchbase.lite.Array;
import com.couchbase.lite.Blob;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.jakewharton.rxbinding.view.RxView;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import doaing.dishesmanager.widget.DishesKindSpinner;
import doaing.dishesmanager.widget.TasteSelectAdapter;
import doaing.mylibrary.MyApplication;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.functions.Action1;
import tools.CDBHelper;
import tools.ToolUtil;
import view.BaseToobarActivity;


/**
 * @author donghaifeng
 */

public class
DisheEditActivity extends BaseToobarActivity {

    String url = "https://www.yaodiandian.net/dishes/";
    private static final int THUMBNAIL_SIZE = 150;
    private List<Document> tasteList;
    private List<Document> tasteAllList;
    private TasteSelectAdapter tasteSelectAdapter;
    private int kindPosition;
    private String[] strings;
    private String newFileUrl;
    private Bitmap bitmap;
    @BindView(R2.id.dishe_im)
    ImageView disheIm;
    @BindView(R2.id.dishe_name)
    EditText disheName;
    @BindView(R2.id.dishe_price_et)
    EditText dishePriceEt;
    @BindView(R2.id.reyview)
    RecyclerView reyview;
    @BindView(R2.id.taste_im_bt)
    ImageView tasteImBt;
    @BindView(R2.id.disheKind_sp)
    DishesKindSpinner disheKindSp;
    @BindView(R2.id.dishe_submit_bt)
    Button disheSubmitBt;
    private Database database;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    Document document;
    private Document newKind;
    private Document oldKind;

    @Override
    protected int setMyContentView() {
        return R.layout.activity_dishe_edit;
    }

    @Override
    public void initData(Intent intent) {

        setToolbarName("菜品编辑");
        database = CDBHelper.getDatabase();
        document = database.getDocument(intent.getStringExtra("dishes"));
        kindPosition = intent.getIntExtra("position", 0);

        initTasteSpinnerDate();
        attachData(document);

        RxView.clicks(disheIm).throttleFirst(300, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);

            }
        });
        disheKindSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int p, long id) {

                kindPosition = p;
                //添加菜品到选择的菜类菜品集合下
                newKind = disheKindSp.getDishesKindList().get(p);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tasteImBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(DisheEditActivity.this);
                builder.setMultiChoiceItems(strings, new boolean[strings.length], new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        if (isChecked) {

                            tasteList.add(tasteAllList.get(which));

                        } else {

                            tasteList.remove(tasteAllList.get(which));

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

        //提交菜品所有信息
        RxView.clicks(disheSubmitBt).throttleFirst(300, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {

                String dishesName = disheName.getText().toString();

                MutableDocument disheMuDoc = document.toMutable();

                if ("".equals(dishesName)) {

                    disheName.setError("菜品名称不能为空");
                    return;

                } else if (!dishesName.equals(document.getString("dishesName"))) {

                    disheMuDoc.setString("dishesName", dishesName);
                    String dishesNameCode9 = ToolUtil.ChangeSZ(ToolUtil.getFirstSpell(dishesName));
                    disheMuDoc.setString("dishesNameCode9", dishesNameCode9);

                    String dishesNameCode26 = ToolUtil.getFirstSpell(dishesName);
                    disheMuDoc.setString("dishesNameCode26", dishesNameCode26);

                }
                String price = dishePriceEt.getText().toString();

                if ("".equals(price)) {


                    dishePriceEt.setError("价格不能为空");

                    return;

                } else if (!price.equals(document.getFloat("price") + "")) {

                    disheMuDoc.setFloat("price", Float.valueOf(price));

                }

                //附加图片到Docment，允许图片为空
                disheMuDoc = attachImage(disheMuDoc, bitmap);
                disheMuDoc.setString("dishesKindId", disheKindSp.getDishesKindList().get(disheKindSp.getSelectedItemPosition()).getId());

                //更新添加口味
                MutableArray array = new MutableArray();
                for (int i = 0; i < tasteList.size(); i++) {

                    array.addString(tasteList.get(i).getId());
                }
                disheMuDoc.setArray("tasteList", array);

                //选择加入新的菜品中
                MutableDocument newMukindDoc = newKind.toMutable();
                if (!newKind.getId().equals(oldKind.getId())) {

                    //1.删除oldkind list中的dishesId 并保存
                    removeDisheIdFromDishesKindList();

                    //2.dishes的 kindId 重新覆盖成newDocument的id
                    disheMuDoc.setString("dishesKindId", newKind.getId());

                    //3.dishes id 加入新的newDocument list中


                    newMukindDoc.getArray("dishesListId").addString(document.getId());

                    //4.设置排列数
                    disheMuDoc.setInt("orderId", newKind.getArray("dishesListId").count());

                }

                try {

                    CDBHelper.getDatabase().save(disheMuDoc);
                    CDBHelper.getDatabase().save(newMukindDoc);
                    EventBus.getDefault().postSticky(kindPosition);

                    finish();

                } catch (CouchbaseLiteException e) {

                    e.printStackTrace();
                }

                //更新静态资源图片
                if (newFileUrl != null && !newFileUrl.isEmpty()) {
                    upDataPicture(new File(newFileUrl));
                }
            }
        });

    }


    /**
     * 上传图片静态资源
     *
     * @param file
     */

    public boolean upDataPicture(File file) {

        final boolean[] flag = {false};
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(url)
                .build();

        FileAddService service = retrofit.create(FileAddService.class);
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("application/otcet-stream"), file);

        MultipartBody.Part body = MultipartBody.Part.createFormData("aFile", file.getName(), requestFile);

        String descriptionString = document.getId();
        RequestBody description =
                RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);
        Call<ResponseBody> call = service.upload(description, body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                flag[0] = response.isSuccessful();

                try {
                    android.util.Log.e("DOAING", response.body().string());

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                android.util.Log.e("DOAING", t.getMessage());
            }
        });

        return flag[0];
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            Uri uri = data.getData();
            ContentResolver cr = this.getContentResolver();
            try {

                //获取文件的绝对路径以及文件的名字
                String name = getRealPathFromURI(uri);
                newFileUrl = name;

                String a[] = name.split("/");
                name = a[a.length - 1].toString();
                int idx = name.indexOf(".");
                name = name.substring(0, idx);

                //显示图片名字
                disheName.setText(name);
                //显示图片
                Glide.with(this).load(uri).into(disheIm);

                //获取附加到Document的数据
                bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));


            } catch (FileNotFoundException e) {
                android.util.Log.e("Exception", e.getMessage(), e);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * document附加图片上去
     */

    private MutableDocument attachImage(MutableDocument task, Bitmap image) {

        if (image != null) {
            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(image, THUMBNAIL_SIZE, THUMBNAIL_SIZE);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 70, out);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            Blob blob = new Blob("image/jpg", in);
            task.setBlob("image", blob);

        }
        return task;
    }


    /**
     * 获取图片的绝对路径
     *
     * @param contentUri
     * @return
     */


    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(DisheEditActivity.this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    /**
     * 初始化所有口味的集合
     */

    private void initTasteSpinnerDate() {


        ResultSet results = null;
        tasteAllList = new ArrayList<>();
        Query query = QueryBuilder.select(SelectResult.expression(Meta.id))
                .from(DataSource.database(database))
                .where(Expression.property("className").equalTo(Expression.string("DishesTasteC")));
        try {
            results = query.execute();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        Result row = null;
        while ((row = results.next()) != null) {

            String id = row.getString(0);
            Document doc = database.getDocument(id);
            if (doc != null) {
                tasteAllList.add(doc);
            }

        }

        strings = new String[tasteAllList.size()];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = tasteAllList.get(i).getString("tasteName");
        }

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


        //得到当前菜品所属菜类
        oldKind = database.getDocument(document.getString("dishesKindId"));

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
        Array array;
        array = document.getArray("tasteList");
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
        if (array != null) {

            MutableArray mutableArray = array.toMutable();
            Iterator<Object> iterator = mutableArray.iterator();

            while (iterator.hasNext()) {

                String o = (String) iterator.next();
                Document document = database.getDocument(o);
                if (document != null) {
                    tasteList.add(document);
                }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toobar_delet, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == R.id.action_delet) {

            new AlertDialog.Builder(this)
                    .setMessage("确定删除当前菜品吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //移除菜类下菜品的关联
                    removeDisheIdFromDishesKindList();

                    try {

                        database.save(oldKind.toMutable());
                        database.delete(document);
                        //删除服务器静态资源
                        deletePicturesFromServer();
                        EventBus.getDefault().postSticky(kindPosition);
                        finish();

                    } catch (CouchbaseLiteException e) {
                        e.printStackTrace();
                    }
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();


        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * 从服务器删除静态图片资源
     */

    private void deletePicturesFromServer() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(url)
                .build();
        FileDeletService service = retrofit.create(FileDeletService.class);

        Call call = service.delet(document.getId());

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()) {

                    Toast.makeText(DisheEditActivity.this, "图片删除成功！", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(DisheEditActivity.this, "图片删除失败！", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {

                Toast.makeText(DisheEditActivity.this, "请求访问失败！", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void removeDisheIdFromDishesKindList() {

        MutableDocument mutableDocument = oldKind.toMutable();
        MutableArray oldArry = mutableDocument.getArray("dishesListId");

        for (int i = 0; i < oldArry.count(); i++) {


            if (oldArry.getString(i).equals(document.getId())) {

                oldArry.remove(i);

                try {
                    database.save(mutableDocument);
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }
                break;

            }
        }
    }
}

