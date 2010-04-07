/*
 * SongListActivity.java
 *
 * Copyright (C) 2010 Eric Butler
 *
 *  Authors:
 *    Eric Butler <eric@codebutler.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.codebutler.tunestreamer;

import com.codebutler.tunestreamer.util.MusicUtils;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.codebutler.rsp.Item;
import com.codebutler.rsp.Server;
import java.net.MalformedURLException;
import java.util.List;

public class SongListActivity extends ListActivity
{
    private final int PLAY_ITEM_ID = 1;
    private final int COPY_ITEM_ID = 2;

    Server mServer;

    @Override
    public void onCreate (Bundle bundle)
    {
        super.onCreate(bundle);

        registerForContextMenu(getListView());

        getListView().setFastScrollEnabled(true);

        // Are we inside the main tab container?
        if (getParent() != null) {
            try {
                TuneStreamerApp app = (TuneStreamerApp) getApplication();
                LibraryActivity parentActivity = (LibraryActivity)getParent();
                int serverId   = parentActivity.getIntent().getExtras().getInt(LibraryActivity.SERVER_ID);
                mServer = app.getServer(serverId);
                List<Item> items = mServer.getMainLibrary().getItems();
                setItems(items);
            } catch (Exception ex) {
                GuiUtil.showErrorAndFinish(this, ex);
            }
        }
    }

    @Override
    public boolean onSearchRequested()
    {
        Bundle appData = new Bundle();
        appData.putInt(LibraryActivity.SERVER_ID, mServer.getId());
        startSearch(null, false, appData, false);
        return true;
    }

    protected void setItems (List<Item> items)
    {
        setListAdapter(new SongListAdapter(this, items));
    }

    @Override
    protected void onListItemClick (ListView list, View view, int position, long id)
    {
        try {
            Item item = (Item) list.getItemAtPosition(position);
            GuiUtil.playItem(this, item);
        } catch (Exception ex) {
            GuiUtil.showErrorAndFinish(this, ex);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
    {
        menu.add(0, PLAY_ITEM_ID, 0, "Play");
        menu.add(0, COPY_ITEM_ID, 0, "Copy URL");
    }

    @Override
    public boolean onContextItemSelected(MenuItem menuItem)
    {
        AdapterContextMenuInfo contextInfo = (AdapterContextMenuInfo) menuItem.getMenuInfo();

        Item item = (Item) getListAdapter().getItem((int)contextInfo.id);

        switch (menuItem.getItemId()) {
            case PLAY_ITEM_ID:
                GuiUtil.playItem(this, item);
                break;

            case COPY_ITEM_ID:
                try {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    clipboard.setText(item.getURL().toString());
                    Toast.makeText(this, "Copied!", Toast.LENGTH_SHORT).show();
                } catch (MalformedURLException ex) {
                    Toast.makeText(this, "URL is invalid.", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        return false;
    }

    private class SongListAdapter extends ArrayAdapter<Item>
    {
        public SongListAdapter (Context context, List<Item> items)
        {
            super(context, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            Activity activity = (Activity) getContext();
            LayoutInflater inflater = activity.getLayoutInflater();

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.track_list_item, null);
                GuiUtil.fixItemHeight(activity, convertView);
            }

            Item item = getItem(position);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
            imageView.setVisibility(View.GONE);

            TextView textView = null;

            textView = (TextView) convertView.findViewById(R.id.line1);
            textView.setText(item.getTitle());

            textView = (TextView) convertView.findViewById(R.id.line2);
            textView.setText(item.getArtist());

            textView = (TextView) convertView.findViewById(R.id.duration);
            textView.setText(MusicUtils.makeTimeString(activity, item.getDuration()));

            return convertView;
        }
    }
}
