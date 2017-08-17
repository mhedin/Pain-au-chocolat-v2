package com.morgane.painauchocolatv2.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.morgane.painauchocolatv2.R;
import com.morgane.painauchocolatv2.models.Participant;

import io.realm.Realm;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_next_bringer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBringerTextView = (TextView) findViewById(R.id.bringer_name_textView);

        toolbar.findViewById(R.id.toolbar_close_imageButton).setOnClickListener(this);
        findViewById(R.id.bringer_ok_button).setOnClickListener(this);
        findViewById(R.id.bringer_another_button).setOnClickListener(this);

        mRealm = Realm.getDefaultInstance();

        findABringer();
    }

    /**
     * Find randomly a potential bringer for the breakfast, displayed after a countdown.
     */
    private void findABringer() {
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
                mBringerTextView.setText(mBringer.getName());
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
                break;
        }
    }
}
