package doaing.dishesmanager;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * 项目名称：new
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2018/1/22 14:56
 * 修改人：donghaifeng
 * 修改时间：2018/1/22 14:56
 * 修改备注：
 */

public interface FileAddService {
    @Multipart
    @POST("img")
    Call<ResponseBody> upload(@Part("description") RequestBody description,
                              @Part MultipartBody.Part file);

}
