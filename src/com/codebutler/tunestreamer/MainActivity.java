/*
 * MainActivity.java
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

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.codebutler.rsp.Playlist;
import com.codebutler.rsp.Server;
import java.util.List;

public class MainActivity extends ListActivity
{
    private final int MENU_ADD_SERVER = 1;
    private final int ACTIVITY_ADD = 1;

    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);

        setTitle("Select Server");

        TuneStreamerApp app = (TuneStreamerApp) getApplication();

        setListAdapter(new ServerListAdapter(this, app.getServers()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuItem item = menu.add(Menu.NONE, MENU_ADD_SERVER, 0, "Add Server");
        item.setIcon(android.R.drawable.ic_menu_add);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == MENU_ADD_SERVER) {
            Intent intent = new Intent(this, AddServerActivity.class);
            startActivityForResult(intent, ACTIVITY_ADD);
            return true;
        }
        return false;
    }

    @Override
    protected void onListItemClick (ListView list, View view, int position, long id)
    {
        final Server server = (Server) list.getItemAtPosition(position);
        new ServerLoader(this, server, new ServerLoader.ServerLoaderListener () {
            public void onFinish (Playlist library) {
                Intent intent = new Intent(MainActivity.this, LibraryActivity.class);
                intent.putExtra(LibraryActivity.SERVER_ID, server.getId());
                intent.putExtra(LibraryActivity.PLAYLIST_ID, library.getId());
                startActivity(intent);
            }
        }).load();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode) {
            case ACTIVITY_ADD:
                if (resultCode == RESULT_OK) {
                    String hostname = data.getExtras().getString("hostname");
                    Integer port    = data.getExtras().getInt("port");

                    TuneStreamerApp app = (TuneStreamerApp) getApplication();
                    app.addServer(hostname, port, null);

                    ((ServerListAdapter)getListAdapter()).notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public boolean onSearchRequested()
    {
        /* Can't search unless viewing library */
        return false;
    }

    private class ServerListAdapter extends ArrayAdapter<Server>
    {
        public ServerListAdapter (Context context, List<Server> items)
        {
            super(context, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            Activity activity = (Activity) getContext();
            LayoutInflater inflater = activity.getLayoutInflater();

            if (convertView == null) {
                convertView = inflater.inflate(android.R.layout.simple_list_item_1, null);
                //GuiUtil.fixItemHeight(activity, convertView);
            }

            Server server = (Server) getItem(position);

            TextView textView = (TextView) convertView.findViewById(android.R.id.text1);

            if (server.getPort() == Server.DEFAULT_PORT)
                textView.setText(server.getHostname());
            else
                textView.setText(String.format("%s:%d", server.getHostname(), server.getPort()));

            return convertView;
        }
    }
}
