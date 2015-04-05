package ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import a.a.groupchat.ParseConstants;
import a.a.groupchat.R;
import adapter.MessageAdapter;


public class GroupActivity extends ActionBarActivity {

    Toolbar toolbar;
    EditText sendMessage;
    ImageButton sendButton;
    ListView groupList;
    String group;
    String groupObjectId;
    List<ParseObject> mSenders;
    List<String> mSenderName;
    List<String> mMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        group = intent.getStringExtra("groupName");
        getSupportActionBar().setTitle(group);

        groupObjectId = intent.getStringExtra("groupObjectId");

        groupList = (ListView) findViewById(R.id.messages_list);
        sendMessage = (EditText) findViewById(R.id.send_message_text);
        sendButton = (ImageButton) findViewById(R.id.message_send_button);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = sendMessage.getText().toString().trim();

                if (!(message.isEmpty())) {
                    ParseObject mNewGroup = new ParseObject(group + "_" + groupObjectId);
                    mNewGroup.put(ParseConstants.KEY_SENDER_OBJECT_ID, ParseUser.getCurrentUser().getObjectId());
                    mNewGroup.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());
                    mNewGroup.put(ParseConstants.KEY_MESSAGE, message);

                    mNewGroup.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.i("GroupActivity", "message send");
                                sendMessage.setText("");
                                getMessages();
                            } else {
                                //error
                                AlertDialog.Builder builder = new AlertDialog.Builder(GroupActivity.this);
                                builder.setTitle(getString(R.string.error_title))
                                        .setMessage(getString(R.string.error_message))
                                        .setPositiveButton(android.R.string.ok, null);

                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        getMessages();
    }

    private void getMessages() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(group + "_" + groupObjectId);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    mSenders = parseObjects;

                    mSenderName = new ArrayList<String>();
                    mMessage = new ArrayList<String>();
                    for (ParseObject sender : parseObjects) {
                        mSenderName.add(sender.getString(ParseConstants.KEY_SENDER_NAME));
                        mMessage.add(sender.getString(ParseConstants.KEY_MESSAGE));
                    }

                    MessageAdapter adapter = new MessageAdapter(GroupActivity.this, mSenderName, mMessage, mSenders);
                    groupList.setAdapter(adapter);

                } else {
                    //error
                    AlertDialog.Builder builder = new AlertDialog.Builder(GroupActivity.this);
                    builder.setTitle(getString(R.string.error_title))
                            .setMessage(getString(R.string.error_message))
                            .setPositiveButton(android.R.string.ok, null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
