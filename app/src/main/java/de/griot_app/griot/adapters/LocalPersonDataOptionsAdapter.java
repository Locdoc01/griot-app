package de.griot_app.griot.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
 * This adapter is specialized for ListViews, which shows an options button.
 * Use CombinedPersonListCreator to obtain a combined ListView of all contacts and set CombinedPersonListCreator.mMode either to
 * CombinedPersonListCreator.PERSONS_OPTIONS_MODE or CombinedPersonListCreator.GROUPS_OPTIONS_MODE, using setMode().
 */
public class LocalPersonDataOptionsAdapter extends ArrayAdapter<LocalPersonData> {

    private static final String TAG = LocalPersonDataOptionsAdapter.class.getSimpleName();
    
    //Views, which are shown in every ListView item
    private FrameLayout mItemBackground;
    private TextView mTextViewCategory;
    private FrameLayout mListSeperator;
    private ProfileImageView mPivPerson;
    private ImageView mImageViewAddPerson;
    private TextView mTextViewPerson;
    private ImageView mButtonOptions;

    //constructor
    public LocalPersonDataOptionsAdapter(Context context, ArrayList<LocalPersonData> data) {
        super(context, 0, data);
    }

    //inflates the layout for every ListView item and initializes its views
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final LocalPersonData data = getItem(position);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if (convertView==null) {
            convertView = inflater.inflate(R.layout.listitem_contact, parent, false);
        }
        // get references to the objects, which are created during the intflation of the layout xml-file
        mItemBackground = convertView.findViewById(R.id.item_background);
        mTextViewCategory = convertView.findViewById(R.id.category);
        mListSeperator = convertView.findViewById(R.id.list_seperator);
        mPivPerson = convertView.findViewById(R.id.piv_person);
        mImageViewAddPerson = convertView.findViewById(R.id.imageView_add_person);
        mTextViewPerson = convertView.findViewById(R.id.textView_person);
        mButtonOptions = convertView.findViewById(R.id.button_item);
        mButtonOptions.setImageResource(R.drawable.options);
        mButtonOptions.setVisibility(View.VISIBLE);

        //show List category
        if (data.getCategory()!=null) {
            mListSeperator.setVisibility(View.VISIBLE);
            mTextViewCategory.setText(data.getCategory());
        } else {
            mListSeperator.setVisibility(View.GONE);
        }

        //show profile pictures, if available, otherwise show placeholder
        if (data.getPictureURL() != null && data.getPictureURL().equals(getContext().getString(R.string.text_add_guest))) {
            mPivPerson.setVisibility(View.GONE);
            mImageViewAddPerson.setVisibility(View.VISIBLE);
        } else {
            mPivPerson.setVisibility(View.VISIBLE);
            mImageViewAddPerson.setVisibility(View.GONE);
            mPivPerson.loadImageFromSource(data.getPictureURL());
        }

        mTextViewPerson.setText(data.getFirstname() + (data.getLastname()==null ? "" : " " + data.getLastname()));

        //Set an OnTouchListener for clickable views
        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        switch (v.getId()) {
                            case R.id.item_background:
                            case R.id.button_item:
                                return true;
                        }
                        return false;
                    case MotionEvent.ACTION_UP:
                        switch (v.getId()) {
                            case R.id.item_background:
                                Log.d(TAG, "person clicked: ");
                                Intent intent;
                                //If first guest item was clicked, nothing happens. (First item is for adding a guest, which gets triggered through OnListItemClickListener)
                                //Else If clicked person was the user himself, his own user profile gets opened
                                //Otherwise the persons user profile or guest profile gets opened, depending if person is user or guest
                                if (data.getFirstname().equals(getContext().getString(R.string.text_add_guest))) {
                                    intent = new Intent(getContext(), GuestProfileInputActivity.class);
                                } else if (data.getContactID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    intent = new Intent(getContext(), OwnProfileInputActivity.class);
                                } else if (data.getIsUser()) {
                                    intent = new Intent(getContext(), UserProfileInputActivity.class);
                                    intent.putExtra("contactID", data.getContactID());
                                } else {
                                    intent = new Intent(getContext(), GuestProfileInputActivity.class);
                                    intent.putExtra("contactID", data.getContactID());
                                }
                                getContext().startActivity(intent);
                                return true;
                            case R.id.button_item:
                                Log.d(TAG, "options clicked: ");
                                Toast.makeText(getContext(), "Öffne Optionsmenü " + position, Toast.LENGTH_SHORT).show();
                                //TODO
                                return true;
                        }
                        return false;
                }
                return false;
            }
        };

        mItemBackground.setOnTouchListener(touchListener);
        mButtonOptions.setOnTouchListener(touchListener);
        parent.setOnTouchListener(touchListener);

        return convertView;
    }
}
