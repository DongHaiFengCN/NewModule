package doaing.order.view.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.couchbase.lite.Document;
import com.couchbase.lite.MutableDocument;

import java.util.List;

import bean.kitchenmanage.order.Goods;
import doaing.order.R;
import doaing.order.untils.MyBigDecimal;
import tools.CDBHelper;

/*
*
 * Created by lenovo on 2017/12/13.
*/


public class ShowParticularsAdapter extends BaseAdapter {

    private Activity activity;
    private List<MutableDocument> goodsCs;
    public ShowParticularsAdapter(Activity activity, List<MutableDocument> goodsCs)
    {
        this.activity = activity;
        this.goodsCs = goodsCs;

    }

    @Override
    public int getCount() {

        return goodsCs == null ? 0 : goodsCs.size();
    }

    @Override
    public Object getItem(int position) {
        return goodsCs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_show_particulars,null);
            viewHolder = new ViewHolder();
            viewHolder.dj = convertView.findViewById(R.id.item_show_dj);
            viewHolder.mc = convertView.findViewById(R.id.item_show_mc);
            viewHolder.sl = convertView.findViewById(R.id.item_show_sl);
            viewHolder.kw = convertView.findViewById(R.id.item_show_kw);
            viewHolder.item_show_lin = convertView.findViewById(R.id.item_show_lin);


            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(goodsCs.get(position).getInt("goodsType")==1)//退菜
        {

            viewHolder.mc.setTextColor(Color.parseColor("#fd7550"));
            viewHolder.kw.setTextColor(Color.parseColor("#fd7550"));
            viewHolder.dj.setTextColor(Color.parseColor("#fd7550"));
            viewHolder.sl.setTextColor(Color.parseColor("#fd7550"));
        }
        else if (goodsCs.get(position).getInt("goodsType")==2)//赠菜
        {
            viewHolder.mc.setTextColor(Color.parseColor("#56d16d"));
            viewHolder.kw.setTextColor(Color.parseColor("#56d16d"));
            viewHolder.dj.setTextColor(Color.parseColor("#56d16d"));
            viewHolder.sl.setTextColor(Color.parseColor("#56d16d"));
        }
        else
        {
            viewHolder.mc.setTextColor(Color.parseColor("#3a3a3a"));
            viewHolder.kw.setTextColor(Color.parseColor("#3a3a3a"));
            viewHolder.dj.setTextColor(Color.parseColor("#3a3a3a"));
            viewHolder.sl.setTextColor(Color.parseColor("#3a3a3a"));
        }
        Document dishDoc = CDBHelper.getDocByID(goodsCs.get(position).getString("dishId"));
        if (goodsCs.get(position).getInt("goodsType") == 0) {
            viewHolder.mc.setText(dishDoc.getString("name"));
        }else if (goodsCs.get(position).getInt("goodsType") == 1) {
            viewHolder.mc.setText(dishDoc.getString("name")+"(退)");
        }else if (goodsCs.get(position).getInt("goodsType") == 2) {
            viewHolder.mc.setText(dishDoc.getString("name")+"(赠)");
        }
        if(goodsCs.get(position).getString("tasteId") != null){
            Document tasteDoc = CDBHelper.getDocByID(goodsCs.get(position).getString("tasteId"));
            viewHolder.kw.setText(tasteDoc.getString("name"));
        }else{
            viewHolder.kw.setText("");
        }
        viewHolder.dj.setText(""+ MyBigDecimal.mul(dishDoc.getFloat("price"),goodsCs.get(position).getFloat("count"),1));

        viewHolder.sl.setText(goodsCs.get(position).getFloat("count")+"");

        return convertView;
    }

    class ViewHolder{
        private TextView mc,dj,sl,kw;
        private LinearLayout item_show_lin;

    }
}
