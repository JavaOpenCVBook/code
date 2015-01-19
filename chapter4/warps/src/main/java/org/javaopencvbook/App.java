package org.javaopencvbook;

import org.javaopencvbook.utils.ImageProcessor;
import org.javaopencvbook.utils.ImageViewer;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import static org.opencv.imgproc.Imgproc.blur;

public class App 
{
	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

	public static void main(String[] args) throws Exception {
		//String filePath = "src/main/resources/images/building.jpg";
		String filePath = "src/main/resources/images/building.jpg";
		Mat newImage = Highgui.imread(filePath, Highgui.CV_LOAD_IMAGE_ANYCOLOR);

		if(newImage.dataAddr()==0){
			System.out.println("Couldn't open file " + filePath);
		}else{

			GUI gui = new GUI("Warp Example", newImage);
			gui.init();
		}
		return;
	}
}