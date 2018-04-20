package de.griot_app.griot.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import de.griot_app.griot.interfaces.OnItemClickListener;
import de.griot_app.griot.views.ProfileImageView;
import de.griot_app.griot.R;
import de.griot_app.griot.dataclasses.LocalInterviewData;

/**
 * ArrayList-RecyclerView-Adapter, which converts an ArrayList of LocalInterviewData-objects into RecyclerView items.
 */
public class LocalInterviewDataAdapter extends RecyclerView.Adapter<LocalInterviewDataAdapter.ViewHolder> {

    private static final String TAG = LocalInterviewDataAdapter.class.getSimpleName();

    private static Context mContext;
    private ArrayList<LocalInterviewData> mListData;
    private static OnItemClickListener<LocalInterviewData> mListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //Views, which are shown in every RecyclerView item
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

        public ViewHolder(View itemView) {
            super(itemView);
            // get references to the objects, which are created during the intflation of the layout xml-file
            mTextViewTitle = (TextView) itemView.findViewById(R.id.textView_headline);
            mTextViewDate = (TextView) itemView.findViewById(R.id.textView_date);
            mTextViewLength = (TextView) itemView.findViewById(R.id.textView_length);
            mImageViewMediaCover = (ImageView) itemView.findViewById(R.id.imageView_mediaCover);
            mImageViewMediaCoverForeground = (ImageView) itemView.findViewById(R.id.imageView_mediaCover_foreground);
            mButtonOptions = (ImageView) itemView.findViewById(R.id.button_options);
            mPivInterviewer = (ProfileImageView) itemView.findViewById(R.id.piv_interviewer);
            mPivNarrator = (ProfileImageView) itemView.findViewById(R.id.piv_narrator);
            mTextViewInterviewer = (TextView) itemView.findViewById(R.id.textView_interviewer);
            mTextViewNarrator = (TextView) itemView.findViewById(R.id.textView_narrator);
            mButtonInterviewer = (FrameLayout) itemView.findViewById(R.id.button_interviewer);
            mButtonNarrator = (FrameLayout) itemView.findViewById(R.id.button_narrator);
            mButtonComments = (TextView) itemView.findViewById(R.id.button_comments);
        }

        public void bindClickListener(final LocalInterviewData dataItem) {
            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.listitem_interview:
                            mListener.onItemClick(dataItem);
                            break;
                        case R.id.button_interviewer:
                            Log.d(TAG, "interviewer clicked: ");
                            //If interviewer is the current user, own user profile gets opened, otherwise the interviewers user profile
                            Intent intent;
                            if (dataItem.getInterviewerID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                intent = new Intent(mContext, OwnProfileInputActivity.class);
                            } else {
                                intent = new Intent(mContext, UserProfileInputActivity.class);
                                intent.putExtra("contactID", dataItem.getInterviewerID());
                            }
                            mContext.startActivity(intent);
                            break;
                        case R.id.button_narrator:
                            Log.d(TAG, "narrator clicked: ");
                            //If narrator is the current user, own user profile gets opened.
                            //Otherwise the narrators user profile or guest profile gets opened, depending if narrator is user or guest
                            if (dataItem.getNarratorID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                intent = new Intent(mContext, OwnProfileInputActivity.class);
                            } else if (dataItem.getNarratorIsUser()) {
                                intent = new Intent(mContext, UserProfileInputActivity.class);
                                intent.putExtra("contactID", dataItem.getNarratorID());
                            } else {
                                intent = new Intent(mContext, GuestProfileInputActivity.class);
                                intent.putExtra("contactID", dataItem.getNarratorID());
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
                            //Folgende Zeile ist nötig für Custom Options Menü. Wird wahrscheinlich später durch andere Lösung ersetzt
//                        ((GriotBaseActivity)mContext).showOptionsMenu(new InterviewOptions().getOptions());
                            break;
                    }
                }
            };

            itemView.setOnClickListener(clickListener);
            mButtonInterviewer.setOnClickListener(clickListener);
            mButtonNarrator.setOnClickListener(clickListener);
            mButtonComments.setOnClickListener(clickListener);
            mButtonOptions.setOnClickListener(clickListener);
        }
    }

    private View.OnClickListener clickListener;

    //constructor
    public LocalInterviewDataAdapter(Context context, ArrayList<LocalInterviewData> listData) {
        mContext = context;
        mListData = listData;
        mListener = new OnItemClickListener<LocalInterviewData>() {
            @Override
            public void onItemClick(LocalInterviewData dataItem) {
                // unfunctional placeholder, for the case, that no actual listener is assigned in the activity
            }
        };

    }

    public void setOnItemClickListener(OnItemClickListener<LocalInterviewData> listener) {
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(R.layout.listitem_interview, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // get the dataItem item for the position
        final LocalInterviewData dataItem = mListData.get(position);

        //initialize the views with the dataItem from the correspondent ArrayList
        holder.mTextViewTitle.setText(dataItem.getTitle());
        holder.mTextViewDate.setText(dataItem.getDateDay() + "." + dataItem.getDateMonth() + "." + dataItem.getDateYear());
        holder.mTextViewLength.setText(Helper.getLengthStringFromMiliseconds(Long.parseLong(dataItem.getLength())));

        if (dataItem.getPictureLocalURI() != null) {
            if (Uri.parse(dataItem.getPictureLocalURI()) != null) {
                ImageView test = new ImageView(mContext);
                test.setImageURI(Uri.parse(dataItem.getPictureLocalURI()));
                if (test.getDrawable() != null) {
                    holder.mImageViewMediaCover.setImageURI(Uri.parse(dataItem.getPictureLocalURI()));
                    //if the interview got recorded as audio, the mediaCover will show the narrator profile picture in black/white and darkened
                    if (dataItem.getMedium().equals("audio")) {
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

        try { holder.mPivInterviewer.getProfileImage().setImageURI(Uri.parse(dataItem.getInterviewerPictureLocalURI())); } catch (Exception e) {}
        try { holder.mPivNarrator.getProfileImage().setImageURI(Uri.parse(dataItem.getNarratorPictureLocalURI())); } catch (Exception e) {}

        holder.mTextViewInterviewer.setText(dataItem.getInterviewerName());
        holder.mTextViewNarrator.setText(dataItem.getNarratorName());
        int n = dataItem.getNumberComments();
        holder.mButtonComments.setText("" + (n==0 ? mContext.getString(R.string.text_none) : n) + " " + ( n == 1 ? mContext.getString(R.string.text_comment) : mContext.getString(R.string.text_comments)));
        holder.bindClickListener(dataItem);
    }

}
