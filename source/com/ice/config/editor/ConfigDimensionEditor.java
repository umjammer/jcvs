
package com.ice.config.editor;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import com.ice.config.*;
import com.ice.pref.UserPrefs;
import com.ice.util.AWTUtilities;


public
class		ConfigDimensionEditor
extends		ConfigureEditor
	{
	protected JTextField	wField;
	protected JTextField	hField;


	public
	ConfigDimensionEditor()
		{
		super( "Dimension" );
		}

	public void
	edit( UserPrefs prefs, ConfigureSpec spec )
		{
		super.edit( prefs, spec );

		Dimension dim =
			prefs.getDimension( spec.getPropertyName(), null );

		if ( dim != null )
			{
			this.wField.setText( Integer.toString( dim.width ) );
			this.hField.setText( Integer.toString( dim.height ) );
			}
		else
			{
			this.wField.setText( "0" );
			this.hField.setText( "0" );
			}
		}

	public void
	saveChanges( UserPrefs prefs, ConfigureSpec spec )
		{
		String propName = spec.getPropertyName();

		try {
			int w = Integer.parseInt( this.wField.getText() );
			int h = Integer.parseInt( this.hField.getText() );

			Dimension newVal = new Dimension( w, h );
			Dimension oldVal =
				prefs.getDimension( propName, new Dimension( 0, 0 ) );

			if ( ! newVal.equals( oldVal ) )
				{
				prefs.setDimension( propName, newVal );
				}
			}
		catch ( NumberFormatException ex )
			{
			ex.printStackTrace();
			}
		}

	public void
	requestInitialFocus()
		{
		this.wField.requestFocus();
		this.wField.selectAll();
		}

	protected JPanel
	createEditPanel()
		{
		JPanel result = new JPanel();
		result.setLayout( new GridBagLayout() );
		result.setBorder( new EmptyBorder( 5, 3, 3, 3 ) );

		int col = 0;
		int row = 0;

		JLabel lbl = new JLabel( "Width" );
		lbl.setBorder( new EmptyBorder( 1, 3, 1, 3 ) );
		AWTUtilities.constrain(
			result, lbl,
			GridBagConstraints.NONE,
			GridBagConstraints.WEST,
			col++, row, 1, 1, 0.0, 0.0 );

		this.wField = new JTextField( "0" );
		AWTUtilities.constrain(
			result, this.wField,
			GridBagConstraints.HORIZONTAL,
			GridBagConstraints.WEST,
			col++, row, 1, 1,  1.0, 0.0 );

		lbl = new JLabel( "Height" );
		lbl.setBorder( new EmptyBorder( 1, 3, 1, 3 ) );
		AWTUtilities.constrain(
			result, lbl,
			GridBagConstraints.NONE,
			GridBagConstraints.WEST,
			col++, row, 1, 1, 0.0, 0.0 );

		this.hField = new JTextField( "0" );
		AWTUtilities.constrain(
			result, this.hField,
			GridBagConstraints.HORIZONTAL,
			GridBagConstraints.WEST,
			col++, row++, 1, 1, 1.0, 0.0 );

		return result;
		}

	}

