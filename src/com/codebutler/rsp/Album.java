/*
 * Album.java
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

import java.util.ArrayList;
import java.util.List;

public class Album
{
    private int        mId;
    private Artist     mArtist;
    private String     mName;
    private List<Item> mItems;

    public static int sLastId = 0;

    public Album (Artist artist, String name)
    {
        mArtist = artist;
        mName   = name;

        mItems = new ArrayList<Item>();

        mId = sLastId;
        sLastId ++;
    }

    public int getId ()
    {
        return mId;
    }

    public Artist getArtist ()
    {
        return mArtist;
    }

    public String getName ()
    {
        return mName;
    }

    public List<Item> getItems ()
    {
        return mItems;
    }

    void addItem (Item item)
    {
        mItems.add(item);
    }
}
