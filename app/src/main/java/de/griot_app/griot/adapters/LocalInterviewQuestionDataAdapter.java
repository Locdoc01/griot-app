package de.griot_app.griot.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

import de.griot_app.griot.Helper;
import de.griot_app.griot.views.TagView;
import de.griot_app.griot.R;
import de.griot_app.griot.dataclasses.LocalInterviewQuestionData;

/**
 * ArrayList-ListView-Adapter, which converts an ArrayList of LocalInterviewQuestionData-objects into ListView items.
 */
public class LocalInterviewQuestionDataAdapter extends ArrayAdapter<LocalInterviewQuestionData> {

    private static final String TAG = LocalInterviewQuestionDataAdapter.class.getSimpleName();

    private final Context mContext;

    //The ArrayList containing the LocalInterviewQuestionData-objects
    private ArrayList<LocalInterviewQuestionData> mListData;

    //If showTags is true, tags for interviewQuestions are visible, can be added and deleted
    private boolean showTags = true;

    //Views, which are shown in every ListView item
    private TextView tvQuestion;
    private TextView tvDate;
    private TextView tvLength;
    private ImageView ivMediaCover;
    private ImageView ivMediaCoverForeground;
    private ImageView btnOptions;
    private TextView tvTags;
    private ImageView btnAddTag;
    private HorizontalScrollView scrollViewTags;
    private LinearLayout layoutScrollViewTags;

    //Constructor
    public LocalInterviewQuestionDataAdapter(Context context, ArrayList<LocalInterviewQuestionData> data) {
        super(context, R.layout.listitem_interview_question, data);
        mContext = context;
        mListData = new ArrayList<>(data);
    }

    /**
     * Sets showTags. If showTags is true, tags for interviewQuestions are visible, can be added and deleted.
     * @param showTags
     */
    public void setShowTags(boolean showTags) { this.showTags = showTags; }

    //inflates the layout for every ListView item and initializes its views
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.listitem_interview_question, null);

        // get references to the objects, which are created during the intflation of the layout xml-file
        tvQuestion = (TextView) v.findViewById(R.id.tv_headline);
        tvDate = (TextView) v.findViewById(R.id.tv_date);
        tvLength = (TextView) v.findViewById(R.id.tv_length);
        ivMediaCover = (ImageView) v.findViewById(R.id.iv_mediaCover);
        ivMediaCoverForeground = (ImageView) v.findViewById(R.id.iv_mediaCover_foreground);
        btnOptions = (ImageView) v.findViewById(R.id.button_options);
        tvTags = (TextView) v.findViewById(R.id.textView_tags);
        btnAddTag = (ImageView) v.findViewById(R.id.button_add_tag);
        scrollViewTags = (HorizontalScrollView) v.findViewById(R.id.scrollView_tags);
        layoutScrollViewTags = (LinearLayout) v.findViewById(R.id.layout_scrollView_tags);

        //initialize the views with the data from the correspondent ArrayList
        final int pos = position;
        tvQuestion.setText("" + (position+1) + ". " + mListData.get(position).getQuestion());
        tvDate.setText(mListData.get(position).getDateDay() + "." + mListData.get(position).getDateMonth() + "." + mListData.get(position).getDateYear());
        tvLength.setText(Helper.getLengthStringFromMiliseconds(Long.parseLong(mListData.get(position).getLength())));

        if (mListData.get(position).getPictureLocalURI() != null) {
            if (Uri.parse(mListData.get(position).getPictureLocalURI()) != null) {
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

        if (!showTags) {
            tvTags.setVisibility(View.GONE);
            btnAddTag.setVisibility(View.GONE);
            layoutScrollViewTags.setVisibility(View.GONE);
        } else {
            //show Tags, allow to add and delete them
            int n = mListData.get(position).getTags().size();
            tvTags.setText("" + (n == 0 ? mContext.getString(R.string.text_none) : n) + " " + (n == 1 ? mContext.getString(R.string.text_tag) : mContext.getString(R.string.text_tags)));

            Iterator iterator = mListData.get(position).getTags().keySet().iterator();
            for (int i = 0; i < mListData.get(position).getTags().size(); i++) {
                //create new TagView, set the tag, set an OnClickListener to Delete-Button and add TagView to scrollView
                final TagView tagView = new TagView(mContext);
                tagView.setTag((String) iterator.next());

                tagView.getButtonDeleteTag().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListData.get(pos).getTags().remove(tagView.getTextViewTag().getText().toString());
                        notifyDataSetChanged();
                    }
                });

                layoutScrollViewTags.addView(tagView);
            }

            //set an OnClickListener for add tag button
            btnAddTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                                            mListData.get(pos).getTags().put(tag, true);
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
                }
            });
        }

        //set an OnClickListener for add tag button
        btnOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Show Options", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
}
