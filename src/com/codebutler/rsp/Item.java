/*
 * Item.java
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

package com.codebutler.rsp;

import java.net.URL;

public class Item
{
    private Playlist mPlaylist;

    private int mId;
    private String mTitle;
    private String mArtist;
    private String mAlbum;
    private long mDuration;

    public Item (Playlist playlist)
    {
        mPlaylist = playlist;
    }

    void setTitle (String value)
    {
        mTitle = value;
    }

    public String getTitle ()
    {
        return mTitle;
    }

    void setArtist (String value)
    {
        mArtist = value;
    }

    public String getArtist ()
    {
        return mArtist;
    }

    void setAlbum (String value)
    {
        mAlbum = value;
    }

    public String getAlbum ()
    {
        return mAlbum;
    }


    void setDuration (long value)
    {
        mDuration = value;
    }

    public long getDuration ()
    {
        return mDuration;
    }

    void setId (int id)
    {
        mId = id;
    }

    public URL getURL () throws java.net.MalformedURLException
    {
        return mPlaylist.getServer().buildUrl("stream/" + Integer.toString(mId));
    }

    @Override
    public String toString ()
    {
        return mTitle;
    }
}
