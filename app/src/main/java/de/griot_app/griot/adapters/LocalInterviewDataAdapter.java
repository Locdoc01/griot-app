package de.griot_app.griot.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import de.griot_app.griot.GuestProfileInputActivity;
import de.griot_app.griot.Helper;
import de.griot_app.griot.OwnProfileInputActivity;
import de.griot_app.griot.UserProfileInputActivity;
import de.griot_app.griot.views.ProfileImageView;
import de.griot_app.griot.R;
import de.griot_app.griot.dataclasses.LocalInterviewData;

/**
 * Created by marcel on 08.08.17.
 */

public class LocalInterviewDataAdapter extends ArrayAdapter<LocalInterviewData> {

    private static final String TAG = LocalInterviewDataAdapter.class.getSimpleName();

    private final Context mContext;

    private ArrayList<LocalInterviewData> mListData;

    private int mPosition;
    private TextView tvTitle;
    private TextView tvDate;
    private TextView tvLength;
    private ImageView ivMediaCoverPlaceholder;
    private ImageView ivMediaCover;
    private ImageView btnOptions;
    private ProfileImageView pivInterviewer;
    private ProfileImageView pivNarrator;
    private TextView tvInterviewer;
    private TextView tvNarrator;
    private FrameLayout btnInterviewer;
    private FrameLayout btnNarrator;
    private TextView tvComments;

    private View.OnClickListener clickListener;

    public LocalInterviewDataAdapter(Context context, ArrayList<LocalInterviewData> data) {
        super(context, R.layout.listitem_interview, data);
        mContext = context;
        mListData = new ArrayList<>(data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.listitem_interview, null);

        tvTitle = (TextView) v.findViewById(R.id.tv_headline);
        tvDate = (TextView) v.findViewById(R.id.tv_date);
        tvLength = (TextView) v.findViewById(R.id.tv_length);
        ivMediaCoverPlaceholder = (ImageView) v.findViewById(R.id.iv_mediaCover_placeholder);
        ivMediaCover = (ImageView) v.findViewById(R.id.iv_mediaCover);
        btnOptions = (ImageView) v.findViewById(R.id.button_options);
        pivInterviewer = (ProfileImageView) v.findViewById(R.id.piv_interviewer);
        pivNarrator = (ProfileImageView) v.findViewById(R.id.piv_narrator);
        tvInterviewer = (TextView) v.findViewById(R.id.textView_interviewer);
        tvNarrator = (TextView) v.findViewById(R.id.textView_narrator);
        btnInterviewer = (FrameLayout) v.findViewById(R.id.button_interviewer);
        btnNarrator = (FrameLayout) v.findViewById(R.id.button_narrator);
        tvComments = (TextView) v.findViewById(R.id.textView_comments);

        mPosition = position;
        tvTitle.setText(mListData.get(mPosition).getTitle());
        tvDate.setText(mListData.get(mPosition).getDateDay() + "." + mListData.get(mPosition).getDateMonth() + "." + mListData.get(mPosition).getDateYear());
        tvLength.setText(Helper.getLengthStringFromMiliseconds(Long.parseLong(mListData.get(mPosition).getLength())));

        if (mListData.get(mPosition).getPictureLocalURI() != null) {
            if (Uri.parse(mListData.get(mPosition).getPictureLocalURI()) != null) {
                ImageView test = new ImageView(mContext);
                test.setImageURI(Uri.parse(mListData.get(mPosition).getPictureLocalURI()));
                if (test.getDrawable() != null) {
                    ivMediaCover.setImageURI(Uri.parse(mListData.get(mPosition).getPictureLocalURI()));
                    ivMediaCoverPlaceholder.setVisibility(View.GONE);
                    ivMediaCover.setVisibility(View.VISIBLE);
                }
            }
        }

        try { pivInterviewer.getProfileImage().setImageURI(Uri.parse(mListData.get(mPosition).getInterviewerPictureLocalURI())); } catch (Exception e) {}
        try { pivNarrator.getProfileImage().setImageURI(Uri.parse(mListData.get(mPosition).getNarratorPictureLocalURI())); } catch (Exception e) {}

        tvInterviewer.setText(mListData.get(mPosition).getInterviewerName());
        tvNarrator.setText(mListData.get(mPosition).getNarratorName());
        int n = mListData.get(mPosition).getNumberComments();
        tvComments.setText("" + (n==0 ? mContext.getString(R.string.text_none) : n) + " " + ( n == 1 ? mContext.getString(R.string.text_comment) : mContext.getString(R.string.text_comments)));

        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_interviewer:
                        Intent intent;
                        if (mListData.get(mPosition).getInterviewerID().equals(FirebaseAuth.getInstance().getCurrentUser())) {
                            intent = new Intent(mContext, OwnProfileInputActivity.class);
                        } else {
                            intent = new Intent(mContext, UserProfileInputActivity.class);
                            intent.putExtra("contactID", mListData.get(mPosition).getInterviewerID());
                        }
                        mContext.startActivity(intent);
                        break;
                    case R.id.button_narrator:
                        if (mListData.get(mPosition).getNarratorID().equals(FirebaseAuth.getInstance().getCurrentUser())) {
                            intent = new Intent(mContext, OwnProfileInputActivity.class);
                        } else if (mListData.get(mPosition).getNarratorIsUser()) {
                            intent = new Intent(mContext, UserProfileInputActivity.class);
                            intent.putExtra("contactID", mListData.get(mPosition).getNarratorID());
                        } else {
                            intent = new Intent(mContext, GuestProfileInputActivity.class);
                            intent.putExtra("contactID", getItem(mPosition).getNarratorID());
                        }
                        mContext.startActivity(intent);
                        break;
                    case R.id.button_options:
                    Toast.makeText(mContext, "Show Options", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        btnOptions.setOnClickListener(clickListener);
        btnInterviewer.setOnClickListener(clickListener);
        btnNarrator.setOnClickListener(clickListener);
        return v;
    }
}
