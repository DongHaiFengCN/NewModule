package doaing.order.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.LiveQueryChange;
import com.couchbase.lite.LiveQueryChangeListener;
import com.couchbase.lite.Ordering;
import com.couchbase.lite.Query;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import doaing.order.R;

public class AreaAdapter extends ArrayAdapter<String> {
	private static final String TAG = AreaAdapter.class.getSimpleName();

	private Database db;
	private LiveQuery listsLiveQuery = null;
	private int  selectItem=-1;
	public AreaAdapter(Context context, Database db )
	{
		super(context, 0);

		if(db == null) throw new IllegalArgumentException();
		this.db = db;

		this.listsLiveQuery = listsLiveQuery();
		this.listsLiveQuery.addChangeListener(new LiveQueryChangeListener() {
			@Override
			public void changed(LiveQueryChange change)
			{
				clear();
				ResultSet rs = change.getRows();
				Result result;
				while ((result = rs.next()) != null)
				{
					add(result.getString(0));
					//Log.e("areaAdapter","liveQuery change getRows ="+result.getString(0));
				}
				notifyDataSetChanged();
			}
		});
		this.listsLiveQuery.run();
	}
	@Override
	protected void finalize() throws Throwable {
		if (listsLiveQuery != null) {
			listsLiveQuery.stop();
			listsLiveQuery = null;
		}

		super.finalize();
	}
	private LiveQuery listsLiveQuery() {
		return Query.select(SelectResult.expression(Expression.meta().getId())
				, SelectResult.expression(Expression.property("areaName")))
				.from(DataSource.database(db))
				.where(Expression.property("className").equalTo("AreaC"))
				.orderBy(Ordering.property("areaNum").ascending())
				.toLive();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		String id = getItem(position);
		Document doc = db.getDocument(id);
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_area, null);

			viewHolder = new ViewHolder();
			viewHolder.areaname = (TextView) convertView.findViewById(R.id.area_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (position == selectItem) {
			convertView.setBackgroundResource(R.color.item_select);
		}
		else
		{
			convertView.setBackgroundResource(R.color.item_normal);
		}
		viewHolder.areaname.setText(doc.getString("areaName"));

		return convertView;
	}

	static class ViewHolder {
		TextView areaname;
	}
	public  void setSelectItem(int selectItem)
	{
		this.selectItem = selectItem;
		notifyDataSetChanged();
	}



}
