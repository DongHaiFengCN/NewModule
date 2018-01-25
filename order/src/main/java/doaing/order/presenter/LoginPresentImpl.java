package doaing.order.presenter;

import android.content.Context;

import doaing.order.module.ILoginModel;
import doaing.order.module.ILoginView;
import doaing.order.module.LoginModelImpl;


/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/9/8 11:00
 * 修改人：donghaifeng
 * 修改时间：2017/9/8 11:00
 * 修改备注：
 */

public class LoginPresentImpl implements ILoginPresenter {

    private ILoginView iLoginView;
    private Context context;

    public LoginPresentImpl(ILoginView iLoginView, Context context){

        this.iLoginView = iLoginView;
        this.context = context ;

    }

    /**
     * 调用具体的业务逻辑
     */
    @Override
    public void doLogin() {

        String info[] = iLoginView.getLoginInfo();                //获取登录的账号密码，存放到数组

        ILoginModel iLoginModel  =new LoginModelImpl();          //得到登陆的操作model

        if(iLoginModel.Networkconnectionfailed(context)){        //判断网络是否可用

            if(!iLoginModel.isEmpty(info)){                      //判断登陆密码账号是否有空

                if(iLoginModel.isExit(context)){            //判断账号密码是否存在

                    if(iLoginView.isSave()){                    //缓存当前用户信息

                        iLoginModel.saveStatus(iLoginView.getSharePreferences());

                    }else {                                    //删除当前缓存用户信息

                        iLoginView.cancleSharePreferences();
                    }

                        iLoginView.success();                    //登陆主界面

                }else{

                    iLoginView.showError("用户不存在！");
                }

            }else{
                iLoginView.showError("用户名密码不能为空！");
            }

        }else {
            iLoginView.showError("网络连接失败！请检查网络！");
        }



    }
}
