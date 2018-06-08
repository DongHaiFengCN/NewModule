package doaing.order.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import doaing.order.R;
import tools.CDBHelper;


import java.util.ArrayList;
import java.util.List;

/**
 * @author donghaifeng
 */
public class AssessmentedItemRecyclerViewAdapter extends RecyclerView.Adapter<AssessmentedItemRecyclerViewAdapter.ViewHolder> {

    private List<String> mValues = new ArrayList<>();

    private Database database;
    private boolean[] flag;
    private int size;

    public boolean[] getFlag() {
        return flag;
    }

    public void setFlag(boolean[] flag) {
        this.flag = flag;
    }

    public AssessmentedItemRecyclerViewAdapter(Database database) {
        this.database = database;
        ResultSet results = null;
        Query query = QueryBuilder.select(SelectResult.expression(Meta.id))
                .from(DataSource.database(database)).where(Expression.property("className")
                        .equalTo(Expression.string("Dish"))
                        .and(Expression.property("sell").equalTo(Expression.booleanValue(true))));
        try {
            results = query.execute();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        Result row;
        while ((row = results.next()) != null) {
            String id = row.getString(0);
            mValues.add(id);
        }

        size = mValues.size();
        flag = new boolean[mValues.size()];
        for (int i = 0; i < size; i++) {
            flag[i] = true;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Document document = database.getDocument(mValues.get(position));
        holder.mIdView.setText(document.getString("name"));

        holder.mContentCk.setChecked(flag[position]);

        holder.mContentCk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                flag[position] = isChecked;

            }
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mIdView;
        CheckBox mContentCk;

        public ViewHolder(View view) {
            super(view);
            mIdView = view.findViewById(R.id.name_tv);
            mContentCk = view.findViewById(R.id.content_ck);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mIdView.getText() + "'";
        }
    }

    /**
     * check all unselected items
     */
    public void checkAll() {


        for (int i = 0; i < size; i++) {
            if (!flag[i]) {

                flag[i] = true;

            }
        }

        notifyDataSetChanged();

    }

    /**
     * check all items in reverse
     */
    public void invert() {


        for (int i = 0; i < size; i++) {

            if (flag[i]) {

                flag[i] = false;

            } else {

                flag[i] = true;
            }

        }

        notifyDataSetChanged();

    }

    /**
     * fragment save settings when detach
     */
    public void save()  {

        for (int i = 0; i < size; i++) {

            MutableDocument document = database.getDocument(mValues.get(i)).toMutable();

            if(flag[i]){

                document.setBoolean("sell",true);

            }else {

                document.setBoolean("sell",false);
            }

            try {

                database.save(document);

            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }

        }


    }
}
