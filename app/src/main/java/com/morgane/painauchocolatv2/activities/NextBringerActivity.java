package com.morgane.painauchocolatv2.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.morgane.painauchocolatv2.R;
import com.morgane.painauchocolatv2.models.Participant;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * The activity finding randomly the next person who will bring the breakfast.
 */
public class NextBringerActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * The random participant selected to bring the next breakfast.
     */
    private Participant mBringer;

    /**
     * The TextView in which the name of the next potential bringer is displayed.
     */
    private TextView mBringerTextView;

    /**
     * The Realm instance.
     */
    private Realm mRealm;

    /**
     * The Button to validate the bringer.
     */
    private Button mValidateButton;

    /**
     * The Button to search for another bringer than the one proposed.
     */
    private Button mAnotherBringerButton;

    /**
     * Flag indicating if the Button to find another bringer must be enabled. It is not if there is
     * only one potential bringer left.
     */
    private boolean mEnableAnotherBringerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_next_bringer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBringerTextView = (TextView) findViewById(R.id.bringer_name_textView);

        toolbar.findViewById(R.id.toolbar_close_imageButton).setOnClickListener(this);

        mValidateButton = (Button) findViewById(R.id.bringer_ok_button);
        mAnotherBringerButton = (Button) findViewById(R.id.bringer_another_button);

        mValidateButton.setOnClickListener(this);
        mAnotherBringerButton.setOnClickListener(this);

        mRealm = Realm.getDefaultInstance();

        long potentialBringersCount = Participant.getPotentialBringersCount(mRealm);

        // If everybody has bring the breakfast, reset the status of all the participant to prepare the next session
        if (potentialBringersCount == 0) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<Participant> participants = mRealm.where(Participant.class).findAll();
                    for (Participant participant : participants) {
                        participant.setHasAlreadyBring(false);
                    }
                }
            });
        }

        // Disable the button to choose another bringer if there is only one possible choice
        mEnableAnotherBringerButton = potentialBringersCount != 1;

        findABringer();
    }

    /**
     * Find randomly a potential bringer for the breakfast, displayed after a countdown.
     */
    private void findABringer() {
        //Disable the buttons to avoid the user to launch for another request while processing
        mValidateButton.setEnabled(false);
        mAnotherBringerButton.setEnabled(false);

        Participant newBringer;
        // Get a random potential bringer, different from the previous selected if there was one
        do {
            newBringer = Participant.getRandomPotentialBringer(mRealm);
        } while(mBringer != null && mBringer.getName().equals(newBringer.getName()));

        mBringer = newBringer;

        new CountDownTimer(4000, 1000) {

            public void onTick(long millisUntilFinished) {
                // Show a waiting time with points, or remove text if it's a new bringer to found
                if (mBringerTextView.getText() != null
                        && mBringerTextView.getText().length() > 0
                        && (mBringerTextView.getText().equals(".") || mBringerTextView.getText().equals(".."))) {
                    mBringerTextView.setText(mBringerTextView.getText() + ".");
                } else {
                    mBringerTextView.setText(".");
                }
            }

            public void onFinish() {
                // Display the name of the new bringer
                mBringerTextView.setText(mBringer.getName());

                // Reactivate the buttons
                mValidateButton.setEnabled(true);
                if (mEnableAnotherBringerButton) {
                    mAnotherBringerButton.setEnabled(true);
                }
            }

        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_close_imageButton:
                finish();
                break;

            case R.id.bringer_another_button:
                // The user may want to select another person.
                findABringer();
                break;

            case R.id.bringer_ok_button:
                // Save the name of the next bringer selected.
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        mBringer.setHasAlreadyBring(true);

                        // There can be only one actual bringer at a time, so remove the old one if there were
                        Participant actualBringer = realm.where(Participant.class).equalTo("isTheActualBringer", true).findFirst();
                        if (actualBringer != null) {
                            actualBringer.setIsTheActualBringer(false);
                        }

                        mBringer.setIsTheActualBringer(true);
                    }
                });

                // Indicate the database has changed
                setResult(RESULT_OK);
                finish();
                break;
        }
    }
}
