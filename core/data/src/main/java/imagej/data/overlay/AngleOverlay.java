/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2012 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package imagej.data.overlay;

import imagej.ImageJ;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.meta.Axes;
import net.imglib2.roi.RectangleRegionOfInterest;

/**
 * Represents an angle having a center point and two end points.
 * 
 * @author Barry DeZonia
 */
public class AngleOverlay extends AbstractROIOverlay<RectangleRegionOfInterest> {

	private static final long serialVersionUID = 1L;

	private RealPoint ctrPoint;
	private RealPoint endPoint1;
	private RealPoint endPoint2;

	// default constructor for use by serialization code
	//   (see AbstractOverlay::duplicate())
	public AngleOverlay() {
		super(new RectangleRegionOfInterest(new double[2], new double[2]));
		ctrPoint = new RealPoint(2);
		endPoint1 = new RealPoint(2);
		endPoint2 = new RealPoint(2);
	}
	
	public AngleOverlay(final ImageJ context) {
		super(context, new RectangleRegionOfInterest(new double[2], new double[2]));
		ctrPoint = new RealPoint(2);
		endPoint1 = new RealPoint(2);
		endPoint2 = new RealPoint(2);
		this.setAxis(Axes.X, 0);
		this.setAxis(Axes.Y, 1);
	}

	public AngleOverlay(final ImageJ context, final RealLocalizable ctr,
		final RealLocalizable end1, final RealLocalizable end2)
	{
		super(context, new RectangleRegionOfInterest(new double[2], new double[2]));
		assert ctr.numDimensions() == end1.numDimensions();
		assert ctr.numDimensions() == end2.numDimensions();
		this.ctrPoint = new RealPoint(ctr);
		this.endPoint1 = new RealPoint(end1);
		this.endPoint2 = new RealPoint(end2);
		this.setAxis(Axes.X, 0);
		this.setAxis(Axes.Y, 1);
		updateRegionOfInterest();
	}

	public RealLocalizable getCenterPoint() {
		return ctrPoint;
	}
	
	public RealLocalizable getEndPoint1() {
		return endPoint1;
	}

	public RealLocalizable getEndPoint2() {
		return endPoint2;
	}

	public void setCenterPoint(final RealLocalizable pt) {
		ctrPoint.setPosition(pt);
		updateRegionOfInterest();
	}

	public void setEndPoint1(final RealLocalizable pt) {
		endPoint1.setPosition(pt);
		updateRegionOfInterest();
	}

	public void setEndPoint2(final RealLocalizable pt) {
		endPoint2.setPosition(pt);
		updateRegionOfInterest();
	}

	/* (non-Javadoc)
	 * @see imagej.data.roi.AbstractOverlay#numDimensions()
	 */
	@Override
	public int numDimensions() {
		return ctrPoint.numDimensions();
	}

	/* (non-Javadoc)
	 * @see imagej.data.roi.AbstractOverlay#writeExternal(java.io.ObjectOutput)
	 */
	@Override
	public void writeExternal(final ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeInt(this.numDimensions());
		for (final RealLocalizable pt : new RealLocalizable[] { ctrPoint, endPoint1, endPoint2 }) {
			for (int i = 0; i < numDimensions(); i++) {
				out.writeDouble(pt.getDoublePosition(i));
			}
		}
	}

	/* (non-Javadoc)
	 * @see imagej.data.roi.AbstractOverlay#readExternal(java.io.ObjectInput)
	 */
	@Override
	public void readExternal(final ObjectInput in) throws IOException,
		ClassNotFoundException
	{
		super.readExternal(in);
		final int nDimensions = in.readInt();
		final RealPoint[] pts = new RealPoint[3];
		final double[] position = new double[nDimensions];
		for (int i = 0; i < pts.length; i++) {
			for (int j = 0; j < nDimensions; j++) {
				position[j] = in.readDouble();
			}
			pts[i] = new RealPoint(position);
		}
		ctrPoint = pts[0];
		endPoint1 = pts[1];
		endPoint2 = pts[2];
		updateRegionOfInterest();
	}

	/*
	@Override
	public Overlay duplicate() {
		AngleOverlay overlay = new AngleOverlay(getContext());
		RealLocalizable cp = getCenterPoint();
		RealLocalizable pt1 = getEndPoint1();
		RealLocalizable pt2 = getEndPoint2();
		RealPoint ncp = new RealPoint(cp.getDoublePosition(0), cp.getDoublePosition(1));
		RealPoint npt1 = new RealPoint(pt1.getDoublePosition(0), pt1.getDoublePosition(1));
		RealPoint npt2 = new RealPoint(pt2.getDoublePosition(0), pt2.getDoublePosition(1));
		overlay.setCenterPoint(ncp);
		overlay.setEndPoint1(npt1);
		overlay.setEndPoint2(npt2);
		overlay.setAlpha(getAlpha());
		overlay.setAxis(Axes.X, Axes.X.ordinal());
		overlay.setAxis(Axes.Y, Axes.Y.ordinal());
		overlay.setFillColor(getFillColor());
		overlay.setLineColor(getLineColor());
		overlay.setLineEndArrowStyle(getLineEndArrowStyle());
		overlay.setLineStartArrowStyle(getLineStartArrowStyle());
		overlay.setLineStyle(getLineStyle());
		overlay.setLineWidth(getLineWidth());
		overlay.setName(getName());
		return overlay;
	}
	*/
	
	@Override
	public void move(double[] deltas) {
		for (int i = 0; i < deltas.length; i++) {
			double currPos = ctrPoint.getDoublePosition(i);
			ctrPoint.setPosition(currPos+deltas[i], i);
			currPos = endPoint1.getDoublePosition(i);
			endPoint1.setPosition(currPos+deltas[i], i);
			currPos = endPoint2.getDoublePosition(i);
			endPoint2.setPosition(currPos+deltas[i], i);
		}
		getRegionOfInterest().move(deltas);
	}
	
	private void updateRegionOfInterest() {
		double minX = myMin(0);
		double minY = myMin(1);
		double maxX = myMax(0);
		double maxY = myMax(1);
		getRegionOfInterest().setOrigin(new double[]{minX, minY});
		getRegionOfInterest().setExtent(new double[]{maxX-minX, maxY-minY});
	}

	private double myMax(int d) {
		double v1 = ctrPoint.getDoublePosition(d);
		double v2 = endPoint1.getDoublePosition(d);
		double v3 = endPoint2.getDoublePosition(d);
		return Math.max(v1, Math.max(v2, v3));
	}
	
	private double myMin(int d) {
		double v1 = ctrPoint.getDoublePosition(d);
		double v2 = endPoint1.getDoublePosition(d);
		double v3 = endPoint2.getDoublePosition(d);
		return Math.min(v1, Math.min(v2, v3));
	}

}
