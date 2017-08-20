package de.griot_app.griot.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

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

    static class ViewHolder {
        public int position;
        public TextView tvTitle;
        public TextView tvDate;
        public TextView tvLength;
        public TextView tvInterviewer;
        public TextView tvNarrator;
        public TextView tvComments;
        public ImageView ivMediaCoverPlaceholder;
        public ImageView ivMediaCover;
        public ProfileImageView pivInterviewer;
        public ProfileImageView pivNarrator;
    }

    public LocalInterviewDataAdapter(Context context, ArrayList<LocalInterviewData> data) {
        super(context, R.layout.listitem_interview, data);
        mContext = context;
        mListData = new ArrayList<>(data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.listitem_interview, null);
            holder = new ViewHolder();

            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_headline);
            holder.tvDate = (TextView) convertView.findViewById(R.id.tv_date);
            holder.tvLength = (TextView) convertView.findViewById(R.id.tv_length);
            holder.tvInterviewer = (TextView) convertView.findViewById(R.id.textView_interviewer);
            holder.tvNarrator = (TextView) convertView.findViewById(R.id.textView_narrator);
            holder.tvComments = (TextView) convertView.findViewById(R.id.textView_comments);
            holder.ivMediaCoverPlaceholder = (ImageView) convertView.findViewById(R.id.iv_mediaCover_placeholder);
            holder.ivMediaCover = (ImageView) convertView.findViewById(R.id.iv_mediaCover);
            holder.pivInterviewer = (ProfileImageView) convertView.findViewById(R.id.piv_interviewer);
            holder.pivNarrator = (ProfileImageView) convertView.findViewById(R.id.piv_narrator);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.position = position;
        holder.tvTitle.setText(mListData.get(position).getTitle());
        holder.tvDate.setText(mListData.get(position).getDate());
        holder.tvLength.setText(mListData.get(position).getLength());
        holder.tvInterviewer.setText(mListData.get(position).getInterviewerName());
        holder.tvNarrator.setText(mListData.get(position).getNarratorName());
        int n = mListData.get(position).getNumberComments();
        holder.tvComments.setText("" + (n==0 ? "keine " : n + " ") + ( n == 1 ? mContext.getString(R.string.text_comment) : mContext.getString(R.string.text_comments)));
        try {
            holder.ivMediaCover.setImageURI(Uri.parse(mListData.get(position).getPictureLocalURI()));
            holder.ivMediaCoverPlaceholder.setVisibility(View.GONE);
            holder.ivMediaCover.setVisibility(View.VISIBLE);
        } catch(Exception e) {}
        try {
            holder.pivInterviewer.getProfileImage().setImageURI(Uri.parse(mListData.get(position).getInterviewerPictureLocalURI()));
        } catch (Exception e) {}
        try {
            holder.pivNarrator.getProfileImage().setImageURI(Uri.parse(mListData.get(position).getNarratorPictureLocalURI()));
        } catch (Exception e) {}

        return convertView;
    }
}
