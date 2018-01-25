package doaing.order.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import doaing.order.R;
import doaing.order.untils.MyBigDecimal;
import doaing.order.untils.MyLog;


/**
 * 项目名称：Order
 * 类描述：
 * @author:donghaifeng
 * 创建时间：2017/9/20 13:20
 * 修改人：donghaifeng
 * 修改时间：2017/9/20 13:20
 * 修改备注：
 */

public class AmountView extends LinearLayout implements View.OnClickListener {

    public float getAmount() {
        return amount;
    }

    private float amount = 1; //购买数量
    private int goods_storage = 100; //实际场景由数据库提供，默认设置为100

    public EditText getEtAmount() {
        return etAmount;
    }

    private EditText etAmount;
    private Button btnDecrease;
    private Button btnIncrease;

    private boolean flag = true;
    private boolean aBoolean = true;
    private int sum;

    public void setChangeListener(ChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    ChangeListener changeListener;

    public AmountView(Context context) {
        this(context, null);
    }

    public AmountView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.view_amount, this);

        etAmount = findViewById(R.id.etAmount);
        btnDecrease = findViewById(R.id.btnDecrease);
        btnIncrease =  findViewById(R.id.btnIncrease);
        btnDecrease.setOnClickListener(this);
        btnIncrease.setOnClickListener(this);
        etAmount.clearFocus();
        etAmount.setFocusableInTouchMode(false);
        etAmount.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(MotionEvent.ACTION_DOWN == motionEvent.getAction()){

                    etAmount.setCursorVisible(true);// 再次点击显示光标

                    aBoolean = false;

                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    etAmount.setFocusableInTouchMode(true);
                    etAmount.requestFocus();
                    etAmount.setText(etAmount.getText());
                    etAmount.selectAll();
                }

                return false;
            }
        });


       etAmount.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(aBoolean){

                    return;
                }
                aBoolean = false;

                if (editable.toString().isEmpty())

                    return;


                amount = Float.valueOf(editable.toString());

                if (amount > goods_storage) {

                  etAmount.setText(goods_storage + "");

                    changeListener.OnChange(amount,true);

                    return;

                }else{

                    if(amount > sum){

                        changeListener.OnChange(amount,true);

                        MyLog.e("加法 "+amount);

                    }else if(amount < sum){

                        changeListener.OnChange(amount,false);

                        MyLog.e("减法 "+amount);

                    }




                }
            }
        });
        /**
         * 获取自定义属性
         */
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attrs, R.styleable.AmountView);
        int btnWidth = obtainStyledAttributes.getDimensionPixelSize(R.styleable.AmountView_btnWidth, LayoutParams.WRAP_CONTENT);
        int tvWidth = obtainStyledAttributes.getDimensionPixelSize(R.styleable.AmountView_tvWidth, 80);
        int tvTextSize = obtainStyledAttributes.getDimensionPixelSize(R.styleable.AmountView_tvTextSize, 0);
        int btnTextSize = obtainStyledAttributes.getDimensionPixelSize(R.styleable.AmountView_btnTextSize, 0);
        obtainStyledAttributes.recycle();

        LayoutParams btnParams = new LayoutParams(btnWidth, LayoutParams.MATCH_PARENT);

        btnDecrease.setLayoutParams(btnParams);
        btnIncrease.setLayoutParams(btnParams);
        if (btnTextSize != 0) {
            btnDecrease.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnTextSize);
            btnIncrease.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnTextSize);
        }

        LayoutParams textParams = new LayoutParams(tvWidth, LayoutParams.MATCH_PARENT);
        etAmount.setLayoutParams(textParams);
        if (tvTextSize != 0) {
            etAmount.setTextSize(tvTextSize);
        }
    }

    @Override
    public void onClick(View v) {


        if(etAmount.isCursorVisible()){//如果光标开启就关闭

            etAmount.setCursorVisible(false);
            aBoolean = true;
        }
        int i = v.getId();
        flag = false;
        amount = Float.parseFloat(etAmount.getText().toString());


        if (i == R.id.btnDecrease) {
            if (amount >= 1.0f) {

                amount = MyBigDecimal.sub(amount,1.0f,1);
                etAmount.setText(amount + "");
                changeListener.OnChange(amount,false);
            }
        } else if (i == R.id.btnIncrease) {
            if (amount < goods_storage) {
                amount = amount+1.0f;
                etAmount.setText(amount + "");
                changeListener.OnChange(amount,true);
            }
        }


    }




    public void setNumber(String number){

        amount = Float.valueOf(number);
        etAmount.setText(number);
    }

    interface ChangeListener{

        void OnChange(float ls, boolean flag);

    }

}
