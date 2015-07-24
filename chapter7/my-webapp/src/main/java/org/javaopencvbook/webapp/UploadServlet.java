package org.javaopencvbook.webapp;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;


@MultipartConfig(fileSizeThreshold=1024*1024*2, // 2MB
maxFileSize=1024*1024*10,      // 10MB
maxRequestSize=1024*1024*50)   // 50MB
public class UploadServlet extends HttpServlet {
	
	private CascadeClassifier faceDetector;
	

	public Mat openFile(String fileName) throws Exception{
		Mat newImage = Imgcodecs.imread(fileName);
		if(newImage.dataAddr()==0){
			throw new Exception ("Couldn't open file "+fileName);
		}
		return newImage;
	}
	
	private void loadCascade() {
		String cascadePath = getResourcePath("/cascades/lbpcascade_frontalface.xml");
	    faceDetector = new CascadeClassifier(cascadePath);
	}
	
	private void detectFaceAndDrawHat(Mat image, Mat overlay) {		
	    MatOfRect faceDetections = new MatOfRect();
	    
	    faceDetector.detectMultiScale(	image, faceDetections, 1.1, 7,0,new Size(),new Size());
	 
	    for (Rect rect : faceDetections.toArray()) {
	        
	        double hatGrowthFactor = 2.3;//1.8;
	        
	        
	        int hatWidth = (int) (rect.width *hatGrowthFactor);
	        int hatHeight = (int) (hatWidth * overlay.height() / overlay.width());
	        int roiX =  rect.x - (hatWidth-rect.width)/2;
	        int roiY =  (int) (rect.y  - 0.6*hatHeight);
	        roiX =  roiX<0?0:roiX;
	        roiY = roiY<0?0:roiY;
	        hatWidth = hatWidth+roiX > image.width() ? image.width() -roiX : hatWidth;
	        
	        hatHeight = hatHeight+roiY > image.height() ? image.height() - roiY : hatHeight;
	        Rect roi = new Rect( new Point(roiX,roiY), new Size( hatWidth, hatHeight));
	        
	        
	        Mat resized = new Mat();
	        Size size = new Size(hatWidth,hatHeight);
	        Imgproc.resize(overlay,resized, size);
	        Mat destinationROI = image.submat( roi );
	        resized.copyTo( destinationROI , resized);
	        
	       
	        break;
	        
	    }
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 
		
		loadCascade();
	
		Mat image = receiveImage(request);
		Mat overlay = loadOverlayImage();
		detectFaceAndDrawHat(image, overlay);
		writeResponse(response, image);
	}

	private void writeResponse(HttpServletResponse response, Mat image)
			throws IOException {
		MatOfByte outBuffer = new MatOfByte();
		Imgcodecs.imencode(".jpg", image, outBuffer);
		
		
		response.setContentType("image/jpeg");  
		ServletOutputStream out;  
		out = response.getOutputStream();
		
		out.write(outBuffer.toArray());
	}

	private Mat loadOverlayImage() {
		String overlayFileName = getResourcePath("/images/fedora.png");
		                         
		
		Mat overlay = null;
		try {
			overlay = openFile(overlayFileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return overlay;
	}

	private Mat receiveImage(HttpServletRequest request) throws IOException, ServletException {
		byte[] encodedImage = receiveImageBytes(request);
		return convertBytesToMatrix(encodedImage);
	}

	private Mat convertBytesToMatrix(byte[] encodedImage) {
		Mat encodedMat = new Mat(encodedImage.length,1,CvType.CV_8U);
		encodedMat.put(0, 0,encodedImage);
		
		Mat image = Imgcodecs.imdecode(encodedMat, Imgcodecs.CV_LOAD_IMAGE_ANYCOLOR);
		return image;
	}

	private byte[] receiveImageBytes(HttpServletRequest request)
			throws IOException, ServletException {
		InputStream is = (InputStream) request.getPart("file").getInputStream();//.getAttribute("image");
		
		BufferedInputStream bin = new BufferedInputStream(is);  
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();  
		int ch =0; 
		while((ch=bin.read())!=-1)  
		{  
			buffer.write(ch);  
		}  
		buffer.flush();
		bin.close();  
		
		byte[] encodedImage = buffer.toByteArray();
		return encodedImage;
	}

	private String getResourcePath(String path) {
		String absoluteFileName = getClass().getResource(path).getPath();
		absoluteFileName = absoluteFileName.replaceFirst("/", "");
		return absoluteFileName;
	}
	

}