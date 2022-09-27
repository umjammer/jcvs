/*
 ** Copyright (c) 1998 by Timothy Gerard Endres
 ** <mailto:time@ice.com>  <http://www.ice.com>
 **
 ** This program is free software.
 **
 ** You may redistribute it and/or modify it under the terms of the GNU
 ** General Public License as published by the Free Software Foundation.
 ** Version 2 of the license should be included with this distribution in
 ** the file LICENSE, as well as License.html. If the license is not
 ** included	with this distribution, you may find a copy at the FSF web
 ** site at 'www.gnu.org' or 'www.fsf.org', or you may write to the
 ** Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139 USA.
 **
 ** THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND,
 ** NOT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR
 ** OF THIS SOFTWARE, ASSUMES _NO_ RESPONSIBILITY FOR ANY
 ** CONSEQUENCE RESULTING FROM THE USE, MODIFICATION, OR
 ** REDISTRIBUTION OF THIS SOFTWARE.
 **
 */

package com.ice.jcvsii;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import javax.activation.CommandObject;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.swing.JOptionPane;

import com.ice.cvsc.CVSLog;
import com.ice.util.StringUtilities;


/**
 * Implements a consistent exec() interface.
 *
 * @author Tim Endres,
 * <a href="mailto:time@gjt.org">time@gjt.org</a>
 */

public
class ExecViewer
        extends Thread
        implements CommandObject {
    private Process proc = null;

    BufferedReader errRdr;
    BufferedReader outRdr;


    public ExecViewer() {
    }

    /**
     * the CommandObject method to accept our DataHandler
     *
     * @param dh The datahandler used to get the content.
     */
    public void
    setCommandContext(String verb, DataHandler dh)
            throws IOException {
        DataSource ds = dh.getDataSource();

        // REVIEW
        // UNDONE
        // This code is worthless, fix it!
        //
        String fileName = "unknown";
        if (ds instanceof FileDataSource) {
            FileDataSource fds = (FileDataSource) ds;
            fileName = fds.getFile().getPath();
        }

        this.exec(verb, dh);
    }

    public void
    exec(String verb, DataHandler dh) {
        String cmdSpec = null;
        String extension = null;

        DataSource ds = dh.getDataSource();

        if (!(ds instanceof FileDataSource)) {
            // UNDONE
            return;
        }

        FileDataSource fds = (FileDataSource) ds;
        File file = fds.getFile();

        String name = file.getName();
        String path = file.getParent();
        String fileName = file.getAbsolutePath();
        String cwdPath = Config.getPreferences().getCurrentDirectory();

        // Some programs (namely windows) like Excel do not like / in
        // pathnames, so we replace / with the platform file separator.
        //
        // @author Urban Widmark <urban@svenskatest.se>
        //
        path = path.replace('/', File.separatorChar);
        cwdPath = cwdPath.replace('/', File.separatorChar);
        fileName = fileName.replace('/', File.separatorChar);

        String envSpec = null;
        String argSpec = null;

        String[] env = null;
        String[] args = null;

        Config cfg = Config.getInstance();

        int index = fileName.lastIndexOf(".");

        if (index != -1 && index < (fileName.length() - 1)) {
            extension = fileName.substring(index);
            envSpec = cfg.getExecCommandEnv(verb, extension);
            argSpec = cfg.getExecCommandArgs(verb, extension);
        } else {
            envSpec = cfg.getExecCommandEnv(verb, "." + fileName);
            argSpec = cfg.getExecCommandArgs(verb, "." + fileName);
        }

        if (argSpec == null) {
            envSpec = cfg.getExecCommandEnv(verb, "._DEF_");
            argSpec = cfg.getExecCommandArgs(verb, "._DEF_");
        }

        if (argSpec == null) {
            String[] fmtArgs = {verb, fileName, extension};
            String msg = ResourceMgr.getInstance().getUIFormat
                    ("execviewer.not.found.msg", fmtArgs);
            String title = ResourceMgr.getInstance().getUIString
                    ("execviewer.not.found.title");
            JOptionPane.showMessageDialog
                    (null, msg, title, JOptionPane.ERROR_MESSAGE);
            return;
        }

        Hashtable subHash = new Hashtable();

        subHash.put("FILE", fileName);
        subHash.put("PATH", path);
        subHash.put("NAME", name);
        subHash.put("CWD", cwdPath);

        if (false)
            System.err.println("EXECVIEWER:  VARS =" + subHash);

        env = this.parseCommandEnv(envSpec, subHash);
        args = this.parseCommandArgs(argSpec, subHash);

        if (false)
            for (int ai = 0; ai < args.length; ++ai)
                System.err.println("EXECVIEWER:  args[" + ai + "] =" + args[ai]);

        if (false)
            for (int ei = 0; ei < env.length; ++ei)
                System.err.println("EXECVIEWER:  env[" + ei + "] =" + env[ei]);

        try {
            if (env.length < 1) {
                this.proc = Runtime.getRuntime().exec(args);
            } else {
                this.proc = Runtime.getRuntime().exec(args, env);
            }

            this.start();
        } catch (IOException ex) {
            String[] fmtArgs = {verb, fileName, ex.getMessage()};
            String msg = ResourceMgr.getInstance().getUIFormat
                    ("execviewer.exec.error.msg", fmtArgs);
            String title = ResourceMgr.getInstance().getUIString
                    ("execviewer.exec.error.title");
            JOptionPane.showMessageDialog
                    (null, msg, title, JOptionPane.ERROR_MESSAGE);
        }
    }

    public String[]
    parseCommandArgs(String argStr, Hashtable subHash) {
        if (argStr == null || argStr.length() == 0)
            return new String[0];

        String[] args = StringUtilities.parseArgumentString(argStr);
        return StringUtilities.argumentSubstitution(args, subHash);
    }

    public String[]
    parseCommandEnv(String envStr, Hashtable subHash) {
        if (envStr == null || envStr.length() == 0)
            return new String[0];

        String[] env = StringUtilities.parseArgumentString(envStr);
        return StringUtilities.argumentSubstitution(env, subHash);
    }

    public void
    run() {
        try {
            this.proc.getOutputStream().close();

            // STDERR
            this.errRdr =
                    new BufferedReader
                            (new InputStreamReader
                                    (this.proc.getErrorStream()));

            Thread t = new Thread(
                    new Runnable() {
                        public void
                        run() {
                            try {
                                for (; ; ) {
                                    String ln = errRdr.readLine();
                                    if (ln == null)
                                        break;
                                }

                                errRdr.close();
                            } catch (IOException ex) {
                                CVSLog.traceMsg
                                        (ex, "reading exec stderr stream");
                            }
                        }
                    }
            );

            t.start();

            // STDOUT
            this.outRdr =
                    new BufferedReader
                            (new InputStreamReader
                                    (this.proc.getInputStream()));

            for (; ; ) {
                String ln = this.outRdr.readLine();
                if (ln == null)
                    break;
            }

            this.outRdr.close();

            try {
                t.join();
            } catch (InterruptedException ex) {
                CVSLog.traceMsg
                        (ex, "interrupted joining the stderr reader");
            }
        } catch (IOException ex) {
            CVSLog.traceMsg
                    (ex, "reading exec stdout stream");
        }

        try {
            proc.waitFor();
        } catch (InterruptedException ex) {
            CVSLog.traceMsg
                    (ex, "interrupted waiting for process");
        }

        int exitVal = proc.exitValue();
    }

}

