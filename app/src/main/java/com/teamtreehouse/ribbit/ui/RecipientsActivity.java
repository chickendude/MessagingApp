package com.teamtreehouse.ribbit.ui;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.teamtreehouse.ribbit.R;
import com.teamtreehouse.ribbit.adapters.UserAdapter;
import com.teamtreehouse.ribbit.models.Message;
import com.teamtreehouse.ribbit.models.MessageFile;
import com.teamtreehouse.ribbit.models.Query;
import com.teamtreehouse.ribbit.models.Relation;
import com.teamtreehouse.ribbit.models.User;
import com.teamtreehouse.ribbit.models.callbacks.FindCallback;
import com.teamtreehouse.ribbit.models.callbacks.SaveCallback;
import com.teamtreehouse.ribbit.utils.FileHelper;

import java.util.ArrayList;
import java.util.List;

public class RecipientsActivity extends AppCompatActivity {

	public static final String TAG = RecipientsActivity.class.getSimpleName();

	public static final String KEY_MESSAGE = "tag_message";

	protected Relation<User> mFriendsRelation;
	protected User mCurrentUser;
	protected List<User> mFriends;
	protected MenuItem mSendMenuItem;
	protected Button mSendButton;
	protected Uri mMediaUri;
	protected String mMessageText;
	protected String mFileType;
	protected GridView mGridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_grid);
		// Show the Up button in the action bar.
		setupActionBar();

		mGridView = (GridView) findViewById(R.id.friendsGrid);
		mGridView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		mGridView.setOnItemClickListener(mOnItemClickListener);

		mSendButton = (Button) findViewById(R.id.sendButton);
		mSendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendMessage();
			}
		});

		TextView emptyTextView = (TextView) findViewById(android.R.id.empty);
		mGridView.setEmptyView(emptyTextView);

		mFileType = getIntent().getExtras().getString(Message.KEY_FILE_TYPE);
		if (mFileType.equals(Message.TYPE_TEXT)) {
			mMessageText = getIntent().getExtras().getString(KEY_MESSAGE);
		} else {
			mMediaUri = getIntent().getData();
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		mCurrentUser = User.getCurrentUser();
		mFriendsRelation = mCurrentUser.getRelation(User.KEY_FRIENDS_RELATION);

		setProgressBarIndeterminateVisibility(true);

		Query<User> query = mFriendsRelation.getQuery();
		query.addAscendingOrder(User.KEY_USERNAME);
		query.findInBackground(new FindCallback<User>() {
			@Override
			public void done(List<User> friends, Exception e) {
				setProgressBarIndeterminateVisibility(false);

				if (e == null) {
					mFriends = friends;

					String[] usernames = new String[mFriends.size()];
					int i = 0;
					for (User user : mFriends) {
						usernames[i] = user.getUsername();
						i++;
					}

					if (mGridView.getAdapter() == null) {
						UserAdapter adapter = new UserAdapter(RecipientsActivity.this, mFriends);
						mGridView.setAdapter(adapter);
					} else {
						((UserAdapter) mGridView.getAdapter()).refill(mFriends);
					}
				} else {
					Log.e(TAG, e.getMessage());
					AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
					builder.setMessage(e.getMessage())
							.setTitle(R.string.error_title)
							.setPositiveButton(android.R.string.ok, null);
					AlertDialog dialog = builder.create();
					dialog.show();
				}
			}
		});
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

	private void sendMessage() {
		Message message = createMessage();
		if (message == null) {
			// error
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.error_selecting_file)
					.setTitle(R.string.error_selecting_file_title)
					.setPositiveButton(android.R.string.ok, null);
			AlertDialog dialog = builder.create();
			dialog.show();
		} else {
			send(message);
			finish();
		}
	}

	protected Message createMessage() {
		Message message = new Message(Message.class.getSimpleName());
		message.put(Message.KEY_SENDER_ID, User.getCurrentUser().getObjectId());
		message.put(Message.KEY_SENDER_NAME, User.getCurrentUser().getUsername());
		message.put(Message.KEY_RECIPIENT_IDS, getRecipientIds());
		message.put(Message.KEY_FILE_TYPE, mFileType);

		if (mFileType.equals(Message.TYPE_TEXT)) {
			message.put(Message.KEY_FILE, mMessageText);
			return message;
		} else {
			byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);

			if (fileBytes == null) {
				return null;
			} else {
				if (mFileType.equals(Message.TYPE_IMAGE)) {
					fileBytes = FileHelper.reduceImageForUpload(fileBytes);
				}

				String fileName = FileHelper.getFileName(this, mMediaUri, mFileType);
				MessageFile file = new MessageFile(fileName, fileBytes, mMediaUri);
				message.put(Message.KEY_FILE, file);

				return message;
			}
		}
	}

	protected ArrayList<String> getRecipientIds() {
		ArrayList<String> recipientIds = new ArrayList<String>();
		for (int i = 0; i < mGridView.getCount(); i++) {
			if (mGridView.isItemChecked(i)) {
				recipientIds.add(mFriends.get(i).getObjectId());
			}
		}
		return recipientIds;
	}

	protected void send(Message message) {
		message.saveInBackground(new SaveCallback() {
			@Override
			public void done(Exception e) {
				if (e == null) {
					// success!
					Toast.makeText(RecipientsActivity.this, R.string.success_message, Toast.LENGTH_LONG).show();
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
					builder.setMessage(R.string.error_sending_message)
							.setTitle(R.string.error_selecting_file_title)
							.setPositiveButton(android.R.string.ok, null);
					AlertDialog dialog = builder.create();
					dialog.show();
				}
			}
		});
	}

	protected OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
								long id) {
			if (mGridView.getCheckedItemCount() > 0) {
//                mSendMenuItem.setVisible(true);
				mSendButton.setVisibility(View.VISIBLE);
			} else {
//                mSendMenuItem.setVisible(false);
				mSendButton.setVisibility(View.GONE);
			}

			ImageView checkImageView = (ImageView) view.findViewById(R.id.checkImageView);

			if (mGridView.isItemChecked(position)) {
				// add the recipient
				checkImageView.setVisibility(View.VISIBLE);
			} else {
				// remove the recipient
				checkImageView.setVisibility(View.INVISIBLE);
			}
		}
	};
}






