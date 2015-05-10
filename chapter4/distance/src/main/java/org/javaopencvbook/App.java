package org.javaopencvbook;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class App 
{
	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

	public static void main(String[] args) throws Exception {
		Mat image = new Mat(3,3 ,CvType.CV_8UC1);
		Mat sum = new Mat();
		byte[] buffer = {0,2,4,6,8,10,12,14,16};
		image.put(0,0,buffer);
		System.out.println(image.dump());
		Imgproc.integral(image, sum);
		System.out.println(sum.dump());
		
		
		String filePath = "src/main/resources/images/coffee.jpg";
		Mat newImage = Highgui.imread(filePath, Highgui.CV_LOAD_IMAGE_ANYCOLOR);

		if(newImage.dataAddr()==0){
			System.out.println("Couldn't open file " + filePath);
		}else{

			GUI gui = new GUI("Distance Transform Example", newImage);
			gui.init();
		}
		return;
	}
}