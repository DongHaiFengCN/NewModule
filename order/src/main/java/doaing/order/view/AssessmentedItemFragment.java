package doaing.order.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.util.ArrayList;
import java.util.List;

import doaing.order.R;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class AssessmentedItemFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListenerDataBase mListener;

    private AssessmentedItemRecyclerViewAdapter assessmentedItemRecyclerViewAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AssessmentedItemFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListenerDataBase) {
            mListener = (OnListenerDataBase) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onListenerData");
        }
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static AssessmentedItemFragment newInstance(int columnCount) {
        AssessmentedItemFragment fragment = new AssessmentedItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        // Set the adapter
        if (view != null) {
            Context context = view.getContext();

            RecyclerView recyclerView = view.findViewById(R.id.list);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            assessmentedItemRecyclerViewAdapter = new AssessmentedItemRecyclerViewAdapter(mListener.getDataBase());
            recyclerView.setAdapter(assessmentedItemRecyclerViewAdapter);

            Button checkAllBt = view.findViewById(R.id.checkall_bt);
            checkAllBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    assessmentedItemRecyclerViewAdapter.checkAll();
                }
            });

            Button inverBt = view.findViewById(R.id.invert_bt);
            inverBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    assessmentedItemRecyclerViewAdapter.invert();
                }
            });

        }
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

        assessmentedItemRecyclerViewAdapter.save();


    }


}


