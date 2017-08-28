package de.griot_app.griot.adapters;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import de.griot_app.griot.perform_interview.ChooseTopicInputActivity;
import de.griot_app.griot.R;
import de.griot_app.griot.dataclasses.LocalQuestionData;
import de.griot_app.griot.dataclasses.LocalTopicData;
import de.griot_app.griot.dataclasses.TopicCatalog;

/**
 * ArrayList-ListView-Adapter, which converts an SparseArray of LocalTopicData-objects into ExpandableListView group items.
 * The LocalTopicData-objects holds ArrayLists of LocalQuestionData-objects, which are converted into ExpandableListView child items.
 * Thus the result is a two-dimensional ListView, which is used fot the topic catalog
 */
//TODO: hide deleted standardTopics and standardFragen (find out, how to delete ListView items depending on the data)

public class TopicCatalogAdapter extends BaseExpandableListAdapter {

    private static final String TAG = TopicCatalogAdapter.class.getSimpleName();

    private final Context mContext;

    //The SparseArray containing the LocalTopicData-objects
    private SparseArray<LocalTopicData> mTopics;

    //Views, which are shown in every ListView item
    private TextView tvTopic;
    private ImageView btnCheck;
    private ImageView btnExpand;
    private TextView tvTitleQuestions;
    private ImageView btnAddQuestion;
    private TextView tvQuestion;
    private ImageView btnToggle;

    //Needed to distinguish between functionality of ChooseTopicInputActivity and MainTopicCatalogActivity
    private boolean topicsCheckable;

    //Constructors

    /**
     * Default-constructor. Topics are not checkable by default.
     * @param context Calling Activity
     * @param catalog Object of TopicCatalog, which holds all data of topics and questions.
     */
    public TopicCatalogAdapter(Context context, TopicCatalog catalog) {
        mContext = context;
        mTopics = catalog.getTopics();
        this.topicsCheckable = false;

    }

    /**
     * Constructor, which allows to set topicsCheckable. If set to true, topics can be selected. Only one topic can be selected at a time.
     * @param context Calling Activity
     * @param catalog Object of TopicCatalog, which holds all data of topics and questions.
     * @param topicsCheckable If set to true, topics can be selected. Only one topic can be selected at a time.
     */
    public TopicCatalogAdapter(Context context, TopicCatalog catalog, boolean topicsCheckable) {
        mContext = context;
        mTopics = catalog.getTopics();
        this.topicsCheckable = topicsCheckable;

    }

    //inflates the layout for every ListView group item and initializes its views
    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.listitem_topic_catalog_topic, null);

        // get references to the objects, which are created during the intflation of the layout xml-file
        tvTopic = (TextView) v.findViewById(R.id.textView_topic);
        btnCheck = (ImageView) v.findViewById(R.id.button_check);
        btnExpand = (ImageView) v.findViewById(R.id.button_expand);

        tvTopic.setText(((LocalTopicData)getGroup(groupPosition)).getTopic());

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

        if (!topicsCheckable) {
            btnCheck.setVisibility(View.GONE);
        } else {
            // if the button is clicked, the appropriate topic gets selected (or unselected)
            btnCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ChooseTopicInputActivity) mContext).buttonCheckClicked(groupPosition);     //TODO works only for ChooseTopicInputActivity
                }
            });
            if (mTopics.get(groupPosition).getSelected()) {
                btnCheck.setImageDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.checkbox_on, null));
            } else {
                btnCheck.setImageDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.checkbox_off, null));
            }
        }

        return v;
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
        return mTopics.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public int getGroupCount() {
        return mTopics.size();
    }

    //inflates the layout for every ListView child item and initializes its views
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View v;
        if (childPosition==0) {
            // head item for question group.
            v = LayoutInflater.from(mContext).inflate(R.layout.listitem_topic_catalog_title_questions, null);

            btnAddQuestion = (ImageView) v.findViewById(R.id.button_add_question);

            btnAddQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "Frage hinzufügen", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            //other items in question group
            v = LayoutInflater.from(mContext).inflate(R.layout.listitem_topic_catalog_question, null);

            tvTitleQuestions = (TextView) v.findViewById(R.id.textView_title_questions);
            tvQuestion = (TextView) v.findViewById(R.id.textView_question);
            btnToggle = (ImageView) v.findViewById(R.id.button_toggle);

            LocalQuestionData child = (LocalQuestionData) getChild(groupPosition, childPosition);

            tvQuestion.setText(child.getQuestion());
            if (child.getQuestionState()==LocalQuestionData.QuestionState.OFF) {
                btnToggle.setImageDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.toggle_off, null));
            } else if (child.getQuestionState()==LocalQuestionData.QuestionState.ON) {
                btnToggle.setImageDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.toggle_on, null));
            } else {
                 //TODO: ability to delete questions
            }
        }
        return v;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mTopics.get(groupPosition).getQuestions().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return ((LocalTopicData)getGroup(groupPosition)).getQuestions().size();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
