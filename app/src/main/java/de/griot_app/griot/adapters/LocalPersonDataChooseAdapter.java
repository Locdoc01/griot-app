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

    private static class ViewHolder {
        //Views, which are shown in every ListView item
        private TextView mTextViewCategory;
        private FrameLayout mListItemSeperator;
        private ProfileImageView mPivPerson;
        private ImageView mImageViewAddPerson;
        private TextView mTextViewPerson;
        private ImageView mButtonCheck;
    }

    //necessary for OnTouchListener
    private ConstraintLayout mTouchedParent = null;

    //constructor
    public LocalPersonDataChooseAdapter(Context context, ArrayList<LocalPersonData> data) {
        super(context, 0, data);
    }

    //inflates the layout for every ListView item and initializes its views
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // get the data item for the position
        final LocalPersonData data = getItem(position);

        ViewHolder holder;
        if (convertView==null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listitem_contact, parent, false);

            // get references to the objects, which are created during the intflation of the layout xml-file
            holder.mTextViewCategory = convertView.findViewById(R.id.category);
            holder.mListItemSeperator = convertView.findViewById(R.id.list_seperator);
            holder.mPivPerson = convertView.findViewById(R.id.piv_person);
            holder.mImageViewAddPerson = convertView.findViewById(R.id.imageView_add_person);
            holder.mTextViewPerson = convertView.findViewById(R.id.textView_person);
            holder.mButtonCheck = convertView.findViewById(R.id.button_item);
            holder.mButtonCheck.setImageResource(R.drawable.check);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //show List category
        if (data.getCategory()!=null) {
            holder.mListItemSeperator.setVisibility(View.VISIBLE);
            holder.mTextViewCategory.setText(data.getCategory());
        } else {
            holder.mListItemSeperator.setVisibility(View.GONE);
        }

        //show check mark, if item got selected
        if (data.getSelected()) {
            holder.mButtonCheck.setVisibility(View.VISIBLE);
        } else {
            holder.mButtonCheck.setVisibility(View.GONE);
        }

        //show profile pictures, if available, otherwise show placeholder
        if (data.getPictureURL() != null && data.getPictureURL().equals(getContext().getString(R.string.text_add_guest))) {
            holder.mPivPerson.setVisibility(View.GONE);
            holder.mImageViewAddPerson.setVisibility(View.VISIBLE);
        } else {
            holder.mPivPerson.setVisibility(View.VISIBLE);
            holder.mImageViewAddPerson.setVisibility(View.GONE);
            holder.mPivPerson.loadImageFromSource(data.getPictureURL());
        }

        holder.mTextViewPerson.setText(data.getFirstname() + (data.getLastname()==null ? "" : " " + data.getLastname()));

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
                                if (data.getFirstname().equals(getContext().getString(R.string.text_add_guest))) {
                                    return false;
                                }
                                ((TextView)mTouchedParent.findViewById(R.id.textView_person)).setTextColor(ContextCompat.getColor(getContext(), R.color.colorGriotBlue));
                                // If profile image or name was touched, it gets blue, and after 0.3s darkgrey again. This prevents color issue if movement occurs during action_down
                                Handler h = mTouchedParent.getHandler();
                                if (h != null) {
                                    h.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((TextView) mTouchedParent.findViewById(R.id.textView_person)).setTextColor(ContextCompat.getColor(getContext(), R.color.colorGriotDarkgrey));
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
                                //TODO: propably unnecessary, since visibility of piv_person of the first item is set to View.GONE
                                if (data.getFirstname().equals(getContext().getString(R.string.text_add_guest))) {
                                    return false;
                                }
                                //If clicked person was the user himself, his own user profile gets opened
                                //Otherwise the persons user profile or guest profile gets opened, depending if person is user or guest
                                if (data.getContactID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
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
                        }
                        return false;
                }
                return false;
            }
        };

        holder.mPivPerson.setOnTouchListener(touchListener);
        holder.mTextViewPerson.setOnTouchListener(touchListener);

        return convertView;
    }
}
