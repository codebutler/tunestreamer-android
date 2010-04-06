/*
 * LibraryActivity.java
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

import android.app.Dialog;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import com.codebutler.rsp.Playlist;
import com.codebutler.rsp.Server;

public class LibraryActivity extends TabActivity
{
    public static final String SERVER_ID   = "com.codebutler.tunestreamer.SERVER_ID";
    public static final String PLAYLIST_ID = "com.codebutler.tunestreamer.PLAYLIST_ID";
    public static final String ALBUM_ID    = "com.codebutler.tunestreamer.ALBUM_ID";

    private final int MENU_INFO = 1;

    private final int INFO_DIALOG = 1;

    private TabHost mTabHost;

    private Server mServer;
    private Playlist mLibrary;

    @Override
    public void onCreate (Bundle bundle)
    {
        super.onCreate(bundle);

        int serverId   = getIntent().getExtras().getInt(SERVER_ID);

        TuneStreamerApp app = (TuneStreamerApp) getApplication();
        mServer  = app.getServer(serverId);
        mLibrary = mServer.getMainLibrary();

        setContentView(R.layout.library);

        mTabHost = getTabHost();
        addTabs();

        setTitle(String.format("Browsing %s:%d", mServer.getHostname(), mServer.getPort()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuItem item = menu.add(Menu.NONE, MENU_INFO, 0, "Server Info");
        item.setIcon(android.R.drawable.ic_menu_info_details);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case MENU_INFO:
                showDialog(INFO_DIALOG);
                return true;

            default:
                return false;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id) {
            case INFO_DIALOG:
                String message = String.format("Total files: %d", mLibrary.getCount());
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Server Info");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setMessage(message);
                return builder.create();
            default:
                return null;
        }
    }

     @Override
     public boolean onSearchRequested()
     {
         Log.i("FOO", "SEARCH REQUESTED");
         Bundle appData = new Bundle();
         appData.putInt(LibraryActivity.SERVER_ID, mServer.getId());
         startSearch(null, false, appData, false);
         return true;
     }

    void addTabs ()
    {
        Intent artistsActivity   = new Intent(this, ArtistListActivity.class);
        Intent albumsActivity    = new Intent(this, AlbumListActivity.class);
        Intent songsActivity     = new Intent(this, SongListActivity.class);
        Intent playlistsActivity = new Intent(this, PlaylistsActivity.class);

        TabSpec tabSpec = mTabHost.newTabSpec("tab_artists")
        	.setIndicator("Artists", getResources().getDrawable(R.drawable.ic_tab_artists))
        	.setContent(artistsActivity);

        mTabHost.addTab(tabSpec);

        tabSpec = mTabHost.newTabSpec("tab_albums")
        	.setIndicator("Albums", getResources().getDrawable(R.drawable.ic_tab_albums))
        	.setContent(albumsActivity);

        mTabHost.addTab(tabSpec);

        tabSpec = mTabHost.newTabSpec("tab_songs")
        	.setIndicator("Songs", getResources().getDrawable(R.drawable.ic_tab_songs))
        	.setContent(songsActivity);

        mTabHost.addTab(tabSpec);

        tabSpec = mTabHost.newTabSpec("tab_playlists")
        	.setIndicator("Playlists", getResources().getDrawable(R.drawable.ic_tab_playlists))
        	.setContent(playlistsActivity);

        mTabHost.addTab(tabSpec);
    }
}
