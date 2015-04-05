package ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import a.a.groupchat.ParseConstants;
import a.a.groupchat.R;
import adapter.CustomAdapter;


public class HomeActivity extends ActionBarActivity {

    Toolbar toolbar;
    List<ParseObject> mGroups;
    ListView groupList;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        groupList = (ListView) findViewById(R.id.group_list);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getGroupDetails();
            }
        });

    }

    private void getGroupDetails() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.KEY_NEW_GROUP);
        try {
            if (query.count() > 0) {

                query.orderByDescending(ParseConstants.KEY_NEW_GROUP_CREATED_AT);

                setSupportProgressBarIndeterminateVisibility(true);

                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> parseObjects, ParseException e) {

                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }

                        setSupportProgressBarIndeterminateVisibility(false);
                        if (e == null) {
                            mGroups = parseObjects;
                            final List<String> groupName = new ArrayList<String>();
                            List<String> groupDescription = new ArrayList<String>();

                            for (ParseObject group : parseObjects) {
                                groupName.add(group.getString(ParseConstants.KEY_NEW_GROUP_NAME));
                                groupDescription.add(group.getString(ParseConstants.KEY_NEW_GROUP_DESCRIPTION));
                            }

                            CustomAdapter adapter = new CustomAdapter(HomeActivity.this, groupName, groupDescription);
                            groupList.setAdapter(adapter);

                            groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent(HomeActivity.this, GroupActivity.class);
                                    intent.putExtra("position", position);
                                    intent.putExtra("groupName", groupName.get(position));
                                    startActivity(intent);
                                }
                            });

                        } else {
                            //error
                            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                            builder.setTitle(getString(R.string.error_login_done_title))
                                    .setMessage(getString(R.string.error_login_done_message))
                                    .setPositiveButton(android.R.string.ok, null);

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ParseUser.getCurrentUser() == null) {
            navigateToLogin();
        } else {
            getGroupDetails();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_logout:
                ParseUser.logOut();
                navigateToLogin();
                break;
            case R.id.action_new_group:
                startActivity(new Intent(HomeActivity.this, GroupAddActivity.class));
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
