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
		String filePath = "src/main/resources/images/marble.jpg";
		
		
		Mat newImage = Highgui.imread(filePath);

		if(newImage.dataAddr()==0){
			System.out.println("Couldn't open file " + filePath);
		}else{

			GUI gui = new GUI("Smooth Filter Example", newImage);
			gui.init();
		}
		return;
	}
}