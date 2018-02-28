package doaing.order.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.Ordering;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.QueryChange;
import com.couchbase.lite.QueryChangeListener;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import doaing.order.R;
import doaing.order.view.DeskActivity;

public class AreaAdapter extends ArrayAdapter<String> {
	private static final String TAG = AreaAdapter.class.getSimpleName();
	private Database db;
	private Query listsLiveQuery = null;
	private int  selectItem=-1;
	AreaLocation areaLocation;

	public interface AreaLocation{
		public void setLocation(boolean location);
	}

	public void setAreaLocation(AreaLocation areaLocation){
		this.areaLocation = areaLocation;
	}

	public AreaAdapter(Context context, Database db)
	{
		super(context, 0);

		if(db == null) throw new IllegalArgumentException();
		this.db = db;

		this.listsLiveQuery = listsLiveQuery();
		this.listsLiveQuery.addChangeListener(new QueryChangeListener() {
			@Override
			public void changed(QueryChange change)
			{
				clear();
				ResultSet rs = change.getResults();
				Result result;
				while ((result = rs.next()) != null)
				{
					add(result.getString(0));
					//Log.e("areaAdapter","liveQuery change getRows ="+result.getString(0));
				}
				notifyDataSetChanged();
				if (getCount() > 0 ){
					areaLocation.setLocation(true);
				}

			}
		});

	}
	@Override
	protected void finalize() throws Throwable {
		if (listsLiveQuery != null) {
			listsLiveQuery = null;
		}

		super.finalize();
	}
	private Query listsLiveQuery() {
		return QueryBuilder.select(SelectResult.expression(Meta.id)
				, SelectResult.expression(Expression.property("areaName")))
				.from(DataSource.database(db))
				.where(Expression.property("className").equalTo(Expression.string("AreaC")))
				.orderBy(Ordering.property("areaNum").ascending())
				 ;
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
