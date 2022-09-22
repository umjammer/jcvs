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

import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.EventListener;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.table.*;


public
class		ColumnHeader
extends		JComponent
	{
    protected TableColumnModel		columnModel;
    protected CellRendererPane		rendererPane;
    protected MouseInputListener	mouseInputListener;

    /**
     * If this flag is true, then the header will repaint the table as
     * a column is dragged or resized.
     */
    protected boolean	updateTableInRealTime;

	/** The index of the column being resized. 0 if not resizing */
	transient protected TableColumn	resizingColumn;

	/** The index of the column being dragged. 0 if not dragging */
	transient protected TableColumn	draggedColumn;

    /** The distance from its original position the column has been dragged */
    transient protected int	draggedDistance;

    /** Resizing of columns are allowed by the user */
    protected boolean	resizingAllowed;

    /** Reordering of columns are allowed by the user */
    protected boolean	reorderingAllowed;

	protected ColumnHeaderRenderer	hdrCellRenderer;

	protected EventListenerList resizeListeners;


	public
	ColumnHeader( TableColumnModel model )
		{
		this.columnModel = model;
		this.resizingAllowed = true;
		this.updateTableInRealTime = true;

		this.resizeListeners = new EventListenerList();

		this.hdrCellRenderer = this.new DefaultColumnHeaderRenderer();

        MouseInputHandler mouseListener = this.new MouseInputHandler();

        this.addMouseListener( mouseListener );
        this.addMouseMotionListener( mouseListener );

        this.rendererPane = new CellRendererPane();
        this.add( this.rendererPane );
		}

	public void
	addResizeListener( ColumnHeader.ResizeListener l )
		{
		this.resizeListeners.add
			( ColumnHeader.ResizeListener.class, l );
		}

	public void
	removeResizeListener( ColumnHeader.ResizeListener l )
		{
		this.resizeListeners.remove
			( ColumnHeader.ResizeListener.class, l );
		}

	protected void
	fireColumnHeadersResized( boolean isResizing )
		{
		// Guaranteed to return a non-null array
		Object[] listeners = resizeListeners.getListenerList();

		// Process the listeners last to first, notifying
		// those that are interested in this event
		for ( int i = listeners.length - 2 ; i >= 0 ; i -= 2 )
			{
			if ( listeners[i] == ColumnHeader.ResizeListener.class )
				{
				ColumnHeader.ResizeEvent evt =
					new ColumnHeader.ResizeEvent( this, isResizing );

				((ColumnHeader.ResizeListener) listeners[ i + 1 ]).
					columnHeadersResized( evt );
				}	       
			}
		}	

	protected void
	fireColumnHeadersNeedUpdate( boolean isResizing )
		{
		// Guaranteed to return a non-null array
		Object[] listeners = resizeListeners.getListenerList();

		// Process the listeners last to first, notifying
		// those that are interested in this event
		for ( int i = listeners.length - 2 ; i >= 0 ; i -= 2 )
			{
			if ( listeners[i] == ColumnHeader.ResizeListener.class )
				{
				ColumnHeader.ResizeEvent evt =
					new ColumnHeader.ResizeEvent( this, isResizing );

				((ColumnHeader.ResizeListener) listeners[ i + 1 ]).
					columnHeadersNeedUpdate( evt );
				}	       
			}
		}	

	public TableColumnModel
	getColumnModel()
		{
		return this.columnModel;
		}

	public ColumnHeaderRenderer
	getRenderer()
		{
		return this.hdrCellRenderer;
		}

	public void
	setRenderer( ColumnHeaderRenderer rend )
		{
		this.hdrCellRenderer = rend;
		}

	public TableColumn
	getDraggedColumn()
		{
		return this.draggedColumn;
		}

	public void
	setDraggedColumn( TableColumn col )
		{
		this.draggedColumn = col;
		}

	public TableColumn
	getResizingColumn()
		{
		return this.resizingColumn;
		}

	public void
	setResizingColumn( TableColumn col )
		{
		this.resizingColumn = col;
		}

	public int
	getDraggedDistance()
		{
		return this.draggedDistance;
		}

	public void
	setDraggedDistance( int dist )
		{
		this.draggedDistance = dist;
		}

	public boolean
	getResizingAllowed()
		{
		return this.resizingAllowed;
		}

	public void
	setResizingAllowed( boolean allowed )
		{
		this.resizingAllowed = allowed;
		}

	public boolean
	getReorderingAllowed()
		{
		return this.reorderingAllowed;
		}

	public void
	setReorderingAllowed( boolean allowed )
		{
		this.reorderingAllowed = allowed;
		}

	public boolean
	getUpdateTableInRealTime()
		{
		return this.updateTableInRealTime;
		}

	public void
	setUpdateTableInRealTime( boolean upd )
		{
		this.updateTableInRealTime = upd;
		}

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicTableUI.
     */
    public
	class		MouseInputHandler
	implements	MouseInputListener
		{
        private int lastEffectiveMouseX;

        public void
		mouseClicked( MouseEvent e )
			{
			}

        public void
		mousePressed( MouseEvent evt )
			{
            setDraggedColumn( null );
            setResizingColumn( null );
            setDraggedDistance( 0 );

            Point p = evt.getPoint();
            lastEffectiveMouseX = p.x;

            int index = columnModel.getColumnIndexAtX( p.x );

            if ( index != -1 )
				{
                // The last 3 pixels + 3 pixels of next column are for resizing
                int resizeIndex = computeResizingColumn( p );
                if ( getResizingAllowed() && (resizeIndex != -1) )
					{
                    TableColumn hitColumn =
						columnModel.getColumn( resizeIndex );
                    setResizingColumn( hitColumn );
					}
                else if ( getReorderingAllowed() )
					{
                    TableColumn hitColumn = columnModel.getColumn( index );
                    setDraggedColumn( hitColumn );
					}
                else
					{
					// Not allowed to reorder or resize. Poor header... :(
					}
				}
			}

        public void
		mouseMoved( MouseEvent evt )
			{
            if ( computeResizingColumn( evt.getPoint() ) != -1 )
				{
                Cursor resizeCursor =
					Cursor.getPredefinedCursor( Cursor.E_RESIZE_CURSOR );

                if ( getCursor() != resizeCursor )
					{
                    setCursor( resizeCursor );
					}
				}
            else
				{
                Cursor defaultCursor =
					Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR );

                if ( getCursor() != defaultCursor )
					{
                    setCursor( defaultCursor );
					}
				}
			}

        public void
		mouseDragged( MouseEvent evt )
			{
			int mouseX = evt.getX();
			int deltaX = mouseX - lastEffectiveMouseX;

			if ( deltaX == 0 )
				{
				return;
				}

			TableColumn rColumn = getResizingColumn();
			TableColumn draggedColumn = getDraggedColumn();

			if ( rColumn != null )
				{
				Dimension origSz = getSize();
				int oldWidth = rColumn.getWidth();
				int newWidth = oldWidth + deltaX;

				rColumn.setWidth( newWidth );
				rColumn.setPreferredWidth( newWidth );

				int acheivedDeltaX = rColumn.getWidth() - oldWidth;
				lastEffectiveMouseX = lastEffectiveMouseX + acheivedDeltaX;
				
				fireColumnHeadersResized( true );

				revalidate();
				repaint();

				if ( getUpdateTableInRealTime() )
					{
					fireColumnHeadersNeedUpdate( true );
					}
				}
			else if ( draggedColumn != null )
				{
				move( evt, deltaX );
				lastEffectiveMouseX = mouseX;
				}
			else
				{
				// Neither dragging nor resizing ...
				lastEffectiveMouseX = mouseX;
				}
			}

        public void
		mouseReleased( MouseEvent evt )
			{
            TableColumn rColumn = getResizingColumn();
            TableColumn draggedColumn = getDraggedColumn();

            if ( rColumn != null )
				{
				fireColumnHeadersResized( false );
				fireColumnHeadersNeedUpdate( false );
				}

			setResizingColumn( null );
			setDraggedColumn( null );
			setDraggedDistance( 0 );

			// Repaint to finish cleaning up
			repaint();
			}

        public void
		mouseEntered( MouseEvent evt )
			{
			}

        public void
		mouseExited( MouseEvent evt )
			{
			}

		private int
		viewIndexForColumn( TableColumn aColumn )
			{
            TableColumnModel cm = getColumnModel();
            for ( int column = 0 ; column < cm.getColumnCount() ; column++ )
				{
                if ( cm.getColumn( column ) == aColumn )
					{
                    return column;
					}
				}
            return -1;
			}

        private void
		move( MouseEvent evt, int delta )
			{
            TableColumnModel columnModel = getColumnModel();
            int lastColumn = columnModel.getColumnCount() - 1;

            TableColumn draggedColumn = getDraggedColumn();
            int draggedDistance = getDraggedDistance() + delta;
            int hitColumnIndex = viewIndexForColumn( draggedColumn );

            // Now check if we have moved enough to do a swap
            if ( (draggedDistance < 0) && (hitColumnIndex != 0) )
				{
                // Moving left; check prevColumn
                int width = columnModel.getColumnMargin() +
                    columnModel.getColumn( hitColumnIndex - 1 ).getWidth();

                if ( -draggedDistance > (width / 2) )
					{
                    // Swap me
                    columnModel.moveColumn( hitColumnIndex, hitColumnIndex - 1 );

                    draggedDistance = width + draggedDistance;
                    hitColumnIndex--;
					}
				}
            else if ( (draggedDistance > 0) && (hitColumnIndex != lastColumn) )
				{
                // Moving right; check nextColumn
                int width = columnModel.getColumnMargin() +
                    columnModel.getColumn( hitColumnIndex + 1 ).getWidth();

                if ( draggedDistance > (width / 2) )
					{
                    // Swap me
                    columnModel.moveColumn( hitColumnIndex, hitColumnIndex + 1 );

                    draggedDistance = -(width - draggedDistance);
                    hitColumnIndex++;
					}
				}

            // Redraw, compute how much we are moving and the total redraw rect
            Rectangle redrawRect = getHeaderRect( hitColumnIndex );  // where I was
            redrawRect.x += getDraggedDistance();

            // draggedDistance += delta;
            Rectangle redrawRect2 = getHeaderRect( hitColumnIndex ); // where I'm now
            redrawRect2.x += draggedDistance;
            redrawRect = redrawRect.union( redrawRect2 );  // Union the 2 rects

            repaint( redrawRect.x, 0, redrawRect.width, redrawRect.height );
            if ( getUpdateTableInRealTime() )
				{
			/*
			** UNDONE
			**
                JTable table = header.getTable();
                if (table != null)
                    table.repaint(redrawRect.x, 0, redrawRect.width,
                                  (table.getRowHeight() +
                                   table.getIntercellSpacing().height)
                                  * table.getRowCount());
			**
			*/
				}

            setDraggedColumn( columnModel.getColumn( hitColumnIndex ) );
            setDraggedDistance( draggedDistance );
			}

        private int
		computeResizingColumn( Point p )
			{
            int column = 0;
            Rectangle resizeRect =
				new Rectangle( -3, 0, 6, getSize().height );

            int columnMargin = getColumnModel().getColumnMargin();

            Enumeration enumeration = getColumnModel().getColumns();

            for ( ; enumeration.hasMoreElements() ; )
				{
                TableColumn aColumn = (TableColumn) enumeration.nextElement();
                resizeRect.x += aColumn.getWidth() + columnMargin;

                if ( resizeRect.x > p.x )
					{
                    // Don't have to check the rest, we already gone past p
                    break;
					}

                if ( resizeRect.contains( p ) )
                    return column;

                column++;
				}

            return -1;
			}
		}


    public void
	paint( Graphics g )
		{
		Rectangle clipBounds = g.getClipBounds();

		if ( getColumnModel() == null )
			return;

		int column = 0;
		boolean drawn = false;
		int draggedColumnIndex = -1;
		Rectangle draggedCellRect = null;

		Dimension size = this.getSize();

		Rectangle cellRect =
			new Rectangle( 0, 0, size.width, size.height );

		Enumeration enumeration = getColumnModel().getColumns();

		for ( ; enumeration.hasMoreElements() ; )
			{
			TableColumn aColumn =
				(TableColumn) enumeration.nextElement();

			int columnMargin = this.getColumnModel().getColumnMargin();

			cellRect.width = aColumn.getWidth() + columnMargin;
			// Note: The header cellRect includes columnMargin so the
			//       drawing of header cells will not have any gaps.

			if ( cellRect.intersects( clipBounds ) )
				{
				drawn = true;
				if ( aColumn != this.getDraggedColumn() )
					{
					paintCell( g, cellRect, column );
					}
				else
					{
					// Draw a gray well in place of the moving column
					g.setColor( getParent().getBackground() );
					g.fillRect( cellRect.x, cellRect.y,
								cellRect.width, cellRect.height );
					draggedCellRect = new Rectangle( cellRect );
					draggedColumnIndex = column;
					}
				}
			else
				{
				if ( drawn )
					// Don't need to iterate through the rest
					break;
				}

			cellRect.x += cellRect.width;
			column++;
			}

		// draw the dragged cell if we are dragging
		TableColumn draggedColumnObject = this.getDraggedColumn();
		if ( draggedColumnObject != null && draggedCellRect != null )
			{
			draggedCellRect.x += getDraggedDistance();
			paintCell( g, draggedCellRect, draggedColumnIndex );
			}
		}

    private void
	paintCell( Graphics g, Rectangle cellRect, int columnIndex )
		{
        TableColumn aCol = this.getColumnModel().getColumn( columnIndex );

		Component component =
			this.hdrCellRenderer.getHeaderCellRendererComponent
				( this, aCol.getHeaderValue(), columnIndex );

        this.rendererPane.add( component );

        this.rendererPane.paintComponent
			( g, component, this, cellRect.x, cellRect.y,
				cellRect.width, cellRect.height, true );
		}

//
// Size Methods
//

    private int
	getHeaderHeight()
		{
        int height = 0;
        TableColumnModel columnModel = getColumnModel();
        for( int col = 0 ; col < columnModel.getColumnCount() ; col++ )
			{
            TableColumn aCol = columnModel.getColumn( col );

            Component comp =
				this.hdrCellRenderer.getHeaderCellRendererComponent
					( this, aCol.getHeaderValue(), col );

			height = Math.max( height, comp.getPreferredSize().height );
			}

        return height;
		}

    private Dimension
	createHeaderSize( long width )
		{
        TableColumnModel columnModel = getColumnModel();
        // None of the callers include the intercell spacing, do it here.
        width += columnModel.getColumnMargin() * columnModel.getColumnCount();
        if ( width > Integer.MAX_VALUE )
			{
            width = Integer.MAX_VALUE;
			}

		return new Dimension( (int)width, getHeaderHeight() );
		}


    /**
     * Return the minimum size of the header. The minimum width is the sum 
     * of the minimum widths of each column (plus inter-cell spacing).
     */
    public Dimension
	getMinimumSize()
		{
        long width = 0;
        Enumeration enumeration = getColumnModel().getColumns();
        for ( ; enumeration.hasMoreElements() ; )
			{
            TableColumn aColumn = (TableColumn)enumeration.nextElement();
            width += aColumn.getMinWidth();
			}
        return createHeaderSize( width );
		}

    /**
     * Return the preferred size of the header. The preferred height is the 
     * maximum of the preferred heights of all of the components provided 
     * by the header renderers. The preferred width is the sum of the 
     * preferred widths of each column (plus inter-cell spacing).
     */
    public Dimension
	getPreferredSize()
		{
        long width = 0;
        Enumeration enumeration = getColumnModel().getColumns();
        for ( ; enumeration.hasMoreElements() ; )
			{
            TableColumn aColumn = (TableColumn)enumeration.nextElement();
            width += aColumn.getPreferredWidth();
			}
        return createHeaderSize( width );
		}

    /**
     * Return the maximum size of the header. The maximum width is the sum 
     * of the maximum widths of each column (plus inter-cell spacing).
     */
    public Dimension
	getMaximumSize()
		{
        long width = 0;
        Enumeration enumeration = getColumnModel().getColumns();
        for ( ; enumeration.hasMoreElements() ; )
			{
            TableColumn aColumn = (TableColumn)enumeration.nextElement();
            width += aColumn.getMaxWidth();
			}
        return createHeaderSize( width );
		}


	/**
     * Returns the rectangle containing the header tile at <I>columnIndex</I>.
     *
     * @return	the rectangle containing the header tile at <I>columnIndex</I>
     * @exception IllegalArgumentException	If <I>columnIndex</I> is out
     *						of range
     */
    public Rectangle
	getHeaderRect( int columnIndex )
		{
		TableColumnModel columnModel = getColumnModel();

		if ( (columnIndex < 0)
				|| (columnIndex >= columnModel.getColumnCount()) )
			{
			throw new IllegalArgumentException("Column index out of range");
			}

		int rectX = 0;
		int column = 0;
		int columnMargin = this.getColumnModel().getColumnMargin();

		Enumeration enumeration = this.getColumnModel().getColumns();

		for ( ; enumeration.hasMoreElements() ; )
			{
			TableColumn aColumn = (TableColumn) enumeration.nextElement();

			if ( column == columnIndex )
				{
				return new Rectangle
					( rectX, 0, aColumn.getWidth() + columnMargin, getSize().height );
				}

			rectX += aColumn.getWidth() + columnMargin;
			column++;
			}

		return new Rectangle();
		}

	public Point
	getLocationOnScreen()
		{
		Container parent = this.getParent();
		if ( parent != null )
			{
			Point parentLocation = parent.getLocationOnScreen();
			Point componentLocation = this.getLocation();
			componentLocation.translate
				( parentLocation.x, parentLocation.y );
			return componentLocation;
			}
		else
			{
			return null;
			}
		}
		
	public boolean
	isFocusTraversable()
		{
		return false;
		}


	public
	interface	ResizeListener
	extends		EventListener
		{
		public void
			columnHeadersResized( ResizeEvent event );

		public void
			columnHeadersNeedUpdate( ResizeEvent event );
		}

	public
	class		ResizeEvent
	extends		AWTEvent
		{
		private boolean		isResizing;

		public
		ResizeEvent( Component source, boolean isResizing )
			{
			super( source, RESERVED_ID_MAX );
			this.isResizing = isResizing;
			}

		public boolean
		isResizing()
			{
			return isResizing;
			}
		}

	public
	interface	ColumnHeaderRenderer
		{
		public Component
			getHeaderCellRendererComponent
				( ColumnHeader header, Object value, int index );
		}

	private
	class		DefaultColumnHeaderRenderer
	extends		JLabel
	implements	ColumnHeaderRenderer
		{
		public
		DefaultColumnHeaderRenderer()
			{
			this.setBorder
				( new CompoundBorder
					( new BevelBorder( BevelBorder.RAISED ),
						new EmptyBorder( 0, 3, 0, 3 ) ) );
			}

		public Component
		getHeaderCellRendererComponent(
				ColumnHeader header, Object value, int index )
			{
			if ( value == null )
				{
				value = "Column " + index;
				}

			this.setText( (String)value );

			return this;
			}
		}

	}

