package com.morgane.painauchocolatv2.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.morgane.painauchocolatv2.R;
import com.morgane.painauchocolatv2.models.Participant;

import java.util.List;
import java.util.Random;

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
     * The font used to display the name of the next bringer.
     */
    private Typeface mBringerNameFont;

    /**
     * The list of participants who have not bring the breakfast for this session yet.
     */
    private List<Participant> mPotentialBringerList;

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

        mPotentialBringerList = Participant.getPotentialBringers(mRealm);

        // If everybody has bring the breakfast, reset the status of all the participant to prepare the next session
        if (mPotentialBringerList.isEmpty()) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<Participant> participants = mRealm.where(Participant.class).findAll();
                    for (Participant participant : participants) {
                        participant.setHasAlreadyBring(false);
                    }
                    mPotentialBringerList = participants;
                }
            });
        }

        // Set the font of the bringer name
        mBringerNameFont = Typeface.createFromAsset(getAssets(), "fonts/GreatVibes_Regular.otf");
        mBringerTextView.setTypeface(mBringerNameFont);

        findABringer();
    }

    /**
     * Find randomly a potential bringer for the breakfast, displayed after a countdown.
     */
    private void findABringer() {
        //Disable the buttons to avoid the user to launch for another request while processing
        mValidateButton.setEnabled(false);
        mAnotherBringerButton.setEnabled(false);

        // Get a random potential bringer, different from the previous selected if there was one
        Participant newBringer;
        do {
            newBringer = getRandomPotentialBringer(mPotentialBringerList);
        } while(mBringer != null && mBringer.getName().equals(newBringer.getName()));

        mBringer = newBringer;

        // Do different effects if there is only one potential bringer or if there is several
        if (mPotentialBringerList.size() > 1) {

            // Do a roulette effect before displaying the name of the potential next bringer
            new CountDownTimer(5000, 70) {

                public void onTick(long millisUntilFinished) {

                /* Change randomly the name of the potential bringer. Until 2 sec, change it fast. Then slowly until 1 sec,
                 * and slower until the end. */
                    if (millisUntilFinished > 2000
                            || (millisUntilFinished > 1000 && millisUntilFinished % 2 == 0)
                            || (millisUntilFinished <= 1000 && millisUntilFinished % 3 == 0)) {
                        mBringerTextView.setText(getRandomPotentialBringer(mPotentialBringerList).getName());
                    }
                }

                public void onFinish() {
                    displayNextBringerName();
                }

            }.start();

        } else {

            // If there is one bringer left, display suspension points before displaying his name
            mBringerTextView.setText("");
            new CountDownTimer(4000, 1000) {

                public void onTick(long millisUntilFinished) {
                    mBringerTextView.setText(mBringerTextView.getText() + ".");
                }

                public void onFinish() {
                    displayNextBringerName();
                }

            }.start();
        }
    }

    /**
     * Get a potential bringer, randomly.
     * @param potentialBringers The list of potential bringers.
     * @return A random potential bringer.
     */
    private Participant getRandomPotentialBringer(List<Participant> potentialBringers) {
        Random random = new Random(System.nanoTime());
        int randomPosition = random.nextInt(potentialBringers.size());
        return potentialBringers.get(randomPosition);
    }

    /**
     * Display the name of the selected potential next bringer, and reactivate the buttons.
     */
    private void displayNextBringerName() {
        // Display the name of the new bringer
        mBringerTextView.setText(mBringer.getName());
        mBringerTextView.setTextColor(ContextCompat.getColor(NextBringerActivity.this, R.color.brown));
        mBringerTextView.setTypeface(mBringerNameFont, Typeface.BOLD);

        // Reactivate the buttons
        mValidateButton.setEnabled(true);
        // Allow to choose another bringer only if there is more than one left
        if (mPotentialBringerList.size() > 1) {
            mAnotherBringerButton.setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_close_imageButton:
                finish();
                break;

            case R.id.bringer_another_button:
                // The user may want to select another person.
                mBringerTextView.setTextColor(ContextCompat.getColor(NextBringerActivity.this, android.R.color.tertiary_text_light));
                mBringerTextView.setTypeface(mBringerNameFont, Typeface.NORMAL);
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
