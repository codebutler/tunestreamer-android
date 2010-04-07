/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.codebutler.tunestreamer.db;

import java.io.Serializable;

/**
 *
 * @author eric
 */
public class ServerInfo implements Serializable
{
    public String  host;
    public Integer port;
    public String  pass;
}
