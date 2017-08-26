package de.griot_app.griot;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.Query;

import de.griot_app.griot.adapters.CombinedPersonListCreator;
import de.griot_app.griot.baseactivities.GriotBaseActivity;


/**
 *
 */
public class ContactManagmentActivity extends GriotBaseActivity {

    private static final String TAG = ContactManagmentActivity.class.getSimpleName();

    private boolean mTabLeftSelected = true;

    private Button mTabLeft;
    private Button mTabRight;

    private View.OnClickListener mTabListener;

    private EditText mEditTextSearchPerson;
    private EditText mEditTextSearchGroup;

    //ListViews, that holds the contact lists
    private ListView mListViewPersons;
    private ListView mListViewGroups;

    private Query mQueryGuests;
    private Query mQueryFriends;

    //Creates the PersonlistView as a combination of guest list data, friend list data and approriate headings
    private CombinedPersonListCreator mCombinedListCreator;
    //TODO: ausprobieren, ob die Klasse auch für Gruppen geeignet ist

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle.setText(R.string.title_contact_managment);
        //mButtonProfile.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorGriotBlue, null));

        mTabLeft = (Button) findViewById(R.id.tab_left);
        mTabRight = (Button) findViewById(R.id.tab_right);

        mEditTextSearchPerson = (EditText) findViewById(R.id.editText_search_person);
        mEditTextSearchGroup = (EditText) findViewById(R.id.editText_search_group);

        mListViewPersons = (ListView) findViewById(R.id.listView_persons);
        mListViewGroups = (ListView) findViewById(R.id.listView_groups);

        mListViewPersons.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        mListViewGroups.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        mQueryGuests = mDatabaseRootReference.child("guests");   //TODO genauer spezifizieren
        mQueryFriends = mDatabaseRootReference.child("users");  //TODO genauer spezifizieren

        //create the Combined ListView
        mCombinedListCreator = new CombinedPersonListCreator(ContactManagmentActivity.this, mListViewPersons);
        mCombinedListCreator.setMode(CombinedPersonListCreator.PERSONS_OPTIONS_MODE);
        mCombinedListCreator.add(mQueryGuests);
        mCombinedListCreator.add(mQueryFriends);

        mCombinedListCreator.loadData();
        //TODO Gruppen

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
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
