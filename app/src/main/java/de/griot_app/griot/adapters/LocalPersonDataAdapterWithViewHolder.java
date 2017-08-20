package de.griot_app.griot.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import de.griot_app.griot.views.ProfileImageView;
import de.griot_app.griot.R;
import de.griot_app.griot.dataclasses.LocalPersonData;

/**
 * Created by marcel on 08.08.17.
 */

public class LocalPersonDataAdapterWithViewHolder extends ArrayAdapter<LocalPersonData> {

    private static final String TAG = LocalPersonDataAdapterWithViewHolder.class.getSimpleName();

    private final Context mContext;

    private ArrayList<LocalPersonData> mListData;

    private int mChecked = -1;

    static class ViewHolder {
        public int position;
        public TextView tvCategory;
        public FrameLayout listSeperator;
        public ProfileImageView pivPerson;
        public TextView tvPerson;
        public ImageView btnCheck;
    }

    public LocalPersonDataAdapterWithViewHolder(Context context, ArrayList<LocalPersonData> data) {
        super(context, R.layout.listitem_contact, data);
        mContext = context;
        mListData = new ArrayList<>(data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.listitem_contact, null);
            holder = new ViewHolder();

            holder.tvCategory = (TextView) convertView.findViewById(R.id.category);
            holder.listSeperator = (FrameLayout) convertView.findViewById(R.id.list_seperator);
            holder.pivPerson = (ProfileImageView) convertView.findViewById(R.id.piv_person);
            holder.tvPerson = (TextView) convertView.findViewById(R.id.textView_person);
            holder.btnCheck = (ImageView) convertView.findViewById(R.id.button_item);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.position = position;
        if (mListData.get(position).getCategory()!=null) {
            holder.listSeperator.setVisibility(View.VISIBLE);
            holder.tvCategory.setText(mListData.get(position).getCategory());
        } else {
            holder.listSeperator.setVisibility(View.GONE);
        }

        //TODO: finde korrekte Position for den ClickListener
        ListView lv = (ListView) parent;
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView btnCheck = (ImageView)view.findViewById(R.id.button_item);
                if (mChecked<0) {
                    btnCheck.setVisibility(View.VISIBLE);
                    mChecked = position;
                } else {
                    if (mChecked==position) {
                        btnCheck.setVisibility(View.GONE);
                        mChecked = -1;
                    } else {
                        parent.getChildAt(mChecked).findViewById(R.id.button_item).setVisibility(View.GONE);
                        btnCheck.setVisibility(View.VISIBLE);
                        mChecked = position;
                    }
                }
            }
        });

        if (mListData.get(position).getPictureLocalURI() != null && mListData.get(position).getPictureLocalURI().equals(mContext.getString(R.string.text_add_guest))) {
            holder.pivPerson.getProfileImage().setImageResource(R.drawable.add_avatar);
            holder.pivPerson.getProfileImagePlus().setVisibility(View.GONE);
            holder.pivPerson.getProfileImageCircle().setVisibility(View.GONE);
        } else {
            try {
                holder.pivPerson.getProfileImage().setImageURI(Uri.parse(mListData.get(position).getPictureLocalURI()));
            } catch (Exception e) {
            }
        }

        holder.tvPerson.setText(mListData.get(position).getFirstname() + " " + mListData.get(position).getLastname());

        return convertView;
    }
}
