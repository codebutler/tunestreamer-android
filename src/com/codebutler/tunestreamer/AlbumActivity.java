/*
 * AlbumActivity.java
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
import com.codebutler.rsp.Album;

public class AlbumActivity extends SongListActivity
{
    Album mAlbum;

    @Override
    public void onCreate (Bundle bundle)
    {
        super.onCreate(bundle);

        int serverId = getIntent().getExtras().getInt(LibraryActivity.SERVER_ID);
        int albumId  = getIntent().getExtras().getInt(LibraryActivity.ALBUM_ID);

        TuneStreamerApp app = (TuneStreamerApp) getApplication();
        mAlbum = app.getServer(serverId).getMainLibrary().getAlbum(albumId);

        setTitle(mAlbum.getName());

        setItems(mAlbum.getItems());
    }
}
