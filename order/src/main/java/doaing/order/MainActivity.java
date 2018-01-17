package doaing.order;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jakewharton.rxbinding.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

/**
 * 主入口
 *
 * @author 董海峰
 * @date 2018/1/17
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bt)
    Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        RxView.clicks(bt).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                ARouter.getInstance().build("/test/activity").navigation();


            }
        });
    }


}
