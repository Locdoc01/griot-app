package de.griot_app.griot.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import de.griot_app.griot.GuestProfileInputActivity;
import de.griot_app.griot.OwnProfileInputActivity;
import de.griot_app.griot.UserProfileInputActivity;
import de.griot_app.griot.views.ProfileImageView;
import de.griot_app.griot.R;
import de.griot_app.griot.dataclasses.LocalPersonData;

/**
 * Created by marcel on 08.08.17.
 */

public class LocalPersonDataAdapter extends ArrayAdapter<LocalPersonData> {

    private static final String TAG = LocalPersonDataAdapter.class.getSimpleName();

    private final Context mContext;

    private int position;
    private TextView tvListSeperator;
    private ProfileImageView pivPerson;
    private TextView tvPerson;
    private ImageView btnCheck;

    private ArrayList<LocalPersonData> mListData;

    public LocalPersonDataAdapter(Context context, ArrayList<LocalPersonData> data) {
        super(context, R.layout.listitem_person, data);
        mContext = context;
        mListData = new ArrayList<>(data);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.listitem_person, null);

        tvListSeperator = (TextView) v.findViewById(R.id.textView_list_seperator);
        pivPerson = (ProfileImageView) v.findViewById(R.id.piv_person);
        tvPerson = (TextView) v.findViewById(R.id.textView_person);
        btnCheck = (ImageView) v.findViewById(R.id.button_check);

        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        switch (v.getId()) {
                            case R.id.piv_person:
                            case R.id.textView_person:
                                if (getItem(position).getFirstname().equals(mContext.getString(R.string.text_add_guest))) {
                                    return false;
                                }
                                ((TextView)v).setTextColor(ContextCompat.getColor(mContext, R.color.colorGriotBlue));
                                return true;
                        }
                        return false;
                    case MotionEvent.ACTION_UP:
                        switch (v.getId()) {
                            case R.id.textView_person:
                                if (getItem(position).getFirstname().equals(mContext.getString(R.string.text_add_guest))) {
                                    return false;
                                }
                                ((TextView)v).setTextColor(ContextCompat.getColor(mContext, R.color.colorGriotDarkgrey));
                            case R.id.piv_person:
                                Intent intent;
                                if (getItem(position).getFirstname().equals(mContext.getString(R.string.text_add_guest))) {
                                    return false;
                                }
                                if (getItem(position).getContactID().equals(FirebaseAuth.getInstance().getCurrentUser())) {
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
            tvListSeperator.setVisibility(View.VISIBLE);
            tvListSeperator.setText(mListData.get(position).getCategory());
        } else {
            tvListSeperator.setVisibility(View.GONE);
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
