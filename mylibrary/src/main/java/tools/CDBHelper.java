package tools;

/*
 * Copyright (c) 2016 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.couchbase.lite.BasicAuthenticator;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Document;
import com.couchbase.lite.Endpoint;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Function;
import com.couchbase.lite.Meta;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Ordering;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Replicator;
import com.couchbase.lite.ReplicatorChange;
import com.couchbase.lite.ReplicatorChangeListener;
import com.couchbase.lite.ReplicatorConfiguration;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.couchbase.lite.URLEndpoint;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.couchbase.lite.LogDomain;
//import com.couchbase.lite.LogLevel;


public class CDBHelper
{
    private static Database db;
    private static CDBHelper instance = null;
    private static String dbName="kitchendb";
    private final static String mSyncGatewayEndpoint = "ws://123.207.174.171:4984/kitchen/";
    //private final static String mSyncGatewayEndpoint = "wss://www.yaodiandian.net:4984/kitchen/";
    //private final static String mSyncGatewayEndpoint = "wss://123.207.174.171:4984/kitchen/";
    private static boolean firstReplicator = true;
    private static Context mcontext;
    public static CDBHelper getSharedInstance(Context context)
    {
        if (instance == null) {
            instance = new CDBHelper(context);
            mcontext = context;
        }
        return instance;
    }

    public static String getDbName() {
        return dbName;
    }

    public static void setDbName(String dbName) {
        CDBHelper.dbName = dbName;
    }


    protected CDBHelper(Context context)
    {

        DatabaseConfiguration config = new DatabaseConfiguration(context);
//        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
//        {
//            File folder = new File(String.format("%s/SmartKitchen", Environment.getExternalStorageDirectory()));
//            config.setDirectory(folder.getAbsolutePath());
//        }
        try {
            db = new Database(dbName, config);
        } catch (CouchbaseLiteException e)
        {
            MyLog.e("CDBHelper","dbinit exception="+e.toString());
            e.printStackTrace();
        }

    }
    public static void deleteDB(){
        try{
            db.delete();
            db=null;
        }
        catch (CouchbaseLiteException e){
            e.printStackTrace();
        }


    }
    public static void reCreateDB(){

        DatabaseConfiguration config = new DatabaseConfiguration(mcontext);
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            File folder = new File(String.format("%s/SmartKitchen", Environment.getExternalStorageDirectory()));
            config.setDirectory(folder.getAbsolutePath());
        }


        MyLog.e("Environment.getExternalStorageState()=",Environment.getExternalStorageState()+"getExternalStorageDirectory= ="+Environment.getExternalStorageDirectory());

        try {
            db = new Database(dbName, config);
        } catch (CouchbaseLiteException e)
        {
            MyLog.e("CDBHelper","dbinit exception="+e.toString());
            e.printStackTrace();
        }
    }

    private static byte[] getPinnedCertFile(Context context) {
        AssetManager assetManager = context.getAssets();
        InputStream is = null;
        byte[] bytes = new byte[0];
        try {
            is = assetManager.open("cert.cer");
            return (IOUtils.toByteArray(is));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
    public static void startPushAndPullReplicationForCurrentUser(String username, String password) {


        if(db==null)
        {

            return;
        }

        //   db.setLogLevel( LogDomain.REPLICATOR,  LogLevel.ERROR);

        URI url = null;
        try {
            url = new URI(mSyncGatewayEndpoint);
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
        Endpoint endpoint = new URLEndpoint(url);
        ReplicatorConfiguration config = new ReplicatorConfiguration(db, endpoint)
                .setReplicatorType(ReplicatorConfiguration.ReplicatorType.PUSH_AND_PULL)
                .setContinuous(true);

        List<String> channels =new ArrayList<>();
        channels.add(username);
        config.setChannels(channels);

        config.setReplicatorType(ReplicatorConfiguration.ReplicatorType.PUSH_AND_PULL);
        config.setContinuous(true);
        config.setAuthenticator(new BasicAuthenticator(username, password));



      //  byte[] pinnedServerCert = getPinnedCertFile(mcontext);
        // Set pinned certificate.
        // config.setPinnedServerCertificate(pinnedServerCert);

        Replicator replicator = new Replicator(config);
        replicator.addChangeListener(new ReplicatorChangeListener() {
            @Override
            public void changed(ReplicatorChange change)
            {
                Replicator.ActivityLevel level = change.getReplicator().getStatus().getActivityLevel();
                MyLog.e("replicatior status","**********level**********="+level);
                Intent intent = new Intent();
                switch (level){
                    case STOPPED:
                        break;
                    case OFFLINE:
                        intent.setAction("sync_complete");
                        mcontext.sendBroadcast(intent);
                        break;
                    case CONNECTING:
                        break;
                    case IDLE:
                        intent.setAction("sync_complete");
                        mcontext.sendBroadcast(intent);
                        break;
                    case BUSY:
                        break;
                    default:
                        break;
                }


            }
        });
        replicator.start();
    }



    public static Database getDatabase() {
        if (instance == null)
        {
        }
        return db;
    }

    public static void setDatabase() {

        db =null;
    }
    // --------------------------------------------------
    // ReplicatorChangeListener implementation
    // --------------------------------------------------

    //*************************对象操作********************************************************

    /**
     * 1.1
     * 接口描述：数据存储或更新
     *
     *
     * @param object  需要保存或更新的类对象，这地方是object,不是document,目的兼容一些界面中原来的类对象生成保存
     *                返回：生成或更新docment的id号
     */
    public static String createAndUpdate(Object object) {
    //1\
   String tmp =  JSON.toJSONString(object);
    //String tmp =  JSON.toJSONString(object,SerializerFeature.WriteNullNumberAsZero, SerializerFeature.NotWriteDefaultValue);

    Map props = (Map) JSON.parse(tmp);
    String id = (String) props.get("id");
    MutableDocument mDocument;

    if (TextUtils.isEmpty(id)) {
        MyLog.e("save obj","obj="+tmp);
        id = props.get("className") + "." + java.util.UUID.randomUUID().toString();
        mDocument = new MutableDocument(id);
    } else {
        mDocument = db.getDocument(id).toMutable();
        MyLog.e("update obj","obj="+tmp);
    }
    try {

        mDocument.setData(props);
        db.save(mDocument);

    } catch (CouchbaseLiteException e) {
        e.printStackTrace();
    }
    return id;
}

    public static String createAndUpdateDefalut(Object object) {
        //1\
        String tmp =  JSON.toJSONString(object);
        MyLog.e("save obj","obj="+tmp);
        Map props = (Map) JSON.parse(tmp);
        String id = (String) props.get("id");
        MutableDocument mDocument;

        if (TextUtils.isEmpty(id)) {
            id = props.get("className") + "." + java.util.UUID.randomUUID().toString();
            mDocument = new MutableDocument(id);
        } else {
            mDocument = db.getDocument(id).toMutable();
        }
        try {

            mDocument.setData(props);

            db.save(mDocument);

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return id;
    }

    /**
     * 1.2 通过id 查寻doc 转成对象
     *
     *
     * @param id
     * @param aClass
     * @param <T>
     * @return
     */
    public static <T> T getObjById(String id, Class<T> aClass) {

        Document document = db.getDocument(id);
        if (document == null) {
            return null;
        }

//        ObjectMapper m = new ObjectMapper();
//        m.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        m.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);

        Map<String, Object> map = document.toMap();
        map.put("id", id);
        String json=JSON.toJSONString(map);
        return JSON.parseObject(json, aClass);

        //return m.convertValue(map, aClass);
    }

    /**
     * 1.3
     *
     *
     * @param aClass
     * @param <E>
     * @return
     */
    public static <E> List<E> getObjByClass(Class<E> aClass) {
        final String classname = aClass.getSimpleName();
        List<E> objList = new ArrayList<>();

        if (classname == null || classname.equals(""))
            return null;
        // 1


        Query query = QueryBuilder.select(SelectResult.all(), SelectResult.expression(Meta.id))
                .from(DataSource.database(db))
                .where(Expression.property("className").equalTo(Expression.string(classname)));
        try {
            ResultSet resultSet = query.execute();
            Result row;
            while ((row = resultSet.next()) != null)
            {
                Map<String, Object> map;
                Dictionary valueMap = row.getDictionary(db.getName());
                // Convert from dictionary to corresponding University object
                map = valueMap.toMap();
                map.put("id", row.getString("id"));

                String json=JSON.toJSONString(map);
                E obj= JSON.parseObject(json, aClass);

                objList.add(obj);
            }

        } catch (CouchbaseLiteException e) {
            Log.e("getDocmentsByClass", "Exception=", e);
        }
        return objList;
    }



    /**
     * 1.4
     *
     *
     * @param where
     * @param orderBy
     * @param aClass
     * @param <T>
     * @return
     */
    public static <T> List<T> getObjByWhere(Expression where, Ordering orderBy, Class<T> aClass) {
        // 1
        List<T> documentList = new ArrayList<>();

        Query query;
        if (where == null){
            return null;
        }
        if (orderBy == null){
            query = QueryBuilder.select(SelectResult.all(), SelectResult.expression(Meta.id)).from(DataSource.database(db)).where(where);
        }
        else {
            query = QueryBuilder.select(SelectResult.all(), SelectResult.expression(Meta.id)).from(DataSource.database(db)).where(where).orderBy(orderBy);
        }


        try {
            ResultSet resultSet = query.execute();
            Result row;
            while ((row = resultSet.next()) != null) {
                Map<String, Object> map;
                Dictionary valueMap = row.getDictionary(db.getName());
                map = valueMap.toMap();
                map.put("id", row.getString("id"));

                String json=JSON.toJSONString(map);
                T obj= JSON.parseObject(json, aClass);

                documentList.add(obj);
            }
        } catch (CouchbaseLiteException e) {
            Log.e("getDocmentsByClass", "Exception=", e);
        }

        return documentList;
    }

    /**
     * 1.5
     *
     *
     * @param object
     * @return
     */
    public static boolean deleteObj( Object object) {

        String tmp =  JSON.toJSONString(object, SerializerFeature.NotWriteDefaultValue);
        Map props = (Map) JSON.parse(tmp);
        String id = (String) props.get("id");
        if (id == null || "".equals(id)) {
            return false;
        } else {
            Document document = db.getDocument(id);
            try {
                db.delete(document);
                return true;

            } catch (CouchbaseLiteException e) {
                Log.e("deleDocument", "Exception=", e);
            }
            return false;
        }

    }

//**************************************************docment操作**************************************
//
    //*************************************************************************************************

    /**
     * 2.1
     *
     *
     * @param document
     */
    public static void saveDocument( MutableDocument document) {

        try {

            db.save(document);
        } catch (CouchbaseLiteException e) {
            Log.e("saveDocument", "Exception=", e);
        }

    }

    /**
     * 2.2
     *
     * @param id
     * @return
     */
    public static Document getDocByID(String id) {

        return db.getDocument(id);
    }


    /**
     * 2.3
     * 接口描述：通过类名称查询所有类对象（其实是Document)
     *
     * @param aClass  class类
     * @return 返回 Document序列
     */
    public static final List<Document> getDocmentsByClass(Class<?> aClass) {
        final String classname = aClass.getSimpleName();
        List<Document> documentList = new ArrayList<>();

        if (classname == null || classname.equals(""))
            return null;
        Query query = QueryBuilder.select(SelectResult.expression(Meta.id))
                .from(DataSource.database(db))
                .where(Expression.property("className").equalTo(Expression.string(classname)));
        try {
            ResultSet resultSet = query.execute();
            Result row;
            while ((row = resultSet.next()) != null) {
                String id = row.getString(0);
                Document doc = db.getDocument(id);
                documentList.add(doc);
            }
        } catch (CouchbaseLiteException e) {
            Log.e("getDocmentsByClass", "Exception=", e);
        }
        return documentList;
    }




    /**
     * 2.4
     *
     * @param where
     * @param orderBy
     * @return
     */
    public static List<Document> getDocmentsByWhere(Expression where, Ordering orderBy) {
        // 1
        List<Document> documentList = new ArrayList<>();

        Query query;
        if (where == null)
            return null;

        if (orderBy == null)
            query = QueryBuilder.select(SelectResult.expression(Meta.id)).from(DataSource.database(db)).where(where);
        else
            query = QueryBuilder.select(SelectResult.expression(Meta.id)).from(DataSource.database(db)).where(where).orderBy(orderBy);

        try {
            ResultSet resultSet = query.execute();
            Result row;
            while ((row = resultSet.next()) != null) {
                String id = row.getString(0);
                Document doc = db.getDocument(id);

                documentList.add(doc);
            }
        } catch (CouchbaseLiteException e) {
            Log.e("getDocmentsByWhere", "Exception=", e);
        }

        return documentList;
    }


    /**
     * 2.5
     *
     * @param document
     */
    public static void deleDocument( Document document) {

        try {
            db.delete(document);
        } catch (CouchbaseLiteException e) {
            Log.e("deleDocument", "Exception=", e);
        }


    }

    /**
     * 2.6
     *
     * @param id
     */
    public static void deleDocumentById( String id) {

        try {
            Document document = db.getDocument(id);
            db.delete(document);
        } catch (CouchbaseLiteException e) {
            Log.e("deleDocument", "Exception=", e);
        }

    }
/*************************************************3.id操作*******************************************
 *
 *
 **************************************************************************************************/
    /**
     * 3.1
     * 接口描述：通过类名称查询所有类对象（其实是Document)
     *
     * @param aClass  class类
     * @return 返回 Document序列
     */
    public static List<String> getIdsByClass(Class<?> aClass) {
        final String classname = aClass.getSimpleName();
        List<String> documentList = new ArrayList<>();

        if (classname == null || classname.equals(""))
            return null;
        // 1
        Query query = QueryBuilder.select(SelectResult.expression(Meta.id))
                .from(DataSource.database(db))
                .where(Expression.property("className").equalTo(Expression.string(classname)));
        try {
            ResultSet resultSet = query.execute();
            Result row;
            while ((row = resultSet.next()) != null)
            {
                String id = row.getString(0);
                documentList.add(id);
            }
        } catch (CouchbaseLiteException e) {
            Log.e("getDocmentsByClass", "Exception=", e);
        }
        return documentList;
    }


    /**
     * 3.2
     * 接口描述：通过查询条件查询数据库中符合条件的Document
     *
     * @param
     * @return
     */
    public static List<String> getIdsByWhere(Expression where, Ordering orderBy) {
        // 1
        List<String> documentList = new ArrayList<>();

        //2
        Query query;
        if (where == null)
            return null;

        if (orderBy == null)
            query = QueryBuilder.select(SelectResult.expression(Meta.id)).from(DataSource.database(db)).where(where);
        else
            query = QueryBuilder.select(SelectResult.expression(Meta.id)).from(DataSource.database(db)).where(where).orderBy(orderBy);

        try {
            ResultSet resultSet = query.execute();
            Result row;
            while ((row = resultSet.next()) != null)
            {

                String id = row.getString(0);
                documentList.add(id);

            }
        } catch (CouchbaseLiteException e) {
            Log.e("getDocmentsByClass", "Exception=", e);
        }

        return documentList;
    }
/**************************************************4.其它操作******************************************
 *
 *
 ***************************************************************************************************/
    /**
     * 4.1
     * 接口描述：通过查询条件查询数据库中符合条件的Document
     *
     * @param
     * @return
     */
    public static List<Map<String, String>> getGoodsByGroup(String startTime, String endTime) {
        // 1
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();


        //2
        Query query = QueryBuilder.select(SelectResult.expression(Function.sum(Expression.property("dishesCount"))),
                SelectResult.expression(Expression.property("dishesName")))
                .from(DataSource.database(db))
                .where(Expression.property("className").equalTo(Expression.string("GoodsC"))
                        .and(Expression.property("createdTime").lessThanOrEqualTo(Expression.string(endTime)))
                        .and(Expression.property("createdTime").greaterThanOrEqualTo(Expression.string(startTime))))
                .groupBy(Expression.property("dishesName"))
                .orderBy(Ordering.expression(Function.sum(Expression.property("dishesCount"))).descending());

        try {
            ResultSet resultSet = query.execute();
            Result row;


            while ((row = resultSet.next()) != null) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("菜名", row.getString("dishesName"));
                map.put("数量", "" + row.getInt(0));

                list.add(map);

            }
        } catch (CouchbaseLiteException e) {
            Log.e("getDocmentsByWhere", "Exception=", e);
        }

        return list;
    }

    /**
     * 4.2
     *
     * @param document
     */
    public static void purge( Document document) {


        try {
            db.purge(document);
        } catch (CouchbaseLiteException e) {
            Log.e("purge", "Exception=", e);
        }


    }

    /**
     * 4.3
     *
     * @param document
     * @param aClass
     * @param <T>
     * @return
     */
    public static <T> T modelForDocument(Document document, Class<T> aClass) {
        Map<String, Object> map = document.toMap();

        String json=JSON.toJSONString(map);
        return JSON.parseObject(json, aClass);


    }

}
