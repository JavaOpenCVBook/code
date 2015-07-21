package org.javaopencvbook;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.javaopencvbook.utils.ImageProcessor;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class App 
{
	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 
	}
	
	private JFrame frame;
	private JLabel imageLabel;
	private CascadeClassifier faceDetector;
	
	public static void main(String[] args) {
		App app = new App();
		app.initGUI();
		
		app.loadCascade();
		
		app.runMainLoop(args);
		
		
		
	}
	
	private void loadCascade() {
		String cascadePath = "src/main/resources/cascades/lbpcascade_frontalface.xml";
	    faceDetector = new CascadeClassifier(cascadePath);
	}

	private void initGUI() {
		frame = new JFrame("Camera Input Example");  
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		frame.setSize(400,400);  
		imageLabel = new JLabel();
		frame.add(imageLabel);
		frame.setVisible(true);       
	}

	private void runMainLoop(String[] args) {
		ImageProcessor imageProcessor = new ImageProcessor();
		Mat webcamMatImage = new Mat();  
		Image tempImage;  
		VideoCapture capture = new VideoCapture(0);
		capture.set(Videoio.CV_CAP_PROP_FRAME_WIDTH,640);
		capture.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT,480);

		if( capture.isOpened()){  
			while (true){  
				capture.read(webcamMatImage);  
				if( !webcamMatImage.empty() ){  
					detectAndDrawFace(webcamMatImage);
					tempImage= imageProcessor.toBufferedImage(webcamMatImage);
					
					ImageIcon imageIcon = new ImageIcon(tempImage, "Captured video");
					imageLabel.setIcon(imageIcon);
					frame.pack();  //this will resize the window to fit the image
				}  
				else{  
					System.out.println(" -- Frame not captured -- Break!"); 
					break;  
				}
			}  
		}
		else{
			System.out.println("Couldn't open capture.");
		}
		
	}

	private void detectAndDrawFace(Mat image) {
	    MatOfRect faceDetections = new MatOfRect();
	    faceDetector.detectMultiScale(	image, faceDetections, 1.1, 7,0,new Size(250,40),new Size());
	    // Draw a bounding box around each face.
	    for (Rect rect : faceDetections.toArray()) {
	        Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
	    }
	}
}
