package de.griot_app.griot.contacts_profiles;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.Query;

import de.griot_app.griot.R;
import de.griot_app.griot.adapters.CombinedPersonListCreator;
import de.griot_app.griot.baseactivities.GriotBaseActivity;


/**
 * This activity provides the contact managmant, which holds one ListView for person contacts and one for group contacts in two different tabs
 * By clicking on a list item the appropriate profile for the contact gets opened.
 * By clicking on add guest the add-guest-form gets opened
 */
public class ContactManagmentActivity extends GriotBaseActivity {

    private static final String TAG = ContactManagmentActivity.class.getSimpleName();

    //Views for tabs
    private Button mTabLeft;
    private Button mTabRight;

    //necessary for tab handling
    private boolean mTabLeftSelected = true;

    //OnClickListener for tabs
    private View.OnClickListener mTabListener;

    private EditText mEditTextSearchPerson;
    private EditText mEditTextSearchGroup;

    //ListViews, that holds the contact lists
    private ListView mListViewPersons;
    private ListView mListViewGroups;

    //Firebase queries for obtaining information for all person contacts
    private Query mQueryGuests;
    private Query mQueryFriends;
    //TODO: add queries for groups

    //Creates the PersonlistView as a combination of guest list data, friend list data and approriate category headings
    private CombinedPersonListCreator mCombinedListCreator;
    //TODO: check, if CombinePersonListCreator works for groups as well

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle.setText(R.string.title_contact_managment);
        //mButtonProfile.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorGriotBlue, null));

        mTabLeft = findViewById(R.id.tab_left);
        mTabRight = findViewById(R.id.tab_right);

        mEditTextSearchPerson = findViewById(R.id.editText_search_person);
        mEditTextSearchGroup = findViewById(R.id.editText_search_group);

        mListViewPersons = findViewById(R.id.listView_persons);
        mListViewGroups = findViewById(R.id.listView_groups);

        mListViewPersons.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        mListViewGroups.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        mQueryGuests = mDatabaseRootReference.child("guests");   //TODO specify to get only guests of the current user
        mQueryFriends = mDatabaseRootReference.child("users");  //TODO specify to get only friends of the current user

        //create the combined ListView
        mCombinedListCreator = new CombinedPersonListCreator(ContactManagmentActivity.this, mListViewPersons);
        mCombinedListCreator.setMode(CombinedPersonListCreator.PERSONS_OPTIONS_MODE);
        mCombinedListCreator.add(mQueryGuests);
        mCombinedListCreator.add(mQueryFriends);
        //TODO same for groups

        //OnClickListener, which control the tabs
        mTabListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tab_left:
                        if (!mTabLeftSelected) {
                            mTabLeft.setBackgroundColor(ContextCompat.getColor(ContactManagmentActivity.this, R.color.colorGriotWhite));
                            mTabLeft.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                            mEditTextSearchPerson.setVisibility(View.VISIBLE);
                            mListViewPersons.setVisibility(View.VISIBLE);
                            mTabRight.setBackgroundColor(ContextCompat.getColor(ContactManagmentActivity.this, R.color.colorBackgroundSelected));
                            mTabRight.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                            mEditTextSearchGroup.setVisibility(View.GONE);
                            mListViewGroups.setVisibility(View.GONE);
                            mTabLeftSelected = true;
                        }
                        break;
                    case R.id.tab_right:
                        if (mTabLeftSelected) {
                            mTabLeft.setBackgroundColor(ContextCompat.getColor(ContactManagmentActivity.this, R.color.colorBackgroundSelected));
                            mTabLeft.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                            mEditTextSearchPerson.setVisibility(View.GONE);
                            mListViewPersons.setVisibility(View.GONE);
                            mTabRight.setBackgroundColor(ContextCompat.getColor(ContactManagmentActivity.this, R.color.colorGriotWhite));
                            mTabRight.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                            mEditTextSearchGroup.setVisibility(View.VISIBLE);
                            mListViewGroups.setVisibility(View.VISIBLE);
                            mTabLeftSelected = false;
                        }
                        break;
                }
            }
        };

        mTabRight.setOnClickListener(mTabListener);
        mTabLeft.setOnClickListener(mTabListener);
    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_contact_managment;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }

    @Override
    protected void doOnStartAfterLoadingUserInformation() {
        mCombinedListCreator.loadData();
        //TODO same for groups
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
