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

    private final Context mContext;

    //The ArrayList containing the LocalPersonData-objects
    private ArrayList<LocalPersonData> mListData;

    //Views, which are shown in every ListView item
    private FrameLayout itemBackground;
    private TextView tvCategory;
    private FrameLayout listSeperator;
    private ProfileImageView pivPerson;
    private TextView tvPerson;
    private ImageView btnOptions;

    //constructor
    public LocalPersonDataOptionsAdapter(Context context, ArrayList<LocalPersonData> data) {
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
        itemBackground = (FrameLayout) v.findViewById(R.id.item_background);
        tvCategory = (TextView) v.findViewById(R.id.category);
        listSeperator = (FrameLayout) v.findViewById(R.id.list_seperator);
        pivPerson = (ProfileImageView) v.findViewById(R.id.piv_person);
        tvPerson = (TextView) v.findViewById(R.id.textView_person);
        btnOptions = (ImageView) v.findViewById(R.id.button_item);
        btnOptions.setImageResource(R.drawable.options);
        btnOptions.setVisibility(View.VISIBLE);

        //show List category
        if (mListData.get(position).getCategory()!=null) {
            listSeperator.setVisibility(View.VISIBLE);
            tvCategory.setText(mListData.get(position).getCategory());
        } else {
            listSeperator.setVisibility(View.GONE);
        }

        //show profile pictures, if available, otherwise show placeholder
        if (mListData.get(position).getPictureLocalURI() != null && mListData.get(position).getPictureLocalURI().equals(mContext.getString(R.string.text_add_guest))) {
            pivPerson.getProfileImage().setImageResource(R.drawable.add_avatar);
            pivPerson.getProfileImagePlus().setVisibility(View.GONE);
            pivPerson.getProfileImageCircle().setVisibility(View.GONE);
        } else {
            try {
                pivPerson.getProfileImage().setImageURI(Uri.parse(mListData.get(position).getPictureLocalURI()));
            } catch (Exception e) {
            }
        }

        tvPerson.setText(mListData.get(position).getFirstname() + (mListData.get(position).getLastname()==null ? "" : " " + mListData.get(position).getLastname()));

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
                                if (getItem(position).getFirstname().equals(mContext.getString(R.string.text_add_guest))) {
                                    intent = new Intent(mContext, GuestProfileInputActivity.class);
                                } else if (getItem(position).getContactID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
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
                            case R.id.button_item:
                                Log.d(TAG, "options clicked: ");
                                Toast.makeText(mContext, "Öffne Optionsmenü " + position, Toast.LENGTH_SHORT).show();
                                //TODO
                                return true;
                        }
                        return false;
                }
                return false;
            }
        };

        itemBackground.setOnTouchListener(touchListener);
        btnOptions.setOnTouchListener(touchListener);
        parent.setOnTouchListener(touchListener);

        return v;
    }
}
