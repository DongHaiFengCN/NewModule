/*
package doaing.order.view.adapter;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import doaing.order.R;
import doaing.order.view.PayActivity;

*/
/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/11/1 10:54
 * 修改人：donghaifeng
 * 修改时间：2017/11/1 10:54
 * 修改备注：
 *//*


public class MemberDishesListAdapter extends BaseAdapter {
    private List list;
    PayActivity payActivity;
    public MemberDishesListAdapter(List list, PayActivity payActivity)
    {

        super();
        this.list = list;
        this.payActivity = payActivity;
    }

    @Override
    public int getCount() {

        return list.isEmpty()?0:list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHold viewHold = null;

        if(view == null){

            //加载布局管理器
            LayoutInflater inflater = LayoutInflater.from(payActivity);
            view = inflater.inflate(R.layout.view_payactivity_memberdishes_sale_dialog_adapteritem,null);
            viewHold = new ViewHold();
            viewHold.dishesname =view.findViewById(R.id.dishesname_tv);
            viewHold.dishestotalprice =view.findViewById(R.id.dishestotalprice_tv);
            viewHold.saleprice = view.findViewById(R.id.saleprice_tv);
            viewHold.issale = view.findViewById(R.id.issale_tv);

            view.setTag(viewHold);

        }else{

            viewHold = (ViewHold) view.getTag();
        }

        SparseArray sparseArray = (SparseArray) list.get(i);
        viewHold.dishesname.setText(sparseArray.get(1).toString());
        viewHold.dishestotalprice.setText(sparseArray.get(2).toString());

        //折扣价格
        if((float)sparseArray.get(3) == 0f){
            viewHold.saleprice.setText("--");
        }else {

            viewHold.saleprice.setText(sparseArray.get(3)+"");
        }



        if((boolean)sparseArray.get(4)){

            viewHold.issale.setVisibility(View.VISIBLE);
        }


        return view;
    }

    class ViewHold{

        TextView dishesname;

        TextView dishestotalprice;

        TextView saleprice;


        ImageView issale;


    }
}
*/
