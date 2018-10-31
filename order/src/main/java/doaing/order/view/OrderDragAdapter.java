package doaing.order.view;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.couchbase.lite.Document;


import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.List;

import bean.kitchenmanage.dish.Dish;
import bean.kitchenmanage.dish.Taste;
import doaing.order.R;
import doaing.order.module.DishesMessage;
import doaing.order.untils.MyBigDecimal;
import tools.CDBHelper;
import tools.MyLog;

/*
*
 * Created by lenovo on 2017/10/30.
*/


public class OrderDragAdapter extends BaseAdapter {

    private List<Document> mlistDishes;
    private Context context;

    public float[] getNumbers() {
        return numbers;
    }

    public void setList(List<String> tastetList) {
        this.tastetList = tastetList;
    }

    private List<String> tastetList;

    //维护数量数组
    private float[] numbers;

    ListView listview;

    public void setMessage(List<Document> mlistDishes, float[] numbers) {

        this.mlistDishes = mlistDishes;

        this.numbers = numbers;
        notifyDataSetChanged();
    }

    ChangerNumbersListener changerNumbersListener;

    public void setTouchListener(SubtractionTouchListener touchListener) {
        this.touchListener = touchListener;
    }

    SubtractionTouchListener touchListener;

    public void setChangerNumbersListener(ChangerNumbersListener changerNumbersListener) {
        this.changerNumbersListener = changerNumbersListener;
    }


    public void setListview(ListView listview) {
        this.listview = listview;
    }


    public OrderDragAdapter(Context context) {
        this.context = context;

    }


    @Override
    public int getCount() {
        return mlistDishes == null ? 0 : mlistDishes.size();
    }

    @Override
    public Object getItem(int position) {
        return mlistDishes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final HolderView view;
        if (convertView == null) {
            view = new HolderView();
            convertView = LayoutInflater.from(context).inflate(R.layout.view_item_recl, parent, false);
            view.name = convertView.findViewById(R.id.item_info);
            view.price = convertView.findViewById(R.id.price_tv);
            view.addtion = convertView.findViewById(R.id.addtion_iv);
            view.number = convertView.findViewById(R.id.view_shu);
            view.substruct = convertView.findViewById(R.id.substruct_iv);
            convertView.setTag(view);
        } else {

            view = (HolderView) convertView.getTag();
        }

        if (mlistDishes.get(position).getBoolean("sell")){
            view.addtion.setVisibility(View.INVISIBLE);
            view.name.setText(mlistDishes.get(position).getString("name")+"(估清)");
        }else{
            view.addtion.setVisibility(View.VISIBLE);
            view.name.setText(mlistDishes.get(position).getString("name"));
        }
        // 当数量不为零，且关闭状态，打开减号与数量；当数量为零，处于开启状态则关闭。
        if (numbers[position] != 0.0f && view.substruct.getVisibility() == View.INVISIBLE
                && view.number.getVisibility() == View.INVISIBLE) {

            view.substruct.setVisibility(View.VISIBLE);

            view.number.setVisibility(View.VISIBLE);

        } else if (numbers[position] == 0.0f && view.substruct.getVisibility() == View.VISIBLE
                && view.number.getVisibility() == View.VISIBLE) {

            view.substruct.setVisibility(View.INVISIBLE);

            view.number.setVisibility(View.INVISIBLE);

        }


        //设置数量
        view.number.setText(MyBigDecimal.add(numbers[position],0,2) + "");
//        final String count = view.number.getText().toString();
//        if (count.equals("0.0")){
//            view.number.setVisibility(View.GONE);
//        }else{
//            view.number.setVisibility(View.VISIBLE);
//
//        }
        //加法指示器
        view.addtion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mlistDishes.get(position).getBoolean("sell")){
                    return;
                }
                Document dishes = CDBHelper.getDocByID( mlistDishes.get(position).getId());
                MyLog.e("dishId---"+dishes.getId());
                setMessage(dishes, true, position);

            }
        });

        //减法指示器
        view.substruct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Document dishes = CDBHelper.getDocByID( mlistDishes.get(position).getId());

                setMessage(dishes, false, position);

            }
        });

        view.price.setText(""+mlistDishes.get(position).getFloat("price"));


        return convertView;
    }

    /**
     * 初始化需要发送的数据，并发送到 MainActivity
     * @param dishesC 获取的菜品实体类
     * @param flag    true+  false
     * @return 是否发送成功
     */


    private void setMessage(final Document dishesC, final boolean flag, final int position) {
        final DishesMessage dishesMessage = new DishesMessage();

        dishesMessage.setDishKindId(dishesC.getString("kindId"));

        dishesMessage.setSingle(true);

        dishesMessage.setDishesC(dishesC);

        dishesMessage.setName(dishesC.getString("name"));

        //判断菜品引用的口味id在数据库中存在实例

        //有口味，添加选择口味dialog
        if (dishesC.getArray("tasteIds") != null && dishesC.getArray("tasteIds").count() > 0) {

            //初始化一个缓存口味的数组
            final String[] strings = new String[dishesC.getArray("tasteIds").count()];

            for (int i = 0; i < dishesC.getArray("tasteIds").count(); i++) {

                Document dishesTasteC = CDBHelper.getDocByID( dishesC.getArray("tasteIds").getString(i));

                if (dishesTasteC != null) {

                    strings[i] = dishesTasteC.getString("name");

                }

            }

            //确认数据库口味不为空
            if (strings[0] != null) {
                dishesMessage.setDishesTaste(strings[0]);
                if (dishesC.getArray("tasteIds").count() == 1){
                    Refresh(flag, dishesMessage);
                }else {

                    tasteSelectDialog(flag, position, dishesMessage, strings);
                }


            }else {
                Refresh(flag, dishesMessage);
            }

        }else {

            Refresh(flag, dishesMessage);
        }



    }

    private void tasteSelectDialog(final boolean flag, final int position, final DishesMessage dishesMessage, final String[] strings) {
        new AlertDialog.Builder(context).setTitle("全部口味")


                .setSingleChoiceItems(strings, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //得到口味

                        dishesMessage.setDishesTaste(strings[i]);

                    }
                })
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Refresh(flag, dishesMessage);


                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }


    private void Refresh(boolean flag, DishesMessage dishesMessage) {

        dishesMessage.setOperation(flag);

        EventBus.getDefault().postSticky(dishesMessage);

        changerNumbersListener.getNumber(numbers);
    }


    void updata(int position, float count) {

        numbers[position] += count;
        notifyDataSetChanged();


    }

    class HolderView {

        TextView name;
        TextView price;
        TextView number;
        ImageView addtion;
        ImageView substruct;

    }

    interface ChangerNumbersListener {

        void getNumber(float[] numbers);

    }

    interface SubtractionTouchListener {

        void setSubtractionTouchListener(String id);


    }
}
