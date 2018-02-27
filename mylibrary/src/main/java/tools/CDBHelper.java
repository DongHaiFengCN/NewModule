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
import android.os.Environment;
import android.util.Log;

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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CDBHelper implements ReplicatorChangeListener
{
    private static Database db;
    private static CDBHelper instance = null;
    private static String dbName="GuestDB";
    private final static String mSyncGatewayEndpoint = "ws://123.207.174.171:4984/kitchen/";

    public static CDBHelper getSharedInstance(Context context)
    {
        if (instance == null) {
            instance = new CDBHelper(context);
        }
        return instance;
    }

    protected CDBHelper(Context context)
    {

            DatabaseConfiguration config = new DatabaseConfiguration(context);
            File folder = new File(String.format("%s/ReserverApp", Environment.getExternalStorageDirectory()));
            config.setDirectory(folder.getAbsolutePath());
            try {
                db = new Database(dbName, config);
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }

    }
    public static void startPushAndPullReplicationForCurrentUser(String username, String password) {

        Log.e("startReplication","channelId="+username+"-----pwd="+password);

        URI url = null;
        try {
            url = new URI(mSyncGatewayEndpoint);
        } catch (URISyntaxException e) {
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

        Replicator replicator = new Replicator(config);
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
    @Override
    public void changed(ReplicatorChange change) {

        if (change.getStatus().getError() != null && change.getStatus().getError().getCode() == 401)
        {
           //Toast.makeText(getApplicationContext(), "Authentication Error: Your username or password is not correct.", Toast.LENGTH_LONG).show();
          //  logout();
        }
    }
    //*************************对象操作********************************************************

    /**
     * 1.1
     * 接口描述：数据存储或更新
     *
     * @param context 程序上下文，获取数据库单实例
     * @param object  需要保存或更新的类对象，这地方是object,不是document,目的兼容一些界面中原来的类对象生成保存
     *                返回：生成或更新docment的id号
     */
    public static String createAndUpdate(Context context, Object object) {
        //1\
        ObjectMapper m = new ObjectMapper();
        Map<String, Object> props = m.convertValue(object, Map.class);
        String id = (String) props.get("_id");
        MutableDocument mDocument;
        if (id == null || "".equals(id)) {
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
     * @param context
     * @param id
     * @param aClass
     * @param <T>
     * @return
     */
    public static <T> T getObjById(Context context, String id, Class<T> aClass) {

        Document document = db.getDocument(id);
        if (document == null) {
            // TODO: error handling
            return null;
        }

        ObjectMapper m = new ObjectMapper();
        m.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        m.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);

        Map<String, Object> map = document.toMap();
        map.put("_id", id);

        return m.convertValue(map, aClass);
    }

    /**
     * 1.3
     *
     * @param context
     * @param aClass
     * @param <E>
     * @return
     */
    public static <E> List<E> getObjByClass(Context context, Class<E> aClass) {
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

                ObjectMapper objectMapper = new ObjectMapper();
                // Ignore undeclared properties
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                Map<String, Object> map;
                Dictionary valueMap = row.getDictionary(db.getName());
                // Convert from dictionary to corresponding University object
                map = valueMap.toMap();
                map.put("_id", row.getString("id"));
                E obj = objectMapper.convertValue(map, aClass);
                objList.add(obj);

            }

        } catch (CouchbaseLiteException e) {
            Log.e("getDocmentsByClass", "Exception=", e);
        }
        return objList;
    }

    /**
     * @return 时间格式 yyyy-MM-dd HH:mm:ss
     */
    public static String getFormatDate() {
        Date date = new Date();
        if (date != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return formatter.format(date);
        }

        return null;
    }

    /**
     * 1.4
     *
     * @param context
     * @param where
     * @param orderBy
     * @param aClass
     * @param <T>
     * @return
     */
    public static <T> List<T> getObjByWhere(Context context, Expression where, Ordering orderBy, Class<T> aClass) {
        // 1
        List<T> documentList = new ArrayList<>();

        Query query;
        if (where == null)
            return null;

        if (orderBy == null)
            query = QueryBuilder.select(SelectResult.all(), SelectResult.expression(Meta.id)).from(DataSource.database(db)).where(where);
        else
            query = QueryBuilder.select(SelectResult.all(), SelectResult.expression(Meta.id)).from(DataSource.database(db)).where(where).orderBy(orderBy);


        try {
            ResultSet resultSet = query.execute();
            Result row;
            while ((row = resultSet.next()) != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                // Ignore undeclared properties
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                Map<String, Object> map;
                Dictionary valueMap = row.getDictionary(db.getName());
                // Convert from dictionary to corresponding University object
                map = valueMap.toMap();
                map.put("_id", row.getString("id"));
                T obj = objectMapper.convertValue(map, aClass);
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
     * @param context
     * @param obj
     * @return
     */
    public static boolean deleteObj(Context context, Object obj) {

        ObjectMapper m = new ObjectMapper();
        Map<String, Object> props = m.convertValue(obj, Map.class);
        String id = (String) props.get("_id");
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
     * @param context
     * @param document
     */
    public static void saveDocument(Context context, MutableDocument document) {

        try {
            db.save(document);
        } catch (CouchbaseLiteException e) {
            Log.e("saveDocument", "Exception=", e);
        }

    }

    /**
     * 2.2
     *
     * @param context
     * @param id
     * @return
     */
    public static Document getDocByID(Context context, String id) {

        return db.getDocument(id);
    }


    /**
     * 2.3
     * 接口描述：通过类名称查询所有类对象（其实是Document)
     *
     * @param context 设备上下文，用于数据库单实例
     * @param aClass  class类
     * @return 返回 Document序列
     */
    public static final List<Document> getDocmentsByClass(Context context, Class<?> aClass) {
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
     * @param context
     * @param where
     * @param orderBy
     * @return
     */
    public static List<Document> getDocmentsByWhere(Context context, Expression where, Ordering orderBy) {
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
     * @param context
     * @param document
     */
    public static void deleDocument(Context context, Document document) {

        try {
            db.delete(document);
        } catch (CouchbaseLiteException e) {
            Log.e("deleDocument", "Exception=", e);
        }


    }

    /**
     * 2.6
     *
     * @param context
     * @param id
     */
    public static void deleDocumentById(Context context, String id) {

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
     * @param context 设备上下文，用于数据库单实例
     * @param aClass  class类
     * @return 返回 Document序列
     */
    public static List<String> getIdsByClass(Context context, Class<?> aClass) {
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
     * @param context 程序上下文，获取数据库单实例
     * @param
     * @return
     */
    public static List<String> getIdsByWhere(Context context, Expression where, Ordering orderBy) {
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
                Log.e("getid", "id---->" + id);
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
     * @param context 程序上下文，获取数据库单实例
     * @param
     * @return
     */
    public static List<Map<String, String>> getGoodsByGroup(Context context, String startTime, String endTime) {
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
     * @param context
     * @param document
     */
    public static void purge(Context context, Document document) {


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
        ObjectMapper m = new ObjectMapper();
        m.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return m.convertValue(document.toMap(), aClass);
    }

}
