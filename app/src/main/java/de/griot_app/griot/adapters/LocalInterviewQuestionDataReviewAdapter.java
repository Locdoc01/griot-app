package de.griot_app.griot.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Iterator;

import de.griot_app.griot.Helper;
import de.griot_app.griot.contacts_profiles.GuestProfileInputActivity;
import de.griot_app.griot.contacts_profiles.OwnProfileInputActivity;
import de.griot_app.griot.contacts_profiles.UserProfileInputActivity;
import de.griot_app.griot.interfaces.OnItemClickListener;
import de.griot_app.griot.views.ProfileImageView;
import de.griot_app.griot.views.TagView;
import de.griot_app.griot.R;
import de.griot_app.griot.dataclasses.LocalInterviewQuestionData;

/**
 * ArrayList-RecyclerView-Adapter, which converts an ArrayList of LocalInterviewQuestionData-objects into RecyclerView items.
 * This Adapter is for use in ReviewInterviewInputActivity
 */
public class LocalInterviewQuestionDataReviewAdapter extends RecyclerView.Adapter<LocalInterviewQuestionDataReviewAdapter.ViewHolder> {

    private static final String TAG = LocalInterviewQuestionDataReviewAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<LocalInterviewQuestionData> mListData;
    // private OnItemClickListener<LocalInterviewQuestionData> mListener;  //TODO löschen ??


    public class ViewHolder extends RecyclerView.ViewHolder {
        //item views
        private TextView mTextViewQuestion;
        private TextView mTextViewDate;
        private TextView mTextViewLength;
        private ImageView mImageViewMediaCover;
        private ImageView mImageViewMediaCoverForeground;
        private ImageView mButtonOptions;
        private TextView mTextViewTags;
        private ImageView mButtonAddTag;
        private HorizontalScrollView mScrollViewTags;
        private LinearLayout mLayoutScrollViewTags;

        public ViewHolder(View itemView) {
            super(itemView);
            // get references ítem views
            mTextViewQuestion = (TextView) itemView.findViewById(R.id.textView_headline);
            mTextViewDate = (TextView) itemView.findViewById(R.id.textView_date);
            mTextViewLength = (TextView) itemView.findViewById(R.id.textView_length);
            mImageViewMediaCover = (ImageView) itemView.findViewById(R.id.imageView_mediaCover);
            mImageViewMediaCoverForeground = (ImageView) itemView.findViewById(R.id.imageView_mediaCover_foreground);
            mButtonOptions = (ImageView) itemView.findViewById(R.id.button_options);
            mTextViewTags = (TextView) itemView.findViewById(R.id.textView_tags);
            mButtonAddTag = (ImageView) itemView.findViewById(R.id.button_add_tag);
            mScrollViewTags = (HorizontalScrollView) itemView.findViewById(R.id.scrollView_tags);
            mLayoutScrollViewTags = (LinearLayout) itemView.findViewById(R.id.layout_scrollView_tags);
        }

        //initialize item views
        public void bindClickListener(final LocalInterviewQuestionData dataItem) {
            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        /* //TODO löschen ??
                        case R.id.listitem_interview_question:
                            mListener.onItemClick(dataItem);
                            break;
                        */
                        case R.id.button_add_tag:
                            View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_input, null);

                            final TextView textViewInputDialog = (TextView) dialogView.findViewById(R.id.textView_inputDialog);
                            final EditText editTextInputDialog = (EditText) dialogView.findViewById(R.id.editText_inputDialog);

                            textViewInputDialog.setText(mContext.getString(R.string.dialog_add_tag));
                            editTextInputDialog.setHint(mContext.getString(R.string.hint_add_tag));

                            //Dialog for tag input
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(mContext, R.style.CustomDialogTheme));
                            alertDialogBuilder.setView(dialogView);
                            alertDialogBuilder
                                    .setCancelable(false)
                                    .setPositiveButton(mContext.getString(R.string.button_next),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    final String tag = editTextInputDialog.getText().toString().trim();
                                                    dataItem.getTags().put(tag, true);
                                                    notifyDataSetChanged();
                                                }
                                            })
                                    .setNegativeButton(mContext.getString(R.string.button_cancel),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                            alertDialogBuilder.create().show();
                            break;
                        case R.id.button_options:
                            Toast.makeText(mContext, "Show Options", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            };

            itemView.setOnClickListener(clickListener);
            mButtonAddTag.setOnClickListener(clickListener);
            mButtonOptions.setOnClickListener(clickListener);
        }

    }


    //constructor. If used, header, footer and tags for each item will be shown
    public LocalInterviewQuestionDataReviewAdapter(Context context, ArrayList<LocalInterviewQuestionData> listData) {
        mContext = context;
        mListData = listData;
        /* // TODO löschen ??
        mListener = new OnItemClickListener<LocalInterviewQuestionData>() {
            @Override
            public void onItemClick(LocalInterviewQuestionData dataItem) {
                // unfunctional placeholder, for the case, that no actual listener is assigned in the activity
            }
        };
        */
    }



    @Override
    public int getItemCount() {
        return mListData.size();
    }


    private LocalInterviewQuestionData getItem(int position) {
        return mListData.get(position);
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(R.layout.listitem_interview_question, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // get the dataItem item for the position
        final LocalInterviewQuestionData dataItem = getItem(position);

        //initialize the views with the data from the correspondent ArrayList
        holder.mTextViewQuestion.setText("" + (position + 1) + ". " + dataItem.getQuestion());
        holder.mTextViewDate.setText(dataItem.getDateDay() + "." + dataItem.getDateMonth() + "." + dataItem.getDateYear());
        holder.mTextViewLength.setText(Helper.getLengthStringFromMiliseconds(Long.parseLong(dataItem.getLength())));

        if (dataItem.getPictureLocalURI() != null) {
            if (Uri.parse(dataItem.getPictureLocalURI()) != null) {
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

        holder.mTextViewTags.setVisibility(View.GONE);
        holder.mButtonAddTag.setVisibility(View.GONE);
        holder.mLayoutScrollViewTags.setVisibility(View.GONE);

//            //set an OnClickListener for add tag button //TODO löschen
//            holder.mButtonAddTag.setOnClickListener(); //TODO löschen

        holder.bindClickListener(dataItem);
    }


}
