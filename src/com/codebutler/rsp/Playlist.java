/*
 * Playlist.java
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

import android.sax.Element;
import android.sax.ElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Log;
import android.util.Xml;
import com.codebutler.tunestreamer.util.SortedArrayList;
import java.io.InputStream;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xml.sax.Attributes;

public class Playlist
{
    private Server mServer;

    private int    mId;
    private String mTitle;
    private int    mCount;

    private List<Item> mItems;
    private List<Artist> mOrderedArtists;
    private Map<String, Artist> mArtists;
    private List<Album> mAlbums;

    // Used while importing
    private Item mCurrentItem = null;

    public Playlist (Server server, int id, String title, int count)
    {
        mServer = server;

        mId    = id;
        mTitle = title;
        mCount = count;
    }

    public Server getServer ()
    {
        return mServer;
    }

    public int getId ()
    {
        return mId;
    }

    public String getTitle ()
    {
        return mTitle;
    }

    public int getCount ()
    {
        return mCount;
    }

    public Boolean hasItems ()
    {
        return (mItems != null);
    }

    public List<Item> getItems () throws Exception
    {
        if (mItems == null)
            throw new Exception("Call fetchItems() first!");

        return mItems;
    }

    public List<Artist> getArtists () throws Exception
    {
        return mOrderedArtists;
    }

    public Artist getArtist (String artistName) throws Exception
    {
        return mArtists.get(artistName);
    }

    public List<Album> getAlbums ()
    {
        return mAlbums;
    }

    public Album getAlbum (int albumId)
    {
        for (Album album : mAlbums) {
            if (album.getId() == albumId)
                return album;
        }
        return null;
    }

    public void fetchItems (final FetchItemsProgressListener progressListener) throws Exception
    {
        if (mItems != null)
            throw new Exception("fetchItems() already called!");

        mItems = new SortedArrayList<Item>(new Comparator<Item> () {
            public int compare(Item first, Item second) {
                return first.getTitle().compareToIgnoreCase(second.getTitle());
            }
        });

        mOrderedArtists = new SortedArrayList<Artist>(new Comparator<Artist> () {
            public int compare(Artist first, Artist second) {
                return first.getName().compareToIgnoreCase(second.getName());
            }
        });

        mArtists = new HashMap<String, Artist>();

        mAlbums = new SortedArrayList<Album>(new Comparator<Album> () {
            public int compare(Album first, Album second) {
                return first.getName().compareToIgnoreCase(second.getName());
            }
        });

        URL url = mServer.buildUrl("db/" + Integer.toString(mId));
        Log.d("FetchItems", url.toString());
        InputStream stream = url.openStream();

        /*
         <?xml version="1.0" encoding="UTF-8" standalone="yes" ?>
         <response>
            <status>
                <errorcode>0</errorcode>
                <errorstring></errorstring>
                <records>0</records>
                <totalrecords>1</totalrecords>
            </status>
            <items>
                <item>
                    <id>1</id>
                    <title>Rock Robotic (Osx Mix)</title>
                    <artist>Oscillator X</artist>
                    <album>Techno * Techyes</album>
                    <genre>Dance &amp; DJ</genre>
                    <type>mp3</type>
                    <bitrate>192</bitrate>
                    <samplerate>44100</samplerate>
                    <song_length>61440</song_length>
                    <file_size>1474560</file_size>
                    <year>0</year>
                    <track>15</track>
                    <total_tracks>0</total_tracks>
                    <disc>0</disc>
                    <total_discs>0</total_discs>
                    <bpm>0</bpm>
                    <compilation>0</compilation>
                    <rating>0</rating>
                    <play_count>4</play_count>
                    <description>MPEG audio file</description>
                    <time_added>1270096204</time_added>
                    <time_modified>1270078936</time_modified>
                    <time_played>1270156178</time_played>
                    <disabled>0</disabled>
                    <codectype>mpeg</codectype>
                </item>
            </items>
         </response>
         */

        RootElement rootElement = new RootElement("response");
        Element itemsElement  = rootElement.getChild("items");
        Element itemElement   = itemsElement.getChild("item");
        Element idElement     = itemElement.getChild("id");
        Element titleElement  = itemElement.getChild("title");
        Element artistElement = itemElement.getChild("artist");
        Element albumElement = itemElement.getChild("album");
        Element lengthElement = itemElement.getChild("song_length");

        itemElement.setElementListener(new ElementListener() {
            public void start(Attributes arg0) {
                mCurrentItem = new Item(Playlist.this);
            }

            public void end() {
                mItems.add(mCurrentItem);

                // Artist cache
                String artistName = mCurrentItem.getArtist();
                if (artistName != null && artistName.trim().length() > 0) {
                    Artist artist = mArtists.get(artistName);
                    if (artist == null) {
                        artist = new Artist(artistName);
                        mArtists.put(artistName, artist);
                        mOrderedArtists.add(artist);
                    }

                    // Album cache
                    // FIXME: This should support compilation albums!
                    String albumName = mCurrentItem.getAlbum();
                    if (albumName != null && albumName.trim().length() > 0) {
                        Album album = artist.getAlbum(albumName);
                        if (album == null) {
                            album = new Album(artist, albumName);
                            artist.addAlbum(album);
                            mAlbums.add(album);
                        }
                        album.addItem(mCurrentItem);
                    }
                }

                mCurrentItem = null;

                if (progressListener != null) {
                    if ((mItems.size() % 100) == 0)
                        progressListener.onProgressChange(mItems.size());
                }
            }
        });

        idElement.setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                mCurrentItem.setId(Integer.parseInt(body));
            }
        });

        titleElement.setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                mCurrentItem.setTitle(body);
            }
        });

        artistElement.setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                mCurrentItem.setArtist(body);
            }
        });

        albumElement.setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                mCurrentItem.setAlbum(body);
            }
        });

        lengthElement.setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                mCurrentItem.setDuration(Long.parseLong(body) / 1000);
            }
        });
        android.util.Xml.parse(stream, Xml.Encoding.UTF_8, rootElement.getContentHandler());
    }
}
