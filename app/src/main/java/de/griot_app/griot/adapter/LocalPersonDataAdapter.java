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

import de.griot_app.griot.views.ProfileImageView;
import de.griot_app.griot.R;
import de.griot_app.griot.dataclasses.LocalPersonData;

/**
 * Created by marcel on 08.08.17.
 */

public class LocalPersonDataAdapter extends ArrayAdapter<LocalPersonData> {

    private static final String TAG = LocalPersonDataAdapter.class.getSimpleName();

    private final Context mContext;

    private ArrayList<LocalPersonData> mListData;

    static class ViewHolder {
        public int position;
        public TextView tvListSeperator;
        public ProfileImageView pivPerson;
        public TextView tvPerson;
        public ImageView btnChoose;
    }

    public LocalPersonDataAdapter(Context context, ArrayList<LocalPersonData> data) {
        super(context, R.layout.listitem_person, data);
        mContext = context;
        mListData = new ArrayList<>(data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.listitem_person, null);
            holder = new ViewHolder();

            holder.tvListSeperator = (TextView) convertView.findViewById(R.id.textView_list_seperator);
            holder.pivPerson = (ProfileImageView) convertView.findViewById(R.id.piv_person);
            holder.tvPerson = (TextView) convertView.findViewById(R.id.textView_person);
            holder.btnChoose = (ImageView) convertView.findViewById(R.id.button_choose);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.position = position;
        if (!mListData.get(position).getCategory().equals(null)) {  //TODO: Prüfung auf null ok?
            holder.tvListSeperator.setVisibility(View.VISIBLE);
            holder.tvListSeperator.setText(mListData.get(position).getCategory());
        } else {
            holder.tvListSeperator.setVisibility(View.GONE);
        }
        try {
            holder.pivPerson.getProfileImage().setImageURI(Uri.parse(mListData.get(position).getPictureLocalURI()));
        } catch (Exception e) {}
        holder.tvPerson.setText(mListData.get(position).getFirstname() + " " + mListData.get(position).getLastname());
        if (true) {
            holder.btnChoose.setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}
