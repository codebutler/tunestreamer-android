/*
 * AlbumListActivity.java
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.codebutler.rsp.Album;
import com.codebutler.rsp.Playlist;
import java.util.List;

public class AlbumListActivity extends ListActivity
{
    private Playlist mLibrary;

    @Override
    public void onCreate (Bundle bundle)
    {
        super.onCreate(bundle);

        getListView().setFastScrollEnabled(true);

        LibraryActivity parentActivity = (LibraryActivity)getParent();
        int serverId   = parentActivity.getIntent().getExtras().getInt(LibraryActivity.SERVER_ID);
        int playlistId = parentActivity.getIntent().getExtras().getInt(LibraryActivity.PLAYLIST_ID);

        TuneStreamerApp app = (TuneStreamerApp) getApplication();
        mLibrary = app.getServer(serverId).getPlaylist(playlistId);

        setListAdapter(new AlbumListAdapter(this, mLibrary.getAlbums()));
    }

    @Override
    public boolean onSearchRequested()
    {
        Bundle appData = new Bundle();
        appData.putInt(LibraryActivity.SERVER_ID, mLibrary.getServer().getId());
        startSearch(null, false, appData, false);
        return true;
     }

    @Override
    protected void onListItemClick (ListView list, View view, int position, long id)
    {
        try {
            Album album = (Album) list.getItemAtPosition(position);

            Intent intent = new Intent(this, AlbumActivity.class);
            intent.putExtra(LibraryActivity.SERVER_ID, mLibrary.getServer().getId());
            intent.putExtra(LibraryActivity.PLAYLIST_ID, mLibrary.getId());
            intent.putExtra(LibraryActivity.ALBUM_ID, album.getId());
            startActivity(intent);
        } catch (Exception ex) {
            GuiUtil.showErrorAndFinish(this, ex);
        }
    }

    private class AlbumListAdapter extends ArrayAdapter<Album>
    {
        BitmapDrawable mDefaultAlbumIcon;

        public AlbumListAdapter (Context context, List<Album> items)
        {
            super(context, 0, items);

            Bitmap b = BitmapFactory.decodeResource(getContext().getResources(),
                    R.drawable.albumart_mp_unknown_list);
            mDefaultAlbumIcon = new BitmapDrawable(getContext().getResources(), b);
            mDefaultAlbumIcon.setFilterBitmap(false);
            mDefaultAlbumIcon.setDither(false);
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

            Album album = getItem(position);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
            imageView.setBackgroundDrawable(mDefaultAlbumIcon);
            imageView.setPadding(0, 0, 1, 0);

            TextView textView = null;

            textView = (TextView) convertView.findViewById(R.id.line1);
            textView.setText(album.getName());

            textView = (TextView) convertView.findViewById(R.id.line2);
            textView.setText(album.getArtist().getName());

            return convertView;
        }
    }
}

