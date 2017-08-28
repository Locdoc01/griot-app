package de.griot_app.griot.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
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

import de.griot_app.griot.contacts_profiles.GuestProfileInputActivity;
import de.griot_app.griot.Helper;
import de.griot_app.griot.contacts_profiles.OwnProfileInputActivity;
import de.griot_app.griot.contacts_profiles.UserProfileInputActivity;
import de.griot_app.griot.views.ProfileImageView;
import de.griot_app.griot.R;
import de.griot_app.griot.dataclasses.LocalInterviewData;

/**
 * ArrayList-ListView-Adapter, which converts an ArrayList of LocalInterviewData-objects into ListView items.
 */
public class LocalInterviewDataAdapter extends ArrayAdapter<LocalInterviewData> {

    private static final String TAG = LocalInterviewDataAdapter.class.getSimpleName();

    private final Context mContext;

    //The ArrayList containing the LocalInterviewData-objects
    private ArrayList<LocalInterviewData> mListData;

    //Views, which are shown in every ListView item
    private TextView tvTitle;
    private TextView tvDate;
    private TextView tvLength;
    private ImageView ivMediaCover;
    private ImageView ivMediaCoverForeground;
    private ImageView btnOptions;
    private ProfileImageView pivInterviewer;
    private ProfileImageView pivNarrator;
    private TextView tvInterviewer;
    private TextView tvNarrator;
    private FrameLayout btnInterviewer;
    private FrameLayout btnNarrator;
    private TextView btnComments;
    private TextView tvComments;

    private View.OnClickListener clickListener;

    //constructor
    public LocalInterviewDataAdapter(Context context, ArrayList<LocalInterviewData> data) {
        super(context, R.layout.listitem_interview, data);
        mContext = context;
        mListData = new ArrayList<>(data);
    }

    //inflates the layout for every ListView item and initializes its views
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.listitem_interview, null);

        // get references to the objects, which are created during the intflation of the layout xml-file
        tvTitle = (TextView) v.findViewById(R.id.tv_headline);
        tvDate = (TextView) v.findViewById(R.id.tv_date);
        tvLength = (TextView) v.findViewById(R.id.tv_length);
        ivMediaCover = (ImageView) v.findViewById(R.id.iv_mediaCover);
        ivMediaCoverForeground = (ImageView) v.findViewById(R.id.iv_mediaCover_foreground);
        btnOptions = (ImageView) v.findViewById(R.id.button_options);
        pivInterviewer = (ProfileImageView) v.findViewById(R.id.piv_interviewer);
        pivNarrator = (ProfileImageView) v.findViewById(R.id.piv_narrator);
        tvInterviewer = (TextView) v.findViewById(R.id.textView_interviewer);
        tvNarrator = (TextView) v.findViewById(R.id.textView_narrator);
        btnInterviewer = (FrameLayout) v.findViewById(R.id.button_interviewer);
        btnNarrator = (FrameLayout) v.findViewById(R.id.button_narrator);
        btnComments = (TextView) v.findViewById(R.id.button_comments);

        //initialize the views with the data from the correspondent ArrayList
        tvTitle.setText(mListData.get(position).getTitle());
        tvDate.setText(mListData.get(position).getDateDay() + "." + mListData.get(position).getDateMonth() + "." + mListData.get(position).getDateYear());
        tvLength.setText(Helper.getLengthStringFromMiliseconds(Long.parseLong(mListData.get(position).getLength())));

        if (mListData.get(position).getPictureLocalURI() != null) {
            if (Uri.parse(mListData.get(position).getPictureLocalURI()) != null) {
                ImageView test = new ImageView(mContext);
                test.setImageURI(Uri.parse(mListData.get(position).getPictureLocalURI()));
                if (test.getDrawable() != null) {
                    ivMediaCover.setImageURI(Uri.parse(mListData.get(position).getPictureLocalURI()));
                    //if the interview got recorded as audio, the mediaCover will show the narrator profile picture in black/white and darkened
                    if (mListData.get(position).getMedium().equals("audio")) {
                        ivMediaCover.setScaleType(ImageView.ScaleType.CENTER_CROP);

                        ColorMatrix matrix = new ColorMatrix();
                        matrix.setSaturation(0);
                        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                        ivMediaCover.setColorFilter(filter);
                        ivMediaCoverForeground.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

        try { pivInterviewer.getProfileImage().setImageURI(Uri.parse(mListData.get(position).getInterviewerPictureLocalURI())); } catch (Exception e) {}
        try { pivNarrator.getProfileImage().setImageURI(Uri.parse(mListData.get(position).getNarratorPictureLocalURI())); } catch (Exception e) {}

        tvInterviewer.setText(mListData.get(position).getInterviewerName());
        tvNarrator.setText(mListData.get(position).getNarratorName());
        int n = mListData.get(position).getNumberComments();
        btnComments.setText("" + (n==0 ? mContext.getString(R.string.text_none) : n) + " " + ( n == 1 ? mContext.getString(R.string.text_comment) : mContext.getString(R.string.text_comments)));

        //set an OnClickListener for clickable views
        final int pos = position;
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_interviewer:
                        Log.d(TAG, "interviewer clicked: ");
                        //If interviewer is the current user, own user profile gets opened, otherwise the interviewers user profile
                        Intent intent;
                        if (mListData.get(pos).getInterviewerID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            intent = new Intent(mContext, OwnProfileInputActivity.class);
                        } else {
                            intent = new Intent(mContext, UserProfileInputActivity.class);
                            intent.putExtra("contactID", mListData.get(pos).getInterviewerID());
                        }
                        mContext.startActivity(intent);
                        break;
                    case R.id.button_narrator:
                        Log.d(TAG, "narrator clicked: ");
                        //If narrator is the current user, own user profile gets opened.
                        //Otherwise the narrators user profile or guest profile gets opened, depending if narrator is user or guest
                        if (mListData.get(pos).getNarratorID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            intent = new Intent(mContext, OwnProfileInputActivity.class);
                        } else if (mListData.get(pos).getNarratorIsUser()) {
                            intent = new Intent(mContext, UserProfileInputActivity.class);
                            intent.putExtra("contactID", mListData.get(pos).getNarratorID());
                        } else {
                            intent = new Intent(mContext, GuestProfileInputActivity.class);
                            intent.putExtra("contactID", getItem(pos).getNarratorID());
                        }
                        mContext.startActivity(intent);
                        break;
                    case R.id.button_comments:
                        Log.d(TAG, "comments clicked: ");
                        Toast.makeText(mContext, "Show Comments", Toast.LENGTH_SHORT).show();
                        //TODO
                        break;
                    case R.id.button_options:
                        Log.d(TAG, "options clicked: ");
                        Toast.makeText(mContext, "Show Options", Toast.LENGTH_SHORT).show();
                        //TODO
                        break;
                }
            }
        };

        btnInterviewer.setOnClickListener(clickListener);
        btnNarrator.setOnClickListener(clickListener);
        btnComments.setOnClickListener(clickListener);
        btnOptions.setOnClickListener(clickListener);
        return v;
    }
}
