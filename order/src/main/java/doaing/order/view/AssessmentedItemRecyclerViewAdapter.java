package doaing.order.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.couchbase.lite.Database;
import com.couchbase.lite.Document;

import doaing.order.R;
import tools.CDBHelper;


import java.util.List;

/**
 * @author donghaifeng
 */
public class AssessmentedItemRecyclerViewAdapter extends RecyclerView.Adapter<AssessmentedItemRecyclerViewAdapter.ViewHolder> {

    private List<String> mValues = null;

    private Database database;
    private Context context;

    private boolean[] checkedStates;
    public void setContext(Context context) {
        this.context = context;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public AssessmentedItemRecyclerViewAdapter(List<String> mValues) {
        this.mValues =mValues;
         checkedStates = new boolean[mValues.size()];
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Document document = database.getDocument(mValues.get(position));
        holder.mIdView.setText(document.getString("dishesName"));
        if(!checkedStates[position]){
            holder.mContentCk.setChecked(true);
        }

    }

    @Override
    public int getItemCount() {
        return mValues == null ? 0 : mValues.size();
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

}
