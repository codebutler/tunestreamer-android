/*
 * PlaylistActivity.java
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

import android.os.Bundle;
import android.os.Message;
import com.codebutler.rsp.FetchItemsProgressListener;
import com.codebutler.rsp.Playlist;

public class PlaylistActivity extends SongListActivity
{
    private Playlist mPlaylist;
    private LoadingHandler mHandler;

    @Override
    public void onCreate (Bundle bundle)
    {
        super.onCreate(bundle);
        
        try {
            int serverId   = getIntent().getExtras().getInt(LibraryActivity.SERVER_ID);
            int playlistId = getIntent().getExtras().getInt(LibraryActivity.PLAYLIST_ID);

            TuneStreamerApp app = (TuneStreamerApp) getApplication();
            mPlaylist = app.getServer(serverId).getPlaylist(playlistId);

            setTitle(mPlaylist.getTitle());

            if (!mPlaylist.hasItems()) {
                mHandler = new LoadingHandler(this, new LoadingHandler.LoadingListener() {
                    public void onFinish() {
                        try {
                            setItems(mPlaylist.getItems());
                            //((ArrayAdapter)getListAdapter()).notifyDataSetChanged();
                        } catch (Exception ex) {
                            GuiUtil.showErrorAndFinish(PlaylistActivity.this, ex);
                        }
                    }
                });
                mHandler.setPlaylist(mPlaylist);

                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        try {
                            updateProgress(LoadingHandler.STATUS_FETCHING_SONGS, null);

                            mPlaylist.fetchItems(new FetchItemsProgressListener() {
                                public void onProgressChange(Integer progress) {
                                    updateProgress(LoadingHandler.STATUS_IMPORTING_SONGS, progress);
                                }
                            });

                            updateProgress(LoadingHandler.STATUS_FINISHED, null);
                        } catch (Exception ex) {
                            updateProgress(LoadingHandler.STATUS_ERROR, ex);
                        }
                    }
                });
                thread.start();
            } else {
                setItems(mPlaylist.getItems());
            }
        } catch (Exception ex) {
            GuiUtil.showErrorAndFinish(this, ex);
        }
    }

    private void updateProgress (int state, Object obj)
    {
        Message message = Message.obtain();
        message.what = state;
        message.obj  = obj;
        mHandler.sendMessage(message);
    }
}
