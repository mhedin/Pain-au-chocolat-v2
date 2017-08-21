package com.morgane.painauchocolatv2.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.morgane.painauchocolatv2.R;
import com.morgane.painauchocolatv2.models.Participant;

import io.realm.Realm;

/**
 * The launcher activity.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * The code used to identify the activity to choose the next bringer.
     */
    private static final int REQUEST_CODE_NEXT_BRINGER = 1;

    /**
     * The code used to identify the activity to manage the participants.
     */
    private static final int REQUEST_CODE_MANAGE_PARTICIPANTS = 2;

    /**
     * The TextView in which the name of the next bringer is displayed.
     */
    private TextView mBringerTextView;

    /**
     * The Button used to choose a new bringer.
     */
    private Button mChooseBringerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        Typeface customTypeface = Typeface.createFromAsset(getAssets(), "fonts/GreatVibes_Regular.otf");
        TextView appNameTextView = (TextView) findViewById(R.id.home_painAuChocolat_textView);
        appNameTextView.setTypeface(customTypeface);

        mChooseBringerButton = (Button) findViewById(R.id.home_nextBringer_button);
        mChooseBringerButton.setOnClickListener(this);

        findViewById(R.id.home_participants_fab).setOnClickListener(this);

        mBringerTextView = (TextView) findViewById(R.id.home_bringer_textView);
        refreshBringerName();
    }

    /**
     * Refresh the name of the next participant to bring the breakfast or display an informative message.
     */
    private void refreshBringerName() {

        Realm realm = Realm.getDefaultInstance();

        // Check if there are existing participants, and if not, display an informative message
        if (Participant.getParticipantsCount(realm) == 0) {
            mBringerTextView.setText(R.string.text_no_participant_created);
            mChooseBringerButton.setEnabled(false);

        } else {
            mChooseBringerButton.setEnabled(true);

            // Get the name of the participant who will bring the breakfast
            Participant actualBringer = realm.where(Participant.class).equalTo("isTheActualBringer", true).findFirst();

            // If there is a participant identified to bring the breakfast, display it
            if (actualBringer != null) {
                mBringerTextView.setText(getString(R.string.text_next_to_bring_with_selection, actualBringer.getName()));

            } else {
                // Else display an informative message
                mBringerTextView.setText(R.string.text_next_to_bring_nobody_selected);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_nextBringer_button:
                startActivityForResult(new Intent(this, NextBringerActivity.class), REQUEST_CODE_NEXT_BRINGER);
                break;

            case R.id.home_participants_fab:
                startActivityForResult(new Intent(this, ParticipantsActivity.class), REQUEST_CODE_MANAGE_PARTICIPANTS);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Do something only if the activity did something
        if (resultCode == Activity.RESULT_OK) {

            // If a new bringer has been chosen, or if the participants have been modified, display it
            if (requestCode == REQUEST_CODE_NEXT_BRINGER || requestCode == REQUEST_CODE_MANAGE_PARTICIPANTS) {
                refreshBringerName();
            }
        }
    }
}
