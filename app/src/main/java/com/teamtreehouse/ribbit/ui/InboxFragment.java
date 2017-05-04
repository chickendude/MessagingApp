package com.teamtreehouse.ribbit.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.teamtreehouse.ribbit.R;
import com.teamtreehouse.ribbit.adapters.MessageAdapter;
import com.teamtreehouse.ribbit.models.Message;
import com.teamtreehouse.ribbit.models.MessageFile;
import com.teamtreehouse.ribbit.models.Query;
import com.teamtreehouse.ribbit.models.User;
import com.teamtreehouse.ribbit.models.callbacks.FindCallback;

import java.util.List;

public class InboxFragment extends ListFragment {
	public interface FabListener {
		void onFabPressed(int button);
	}

	private static final String TAG = InboxFragment.class.getSimpleName();
	protected List<Message> mMessages;
	protected SwipeRefreshLayout mSwipeRefreshLayout;
	private FabListener mFabListener;
	private FloatingActionMenu mFloatingActionMenu;


	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mFabListener = (FabListener) context;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mFabListener = (FabListener) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_inbox,
				container, false);

		mFloatingActionMenu = (FloatingActionMenu) rootView.findViewById(R.id.fabMenu);
		FloatingActionButton fabPicture = (FloatingActionButton) rootView.findViewById(R.id.fabPicture);
		FloatingActionButton fabVideo = (FloatingActionButton) rootView.findViewById(R.id.fabVideo);
		FloatingActionButton fabChoosePicture = (FloatingActionButton) rootView.findViewById(R.id.fabChoosePicture);
		FloatingActionButton fabChooseVideo = (FloatingActionButton) rootView.findViewById(R.id.fabChooseVideo);
		FloatingActionButton fabSendText = (FloatingActionButton) rootView.findViewById(R.id.fabSendText);
		fabPicture.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mFloatingActionMenu.close(true);
				mFabListener.onFabPressed(0);
			}
		});
		fabVideo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mFloatingActionMenu.close(true);
				mFabListener.onFabPressed(1);
			}
		});
		fabChoosePicture.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mFloatingActionMenu.close(true);
				mFabListener.onFabPressed(2);
			}
		});
		fabChooseVideo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mFloatingActionMenu.close(true);
				mFabListener.onFabPressed(3);
			}
		});
		fabSendText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mFloatingActionMenu.close(true);
				mFabListener.onFabPressed(4);
			}
		});

		mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
		mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
		// Deprecated method - what should we call instead?
		mSwipeRefreshLayout.setColorSchemeResources(
				R.color.swipeRefresh1,
				R.color.swipeRefresh2,
				R.color.swipeRefresh3,
				R.color.swipeRefresh4);

		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Log.d(TAG, "onViewCreated");
		retrieveMessages();
	}

	@Override
	public void onResume() {
		super.onResume();
//		getActivity().setProgressBarIndeterminateVisibility(true);
	}

	private void retrieveMessages() {
		Log.d(TAG, "retrieveMessages");
		if (User.getCurrentUser() != null) {
			Log.d(TAG, "current user not null");
			Query<Message> query = Message.getQuery();
			query.whereEqualTo(Message.KEY_RECIPIENT_IDS, User.getCurrentUser().getObjectId());
			query.addDescendingOrder(Message.KEY_CREATED_AT);
			query.findInBackground(new FindCallback<Message>() {
				@Override
				public void done(List<Message> messages, Exception e) {
					getActivity().setProgressBarIndeterminateVisibility(false);

					if (mSwipeRefreshLayout.isRefreshing()) {
						mSwipeRefreshLayout.setRefreshing(false);
					}

					if (e == null) {
						// We found messages!
						mMessages = messages;

//						String[] usernames = new String[mMessages.size()];
//						int i = 0;
//						for (Message message : mMessages) {
//							usernames[i] = message.getString(Message.KEY_SENDER_NAME);
//							i++;
//						}

						updateAdapterWithMessages();
					}
				}
			});
		}

	}

	private void updateAdapterWithMessages() {
		if (getListView().getAdapter() == null) {
			MessageAdapter adapter = new MessageAdapter(
					getListView().getContext(),
					mMessages);
			setListAdapter(adapter);
		} else {
			// refill the adapter!
			((MessageAdapter) getListView().getAdapter()).refill(mMessages);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Message message = mMessages.get(position);
		mMessages.remove(position);
		updateAdapterWithMessages();

		String messageType = message.getString(Message.KEY_FILE_TYPE);
		if (messageType.equals(Message.TYPE_TEXT)) {
			String messageText = message.getString(Message.KEY_FILE);
			Intent intent = new Intent(getActivity(), ViewTextMessageActivity.class);
			intent.putExtra(RecipientsActivity.KEY_MESSAGE, messageText);
			startActivity(intent);
		} else {
			MessageFile file = message.getFile(Message.KEY_FILE);
			Uri fileUri = file.getUri();

			if (messageType.equals(Message.TYPE_IMAGE)) {
				// view the image
				Intent intent = new Intent(getActivity(), ViewImageActivity.class);
				intent.setData(fileUri);
				startActivity(intent);
			} else {
				// view the video
				Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
				intent.setDataAndType(fileUri, "video/*");
				startActivity(intent);
			}
		}

		// Delete it!
		List<String> ids = message.getList(Message.KEY_RECIPIENT_IDS);

		if (ids.size() == 1) {
			// last recipient - delete the whole thing!
			message.deleteInBackground();
		} else {
			// remove the recipient
			message.removeRecipient(User.getCurrentUser().getObjectId());
		}
	}

	protected OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			retrieveMessages();
		}
	};
}








