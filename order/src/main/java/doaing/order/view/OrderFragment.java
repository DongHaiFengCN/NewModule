package doaing.order.view;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.MutableDocument;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bean.kitchenmanage.dish.Dish;
import bean.kitchenmanage.dish.DishesKind;
import bean.kitchenmanage.order.Goods;
import doaing.order.R;
import doaing.order.module.DishesMessage;
import doaing.order.untils.MyBigDecimal;
import doaing.order.view.adapter.MyGridAdapter;
import tools.CDBHelper;
import tools.MyLog;

/*
*
 * Created by lenovo on 2017/10/26.
*/


public class OrderFragment extends Fragment {

    ListView dishesRv;
    ListView orderList;
    private DishesKindAdapter leftAdapter;
    private OrderClassifyAdapter orderClassifyAdapter;
    private View view;
    private Database db;
    //缓存disheskind 与 对应菜品数量的number集合

    private Map<String, float[]> dishesCollection = new HashMap<>();
    private Map<String, List<Document>> dishesObjectCollection;
    private boolean[] booleans;
    List<DishesKind> dishesKindCList;

    String kindId;

    List<Document> goodsCList;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);
        intiData1();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frame_order, null);
        dishesRv = view.findViewById(R.id.dishes_rv);
        orderList = view.findViewById(R.id.order_list);
        return view;
    }

    private void intiData1() {
        db = CDBHelper.getDatabase();
        //获取初始化数据
        dishesKindCList = CDBHelper.getObjByClass(DishesKind.class);

        booleans = new boolean[dishesKindCList.size()];

        orderClassifyAdapter = new OrderClassifyAdapter(((MainActivity) getActivity()), db, dishesKindCList.get(0).getId());
        ((MainActivity) getActivity()).setOrderDragAdapter(orderClassifyAdapter);
        leftAdapter = new DishesKindAdapter();
        ((MainActivity) getActivity()).setDishesKindAdapter(leftAdapter);
        leftAdapter.setaBoolean(booleans);

        leftAdapter.setNames(dishesKindCList);

        orderList.setAdapter(leftAdapter);

        orderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                leftAdapter.changeSelected(position);
                orderClassifyAdapter.setKindId(dishesKindCList.get(position).getId());

            }
        });
        dishesRv.setAdapter(orderClassifyAdapter);

        dishesRv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String dishId = (String) orderClassifyAdapter.getItem(position);

                Document dishes = CDBHelper.getDocByID(dishId);
                if (!dishes.getBoolean("sell")) {
                    showDialog(dishes, position, orderClassifyAdapter.getNumber()[position]);
                }
            }
        });

    }

/*
*
     * 菜品选择弹出框编辑模块
*/
    private void showDialog(final Document dishes, final int position, float number) {

        final DishesMessage dishesMessage = new DishesMessage();

        final List<String> tasteList = new ArrayList<>();
        if (dishes.getArray("tasteIds") != null && dishes.getArray("tasteIds").count() > 0) {

            for (int i = 0; i < dishes.getArray("tasteIds").count(); i++) {
                Document document = CDBHelper.getDocByID(dishes.getArray("tasteIds").getString(i));
                if (document == null || document.getString("name") == null) {
                    continue;
                }
                tasteList.add(document.getString("name"));
            }

            if (tasteList.size() > 0) {
                dishesMessage.setDishesTaste(tasteList.get(0));
            } else if (tasteList.size() == 0) {

                return;
            }


        }

        view = LayoutInflater.from(getActivity()).inflate(R.layout.view_item_dialog, null);

        RecyclerView recyclerView = view.findViewById(R.id.view_dialog_recycler);

        final GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);


        recyclerView.setLayoutManager(manager);
        MyGridAdapter myGridAdapter = new MyGridAdapter(getActivity(), tasteList);
        myGridAdapter.setmOnItemOlickListener(new MyGridAdapter.OnItemOlickListener() {
            @Override
            public void onItemClick(int position) {

                dishesMessage.setDishesTaste(tasteList.get(position));
            }
        });
        recyclerView.setAdapter(myGridAdapter);

        final TextView price_tv = view.findViewById(R.id.price);

        final AmountView amountView = view.findViewById(R.id.amount_view);

        amountView.setNumber(1.0 + "");

        String all = MyBigDecimal.mul(amountView.getAmount() + "", dishes.getFloat("price") + "", 2);

        price_tv.setText("总计 " + all + " 元");


        dishesMessage.setDishKindId(dishes.getString("kindId"));
        dishesMessage.setOperation(true);
        dishesMessage.setSingle(false);
        final float[] l = new float[1];


        l[0] = Float.parseFloat(all);


        //增删选择器的数据改变的监听方法

        amountView.setChangeListener(new AmountView.ChangeListener() {
            @Override
            public void OnChange(float ls, boolean flag) {

                String all = MyBigDecimal.mul(ls + "", dishes.getFloat("price") + "", 2);
                l[0] = Float.parseFloat(all);


                price_tv.setText("总计 " + l[0] + " 元");

            }
        });

        dishesMessage.setName(dishes.getString("name"));

        dishesMessage.setDishesC(dishes);

        AlertDialog.Builder builder = new AlertDialog
                .Builder(getActivity());
        builder.setTitle(dishes.getString("name"));
        builder.setView(view);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", null);

        final AlertDialog alertDialog = builder.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (amountView.getAmount() > 0f) {

                    dishesMessage.setCount(amountView.getAmount());

                    EventBus.getDefault().postSticky(dishesMessage);

                    alertDialog.dismiss();

                } else {

                    Toast.makeText(getActivity(), "没有选择商品数量！", Toast.LENGTH_SHORT).show();
                    return;
                }


            }
        });
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    public class DishesKindAdapter extends BaseAdapter {


        private int mSelect = 0; //选中项
        boolean aBoolean[] = null;
        Map<String,Integer> number = null;

        public void setaBoolean(boolean[] aBoolean) {
            this.aBoolean = aBoolean;
        }

        public void setNames(List<DishesKind> names) {
            this.names = names;
        }

        private List<DishesKind> names;

        public DishesKindAdapter() {
        }


        @Override
        public int getCount() {
            return names == null ? 0 : names.size();
        }

        @Override
        public Object getItem(int i) {
            return names.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {

            LayoutInflater listContainerLeft;
            listContainerLeft = LayoutInflater.from(getActivity());
            final ListItemView listItemView;
            if (view == null) {
                listItemView = new ListItemView();
                view = listContainerLeft.inflate(R.layout.view_kindname_lv, null);
                listItemView.tv_title = view.findViewById(R.id.title);
                listItemView.imagePoint = view.findViewById(R.id.imagePoint);
                view.setTag(listItemView);
            } else {
                listItemView = (ListItemView) view.getTag();
            }
            if (mSelect == i) {
                view.setBackgroundResource(R.drawable.animtableclick);
                listItemView.tv_title.setTextColor(getActivity().getResources().getColor(R.color.white));
            } else {
                view.setBackgroundResource(R.drawable.animtablenoclick);
                listItemView.tv_title.setTextColor(getActivity().getResources().getColor(R.color.md_black_1000));
            }
            if (number != null) {
                if (number.size() == 0) {
                    listItemView.imagePoint.setVisibility(View.INVISIBLE);
                } else {
                    if (number.get(dishesKindCList.get(i).getId()) == null){
                        listItemView.imagePoint.setVisibility(View.INVISIBLE);
                    }else {
                        listItemView.imagePoint.setVisibility(View.VISIBLE);
                        listItemView.imagePoint.setText(number.get(dishesKindCList.get(i).getId()) + "");
                    }
                }
            }
            listItemView.tv_title.setText(names.get(i).getName());
            return view;

        }


        public void changeSelected(int positon) { //刷新方法
            mSelect = positon;
            notifyDataSetChanged();
        }

        class ListItemView {

            TextView tv_title;
            //ImageView imageView;
            TextView imagePoint;
        }

        public void dishKindCount(List<Document> goodsDocList){
            if (number == null){
                number = new HashMap<>();
            }
            for (Document doc : resetNumber(goodsDocList)){
                Document dishDoc = CDBHelper.getDocByID(doc.getString("dishId"));
                if (number.get(dishDoc.getString("kindId")) == null){
                    number.put(dishDoc.getString("kindId"),1);
                }else {
                    number.put(dishDoc.getString("kindId"),number.get(dishDoc.getString("kindId"))+1);
                }
            }
        }

        private List<Document> resetNumber(List<Document> goodsDocList) {
            //初始化数量
            number.clear();

            Set set = new HashSet();
            List<Document> newList = new ArrayList<>();
            for (Iterator iter = goodsDocList.iterator(); iter.hasNext();){
                Document object = (Document) iter.next();
                if(set.add(object.getString("dishId"))) {
                    newList.add(object);
                }
            }
            return newList;
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void setOrderList(String s) {

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }


}
