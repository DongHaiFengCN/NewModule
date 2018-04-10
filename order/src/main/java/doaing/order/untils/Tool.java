
package doaing.order.untils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.couchbase.lite.Expression;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import bean.kitchenmanage.order.OrderNum;
import bean.kitchenmanage.promotion.PromotionRule;
import doaing.mylibrary.MyApplication;
import tools.CDBHelper;


/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/10/30 10:07
 * 修改人：donghaifeng
 * 修改时间：2017/10/30 10:07
 * 修改备注：
 */


public class Tool {
    public static long lastClickTime;
    public static boolean isNotEmpty(Object c){

        if(c == null){

            return false;
        }
        if(c instanceof String){

            if(TextUtils.isEmpty((CharSequence) c)){

                return false;
            }
        }
        if(c instanceof List){

            if(((List) c).isEmpty()){

                return false;
            }
        }
        return true;
    }

    public static boolean bindView(View view,String value){


     if(view instanceof TextView){


        if(TextUtils.isEmpty(value)){

             ((TextView) view).setText("");

            return false;

         }else{

             ((TextView) view).setText(value);

         }


     }else if(view instanceof EditText) {


         if(TextUtils.isEmpty(value)){

             ((EditText) view).setText("");
             return false;

         }else {

             ((EditText) view).setText(value);


         }
     }

        return true;
    }
    //减法
    public static float substrct(float a,float b){

        BigDecimal b1 = new BigDecimal(a+"");
        BigDecimal b2 = new BigDecimal(b+"");
        return b1.subtract(b2).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();

    }
    //乘法
    public static float multiply(float a,float b){

        BigDecimal b1 = new BigDecimal(a+"");
        BigDecimal b2 = new BigDecimal(b+"");
        return b1.multiply(b2).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();

    }
    //除法
    public static float divide(float a,float b){

        BigDecimal b1 = new BigDecimal(a+"");
        BigDecimal b2 = new BigDecimal(b+"");
        return b1.divide(b2).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();

    }

    //排序
    public static List<PromotionRule> Sort(List<PromotionRule> list){

        if(list.size() == 1){
            return list;
        }

            //从大到小排序
            if(list.size()>0)
            {
                PromotionRule tmp = null;
                for(int i=0;i<list.size();i++)
                {
                    for(int j=i+1;j<list.size();j++)
                    {
                        if(list.get(i).getCounts()<list.get(j).getCounts())
                        {
                            tmp = list.get(i);
                            list.set(i,list.get(j));
                            list.set(j,tmp);
                        }
                    }
                }
            }


        return list;
    }



/**
     * 返回最近的一次时间
     * @param s
     * @return
     */


    public static int getLastCheckOrder(List<String> s){

        String MARK = "";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.CHINA);

        //date集合

        List<Date> list = new ArrayList<>();

        //返回date集合

        for(int i = 0; i < s.size(); i++ ){
            Date d1 = null;
            try {
                d1 = simpleDateFormat.parse(s.get(i));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            list.add(d1);
        }

        Date d = list.get(0);  //0为第一个数组下标
        int flag = 0;
        for(int j = 1 ; j < list.size() ; j++){

          //  d=(list.get(j).getTime() < d.getTime()?d:list.get(j));

            if(list.get(j).getTime() > d.getTime()){

                d = list.get(j);
                flag = j;

            }


        }


    //    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return flag;
    }

    public static String getNewFormatDate(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);

    }


    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if ( (time - lastClickTime) < 3000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static    String getOrderSerialNum(Context  context)
    {
        MyApplication myApp = (MyApplication)context.getApplicationContext();
        String orderNum=null;
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");

        List<OrderNum> orderNumList = CDBHelper.getObjByWhere(context.getApplicationContext(), Expression.property("className").equalTo(Expression.string("OrderNum"))
                ,null
                ,OrderNum.class);
        if(orderNumList.size()<=0)//第一次使用
        {
            OrderNum obj = new OrderNum();
            obj.setChannelId(myApp.getCompany_ID());
            String time=formatter.format(new Date());
            obj.setDate(time);
            obj.setNum(1);
            CDBHelper.createAndUpdate(obj);
            orderNum =  "001";
        }
        else//有数据，判断是不是当天
        {
            OrderNum obj = orderNumList.get(0);
            String olderDate = obj.getDate();
            String newDate =  formatter.format(new Date());
            int num = obj.getNum();
            if(!newDate.equals(olderDate))//不是一天的，
            {
                obj.setNum(1);
                obj.setDate(newDate);
                CDBHelper.createAndUpdate(obj);
                orderNum =  "001";
            }
            else//同一天
            {
                int newNum = num+1;
                obj.setNum(newNum);
                CDBHelper.createAndUpdate(obj);
                orderNum = String.format("%3d", newNum).replace(" ", "0");
            }
        }

        return orderNum;

    }
}

