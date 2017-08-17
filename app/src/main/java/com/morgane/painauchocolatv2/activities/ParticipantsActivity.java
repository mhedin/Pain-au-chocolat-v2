package com.morgane.painauchocolatv2.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;

import com.morgane.painauchocolatv2.R;

import com.morgane.painauchocolatv2.adapters.ParticipantAdapter;
import com.morgane.painauchocolatv2.fragments.AddParticipantDialogFragment;
import com.morgane.painauchocolatv2.fragments.ShowParticipantInfoDialogFragment;
import com.morgane.painauchocolatv2.interfaces.DatabaseManipulation;

import io.realm.Realm;
import io.realm.RealmResults;

import com.morgane.painauchocolatv2.interfaces.RecyclerViewOnItemLongClickListener;
import com.morgane.painauchocolatv2.models.Participant;

/**
 * The activity used to manage the participants. They are all displayed, with their actual status (has already
 * bring the breakfast for this session, has not bring it yet, or is the next one to bring)
 */
public class ParticipantsActivity extends AppCompatActivity implements View.OnClickListener, DatabaseManipulation, RecyclerViewOnItemLongClickListener {

    /**
     * The adapter used to display the list of participants.
     */
    private ParticipantAdapter mAdapter;

    /**
     * The Realm instance.
     */
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_participants);

        realm = Realm.getDefaultInstance();

        RealmResults<Participant> participantRealmResults = realm.where(Participant.class).findAll().sort("name");

        mAdapter = new ParticipantAdapter(participantRealmResults, this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.participants_recyclerView);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter.notifyDataSetChanged();

        FloatingActionButton addParticipantButton = (FloatingActionButton) findViewById(R.id.participants_add_fab);
        addParticipantButton.setOnClickListener(this);

        // If there is no participant created yet, display directly the dialog to add one
        if (participantRealmResults.isEmpty()) {
            addParticipantButton.performClick();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.participants_add_fab:
                // Display the dialog fragment used to add a new participant
                FragmentManager fm = getSupportFragmentManager();
                AddParticipantDialogFragment dialogFragment = new AddParticipantDialogFragment ();
                dialogFragment.show(fm, "Add participant Fragment");
                break;
        }
    }

    @Override
    public void insertInDatabase(final String name) {
        // Save the new participant in the database
        final Participant participant = new Participant(name);
        Number maxId = realm.where(Participant.class).max("id");
        participant.setId(maxId != null ? maxId.longValue() + 1 : 1);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(participant);
            }
        });

        // Indicate the database has changed
        setResult(RESULT_OK);
    }

    @Override
    public void updateInDatabase(final Participant participant, final boolean newHasAlreadyBringValue,
                                 final boolean newIsTheActualBringerValue) {
        // Update the bring values of the selected participant in the database
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                participant.setHasAlreadyBring(newHasAlreadyBringValue);

                if (newIsTheActualBringerValue) {
                    // There can be only one actual bringer at a time, so remove the old one if there were
                    Participant actualBringer = realm.where(Participant.class).equalTo("isTheActualBringer", true).findFirst();
                    if (actualBringer != null) {
                        actualBringer.setIsTheActualBringer(false);
                    }
                }
                participant.setIsTheActualBringer(newIsTheActualBringerValue);
            }
        });

        // Indicate the database has changed
        setResult(RESULT_OK);
    }

    @Override
    public void removeFromDatabase(final Participant participant) {
        // Remove the selected participant from the database
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                participant.deleteFromRealm();
            }
        });

        // Indicate the database has changed
        setResult(RESULT_OK);
    }

    @Override
    public void onItemLongClick(View v, Participant participant) {
        // Display the information of the selected participant, and allow the user to modify some parameters in it
        FragmentManager fm = getSupportFragmentManager();
        ShowParticipantInfoDialogFragment dialogFragment = ShowParticipantInfoDialogFragment.newInstance(participant);
        dialogFragment.show(fm, "Show participant Fragment");
    }
}
