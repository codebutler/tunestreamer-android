/*
 * Server.java
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Server
{
    private String mHostname;
    private int    mPort;
    private String mPassword;

    private int    mId;
    private String mName;
    private int    mCount;

    private ArrayList<Playlist> mPlaylists;

    public static final Integer DEFAULT_PORT = 3689;

    public static int sLastId = 0;

    public Server (String hostname, int port, String password)
    {
        mHostname = hostname;
        mPort     = port;
        mPassword = password;

        mId = sLastId;
        sLastId ++;
    }

    public int getId ()
    {
        return mId;
    }

    public String getHostname ()
    {
        return mHostname;
    }

    public int getPort ()
    {
        return mPort;
    }

    public String getName() throws Exception
    {
        checkHaveInfo();
        return mName;
    }

    public void setPassword (String value)
    {
        mPassword = value;
    }

    public String getPassword ()
    {
        return mPassword;
    }

    public int getCount () throws Exception
    {
        checkHaveInfo();
        return mCount;
    }

    public boolean isLoaded ()
    {
        return (mName != null) && (mPlaylists != null);
    }

    public Playlist getMainLibrary ()
    {
        return getPlaylist("Library");
    }

    public List<Playlist> getPlaylists ()
    {
        return mPlaylists;
    }

    public Playlist getPlaylist(int id)
    {
        for (Playlist playlist : mPlaylists) {
            if (playlist.getId() == id)
                return playlist;
        }
        return null;
    }

    public Playlist getPlaylist(String title)
    {
        for (Playlist playlist : mPlaylists) {
            if (playlist.getTitle().equals(title))
                return playlist;
        }
        return null;
    }

    public void fetchInfo () throws Exception
    {
        if (mName != null)
            throw new Exception("fetchInfo() already called!");

       String response = Util.getURL(buildUrl("info"));

       /*
        <?xml version="1.0" encoding="UTF-8" standalone="yes" ?>
        <response>
            <status>
                <errorcode>0</errorcode>
                <errorstring></errorstring>
                <records>0</records>
                <totalrecords>0</totalrecords>
            </status>
            <info>
                <count>11160</count>
                <rsp-version>1.0</rsp-version>
                <server-version>0.10</server-version>
                <name>bucket</name>
            </info>
        </response>
       */

       Document document = Util.parseXml(response);

       Element infoElement = (Element) document.getDocumentElement().getElementsByTagName("info").item(0);

       String version = Util.getChildNodeValue(infoElement, "rsp-version");
       if (!version.equals("1.0"))
           throw new Exception("Unsupported version");

       mCount = Integer.parseInt(Util.getChildNodeValue(infoElement, "count"));
       mName  = Util.getChildNodeValue(infoElement, "name");
    }

    public void fetchPlaylists () throws Exception
    {
        if (mPlaylists != null)
            throw new Exception("fetchPlaylists() already called!");

        String response = Util.getURL(buildUrl("db"));

        /*
        <?xml version="1.0" encoding="UTF-8" standalone="yes" ?>
        <response>
            <status>
                <errorcode>0</errorcode>
                <errorstring></errorstring>
                <records>269</records>
                <totalrecords>269</totalrecords>
            </status>
            <playlists>
                <playlist>
                    <id>1</id>
                    <title>Library</title>
                    <items>11160</items>
                </playlist>
                <playlist>
                    <id>2</id>
                    <title>Music</title>
                    <items>10725</items>
                </playlist>
            </playlists>
        </response>
        */

       Document document = Util.parseXml(response);

       Element playlistsElement = (Element) document.getDocumentElement()
           .getElementsByTagName("playlists").item(0);

       NodeList playlists = playlistsElement.getChildNodes();

       mPlaylists = new SortedArrayList<Playlist>(new Comparator<Playlist> () {
            public int compare(Playlist first, Playlist second) {
                return first.getTitle().compareToIgnoreCase(second.getTitle());
            }
        });

       for (int x = 0; x < playlists.getLength(); x++) {
           Element playlistElement = (Element) playlists.item(x);

           int    id    = Integer.parseInt(Util.getChildNodeValue(playlistElement, "id"));
           String title = Util.getChildNodeValue(playlistElement, "title");
           int    items = Integer.parseInt(Util.getChildNodeValue(playlistElement, "items"));

           Playlist playlist = new Playlist(this, id, title, items);

           mPlaylists.add(playlist);
       }
    }

    void checkHaveInfo () throws Exception
    {
        if (mName == null)
            throw new Exception("Call getInfo() first!");
    }

    URL buildUrl(String method) throws MalformedURLException
    {
       String port = Integer.toString(mPort);
       return new URL("http://" + mHostname + ":" + port + "/rsp/" + method);
    }
}
