package de.griot_app.griot.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
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
import de.griot_app.griot.ImageLoader;
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
 * This Adapter is for use in DetailsInterviewActivity
 */
public class LocalInterviewQuestionDataDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = LocalInterviewQuestionDataDetailsAdapter.class.getSimpleName();

    //values to determine the type of view
    private static final int VIEWTYPE_HEADER = 0;
    private static final int VIEWTYPE_ITEM = 1;
    private static final int VIEWTYPE_FOOTER = 2;

    private Context mContext;

    private ImageLoader mImageLoader;

    private ArrayList<LocalInterviewQuestionData> mListData;
    private OnItemClickListener<LocalInterviewQuestionData> mListener;

    private Intent mIntent;

    //intent data
    private String selectedInterviewID;
    private String interviewTitle;
    private String dateYear;
    private String dateMonth;
    private String dateDay;
    private String topic;
    private String medium;
    private String length;
    private String pictureLocalURI;
    private String pictureURL;
    private String interviewerID;
    private String interviewerName;
    private String interviewerPictureLocalURI;
    private String interviewerPictureURL;
    private String narratorID;
    private String narratorName;
    private String narratorPictureLocalURI;
    private String narratorPictureURL;
    private boolean narratorIsUser;
    private String[] associatedUsers;
    private String[] associatedGuests;
    private String[] tags;
    private int numberComments;

    public class ViewHolderHeader extends RecyclerView.ViewHolder {
        //header views
        private ImageView mMediaPlayer;
        private ImageView mMediaPlayerForeground;
        private ProfileImageView mPivInterviewer;
        private ProfileImageView mPivNarrator;
        private TextView mTextViewInterviewer;
        private TextView mTextViewNarrator;
        private FrameLayout mButtonInterviewer;
        private FrameLayout mButtonNarrator;
        private TextView mButtonComments;
        private ImageView mButtonOptions;
        private TextView mTextViewInterviewTitle;
        private TextView mDate;
        private LinearLayout mLayoutScrollViewTags;

        private View.OnClickListener mClickListener;

        public ViewHolderHeader(View itemView) {
            super(itemView);
            //Get references to header views
            mMediaPlayer = (ImageView) itemView.findViewById(R.id.mediaPlayer);
            mMediaPlayerForeground = (ImageView) itemView.findViewById(R.id.mediaPlayer_foreground);
            mPivInterviewer = (ProfileImageView) itemView.findViewById(R.id.piv_interviewer);
            mPivNarrator = (ProfileImageView) itemView.findViewById(R.id.piv_narrator);
            mTextViewInterviewer = (TextView) itemView.findViewById(R.id.textView_interviewer);
            mTextViewNarrator = (TextView) itemView.findViewById(R.id.textView_narrator);
            mButtonInterviewer = (FrameLayout) itemView.findViewById(R.id.button_interviewer);
            mButtonNarrator = (FrameLayout) itemView.findViewById(R.id.button_narrator);
            mButtonComments = (TextView) itemView.findViewById(R.id.button_comments);
            mButtonOptions = (ImageView) itemView.findViewById(R.id.button_options);
            mTextViewInterviewTitle = (TextView) itemView.findViewById(R.id.textView_interview_title);
            mDate = (TextView) itemView.findViewById(R.id.textView_date);
            mLayoutScrollViewTags = (LinearLayout) itemView.findViewById(R.id.layout_scrollView_tags);
        }
    }

    public class ViewHolderFooter extends RecyclerView.ViewHolder {

        //footer views
        private TextView mTextViewTopic;
        private LinearLayout mLayoutScrollViewVisibility;
        private TextView mTextViewComments;
        private EditText mEditTextPostComment;
        private ImageView mButtonPostComment;

        public ViewHolderFooter(View itemView) {
            super(itemView);
            //get references to footer views
            mTextViewTopic = (TextView) itemView.findViewById(R.id.textView_topic);
            mLayoutScrollViewVisibility = (LinearLayout) itemView.findViewById(R.id.layout_scrollView_visibility);
            mTextViewComments = (TextView) itemView.findViewById(R.id.textView_comments);
            mEditTextPostComment = (EditText) itemView.findViewById(R.id.editText_post_comment);
            mButtonPostComment = (ImageView) itemView.findViewById(R.id.button_post_comment);
        }
    }

    public class ViewHolderItem extends RecyclerView.ViewHolder {
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

        public ViewHolderItem(View itemView) {
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
                        case R.id.listitem_interview_question:
                            mListener.onItemClick(dataItem);
                            break;
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
    public LocalInterviewQuestionDataDetailsAdapter(Context context, ArrayList<LocalInterviewQuestionData> listData, Intent intent) {
        mContext = context;
        mImageLoader = new ImageLoader(mContext);
        mListData = listData;
        mListener = new OnItemClickListener<LocalInterviewQuestionData>() {
            @Override
            public void onItemClick(LocalInterviewQuestionData dataItem) {
                // unfunctional placeholder, for the case, that no actual listener is assigned in the activity
            }
        };
        mIntent = intent;

        //get intent data
        selectedInterviewID = intent.getStringExtra("selectedInterviewID");

        interviewTitle = intent.getStringExtra("interviewTitle");
        dateYear = intent.getStringExtra("dateYear");
        dateMonth = intent.getStringExtra("dateMonth");
        dateDay = intent.getStringExtra("dateDay");
        topic = intent.getStringExtra("topic");
        medium = intent.getStringExtra("medium");
        length = intent.getStringExtra("length");
        pictureLocalURI = intent.getStringExtra("pictureLocalURI");
        pictureURL = intent.getStringExtra("pictureURL");
        interviewerID = intent.getStringExtra("interviewerID");
        interviewerName = intent.getStringExtra("interviewerName");
        interviewerPictureLocalURI = intent.getStringExtra("interviewerPictureLocalURI");
        interviewerPictureURL = intent.getStringExtra("interviewerPictureURL");
        narratorID = intent.getStringExtra("narratorID");
        narratorName = intent.getStringExtra("narratorName");
        narratorPictureLocalURI = intent.getStringExtra("narratorPictureLocalURI");
        narratorPictureURL = intent.getStringExtra("narratorPictureURL");
        narratorIsUser = intent.getBooleanExtra("narratorIsUser", false);
        associatedUsers = intent.getStringArrayExtra("associatedUsers");
        associatedGuests = intent.getStringArrayExtra("associatedGuests");
        tags = intent.getStringArrayExtra("tags");
        numberComments = intent.getIntExtra("numberComments", 0);
    }


    public void setOnItemClickListener(OnItemClickListener<LocalInterviewQuestionData> listener) {
        mListener = listener;
    }


    // itemCount equals size of mListData + header + footer
    @Override
    public int getItemCount() {
        return mListData.size() + 2;
    }


    private LocalInterviewQuestionData getItem(int position) {
        return mListData.get(position-1);       //TODO: prüfen
    }


    private boolean isPositionHeader(int position) {
        return position == 0;
    }


    private boolean isPositionFooter(int position) {
        return position == getItemCount()-1;    //TODO: prüfen
    }


    /**
     *
     * @param position of view
     * @return type of view
     */
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return VIEWTYPE_HEADER;
        } else if (isPositionFooter(position)) {
            return VIEWTYPE_FOOTER;
        } else {
            return VIEWTYPE_ITEM;
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        //types of inflated layout and return value depend on the view type
        if (viewType == VIEWTYPE_HEADER) {
            View itemView = inflater.inflate(R.layout.listheader_details_interview, parent, false);
            return new ViewHolderHeader(itemView);
        }
        else if (viewType == VIEWTYPE_FOOTER) {
            View itemView = inflater.inflate(R.layout.listfooter_details_interview, parent, false);
            return new ViewHolderFooter(itemView);
        }
        else {
            View itemView = inflater.inflate(R.layout.listitem_interview_question, parent, false);
            return new ViewHolderItem(itemView);
        }

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ViewHolderHeader) {
            ViewHolderHeader holderHeader = (ViewHolderHeader) holder;
            //initialize header views with intent data:
            //initialize mediaPlayer
/*
            if (pictureLocalURI != null) {
                if (Uri.parse(pictureLocalURI) != null) {
                    ImageView test = new ImageView(mContext);
                    test.setImageURI(Uri.parse(pictureLocalURI));
                    if (test.getDrawable() != null) {
                        holderHeader.mMediaPlayer.setImageURI(Uri.parse(pictureLocalURI));
                        //if the interview got recorded as audio, the mediaCover will show the narrator profile picture in black/white and darkened
                        if (medium.equals("audio")) {
                            holderHeader.mMediaPlayer.setScaleType(ImageView.ScaleType.CENTER_CROP);

                            ColorMatrix matrix = new ColorMatrix();
                            matrix.setSaturation(0);
                            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                            holderHeader.mMediaPlayer.setColorFilter(filter);
                            holderHeader.mMediaPlayerForeground.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
*/
            mImageLoader.load(holderHeader.mMediaPlayer, pictureURL);
            if (medium.equals("audio")) {
                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation(0);
                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                holderHeader.mMediaPlayer.setColorFilter(filter);
                holderHeader.mMediaPlayerForeground.setVisibility(View.VISIBLE);
            }

            //initialize other header views
            holderHeader.mPivInterviewer.loadImageFromSource(interviewerPictureURL);
            holderHeader.mPivNarrator.loadImageFromSource(narratorPictureURL);
            holderHeader.mTextViewInterviewer.setText(interviewerName);
            holderHeader.mTextViewNarrator.setText(narratorName);
            holderHeader.mButtonComments.setText("" + (numberComments==0 ? mContext.getString(R.string.text_none) : numberComments) + " " + ( numberComments == 1 ? mContext.getString(R.string.text_comment) : mContext.getString(R.string.text_comments)));
            holderHeader.mTextViewInterviewTitle.setText(interviewTitle);
            holderHeader.mDate.setText(dateDay + "." + dateMonth + "." + dateYear);

            // create TagViews and add them to ScrollView
            for (int i=0 ; i<tags.length ; i++) {
                TagView tagView = new TagView(mContext);
                tagView.setTag(tags[i]);
                tagView.setVisibilityDeleteButton(false);
                holderHeader.mLayoutScrollViewTags.addView(tagView);
            }

            // set OnClickListener to button views
            holderHeader.mClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.button_interviewer:
                            Log.d(TAG, "interviewer clicked: ");
                            Intent intent;
                            if (interviewerID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                intent = new Intent(mContext, OwnProfileInputActivity.class);
                            } else {
                                intent = new Intent(mContext, UserProfileInputActivity.class);
                                intent.putExtra("contactID", interviewerID);
                            }
                            mContext.startActivity(intent);
                            break;
                        case R.id.button_narrator:
                            Log.d(TAG, "narrator clicked: ");
                            if (narratorID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                intent = new Intent(mContext, OwnProfileInputActivity.class);
                            } else if (narratorIsUser) {
                                intent = new Intent(mContext, UserProfileInputActivity.class);
                                intent.putExtra("contactID", narratorID);
                            } else {
                                intent = new Intent(mContext, GuestProfileInputActivity.class);
                                intent.putExtra("contactID", narratorID);
                            }
                            mContext.startActivity(intent);
                            break;
                        case R.id.button_comments:
                            Log.d(TAG, "comments clicked: ");
                            Toast.makeText(mContext, "Show Comments", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.button_options:
                            Log.d(TAG, "options clicked: ");
                            Toast.makeText(mContext, "Show Options", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            };

            holderHeader.mButtonInterviewer.setOnClickListener(holderHeader.mClickListener);
            holderHeader.mButtonNarrator.setOnClickListener(holderHeader.mClickListener);
            holderHeader.mButtonComments.setOnClickListener(holderHeader.mClickListener);
            holderHeader.mButtonOptions.setOnClickListener(holderHeader.mClickListener);

        } else if (holder instanceof ViewHolderFooter) {
            ViewHolderFooter holderFooter = (ViewHolderFooter) holder;
            //initialize footer views with intent data
            holderFooter.mTextViewTopic.setText(topic);

            int width = mContext.getResources().getDimensionPixelSize(R.dimen.dimen_piv);
            int height = mContext.getResources().getDimensionPixelSize(R.dimen.dimen_piv);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
            ProfileImageView pivInterviewer = new ProfileImageView(mContext);
            pivInterviewer.loadImageFromSource(interviewerPictureURL);
            holderFooter.mLayoutScrollViewVisibility.addView(pivInterviewer);
            pivInterviewer.setLayoutParams(params);
            if (!interviewerID.equals(narratorID)) {
                ProfileImageView pivNarrator = new ProfileImageView(mContext);
                pivNarrator.loadImageFromSource(narratorPictureURL);
                holderFooter.mLayoutScrollViewVisibility.addView(pivNarrator);
                pivNarrator.setLayoutParams(params);
            }
            //TODO: add associated users & guests to the ScrollView

            holderFooter.mTextViewComments.setText("" + (numberComments==0 ? mContext.getString(R.string.text_none) : numberComments));

        }
        else if (holder instanceof ViewHolderItem) {
            ViewHolderItem holderItem = (ViewHolderItem) holder;
            // get the dataItem item for the position
            final LocalInterviewQuestionData dataItem = getItem(position);

            //initialize the views with the data from the correspondent ArrayList
            holderItem.mTextViewQuestion.setText("" + (position + 1) + ". " + dataItem.getQuestion());
            holderItem.mTextViewDate.setText(dataItem.getDateDay() + "." + dataItem.getDateMonth() + "." + dataItem.getDateYear());
            holderItem.mTextViewLength.setText(Helper.getLengthStringFromMiliseconds(Long.parseLong(dataItem.getLength())));

            /*
            if (dataItem.getPictureLocalURI() != null) {
                if (Uri.parse(dataItem.getPictureLocalURI()) != null) {
                    holderItem.mImageViewMediaCover.setImageURI(Uri.parse(dataItem.getPictureLocalURI()));
                    //if the interview got recorded as audio, the mediaCover will show the narrator profile picture in black/white and darkened
                    if (dataItem.getMedium().equals("audio")) {
                        holderItem.mImageViewMediaCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        ColorMatrix matrix = new ColorMatrix();
                        matrix.setSaturation(0);
                        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                        holderItem.mImageViewMediaCover.setColorFilter(filter);
                        holderItem.mImageViewMediaCoverForeground.setVisibility(View.VISIBLE);
                    }
                }
            }
*/
            mImageLoader.load(holderItem.mImageViewMediaCover, dataItem.getPictureURL());
            if (dataItem.getMedium().equals("audio")) {
                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation(0);
                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                holderItem.mImageViewMediaCover.setColorFilter(filter);
                holderItem.mImageViewMediaCoverForeground.setVisibility(View.VISIBLE);
            }


            //initialize TagView
            int n = dataItem.getTags().size();
            holderItem.mTextViewTags.setText("" + (n == 0 ? mContext.getString(R.string.text_none) : n) + " " + (n == 1 ? mContext.getString(R.string.text_tag) : mContext.getString(R.string.text_tags)));

            Iterator iterator = dataItem.getTags().keySet().iterator();
            for (int i = 0; i < dataItem.getTags().size(); i++) {
                //create new TagView, set the tag, set an OnClickListener to Delete-Button and add TagView to scrollView
                final TagView tagView = new TagView(mContext);
                tagView.setTag((String) iterator.next());

                tagView.getButtonDeleteTag().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dataItem.getTags().remove(tagView.getTextViewTag().getText().toString());
                        Log.e(TAG, "Tag zum löschen: " + tagView.getTextViewTag().getText().toString());
                        notifyDataSetChanged();
                    }
                });

                holderItem.mLayoutScrollViewTags.addView(tagView);
            }

//            //set an OnClickListener for add tag button //TODO löschen
//            holder.mButtonAddTag.setOnClickListener(); //TODO löschen

            holderItem.bindClickListener(dataItem);
        }
    }

}
