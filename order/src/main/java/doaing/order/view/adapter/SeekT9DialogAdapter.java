/*
package doaing.order.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import doaing.order.R;

*/
/**
 * Created by lenovo on 2017/11/8.
 *//*


public class SeekT9DialogAdapter extends RecyclerView.Adapter {

    Context context;
    List<String> list = new ArrayList<>();

    private int index = -1;

    private OnItemOlickListener mOnItemOlickListener = null;


    public interface OnItemOlickListener {

        void onItemClick(int position);
    }

    public void setmOnItemOlickListener(OnItemOlickListener listener) {
        this.mOnItemOlickListener = listener;
    }

    public SeekT9DialogAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HolderView view = new HolderView(LayoutInflater.from(context).inflate(R.layout.item_t9_dialog, parent, false));
        return view;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final HolderView holderView = (HolderView) holder;

        holderView.itemTvCaipin.setText(list.get(position));
//
//        holder.itemView.setTag(position);
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
            holderView.itemCkCaipin.setChecked(true);
        } else {
            holderView.itemCkCaipin.setChecked(false);
        }


    }

    public int getIndex() {
        return index;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }


    public class HolderView extends RecyclerView.ViewHolder {
        @BindView(R.id.item_tv_caipin)
        TextView itemTvCaipin;
        @BindView(R.id.item_ck_caipin)
        CheckBox itemCkCaipin;


        public HolderView(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
*/
