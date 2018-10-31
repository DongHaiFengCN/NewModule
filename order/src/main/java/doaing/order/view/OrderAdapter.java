package doaing.order.view;

import android.os.Handler;
import android.text.TextUtils;
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
import tools.CDBHelper;
import tools.MyLog;

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/9/20 16:20
 * 修改人：donghaifeng
 * 修改时间：2017/9/20 16:20
 * 修改备注：*/



public class OrderAdapter extends BaseAdapter {

/**
     * SparseArray<Object> 中 0位置是菜品名字;1位置是菜品口味;2位置是菜品选择的数量;3单价}
     **/



    private List<Document> goodsCs;
    private MainActivity context;
    private int Price = 0;
    private Handler mHandler= new Handler();
    private setOnItemListener listener;

    public void setOnchangeListener(OnchangeListener onchangeListener) {
        this.onchangeListener = onchangeListener;
    }


    public void setListener(setOnItemListener listener){
        this.listener = listener;
    }

    private OnchangeListener onchangeListener;

    public OrderAdapter(){}

    public OrderAdapter(List<Document> goodsCs, MainActivity mainActivity) {
        this.goodsCs = goodsCs;
        this.context = mainActivity;
    }

    @Override
    public int getCount() {
        return goodsCs == null ? 0 : goodsCs.size();
    }

    @Override
    public Object getItem(int i) {

        return goodsCs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {


       final ViewHold  viewHold;

        if(view == null){

            //加载布局管理器
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.view_orderitem_lv,null);
            viewHold = new ViewHold();
            viewHold.name =view.findViewById(R.id.name);
            viewHold.taste =view.findViewById(R.id.taste);
            viewHold.price = view.findViewById(R.id.price);
            viewHold.number =view.findViewById(R.id.amount_view);
            viewHold.number.getEtAmount().setEnabled(false);
            viewHold.order_lin = view.findViewById(R.id.order_lin);
            view.setTag(viewHold);

        }else{

            viewHold = (ViewHold) view.getTag();
        }
        final Document document = CDBHelper.getDocByID(goodsCs.get(i).getString("dishId"));
        if (goodsCs.get(i).getInt("goodsType") == 0) {
            viewHold.name.setText(document.getString("name"));
        }else if (goodsCs.get(i).getInt("goodsType") == 1) {
            viewHold.name.setText(document.getString("name")+"(退)");
        }else if (goodsCs.get(i).getInt("goodsType") == 2) {
            viewHold.name.setText(document.getString("name")+"(赠)");
        }
        if (TextUtils.isEmpty(goodsCs.get(i).getString("tasteId"))){
            viewHold.taste.setText("");
        }else{
            Document tasteDoc = CDBHelper.getDocByID(goodsCs.get(i).getString("tasteId"));
            viewHold.taste.setText(tasteDoc.getString("name"));
        }

        viewHold.number.setNumber(goodsCs.get(i).getFloat("count")+"");
        MyLog.e("orderAdapter---"+goodsCs.get(i).getFloat("count"));
        viewHold.order_lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.setListener(i);
            }
        });
        //设置item的点击事件
        viewHold.number.setChangeListener(new AmountView.ChangeListener() {
            @Override
            public void OnChange(float ls,boolean flag)
            {
                MyLog.e("OnChange    数量="+ls);

                MutableDocument mutableDocument = CDBHelper.getDocByID(goodsCs.get(i).getId()).toMutable();
                MyLog.e("========"+mutableDocument.getId());
                if (mutableDocument.getInt("status") == 2) {
                    mutableDocument.setFloat("count", ls);
                    CDBHelper.saveDocument(mutableDocument);
                    onchangeListener.onchangeListener(flag, document.getFloat("price"), ls);

                    if (ls <= 0) {
                        mutableDocument.setFloat("count", 0);
                        CDBHelper.deleDocument(mutableDocument);
                        goodsCs.remove(i);
                        notifyDataSetChanged();
                    }
                    if (context.getGoodsList().size() == 0) {
                        if (context.getT9GoodsList() != null) {
                            context.getT9GoodsList().clear();
                            context.getSeekT9Adapter().notifyDataSetChanged();
                        }

                    }
                }
            }
        });

        return view;
    }


    class ViewHold{

        TextView name;

        TextView taste;

        TextView price;
        LinearLayout order_lin;

        AmountView number;


    }

   // flag ？+ ：-,price 单价 ,sum 当前item的商品的个数。
    interface OnchangeListener{

       void onchangeListener(boolean flag, float price, float sum);
    }

    interface setOnItemListener{
        void setListener(int pos);
    }


}
