package doaing.dishesmanager;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.HEAD;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * 项目名称：new
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2018/1/22 14:56
 * 修改人：donghaifeng
 * 修改时间：2018/1/22 14:56
 * 修改备注：
 */

public interface FileDeletService {
    @Headers({"Content-Type: x-www-form-urlencoded"})//需要添加头
    @DELETE("img")
    Call<ResponseBody> delet(@Query("dishesId") String id);

}
