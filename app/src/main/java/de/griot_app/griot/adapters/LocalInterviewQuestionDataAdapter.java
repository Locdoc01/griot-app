package de.griot_app.griot.adapters;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.griot_app.griot.views.TagView;
import de.griot_app.griot.R;
import de.griot_app.griot.dataclasses.LocalInterviewQuestionData;

/**
 * Created by marcel on 08.08.17.
 */

public class LocalInterviewQuestionDataAdapter extends ArrayAdapter<LocalInterviewQuestionData> {

    private static final String TAG = LocalInterviewQuestionDataAdapter.class.getSimpleName();

    private final Context mContext;

    private ArrayList<LocalInterviewQuestionData> mListData;

    static class ViewHolder {
        public int position;
        public TextView tvQuestion;
        public TextView tvDate;
        public TextView tvLength;
        public ImageView ivMediaCoverPlaceholder;
        public ImageView ivMediaCover;
        public ImageView btnOptions;
        public TextView tvTags;
        public ImageView btnAddTag;
        public LinearLayout scrollViewTags;
    }

    public LocalInterviewQuestionDataAdapter(Context context, ArrayList<LocalInterviewQuestionData> data) {
        super(context, R.layout.listitem_interview_question_add_tags, data);
        mContext = context;
        mListData = new ArrayList<>(data);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.listitem_interview_question_add_tags, null);
            holder = new ViewHolder();

            holder.tvQuestion = (TextView) convertView.findViewById(R.id.tv_headline);
            holder.tvDate = (TextView) convertView.findViewById(R.id.tv_date);
            holder.tvLength = (TextView) convertView.findViewById(R.id.tv_length);
            holder.ivMediaCoverPlaceholder = (ImageView) convertView.findViewById(R.id.iv_mediaCover_placeholder);
            holder.ivMediaCover = (ImageView) convertView.findViewById(R.id.iv_mediaCover);
            holder.btnOptions = (ImageView) convertView.findViewById(R.id.button_options);
            holder.tvTags = (TextView) convertView.findViewById(R.id.textView_tags);
            holder.btnAddTag = (ImageView) convertView.findViewById(R.id.button_add_tag);
            holder.scrollViewTags = (LinearLayout) convertView.findViewById(R.id.layout_tags);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.position = position;
        holder.tvQuestion.setText("" + (position+1) + ". " + mListData.get(position).getQuestion());
        holder.tvDate.setText(mListData.get(position).getDateDay() + "." + mListData.get(position).getDateMonth() + "." + mListData.get(position).getDateYear());
        holder.tvLength.setText(mListData.get(position).getLength());

        if (mListData.get(position).getPictureLocalURI() != null) {
            if (Uri.parse(mListData.get(position).getPictureLocalURI()) != null) {
                holder.ivMediaCover.setImageURI(Uri.parse(mListData.get(position).getPictureLocalURI()));
                holder.ivMediaCoverPlaceholder.setVisibility(View.GONE);
                holder.ivMediaCover.setVisibility(View.VISIBLE);
            }
        }

        int n = mListData.get(position).getTags().size();
        holder.tvTags.setText("" + (n==0 ? mContext.getString(R.string.text_none) : n) + " " + ( n == 1 ? mContext.getString(R.string.text_tag) : mContext.getString(R.string.text_tags)));

        holder.btnAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_input, null);

                final TextView textViewInputDialog = (TextView) dialogView.findViewById(R.id.textView_inputDialog);
                final EditText editTextInputDialog = (EditText) dialogView.findViewById(R.id.editText_inputDialog);

                textViewInputDialog.setText(mContext.getString(R.string.dialog_add_tag));
                editTextInputDialog.setHint(mContext.getString(R.string.hint_add_tag));

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(mContext, R.style.CustomDialogTheme));
                // set dialog view
                alertDialogBuilder.setView(dialogView);
                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton(mContext.getString(R.string.button_next),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        final TagView tagView = new TagView(mContext);
                                        final String tag = editTextInputDialog.getText().toString().trim();
                                        tagView.setTag(tag);
                                        tagView.getButtonDeleteTag().setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                holder.scrollViewTags.removeView(tagView);
                                                mListData.get(holder.position).getTags().remove(tag);
                                                notifyDataSetChanged();
                                            }
                                        });
                                        holder.scrollViewTags.addView(tagView);
                                        mListData.get(holder.position).getTags().put(tag, true);
                                    }
                                })
                        .setNegativeButton(mContext.getString(R.string.button_cancel),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create and show alert dialog
                alertDialogBuilder.create().show();
            }
        });

        holder.btnOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Show Options", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}
