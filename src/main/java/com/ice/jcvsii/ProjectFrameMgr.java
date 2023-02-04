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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.ice.cvsc.CVSLog;

public class ProjectFrameMgr {

    private static boolean debug = false;
    private static Map<String, ProjectFrame> frames = new HashMap<>();

    public static void addProject(ProjectFrame frame, String localRootPath) {
        if (ProjectFrameMgr.debug) {
            CVSLog.logMsgStderr("PROJECT_FRAME_MGR: ADD: " + localRootPath);
        }

        ProjectFrameMgr.frames.put(localRootPath, frame);
    }

    public static void removeProject(ProjectFrame frame, String localRootPath) {
        if (ProjectFrameMgr.debug) {
            CVSLog.logMsgStderr("PROJECT_FRAME_MGR: REMOVE: " + localRootPath);
        }

        // NOTE
        // Because the user can open subtrees, we can end up with a
        // case where the localRootPath is no longer the path that
        // was opened, because the localRoot was adjusted to point
        // at the root of the module. Ergo, we must remove by direct
        // comparison over the entire Hashtable.
        //
        for (String key : ProjectFrameMgr.frames.keySet()) {

            ProjectFrame frm = ProjectFrameMgr.frames.get(key);

            if (frm == frame) {
                if (ProjectFrameMgr.debug) {
                    CVSLog.logMsgStderr("PROJECT_FRAME_MGR: REMOVE: Matched " + key);
                }
                ProjectFrameMgr.frames.remove(key);
                break;
            }
        }
    }

    public static boolean checkProjectOpen(String localRootPath) {
        if (ProjectFrameMgr.debug) {
            CVSLog.logMsgStderr("PROJECT_FRAME_MGR: CHECK: " + localRootPath);
        }

        ProjectFrame frame = ProjectFrameMgr.frames.get(localRootPath);

        if (frame != null) {
            if (frame.isShowing()) {
                frame.toFront();
                frame.requestFocus();
            }
        }

        return (frame != null);
    }

    public static Collection<ProjectFrame> eerateProjectFrames() {
        return ProjectFrameMgr.frames.values();
    }

    public static void closeAllProjects() {

        for (String key : ProjectFrameMgr.frames.keySet()) {
            if (ProjectFrameMgr.debug) {
                CVSLog.logMsgStderr("PROJECT_FRAME_MGR: CLOSE: " + key);
            }

            ProjectFrame frame = ProjectFrameMgr.frames.get(key);

            frame.dispose();
        }
    }
}
