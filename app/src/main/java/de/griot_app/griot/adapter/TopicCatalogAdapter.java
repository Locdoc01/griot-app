package de.griot_app.griot.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import de.griot_app.griot.ChooseTopicInputActivity;
import de.griot_app.griot.R;
import de.griot_app.griot.dataclasses.LocalPersonData;
import de.griot_app.griot.dataclasses.LocalQuestionData;
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
    private ImageView btnExpand;
    private TextView tvQuestion;
    private ImageView btnToggle;

    private SparseArray<QuestionGroup> mQuestionGroups;

    public TopicCatalogAdapter(Context context, TopicCatalog catalog) {
        mContext = context;
        mQuestionGroups = catalog.getQuestionGroups();

    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_topic, null);
        }
        tvTopic = (TextView) convertView.findViewById(R.id.textView_topic);
        btnCheck = (ImageView) convertView.findViewById(R.id.button_check);
        btnExpand = (ImageView) convertView.findViewById(R.id.button_expand);

        tvTopic.setText(((QuestionGroup)getGroup(groupPosition)).getTopic());

        //if the button is clicked, the appropriate topic gets expanded or collaped
        final ExpandableListView listview = (ExpandableListView) parent;
        btnExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listview.isGroupExpanded(groupPosition)) {
                    listview.collapseGroup(groupPosition);
                    ((ImageView)v).setImageDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.up, null));
                } else {
                    listview.expandGroup(groupPosition);
                    ((ImageView)v).setImageDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.down, null));
                }
                notifyDataSetChanged();
            }
        });

        // if the button is clicked, the appropriate topic gets selected (or unselected)
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ChooseTopicInputActivity)mContext).buttonCheckClicked(groupPosition);     //TODO Funktioniert nur für ChooseTopicInputActivity
            }
        });

        if (mQuestionGroups.get(groupPosition).getSelected()) {
            btnCheck.setImageDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.checkbox_on, null));
        } else {
            btnCheck.setImageDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.checkbox_off, null));
        }
        return convertView;
    }



    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);

    }

    @Override
    public Object getGroup(int groupPosition) {
        return mQuestionGroups.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public int getGroupCount() {
        return mQuestionGroups.size();
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_question, null);
        }
        tvQuestion = (TextView) convertView.findViewById(R.id.textView_question);
        btnToggle = (ImageView) convertView.findViewById(R.id.button_toggle);

        LocalQuestionData child = (LocalQuestionData) getChild(groupPosition, childPosition);
        tvQuestion.setText(child.getQuestion());
        switch (child.getQuestionState()) {
            case LocalQuestionData.QuestionState.OFF:
                btnToggle.setImageDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.toggle_off, null));
                break;
            case LocalQuestionData.QuestionState.ON:
                btnToggle.setImageDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.toggle_on, null));
                break;
            case LocalQuestionData.QuestionState.GONE:
                //TODO: Möglichkeit, Fragen zu löschen später implementieren
                break;
        }
        //TODO überlegen, wo die QuestionStates persistent gespeichert werden sollen, in DB oder lokalem Ort? (muss mit Themenkalatog irgendwie synchronisiert sein))

        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalQuestionData data = (LocalQuestionData) getChild(groupPosition, childPosition);
                if (data.getQuestionState()==LocalQuestionData.QuestionState.OFF) {
                    ((ImageView)v).setImageDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.toggle_on, null));
                    data.setQuestionState(LocalQuestionData.QuestionState.ON);
                } else if (data.getQuestionState()==LocalQuestionData.QuestionState.ON) {
                    ((ImageView)v).setImageDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.toggle_off, null));
                    data.setQuestionState(LocalQuestionData.QuestionState.OFF);
                }
//                notifyDataSetChanged();
            }
        });
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
