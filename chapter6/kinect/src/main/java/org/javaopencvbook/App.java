package org.javaopencvbook;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

public class App 
{
	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
	
	final static int CV_CAP_OPENNI = 900;
    final static int CV_CAP_OPENNI_DEPTH_MAP                 = 0; // Depth values in mm (CV_16UC1)
    final static int CV_CAP_OPENNI_POINT_CLOUD_MAP           = 1; // XYZ in meters (CV_32FC3)
    final static int CV_CAP_OPENNI_DISPARITY_MAP             = 2; // Disparity in pixels (CV_8UC1)
    final static int CV_CAP_OPENNI_DISPARITY_MAP_32F         = 3; // Disparity in pixels (CV_32FC1)
    final static int CV_CAP_OPENNI_VALID_DEPTH_MASK          = 4; // CV_8UC1
    final static int CV_CAP_OPENNI_BGR_IMAGE                 = 5;
    final static int CV_CAP_OPENNI_GRAY_IMAGE                = 6;

	public static void main(String[] args) throws Exception {
		System.out.println(Core.getBuildInformation());
		
		Mat disparityImage = new Mat();
		Mat disparityThreshold = new Mat();
		Mat colorImage = new Mat();
		Mat maskedImage = new Mat();
		Mat depthMap = new Mat();
		Mat background = Highgui.imread("src/main/resources/images/background.jpg");
		Mat resizedBackground = new Mat();
		Mat workingBackground = new Mat();
		
		VideoCapture capture = new VideoCapture(CV_CAP_OPENNI);
		capture.grab();
		capture.retrieve( depthMap,  CV_CAP_OPENNI_DISPARITY_MAP);
		depthMap.convertTo( disparityImage,CvType.CV_8UC1, 1.00f );
		

		if(disparityImage.rows()>0){
			Imgproc.resize(background, resizedBackground, disparityImage.size());
			
			GUI gui = new GUI("OpenCV Kinect Depth Chroma Key", disparityImage);
			gui.init();
			while(true){
				capture.grab();
				
				capture.retrieve( depthMap, CV_CAP_OPENNI_DISPARITY_MAP);
				disparityImage = depthMap.clone();
				capture.retrieve(colorImage, CV_CAP_OPENNI_BGR_IMAGE);
				
				workingBackground = resizedBackground.clone();		
				Imgproc.threshold(disparityImage, disparityThreshold, gui.getLevel(), 255.0f, Imgproc.THRESH_BINARY);
				maskedImage.setTo(new Scalar(0,0,0));
				colorImage.copyTo(maskedImage,disparityThreshold);
				maskedImage.copyTo(workingBackground,maskedImage);
				
				renderOutputAccordingToMode(disparityImage, disparityThreshold,
						colorImage, resizedBackground, workingBackground,maskedImage, gui);
				
			}
		}
		else{
			System.out.println("Couldn't retrieve frames. Check if Kinect Sensor is connected and if opencv was compiled with OpenNI support");
		}
	}

	
	private static void renderOutputAccordingToMode(Mat disparityImage,
			Mat disparityThreshold, Mat colorImage, Mat resizedBackground,
			Mat workingBackground, Mat maskedImage, GUI gui) {
		if(GUI.BACKGROUND_STRING.equals(gui.getOutputMode())){
			gui.updateView(resizedBackground);
		}
		else if( GUI.DISPARITY_STRING.equals(gui.getOutputMode())){
			gui.updateView(disparityImage);		
		}
		else if(GUI.RGB_STRING.equals(gui.getOutputMode())){
			gui.updateView(colorImage);
		}
		else if(GUI.DISPARITY_THRESHOLD_STRING.equals(gui.getOutputMode())){
			gui.updateView(disparityThreshold);
		}
		else if(GUI.RGB_MASK_STRING.equals(gui.getOutputMode())){
			gui.updateView(maskedImage);
		}
		else{
			gui.updateView(workingBackground);
		}
	}
}