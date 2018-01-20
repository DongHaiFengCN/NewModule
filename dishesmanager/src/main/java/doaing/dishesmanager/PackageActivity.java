package doaing.dishesmanager;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import view.BaseToobarActivity;

public class PackageActivity extends BaseToobarActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    protected int setMyContentView() {
        return R.layout.activity_package;
    }
    @Override
    protected Toolbar setToolBarInfo() {
        return toolbar;
    }
    @Override
    public void initData() {

    }


}
