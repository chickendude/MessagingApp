package com.teamtreehouse.ribbit.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.teamtreehouse.ribbit.R;

import java.util.Timer;
import java.util.TimerTask;

public class ViewImageActivity extends AppCompatActivity {

	private int mCounter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_image);
		// Show the Up button in the action bar.
		setupActionBar();

		mCounter = 10;

		final TextView counterLabel = (TextView) findViewById(R.id.counterLabel);
		counterLabel.setText(String.format("Message will be destroyed in %s seconds", mCounter));

		ImageView imageView = (ImageView) findViewById(R.id.imageView);
		Uri imageUri = getIntent().getData();
		Picasso.with(this).load(imageUri.toString()).into(imageView);

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				mCounter--;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						counterLabel.setText(String.format("Message will be destroyed in %s seconds", mCounter));
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
