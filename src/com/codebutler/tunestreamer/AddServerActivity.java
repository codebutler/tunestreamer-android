/*
 * AddServerActivity.java
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

import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

public class AddServerActivity extends PreferenceActivity
{
    EditTextPreference mHostname;
    EditTextPreference mPort;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // ToDo add your GUI initialization code here
        addPreferencesFromResource(R.xml.server_prefs);

        mHostname = (EditTextPreference) findPreference("hostname");
        bindLabelToValue(mHostname);

        mPort = (EditTextPreference) findPreference("port");
        bindLabelToValue(mPort);

        addButtons();
    }

    void bindLabelToValue (EditTextPreference preference)
    {
        preference.setSummary(preference.getText());
        preference.setOnPreferenceChangeListener(
            new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference p, Object newValue) {
                    p.setSummary((String) newValue);
                    return true;
                }
            });
    }

    // Based on code from the DeskClock app
    void addButtons ()
    {
        getListView().setItemsCanFocus(true);

        FrameLayout content = (FrameLayout) getListView().getParent();

        ListView lv = getListView();
        content.removeView(lv);

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT);
        lp.weight = 1;
        ll.addView(lv, lp);

        View v = LayoutInflater.from(this).inflate(R.layout.server_prefs_buttons, ll);

        Button b = (Button) v.findViewById(R.id.server_save);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("hostname", mHostname.getText());
                intent.putExtra("port", Integer.parseInt(mPort.getText()));
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        b = (Button) v.findViewById(R.id.server_cancel);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        setContentView(ll);
    }
}
