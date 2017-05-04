package com.teamtreehouse.ribbit.ui;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.teamtreehouse.ribbit.R;

import java.util.Timer;
import java.util.TimerTask;

public class ViewTextMessageActivity extends AppCompatActivity {
	private int counter;
	private TextView textView;
	private TextView counterLabel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_textmessage);
		// Show the Up button in the action bar.
		setupActionBar();
		counter = 10;

		textView = (TextView) findViewById(R.id.textView);
		counterLabel = (TextView) findViewById(R.id.counterLabel);
		String message = getIntent().getStringExtra(RecipientsActivity.KEY_MESSAGE);
		textView.setText(message);

		counterLabel.setText(String.format("Message will be destroyed in %s seconds", counter));

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				counter--;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						counterLabel.setText(String.format("Message will be destroyed in %s seconds", counter));
					}
				});
			}
		}, 1000, 1000);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				finish();
			}
		}, 10 * 1000);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				// This ID represents the Home or Up button. In the case of this
				// activity, the Up button is shown. Use NavUtils to allow users
				// to navigate up one level in the application structure. For
				// more details, see the Navigation pattern on Android Design:
				//
				// http://developer.android.com/design/patterns/navigation.html#up-vs-back
				//
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
