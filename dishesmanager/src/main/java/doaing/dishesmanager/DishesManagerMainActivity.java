package doaing.dishesmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;

import butterknife.BindView;
import butterknife.ButterKnife;
import view.BaseToobarActivity;

/**
 * @author donghaifeng
 * @Data 2018/1/17
 */
@Route(path = "/dishes/DishesManagerMainActivity")
public class DishesManagerMainActivity extends BaseToobarActivity {

    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.taste_im_bt)
    ImageView tasteImBt;
    @BindView(R2.id.dishes_im_bt)
    ImageView dishesImBt;
    @BindView(R2.id.disheskind_im_bt)
    ImageView disheskindImBt;
    @BindView(R2.id.package_im_bt)
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
        setToolbarName("产品管理");

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
