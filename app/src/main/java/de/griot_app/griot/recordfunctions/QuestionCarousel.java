package de.griot_app.griot.recordfunctions;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import de.griot_app.griot.R;

/**
 * QuestionCarousel is a View that shows an animated and scrollable carousel of TextViews based on a given List of Strings
 */
public class QuestionCarousel extends FrameLayout implements View.OnTouchListener{

    private static final String TAG = QuestionCarousel.class.getSimpleName();

    private Context mContext;

    private String[] mListStrings;
    private List<CarouselTextView> mListCarousel;
    private int mQuestionCount;

    private ImageView mShadowTop;
    private ImageView mShadowTopLine;
    private ImageView getmShadowMiddle;
    private ImageView mShadowBottom;
    private ImageView mShadowBottomLine;

    //private FrameLayout mlayoutQuestions;

    private float mRegularHeightMiddle;
    private float mRegularHeightTopBottom;
    private int mCurrentQuestion;
    private boolean mTouchInProgress;
    private boolean mAnimationInProgress;
    private float mTouchStart;
    private float mDensity;

    private ObjectAnimator mAnimatorFadeOutTop;
    private ObjectAnimator mAnimatorFadeOutBottom;
    private ObjectAnimator mAnimatorFadeInTop;
    private ObjectAnimator mAnimatorFadeInBottom;

    private ObjectAnimator mAnimatorTextSizeMiddleToTop;
    private ObjectAnimator mAnimatorTextSizeMiddleToBottom;
    private ObjectAnimator mAnimatorTextSizeTopToMiddle;
    private ObjectAnimator mAnimatorTextSizeBottomToMiddle;

    private ObjectAnimator mAnimatorYMiddleToTop;
    private ObjectAnimator mAnimatorYMiddleToBottom;
    private ObjectAnimator mAnimatorYTopToMiddle;
    private ObjectAnimator mAnimatorYBottomToMiddle;

    private AnimatorSet mAnimatorSetMiddleToTop;
    private AnimatorSet mAnimatorSetMiddleToBottom;
    private AnimatorSet mAnimatorSetTopToMiddle;
    private AnimatorSet mAnimatorSetBottomToMiddle;

    private AnimatorSet mAnimatorSetNextQuestion;
    private AnimatorSet mAnimatorSetLastQuestion;

    private long mDuration;

    private int mFirstShownQuestion = 0;

    private int mRecordIndex;

    private boolean mInvertedLayout;


    /**
     * Constructor
     * @param context   The holding activity
    //* @param list      A List of Strings. This will be the data model for the view
     */

    // All three constructors are necessary in order to create an object of this class from a layout, so that it could be found by findViewbyid()
    public QuestionCarousel(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public QuestionCarousel(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public QuestionCarousel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }



    /**
     * Initializations to be performed in constructurs. Therfore this method has to be called from constructors.
     */
    private void init() {
        View v = LayoutInflater.from(mContext).inflate(R.layout.class_question_carousel, this);
        //mlayoutQuestions = (FrameLayout) v.findViewById(R.id.layout_questions);

        mShadowTop = (ImageView) v.findViewById(R.id.question_shadow_top);
        mShadowTopLine = (ImageView) v.findViewById(R.id.question_shadow_top_line);
        getmShadowMiddle = (ImageView) v.findViewById(R.id.question_shadow_middle);
        mShadowBottom = (ImageView) v.findViewById(R.id.question_shadow_bottom);
        mShadowBottomLine = (ImageView) v.findViewById(R.id.question_shadow_bottom_line);

        //TODO: Fall abfangen, dass keine Liste hinzugefügt wurde
        mCurrentQuestion = -1;
        mDensity = getResources().getDisplayMetrics().density;
        mDuration = 200;
        mRecordIndex = -1;
        mInvertedLayout = false;

        Log.d(TAG, "setQuestionList: getChildCount: " + getChildCount());

        // Since setting coordinates only takes effect after the layout was created, this has to be done by the onPreDraw() method from onPreDrawListener

        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            // called when layout is created. The measurements of the view and layout dimensions are available by then
            @Override
            public boolean onPreDraw() {
                Log.d(TAG, "init: OnPreDrawListener: OnPreDraw: ");
                // removes the Listener again
                getViewTreeObserver().removeOnPreDrawListener(this);

                mShadowTop.setY(0);
                mShadowTopLine.setY(getResources().getDimension(R.dimen.carouselShadowTopHeight) - 1);
                getmShadowMiddle.setY(getResources().getDimension(R.dimen.carouselShadowTopHeight));
                mShadowBottom.setY(getResources().getDimension(R.dimen.carouselLayoutHeight) - getResources().getDimension(R.dimen.carouselTextViewSpaceHeight));
                mShadowBottomLine.setY(mShadowBottom.getY());

                return true;
            }
        });

    }


    /**
     * Sets a new question list. The old one together with their questions recording states will be removed.
     * @param list  The new question list
     */
    public void setQuestionList(String[] list) {
        mListStrings = list;
        mQuestionCount = mListStrings.length;
        mCurrentQuestion = 0;

        initiateCarousel();
    }

    public void setFirstShownQuestion(int index) {
        mFirstShownQuestion = index;
        for ( int i=0 ; i<index ; i++) {
            animateUpward();
        }
    }

    /**
     * Sets only the middle background shadow visible.
     */
    public void setInvertedLayout() {
        mInvertedLayout = true;
        mShadowTop.setVisibility(GONE);
        getmShadowMiddle.setVisibility(VISIBLE);
        mShadowBottom.setVisibility(GONE);
        mListCarousel.get(mCurrentQuestion).setShadowLayer(0.0f, 0.0f, 0.0f, Color.BLACK);
    }

    /**
     * Sets the top and bottom background shadow visible.
     */
    public void setNormalLayout() {
        mInvertedLayout = false;
        mShadowTop.setVisibility(VISIBLE);
        getmShadowMiddle.setVisibility(GONE);
        mShadowBottom.setVisibility(VISIBLE);
        mListCarousel.get(mCurrentQuestion).setShadowLayer(1.6f, 1.5f, 1.3f, Color.BLACK);
    }

    /**
     * Sets a record symbol next to the currently recorded question.
     * @param index     Index of the currently recorded question
     */
    public void setRecordOn(int index) {
        if (index==mRecordIndex || index<0) {
            return;
        }
        if (index >= 0 && index < mListCarousel.size()) {
            setRecordOff();
            mRecordIndex = index;

            Drawable draw = getResources().getDrawable(R.drawable.circle_red);
            int size = (int) (getResources().getDimension(R.dimen.textSizeLastNextQuestion) * mDensity);
            draw.setBounds(0, 0, size, size);
            mListCarousel.get(mRecordIndex).setCompoundDrawables(draw, null, null, null);
            mListCarousel.get(mRecordIndex).setCompoundDrawablePadding(size / 2);
        }

    }

    /**
     * Removes the record symbol from the question that was just recorded and sign that question as recordet.
     * The appearance of the recorded sign is dependant on the finished mode.
     */
    public void setRecordOff() {
        if (mRecordIndex >= 0) {
            mListCarousel.get(mRecordIndex).setCompoundDrawables(null, null, null, null);
            mListCarousel.get(mRecordIndex).setShadowLayer(0.0f, 0.0f, 0.0f, Color.BLACK);
            mListCarousel.get(mRecordIndex).setTextColor(ContextCompat.getColor(mContext, R.color.colorGriotBlue));
            mListCarousel.get(mRecordIndex).setRecorded(true);
            mRecordIndex = -1;
        }
    }


    /**
     * Returns the index of the current middle TextView.
     * @return index of current middle TextView
     */
    public int getCurrentIndex() { return mCurrentQuestion; }

    @Override
    public boolean onTouch(View view, MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d(TAG, "onTouch: " + event.getAction() + " , mTouchStart: " + mTouchStart + " , getY: " + event.getY() + " , mTouchInProgress: " + mTouchInProgress);
            mTouchStart = event.getY();
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (!mTouchInProgress && !mAnimationInProgress) {
                if (mTouchStart +15.0f* mDensity < event.getY()) {
                    Log.d(TAG, "onTouch: Move Down!: " + event.getAction() + " , mTouchStart: " + mTouchStart + " , getY: " + event.getY() + " , mTouchInProgress: " + mTouchInProgress);
                    mTouchInProgress = true;
                    animateDownward();
                } else if (mTouchStart -15.0f* mDensity > event.getY()) {
                    Log.d(TAG, "onTouch: Move Up!: " + event.getAction() + " , mTouchStart: " + mTouchStart + " , getY: " + event.getY() + " , mTouchInProgress: " + mTouchInProgress);
                    mTouchInProgress = true;
                    animateUpward();
                } else {
                    return false;
                }
                return true;
            }
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.d(TAG, "onTouch: " + event.getAction() + " , mTouchStart: " + mTouchStart + " , getY: " + event.getY() + ", mTouchInProgress: " + mTouchInProgress);
            mTouchInProgress = false;
            return true;
        }

        return false;
    }


    /**
     * Returns the top y-position in the carousel for the given TextView.
     * Usually that is the TextView, that will next be moved to the top.
     * @param ctv   The CarouselTextView which is to be placed at the top
     * @return      y-position in pixels
     */
    private float getTopPosition(final CarouselTextView ctv) {
        Log.d(TAG, "getTopPosition: ctv.getTextSize: " + ctv.getTextSize()/mDensity + " , textSizeCurrentQuestion: " + (int)getResources().getDimension(R.dimen.textSizeCurrentQuestion));

        float carouselHeight = getResources().getDimension(R.dimen.carouselTextViewSpaceHeight) * 3;
        float topShadowHeightOffset = getResources().getDimension(R.dimen.carouselShadowTopHeight) - getResources().getDimension(R.dimen.carouselTextViewSpaceHeight);

        if (ctv.getLines() == 1) {
            return topShadowHeightOffset + carouselHeight / 3 - mRegularHeightTopBottom / 2 * 3;
        } else {
            return topShadowHeightOffset + carouselHeight / 3 - ctv.getHeight() / 4 * 5;
        }
    }


    /**
     * Returns the middle y-position in the carousel for the given TextView.
     * Usually that is the TextView, that will next be moved to the middle or the one, that is placed at the middle initially.
     * @param ctv   The CarouselTextView which is to be placed at the middle
     * @return      y-position in pixels
     */
    private float getMiddlePosition(CarouselTextView ctv) {
        Log.d(TAG, "getMiddlePosition: ctv.getTextSize: " + ctv.getTextSize()/mDensity + " , textSizeLastNextQuestion: " + (int)getResources().getDimension(R.dimen.textSizeLastNextQuestion));

        float carouselHeight = getResources().getDimension(R.dimen.carouselTextViewSpaceHeight) * 3;
        float topShadowHeightOffset = getResources().getDimension(R.dimen.carouselShadowTopHeight) - getResources().getDimension(R.dimen.carouselTextViewSpaceHeight);

        if (!ctv.wasResized()) {
            return topShadowHeightOffset + carouselHeight / 2 - mRegularHeightMiddle / 2;
        } else {
            return topShadowHeightOffset + carouselHeight / 2 - ctv.getHeight() / 2;
        }
    }


    /**
     * Returns the bottom y-position in the carousel for the given TextView.
     * Usually that is the TextView, that will next be moved to the bottom or the one, that is placed at the bottom initially.
     * @param ctv   The CarouselTextView which is to be placed at the bottom
     * @return      y-position in pixels
     */
    private float getBottomPosition(CarouselTextView ctv) {
        Log.e(TAG, "getBottomPosition: ctv.getTextSize: " + ctv.getTextSize()/mDensity + " , textSizeCurrentQuestion: " + (int)getResources().getDimension(R.dimen.textSizeCurrentQuestion));
        Log.e(TAG, "getLines: " + ctv.getLines() + " , ctv.getHeight: " + ctv.getHeight());

        float carouselHeight = getResources().getDimension(R.dimen.carouselTextViewSpaceHeight) * 3;
        float topShadowHeightOffset = getResources().getDimension(R.dimen.carouselShadowTopHeight) - getResources().getDimension(R.dimen.carouselTextViewSpaceHeight);

        if (ctv.getLines() == 1) {
            return topShadowHeightOffset + carouselHeight / 3 * 2 + mRegularHeightTopBottom / 2;
        } else {
            return topShadowHeightOffset + carouselHeight / 3 * 2 + ctv.getHeight() / 5;
        }
    }


    /**
     * Returns the current index for the three shown TextViews respectively for the next invisible TextViews
     * @return index of the TextView
     */
    private int getNextTopIndex() { return mCurrentQuestion-2; }
    private int getTopIndex() { return mCurrentQuestion-1; }
    private int getMiddleIndex() { return mCurrentQuestion; }
    private int getBottomIndex() { return mCurrentQuestion+1; }
    private int getNextBottomIndex() { return mCurrentQuestion+2; }


    /**
     * Returns the TextViews at the specific position
     * @return  The CarouselTextView at the position
     */
    private CarouselTextView getNextTopTextView() { return mListCarousel.get(getNextTopIndex()); }
    private CarouselTextView getTopTextView() { return mListCarousel.get(getTopIndex()); }
    private CarouselTextView getMiddleTextView() { return mListCarousel.get(getMiddleIndex()); }
    private CarouselTextView getBottomTextView() { return mListCarousel.get(getBottomIndex()); }
    private CarouselTextView getNextBottomTextView() { return mListCarousel.get(getNextBottomIndex()); }


    /**
     * Initializes the Carousel and its TextViews
     *
     * This method has to be called once in constructor and after setting a new question list
     */
    private void initiateCarousel() {
        Log.d(TAG, "initiateCarousel: ");

        mListCarousel = new ArrayList<>();

        //mlayoutQuestions.removeAllViews();

        // if a new QuestionList was set, the old TextViews will be removed before the new ones are added.
        // since the first child of the layout is the layout itself, the childs beginning from
        // index 1 will be removed)
        Log.d(TAG, "getChildCount: " + getChildCount());
        if (getChildCount()>1) {
            removeViews(1, getChildCount()-1);
        }
//        removeAllViews();

        for ( int i=0 ; i<mQuestionCount ; i++ ) {
            //creates CarouselTextViews based on the Strings in mListStrings and add them to mListCarousel as well as to mLayoutQuestions
            CarouselTextView tv = new CarouselTextView(mContext);
            tv.setText(mListStrings[i]);
            mListCarousel.add(tv);

            //mlayoutQuestions.addView(tv);
            addView(tv);

            // sets TextView dimension and layout-alignment preferences
            LayoutParams params = new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT );
            params.gravity = Gravity.CENTER;
            tv.setLayoutParams(params);

            if (i == getMiddleIndex()) {
                // sets initial attributes to first TextView
                tv.setAlpha(1.0f);
                tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorGriotWhite));
                tv.setTextSize(getResources().getDimension(R.dimen.textSizeCurrentQuestion));
                tv.setTypeface(Typeface.DEFAULT_BOLD);
                if (!mInvertedLayout) {
                    // set shadow for better readability
                    tv.setShadowLayer(1.6f, 1.5f, 1.3f, Color.BLACK);
                }
            } else {
                if (i == getBottomIndex()) {
                    // sets second TextView initially visible
                    tv.setAlpha(1.0f);
                } else {
                    // sets all other TextViews initially invisible
                    tv.setAlpha(0.0f);
                }
                //sets initial attribtes to all other TextViews except first one
                tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorLastNextQuestion));
                tv.setTextSize(getResources().getDimension(R.dimen.textSizeLastNextQuestion));
                tv.setTypeface(Typeface.DEFAULT);
            }
        }
        // The heights of the TextViews (which are textSize-dependent) are needed for setting the right TextView positions.
        // Because this heights are unknown (since TextViews add an unpredictable vertical padding) until the layout was created
        // it's necessary to put the calculations for the initial positions into an OnPreDrawListener.

        //mlayoutQuestions.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            // called when the layout is created. The measurements of TextView dimensions are available by then
            @Override
            public boolean onPreDraw() {
                Log.d(TAG, "initiateCarousel: OnPreDrawListener: OnPreDraw: ");
                // removes the Listener again
                //mlayoutQuestions.getViewTreeObserver().removeOnPreDrawListener(this);
                getViewTreeObserver().removeOnPreDrawListener(this);

                //asigns initial positions to TextViews
                for (int i=0 ; i<mQuestionCount ; i++) {

                    if (i==getMiddleIndex()) {
                        mRegularHeightMiddle = mListCarousel.get(i).getHeight();
                        mListCarousel.get(i).setY(getMiddlePosition(mListCarousel.get(i)));
                    } else {
                        if (i==getBottomIndex()) {
                            mRegularHeightTopBottom = mListCarousel.get(i).getHeight();
                        }
                        mListCarousel.get(i).setY(getBottomPosition(mListCarousel.get(i)));
                    }
                }
                setAnimators();
                //mlayoutQuestions.setOnTouchListener(QuestionCarousel.this);
                setOnTouchListener(QuestionCarousel.this);
                return true;
            }
        });
    }


    /**
     * Sets up all single ObjectAnimators and minor AnimatorSets to animate the visible TextViews.
     *
     * This method has to be called after every animation.
     */
    private void setAnimators() {
        Log.d(TAG, "setAnimators: ");
        // there is only a question at the top, if current question index is > 0
        if (mCurrentQuestion > 0) {
            mAnimatorFadeOutTop = ObjectAnimator.ofFloat(getTopTextView(), "alpha", 1.0f, 0.0f);

            mAnimatorSetTopToMiddle = new AnimatorSet();
            mAnimatorYTopToMiddle = ObjectAnimator.ofFloat(getTopTextView(), "y", getMiddlePosition(getTopTextView()));
            //if the textSize of the TextView has been resized to fit the TextView into the carousel its size will be fixed so that the animator for textSize is not needed
            if (!getTopTextView().wasResized()) {
                mAnimatorTextSizeTopToMiddle = ObjectAnimator.ofFloat(
                        getTopTextView(), "textSize",
                        getResources().getDimension(R.dimen.textSizeLastNextQuestion),
                        getResources().getDimension(R.dimen.textSizeCurrentQuestion)
                );
                mAnimatorSetTopToMiddle
                        .play(mAnimatorTextSizeTopToMiddle)
                        .with(mAnimatorYTopToMiddle);
            } else {
                mAnimatorSetTopToMiddle
                        .play(mAnimatorYTopToMiddle);
            }
        }
        // there is only a question at the bottom, if currend question index is < mListCarousel.size();
        if (mCurrentQuestion < mQuestionCount-1) {
            mAnimatorFadeOutBottom = ObjectAnimator.ofFloat(getBottomTextView(), "alpha", 1.0f, 0.0f);

            mAnimatorSetBottomToMiddle = new AnimatorSet();
            mAnimatorYBottomToMiddle = ObjectAnimator.ofFloat(getBottomTextView(), "y", getMiddlePosition(getBottomTextView()));
            // see comment above
            if (!getBottomTextView().wasResized()) {
                mAnimatorTextSizeBottomToMiddle = ObjectAnimator.ofFloat(
                        getBottomTextView(), "textSize",
                        getResources().getDimension(R.dimen.textSizeLastNextQuestion),
                        getResources().getDimension(R.dimen.textSizeCurrentQuestion)
                );
                mAnimatorSetBottomToMiddle
                        .play(mAnimatorTextSizeBottomToMiddle)
                        .with(mAnimatorYBottomToMiddle);
            } else {
                mAnimatorSetBottomToMiddle
                        .play(mAnimatorYBottomToMiddle);
            }
        }

        // there is only a invisible question to fade in at the top, if current question index is > 1
        if (mCurrentQuestion > 1) {
            mAnimatorFadeInTop = ObjectAnimator.ofFloat(getNextTopTextView(), "alpha", 0.0f, 1.0f);
        }

        // there is only a invisible question to fade in at the bottom, if current question index is < mListCarousel.size()
        if (mCurrentQuestion < mQuestionCount-2) {
            mAnimatorFadeInBottom = ObjectAnimator.ofFloat(getNextBottomTextView(), "alpha", 0.0f, 1.0f);
        }

        mAnimatorSetMiddleToTop = new AnimatorSet();
        mAnimatorYMiddleToTop = ObjectAnimator.ofFloat(getMiddleTextView(), "y", getTopPosition(getMiddleTextView()));
        //see comment above
        if (!getMiddleTextView().wasResized()) {
            mAnimatorTextSizeMiddleToTop = ObjectAnimator.ofFloat(
                    getMiddleTextView(), "textSize",
                    getResources().getDimension(R.dimen.textSizeCurrentQuestion),
                    getResources().getDimension(R.dimen.textSizeLastNextQuestion)
            );
            mAnimatorSetMiddleToTop
                    .play(mAnimatorTextSizeMiddleToTop)
                    .with(mAnimatorYMiddleToTop);
        } else {
            mAnimatorSetMiddleToTop
                    .play(mAnimatorYMiddleToTop);
        }

        mAnimatorSetMiddleToBottom = new AnimatorSet();
        mAnimatorYMiddleToBottom = ObjectAnimator.ofFloat(getMiddleTextView(), "y", getBottomPosition(getMiddleTextView()));
        //see comment above
        if (!getMiddleTextView().wasResized()) {
            mAnimatorTextSizeMiddleToBottom = ObjectAnimator.ofFloat(
                    getMiddleTextView(), "textSize",
                    getResources().getDimension(R.dimen.textSizeCurrentQuestion),
                    getResources().getDimension(R.dimen.textSizeLastNextQuestion)
            );
            mAnimatorSetMiddleToBottom
                    .play(mAnimatorTextSizeMiddleToBottom)
                    .with(mAnimatorYMiddleToBottom);
        } else {
            mAnimatorSetMiddleToBottom
                    .play(mAnimatorYMiddleToBottom);
        }

        mAnimatorSetNextQuestion = new AnimatorSet();
        mAnimatorSetNextQuestion.setDuration(mDuration);

        mAnimatorSetLastQuestion = new AnimatorSet();
        mAnimatorSetLastQuestion.setDuration(mDuration);
    }


    /**
     * Sets all attributes of visible TextViews that are not animated
     *
     * This method has to be called after every animation.
     */
    private void setUnanimatedAttributes() {
        Log.d(TAG, "setUnanimatedAttributes: ");
        if (mCurrentQuestion > 0) {
            getTopTextView().setTypeface(Typeface.DEFAULT);
            getTopTextView().setShadowLayer(0.0f, 0.0f, 0.0f, Color.BLACK);
            if (getTopTextView().wasRecorded()) {
                getTopTextView().setTextColor(ContextCompat.getColor(mContext, R.color.colorGriotBlue));
            } else {
                getTopTextView().setTextColor(ContextCompat.getColor(mContext, R.color.colorLastNextQuestion));
            }
        }
        getMiddleTextView().setTypeface(Typeface.DEFAULT_BOLD);
        if (!mInvertedLayout) {
            getMiddleTextView().setShadowLayer(1.6f, 1.5f, 1.3f, Color.BLACK);
        }
        if (getMiddleTextView().wasRecorded()) {
            getMiddleTextView().setShadowLayer(0.0f, 0.0f, 0.0f, Color.BLACK);
            getMiddleTextView().setTextColor(ContextCompat.getColor(mContext, R.color.colorGriotBlue));
        } else {
            getMiddleTextView().setTextColor(ContextCompat.getColor(mContext, R.color.colorGriotWhite));
        }
        if (mCurrentQuestion < mQuestionCount-1) {
            getBottomTextView().setTypeface(Typeface.DEFAULT);
            getBottomTextView().setShadowLayer(0.0f, 0.0f, 0.0f, Color.BLACK);
            if (getBottomTextView().wasRecorded()) {
                getBottomTextView().setTextColor(ContextCompat.getColor(mContext, R.color.colorGriotBlue));
            } else {
                getBottomTextView().setTextColor(ContextCompat.getColor(mContext, R.color.colorLastNextQuestion));
            }
        }
    }


    /**
     * Sets up the major AnimatorSet and performs upward carousel animation
     */
    private void animateUpward() {
        Log.d(TAG, "animateUpward: ");
        if (mCurrentQuestion < mQuestionCount-1) {

            if (mCurrentQuestion == 0) {
                mAnimatorSetNextQuestion
                        .play(mAnimatorSetMiddleToTop)
                        .with(mAnimatorSetBottomToMiddle)
                        .with(mAnimatorFadeInBottom);
            } else if (mCurrentQuestion == mQuestionCount-2) {
                mAnimatorSetNextQuestion
                        .play(mAnimatorFadeOutTop)
                        .with(mAnimatorSetMiddleToTop)
                        .with(mAnimatorSetBottomToMiddle);
            } else {
                mAnimatorSetNextQuestion
                        .play(mAnimatorFadeOutTop)
                        .with(mAnimatorSetMiddleToTop)
                        .with(mAnimatorSetBottomToMiddle)
                        .with(mAnimatorFadeInBottom);
            }
            mAnimatorSetNextQuestion.addListener(new Animator.AnimatorListener() {
                @Override public void onAnimationStart(Animator animation) { mAnimationInProgress = true; }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mCurrentQuestion++;
                    setUnanimatedAttributes();
                    setAnimators();
                    mAnimationInProgress = false;
                }

                @Override public void onAnimationCancel(Animator animation) {}
                @Override public void onAnimationRepeat(Animator animation) {}
            });
            mAnimatorSetNextQuestion.start();
        }
    }


    /**
     * Sets up the major AnimatorSet and performs downward carousel animation
     */
    private void animateDownward() {
        Log.d(TAG, "animateDownward: ");
        if (mCurrentQuestion > 0) {

            if (mCurrentQuestion == mQuestionCount-1) {
                mAnimatorSetLastQuestion
                        .play(mAnimatorSetMiddleToBottom)
                        .with(mAnimatorSetTopToMiddle)
                        .with(mAnimatorFadeInTop);
            } else if (mCurrentQuestion == 1) {
                mAnimatorSetLastQuestion
                        .play(mAnimatorFadeOutBottom)
                        .with(mAnimatorSetMiddleToBottom)
                        .with(mAnimatorSetTopToMiddle);
            }else {
                mAnimatorSetLastQuestion
                        .play(mAnimatorFadeOutBottom)
                        .with(mAnimatorSetMiddleToBottom)
                        .with(mAnimatorSetTopToMiddle)
                        .with(mAnimatorFadeInTop);
            }

            mAnimatorSetLastQuestion.addListener(new Animator.AnimatorListener() {
                @Override public void onAnimationStart(Animator animation) { mAnimationInProgress = true; }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mCurrentQuestion--;
                    setUnanimatedAttributes();
                    setAnimators();
                    mAnimationInProgress = false;
                }

                @Override public void onAnimationCancel(Animator animation) {}
                @Override public void onAnimationRepeat(Animator animation) {}
            });
            mAnimatorSetLastQuestion.start();
        }
    }
}
