package com.example.rimcat.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.rimcat.MainActivity;
import com.example.rimcat.R;

public class KeyboardFragment extends QuestionFragment {

    private static final String     TAG = "RecallResponseFragment";
    private Context mContext;
    private TextView promptText;
    private EditText responseText;
    private Button submitBtn;
    private FloatingActionButton audioBtn;
    int currentScreen = 0;
    boolean wasMicPressed, micPressedLast;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_keyboard, container, false);
        mContext = view.getContext();
        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        responseText = view.findViewById(R.id.keyboard_recall_word);
        audioBtn = view.findViewById(R.id.keyboard_audio_btn);
        submitBtn = view.findViewById(R.id.keyboard_addBtn);
        promptText = view.findViewById(R.id.keyboard_text);

        responseText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                micPressedLast = false;
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (responseText.getText().toString() != null && responseText.getText().toString().toLowerCase().equals("hello"))
                    moveToNextTask();
                else {
                    Toast t = Toast.makeText(mContext, "Make sure that the text box has the word 'hello' in it.", Toast.LENGTH_LONG);
                    ViewGroup group = (ViewGroup) t.getView();
                    TextView toastTV = (TextView) group.getChildAt(0);
                    toastTV.setTextSize(20);
                    t.show();
                }

            }
        });

        // Initialize speech to text button
        audioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                micPressedLast = true;
                wasMicPressed = true;
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    responseText.setText("");
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(mContext,
                            "Oops! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    ViewGroup group = (ViewGroup) t.getView();
                    TextView toastTV = (TextView) group.getChildAt(0);
                    toastTV.setTextSize(20);
                    t.show();
                }
            }
        });
        audioBtn.hide();

        cardView = view.findViewById(R.id.card);
        startAnimation(true);
        logStartTime();
        nextButtonReady();
        return view;
    }

    private void moveToNextTask() {
        if (currentScreen == 0) {
            currentScreen++;
            promptText.setText(R.string.keyboard_prompt2);
            ((MainActivity)getActivity()).hideSoftKeyboard();
            responseText.setText("");
            audioBtn.show();
            vibrateToastAndExecuteSound("hello", true);
        } else if (wasMicPressed) {
            logEndTimeAndData(getActivity().getApplicationContext(), "keyboard_test,null");
            ((MainActivity)getActivity()).getFragmentData(null);
        } else {
            Toast t = Toast.makeText(mContext, "Try using the microphone button at least once.", Toast.LENGTH_LONG);
            ViewGroup group = (ViewGroup) t.getView();
            TextView toastTV = (TextView) group.getChildAt(0);
            toastTV.setTextSize(20);
            t.show();
        }
    }

    public void setResponseTextToSpeechText(String speechText) {
        responseText.setText(speechText);
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
    public String getCorrectAnswer() {
        return "N/A";
    }

    @Override
    public String getTriedMicrophone() {
        return "" + micPressedLast;
    }
}
