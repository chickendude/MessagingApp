package com.teamtreehouse.ribbit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamtreehouse.ribbit.R;
import com.teamtreehouse.ribbit.models.Message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {

	private static final String TAG = MessageAdapter.class.getSimpleName();
	protected Context mContext;
	protected List<Message> mMessages;

	public MessageAdapter(Context context, List<Message> messages) {
		super(context, R.layout.message_item, messages);
		mContext = context;

		// Create a full copy of mMessages
		mMessages = new ArrayList<Message>();
		for (Message msg : messages) {
			mMessages.add(msg);
		}
	}



	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);
			holder = new ViewHolder();
			holder.iconImageView = (ImageView) convertView.findViewById(R.id.messageIcon);
			holder.nameLabel = (TextView) convertView.findViewById(R.id.senderLabel);
			holder.timeLabel = (TextView) convertView.findViewById(R.id.timeLabel);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Message message = mMessages.get(position);

		Date createdAt = message.getCreatedAt();
		long now = new Date().getTime();
		long timeAgo = now - createdAt.getTime();
		long seconds = timeAgo/1000;
		long minutes = seconds/60;
		long hours = minutes/60;
		long days = hours/24;
		long months = days/30;

		long unitAmount = seconds;
		String unitName = "second";
		if (months > 0) {
			unitAmount = months;
			unitName = "month";
		} else if (days > 0) {
			unitAmount = days;
			unitName = "day";
		} else if (hours > 0) {
			unitAmount = hours;
			unitName = "hour";
		} else if (minutes > 0) {
			unitAmount = minutes;
			unitName = "minute";
		}

		if (unitAmount > 1) {
			unitName += "s";
		}

		holder.timeLabel.setText(String.format("%d %s ago", unitAmount, unitName));

		if (message.getString(Message.KEY_FILE_TYPE).equals(Message.TYPE_IMAGE)) {
			holder.iconImageView.setImageResource(R.drawable.ic_picture);
		} else {
			holder.iconImageView.setImageResource(R.drawable.ic_video);
		}
		holder.nameLabel.setText(message.getString(Message.KEY_SENDER_NAME));

		return convertView;
	}

	private static class ViewHolder {
		ImageView iconImageView;
		TextView nameLabel;
		TextView timeLabel;
	}

	public void refill(List<Message> messages) {
		mMessages = messages;
		notifyDataSetChanged();
	}
}






