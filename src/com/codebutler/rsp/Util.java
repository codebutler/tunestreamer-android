/*
 * Util.java
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

import android.util.Log;
import java.io.CharArrayReader;
import java.io.Reader;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Util
{
    public static String getURL (URL url, String password) throws Exception
    {
        Log.i("Util.getURL", url.toString());

        DefaultHttpClient client = new DefaultHttpClient();

        if (password != null && password.length() > 0) {
            UsernamePasswordCredentials creds;
            creds = new UsernamePasswordCredentials("user", password);
            client.getCredentialsProvider().setCredentials(AuthScope.ANY, creds);
        }

        HttpGet method = new HttpGet(url.toURI());
        BasicResponseHandler responseHandler = new BasicResponseHandler();
        return client.execute(method, responseHandler);
    }

    public static Document parseXml (String xml) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Reader reader = new CharArrayReader(xml.toCharArray());
        return builder.parse(new org.xml.sax.InputSource(reader));
    }

    public static String getChildNodeValue (Element element, String tagName)
    {
        return element.getElementsByTagName(tagName).item(0).getFirstChild().getNodeValue();
    }
}
