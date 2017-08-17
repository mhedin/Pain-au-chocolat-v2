package com.morgane.painauchocolatv2.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.morgane.painauchocolatv2.R;
import com.morgane.painauchocolatv2.interfaces.DatabaseManipulation;
import com.morgane.painauchocolatv2.models.Participant;

/**
 * The DialogFragment in which the information of a participant are displayed, and where the user can
 * modify his bring status, or remove the participant.
 */
public class ShowParticipantInfoDialogFragment extends AppCompatDialogFragment implements View.OnClickListener {

    /**
     * Constant used to register in the arguments the participant selected.
     */
    private static final String ARGUMENTS_PARTICIPANT = "argumentsParticipant";

    /**
     * The participant displayed.
     */
    private Participant participant;

    /**
     * Create a new instance of the DialogFragment, with the given arguments.
     * @param participant The participant to display.
     * @return A new instance of the DialogFragment with the given arguments.
     */
    public static ShowParticipantInfoDialogFragment newInstance(Participant participant) {
        ShowParticipantInfoDialogFragment fragment = new ShowParticipantInfoDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARGUMENTS_PARTICIPANT, participant);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_show_participant_info, container, false);

        participant = getArguments().getParcelable(ARGUMENTS_PARTICIPANT);

        getDialog().setTitle(participant.getName());

        TextView messageTextView = (TextView) view.findViewById(R.id.show_participant_message);
        int messageStringResource = participant.hasAlreadyBring() ? R.string.show_participant_message_already_bring :
                R.string.show_participant_message_not_bring;
        messageTextView.setText(getString(messageStringResource, participant.getName()));

        Button bringButton = (Button) view.findViewById(R.id.show_participant_bring_button);
        bringButton.setText(participant.hasAlreadyBring() ? R.string.show_participant_button_cancel_bring
                : R.string.show_participant_button_next);
        bringButton.setOnClickListener(this);

        view.findViewById(R.id.show_participant_ok_button).setOnClickListener(this);
        view.findViewById(R.id.show_participant_delete_button).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show_participant_ok_button:
                dismiss();
                break;

            case R.id.show_participant_delete_button:
                // Delete the participant
                ((DatabaseManipulation)getActivity()).removeFromDatabase(participant);
                dismiss();
                break;

            case R.id.show_participant_bring_button:
                // Reverse the participant status : make him the next one to bring if he hadn't bring yet, or cancel his bring if he has.
                ((DatabaseManipulation)getActivity()).updateInDatabase(participant, !participant.hasAlreadyBring(),
                        !participant.hasAlreadyBring());
                dismiss();
                break;
        }
    }
}
