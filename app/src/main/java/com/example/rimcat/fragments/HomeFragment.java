package com.example.rimcat.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.rimcat.MainActivity;
import com.example.rimcat.R;

public class HomeFragment extends QuestionFragment {
    private static final String TAG = "HomeFragment";
    private EditText inputPatientID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        inputPatientID = view.findViewById(R.id.input_patientID);

        cardView = view.findViewById(R.id.card);
        startAnimation(true);
        nextButtonReady();
        return view;
    }

    @Override
    public boolean loadDataModel() {
        if (inputPatientID.getText().toString().equals(""))
            return false;
        QuestionFragment.PATIENT_ID = inputPatientID.getText().toString();
        return true;
    }

    @Override
    public void moveToNextPage() {
        ((MainActivity)getActivity()).addFragment(new InstructionsFragment(), "InstructionsFragment");
    }

    @Override
    public String getCorrectAnswer() {
        return null;
    }

    @Override
    public String getTriedMicrophone() {
        return null;
    }
}
