package com.example.rimcat;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.TransitionDrawable;
import android.speech.RecognizerIntent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rimcat.dialogs.RecallFinishDialog;
import com.example.rimcat.dialogs.RetryDialog;
import com.example.rimcat.fragments.ComputationFragment;
import com.example.rimcat.fragments.DayOfWeekFragment;
import com.example.rimcat.fragments.DigitSpanFragment;
import com.example.rimcat.fragments.EducationFragment;
import com.example.rimcat.fragments.FigureSelectFragment;
import com.example.rimcat.fragments.FigureStudyFragment;
import com.example.rimcat.fragments.FinishFragment;
import com.example.rimcat.fragments.HomeFragment;
import com.example.rimcat.fragments.ImageNameFragment;
import com.example.rimcat.fragments.InstructionsFragment;
import com.example.rimcat.fragments.QuestionFragment;
import com.example.rimcat.fragments.ReadCompStoryFragment;
import com.example.rimcat.fragments.ReadCompTestFragment;
import com.example.rimcat.fragments.VerbalRecallFragment;
import com.example.rimcat.fragments.SeasonFragment;
import com.example.rimcat.fragments.SemanticChoiceFragment;
import com.example.rimcat.fragments.SemanticRelatednessFragment;
import com.example.rimcat.fragments.TodayDateFragment;
import com.example.rimcat.fragments.VerbalLearningFragment;
import com.example.rimcat.fragments.VerbalRecognitionFragment;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements RetryDialog.RetryDialogListener, RecallFinishDialog.RecallFinishDialogListener {
    private static final String     TAG = "MainActivity";
    private static final int        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1400;
    private static final int        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1401;
    private static final int        MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1402;
    private static final int        RESULT_SPEECH = 65676;
    private static final int        BACKGROUND_TRANSITION_TIME = 2000;
    private static final int        NUM_SCREENS = 43;
    private FragmentManager         fragmentManager;
    private FragmentTransaction     fragmentTransaction;
    private String                  fragmentTag;
    private int                     viewNumber = 0;
    private ConstraintLayout        appBackground;
    private FloatingActionButton    nextButton;
    private TextView                nextText;
    private boolean                 isNextButtonReady;
    private ProgressBar             appProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        appBackground = findViewById(R.id.app_background);

        // Initially change view to home fragment
        fragmentManager = getSupportFragmentManager();
        fragmentTag = "HomeFragment";
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, new HomeFragment(), "HomeFragment");
        fragmentTransaction.commit();

        // Initialize views and model
        nextButton = findViewById(R.id.floatingActionButton);
        nextText = findViewById(R.id.nextText);
        appProgress = findViewById(R.id.app_progress);
        appProgress.setMax(NUM_SCREENS);
    }

    public void getFragmentData(View view) {
        if (isNextButtonReady) {
            isNextButtonReady = false;
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
            }else {
                QuestionFragment fragment = (QuestionFragment) fragmentManager.findFragmentByTag(fragmentTag);
                if (fragment.loadDataModel()) {
                    changeBackground();
                    fragment.startAnimation(false);
                    // Checks to hide or show the Next button
                    viewButtonVisibility();
                } else {
                    Toast.makeText(this, "Please fill out all fields before proceeding.", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    public void addFragment(Fragment nextFragment, String fragmentTag) {
        Log.d(TAG, "addFragment: Moving to new fragment --- " + fragmentTag);
        this.fragmentTag = fragmentTag;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, nextFragment, fragmentTag);
        fragmentTransaction.commit();
    }

    public void changeBackground() {
        TransitionDrawable trans = (TransitionDrawable) appBackground.getBackground();
        if (viewNumber == 0 || viewNumber % 2 == 0)
            trans.startTransition(BACKGROUND_TRANSITION_TIME);
        else
            trans.reverseTransition(BACKGROUND_TRANSITION_TIME);
    }

    public int getViewNumber() {
        return viewNumber;
    }

    public void incrementViewNumber() {
        viewNumber++;
        appProgress.setProgress(viewNumber);
        Log.d(TAG, "incrementViewNumber: View number is " + viewNumber);
    }

    private void viewButtonVisibility() {
        Log.d(TAG, "viewButtonVisibility: View Number: " + viewNumber);
        if (    viewNumber == DataLogModel.INSTRUCTIONS_SCREEN_2 ||
                viewNumber == DataLogModel.INSTRUCTIONS_SCREEN_3 ||
                viewNumber == DataLogModel.INSTRUCTIONS_SCREEN_4 ||
                viewNumber == DataLogModel.INSTRUCTIONS_SCREEN_5 ||
                viewNumber == DataLogModel.INSTRUCTIONS_SCREEN_6 ||
                viewNumber == DataLogModel.INSTRUCTIONS_SCREEN_7 ||
                viewNumber == DataLogModel.INSTRUCTIONS_SCREEN_8 ||
                viewNumber == DataLogModel.INSTRUCTIONS_SCREEN_9 ||
                viewNumber == DataLogModel.INSTRUCTIONS_SCREEN_10 ||
                viewNumber == DataLogModel.INSTRUCTIONS_SCREEN_11 ||
                viewNumber == DataLogModel.INSTRUCTIONS_SCREEN_12 ||
                viewNumber == DataLogModel.INSTRUCTIONS_SCREEN_13 ||
                viewNumber == DataLogModel.INSTRUCTIONS_SCREEN_14 ||
                viewNumber == DataLogModel.INSTRUCTIONS_SCREEN_15 ||
                viewNumber == DataLogModel.INSTRUCTIONS_SCREEN_16 ||
                viewNumber == DataLogModel.INSTRUCTIONS_SCREEN_17 ||
                viewNumber == DataLogModel.VERBAL_LEARNING_SCREEN_1 ||
                viewNumber == DataLogModel.VERBAL_LEARNING_SCREEN_2 ||
                viewNumber == DataLogModel.VERBAL_LEARNING_SCREEN_3 ||
                viewNumber == DataLogModel.VERBAL_LEARNING_SCREEN_4 ||
                viewNumber == DataLogModel.FIGURE_STUDY_SCREEN ||
                viewNumber == DataLogModel.SEMANTIC_RELATEDNESS_SCREEN) {
            nextText.setVisibility(View.INVISIBLE);
            nextButton.hide();
        }
        else if (nextText.getVisibility() == View.INVISIBLE) {
            nextText.setVisibility(View.VISIBLE);
            nextButton.show();
        }
    }

    public void nextButtonReady() {
        isNextButtonReady = true;
    }

    private void debugScreenSelect(int itemID) {
        fragmentTransaction = fragmentManager.beginTransaction();

        switch (itemID) {
            case R.id.screen_home_om:
                this.viewNumber = DataLogModel.HOME_SCREEN;
                fragmentTag = "HomeFragment";
                fragmentTransaction.replace(R.id.container, new HomeFragment(), "HomeFragment");
                break;
            case R.id.screen_inst_1_om:
                this.viewNumber = DataLogModel.INSTRUCTIONS_SCREEN_1;
                fragmentTag = "InstructionsFragment";
                fragmentTransaction.replace(R.id.container, new InstructionsFragment(), "InstructionsFragment");
                break;
            case R.id.screen_education_om:
                this.viewNumber = DataLogModel.EDUCATION_SCREEN;
                fragmentTag = "EducationFragment";
                fragmentTransaction.replace(R.id.container, new EducationFragment(), "EducationFragment");
                break;
            case R.id.screen_date_om:
                this.viewNumber = DataLogModel.TODAYS_DATE_SCREEN;
                fragmentTag = "TodayDateFragment";
                fragmentTransaction.replace(R.id.container, new TodayDateFragment(), "TodayDateFragment");
                break;
            case R.id.screen_day_om:
                this.viewNumber = DataLogModel.DAY_OF_WEEK_SCREEN;
                fragmentTag = "DayOfWeekFragment";
                fragmentTransaction.replace(R.id.container, new DayOfWeekFragment(), "DayOfWeekFragment");
                break;
            case R.id.screen_season_om:
                this.viewNumber = DataLogModel.SEASON_SCREEN;
                fragmentTag = "SeasonFragment";
                fragmentTransaction.replace(R.id.container, new SeasonFragment(), "SeasonFragment");
                break;
            case R.id.screen_inst_2_om:
                this.viewNumber = DataLogModel.INSTRUCTIONS_SCREEN_2;
                fragmentTag = "InstructionsFragment";
                fragmentTransaction.replace(R.id.container, new InstructionsFragment(), "InstructionsFragment");
                break;
            case R.id.screen_img_nm_om:
                this.viewNumber = DataLogModel.IMAGE_NAME_SCREEN;
                fragmentTag = "ImageNameFragment";
                fragmentTransaction.replace(R.id.container, new ImageNameFragment(), "ImageNameFragment");
                break;
            case R.id.screen_inst_3_om:
                this.viewNumber = DataLogModel.INSTRUCTIONS_SCREEN_3;
                fragmentTag = "InstructionsFragment";
                fragmentTransaction.replace(R.id.container, new InstructionsFragment(), "InstructionsFragment");
                break;
            case R.id.screen_verbal_1_om:
                this.viewNumber = DataLogModel.VERBAL_LEARNING_SCREEN_1;
                fragmentTag = "VerbalRecallFragment";
                fragmentTransaction.replace(R.id.container, new VerbalLearningFragment(), "VerbalRecallFragment");
                break;
            case R.id.screen_recall_1_om:
                this.viewNumber = DataLogModel.VERBAL_RECALL_SCREEN_1;
                fragmentTag = "RecallResponseFragment";
                fragmentTransaction.replace(R.id.container, new VerbalRecallFragment(), "RecallResponseFragment");
                break;
            case R.id.screen_inst_4_om:
                this.viewNumber = DataLogModel.INSTRUCTIONS_SCREEN_4;
                fragmentTag = "InstructionsFragment";
                fragmentTransaction.replace(R.id.container, new InstructionsFragment(), "InstructionsFragment");
                break;
            case R.id.screen_verbal_2_om:
                this.viewNumber = DataLogModel.VERBAL_LEARNING_SCREEN_2;
                fragmentTag = "VerbalRecallFragment";
                fragmentTransaction.replace(R.id.container, new VerbalLearningFragment(), "VerbalRecallFragment");
                break;
            case R.id.screen_recall_2_om:
                this.viewNumber = DataLogModel.VERBAL_RECALL_SCREEN_2;
                fragmentTag = "RecallResponseFragment";
                fragmentTransaction.replace(R.id.container, new VerbalRecallFragment(), "RecallResponseFragment");
                break;
            case R.id.screen_inst_5_om:
                this.viewNumber = DataLogModel.INSTRUCTIONS_SCREEN_5;
                fragmentTag = "InstructionsFragment";
                fragmentTransaction.replace(R.id.container, new InstructionsFragment(), "InstructionsFragment");
                break;
            case R.id.screen_verbal_3_om:
                this.viewNumber = DataLogModel.VERBAL_LEARNING_SCREEN_3;
                fragmentTag = "VerbalRecallFragment";
                fragmentTransaction.replace(R.id.container, new VerbalLearningFragment(), "VerbalRecallFragment");
                break;
            case R.id.screen_recall_3_om:
                this.viewNumber = DataLogModel.VERBAL_RECALL_SCREEN_3;
                fragmentTag = "RecallResponseFragment";
                fragmentTransaction.replace(R.id.container, new VerbalRecallFragment(), "RecallResponseFragment");
                break;
            case R.id.screen_inst_6_om:
                this.viewNumber = DataLogModel.INSTRUCTIONS_SCREEN_6;
                fragmentTag = "InstructionsFragment";
                fragmentTransaction.replace(R.id.container, new InstructionsFragment(), "InstructionsFragment");
                break;
            case R.id.screen_verbal_4_om:
                this.viewNumber = DataLogModel.VERBAL_LEARNING_SCREEN_4;
                fragmentTag = "VerbalRecallFragment";
                fragmentTransaction.replace(R.id.container, new VerbalLearningFragment(), "VerbalRecallFragment");
                break;
            case R.id.screen_recall_4_om:
                this.viewNumber = DataLogModel.VERBAL_RECALL_SCREEN_4;
                fragmentTag = "RecallResponseFragment";
                fragmentTransaction.replace(R.id.container, new VerbalRecallFragment(), "RecallResponseFragment");
                break;
            case R.id.screen_inst_7_om:
                this.viewNumber = DataLogModel.INSTRUCTIONS_SCREEN_7;
                fragmentTag = "InstructionsFragment";
                fragmentTransaction.replace(R.id.container, new InstructionsFragment(), "InstructionsFragment");
                break;
            case R.id.screen_recall_5_om:
                this.viewNumber = DataLogModel.VERBAL_RECALL_SCREEN_5;
                fragmentTag = "RecallResponseFragment";
                fragmentTransaction.replace(R.id.container, new VerbalRecallFragment(), "RecallResponseFragment");
                break;
            case R.id.screen_inst_8_om:
                this.viewNumber = DataLogModel.INSTRUCTIONS_SCREEN_8;
                fragmentTag = "InstructionsFragment";
                fragmentTransaction.replace(R.id.container, new InstructionsFragment(), "InstructionsFragment");
                break;
            case R.id.screen_fig_study_om:
                this.viewNumber = DataLogModel.FIGURE_STUDY_SCREEN;
                fragmentTag = "FigureStudyFragment";
                fragmentTransaction.replace(R.id.container, new FigureStudyFragment(), "FigureStudyFragment");
                break;
            case R.id.screen_fig_select_om:
                this.viewNumber = DataLogModel.FIGURE_SELECT_SCREEN;
                fragmentTag = "FigureSelectFragment";
                fragmentTransaction.replace(R.id.container, new FigureSelectFragment(), "FigureSelectFragment");
                break;
            case R.id.screen_inst_9_om:
                this.viewNumber = DataLogModel.INSTRUCTIONS_SCREEN_9;
                fragmentTag = "InstructionsFragment";
                fragmentTransaction.replace(R.id.container, new InstructionsFragment(), "InstructionsFragment");
                break;
            case R.id.screen_digit_span_om:
                this.viewNumber = DataLogModel.DIGIT_SPAN_SCREEN;
                fragmentTag = "DigitSpanFragment";
                fragmentTransaction.replace(R.id.container, new DigitSpanFragment(), "DigitSpanFragment");
                break;
            case R.id.screen_inst_10_om:
                this.viewNumber = DataLogModel.INSTRUCTIONS_SCREEN_10;
                fragmentTag = "InstructionsFragment";
                fragmentTransaction.replace(R.id.container, new InstructionsFragment(), "InstructionsFragment");
                break;
            case R.id.screen_read_comp_story_om:
                this.viewNumber = DataLogModel.READ_COMP_STORY_SCREEN;
                fragmentTag = "ReadingCompFragment";
                fragmentTransaction.replace(R.id.container, new ReadCompStoryFragment(), "ReadingCompFragment");
                break;
            case R.id.screen_inst_11_om:
                this.viewNumber = DataLogModel.INSTRUCTIONS_SCREEN_11;
                fragmentTag = "InstructionsFragment";
                fragmentTransaction.replace(R.id.container, new InstructionsFragment(), "InstructionsFragment");
                break;
            case R.id.screen_computation_om:
                this.viewNumber = DataLogModel.COMPUTATION_SCREEN;
                fragmentTag = "ComputationFragment";
                fragmentTransaction.replace(R.id.container, new ComputationFragment(), "ComputationFragment");
                break;
            case R.id.screen_inst_12_om:
                this.viewNumber = DataLogModel.INSTRUCTIONS_SCREEN_12;
                fragmentTag = "InstructionsFragment";
                fragmentTransaction.replace(R.id.container, new InstructionsFragment(), "InstructionsFragment");
                break;
            case R.id.screen_recall_6_om:
                this.viewNumber = DataLogModel.VERBAL_RECALL_SCREEN_6;
                fragmentTag = "RecallResponseFragment";
                fragmentTransaction.replace(R.id.container, new VerbalRecallFragment(), "RecallResponseFragment");
                break;
            case R.id.screen_inst_13_om:
                this.viewNumber = DataLogModel.INSTRUCTIONS_SCREEN_13;
                fragmentTag = "InstructionsFragment";
                fragmentTransaction.replace(R.id.container, new InstructionsFragment(), "InstructionsFragment");
                break;
            case R.id.screen_verbal_rec_om:
                this.viewNumber = DataLogModel.VERBAL_RECOGNITION_SCREEN;
                fragmentTag = "VerbalRecognitionFragment";
                fragmentTransaction.replace(R.id.container, new VerbalRecognitionFragment(), "VerbalRecognitionFragment");
                break;
            case R.id.screen_inst_14_om:
                this.viewNumber = DataLogModel.INSTRUCTIONS_SCREEN_14;
                fragmentTag = "InstructionsFragment";
                fragmentTransaction.replace(R.id.container, new InstructionsFragment(), "InstructionsFragment");
                break;
            case R.id.screen_semantic_choice_om:
                this.viewNumber = DataLogModel.SEMANTIC_CHOICE_SCREEN;
                fragmentTag = "SemanticChoiceFragment";
                fragmentTransaction.replace(R.id.container, new SemanticChoiceFragment(), "SemanticChoiceFragment");
                break;
            case R.id.screen_inst_15_om:
                this.viewNumber = DataLogModel.INSTRUCTIONS_SCREEN_15;
                fragmentTag = "InstructionsFragment";
                fragmentTransaction.replace(R.id.container, new InstructionsFragment(), "InstructionsFragment");
                break;
            case R.id.screen_fig_select_2_om:
                this.viewNumber = DataLogModel.FIGURE_SELECT_SCREEN_2;
                fragmentTag = "FigureSelectFragment";
                fragmentTransaction.replace(R.id.container, new FigureSelectFragment(), "FigureSelectFragment");
                break;
            case R.id.screen_inst_16_om:
                this.viewNumber = DataLogModel.INSTRUCTIONS_SCREEN_16;
                fragmentTag = "InstructionsFragment";
                fragmentTransaction.replace(R.id.container, new InstructionsFragment(), "InstructionsFragment");
                break;
            case R.id.screen_read_comp_test_om:
                this.viewNumber = DataLogModel.READ_COMP_TEST_SCREEN;
                fragmentTag = "ReadCompTestFragment";
                fragmentTransaction.replace(R.id.container, new ReadCompTestFragment(), "ReadCompTestFragment");
                break;
            case R.id.screen_inst_17_om:
                this.viewNumber = DataLogModel.INSTRUCTIONS_SCREEN_17;
                fragmentTag = "InstructionsFragment";
                fragmentTransaction.replace(R.id.container, new InstructionsFragment(), "InstructionsFragment");
                break;
            case R.id.screen_semantic_relatedness_om:
                this.viewNumber = DataLogModel.SEMANTIC_RELATEDNESS_SCREEN;
                fragmentTag = "SemanticRelatedness";
                fragmentTransaction.replace(R.id.container, new SemanticRelatednessFragment(), "SemanticRelatedness");
                break;
            case R.id.screen_finish_om:
                this.viewNumber = DataLogModel.FINISH_SCREEN;
                fragmentTag = "FinishFragment";
                fragmentTransaction.replace(R.id.container, new FinishFragment(), "FinishFragment");
                break;
        }
        appProgress.setProgress(viewNumber);
        viewButtonVisibility();
        fragmentTransaction.commit();
    }

    public void showRetryDialog() {
        RetryDialog dialog = new RetryDialog();
        dialog.show(getSupportFragmentManager(), "RetryDialog");
    }

    public void showRecallFinishDialog() {
        RecallFinishDialog dialog = new RecallFinishDialog();
        dialog.show(getSupportFragmentManager(), "RecallFinishDialog");
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (this.getCurrentFocus() != null && inputManager != null) {
            inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            inputManager.hideSoftInputFromInputMethod(this.getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onRetryDialogPositiveClick(DialogFragment dialog) {
        VerbalRecallFragment fragment = (VerbalRecallFragment) fragmentManager.findFragmentByTag(fragmentTag);
        fragment.executePostMessageSetup();
    }

    @Override
    public void onFinishDialogPositiveClick(DialogFragment dialog) {
        getFragmentData(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ArrayList<String> speechText = null;

        if (resultCode == RESULT_OK && data != null) {
            speechText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        }
        if (speechText != null) {
            switch (viewNumber) {
                case DataLogModel.VERBAL_RECALL_SCREEN_1:
                case DataLogModel.VERBAL_RECALL_SCREEN_2:
                case DataLogModel.VERBAL_RECALL_SCREEN_3:
                case DataLogModel.VERBAL_RECALL_SCREEN_4:
                case DataLogModel.VERBAL_RECALL_SCREEN_5:
                case DataLogModel.VERBAL_RECALL_SCREEN_6:
                    VerbalRecallFragment verbalRecallFragment = (VerbalRecallFragment) fragmentManager.findFragmentByTag(fragmentTag);
                    verbalRecallFragment.setResponseTextToSpeechText(speechText.get(0));
                    break;
//                case DataLogModel.DIGIT_SPAN_SCREEN:
//                    DigitSpanFragment digitSpanFragment = (DigitSpanFragment) fragmentManager.findFragmentByTag(fragmentTag);
//                    digitSpanFragment.setResponseTextToSpeechText(speechText.get(0));
//                    break;
//                case DataLogModel.COMPUTATION_SCREEN:
//                    ComputationFragment computationFragment = (ComputationFragment) fragmentManager.findFragmentByTag(fragmentTag);
//                    computationFragment.setResponseTextToSpeechText(speechText.get(0));
//                    break;
            }

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            getFragmentData(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        debugScreenSelect(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }
}
