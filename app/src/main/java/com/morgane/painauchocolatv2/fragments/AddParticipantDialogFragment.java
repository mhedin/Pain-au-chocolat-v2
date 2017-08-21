package com.morgane.painauchocolatv2.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.morgane.painauchocolatv2.R;

import com.morgane.painauchocolatv2.interfaces.DatabaseManipulation;
import com.morgane.painauchocolatv2.models.Participant;

import io.realm.Realm;

/**
 * The DialogFragment used to add a new participant to the application.
 */
public class AddParticipantDialogFragment extends AppCompatDialogFragment implements View.OnClickListener,
        ViewTreeObserver.OnGlobalLayoutListener {

    /**
     * The EditText in which the user can type the name of the new participant.
     */
    private EditText mEditText;

    /**
     * The Realm instance.
     */
    private Realm mRealm;

    /**
     * Flag indicating if the keyboard is currently displayed on the screen.
     */
    private boolean mIsKeyboardVisible;

    /**
     * Previous location of the dialog. Used to determine if the keyboard is currently displayed or not, because the dialog
     * moves when the keyboard appears or disappears.
     */
    private int mPreviousDialogVerticalLocation = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_add_participant, container, false);

        getDialog().setTitle(R.string.add_participant_title);

        view.findViewById(R.id.add_participant_cancel_button).setOnClickListener(this);
        view.findViewById(R.id.add_participant_validate_button).setOnClickListener(this);

        mEditText = (EditText) view.findViewById(R.id.add_participant_name);

        mRealm = Realm.getDefaultInstance();

        // Used to know if the keyboard is visible on screen or not
        view.getViewTreeObserver().addOnGlobalLayoutListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Display the keyboard when the dialog appears
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        mIsKeyboardVisible = true;
    }

    @Override
    public void onPause() {
        super.onPause();

        // Hide the keyboard, to prevent from keyboard which stays opened while the application is not displayed on screen anymore
        hideKeyboard();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        // Hide the keyboard when the dialog is closed
        hideKeyboard();
    }

    /**
     * Hide the keyboard if it is still visible.
     */
    private void hideKeyboard() {
        if (mIsKeyboardVisible) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
            mIsKeyboardVisible = false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_participant_cancel_button:
                dismiss();
                break;

            case R.id.add_participant_validate_button:
                String participantName = mEditText.getText().toString();
                // The name of the participant cannot be empty
                if (participantName.isEmpty()) {
                    mEditText.setError(getString(R.string.add_participant_error_empty));

                } else if (Participant.doesParticipantAlreadyExist(participantName, mRealm)) {
                    // The name of the participant cannot be the same as an already existing one
                    mEditText.setError(getString(R.string.add_participant_error_already_exists));
                } else {
                    // Save the new participant and close the dialog
                    ((DatabaseManipulation)getActivity()).insertInDatabase(participantName);
                    dismiss();
                }
                break;
        }
    }

    @Override
    public void onGlobalLayout() {
        int[] dialogLocation = new int[2];
        getView().getLocationOnScreen(dialogLocation);

        if (mPreviousDialogVerticalLocation > dialogLocation[1]) {
            // Keyboard is opened
            mIsKeyboardVisible = true;
        } else {
            // Keyboard is closed
            mIsKeyboardVisible = false;
        }

        mPreviousDialogVerticalLocation = dialogLocation[1];
    }
}
