package view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import tools.ToolUtil;


/**
 * @author donghaifeng
 */
public abstract class BaseToobarActivity extends AppCompatActivity {

    private Subscription subscription;
    private String name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setMyContentView());
        ButterKnife.bind(this);
        initData(getIntent());
        setToolbar(setToolBarInfo());

    }

    protected void setToolbarName(String name) {

        this.name = name;


    }


    /**
     * 返回布局文件
     * @return 获取布局资源的id
     */

    protected abstract int setMyContentView();


    /**
     * 初始化数据
     *
     * @param intent 获取其他activity传入的参数
     */
    public abstract void initData(Intent intent);

    /**
     * 设置toolbar
     *
     * @return Toolbar
     */
    protected abstract Toolbar setToolBarInfo();

    protected void setToolbar(Toolbar toolbar) {
        if (toolbar == null) {

            return;
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

            }
        });

        if(!"".equals(this.name )){
             getSupportActionBar().setTitle(this.name );
        }

    }


    /**
     * 点击软键盘之外的空白处，隐藏软件盘
     *
     * @param ev 触发事件
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (ToolUtil.isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    /**
     * 显示软键盘
     */
    public void showInputMethod() {

        Observable observable = Observable.timer(500, TimeUnit.MILLISECONDS).subscribeOn(AndroidSchedulers.mainThread());
        subscription = observable.subscribe(new Action1() {
            @Override
            public void call(Object o) {
                if (getCurrentFocus() != null) {
                    InputMethodManager inputManager = (InputMethodManager) getCurrentFocus().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(getCurrentFocus(), 0);
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }
}
