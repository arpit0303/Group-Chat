package a.a.groupchat;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Arpit on 30/03/15.
 */
public class GroupChat extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "5FBtYPiZESQHN6BkXFBJfHkUXFQwFVeqw7QYPcQR", "2NXSbvuBchfUpPfPOmbrxwzxnzSiy5qFpC0J65Jz");
    }
}
