/*
 * GuiUtil.java
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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import com.codebutler.rsp.Item;
import java.net.MalformedURLException;

/**
 *
 * @author eric
 */
public class GuiUtil
{
    public static void fixItemHeight (Activity activity, View view)
    {
        // FIXME: For some reason even though the view has layout_height and
        // layout_width set in the XML, getLayoutParams() is returning null,
        // causing the wrong row height to be used.
        LayoutParams params = view.getLayoutParams();
        if (params == null) {
            view.setLayoutParams(new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                activity.getResources().getDimensionPixelSize(R.dimen.row_height),
                0));
        }
    }

    public static void showErrorAndFinish (final Activity activity, Exception ex)
    {
        String text = (ex.getMessage() == null) ? ex.toString() : ex.getMessage();
        Log.e("LibraryListActivity", text);
        ex.printStackTrace();

        new AlertDialog.Builder(activity)
            .setMessage(text)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok, new OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    activity.finish();
                }
            })
            .show();
    }


    public static void playItem (Context context, Item item)
    {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri data = Uri.parse(item.getURL().toString());
            intent.setDataAndType(data, "audio/mp3");
            context.startActivity(intent);
        } catch (MalformedURLException ex) {
            new AlertDialog.Builder(context).setMessage(ex.getMessage())
                                            .show();
        }
    }

}
