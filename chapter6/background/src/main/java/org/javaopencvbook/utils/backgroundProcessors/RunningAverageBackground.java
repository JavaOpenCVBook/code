package org.javaopencvbook.utils.backgroundProcessors;

import org.javaopencvbook.utils.VideoProcessor;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;


public class RunningAverageBackground implements VideoProcessor {
	
	Mat inputGray = new Mat();
	Mat accumulatedBackground = new Mat();
	Mat backImage = new Mat();
	Mat foreground = new Mat();
	double learningRate = 0.01;//0.001;
	int threshold = 60;//30;
	
	
	public RunningAverageBackground() {
	}
	
	
	public Mat process(Mat inputImage) {
		Mat foregroundThresh = new Mat();
		// Firstly, convert to gray-level image, yields good results with performance		
		Imgproc.cvtColor(inputImage, inputGray, Imgproc.COLOR_BGR2GRAY);
		// initialize background to 1st frame, convert to floating type
		if (accumulatedBackground.empty())
			inputGray.convertTo(accumulatedBackground, CvType.CV_32F);
		// convert background to 8U, for differencing with input image
		accumulatedBackground.convertTo(backImage,CvType.CV_8U);
		// compute difference between image and background
		Core.absdiff(backImage,inputGray,foreground);
		
		// apply threshold to foreground image
		Imgproc.threshold(foreground,foregroundThresh, threshold,255, Imgproc.THRESH_BINARY_INV);
		
		
		// accumulate background
		Mat inputFloating = new Mat();
		inputGray.convertTo(inputFloating, CvType.CV_32F);
		Imgproc.accumulateWeighted(inputFloating, accumulatedBackground,learningRate, foregroundThresh);
		
		return negative(foregroundThresh);
	}


	private Mat negative(Mat foregroundThresh) {
		Mat result = new Mat();
		Mat white = foregroundThresh.clone();
		white.setTo(new Scalar(255.0));
		Core.subtract(white, foregroundThresh,  result);
		return result;
	}

}
