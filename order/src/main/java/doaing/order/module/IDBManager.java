package doaing.order.module;

import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/9/8 14:30
 * 修改人：donghaifeng
 * 修改时间：2017/9/8 14:30
 * 修改备注：
 */

public interface IDBManager<T> {



    boolean isLogin(String name, String pass);



    void save(Document document) throws CouchbaseLiteException;

    <T> T getById(String id);

    Document getMembers(String tel);
    Document getCard(String id);


    void setContext(Context context);






}
