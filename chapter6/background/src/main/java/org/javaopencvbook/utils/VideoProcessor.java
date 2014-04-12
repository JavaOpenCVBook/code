package org.javaopencvbook.utils;

import org.opencv.core.Mat;

public interface VideoProcessor {
	public Mat process(Mat inputImage);

}
