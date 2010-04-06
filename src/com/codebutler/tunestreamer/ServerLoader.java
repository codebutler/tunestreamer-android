/*
 * ServerLoader.java
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
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.codebutler.rsp.FetchItemsProgressListener;
import com.codebutler.rsp.Playlist;
import com.codebutler.rsp.Server;

public class ServerLoader
{
    private static final int STATUS_FETCHING_PLAYLISTS = 1;
    private static final int STATUS_FETCHING_SONGS     = 2;
    private static final int STATUS_IMPORTING_SONGS    = 3;
    private static final int STATUS_FINISHED           = 4;
    private static final int STATUS_ERROR              = 5;

    Activity       mActivity;
    Server         mServer;
    Playlist       mLibrary;
    ProgressDialog mDialog;
    ServerLoaderListener mListener;

    public ServerLoader (Activity activity, Server server, ServerLoaderListener listener)
    {
        mActivity = activity;
        mServer  = server;
        mListener = listener;
    }

    public void load ()
    {
        mDialog = new ProgressDialog(mActivity);
        mDialog.setMessage("Connecting...");
        mDialog.setIndeterminate(true);
        mDialog.setOnCancelListener(mCancelListener);
        mDialog.show();

        mThread.start();
    }

    private Thread mThread = new Thread(new Runnable() {
        public void run() {
            try {
                mServer.fetchInfo();

                updateProgress(STATUS_FETCHING_PLAYLISTS, null);

                mServer.fetchPlaylists();

                mLibrary = mServer.getMainLibrary();

                updateProgress(STATUS_FETCHING_SONGS, null);

                mLibrary.fetchItems(new FetchItemsProgressListener() {
                    public void onProgressChange(Integer progress) {
                        updateProgress(STATUS_IMPORTING_SONGS, progress);
                    }
                });

                updateProgress(STATUS_FINISHED, null);
            } catch (Exception ex) {
                updateProgress(STATUS_ERROR, ex);
            }
        }
    });

    private OnCancelListener mCancelListener = new OnCancelListener() {
        public void onCancel(DialogInterface arg0) {
            mThread.stop();
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what) {
                case STATUS_FETCHING_PLAYLISTS:
                    mDialog.setMessage("Fetching playlists...");
                    break;
                case STATUS_FETCHING_SONGS:
                    mDialog.setMessage("Fetching songs...");
                    break;
                case STATUS_IMPORTING_SONGS:
                    Integer progress = (Integer) msg.obj;
                    if (mDialog.isIndeterminate()) {
                        ProgressDialog oldDialog = mDialog;

                        mDialog = new ProgressDialog(mActivity);
                        mDialog.setMessage("Importing songs...");
                        mDialog.setMax(mLibrary.getCount());
                        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        mDialog.setOnCancelListener(mCancelListener);
                        mDialog.show();

                        oldDialog.dismiss();
                    }
                    mDialog.setProgress(progress);
                    break;
                case STATUS_FINISHED:
                    mDialog.dismiss();
                    mListener.onFinish(mLibrary);
                    break;
                case STATUS_ERROR:
                    Exception err = (Exception) msg.obj;
                    showError(err);
                    break;
            }
        }
    };

    void updateProgress (int state, Object obj)
    {
        Message message = Message.obtain();
        message.what = state;
        message.obj  = obj;
        mHandler.sendMessage(message);
    }

    void showError (Exception ex)
    {
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

    public interface ServerLoaderListener
    {
        public void onFinish (Playlist library);
    }
}
