package doaing.dishesmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import view.BaseToobarActivity;

/**
 * @author donghaifeng
 * @Data 2018/1/17
 */

public class DishesManagerMainActivity extends BaseToobarActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.taste_im_bt)
    ImageView tasteImBt;
    @BindView(R.id.dishes_im_bt)
    ImageView dishesImBt;
    @BindView(R.id.disheskind_im_bt)
    ImageView disheskindImBt;
    @BindView(R.id.package_im_bt)
    ImageView packageImBt;

    @Override
    protected int setMyContentView() {
        return R.layout.activity_dishes_manager_main;
    }

    @Override
    protected Toolbar setToolBarInfo() {
        return toolbar;
    }

    @Override
    public void initData(Intent intent) {

        tasteImBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(DishesManagerMainActivity.this, TasteActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
        disheskindImBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(DishesManagerMainActivity.this, DishesKindActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
        dishesImBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(DishesManagerMainActivity.this, DishesActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        packageImBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(DishesManagerMainActivity.this, PackageActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });


    }


}
