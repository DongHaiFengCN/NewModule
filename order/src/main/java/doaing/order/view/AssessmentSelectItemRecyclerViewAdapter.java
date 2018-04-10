package doaing.order.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.MutableDocument;

import java.util.List;

import doaing.order.R;


/**
 * @author donghaifeng
 */
public class AssessmentSelectItemRecyclerViewAdapter extends RecyclerView.Adapter<AssessmentSelectItemRecyclerViewAdapter.ViewHolder> {

    private List<String> mDocumentList;

    private Database database;
    private Context context;
    public void setContext(Context context) {
        this.context = context;
    }
    public void setDatabase(Database database) {
        this.database = database;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final com.couchbase.lite.Document mDocument = database.getDocument(mDocumentList.get(position));
        holder.mItem = mDocument.getId();
        holder.mIdView.setText(mDocument.getString("name"));
        final boolean sell = mDocument.getBoolean("sell");
        if (!sell) {
            holder.mContentView.setText("正常");
        } else if (sell) {
            holder.mContentView.setText("已估清");
        }
        holder.mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    MutableDocument mutableDocument = mDocument.toMutable();
                    if (!sell) {
                        mutableDocument.setBoolean("sell", true);
                        try {
                            database.save(mutableDocument);
                            notifyDataSetChanged();
                        } catch (CouchbaseLiteException e) {
                            e.printStackTrace();
                        }
                    }else if(sell){

                        Toast.makeText(context,"已经估清",Toast.LENGTH_SHORT).show();
                    }

                }
        });
    }

    @Override
    public int getItemCount() {
        return mDocumentList == null ? 0 : mDocumentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
         final TextView mIdView;
         final Button mContentView;
         String mItem;

        public ViewHolder(View view) {
            super(view);
            mIdView = view.findViewById(R.id.id);
            mContentView = view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

    }

    /**
     * @param mDocumentList data
     */
    void setDocumentsList(List<String> mDocumentList) {

        this.mDocumentList = mDocumentList;

        notifyDataSetChanged();

    }
}
