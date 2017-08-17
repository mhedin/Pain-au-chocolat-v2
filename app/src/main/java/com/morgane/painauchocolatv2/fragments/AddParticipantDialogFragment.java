package com.morgane.painauchocolatv2.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.morgane.painauchocolatv2.R;

import com.morgane.painauchocolatv2.interfaces.DatabaseManipulation;
import com.morgane.painauchocolatv2.models.Participant;

import io.realm.Realm;

/**
 * The DialogFragment used to add a new participant to the application.
 */
public class AddParticipantDialogFragment extends AppCompatDialogFragment implements View.OnClickListener {

    /**
     * The EditText in which the user can type the name of the new participant.
     */
    private EditText mEditText;

    /**
     * The Realm instance.
     */
    private Realm mRealm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_add_participant, container, false);

        getDialog().setTitle(R.string.add_participant_title);

        view.findViewById(R.id.add_participant_cancel_button).setOnClickListener(this);
        view.findViewById(R.id.add_participant_validate_button).setOnClickListener(this);

        mEditText = (EditText) view.findViewById(R.id.add_participant_name);

        mRealm = Realm.getDefaultInstance();

        return view;
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
}
