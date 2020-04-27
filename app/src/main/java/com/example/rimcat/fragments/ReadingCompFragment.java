package com.example.rimcat.fragments;

import android.content.res.AssetFileDescriptor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.example.rimcat.MainActivity;
import com.example.rimcat.R;
import java.io.IOException;

public class ReadingCompFragment extends QuestionFragment {

    private static final String TAG = "ReadingCompFragment";
    /**
     * 0 = Yes or No (Two Question Group)
     * 1 = Four Question Group
     * 2 = Fill in the Blank
     */
    private static final int[] QUESTION_TYPE = new int[] {
            1, 0, 0, 1, 0, 1, 1, 1, 1
    };
    private static final int[] READING_COMP_SECTIONS = {
            R.string.reading_comp_prompt_1, R.string.reading_comp_prompt_2, R.string.reading_comp_prompt_3
    };
    private static final int[] READING_COMP_AUDIO = {
            R.raw.reading_comp_1, R.raw.reading_comp_2, R.raw.reading_comp_3
    };
    private CardView    card1, card2;
    private TextView    storyText, questionsText;
    private Button      nextBtn;
    private String[]    questionsArray, answersArray;
    private RadioGroup  twoQuestionGrp, fourQuestionGrp;
    private RadioButton radioButton1, radioButton2, radioButton3, radioButton4;
    private int         reading_comp_sec, questionCount, fourAnswerCount;
    private MediaPlayer storyMedia;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reading_comp, container, false);

        // Initialize all views
        card1 = view.findViewById(R.id.card1);
        card2 = view.findViewById(R.id.card2);
        storyText = view.findViewById(R.id.ready_inst1);
        questionsText = view.findViewById(R.id.reading_comp_question);
        questionsText.setTypeface(null, Typeface.BOLD);
        nextBtn = view.findViewById(R.id.read_next_btn);
        questionsArray = getResources().getStringArray(R.array.reading_comp_questions);
        answersArray = getResources().getStringArray(R.array.reading_comp_answers);
        twoQuestionGrp = view.findViewById(R.id.read_two_ans);
        fourQuestionGrp = view.findViewById(R.id.read_four_ans);
        radioButton1 = view.findViewById(R.id.read_ans1);
        radioButton2 = view.findViewById(R.id.read_ans2);
        radioButton3 = view.findViewById(R.id.read_ans3);
        radioButton4 = view.findViewById(R.id.read_ans4);

        // Set those views with onClick listeners and initial values
        questionsText.setText(questionsArray[questionCount]);
        radioButton1.setText(answersArray[(fourAnswerCount * 4)]);
        radioButton2.setText(answersArray[(fourAnswerCount * 4) + 1]);
        radioButton3.setText(answersArray[(fourAnswerCount * 4) + 2]);
        radioButton4.setText(answersArray[(fourAnswerCount * 4) + 3]);
        card2.setVisibility(View.INVISIBLE);
        twoQuestionGrp.setVisibility(View.INVISIBLE);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Record the data based on current question
                if (QUESTION_TYPE[questionCount] == 0 && twoQuestionGrp.getCheckedRadioButtonId() != -1) {
                    resetRadioGroup(twoQuestionGrp);
                    moveToNextQuestion();
                }
                else if (QUESTION_TYPE[questionCount] == 1 && fourQuestionGrp.getCheckedRadioButtonId() != -1) {
                    resetRadioGroup(fourQuestionGrp);
                    moveToNextQuestion();
                }
            }
        });

//        // Set up media player
        MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                reading_comp_sec++;
                if (storyMedia != null) {
                    storyMedia.reset();
                }
                Log.d(TAG, "onCompletion: Reading comp sec " + reading_comp_sec);
                if (reading_comp_sec < READING_COMP_SECTIONS.length) {
                    storyText.setText(READING_COMP_SECTIONS[reading_comp_sec]);
                    AssetFileDescriptor afd = getActivity().getResources().openRawResourceFd(READING_COMP_AUDIO[reading_comp_sec]);
                    try {
                        storyMedia.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                        storyMedia.setOnCompletionListener(this);
                        storyMedia.prepare();
                        storyMedia.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    if (storyMedia != null) {
                        storyMedia.release();
                    }
                    card1.setVisibility(View.INVISIBLE);
                    card2.setVisibility(View.VISIBLE);
                }
            }
        };
        storyMedia = MediaPlayer.create(getActivity().getApplicationContext(), READING_COMP_AUDIO[reading_comp_sec]);
        storyMedia.setOnCompletionListener(onCompletionListener);
        storyMedia.start();
        cardView = view.findViewById(R.id.read_main_page);
        startAnimation(true);
        logStartTime();

        Log.d(TAG, "onCreateView: " + questionsArray.length);
        Log.d(TAG, "onCreateView: " + QUESTION_TYPE.length);
        return view;
    }

    private void resetRadioGroup(RadioGroup group) {
        int radioButtonID = group.getCheckedRadioButtonId();
        View radioButton = group.findViewById(radioButtonID);
        int idx = group.indexOfChild(radioButton);
        RadioButton r = (RadioButton) group.getChildAt(idx);
        logEndTimeAndData(getActivity().getApplicationContext(), "reading_comp," + r.getText().toString());
        group.clearCheck();
    }

    private void moveToNextQuestion() {
        questionCount++;
        if (questionCount < QUESTION_TYPE.length) {
            questionsText.setText(questionsArray[questionCount]);
            // Prepare view for next question
            if (QUESTION_TYPE[questionCount] == 0) {
                fourQuestionGrp.setVisibility(View.INVISIBLE);
                twoQuestionGrp.setVisibility(View.VISIBLE);
            } else if (QUESTION_TYPE[questionCount] == 1) {
                twoQuestionGrp.setVisibility(View.INVISIBLE);
                fourQuestionGrp.setVisibility(View.VISIBLE);
                fourAnswerCount++;
                radioButton1.setText(answersArray[(fourAnswerCount * 4)]);
                radioButton2.setText(answersArray[(fourAnswerCount * 4) + 1]);
                radioButton3.setText(answersArray[(fourAnswerCount * 4) + 2]);
                radioButton4.setText(answersArray[(fourAnswerCount * 4) + 3]);

            }
        } else {
            ((MainActivity)getActivity()).getFragmentData(null);
        }
    }

    @Override
    public boolean loadDataModel() {
        return true;
    }

    @Override
    public void moveToNextPage() {
        ((MainActivity)getActivity()).addFragment(new InstructionsFragment(), "InstructionsFragment");
    }

    @Override
    public void onDestroy() {
        if (storyMedia != null) {
            storyMedia.release();
            storyMedia = null;
        }
        super.onDestroy();
    }
}
