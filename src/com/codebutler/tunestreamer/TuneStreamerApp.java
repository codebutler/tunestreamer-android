/*
 * TuneStreamerApp.java
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

import android.util.Log;
import com.codebutler.rsp.Server;
import com.codebutler.tunestreamer.db.ServerInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class TuneStreamerApp extends android.app.Application
{
    private List<Server> mServers;
//    private Server mCurrentServer;

    public List<Server> getServers ()
    {
        return mServers;
    }

    public Server getServer (int id)
    {
        for (Server server : mServers) {
            if (server.getId() == id)
                return server;
        }
        return null;
    }

    @Override
    public void onCreate ()
    {
        mServers = new ArrayList();

        File file = new File(getFilesDir(), "servers.data");

        if (file.exists()) {
            try {
                FileInputStream fileStream = new FileInputStream(file);
                ObjectInputStream objectStream = new ObjectInputStream(fileStream);

                List<ServerInfo> servers = (List<ServerInfo>) objectStream.readObject();
                for (ServerInfo serverInfo : servers) {
                    addServer(serverInfo.host, serverInfo.port, serverInfo.pass);
                }
            } catch (Exception ex) {
                Log.e("TuneStreamerApp", "Failed to load servers: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public void addServer (String host, Integer port, String password) throws Exception
    {
        mServers.add(new Server(host, port, password));
        saveServers();
    }

    public void deleteServer (Server server) throws Exception
    {
        for (Server thisServer : mServers) {
            if (thisServer == server) {
                mServers.remove(server);
                return;
            }
        }
        saveServers();
    }

    void saveServers () throws Exception
    {
        File file = new File(getFilesDir(), "servers.data");

        FileOutputStream fileStream = new FileOutputStream(file);
        ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);

        List<ServerInfo> servers = new ArrayList<ServerInfo>();
        for (Server server : getServers()) {
            ServerInfo info = new ServerInfo();
            info.host = server.getHostname();
            info.port = server.getPort();
            info.pass = server.getPassword();
            servers.add(info);
        }
        objectStream.writeObject(servers);
    }
}
