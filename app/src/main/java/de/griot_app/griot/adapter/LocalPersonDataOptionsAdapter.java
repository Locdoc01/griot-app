package de.griot_app.griot.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import de.griot_app.griot.GuestProfileInputActivity;
import de.griot_app.griot.OwnProfileInputActivity;
import de.griot_app.griot.UserProfileInputActivity;
import de.griot_app.griot.mainactivities.MainChooseFriendInputActivity;
import de.griot_app.griot.views.ProfileImageView;
import de.griot_app.griot.R;
import de.griot_app.griot.dataclasses.LocalPersonData;

/**
 * Created by marcel on 08.08.17.
 */

public class LocalPersonDataOptionsAdapter extends ArrayAdapter<LocalPersonData> {

    private static final String TAG = LocalPersonDataOptionsAdapter.class.getSimpleName();

    private final Context mContext;

    private int position;
    private FrameLayout itemBackground;
    private TextView tvCategory;
    private FrameLayout listSeperator;
    private ProfileImageView pivPerson;
    private TextView tvPerson;
    private ImageView btnOptions;

    private int mTouchedID = -1;
    private ConstraintLayout mTouchedParent = null;

    private ArrayList<LocalPersonData> mListData;

    public LocalPersonDataOptionsAdapter(Context context, ArrayList<LocalPersonData> data) {
        super(context, R.layout.listitem_person, data);
        mContext = context;
        mListData = new ArrayList<>(data);
    }



    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.listitem_person, null);

        itemBackground = (FrameLayout) v.findViewById(R.id.item_background);
        tvCategory = (TextView) v.findViewById(R.id.category);
        listSeperator = (FrameLayout) v.findViewById(R.id.list_seperator);
        pivPerson = (ProfileImageView) v.findViewById(R.id.piv_person);
        tvPerson = (TextView) v.findViewById(R.id.textView_person);
        btnOptions = (ImageView) v.findViewById(R.id.button_item);
        btnOptions.setImageResource(R.drawable.options);
        btnOptions.setVisibility(View.VISIBLE);

        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    //happens on down action
                    case MotionEvent.ACTION_DOWN:
                        mTouchedParent = (ConstraintLayout)v.getParent();
                        mTouchedID = v.getId();
                        switch (v.getId()) {
                            case R.id.item_background:
                                ((TextView)mTouchedParent.findViewById(R.id.textView_person)).setTextColor(ContextCompat.getColor(mContext, R.color.colorGriotBlue));
                                return true;
                            case R.id.button_item:
                                ((ImageView)mTouchedParent.findViewById(R.id.button_item)).setColorFilter(ContextCompat.getColor(mContext, R.color.colorGriotBlue));
                                return true;
                        }
                        return false;
                    case MotionEvent.ACTION_UP:
                        //happens on any up action
                        switch (mTouchedID) {
                            case R.id.item_background:
                                ((TextView)mTouchedParent.findViewById(R.id.textView_person)).setTextColor(ContextCompat.getColor(mContext, R.color.colorGriotDarkgrey));
                            case R.id.button_item:
                                ((ImageView)mTouchedParent.findViewById(R.id.button_item)).setColorFilter(ContextCompat.getColor(mContext, R.color.colorGriotDarkgrey));
                        }
                        //happens, if up action took place at the same item as down action
                        switch (v.getId()) {
                            case R.id.item_background:
                                Intent intent;
                                if (getItem(position).getFirstname().equals(mContext.getString(R.string.text_add_guest))) {
                                    intent = new Intent(mContext, GuestProfileInputActivity.class);
                                } else if (getItem(position).getContactID().equals(FirebaseAuth.getInstance().getCurrentUser())) {
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
                                Toast.makeText(mContext, "Öffne Optionsmenü " + position, Toast.LENGTH_SHORT).show();
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

        this.position = position;

        if (mListData.get(position).getCategory()!=null) {
            listSeperator.setVisibility(View.VISIBLE);
            tvCategory.setText(mListData.get(position).getCategory());
        } else {
            listSeperator.setVisibility(View.GONE);
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
