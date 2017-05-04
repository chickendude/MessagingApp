package com.teamtreehouse.ribbit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.teamtreehouse.ribbit.R;
import com.teamtreehouse.ribbit.models.Message;

/**
 * Created by crater-windoze on 5/5/2017.
 */

public class TextMessageActivity extends AppCompatActivity {
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_textmessage);
		final EditText editText = (EditText) findViewById(R.id.messageEdit);
		Button sendButton = (Button) findViewById(R.id.sendButton);
		sendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent recipientsIntent = new Intent(TextMessageActivity.this, RecipientsActivity.class);
				String messageTxt = editText.getText().toString();
				recipientsIntent.putExtra(RecipientsActivity.KEY_MESSAGE, messageTxt);
				recipientsIntent.putExtra(Message.KEY_FILE_TYPE, Message.TYPE_TEXT);
				startActivity(recipientsIntent);
				finish();
			}
		});
	}
}
