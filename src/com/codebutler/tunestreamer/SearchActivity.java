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

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import com.codebutler.rsp.Item;
import com.codebutler.rsp.Playlist;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends SongListActivity
{
    @Override
    public void onCreate (Bundle bundle)
    {
        super.onCreate(bundle);

        final Intent queryIntent = getIntent();
        final String queryAction = queryIntent.getAction();
        final Bundle appData = queryIntent.getBundleExtra(SearchManager.APP_DATA);

        int serverId   = appData.getInt(LibraryActivity.SERVER_ID);

        TuneStreamerApp app = (TuneStreamerApp) getApplication();

        Playlist library = app.getServer(serverId).getMainLibrary();

        if (queryAction.equals(Intent.ACTION_SEARCH)) {
            final String queryString = queryIntent.getStringExtra(SearchManager.QUERY);

            try {
                List<Item> items = new ArrayList<Item>();

                for (Item item : library.getItems()) {
                    if (searchMatch(item.getTitle(),  queryString) ||
                        searchMatch(item.getAlbum(),  queryString) ||
                        searchMatch(item.getArtist(), queryString))
                    {
                        items.add(item);
                    }
                }

                setItems(items);

            } catch (Exception ex) {
                GuiUtil.showErrorAndFinish(this, ex);
            }
        }
    }

    private Boolean searchMatch (String fieldValue, String queryString)
    {
        return fieldValue.toLowerCase().contains(queryString.toLowerCase());
    }
}
