package doaing.order.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import doaing.order.R;
import doaing.order.untils.MyBigDecimal;
import doaing.order.untils.Tool;


/**
 * @author donghaifeng
 */
public class DiscountActivity extends AppCompatActivity implements View.OnClickListener {

    RadioButton unitTen;
    RadioButton unitElement;
    RadioButton unitHorn;
    RadioGroup unit;
    Button submitArea;
    TextView totalTv;
    EditText discountEt;
    private EditText disEt;
    private float stashTotal;
    private CharSequence c;
    private InputMethodManager inputMethodManager;
    private  float total ;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        stashTotal = getIntent().getFloatExtra("Total", 0);
        totalTv.setText(String.valueOf(stashTotal));
        discountEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                reset();
                if (MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                    discountEt.setCursorVisible(true);
                }

                return false;
            }
        });
        disEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                reset();
                disEt.setCursorVisible(true);
                return false;
            }
        });

        discountEt.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {




            }

            @Override
            public void afterTextChanged(Editable editable) {


               if(!TextUtils.isEmpty(discountEt.getText().toString())){

                    if(!".".equals(editable.charAt(editable.length()-1)+"")){

                       if(stashTotal >= Float.valueOf(discountEt.getText().toString())){

                           totalTv.setText(Tool.substrct(stashTotal,Float.valueOf(discountEt.getText().toString()))+"");

                       }
                   }


                }else {

                    if(discountEt.isCursorVisible()){

                        totalTv.setText(stashTotal+"");
                    }
                }
            }
        });

        disEt.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(!TextUtils.isEmpty(disEt.getText().toString())){

                    if(!".".equals(editable.charAt(editable.length()-1)+"")){

                        if(100f >= Float.valueOf(disEt.getText().toString())){

                            totalTv.setText(MyBigDecimal.mul(stashTotal+"",MyBigDecimal.div(disEt.getText().toString(),"100",2),2));

                        }

                    }


                }else {

                    if(discountEt.isCursorVisible()){

                        totalTv.setText(stashTotal+"");
                    }
                }
            }
        });

    }

    private void initView() {
        unitTen = findViewById(R.id.unit_ten);
        unitElement = findViewById(R.id.unit_element);
        unitHorn = findViewById(R.id.unit_horn);
        unit = findViewById(R.id.unit);
        submitArea = findViewById(R.id.submit_area);
        totalTv = findViewById(R.id.total_tv);
        discountEt = findViewById(R.id.discount_et);
        disEt = findViewById(R.id.dis_et);
        unitTen.setOnClickListener(this);
        unitElement.setOnClickListener(this);
        unitHorn.setOnClickListener(this);
        submitArea.setOnClickListener(this);

    }

    private void reset() {
        if(unitTen.isChecked()){

            unitTen.setChecked(false);
        }
        if(unitElement.isChecked()){

            unitElement.setChecked(false);
        }
        if(unitHorn.isChecked()){

            unitHorn.setChecked(false);
        }
        if(discountEt.isCursorVisible()){

            discountEt.setCursorVisible(false);
        }
        if(getTextTotal() != stashTotal){

            totalTv.setText(String.valueOf(stashTotal));
        }
        if(disEt.isCursorVisible()){
            discountEt.setCursorVisible(false);
        }

    }

    public float getTextTotal(){

        return (TextUtils.isEmpty(totalTv.getText().toString()))?0:Float.valueOf(totalTv.getText().toString());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pay, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            finish();

        } else if (i == R.id.reset) {


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        if(discountEt.isCursorVisible()){

            discountEt.setCursorVisible(false);

            if(!TextUtils.isEmpty(discountEt.getText().toString())){
                discountEt.setText("");
            }


            if(inputMethodManager.isActive()){

                inputMethodManager.hideSoftInputFromWindow(DiscountActivity.this.getCurrentFocus().getWindowToken(), 0);
            }
        }

        int i = view.getId();
        if (i == R.id.unit_ten) {
            compareTotal(100f);


        } else if (i == R.id.unit_element) {
            compareTotal(10f);


        } else if (i == R.id.unit_horn) {
            compareTotal(1f);


        } else if (i == R.id.submit_area) {
            Intent intent = new Intent();
            intent.putExtra("Margin", Tool.substrct(stashTotal, getTextTotal()));
            setResult(RESULT_OK, intent);
            finish();


        }





    }

/*
*
     * 判断是否满足抹零条件
     * @param t
*/


      public void compareTotal(float t){


          if(stashTotal > t){

              int stash = (int) (stashTotal/t);

              totalTv.setText(String.valueOf(stash*t));

          }else {

              Toast.makeText(DiscountActivity.this,"不满足条件！",Toast.LENGTH_SHORT).show();

          }
      }

}
