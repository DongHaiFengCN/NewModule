package doaing.order.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.Ordering;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.QueryChange;
import com.couchbase.lite.QueryChangeListener;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import doaing.order.R;


/**
 * Class description goes here.
 * <p>
 * Created by loongsun on 2017/5/28.
 * <p>
 * email: 125736964@qq.com
 */
public class LiveTableRecyclerAdapter extends RecyclerView.Adapter<LiveTableRecyclerAdapter.TestHolderView> {

    private Database db;
    private Query listsLiveQuery = null;
    protected Context context;
   // private List<String> documentList;
    private List<HashMap<String,Object>> hashMapList;

    private onRecyclerViewItemClickListener itemClickListener = null;

    public LiveTableRecyclerAdapter(Context context, final Database db, String areaId)
    {
        if(db == null) throw new IllegalArgumentException();
        this.db = db;
        this.context = context;
        this.listsLiveQuery = listsLiveQuery(areaId);
        this.listsLiveQuery.addChangeListener(new QueryChangeListener() {
            @Override
            public void changed(QueryChange change)
            {
                clear();
                ResultSet rs = change.getResults();
                Result row;
                while ((row = rs.next()) != null)
                {
                    HashMap map = new HashMap();
                    map.put("id",row.getString(0));
                    map.put("state",row.getInt(1));
                    map.put("name",row.getString(2));
                    map.put("maxPersons",row.getInt("maxPersons"));
                    map.put("currentPersions", row.getInt("currentPersions"));
                    hashMapList.add(map);
                    //documentList.add(row.getString(0));
                   Log.e("","live change table name="+row.getString(2));
                }
                notifyDataSetChanged();

            }
        });
    }
    private Query listsLiveQuery(String areaId)
    {
        return QueryBuilder.select(SelectResult.expression(Meta.id),
                SelectResult.expression(Expression.property("state")),
                SelectResult.expression(Expression.property("tableName")),
                SelectResult.expression(Expression.property("maxPersons")),
                SelectResult.expression(Expression.property("currentPersions")))
                .from(DataSource.database(db))
                .where(Expression.property("className").equalTo(Expression.string("TableC"))
                        .and(Expression.property("areaId").equalTo(Expression.string(areaId))))
                .orderBy(Ordering.property("tableNum").ascending());
    }
    @Override
    public TestHolderView onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_table, parent, false);

        view.setOnClickListener(new View.OnClickListener()
        {   //为每一个item绑定监听
            @Override
            public void onClick(View v)
            {
                // TODO 自动生成的方法存根
                if (itemClickListener != null){
                itemClickListener.onItemClick(v,v.getTag());}
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {

                if (itemClickListener != null){
                    itemClickListener.onItemLongClick(v,v.getTag());
               }
               return true;

            }
        });

        TestHolderView testHolderView = new TestHolderView(view);
        view.setTag("1");
        return testHolderView;
    }

    private void clear()
    {
     if(hashMapList==null)
         hashMapList=new ArrayList<HashMap<String, Object>>();
        hashMapList.clear();
    }
    @Override
    public void onBindViewHolder(TestHolderView holder, int position)
    {

        HashMap map = hashMapList.get(position);
        String docId=map.get("id").toString();
        int state = (int)map.get("state");
        String name = map.get("name").toString();
        int maxPersons,currentPersions;
        maxPersons = (int)map.get("maxPersons");
        currentPersions = (int)map.get("currentPersions");
        switch (state)
        {
            case 0:
                holder.cardView.setCardBackgroundColor(Color.rgb(86,209,109));
                break;
            case 1:
                holder.cardView.setCardBackgroundColor(Color.rgb(255,193,17));
                break;
            case  2:
                holder.cardView.setCardBackgroundColor(Color.rgb(253,117,80));
                break;
            default:
                break;
        }
        holder.tv.setText(name);
        holder.itemView.setTag(docId);
        if (state == 0){
            if (maxPersons == 0){
                holder.num.setText("");
            }else {
                holder.num.setText(""+maxPersons);
            }
        }else {
            if (maxPersons == 0){
                if (currentPersions ==0){
                    holder.num.setText("");
                }else {
                    holder.num.setText(""+currentPersions);
                }
            }else {
                if (currentPersions == 0){
                    holder.num.setText(""+maxPersons);
                }else {
                    holder.num.setText(""+currentPersions + "/" + maxPersons);
                }
            }
        }


    }

    @Override
    public int getItemCount()
    {
        return hashMapList != null ? hashMapList.size() : 0;
    }
    public class TestHolderView extends RecyclerView.ViewHolder
    {
        protected ImageView img;
        protected TextView tv;
        protected CardView cardView;
        private  TextView num;

        public TestHolderView(View itemView)
        {
            super(itemView);
            tv = itemView.findViewById(R.id.item_tablestate_name);
            cardView= itemView.findViewById(R.id.table_state_cardview);
            num = itemView.findViewById(R.id.item_tablestate_num);
        }
    }

    public void setOnItemClickListener(onRecyclerViewItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public  interface onRecyclerViewItemClickListener {

        void onItemClick(View v, Object tag);
        void onItemLongClick(View v, Object tag);
    }


    public void StopQuery()
    {
        if (listsLiveQuery != null) {
            listsLiveQuery = null;
        }
    }
}
