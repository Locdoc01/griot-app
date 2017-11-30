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

import de.griot_app.griot.baseactivities.GriotBaseActivity;
import de.griot_app.griot.contacts_profiles.GuestProfileInputActivity;
import de.griot_app.griot.Helper;
import de.griot_app.griot.contacts_profiles.OwnProfileInputActivity;
import de.griot_app.griot.contacts_profiles.UserProfileInputActivity;
import de.griot_app.griot.optionmenus.InterviewOptions;
import de.griot_app.griot.views.ProfileImageView;
import de.griot_app.griot.R;
import de.griot_app.griot.dataclasses.LocalInterviewData;

/**
 * ArrayList-ListView-Adapter, which converts an ArrayList of LocalInterviewData-objects into ListView items.
 */
public class LocalInterviewDataAdapter extends ArrayAdapter<LocalInterviewData> {

    private static final String TAG = LocalInterviewDataAdapter.class.getSimpleName();

    private static class ViewHolder {
        //Views, which are shown in every ListView item
        private TextView mTextViewTitle;
        private TextView mTextViewDate;
        private TextView mTextViewLength;
        private ImageView mImageViewMediaCover;
        private ImageView mImageViewMediaCoverForeground;
        private ImageView mButtonOptions;
        private ProfileImageView mPivInterviewer;
        private ProfileImageView mPivNarrator;
        private TextView mTextViewInterviewer;
        private TextView mTextViewNarrator;
        private FrameLayout mButtonInterviewer;
        private FrameLayout mButtonNarrator;
        private TextView mButtonComments;
        private TextView mTextViewComments;
    }

    private View.OnClickListener clickListener;

    //constructor
    public LocalInterviewDataAdapter(Context context, ArrayList<LocalInterviewData> data) {
        super(context, 0, data);
    }

    //inflates the layout for every ListView item and initializes its views
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // get the data item for the position
        final LocalInterviewData data = getItem(position);

        ViewHolder holder;
        if (convertView==null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listitem_interview, parent, false);

            // get references to the objects, which are created during the intflation of the layout xml-file
            holder.mTextViewTitle = (TextView) convertView.findViewById(R.id.textView_headline);
            holder.mTextViewDate = (TextView) convertView.findViewById(R.id.textView_date);
            holder.mTextViewLength = (TextView) convertView.findViewById(R.id.textView_length);
            holder.mImageViewMediaCover = (ImageView) convertView.findViewById(R.id.imageView_mediaCover);
            holder.mImageViewMediaCoverForeground = (ImageView) convertView.findViewById(R.id.imageView_mediaCover_foreground);
            holder.mButtonOptions = (ImageView) convertView.findViewById(R.id.button_options);
            holder.mPivInterviewer = (ProfileImageView) convertView.findViewById(R.id.piv_interviewer);
            holder.mPivNarrator = (ProfileImageView) convertView.findViewById(R.id.piv_narrator);
            holder.mTextViewInterviewer = (TextView) convertView.findViewById(R.id.textView_interviewer);
            holder.mTextViewNarrator = (TextView) convertView.findViewById(R.id.textView_narrator);
            holder.mButtonInterviewer = (FrameLayout) convertView.findViewById(R.id.button_interviewer);
            holder.mButtonNarrator = (FrameLayout) convertView.findViewById(R.id.button_narrator);
            holder.mButtonComments = (TextView) convertView.findViewById(R.id.button_comments);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //initialize the views with the data from the correspondent ArrayList
        holder.mTextViewTitle.setText(data.getTitle());
        holder.mTextViewDate.setText(data.getDateDay() + "." + data.getDateMonth() + "." + data.getDateYear());
        holder.mTextViewLength.setText(Helper.getLengthStringFromMiliseconds(Long.parseLong(data.getLength())));

        if (data.getPictureLocalURI() != null) {
            if (Uri.parse(data.getPictureLocalURI()) != null) {
                ImageView test = new ImageView(getContext());
                test.setImageURI(Uri.parse(data.getPictureLocalURI()));
                if (test.getDrawable() != null) {
                    holder.mImageViewMediaCover.setImageURI(Uri.parse(data.getPictureLocalURI()));
                    //if the interview got recorded as audio, the mediaCover will show the narrator profile picture in black/white and darkened
                    if (data.getMedium().equals("audio")) {
                        holder.mImageViewMediaCover.setScaleType(ImageView.ScaleType.CENTER_CROP);

                        ColorMatrix matrix = new ColorMatrix();
                        matrix.setSaturation(0);
                        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                        holder.mImageViewMediaCover.setColorFilter(filter);
                        holder.mImageViewMediaCoverForeground.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

        try { holder.mPivInterviewer.getProfileImage().setImageURI(Uri.parse(data.getInterviewerPictureLocalURI())); } catch (Exception e) {}
        try { holder.mPivNarrator.getProfileImage().setImageURI(Uri.parse(data.getNarratorPictureLocalURI())); } catch (Exception e) {}

        holder.mTextViewInterviewer.setText(data.getInterviewerName());
        holder.mTextViewNarrator.setText(data.getNarratorName());
        int n = data.getNumberComments();
        holder.mButtonComments.setText("" + (n==0 ? getContext().getString(R.string.text_none) : n) + " " + ( n == 1 ? getContext().getString(R.string.text_comment) : getContext().getString(R.string.text_comments)));

        //set an OnClickListener for clickable views
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_interviewer:
                        Log.d(TAG, "interviewer clicked: ");
                        //If interviewer is the current user, own user profile gets opened, otherwise the interviewers user profile
                        Intent intent;
                        if (data.getInterviewerID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            intent = new Intent(getContext(), OwnProfileInputActivity.class);
                        } else {
                            intent = new Intent(getContext(), UserProfileInputActivity.class);
                            intent.putExtra("contactID", data.getInterviewerID());
                        }
                        getContext().startActivity(intent);
                        break;
                    case R.id.button_narrator:
                        Log.d(TAG, "narrator clicked: ");
                        //If narrator is the current user, own user profile gets opened.
                        //Otherwise the narrators user profile or guest profile gets opened, depending if narrator is user or guest
                        if (data.getNarratorID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            intent = new Intent(getContext(), OwnProfileInputActivity.class);
                        } else if (data.getNarratorIsUser()) {
                            intent = new Intent(getContext(), UserProfileInputActivity.class);
                            intent.putExtra("contactID", data.getNarratorID());
                        } else {
                            intent = new Intent(getContext(), GuestProfileInputActivity.class);
                            intent.putExtra("contactID", data.getNarratorID());
                        }
                        getContext().startActivity(intent);
                        break;
                    case R.id.button_comments:
                        Log.d(TAG, "comments clicked: ");
                        Toast.makeText(getContext(), "Show Comments", Toast.LENGTH_SHORT).show();
                        //TODO
                        break;
                    case R.id.button_options:
                        Log.d(TAG, "options clicked: ");
                        Toast.makeText(getContext(), "Show Options", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        holder.mButtonInterviewer.setOnClickListener(clickListener);
        holder.mButtonNarrator.setOnClickListener(clickListener);
        holder.mButtonComments.setOnClickListener(clickListener);
        holder.mButtonOptions.setOnClickListener(clickListener);

        return convertView;
    }
}
