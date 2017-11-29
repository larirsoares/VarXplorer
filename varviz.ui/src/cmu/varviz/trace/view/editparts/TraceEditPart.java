/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2016  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 * 
 * FeatureIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FeatureIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See http://featureide.cs.ovgu.de/ for further information.
 */
package cmu.varviz.trace.view.editparts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import cmu.varviz.VarvizConstants;
import cmu.varviz.trace.Trace;

/**
 * TODO description
 * 
 * @author Jens Meinicke
 */
public class TraceEditPart extends AbstractTraceEditPart {

	public TraceEditPart(Trace model) {
		super();
		setModel(model);
	}

	public Trace getRoleModel() {
		return (Trace) getModel();
	}

	@Override
	protected IFigure createFigure() {
		Figure fig = new FreeformLayer();
		fig.setLayoutManager(new FreeformLayout());
		fig.setBackgroundColor(VarvizConstants.WHITE);
		fig.setOpaque(true);
		return fig;
	}

	@Override
	protected List<?> getModelChildren() {
		Trace t = getRoleModel();
		List<Object> list = new ArrayList<Object>();
		list.add(t.getSTART());
		if (t.getMain() != null) {
			list.add(t.getMain());
		}
		list.add(t.getEND());
		return list;
	}

	private final static int BORDER_MARGIN = 10;

	/**
	 * TODO remove code clone with {@link MethodEditPart}
	 */
	@Override
	public void layout() {
		final IFigure methodFigure = getFigure();
		Rectangle bounds = methodFigure.getBounds();
		int h = 0;
		@SuppressWarnings("unchecked")
		final List<Object> children = getChildren();
		for (Object object : children) {
			if (object instanceof AbstractTraceEditPart) {
				AbstractTraceEditPart childEditPart = (AbstractTraceEditPart) object;
				childEditPart.layout();
				childEditPart.getFigure().translateToRelative(bounds.getTopLeft());
				childEditPart.getFigure().setLocation(new Point(0, h));
				h += childEditPart.getFigure().getSize().height;
				h += BORDER_MARGIN * 2;
			}
		}

		// center elements
		int maxwidth = 0;
		for (Object object : children) {
			if (object instanceof AbstractTraceEditPart) {
				AbstractTraceEditPart childEditPart = (AbstractTraceEditPart) object;
				maxwidth = Math.max(childEditPart.getFigure().getBounds().width, maxwidth);
			}
		}

		for (Object object : children) {
			if (object instanceof AbstractTraceEditPart) {
				AbstractTraceEditPart childEditPart = (AbstractTraceEditPart) object;
				Rectangle childBounds = childEditPart.getFigure().getBounds();

				int newX = maxwidth / 2 - childBounds.width / 2;
				childEditPart.getFigure().setLocation(new Point(newX, childBounds.y));
			}
		}

		Object endNode = children.get(children.size() - 1);
		AbstractTraceEditPart childEditPart = (AbstractTraceEditPart) endNode;

		AbstractGraphicalEditPart statement = null;
		for (Object object : children) {
			if (object instanceof AbstractTraceEditPart) {
				if (object instanceof MethodEditPart) {
					statement = ((MethodEditPart) object).getLastTrueStatement();
				}
			}
		}

		if (statement != null) {
			childEditPart.getFigure().setLocation(new Point(statement.getFigure().getBounds().getCenter().x - childEditPart
					.getFigure().getBounds().width / 2, childEditPart.getFigure().getBounds().y));
		}
	}

}
