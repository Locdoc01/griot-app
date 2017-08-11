package de.griot_app.griot.adapter;

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

import de.griot_app.griot.dataclasses.InterviewData;
import de.griot_app.griot.ProfileImageView;
import de.griot_app.griot.R;

/**
 * Created by marcel on 08.08.17.
 */

public class InterviewDataAdapter extends ArrayAdapter<InterviewData> {

    private static final String TAG = InterviewDataAdapter.class.getSimpleName();

    private final Context mContext;

    private ArrayList<InterviewData> mListData;

    static class ViewHolder {
        public int position;
        public TextView tvTitle;
        public TextView tvDate;
        public TextView tvLength;
        public TextView tvInterviewer;
        public TextView tvNarrator;
        public TextView tvComments;
        public ImageView ivMediaCover;
        public ProfileImageView pivInterviewer;
        public ProfileImageView pivNarrator;
    }

    public InterviewDataAdapter(Context context, ArrayList<InterviewData> data) {
        super(context, R.layout.listitem_main_overview, data);
        mContext = context;
        mListData = new ArrayList<>(data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.listitem_main_overview, null);
            holder = new ViewHolder();

            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tvDate = (TextView) convertView.findViewById(R.id.tv_date);
            holder.tvLength = (TextView) convertView.findViewById(R.id.tv_length);
            holder.tvInterviewer = (TextView) convertView.findViewById(R.id.tv_interviewer);
            holder.tvNarrator = (TextView) convertView.findViewById(R.id.tv_narrator);
            holder.tvComments = (TextView) convertView.findViewById(R.id.tv_comments);
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
        holder.tvInterviewer.setText("TODO"); //TODO
        holder.tvNarrator.setText("TODO"); //TODO
        holder.tvComments.setText("TODO"); //TODO
        try {
            holder.ivMediaCover.setImageURI(Uri.parse(mListData.get(position).getPictureLocalURI())); //TODO
        } catch(Exception e) {}
        holder.pivInterviewer.setImagePath("TODO"); //TODO
        holder.pivNarrator.setImagePath("TODO"); //TODO

        return convertView;
    }
}
