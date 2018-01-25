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
import android.util.Log;
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
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.LiveQueryChange;
import com.couchbase.lite.LiveQueryChangeListener;
import com.couchbase.lite.Query;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.jakewharton.rxbinding.view.RxView;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import doaing.dishesmanager.widget.DishesKindSpinner;
import doaing.dishesmanager.widget.TasteSelectAdapter;
import module.MyApplication;
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
import tools.ToolUtil;
import view.BaseToobarActivity;

public class DisheAddActivity extends BaseToobarActivity {

    private static final int THUMBNAIL_SIZE = 150;
    private Bitmap bitmap;
    private Document kind;
    private List<Document> list;
    String url = "http://123.207.174.171:3000/dishes/";
    private String newFileUrl;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.dishe_name)
    EditText disheName;
    @BindView(R.id.dishe_price_et)
    EditText dishePriceEt;
    @BindView(R.id.disheKind_sp)
    DishesKindSpinner disheKindSp;
    @BindView(R.id.dishe_submit_bt)
    Button disheSubmitBt;
    @BindView(R.id.dishe_im)
    ImageView disheIm;
    @BindView(R.id.taste_im_bt)
    ImageView tasteImBt;
    Document document;
    String[] strings;
    private int position = 0;

    @Override
    protected int setMyContentView() {

        return R.layout.activity_dishe_add;
    }

    @Override
    public void initData(Intent intent) {


        document = new Document();
        //初始化口味
        initTasteData();


        disheKindSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int p, long id) {

                position = p;
                //添加菜品到选择的菜类菜品集合下
                kind = disheKindSp.getDishesKindList().get(position);
                kind.getArray("dishesListId").addString(document.getId());

                //添加菜类id到菜品中
                document.setString("dishesKindId", kind.getId());
                //默认依次添加到队尾
                document.setInt("orderId", kind.getArray("dishesListId").count());


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        RxView.clicks(disheIm).throttleFirst(300, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);

            }
        });


        //提交菜品所有信息
        RxView.clicks(disheSubmitBt).throttleFirst(300, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {

                document.setString("channelId", ((MyApplication) getApplication()).getCompany_ID());
                document.setString("className", "DishesC");

                if (disheName.getText().toString().equals("")) {

                    disheName.setError("菜品名称不能为空");

                    return;

                } else {

                    String dishesName = disheName.getText().toString();
                    Log.e("DOAING", dishesName);
                    document.setString("dishesName", dishesName);

                    String dishesNameCode9 = ToolUtil.ChangeSZ(dishesName);
                    Log.e("DOAING", dishesNameCode9);
                    document.setString("dishesNameCode9", dishesNameCode9);


                }
                if (dishePriceEt.getText().toString().equals("")) {

                    dishePriceEt.setError("价格不能为空");

                    return;

                } else {


                    document.setFloat("price", Float.valueOf(dishePriceEt.getText().toString()));

                }
                //判断菜类是否存在
                if (disheKindSp.getDishesKindList().isEmpty()) {

                    Toast.makeText(DisheAddActivity.this, "请先添加菜类信息！", Toast.LENGTH_LONG).show();

                    return;
                }

                //附加图片到Docment，允许图片为空
                document = attachImage(document, bitmap);
                document.setString("dishesKindId", disheKindSp.getDishesKindList().get(disheKindSp.getSelectedItemPosition()).getId());
                //添加口味
                Array array = new Array();
                if (list.size() > 0) {

                    for (int i = 0; i < list.size(); i++) {

                        array.addString(list.get(i).getId());
                    }

                }
                document.setArray("tasteList", array);
                try {

                    ((MyApplication) getApplication()).getDatabase().save(document);
                    ((MyApplication) getApplication()).getDatabase().save(kind);

                    EventBus.getDefault().postSticky(new Integer(position));

                    finish();

                } catch (CouchbaseLiteException e) {

                    e.printStackTrace();
                }

                //提交静态图片

                if (newFileUrl != null && !newFileUrl.isEmpty()) {
                    upDataPicture(new File(newFileUrl));
                }


            }
        });

    }

    /**
     * 加载口味选择器
     */
    private void initTasteData() {

        list = new ArrayList<>();

        //初始化适配器
        final TasteSelectAdapter oap = new TasteSelectAdapter(list, getApplicationContext());
        final Database database = ((MyApplication) getApplicationContext()).getDatabase();

        final List<Document> tasteList = new ArrayList<>();
        LiveQuery query = Query.select(SelectResult.expression(Expression.meta().getId()))
                .from(DataSource.database(database))
                .where(Expression.property("className").equalTo("DishesTasteC")).toLive();

        query.addChangeListener(new LiveQueryChangeListener() {
            @Override
            public void changed(LiveQueryChange change) {

                if (!tasteList.isEmpty()) {
                    tasteList.clear();
                }
                ResultSet rows = change.getRows();
                Result row = null;
                while ((row = rows.next()) != null) {

                    String id = row.getString(0);
                    Document doc = database.getDocument(id);
                    tasteList.add(doc);

                }

                strings = new String[tasteList.size()];
                for (int i = 0; i < strings.length; i++) {
                    strings[i] = tasteList.get(i).getString("tasteName");
                }
            }
        });
        query.run();

        tasteImBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(DisheAddActivity.this);
                builder.setMultiChoiceItems(strings, new boolean[strings.length], new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            list.add(tasteList.get(which));

                        } else {
                            list.remove(which);

                        }
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        oap.notifyDataSetChanged();

                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
            }
        });


        RecyclerView recyclerView = findViewById(R.id.reyview);

        //LinearLayoutMannager 是一个布局排列 ， 管理的接口,子类都都需要按照接口的规范来实现。
        LinearLayoutManager ms = new LinearLayoutManager(this);

        // 设置 recyclerview 布局方式为横向布局
        ms.setOrientation(LinearLayoutManager.HORIZONTAL);

        //给RecyClerView 添加设置好的布局样式
        recyclerView.setLayoutManager(ms);

        //对 recyclerview 添加数据内容
        recyclerView.setAdapter(oap);
    }

    @Override
    protected Toolbar setToolBarInfo() {
        return toolbar;
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
                // disheName.setCursorVisible(false);
                //显示图片
                Glide.with(this).load(uri).into(disheIm);

                //获取附加到Document的数据
                bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));


            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(), e);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * document附加图片上去
     */
    private Document attachImage(Document task, Bitmap image) {

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
        CursorLoader loader = new CursorLoader(DisheAddActivity.this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
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

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

        return flag[0];
    }

}
