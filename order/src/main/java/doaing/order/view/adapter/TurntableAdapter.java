package doaing.order.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.couchbase.lite.Document;

import java.util.List;

import doaing.order.R;
import tools.CDBHelper;

public class TurntableAdapter extends RecyclerView.Adapter{
    private Context context;
    private List<Document> list;
    private OnClickListener listener;
    public TurntableAdapter(Context context, List<Document> list){
        this.context = context;
        this.list = list;
    }
    public void setOnClickListener(OnClickListener listener){
        this.listener = listener;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LinearLayout.inflate(context, R.layout.order_turntable_item,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        Document dishDoc = CDBHelper.getDocByID(list.get(position).getString("dishId"));
        viewHolder.name.setText(dishDoc.getString("name"));
        if (list.get(position).getString("tasteId") != null){
            Document tasteDoc = CDBHelper.getDocByID(list.get(position).getString("tasteId"));
            viewHolder.taste.setText(tasteDoc.getString("name"));
        }else{
            viewHolder.taste.setText("");
        }
        viewHolder.count.setText(list.get(position).getFloat("count")+"");
        viewHolder.price.setText(dishDoc.getFloat("price")+"");

        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                listener.onClick(position,b);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name,taste,count,price;
        public CheckBox checkBox;
        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_turntable_name);
            taste = itemView.findViewById(R.id.item_turntable_taste);
            count = itemView.findViewById(R.id.item_turntable_count);
            price = itemView.findViewById(R.id.item_turntable_price);
            checkBox = itemView.findViewById(R.id.item_turntable_check);
        }
    }
    public interface OnClickListener{
        void onClick(int p, boolean b);
    }
}
