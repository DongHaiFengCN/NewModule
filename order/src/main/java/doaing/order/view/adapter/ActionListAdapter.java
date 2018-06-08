package doaing.order.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;


import java.util.List;

import bean.kitchenmanage.promotion.Promotion;
import bean.kitchenmanage.promotion.PromotionRule;
import doaing.order.R;
import doaing.order.view.PayActivity;
import tools.CDBHelper;

/*
*
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/11/1 10:54
 * 修改人：donghaifeng
 * 修改时间：2017/11/1 10:54
 * 修改备注：
*/



public class ActionListAdapter extends BaseAdapter {

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    ListView listView;
    private List list;
    PayActivity payActivity;
    int[] flag;

    public ActionListAdapter(List list, PayActivity payActivity) {

        super();
        this.list = list;
        this.payActivity = payActivity;
        flag = new int[list.size()];
        // flag  = new int[20];
    }

    @Override
    public int getCount() {

        return list.isEmpty() ? 0 : list.size();
        // return 20;
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
    public View getView(final int i, View view, ViewGroup viewGroup) {

        ViewHold viewHold = null;

        if (view == null) {

            //加载布局管理器
            LayoutInflater inflater = LayoutInflater.from(payActivity);
            view = inflater.inflate(R.layout.view_payactivity_action_dialog_adapteritem, null);
            viewHold = new ViewHold();
            viewHold.actionName = view.findViewById(R.id.actionName_tv);
            viewHold.actionTime = view.findViewById(R.id.actionTime_tv);
            viewHold.actionType = view.findViewById(R.id.actionType_tv);
            viewHold.actionIsCheck_ck = view.findViewById(R.id.actionIsCheck_ck);

            view.setTag(viewHold);

        } else {

            viewHold = (ViewHold) view.getTag();
        }
        Promotion promotion = (Promotion) list.get(i);
        viewHold.actionName.setText(promotion.getName());
        viewHold.actionTime.setText(promotion.getStartTime() + "    /     " + promotion.getEndTime());
        for (int p = 0 ; p < promotion.getPromotionRuleList().size();p++) {
            PromotionRule promotionRules = promotion.getPromotionRuleList().get(p);
            if (promotionRules.getMode() == 1) {

                viewHold.actionType.setText("打折");

            } else if (promotionRules.getMode() == 2) {

                viewHold.actionType.setText("满赠");

            }else if (promotionRules.getMode() == 3){

                viewHold.actionType.setText("满减");
            }
        }

        viewHold.actionIsCheck_ck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                callback.click(i,isChecked);
            }
        });

        return view;
    }

    public int[] getFlag() {

        return flag;
    }

    class ViewHold {

        TextView actionName;

        TextView actionTime;

        TextView actionType;

        CheckBox actionIsCheck_ck;


    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    Callback callback;

    public interface Callback {
        public void click(int p,boolean flag);
    }
}
