package org.javaopencvbook.utils;

import java.util.Random;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class FloodFillFacade {
	
	public static final int NULL_RANGE = 0;
	public static final int FIXED_RANGE = 1;
	public static final int FLOATING_RANGE = 2;
	private boolean colored = true;
	private boolean masked = true;
	private int range = FIXED_RANGE;
	private Random random = new Random();
	private int connectivity = 4;
	private int newMaskVal = 255;
	private int lowerDiff = 20;
	private int upperDiff = 20;
	
	
	
	
	@Override
	public String toString() {
		return "FloodFillFacade [colored=" + colored + ", masked=" + masked
				+ ", range=" + range + ", random=" + random + ", connectivity="
				+ connectivity + ", newMaskVal=" + newMaskVal + ", lowerDiff="
				+ lowerDiff + ", upperDiff=" + upperDiff + "]";
	}


	public int fill(Mat image, Mat mask, int x, int y) {
		Point seedPoint = new Point(x,y);
		
		int b = random.nextInt(256);
	    int g = random.nextInt(256);
	    int r = random.nextInt(256);
	    Rect rect = new Rect();

	    Scalar newVal = isColored() ? new Scalar(b, g, r) : new Scalar(r*0.299 + g*0.587 + b*0.114);
	    
		Scalar lowerDifference = new Scalar(lowerDiff,lowerDiff,lowerDiff);
		Scalar upperDifference = new Scalar(upperDiff,upperDiff,upperDiff);
		if(range == NULL_RANGE){
			lowerDifference = new Scalar (0,0,0);
			upperDifference = new Scalar (0,0,0);
		}
		int flags = connectivity + (newMaskVal << 8) +
               (
            		   (range == FIXED_RANGE ? Imgproc.FLOODFILL_FIXED_RANGE : 0)
            		   |
            		   0);//Imgproc.FLOODFILL_MASK_ONLY);
	    int area = 0;
	    if(masked){
	    	area = Imgproc.floodFill(image, mask, seedPoint, newVal, rect, lowerDifference,
	    			upperDifference, flags);
	    }
	    else{
	    	area = Imgproc.floodFill(image, new Mat(), seedPoint, newVal, rect, lowerDifference,
	    			upperDifference, flags);
	    }
	    		
		return area;
		
	}
	

	public int getConnectivity() {
		return connectivity;
	}

	public void setConnectivity(int connectivity) {
		this.connectivity = connectivity;
	}

	public boolean isColored() {
		return colored;
	}

	public void setColored(boolean colored) {
		this.colored = colored;
	}

	public boolean isMasked() {
		return masked;
	}

	public void setMasked(boolean masked) {
		this.masked = masked;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}


	public int getLowerDiff() {
		return lowerDiff;
	}

	public void setLowerDiff(int lowerDiff) {
		this.lowerDiff = lowerDiff;
	}

	public int getUpperDiff() {
		return upperDiff;
	}

	public void setUpperDiff(int upperDiff) {
		this.upperDiff = upperDiff;
	} 

	

}
