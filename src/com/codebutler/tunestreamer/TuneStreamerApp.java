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

import com.codebutler.rsp.Server;
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
        // FIXME: Might be better to use a reliable id in the future.
        return mServers.get(id);
    }

    @Override
    public void onCreate ()
    {
        mServers = new ArrayList();
    }

    public void addServer (String host, Integer port, String password)
    {
        mServers.add(new Server(host, port, password));
    }
}
