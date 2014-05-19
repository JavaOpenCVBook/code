package org.javaopencvbook.utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ImageProcessor {
	
	public BufferedImage toBufferedImage(Mat matrix){
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if ( matrix.channels() > 1 ) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = matrix.channels()*matrix.cols()*matrix.rows();
		byte [] buffer = new byte[bufferSize];
		matrix.get(0,0,buffer); // get all the pixels
		BufferedImage image = new BufferedImage(matrix.cols(),matrix.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);  
		return image;
	}
	
	public Mat blur(Mat input, int numberOfTimes){
		Mat sourceImage = new Mat();
		Mat destImage = input.clone();
		for(int i=0;i<numberOfTimes;i++){
			sourceImage = destImage.clone();
			Imgproc.blur(sourceImage, destImage, new Size(3.0, 3.0));
		}
		return destImage;
	}
	
	public Mat erode(Mat input, int elementSize, int elementShape){
		Mat outputImage = new Mat();
		Mat element = getKernelFromShape(elementSize, elementShape);
		Imgproc.erode(input,outputImage, element);
		return outputImage;
	}

	

	public Mat dilate(Mat input, int elementSize, int elementShape) {
		Mat outputImage = new Mat();
		Mat element = getKernelFromShape(elementSize, elementShape);
		Imgproc.dilate(input,outputImage, element);
		return outputImage;
	}

	public Mat open(Mat input, int elementSize, int elementShape) {
		Mat outputImage = new Mat();
		Mat element = getKernelFromShape(elementSize, elementShape);
		Imgproc.morphologyEx(input,outputImage, Imgproc.MORPH_OPEN, element);
		return outputImage;
	}

	public Mat close(Mat input, int elementSize, int elementShape) {
		Mat outputImage = new Mat();
		Mat element = getKernelFromShape(elementSize, elementShape);
		Imgproc.morphologyEx(input,outputImage, Imgproc.MORPH_CLOSE, element);
		return outputImage;
	}
	
	private Mat getKernelFromShape(int elementSize, int elementShape) {
		return Imgproc.getStructuringElement(elementShape, new Size(elementSize*2+1, elementSize*2+1), new Point(elementSize, elementSize) );
	}

}
