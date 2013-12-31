package org.javaopencvbook;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.javaopencvbook.util.ImageViewer;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

public class App 
{
	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

	public static void main(String[] args) throws Exception {
		String filePath = "src/main/resources/images/cathedral.jpg";
		Mat newImage = Highgui.imread(filePath);
		if(newImage.dataAddr()==0){
			System.out.println("Couldn't open file " + filePath);
		}else{
			ImageViewer imageViewer = new ImageViewer();
			imageViewer.show(newImage, "Loaded image");
		}

	}
}