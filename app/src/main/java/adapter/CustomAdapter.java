package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import a.a.groupchat.R;

/**
 * Created by Arpit on 30/03/15.
 */
public class CustomAdapter extends ArrayAdapter<String> {

    protected Context mContext;
    protected List<String> mTitle;
    protected List<String> mNote;

    public CustomAdapter(Context context, List<String> title, List<String> note) {
        super(context, R.layout.custom_list_item, title);
        mContext = context;
        mTitle = title;
        mNote = note;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.custom_list_item, null);
            holder = new ViewHolder();

            holder.groupName = (TextView) convertView.findViewById(R.id.header_list);
            holder.groupDesc = (TextView) convertView.findViewById(R.id.details_list);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.groupName.setText(mTitle.get(position));
        holder.groupDesc.setText(mNote.get(position));

        return convertView;
    }

    public static class ViewHolder {
        TextView groupName;
        TextView groupDesc;
    }
}
