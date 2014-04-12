package org.javaopencvbook.utils.backgroundProcessors;

import org.javaopencvbook.utils.VideoProcessor;
import org.opencv.core.Core;
import org.opencv.core.Mat;

public class AbsDifferenceBackground implements VideoProcessor {
	private Mat backgroundImage;

	public AbsDifferenceBackground(Mat backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

	public Mat process(Mat inputImage) {
		Mat foregroundImage = new Mat();
		Core.absdiff(backgroundImage,inputImage , foregroundImage);
		return foregroundImage;
	}

}
