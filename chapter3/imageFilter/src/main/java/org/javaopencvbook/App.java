package org.javaopencvbook;

import org.javaopencvbook.utils.ImageProcessor;
import org.javaopencvbook.utils.ImageViewer;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class App 
{
	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

	public static void main(String[] args) throws Exception {
		String filePath = "src/main/resources/images/baboon.jpg";
		Mat newImage = Highgui.imread(filePath);
		Mat negImage = new Mat();
		Core.bitwise_not(newImage, negImage);
		newImage = negImage;
		
		//newImage = Imgproc.getStructuringElement(shape, ksize)
		if(newImage.dataAddr()==0){
			System.out.println("Couldn't open file " + filePath);
		}else{
			ImageProcessor ip = new ImageProcessor();
			Mat output = new Mat();
			//for(int i=0;i<4;i++)
			Imgproc.erode(newImage,output, new Mat(), new Point(), 1);//ip.blur(newImage, 10);
			ImageViewer imageViewer = new ImageViewer();
			imageViewer.show(output, "Eroded image");
			ImageViewer ivo = new ImageViewer();
			ivo.show(negImage,"original");
			
			//TODO clonar aplicacao dos samples do OpenCV, muito elucidativa
		}

	}
}