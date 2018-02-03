package doaing.order.module;


import doaing.mylibrary.ISharedPreferences;

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/9/8 10:28
 * 修改人：donghaifeng
 * 修改时间：2017/9/8 10:28
 * 修改备注：
 */

public interface ILoginView extends ISharedPreferences {

   //获取登陆信息
   String[] getLoginInfo();

   //展示错误信息
   void showError(String error);

   // 登陆成功
   void success();

   //是否保存下次登陆
   boolean isSave();




}
