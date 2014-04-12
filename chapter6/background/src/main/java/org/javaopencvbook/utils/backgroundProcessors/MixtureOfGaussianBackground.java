package org.javaopencvbook.utils.backgroundProcessors;

import org.javaopencvbook.utils.VideoProcessor;
import org.opencv.core.Mat;
import org.opencv.video.BackgroundSubtractorMOG;


public class MixtureOfGaussianBackground implements VideoProcessor {
	private BackgroundSubtractorMOG mog = new BackgroundSubtractorMOG();
	private Mat foreground = new Mat();
	private double learningRate = 0.01;

	public Mat process(Mat inputImage) {
		
		mog.apply(inputImage, foreground, learningRate);
		return foreground;
	}

}
