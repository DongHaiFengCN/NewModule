package doaing.dishesmanager.widget;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.couchbase.lite.Document;

import java.util.List;

import doaing.dishesmanager.R;

/**
 * 项目名称：MyApplication2
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2018/1/23 16:06
 * 修改人：donghaifeng
 * 修改时间：2018/1/23 16:06
 * 修改备注：
 * @author donghaifeng
 */


public class TasteSelectAdapter extends RecyclerView.Adapter<TasteSelectAdapter.ViewHolder> {

    public List<Document> getList() {
        return list;
    }

    private List<Document> list;
    private LayoutInflater mInflater;
    public TasteSelectAdapter(List<Document> list, Context context){

        mInflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        @SuppressLint("InflateParams") View view = mInflater.inflate(R.layout.layout,
                null, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.mImg =  view
                .findViewById(R.id.img);

        viewHolder.mTxt =  view
                .findViewById(R.id.mTxt);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.mTxt.setText(list.get(position).getString("tasteName"));
        holder.mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(position);
                notifyDataSetChanged();

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        ViewHolder(View arg0)
        {
            super(arg0);
        }

        ImageView mImg;
        TextView mTxt;
    }
}

