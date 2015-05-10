package org.javaopencvbook;

public class GenerateVecFile {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String directory = "TrainImages";
		for(int i=0;i<=549;i++){
			System.out.println(directory+"\\pos-"+ i + ".pgm 1 0 0 100 40");
		}
		
		for(int i=0;i<=499;i++){
			System.out.println(directory+"\\neg-"+ i + ".pgm");
		}

	}

}
