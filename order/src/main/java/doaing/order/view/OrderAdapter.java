package doaing.order.view;

import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.List;

import bean.kitchenmanage.order.GoodsC;
import doaing.order.R;
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



    private List<GoodsC> goodsCs;
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

    public OrderAdapter(List<GoodsC> goodsCs, MainActivity mainActivity) {
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

        viewHold.name.setText(goodsCs.get(i).getDishesName());
        if (TextUtils.isEmpty(goodsCs.get(i).getDishesTaste())){
            viewHold.taste.setText("");
        }else{
            viewHold.taste.setText(goodsCs.get(i).getDishesTaste());
        }

        viewHold.number.setNumber(goodsCs.get(i).getDishesCount()+"");

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
                goodsCs.get(i).setDishesCount(ls);
                onchangeListener.onchangeListener(flag, goodsCs.get(i).getPrice(),ls);
                if (context.getGoodsList().size()==0 ){
                    context.getT9GoodsList().clear();
                    context.getSeekT9Adapter().notifyDataSetChanged();
                }
                if(ls <= 0){

                    goodsCs.get(i).setDishesCount(0);
                    goodsCs.remove(i);
                    notifyDataSetChanged();
                }
            }
        });

//        }else{
//           // final float price = goodsCs.get(i).getAllPrice();
//            //设置item的点击事件
//            viewHold.number.setChangeListener(new AmountView.ChangeListener() {
//                @Override
//                public void OnChange(float ls,boolean flag) {
//
//                    goodsCs.get(i).setDishesCount(ls);
//                   // goodsCs.get(i).setAllPrice(MyBigDecimal.mul(ls,price,2));
//
//                    onchangeListener.onchangeListener(flag, goodsCs.get(i).getPrice() ,ls);
//
//
//                    if(ls <= 0){
//
//                        goodsCs.get(i).setDishesCount(0);
//                        goodsCs.remove(i);
//                        notifyDataSetChanged();
//
//                    }
//
//
//
//                }
//            });
//
//        }
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
