/*
 * PlaylistsActivity.java
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.codebutler.rsp.Playlist;
import com.codebutler.rsp.Server;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaylistsActivity extends ListActivity
{
    Server mServer;

    @Override
    public void onCreate (Bundle bundle)
    {
        super.onCreate(bundle);

        getListView().setFastScrollEnabled(true);

        LibraryActivity parentActivity = (LibraryActivity)getParent();
        int serverId = parentActivity.getIntent().getExtras().getInt(LibraryActivity.SERVER_ID);

        TuneStreamerApp app = (TuneStreamerApp) getApplication();
        mServer = app.getServer(serverId);
       
        List<String> ignore = Arrays.asList(new String[] {
            "Library", "Music", "Movies", "TV Shows"
        });

        List<Playlist> playlists = new ArrayList<Playlist>();
        for (Playlist p : mServer.getPlaylists()) {
            if (!ignore.contains(p.getTitle()))
                playlists.add(p);
        }

        setListAdapter(new PlaylistsAdapter(this, playlists));
    }

    @Override
    public boolean onSearchRequested()
    {
        Bundle appData = new Bundle();
        appData.putInt(LibraryActivity.SERVER_ID, mServer.getId());
        startSearch(null, false, appData, false);
        return true;
     }

    @Override
    protected void onListItemClick (ListView list, View view, int position, long id)
    {
        try {
            Playlist playlist = (Playlist) list.getItemAtPosition(position);

            Intent intent = new Intent(this, PlaylistActivity.class);
            intent.putExtra(LibraryActivity.SERVER_ID, mServer.getId());
            intent.putExtra(LibraryActivity.PLAYLIST_ID, playlist.getId());
            startActivity(intent);

        } catch (Exception ex) {
            GuiUtil.showErrorAndFinish(this, ex);
        }
    }

    private class PlaylistsAdapter extends ArrayAdapter<Playlist>
    {
        public PlaylistsAdapter (Context context, List<Playlist> playlists)
        {
            super(context, 0, playlists);
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

            Playlist playlist = getItem(position);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
            imageView.setImageResource(R.drawable.ic_mp_playlist_list);

            TextView textView = (TextView) convertView.findViewById(R.id.line1);
            textView.setText(playlist.getTitle());

            convertView.findViewById(R.id.line2).setVisibility(View.GONE);

            return convertView;
        }
    }
}
