package de.griot_app.griot.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import de.griot_app.griot.contacts_profiles.GuestProfileInputActivity;
import de.griot_app.griot.contacts_profiles.OwnProfileInputActivity;
import de.griot_app.griot.contacts_profiles.UserProfileInputActivity;
import de.griot_app.griot.views.ProfileImageView;
import de.griot_app.griot.R;
import de.griot_app.griot.dataclasses.LocalPersonData;

/**
 * ArrayList-ListView-Adapter, which converts an ArrayList of LocalPersonData-objects into ListView items.
 *
 * This adapter is specialized for ListViews, which allows the user to choose a contact.
 * Use CombinedPersonListCreator to obtain a combined ListView of all contacts and set CombinedPersonListCreator.mMode either to
 * CombinedPersonListCreator.PERSONS_CHOOSE_MODE or CombinedPersonListCreator.GROUPS_CHOOSE_MODE, using setMode().
 */
public class LocalPersonDataChooseAdapter extends ArrayAdapter<LocalPersonData> {

    private static final String TAG = LocalPersonDataChooseAdapter.class.getSimpleName();

    private final Context mContext;

    //The ArrayList containing the LocalPersonData-objects
    private ArrayList<LocalPersonData> mListData;

    //Views, which are shown in every ListView item
    private TextView mTextViewCategory;
    private FrameLayout mListItemSeperator;
    private ProfileImageView mPivPerson;
    private TextView mTextViewPerson;
    private ImageView mButtonCheck;

    //necessary for OnTouchListener
    private ConstraintLayout mTouchedParent = null;

    //constructor
    public LocalPersonDataChooseAdapter(Context context, ArrayList<LocalPersonData> data) {
        super(context, R.layout.listitem_contact, data);
        mContext = context;
        mListData = new ArrayList<>(data);
    }

    //inflates the layout for every ListView item and initializes its views
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.listitem_contact, null);

        // get references to the objects, which are created during the intflation of the layout xml-file
        mTextViewCategory = (TextView) v.findViewById(R.id.category);
        mListItemSeperator = (FrameLayout) v.findViewById(R.id.list_seperator);
        mPivPerson = (ProfileImageView) v.findViewById(R.id.piv_person);
        mTextViewPerson = (TextView) v.findViewById(R.id.textView_person);
        mButtonCheck = (ImageView) v.findViewById(R.id.button_item);
        mButtonCheck.setImageResource(R.drawable.check);

        //show List category
        if (mListData.get(position).getCategory()!=null) {
            mListItemSeperator.setVisibility(View.VISIBLE);
            mTextViewCategory.setText(mListData.get(position).getCategory());
        } else {
            mListItemSeperator.setVisibility(View.GONE);
        }

        //show check mark, if item got selected
        if (mListData.get(position).getSelected()) {
            mButtonCheck.setVisibility(View.VISIBLE);
        } else {
            mButtonCheck.setVisibility(View.GONE);
        }

        //show profile pictures, if available, otherwise show placeholder
        if (mListData.get(position).getPictureLocalURI() != null && mListData.get(position).getPictureLocalURI().equals(mContext.getString(R.string.text_add_guest))) {
            mPivPerson.getProfileImage().setImageResource(R.drawable.add_avatar);
            mPivPerson.getProfileImagePlus().setVisibility(View.GONE);
            mPivPerson.getProfileImageCircle().setVisibility(View.GONE);
        } else {
            try {
                mPivPerson.getProfileImage().setImageURI(Uri.parse(mListData.get(position).getPictureLocalURI()));
            } catch (Exception e) {
            }
        }

        mTextViewPerson.setText(mListData.get(position).getFirstname() + (mListData.get(position).getLastname()==null ? "" : " " + mListData.get(position).getLastname()));

        //Set an OnTouchListener for clickable views
        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mTouchedParent = (ConstraintLayout)v.getParent();
                        switch (v.getId()) {
                            case R.id.piv_person:
                            case R.id.textView_person:
                                if (getItem(position).getFirstname().equals(mContext.getString(R.string.text_add_guest))) {
                                    return false;
                                }
                                ((TextView)mTouchedParent.findViewById(R.id.textView_person)).setTextColor(ContextCompat.getColor(mContext, R.color.colorGriotBlue));
                                // If profile image or name was touched, it gets blue, and after 0.3s darkgrey again. This prevents color issue if movement occurs during action_down
                                Handler h = mTouchedParent.getHandler();
                                if (h != null) {
                                    h.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((TextView) mTouchedParent.findViewById(R.id.textView_person)).setTextColor(ContextCompat.getColor(mContext, R.color.colorGriotDarkgrey));
                                        }
                                    }, 300);
                                }
                                return true;
                        }
                        return false;
                    case MotionEvent.ACTION_UP:
                        switch (v.getId()) {
                            case R.id.textView_person:
                            case R.id.piv_person:
                                Log.d(TAG, "person clicked: ");
                                Intent intent;
                                //If first guest item was clicked, nothing happens. (First item is for adding a guest, which gets triggered through OnListItemClickListener)
                                if (getItem(position).getFirstname().equals(mContext.getString(R.string.text_add_guest))) {
                                    return false;
                                }
                                //If clicked person was the user himself, his own user profile gets opened
                                //Otherwise the persons user profile or guest profile gets opened, depending if person is user or guest
                                if (getItem(position).getContactID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    intent = new Intent(mContext, OwnProfileInputActivity.class);
                                } else if (getItem(position).getIsUser()) {
                                    intent = new Intent(mContext, UserProfileInputActivity.class);
                                    intent.putExtra("contactID", getItem(position).getContactID());
                                } else {
                                    intent = new Intent(mContext, GuestProfileInputActivity.class);
                                    intent.putExtra("contactID", getItem(position).getContactID());
                                }
                                mContext.startActivity(intent);
                                return true;
                        }
                        return false;
                }
                return false;
            }
        };

        mPivPerson.setOnTouchListener(touchListener);
        mTextViewPerson.setOnTouchListener(touchListener);

        return v;
    }
}
