package doaing.order.view;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.couchbase.lite.Database;

import doaing.order.R;
import tools.CDBHelper;
import view.BaseToobarActivity;

/**
 * A management activity used to add and remove information on the evaluation of dishes
 *
 * @author donghaifeng
 */
public class AssessmentActivity extends BaseToobarActivity implements OnListenerDataBase {

    private boolean flag = false;
    public SearchView.SearchAutoComplete mSearchAutoComplete;
    private android.support.v4.app.FragmentTransaction transaction;
    private AssessmentedItemFragment assessmentedItemFragment;
    private AssessmentSelectItemFragment assessmentSelectItemFragment;

    @Override
    protected int setMyContentView() {
        return R.layout.activity_assessment;
    }

    @Override
    public void initData(Intent intent) {
        setToolbarName("估清清单");

        assessmentedItemFragment = AssessmentedItemFragment.newInstance(1);
        assessmentedItemFragment.onAttach(this);
        assessmentSelectItemFragment = AssessmentSelectItemFragment.newInstance(1);
        assessmentSelectItemFragment.onAttach(this);

        initializationFirst();
    }
    @Override
    protected Toolbar setToolBarInfo() {
        return findViewById(R.id.toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_assessment, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializationSecond();
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                initializationFirst();
                return false;
            }
        });
        mSearchAutoComplete = mSearchView.findViewById(R.id.search_src_text);
        mSearchAutoComplete.setBackgroundColor(ContextCompat.getColor(this, R.color.md_white));
        mSearchView.setQueryHint("搜索估清菜品");

        //set HintText color
        mSearchAutoComplete.setHintTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));

        //set Text color
        mSearchAutoComplete.setTextColor(ContextCompat.getColor(this, R.color.md_blue_grey_700));

        //Set SubmitButton is not visible when the search box opens
        mSearchView.setSubmitButtonEnabled(false);

        mSearchAutoComplete.setImeOptions(EditorInfo.IME_ACTION_SEND);
        mSearchAutoComplete.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        mSearchAutoComplete.setSingleLine(true);
        mSearchAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {

                // Length of the input is greater than 1
                if (s.length() > 1) {
                    assessmentSelectItemFragment.setQueryData(s.toString());
                } else if (s.length() == 0) {
                    assessmentSelectItemFragment.setQueryData(null);
                }
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Initialization activity loads this fragment {@see AssessmentedItemFragment}
     */
    private void initializationFirst() {
        transaction = getSupportFragmentManager().beginTransaction();

        if(flag){

            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        }else {
            flag = true;
        }

        transaction.replace(R.id.assessment_fragment, assessmentedItemFragment).commit();
    }
    /**
     * Enter the search fragment {@see AssessmentSelectItemFragment}
     */
    private void initializationSecond() {
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.assessment_fragment, assessmentSelectItemFragment).commit();
    }
    @Override
    public Database getDataBase() {
        return CDBHelper.getDatabase();
    }
}
