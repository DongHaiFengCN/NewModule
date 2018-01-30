package doaing.dishesmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import view.BaseToobarActivity;

public class PackageAddActivity extends BaseToobarActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Override
    protected int setMyContentView() {
        return R.layout.activity_package_add;
    }

    @Override
    public void initData(Intent intent) {

    }

    @Override
    protected Toolbar setToolBarInfo() {
        return toolbar;
    }
}
