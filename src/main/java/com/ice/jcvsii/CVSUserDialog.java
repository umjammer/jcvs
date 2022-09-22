/*
** Java cvs client application package.
** Copyright (c) 1997 by Timothy Gerard Endres
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

import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import java.applet.*;

import javax.swing.JOptionPane;

import com.ice.pref.UserPrefs;


public
class		CVSUserDialog
	{
	static public final int		NOTE = 1;
	static public final int		ERROR = 2;


	static public void
	Note( String message )
		{		 
		CVSUserDialog.Note( null, message );
		}

	static public void
	Note( Frame parent, String message )
		{
		Point location =
			Config.getPreferences().getPoint
				( "noteDialog.location", new Point( 40, 40 ) );

		Dimension size =
			Config.getPreferences().getDimension
				( "noteDialog.size", new Dimension( 480, 200 ) );

		CVSUserDialog.Note( parent, message, location, size );
		}

	static public void
	Note( Frame parent, String message, Point location )
		{
		Dimension size =
			Config.getPreferences().getDimension
				( "noteDialog.size", new Dimension( 480, 200 ) );

		CVSUserDialog.Note( parent, message, location, size );
		}

	static public void
	Note( Frame parent, String message, Point location, Dimension size )
		{
		JOptionPane.showMessageDialog
			( parent, message, "Note", JOptionPane.INFORMATION_MESSAGE );
		}

	static public void
	Error( String message )
		{
		CVSUserDialog.Note( null, message );
		}

	static public void
	Error( Frame parent, String message )
		{
		Point loc =
			Config.getPreferences().getPoint
				( "errorDialog.location", new Point( 40, 40 ) );

		Dimension size =
			Config.getPreferences().getDimension
				( "errorDialog.size", new Dimension( 480, 200 ) );

		CVSUserDialog.Note( parent, message, loc, size );
		}

	static public void
	Error( Frame parent, String message, Point location )
		{
		Dimension size =
			Config.getPreferences().getDimension
				( "errorDialog.size", new Dimension( 480, 200 ) );

		CVSUserDialog.Note( parent, message, location, size );
		}

	static public void
	Error( Frame parent, String message, Point location, Dimension size )
		{
		com.ice.cvsc.CVSLog.logMsg( "ERROR_DIALOG: '" + message + "'" );

		JOptionPane.showMessageDialog
			( parent, message, "Error", JOptionPane.ERROR_MESSAGE );
		}

	}
