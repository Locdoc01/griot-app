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

    private static class ViewHolder {
        //Views, which are shown in every ListView item
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
    }

    //If mShowTags is true, tags for interviewQuestions are visible, can be added and deleted
    private boolean mShowTags = true;

    //Constructor
    public LocalInterviewQuestionDataAdapter(Context context, ArrayList<LocalInterviewQuestionData> data) {
        super(context, 0, data);
    }

    /**
     * Sets mShowTags. If mShowTags is true, tags for interviewQuestions are visible, can be added and deleted.
     * @param mShowTags
     */
    public void setShowTags(boolean mShowTags) { this.mShowTags = mShowTags; }

    //inflates the layout for every ListView item and initializes its views
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final LocalInterviewQuestionData data = getItem(position);

        ViewHolder holder;
        if (convertView==null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listitem_interview_question, parent, false);

            // get references to the objects, which are created during the intflation of the layout xml-file
            holder.mTextViewQuestion = (TextView) convertView.findViewById(R.id.textView_headline);
            holder.mTextViewDate = (TextView) convertView.findViewById(R.id.textView_date);
            holder.mTextViewLength = (TextView) convertView.findViewById(R.id.textView_length);
            holder.mImageViewMediaCover = (ImageView) convertView.findViewById(R.id.imageView_mediaCover);
            holder.mImageViewMediaCoverForeground = (ImageView) convertView.findViewById(R.id.imageView_mediaCover_foreground);
            holder.mButtonOptions = (ImageView) convertView.findViewById(R.id.button_options);
            holder.mTextViewTags = (TextView) convertView.findViewById(R.id.textView_tags);
            holder.mButtonAddTag = (ImageView) convertView.findViewById(R.id.button_add_tag);
            holder.mScrollViewTags = (HorizontalScrollView) convertView.findViewById(R.id.scrollView_tags);
            holder.mLayoutScrollViewTags = (LinearLayout) convertView.findViewById(R.id.layout_scrollView_tags);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //initialize the views with the data from the correspondent ArrayList
        holder.mTextViewQuestion.setText("" + (position+1) + ". " + data.getQuestion());
        holder.mTextViewDate.setText(data.getDateDay() + "." + data.getDateMonth() + "." + data.getDateYear());
        holder.mTextViewLength.setText(Helper.getLengthStringFromMiliseconds(Long.parseLong(data.getLength())));

        if (data.getPictureLocalURI() != null) {
            if (Uri.parse(data.getPictureLocalURI()) != null) {
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

        if (!mShowTags) {
            holder.mTextViewTags.setVisibility(View.GONE);
            holder.mButtonAddTag.setVisibility(View.GONE);
            holder.mLayoutScrollViewTags.setVisibility(View.GONE);
        } else {
            //show Tags, allow to add and delete them
            int n = data.getTags().size();
            holder.mTextViewTags.setText("" + (n == 0 ? getContext().getString(R.string.text_none) : n) + " " + (n == 1 ? getContext().getString(R.string.text_tag) : getContext().getString(R.string.text_tags)));

            Iterator iterator = data.getTags().keySet().iterator();
            for (int i = 0; i < data.getTags().size(); i++) {
                //create new TagView, set the tag, set an OnClickListener to Delete-Button and add TagView to scrollView
                final TagView tagView = new TagView(getContext());
                tagView.setTag((String) iterator.next());

                tagView.getButtonDeleteTag().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        data.getTags().remove(tagView.getTextViewTag().getText().toString());
                        notifyDataSetChanged();
                    }
                });

                holder.mLayoutScrollViewTags.addView(tagView);
            }

            //set an OnClickListener for add tag button
            holder.mButtonAddTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_input, null);

                    final TextView textViewInputDialog = (TextView) dialogView.findViewById(R.id.textView_inputDialog);
                    final EditText editTextInputDialog = (EditText) dialogView.findViewById(R.id.editText_inputDialog);

                    textViewInputDialog.setText(getContext().getString(R.string.dialog_add_tag));
                    editTextInputDialog.setHint(getContext().getString(R.string.hint_add_tag));

                    //Dialog for tag input
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.CustomDialogTheme));
                    alertDialogBuilder.setView(dialogView);
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton(getContext().getString(R.string.button_next),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            final String tag = editTextInputDialog.getText().toString().trim();
                                            data.getTags().put(tag, true);
                                            notifyDataSetChanged();
                                        }
                                    })
                            .setNegativeButton(getContext().getString(R.string.button_cancel),
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
        holder.mButtonOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Show Options", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}
