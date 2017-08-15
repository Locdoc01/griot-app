package de.griot_app.griot.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import de.griot_app.griot.R;
import de.griot_app.griot.dataclasses.QuestionGroup;
import de.griot_app.griot.dataclasses.TopicCatalog;

/**
 * Created by marcel on 15.08.17.
 */

public class TopicCatalogAdapter extends BaseExpandableListAdapter {

    private static final String TAG = TopicCatalogAdapter.class.getSimpleName();

    private final Context mContext;

    private TextView tvTopic;
    private ImageView btnCheck;
    private TextView tvQuestion;
    private ImageView btnToggle;

    private SparseArray<QuestionGroup> mQuestionGroups;

    public TopicCatalogAdapter(Context context, TopicCatalog catalog) {
        mContext = context;
        mQuestionGroups = catalog.getQuestionGroups();
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_topic, null);
        }
        tvTopic = (TextView) convertView.findViewById(R.id.textView_topic);
        btnCheck = (ImageView) convertView.findViewById(R.id.button_check);

        tvTopic.setText(((QuestionGroup)getGroup(groupPosition)).getTopic());

        if (mQuestionGroups.get(groupPosition).getSelected()) {
            btnCheck.setVisibility(View.VISIBLE);
        } else {
            btnCheck.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mQuestionGroups.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public int getGroupCount() {
        return mQuestionGroups.size();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_question, null);
        }
        tvQuestion = (TextView) convertView.findViewById(R.id.textView_question);
        btnToggle = (ImageView) convertView.findViewById(R.id.button_toggle);

        tvQuestion.setText((String)getChild(groupPosition, childPosition));
        //TODO Toggle-Button setzen, überlegen, wo die Daten herkommen sollen, aus DB oder von lokalem Ort? (muss mit Themenkalatog irgendwie synchronisiert sein))
        return convertView;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mQuestionGroups.get(groupPosition).getQuestions().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return ((QuestionGroup)getGroup(groupPosition)).getQuestions().size();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
