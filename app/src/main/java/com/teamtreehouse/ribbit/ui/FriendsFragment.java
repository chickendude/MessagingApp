package com.teamtreehouse.ribbit.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.teamtreehouse.ribbit.R;
import com.teamtreehouse.ribbit.adapters.UserAdapter;
import com.teamtreehouse.ribbit.models.Query;
import com.teamtreehouse.ribbit.models.Relation;
import com.teamtreehouse.ribbit.models.User;
import com.teamtreehouse.ribbit.models.callbacks.FindCallback;

import java.util.List;

public class FriendsFragment extends Fragment {

    public static final String TAG = FriendsFragment.class.getSimpleName();

    protected Relation<User> mFriendsRelation;
    protected User mCurrentUser;
    protected List<User> mFriends;
    protected GridView mGridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_grid,
                container, false);

        mGridView = (GridView) rootView.findViewById(R.id.friendsGrid);

        TextView emptyTextView = (TextView) rootView.findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mCurrentUser = User.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(User.KEY_FRIENDS_RELATION);

        getActivity().setProgressBarIndeterminateVisibility(true);


        Query<User> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(User.KEY_USER_ID);
        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> friends, Exception e) {
                getActivity().setProgressBarIndeterminateVisibility(false);

                if (e == null) {
                    mFriends = friends;

                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (User user : mFriends) {
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    if (mGridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(getActivity(), mFriends);
                        mGridView.setAdapter(adapter);
                    } else {
                        ((UserAdapter) mGridView.getAdapter()).refill(mFriends);
                    }
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

}
