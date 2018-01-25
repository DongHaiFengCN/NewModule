package view;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import tools.MyLog;

/**
 * 项目名称：dlcache
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2018/1/16 9:08
 * 修改人：donghaifeng
 * 修改时间：2018/1/16 9:08
 * 修改备注：
 */

public abstract class BaseFragment extends Fragment {

    private Activity activity;
    private View mContentView;
    private Unbinder unbinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (mContentView == null) {

            mContentView = inflater.inflate(setLayoutResourceID(), container, false);

            //强制竖屏显示
            if (activity != null) {

                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            }


            //返回一个Unbinder值（进行解绑），注意这里的this不能使用getActivity()
            unbinder =  ButterKnife.bind(this,mContentView);

            initDate(getArguments());
        }

        return mContentView;
    }


    /**
     * 此方法用于返回Fragment设置ContentView的布局文件资源ID
     *
     * @return 布局文件资源ID
     */
    protected abstract int setLayoutResourceID();


    /**
     * 获取Activity传递参数到fragmnet
     *
     * @param bundle
     */
    protected abstract void initDate(Bundle bundle);

    @CallSuper
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        MyLog.e("onDestroyView");
        if(unbinder != null){
            unbinder.unbind();
        }

        //复用rootview，防止view重叠的问题卡的问题
        if (null != mContentView) {
            final ViewParent parent = mContentView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mContentView);
            }

        }
    }


}
