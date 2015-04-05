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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import a.a.groupchat.ParseConstants;
import a.a.groupchat.R;


public class GroupAddActivity extends ActionBarActivity {

    Toolbar toolbar;
    ListView mGroupMemberListView;
    EditText groupName;
    EditText groupDesc;
    List<ParseUser> mUsers;
    ParseRelation<ParseUser> mFriendRelationship;
    ParseUser mCurrentUser;
    List<String> groupMemberList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_add);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        groupMemberList = new ArrayList<>();
        groupName = (EditText) findViewById(R.id.new_group_name);
        groupDesc = (EditText) findViewById(R.id.new_group_description);
        mGroupMemberListView = (ListView) findViewById(R.id.group_member_listView);
        Button submit = (Button) findViewById(R.id.group_submit_button);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mGroupName = groupName.getText().toString().trim();
                String mGroupDesc = groupDesc.getText().toString().trim();

                if (mGroupName.isEmpty() || mGroupDesc.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GroupAddActivity.this);
                    builder.setTitle(getString(R.string.error_empty_field_title))
                            .setMessage(getString(R.string.error_empty_field_message))
                            .setPositiveButton(android.R.string.ok, null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    final ParseObject newGroup = new ParseObject(ParseConstants.KEY_NEW_GROUP);
                    newGroup.put(ParseConstants.KEY_NEW_GROUP_NAME, mGroupName);
                    newGroup.put(ParseConstants.KEY_NEW_GROUP_DESCRIPTION, mGroupDesc);
                    newGroup.put(ParseConstants.KEY_NEW_GROUP_MEMBERS, groupMemberList);

                    newGroup.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                //Success

                                Log.i("New Group", "successful data entered.");
                                Toast.makeText(GroupAddActivity.this, "New Group Created", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(GroupAddActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                //error
                                AlertDialog.Builder builder = new AlertDialog.Builder(GroupAddActivity.this);
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


        mGroupMemberListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mGroupMemberListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (mGroupMemberListView.isItemChecked(position)) {
                    //add friend
                    groupMemberList.add(mUsers.get(position).getObjectId());
                } else {
                    //remove friend
                    groupMemberList.remove(mUsers.get(position).getObjectId());
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByDescending(ParseConstants.KEY_USER_NAME);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if (e == null) {
                    //success
                    mUsers = parseUsers;
                    final List<String> user = new ArrayList<String>();
                    for (ParseUser parseUser : parseUsers) {
                        user.add(parseUser.getString(ParseConstants.KEY_USER_NAME));
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(GroupAddActivity.this,
                            android.R.layout.simple_list_item_checked, user);
                    mGroupMemberListView.setAdapter(adapter);

                } else {
                    //error
                    AlertDialog.Builder builder = new AlertDialog.Builder(GroupAddActivity.this);
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
        getMenuInflater().inflate(R.menu.menu_group_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
