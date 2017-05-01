package com.teamtreehouse.ribbit.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.teamtreehouse.ribbit.R;
import com.teamtreehouse.ribbit.adapters.MessageAdapter;
import com.teamtreehouse.ribbit.models.Message;
import com.teamtreehouse.ribbit.models.MessageFile;
import com.teamtreehouse.ribbit.models.Query;
import com.teamtreehouse.ribbit.models.User;
import com.teamtreehouse.ribbit.models.callbacks.FindCallback;

import java.util.List;

public class InboxFragment extends ListFragment {

    protected List<Message> mMessages;
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox,
                container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        // Deprecated method - what should we call instead?
        mSwipeRefreshLayout.setColorScheme(
                R.color.swipeRefresh1,
                R.color.swipeRefresh2,
                R.color.swipeRefresh3,
                R.color.swipeRefresh4);

        retrieveMessages();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setProgressBarIndeterminateVisibility(true);
    }

    private void retrieveMessages() {
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

                    String[] usernames = new String[mMessages.size()];
                    int i = 0;
                    for (Message message : mMessages) {
                        usernames[i] = message.getString(Message.KEY_SENDER_NAME);
                        i++;
                    }
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
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Message message = mMessages.get(position);
        String messageType = message.getString(Message.KEY_FILE_TYPE);
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

        // Delete it!
        List<String> ids = message.getList(Message.KEY_RECIPIENT_IDS);

        if (ids.size() == 1) {
            // last recipient - delete the whole thing!
            message.deleteInBackground();
        }
        else {
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








