package com.morgane.painauchocolatv2.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import com.morgane.painauchocolatv2.R;
import com.morgane.painauchocolatv2.models.Participant;

import io.realm.Realm;

/**
 * The launcher activity.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        Typeface customTypeface = Typeface.createFromAsset(getAssets(),
                "fonts/GreatVibes_Regular.otf");
        TextView appNameTextView = (TextView) findViewById(R.id.home_painAuChocolat_textView);
        appNameTextView.setTypeface(customTypeface);

        findViewById(R.id.home_nextBringer_button).setOnClickListener(this);
        findViewById(R.id.home_participants_fab).setOnClickListener(this);

        TextView bringerTextView = (TextView) findViewById(R.id.home_bringer_textView);
        Realm realm = Realm.getDefaultInstance();
        Participant actualBringer = realm.where(Participant.class).equalTo("isTheActualBringer", true).findFirst();
        if (actualBringer != null) {
            bringerTextView.setText(getString(R.string.text_next_to_bring_with_selection, actualBringer.getName()));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_nextBringer_button:
                startActivity(new Intent(this, NextBringerActivity.class));
                break;

            case R.id.home_participants_fab:
                startActivity(new Intent(this, ParticipantsActivity.class));
                break;
        }
    }
}
