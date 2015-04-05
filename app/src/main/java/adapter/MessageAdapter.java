package adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;

import a.a.groupchat.ParseConstants;
import a.a.groupchat.R;

/**
 * Created by Arpit on 05/04/15.
 */
public class MessageAdapter extends ArrayAdapter<String> {

    protected Context mContext;
    protected List<String> mSenderName;
    protected List<String> mMessage;
    List<ParseObject> mSenders;

    public MessageAdapter(Context context, List<String> name, List<String> message, List<ParseObject> senders) {
        super(context, R.layout.sender_message_list_item, name);
        mContext = context;
        mSenderName = name;
        mMessage = message;
        mSenders = senders;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.sender_message_list_item, null);
            holder = new ViewHolder();

            holder.senderName = (TextView) convertView.findViewById(R.id.senderName);
            holder.message = (TextView) convertView.findViewById(R.id.senderMessage);
            holder.sendingDate = (TextView) convertView.findViewById(R.id.sendingDate);

            holder.myMessage = (TextView) convertView.findViewById(R.id.myMessage);
            holder.mySendingDate = (TextView) convertView.findViewById(R.id.mySendingDate);

            holder.senderLayout = (LinearLayout) convertView.findViewById(R.id.senderLayout);
            holder.myLayout = (RelativeLayout) convertView.findViewById(R.id.myLayout);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ParseObject sender = mSenders.get(position);

        Date createdAt = sender.getCreatedAt();
        long now = new Date().getTime();
        String convertedDate = DateUtils.getRelativeTimeSpanString(createdAt.getTime(),
                now,
                DateUtils.SECOND_IN_MILLIS).toString();

        if((sender.getString(ParseConstants.KEY_SENDER_OBJECT_ID)).equals(ParseUser.getCurrentUser().getObjectId())){
            //message send by me
            holder.myLayout.setVisibility(View.VISIBLE);
            holder.senderLayout.setVisibility(View.GONE);
            holder.myMessage.setText(mMessage.get(position));
            holder.mySendingDate.setText(convertedDate);
        }
        else {
            //send by others
            holder.myLayout.setVisibility(View.GONE);
            holder.senderLayout.setVisibility(View.VISIBLE);
            holder.senderName.setText(mSenderName.get(position));
            holder.message.setText(mMessage.get(position));
            holder.sendingDate.setText(convertedDate);
        }


        return convertView;
    }

    public static class ViewHolder {
        TextView senderName;
        TextView message;
        TextView sendingDate;

        TextView myMessage;
        TextView mySendingDate;

        LinearLayout senderLayout;
        RelativeLayout myLayout;
    }
}
