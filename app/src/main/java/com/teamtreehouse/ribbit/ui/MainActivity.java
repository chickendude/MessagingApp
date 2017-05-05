package com.teamtreehouse.ribbit.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.teamtreehouse.ribbit.R;
import com.teamtreehouse.ribbit.adapters.SectionsPagerAdapter;
import com.teamtreehouse.ribbit.models.Message;
import com.teamtreehouse.ribbit.models.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements InboxFragment.FabListener {

	public static final String TAG = MainActivity.class.getSimpleName();

	public static final int TAKE_PHOTO_REQUEST = 0;
	public static final int TAKE_VIDEO_REQUEST = 1;
	public static final int PICK_PHOTO_REQUEST = 2;
	public static final int PICK_VIDEO_REQUEST = 3;

	public static final int MEDIA_TYPE_IMAGE = 4;
	public static final int MEDIA_TYPE_VIDEO = 5;

	public static final int FILE_SIZE_LIMIT = 1024 * 1024 * 10; // 10 MB

	protected Uri mMediaUri;

	protected DialogInterface.OnClickListener mDialogListener =
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					sendNewMessage(which);
				}
			};

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		User currentUser = User.getCurrentUser();
		if (currentUser == null) {
			navigateToLogin();
		} else {
			Log.i(TAG, currentUser.getUsername());
		}

		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (requestCode == PICK_PHOTO_REQUEST || requestCode == PICK_VIDEO_REQUEST) {
				if (data == null) {
					Toast.makeText(this, getString(R.string.general_error), Toast.LENGTH_LONG).show();
				} else {
					mMediaUri = data.getData();
				}

				Log.i(TAG, "Media URI: " + mMediaUri);
				if (requestCode == PICK_VIDEO_REQUEST) {
					// make sure the file is less than 10 MB
					int fileSize = 0;
					InputStream inputStream = null;

					try {
						inputStream = getContentResolver().openInputStream(mMediaUri);
						fileSize = inputStream.available();
					} catch (FileNotFoundException e) {
						Toast.makeText(this, R.string.error_opening_file, Toast.LENGTH_LONG).show();
						return;
					} catch (IOException e) {
						Toast.makeText(this, R.string.error_opening_file, Toast.LENGTH_LONG).show();
						return;
					} finally {
						try {
							inputStream.close();
						} catch (IOException e) { /* Intentionally blank */ }
					}

					if (fileSize >= FILE_SIZE_LIMIT) {
						Toast.makeText(this, R.string.error_file_size_too_large, Toast.LENGTH_LONG).show();
						return;
					}
				}
			} else {
				Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				mediaScanIntent.setData(mMediaUri);
				sendBroadcast(mediaScanIntent);
			}

			Intent recipientsIntent = new Intent(this, RecipientsActivity.class);
			recipientsIntent.setData(mMediaUri);

			String fileType;
			if (requestCode == PICK_PHOTO_REQUEST || requestCode == TAKE_PHOTO_REQUEST) {
				fileType = Message.TYPE_IMAGE;
			} else {
				fileType = Message.TYPE_VIDEO;
			}

			recipientsIntent.putExtra(Message.KEY_FILE_TYPE, fileType);
			startActivity(recipientsIntent);
		} else if (resultCode != RESULT_CANCELED) {
			Toast.makeText(this, R.string.general_error, Toast.LENGTH_LONG).show();
		}
	}

	private void navigateToLogin() {
		Intent intent = new Intent(this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();

		switch (itemId) {
			case R.id.action_logout:
				User.logOut();
				navigateToLogin();
				break;
			case R.id.action_edit_friends:
				Intent intent = new Intent(this, EditFriendsActivity.class);
				startActivity(intent);
				break;
			case R.id.action_camera:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setItems(R.array.camera_choices, mDialogListener);
				AlertDialog dialog = builder.create();
				dialog.show();
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	public void sendNewMessage(int which) {
		switch (which) {
			case 0: // Take picture
				Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
				if (mMediaUri == null) {
					// display an error
					Toast.makeText(MainActivity.this, R.string.error_external_storage,
							Toast.LENGTH_LONG).show();
				} else {
					takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
					startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
				}
				break;
			case 1: // Take video
				Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
				mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
				if (mMediaUri == null) {
					// display an error
					Toast.makeText(MainActivity.this, R.string.error_external_storage,
							Toast.LENGTH_LONG).show();
				} else {
					videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
					videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
					videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0); // 0 = lowest res
					startActivityForResult(videoIntent, TAKE_VIDEO_REQUEST);
				}
				break;
			case 2: // Choose picture
				Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
				choosePhotoIntent.setType("image/*");
				startActivityForResult(choosePhotoIntent, PICK_PHOTO_REQUEST);
				break;
			case 3: // Choose video
				Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
				chooseVideoIntent.setType("video/*");
				Toast.makeText(MainActivity.this, R.string.video_file_size_warning, Toast.LENGTH_LONG).show();
				startActivityForResult(chooseVideoIntent, PICK_VIDEO_REQUEST);
				break;
			case 4: // Send text message
				Intent sendTextMessageIntent = new Intent(this, TextMessageActivity.class);
				startActivity(sendTextMessageIntent);
				break;
		}
	}

	private Uri getOutputMediaFileUri(int mediaType) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		if (isExternalStorageAvailable()) {
			// get the URI

			// 1. Get the external storage directory
			File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

			// 2. Create a unique file name
			String fileName = "";
			String fileType = "";
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

			if (mediaType == MEDIA_TYPE_IMAGE) {
				fileName = "IMG_" + timeStamp;
				fileType = ".jpg";
			} else if (mediaType == MEDIA_TYPE_VIDEO) {
				fileName = "VID_" + timeStamp;
				fileType = ".mp4";
			} else {
				return null;
			}

			// 3. Create the file
			File mediaFile;
			try {
				mediaFile = File.createTempFile(fileName, fileType, mediaStorageDir);
				Log.i(TAG, "File: " + Uri.fromFile(mediaFile));

				// 4. Return the file's URI
				return Uri.fromFile(mediaFile);
			} catch (IOException e) {
				Log.e(TAG, "Error creating file: " +
						mediaStorageDir.getAbsolutePath() + fileName + fileType);
			}
		}

		// something went wrong
		return null;
	}

	private boolean isExternalStorageAvailable() {
		String state = Environment.getExternalStorageState();

		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onFabPressed(int button) {
		sendNewMessage(button);
	}
}
