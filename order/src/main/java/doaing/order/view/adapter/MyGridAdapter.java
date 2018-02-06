package doaing.order.view.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import doaing.order.R;
/*
*
 * Created by lenovo on 2017/11/9.
*/



public class MyGridAdapter extends RecyclerView.Adapter{
    private int index = 0;
    private Activity activity;
    private List<String> tasteList;

    public MyGridAdapter(Activity activity, List<String> tasteList){
        this.activity = activity;
        this.tasteList = tasteList;
    }

    private OnItemOlickListener mOnItemOlickListener = null;


    public interface OnItemOlickListener {

        void onItemClick(int position);
    }

    public void setmOnItemOlickListener(OnItemOlickListener listener) {
        this.mOnItemOlickListener = listener;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HolderView holderView;
        View convertView = LayoutInflater.from(activity).inflate(R.layout.item_dialog_girdview, null);
        holderView = new HolderView(convertView);
        return holderView;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final HolderView holderView = (HolderView) holder;
        holderView.itemRcTv.setText(tasteList.get(position));
        holderView.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                index = holderView.getLayoutPosition();
                notifyDataSetChanged();
                holderView.itemView.setTag(position);
                if (mOnItemOlickListener != null) {
                    mOnItemOlickListener.onItemClick((int) v.getTag());
                }
            }
        });
        if (position == index) {
            holderView.itemRcCk.setChecked(true);
        } else {
            holderView.itemRcCk.setChecked(false);
        }
    }


    @Override
    public int getItemCount() {
        return tasteList == null ? 0 : tasteList.size();
    }


    class HolderView extends RecyclerView.ViewHolder {

        TextView itemRcTv;
        CheckBox itemRcCk;
        public HolderView(View itemView) {
            super(itemView);
            itemRcTv = itemView.findViewById(R.id.item_rc_tv);
            itemRcCk = itemView.findViewById(R.id.item_rc_ck);
        }
    }
}
