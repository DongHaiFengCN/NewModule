package doaing.order.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        holder.mIdView.setText(mDocument.getString("dishesName"));
        final int state = mDocument.getInt("state");
        if (state == 0) {
            holder.mContentView.setText("正常");
        } else if (state == 1) {
            holder.mContentView.setText("已估清");
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    MutableDocument mutableDocument = mDocument.toMutable();
                    if (state == 0) {
                        mutableDocument.setInt("state", 1);
                        try {
                            database.save(mutableDocument);
                            notifyDataSetChanged();
                        } catch (CouchbaseLiteException e) {
                            e.printStackTrace();
                        }
                    }else if(state == 1){

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
         final View mView;
         final TextView mIdView;
         final TextView mContentView;
         String mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
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
