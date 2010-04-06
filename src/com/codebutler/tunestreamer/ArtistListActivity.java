/*
 * ArtistListActivity.java
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
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import com.codebutler.rsp.Album;
import com.codebutler.rsp.Artist;
import com.codebutler.rsp.Playlist;

public class ArtistListActivity extends ExpandableListActivity
{
    private Playlist mLibrary;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        getExpandableListView().setFastScrollEnabled(true);

        LibraryActivity parentActivity = (LibraryActivity)getParent();
        int serverId   = parentActivity.getIntent().getExtras().getInt(LibraryActivity.SERVER_ID);

        TuneStreamerApp app = (TuneStreamerApp) getApplication();
        mLibrary = app.getServer(serverId).getMainLibrary();

        setListAdapter(new ArtistListAdapter(this, mLibrary));
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
                                int groupPosition, int childPosition, long id)
    {
        try {
            Artist artist = mLibrary.getArtists().get(groupPosition);
            Album album = artist.getAlbums().get(childPosition);

            Intent intent = new Intent(this, AlbumActivity.class);
            intent.putExtra(LibraryActivity.SERVER_ID, mLibrary.getServer().getId());
            intent.putExtra(LibraryActivity.ALBUM_ID, album.getId());
            startActivity(intent);

            return true;
        } catch (Exception ex) {
            Log.e("ArtistListAdapter", ex.toString());
            ex.printStackTrace();
            return super.onChildClick(parent, v, groupPosition, childPosition, id);
        }
    }

    @Override
    public boolean onSearchRequested()
    {
        Bundle appData = new Bundle();
        appData.putInt(LibraryActivity.SERVER_ID, mLibrary.getServer().getId());
        startSearch(null, false, appData, false);
        return true;
    }

    private class ArtistListAdapter extends BaseExpandableListAdapter
    {
        Activity mActivity;
        Playlist mPlaylist;

        public ArtistListAdapter (ArtistListActivity activity, Playlist playlist)
        {
            mActivity = activity;
            mPlaylist = playlist;
        }

        public Object getChild(int groupPosition, int childPosition)
        {
            try {
                Artist artist = (Artist) getGroup(groupPosition);
                return artist.getAlbums().get(childPosition);
            } catch (Exception ex) {
                GuiUtil.showErrorAndFinish(mActivity, ex);
                return null;
            }
        }

        public long getChildId(int groupPosition, int childPosition)
        {
            return childPosition;
        }

        public View getChildView(int groupPosition, int childPosition,
                                 boolean isExpanded, View convertView,
                                 ViewGroup parent)
        {
            try {
                if (convertView == null) {
                    LayoutInflater inflater = mActivity.getLayoutInflater();
                    convertView = inflater.inflate(R.layout.track_list_item_child, null);
                    GuiUtil.fixItemHeight(mActivity, convertView);
                }

                Artist artist = (Artist)getGroup(groupPosition);
                Album album = artist.getAlbums().get(childPosition);

                TextView textView = null;

                textView = (TextView) convertView.findViewById(R.id.line1);
                textView.setText(album.getName());

                textView = (TextView) convertView.findViewById(R.id.line2);
                textView.setText(album.getItems().size() + " items");

                ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
                imageView.setBackgroundResource(R.drawable.albumart_mp_unknown_list);
                imageView.setPadding(0, 0, 1, 0);

                return convertView;
            } catch (Exception ex) {
                GuiUtil.showErrorAndFinish(mActivity, ex);
                return null;
            }
        }

        public int getChildrenCount(int groupPosition)
        {
            try {
                Artist artist = (Artist) getGroup(groupPosition);
                return artist.getAlbums().size();
            } catch (Exception ex) {
                GuiUtil.showErrorAndFinish(mActivity, ex);
                return 0;
            }
        }

        public Object getGroup(int groupPosition)
        {
            try {
                return mPlaylist.getArtists().get(groupPosition);
            } catch (Exception ex) {
                GuiUtil.showErrorAndFinish(mActivity, ex);
                return 0;
            }
        }

        public int getGroupCount()
        {
            try {
                return mPlaylist.getArtists().size();
            } catch (Exception ex) {
                GuiUtil.showErrorAndFinish(mActivity, ex);
                return 0;
            }
        }

        public long getGroupId(int groupPosition)
        {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent)
        {
            if (convertView == null) {
                LayoutInflater inflater = mActivity.getLayoutInflater();
                convertView = inflater.inflate(R.layout.track_list_item_group, null);
                GuiUtil.fixItemHeight(mActivity, convertView);
            }

            Artist artist = (Artist) getGroup(groupPosition);
            String groupCount = Integer.toString(artist.getAlbums().size()) + " albums";

            TextView text1 = (TextView) convertView.findViewById(R.id.line1);
            text1.setText(artist.getName());

            TextView text2 = (TextView) convertView.findViewById(R.id.line2);
            text2.setText(groupCount);

            return convertView;
        }

        public boolean hasStableIds()
        {
            return true;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition)
        {
            return true;
        }
    }
}
