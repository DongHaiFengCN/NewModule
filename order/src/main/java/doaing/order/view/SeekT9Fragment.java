package doaing.order.view;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Ordering;

import java.util.ArrayList;
import java.util.List;

import bean.kitchenmanage.dishes.DishesC;
import bean.kitchenmanage.order.GoodsC;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import doaing.order.R;
import doaing.order.view.adapter.MyGridAdapter;
import doaing.order.application.CDBHelper;
import doaing.order.application.MyApplication;
import doaing.order.untils.MyBigDecimal;
import doaing.order.untils.MyLog;

import static doaing.order.application.CDBHelper.getFormatDate;


/**
 * Created by lenovo on 2017/10/26.
 */

public class SeekT9Fragment extends Fragment {

    @BindView(R.id.activity_seek_list)
    ListView activitySeekList;
    /* @BindView(R.id.activity_seek_list)
     RecyclerView activitySeekList;*/
    Unbinder unbinder;
    @BindView(R.id.activity_seek_edit)
    EditText activitySeekEdit;
    @BindView(R.id.ibtn_key_1)
    RelativeLayout ibtnKey1;
    @BindView(R.id.ibtn_key_2)
    RelativeLayout ibtnKey2;
    @BindView(R.id.ibtn_key_3)
    RelativeLayout ibtnKey3;
    @BindView(R.id.ibtn_key_4)
    RelativeLayout ibtnKey4;
    @BindView(R.id.ibtn_key_5)
    RelativeLayout ibtnKey5;
    @BindView(R.id.ibtn_key_6)
    RelativeLayout ibtnKey6;
    @BindView(R.id.ibtn_key_7)
    RelativeLayout ibtnKey7;
    @BindView(R.id.ibtn_key_8)
    RelativeLayout ibtnKey8;
    @BindView(R.id.ibtn_key_9)
    RelativeLayout ibtnKey9;
    @BindView(R.id.ibtn_key_l)
    RelativeLayout ibtnKeyL;
    @BindView(R.id.ibtn_key_0)
    RelativeLayout ibtnKey0;
    @BindView(R.id.ibtn_key_r)
    RelativeLayout ibtnKeyR;
    @BindView(R.id.ibtn_key_del)
    RelativeLayout ibtnKeyDel;
    @BindView(R.id.tl_key_grid)
    TableLayout tlKeyGrid;
    @BindView(R.id.activity_seek_lin_26)
    LinearLayout activitySeekLin26;

    public static String[][] pinyin2sz = new String[][]{{"a", "b", "c", ""}, {"d", "e", "f", ""}, {"g", "h", "i", ""}, {"j", "k", "l", ""}, {"m", "n", "o", ""}, {"p", "q", "r", "s"}, {"t", "u", "v", ""}, {"w", "x", "y", "z"}};
    private float total = 0.0f;
    public int point = 1, tastePos;
    private float tmpAllPrice;

    private SeekT9Adapter seekT9Adapter;
    private List<GoodsC> t9GoodsList;

    private List<String> tasteList;

    View view;
    private Handler mHandler = null;
    private MyApplication myapp;
    private boolean isT9And26 = true;
    private boolean isT9And261 = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        MyLog.d( "onCreateView");
        View view = inflater.inflate(R.layout.activity_seek, container, false);

        unbinder = ButterKnife.bind(this, view);
        mHandler = new Handler();
        myapp = (MyApplication) getActivity().getApplication();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("T9and26", 0);
        isT9And261 = sharedPreferences.getBoolean("isFlag",true);
        initView();
        return view;

    }

    @Override
    public void onHiddenChanged(boolean hidden)
    {
        super.onHiddenChanged(hidden);
        MyLog.d( "onHiddenChanged");
        if(!hidden)
        {
            MyLog.d( "hidden="+hidden);
            t9GoodsList.clear();
            seekT9Adapter.notifyDataSetChanged();
            activitySeekEdit.setText("");
        }
    }

    public void initView()
    {
       if(t9GoodsList==null)
           t9GoodsList = new ArrayList<>();
       else
           t9GoodsList.clear();

        seekT9Adapter = new SeekT9Adapter((MainActivity) getActivity(), activitySeekEdit,t9GoodsList);

        seekT9Adapter.setListener(new SeekT9Adapter.SeekT9OnClickListener() {
            @Override
            public void OnClickListener(View view, String name, float price, int pos) {
                view.setBackgroundResource(R.color.lucency);
                showDialog(name, price, pos);
            }

        });
        activitySeekList.setAdapter(seekT9Adapter);
        ((MainActivity) getActivity()).setT9GoodsList(t9GoodsList);
        ((MainActivity) getActivity()).setSeekT9Adapter(seekT9Adapter);
        if (isT9And261){
            isT9And26 = false;
            tlKeyGrid.setVisibility(View.GONE);
            activitySeekLin26.setVisibility(View.VISIBLE);
        }else{
            isT9And26 = true;
            tlKeyGrid.setVisibility(View.VISIBLE);
            activitySeekLin26.setVisibility(View.GONE);
        }


        setSeetSearch();

    }


    /**
     * 菜品选择弹出框编辑模块
     *
     * @param name  传入的菜品的名称
     * @param price 传入的菜品的价格
     */
    private void showDialog(final String name, final float price, final int selGoodsPos)
    {

        tastePos = 0;
        if(tasteList==null)
        tasteList = new ArrayList<>();
        else
            tasteList.clear();

        view = LayoutInflater.from(getActivity()).inflate(R.layout.view_item_dialog, null);

        final TextView price_tv = view.findViewById(R.id.price);

        final DishesC dishesC = CDBHelper.getObjById(getActivity().getApplicationContext(), t9GoodsList.get(selGoodsPos).getDishesId(), DishesC.class);
        final AmountView amountView = view.findViewById(R.id.amount_view);
        final  float  sourceCount = t9GoodsList.get(selGoodsPos).getDishesCount();
        if (sourceCount == 0.0)
        {
            amountView.setNumber("1.0");
        } else {
            amountView.setNumber(sourceCount + "");
        }
        String all = MyBigDecimal.mul(amountView.getAmount()+ "", price + "", 2);
        price_tv.setText("总计 " + all + " 元");
        tmpAllPrice = Float.parseFloat(all);
        //增删选择器的数据改变的监听方法

        amountView.setChangeListener(new AmountView.ChangeListener() {
            @Override
            public void OnChange(float ls, boolean flag) {

                //实时计算当前菜品选择不同数量后的单品总价
                String all = MyBigDecimal.mul(ls + "", price + "", 2);
                tmpAllPrice = Float.parseFloat(all);

                price_tv.setText("总计 " + tmpAllPrice + " 元");

            }
        });
        if (dishesC.getTasteList() != null)
        {
            for (int i = 0; i < dishesC.getTasteList().size(); i++) {
                Document document = CDBHelper.getDocByID(getActivity().getApplicationContext(), dishesC.getTasteList().get(i).toString());
                tasteList.add(document.getString("tasteName"));
            }

        }
        //设置每行展示3个
        final GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
        RecyclerView recyclerView = view.findViewById(R.id.view_dialog_recycler);
        recyclerView.setLayoutManager(manager);
        MyGridAdapter myGridAdapter = new MyGridAdapter(getActivity(), tasteList);
        myGridAdapter.setmOnItemOlickListener(new MyGridAdapter.OnItemOlickListener() {
            @Override
            public void onItemClick(int position) {
                tastePos = position;
            }
        });
        recyclerView.setAdapter(myGridAdapter);
        AlertDialog.Builder builder = new AlertDialog
                .Builder(getActivity());


        builder.setTitle(name);
        builder.setView(view);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                float destCount = Float.parseFloat(amountView.getEtAmount().getText().toString());
                Log.e("SeekT9",destCount+"----------"+amountView.getAmount());
                if(destCount<=0)
                {
                    Toast.makeText(getActivity(), "没有选择商品数量！", Toast.LENGTH_SHORT).show();
                    return;
                }

                t9GoodsList.get(selGoodsPos).setDishesCount(destCount);
                seekT9Adapter.notifyDataSetChanged();

                GoodsC goodsC = new GoodsC(myapp.getCompany_ID());
                goodsC.setDishesName(name);
                if (tasteList.size() == 0) {
                    goodsC.setDishesTaste(null);
                } else {
                    goodsC.setDishesTaste(tasteList.get(tastePos));
                }
                goodsC.setDishesCount(destCount);
                goodsC.setPrice(price);
                goodsC.setGoodsType(0);
                goodsC.setCreatedTime(getFormatDate());
                goodsC.setDishesId(dishesC.get_id());
                goodsC.setDishesKindId(dishesC.getDishesKindId());
                ((MainActivity) getActivity()).changeOrderGoodsByT9(goodsC);
                activitySeekEdit.setText("");
            }
        });
        builder.show();
    }

    private void setSeetSearch() {

        activitySeekEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isT9And26){
                    search(activitySeekEdit.getText().toString());
                }else{
                    search26(activitySeekEdit.getText().toString());
                }

            }
        });

    }

    // 查询方法
    public void search(final String search) {
        if (search.length() < 2)
            return;

        t9GoodsList.clear();

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                List<Document> documentList = CDBHelper.getDocmentsByWhere(getActivity().getApplicationContext()
                        , Expression.property("className").equalTo("DishesC")
                                .and(Expression.property("dishesNameCode9").like("%" + search + "%"))
                        , null);
                for (Document doc : documentList) {
                    GoodsC goodsObj = new GoodsC(myapp.getCompany_ID());
                    goodsObj.setDishesName(doc.getString("dishesName"));
                    goodsObj.setDishesCount(0);
                    goodsObj.setPrice(doc.getFloat("price"));
                    goodsObj.setDishesId(doc.getId());
                    goodsObj.setDishesKindId(doc.getString("dishesKindId"));
                    t9GoodsList.add(goodsObj);

                }
                seekT9Adapter.notifyDataSetChanged();
            }

        });
    }

    // 查询方法
    public void search26(final String search) {
        if (search.length() < 2)
            return;

        t9GoodsList.clear();

        mHandler.post(new Runnable() {
            @Override
            public void run() {

                List<Document> documentList = CDBHelper.getDocmentsByWhere(getActivity().getApplicationContext()
                        , Expression.property("className").equalTo("DishesC")
                                .and(Expression.property("dishesNameCode26").like("%"+search + "%"))
                        , Ordering.property("dishesName").ascending());

                for (Document doc : documentList) {
                    GoodsC goodsObj = new GoodsC(myapp.getCompany_ID());
                    goodsObj.setDishesName(doc.getString("dishesName"));
                    goodsObj.setDishesCount(0);
                    goodsObj.setPrice(doc.getFloat("price"));
                    goodsObj.setDishesId(doc.getId());
                    goodsObj.setDishesKindId(doc.getString("dishesKindId"));
                    t9GoodsList.add(goodsObj);
                }
                seekT9Adapter.notifyDataSetChanged();
            }

        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    private  String findZDCKindId()
    {
        List<String> zdcIdList = CDBHelper.getIdsByWhere(getActivity().getApplicationContext()
                , Expression.property("className").equalTo("DishesKindC")
                 .and(Expression.property("kindName").equalTo("自点菜"))
                ,null);
        if(zdcIdList.size()>0)
            return  zdcIdList.get(0);
        else return "";
    }

    @OnClick({R.id.activity_seek_edit, R.id.ibtn_key_1, R.id.ibtn_key_2, R.id.ibtn_key_3, R.id.ibtn_key_4, R.id.ibtn_key_5, R.id.ibtn_key_6, R.id.ibtn_key_7, R.id.ibtn_key_8, R.id.ibtn_key_9, R.id.ibtn_key_l, R.id.ibtn_key_0, R.id.ibtn_key_r, R.id.ibtn_key_del,R.id.seek_26_q, R.id.seek_26_w, R.id.seek_26_e, R.id.seek_26_r, R.id.seek_26_t, R.id.seek_26_y, R.id.seek_26_u, R.id.seek_26_i, R.id.seek_26_o, R.id.seek_26_p, R.id.seek_26_a, R.id.seek_26_s, R.id.seek_26_d, R.id.seek_26_f, R.id.seek_26_g, R.id.seek_26_h, R.id.seek_26_j, R.id.seek_26_k, R.id.seek_26_l, R.id.seek_26_z, R.id.seek_26_x, R.id.seek_26_c, R.id.seek_26_v, R.id.seek_26_b, R.id.seek_26_n, R.id.seek_26_m,R.id.seek_26_sc,R.id.seek_26_qh})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.activity_seek_edit:

                break;

            case R.id.ibtn_key_1:

                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "1");
                isT9And26 = true;
                break;

            case R.id.ibtn_key_2:

                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "2");
                isT9And26 = true;

                break;

            case R.id.ibtn_key_3:

                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "3");
                isT9And26 = true;
                break;

            case R.id.ibtn_key_4:

                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "4");
                isT9And26 = true;
                break;

            case R.id.ibtn_key_5:

                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "5");
                isT9And26 = true;
                break;

            case R.id.ibtn_key_6:

                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "6");
                isT9And26 = true;
                break;

            case R.id.ibtn_key_7:

                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "7");
                isT9And26 = true;
                break;

            case R.id.ibtn_key_8:

                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "8");
                isT9And26 = true;
                break;

            case R.id.ibtn_key_9:

                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "9");
                isT9And26 = true;
                break;

            case R.id.ibtn_key_l:


                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                View view1 = View
                        .inflate(getActivity(), R.layout.custom_dc_dialog, null);//设置弹窗布局
                alert.setView(view1);
                alert.setCancelable(true);
                final EditText cm = view1.findViewById(R.id.custom_dc_c);//菜名
                final EditText jg = view1.findViewById(R.id.custom_dc_t);//价格
                final EditText f_count = view1.findViewById(R.id.custom_dc_count);
                f_count.clearFocus();
                f_count.setFocusableInTouchMode(false);
                f_count.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            f_count.setFocusableInTouchMode(true);
                            f_count.requestFocus();
                            f_count.selectAll();
                        }
                        return false;
                    }
                });
                //取消或确定按钮监听事件处理
                final AlertDialog dialog = alert.create();
                Button btn_cancel = view1
                        .findViewById(R.id.custom_dc_qx);//取消按钮
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });


                Button btn_comfirm = view1
                        .findViewById(R.id.custom_dc_qd);//确定按钮

                btn_comfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GoodsC obj = new GoodsC(myapp.getCompany_ID());
                        if(TextUtils.isEmpty(f_count.getText().toString()))
                        {
                            Toast.makeText(getActivity(), "数量不能为空或者.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (!TextUtils.isEmpty(cm.getText())&&!cm.getText().toString().equals(".")) {
                            obj.setDishesName(cm.getText().toString());
                            if (!jg.getText().toString().equals(".") && !TextUtils.isEmpty(jg.getText()))
                            {
                                float singlePrice =  Float.parseFloat(jg.getText().toString());
                                obj.setPrice(singlePrice);
                                float counts = Float.parseFloat(f_count.getText().toString());
                                obj.setDishesCount(counts);
                                //obj.setDishesId("DishesC."+ UUID.randomUUID());
                                String zdcDishedKindId = findZDCKindId();
                                obj.setDishesKindId(zdcDishedKindId);
                                obj.setGoodsType(3);
                                obj.setCreatedTime(getFormatDate());
                                ((MainActivity) getActivity()).getGoodsList().add(obj);
                                //购物车计数器数据更新
                                point = (((MainActivity) getActivity()).getPoint());
                                point++;
                                ((MainActivity) getActivity()).setPoint(point);

                                //计算总价
                                total = ((MainActivity) getActivity()).getTotal();
                                total = MyBigDecimal.add(total,MyBigDecimal.mul(singlePrice,counts,2),2);
                                ((MainActivity) getActivity()).setTotal(total);


                                dialog.dismiss();
                            } else {
                                Toast.makeText(getActivity(), "价格不可以为空或者.", Toast.LENGTH_LONG).show();
                            }

                        } else {
                            Toast.makeText(getActivity(), "菜名不可以为空或者.", Toast.LENGTH_LONG).show();
                        }

                    }
                });
                dialog.show();

                break;

            case R.id.ibtn_key_0:

                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "0");
                search(activitySeekEdit.getText().toString());
                break;

            case R.id.ibtn_key_r:
                tlKeyGrid.setVisibility(View.GONE);
                activitySeekLin26.setVisibility(View.VISIBLE);
                activitySeekEdit.setText("");
                t9GoodsList.clear();
                seekT9Adapter.notifyDataSetChanged();
                SharedPreferences settings = getActivity().getSharedPreferences("T9and26", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.clear();
                editor.commit();
                editor.putBoolean("isFlag",true);
                editor.commit();
                break;
            case R.id.ibtn_key_del:
                int length = activitySeekEdit.getSelectionEnd();
                if (length > 1) {
                    activitySeekEdit.getText().delete(length - 1, length);
                    search(activitySeekEdit.getText().toString());
                }
                if (length == 1) {
                    search("oo");
                    activitySeekEdit.getText().delete(length - 1, length);
                }

                break;

            case R.id.activity_seek_lin_26:
                break;
            case R.id.seek_26_q:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "q");
                isT9And26 = false;
                break;
            case R.id.seek_26_w:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "w");
                isT9And26 = false;
                break;
            case R.id.seek_26_e:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "e");
                isT9And26 = false;
                break;
            case R.id.seek_26_r:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "r");
                isT9And26 = false;
                break;
            case R.id.seek_26_t:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "t");
                isT9And26 = false;
                break;
            case R.id.seek_26_y:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "y");
                isT9And26 = false;
                break;
            case R.id.seek_26_u:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "u");
                isT9And26 = false;
                break;
            case R.id.seek_26_i:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "i");
                isT9And26 = false;
                break;
            case R.id.seek_26_o:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "o");
                isT9And26 = false;
                break;
            case R.id.seek_26_p:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "p");
                isT9And26 = false;
                break;
            case R.id.seek_26_a:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "a");
                isT9And26 = false;
                break;
            case R.id.seek_26_s:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "s");
                isT9And26 = false;
                break;
            case R.id.seek_26_d:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "d");
                isT9And26 = false;
                break;
            case R.id.seek_26_f:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "f");
                isT9And26 = false;
                break;
            case R.id.seek_26_g:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "g");
                isT9And26 = false;
                break;
            case R.id.seek_26_h:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "h");
                isT9And26 = false;
                break;
            case R.id.seek_26_j:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "j");
                isT9And26 = false;
                break;
            case R.id.seek_26_k:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "k");
                isT9And26 = false;
                break;
            case R.id.seek_26_l:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "l");
                isT9And26 = false;
                break;
            case R.id.seek_26_z:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "z");
                isT9And26 = false;
                break;
            case R.id.seek_26_x:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "x");
                isT9And26 = false;
                break;
            case R.id.seek_26_c:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "c");
                isT9And26 = false;
                break;
            case R.id.seek_26_v:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "v");
                isT9And26 = false;
                break;
            case R.id.seek_26_b:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "b");
                isT9And26 = false;
                break;
            case R.id.seek_26_n:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "n");
                isT9And26 = false;
                break;
            case R.id.seek_26_m:
                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "m");
                isT9And26 = false;
                break;

            case R.id.seek_26_qh:
                tlKeyGrid.setVisibility(View.VISIBLE);
                activitySeekLin26.setVisibility(View.GONE);
                activitySeekEdit.setText("");
                t9GoodsList.clear();
                seekT9Adapter.notifyDataSetChanged();

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("T9and26", 0);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.clear();
                edit.commit();
                edit.putBoolean("isFlag",false);
                edit.commit();
                break;

            case R.id.seek_26_sc:
                int length1 = activitySeekEdit.getSelectionEnd();
                if (length1 > 1) {
                    activitySeekEdit.getText().delete(length1 - 1, length1);
                    search(activitySeekEdit.getText().toString());
                }
                if (length1 == 1) {
                    search("oo");
                    activitySeekEdit.getText().delete(length1 - 1, length1);
                }
                break;

            default:
                break;
        }
    }
}
