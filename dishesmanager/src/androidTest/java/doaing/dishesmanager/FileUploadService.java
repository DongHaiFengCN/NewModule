package doaing.dishesmanager;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * 项目名称：new
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2018/1/22 9:19
 * 修改人：donghaifeng
 * 修改时间：2018/1/22 9:19
 * 修改备注：
 */

public interface FileUploadService {


    @Multipart
    @POST("img")
    Call<ResponseBody> uploadImage(@Part MultipartBody.Part photo,@Part("description") RequestBody requestBody);


    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("imgs")
        //@Body修饰符自动转换bean为json字符串
    Call<ResponseBody> postPay(@Body PayInfo  payInfo);
}
