package doaing.dishesmanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.couchbase.lite.Document;
import com.couchbase.lite.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.kitchenmanage.dishes.DishesC;
import bean.kitchenmanage.dishes.DishesKindC;
import doaing.dishesmanager.PackageActivity;
import doaing.dishesmanager.PackageAddActivity;
import doaing.dishesmanager.PackageEditActivity;
import doaing.dishesmanager.R;

/**
 * 项目名称：new
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2018/1/29 11:47
 * 修改人：donghaifeng
 * 修改时间：2018/1/29 11:47
 * 修改备注：
 */

public class PackageManagerExpandableAdapter extends BaseExpandableListAdapter {

    private Map<String, List<Document>> dataset;
    private List<Document> groupList;
    Context context;

    public PackageManagerExpandableAdapter(List<Document> groupList, Map<String, List<Document>> dataset, Context context) {

        this.context = context;
        this.groupList = groupList;
        this.dataset = dataset;

    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groupList.get(groupPosition).getArray("dishesListId").count()+1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupView groupView;
        if (convertView == null) {
            groupView = new GroupView();
            convertView = LayoutInflater.from(context).inflate(R.layout.parent_item, null);
            groupView.groupTv = convertView.findViewById(R.id.parent_title);
            convertView.setTag(groupView);

        } else {
            groupView = (GroupView) convertView.getTag();

        }

        Document document = groupList.get(groupPosition);
        groupView.groupTv.setText(document.getString("kindName"));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildView childView;
        if (convertView == null) {
            childView = new ChildView();
            convertView = LayoutInflater.from(context).inflate(R.layout.child_item, null);
            childView.childTv = convertView.findViewById(R.id.child_title);
           childView.childBt = convertView.findViewById(R.id.child_bt);
            convertView.setTag(childView);
        } else {
            childView = (ChildView) convertView.getTag();
        }

        final Document document = groupList.get(groupPosition);

        Log.e("DOAING","一级套餐："+document.getString("kindName"));

        if(!isLastChild){



          /*  Log.e("DOAING","二级套餐数量："+dataset.get(document.getString("kindName")).size());

            Log.e("DOAING","二级套餐ID："+dataset.get(document.getString("kindName")).get(childPosition).getId());
            Log.e("DOAING","二级套餐名称："+dataset.get(document.getString("kindName")).get(childPosition).getString("dishesName"));
*/
            childView.childTv.setText(dataset.get(document.getString("kindName")).get(childPosition)
                    .getString("dishesName"));
            childView.childTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent intent = new Intent(context,PackageEditActivity.class);
                    intent.putExtra("id",document.getId());
                    context.startActivity(intent);

                }
            });

        }else {

            childView.childTv.setVisibility(View.GONE);
            childView.childBt.setVisibility(View.VISIBLE);

            childView.childBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context,PackageAddActivity.class);
                    intent.putExtra("id",document.getId());
                    context.startActivity(intent);
                }
            });
        }


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    static class GroupView {

        TextView groupTv;

    }

    static class ChildView {

        TextView childTv;
        Button childBt;
    }

}
