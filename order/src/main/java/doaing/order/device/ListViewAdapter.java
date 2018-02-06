package doaing.order.device;
import java.util.List;
import java.util.Map;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import doaing.order.R;

public class ListViewAdapter extends BaseAdapter{
	public final static String DEBUG_TAG="MyAdapter";
	public static final String IMG = "img";
	public static final String TITEL = "titel";
	public static final String STATUS = "status";
	public static final String INFO = "info";
    public static final String INFOTYPE = "infotype";
    public static final String INFOADRESS = "infoadress";
    public static final String BT_ENABLE = "btenable";
    public static final String ENABLE = "enable";
    public static final String DISABLE="disable";
	public static final int MESSAGE_CONNECT = 1;
	private Handler mHandler= null;
    private List<Map<String, Object>> listItems;    //商品信息集合   
    private LayoutInflater listContainer;           //视图容器    
    public final class ListItemView{                //自定义控件集�?    
            public ImageView image;
            public TextView name;
            public TextView typestr;
            public TextView adress;
            public  ImageView cntstation;
            public TextView title;     
            public TextView info;     
            public Button button;  
     }  
    ListItemView  listItemView = null;
    public ListViewAdapter(Context context,List<Map<String, Object>> listItems,
    		Handler handler) {           
        listContainer = LayoutInflater.from(context);   //创建视图容器并设置上下文   
        this.listItems = listItems;
        mHandler = handler; 
    }   
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
	}
	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
  //      ListItemView  listItemView = null;   
        if (arg1 == null) {   
            listItemView = new ListItemView();    
            arg1 = listContainer.inflate(R.layout.main_screen_list_item, null);

            listItemView.name = arg1.findViewById(R.id.tvOperationItem);
            listItemView.typestr = arg1.findViewById(R.id.printertype);
            listItemView.adress= arg1.findViewById(R.id.tvadress);
            listItemView.cntstation= arg1.findViewById(R.id.img_cnt_station);
           //设置控件集到convertView   
            arg1.setTag(listItemView);   
        }else {   
            listItemView = (ListItemView)arg1.getTag();   
        }  
        final int arg = arg0;
        listItemView.name.setText((String) listItems.get(arg0).get(TITEL));
        listItemView.typestr.setText((String) listItems.get(arg0).get(INFOTYPE));
        listItemView.adress.setText((String) listItems.get(arg0).get(INFOADRESS));


        if("连接".equals(listItems.get(arg0).get(STATUS)))
            //listItemView.cntstation.setBackgroundResource(R.drawable.printer_connect);
            listItemView.cntstation.setImageResource(R.mipmap.printer_connected);
        else
            listItemView.cntstation.setImageResource(R.mipmap.printer_connect);

        String str = (String)listItems.get(arg0).get(BT_ENABLE);
        if(str.equals(ENABLE))
        {
            listItemView.cntstation.setEnabled(true);
        }
        else
        {
            listItemView.cntstation.setEnabled(false);
        }
        listItemView.cntstation.setOnClickListener(new View.OnClickListener() {
            @Override  
            public void onClick(View v) { 
            	Log.d(DEBUG_TAG,"arg1 " + arg);
                Message message = new Message();  
                message.what = MESSAGE_CONNECT;  
                message.arg1 = arg;  
                listItemView.cntstation.getTag();
                mHandler.sendMessage(message);  
            }   
        });                  
       return arg1;  
   }  

}

