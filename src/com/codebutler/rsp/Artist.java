/*
 * Artist.java
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

import com.codebutler.tunestreamer.util.SortedArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Artist
{
    String      mName;
    List<Album> mOrderedAlbums;
    Map<String, Album> mAlbums;

   public Artist (String name)
   {
       mName = name;
       mOrderedAlbums = new SortedArrayList<Album>(new Comparator<Album> () {
            public int compare(Album first, Album second) {
                return first.getName().compareToIgnoreCase(second.getName());
            }
        });

       mAlbums = new HashMap<String, Album>();
   }

   public String getName ()
   {
       return mName;
   }

   public List<Album> getAlbums()
   {
       return mOrderedAlbums;
   }

   public Album getAlbum(String albumName)
   {
      return mAlbums.get(albumName);
   }

   void addAlbum (Album album)
   {
       mAlbums.put(album.getName(), album);
       mOrderedAlbums.add(album);
   }
}
