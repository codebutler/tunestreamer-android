/*
 * LoadingHandler.java
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
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Message;
import android.util.Log;
import android.os.Handler;
import com.codebutler.rsp.Playlist;

public class LoadingHandler extends Handler
{
    static final int STATUS_FETCHING_INFO      = 1;
    static final int STATUS_FETCHING_PLAYLISTS = 2;
    static final int STATUS_FETCHING_SONGS     = 3;
    static final int STATUS_IMPORTING_SONGS    = 4;
    static final int STATUS_FINISHED           = 5;
    static final int STATUS_ERROR              = 6;

    private Playlist mPlaylist;
    private Activity mActivity;
    private LoadingListener mListener;
    private ProgressDialog mDialog;

    private OnCancelListener mCancelListener = new OnCancelListener() {
        @Override
        public void onCancel(DialogInterface arg0) {
          Thread.currentThread().stop();;
        }
    };

    public LoadingHandler (Activity activity, LoadingListener listener)
    {
        mActivity = activity;
        mListener = listener;
    }

    public void setPlaylist (Playlist playlist)
    {
        mPlaylist = playlist;
    }

    @Override
    public void handleMessage(Message msg)
    {
        switch (msg.what) {
            case STATUS_FETCHING_INFO:
                ensureDialog(true);
                mDialog.setMessage("Connecting...");

            case STATUS_FETCHING_PLAYLISTS:
                ensureDialog(true);
                mDialog.setMessage("Fetching playlists...");
                break;

            case STATUS_FETCHING_SONGS:
                ensureDialog(true);
                mDialog.setMessage("Fetching songs...");
                break;

            case STATUS_IMPORTING_SONGS:
                Integer progress = (Integer) msg.obj;
                ensureDialog(false);
                mDialog.setMessage("Importing songs...");
                mDialog.setMax(mPlaylist.getCount());
                mDialog.setProgress(progress);
                break;

            case STATUS_FINISHED:
                if (mDialog != null)
                    mDialog.dismiss();
                mListener.onFinish();
                break;
                
            case STATUS_ERROR:
                Exception err = (Exception) msg.obj;
                showError(err);
                break;
        }
    }

    private void ensureDialog (boolean indeterminate)
    {
        if (mDialog == null || mDialog.isIndeterminate() != indeterminate) {
            if (mDialog != null)
                mDialog.dismiss();

            mDialog = new ProgressDialog(mActivity);
            mDialog.setOnCancelListener(mCancelListener);
            mDialog.setIndeterminate(indeterminate);

            if (!indeterminate)
                mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

            mDialog.show();
        }
    }

    private void showError (Exception ex)
    {
        if (mDialog != null)
            mDialog.dismiss();

        String text = (ex.getMessage() == null) ? ex.toString() : ex.getMessage();
        Log.e("LibraryActivity", text);
        ex.printStackTrace();

        new AlertDialog.Builder(mActivity)
            .setMessage(text)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok, null)
            .show();
    }
    
    public interface LoadingListener
    {
        public void onFinish ();
    }
}
