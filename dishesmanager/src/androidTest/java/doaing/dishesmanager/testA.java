package doaing.dishesmanager;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tools.MyLog;

/**
 * 项目名称：new
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2018/1/20 9:14
 * 修改人：donghaifeng
 * 修改时间：2018/1/20 9:14
 * 修改备注：
 */

public class testA {
    Retrofit sRetrofit;

    String url = "http://123.207.174.171:3000/dishes/";
    @Before
    public void start(){

    }
    @Test
    public void set() {

        MyLog.e(Environment.getExternalStorageDirectory().getPath());

        Log.v("Upload", Environment.getExternalStorageDirectory().getPath());
    }

    @Test
    public void test(){
        sRetrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FileUploadService apiManager = sRetrofit.create(FileUploadService.class);
        PayInfo payInfo = new PayInfo();
        payInfo.setAuth_code("1111111111");
        payInfo.setOut_trade_no("22222222222");
        payInfo.setSeller_id("33333333333");
        Call<ResponseBody> call =  apiManager.postPay(payInfo);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    Log.v("DOAING1",response.body().string());
                    Log.v("DOAING2",response.toString());
                    Log.v("DOAING3",response.message());
                    Log.v("DOAING4",response.isSuccessful()+"");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.v("DOAING5",t.getMessage());
            }
        });

    }

    @Test
    public void add(){
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(url)
                .build();


        FileAddService service = retrofit.create(FileAddService.class);
        File file = new File("/storage/emulated/0/p/cccc.jpg");
      //访问手机端的文件资源，保证手机端sdcdrd中必须有这个文件

        Log.e("DOAING",file.exists()+"");
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part body = MultipartBody.Part.createFormData("aFile", file.getName(), requestFile);

        String descriptionString = "This is a description";
        RequestBody description =
                RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);

        Call<ResponseBody> call = service.upload(description, body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.v("DOAING",response.isSuccessful()+"");

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.v("DOAING", t.getMessage());

            }
        });

/*
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                Log.v("DOAING",response.isSuccessful()+"");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.v("DOAING", t.getMessage());
            }
        });*/

    }


}
