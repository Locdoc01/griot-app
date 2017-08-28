package de.griot_app.griot.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
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
 * Created by marcel on 08.08.17.
 */

public class LocalPersonDataChooseAdapter extends ArrayAdapter<LocalPersonData> {

    private static final String TAG = LocalPersonDataChooseAdapter.class.getSimpleName();

    private final Context mContext;

    private int position;
    private TextView tvCategory;
    private FrameLayout listSeperator;
    private ProfileImageView pivPerson;
    private TextView tvPerson;
    private ImageView btnCheck;

    private ConstraintLayout mTouchedParent = null;

    private ArrayList<LocalPersonData> mListData;

    public LocalPersonDataChooseAdapter(Context context, ArrayList<LocalPersonData> data) {
        super(context, R.layout.listitem_contact, data);
        mContext = context;
        mListData = new ArrayList<>(data);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.listitem_contact, null);

        tvCategory = (TextView) v.findViewById(R.id.category);
        listSeperator = (FrameLayout) v.findViewById(R.id.list_seperator);
        pivPerson = (ProfileImageView) v.findViewById(R.id.piv_person);
        tvPerson = (TextView) v.findViewById(R.id.textView_person);
        btnCheck = (ImageView) v.findViewById(R.id.button_item);
        btnCheck.setImageResource(R.drawable.check);

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
                                Intent intent;
                                if (getItem(position).getFirstname().equals(mContext.getString(R.string.text_add_guest))) {
                                    return false;
                                }
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

        pivPerson.setOnTouchListener(touchListener);
        tvPerson.setOnTouchListener(touchListener);

        this.position = position;

        if (mListData.get(position).getCategory()!=null) {
            listSeperator.setVisibility(View.VISIBLE);
            tvCategory.setText(mListData.get(position).getCategory());
        } else {
            listSeperator.setVisibility(View.GONE);
        }

        if (mListData.get(position).getSelected()) {
            btnCheck.setVisibility(View.VISIBLE);
        } else {
            btnCheck.setVisibility(View.GONE);
        }
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

        return v;
    }
}
