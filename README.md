code
====

Code repository for Packt Java OpenCV Book

https://www.packtpub.com/application-development/opencv-java-javacv

In order to setup the code, follow these instructions:

1- Setup Maven according to https://maven.apache.org/users/index.html . Make sure you check the links https://maven.apache.org/download.cgi https://maven.apache.org/install.html and https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html .

Also, make sure you download the BINARY file and not the SOURCE one.

2- Go to https://github.com/JavaOpenCVBook/code and download it to some folder (click on Download zip on the right)


3- Extract the projects from the zip file

4- Customize the desired pom.xml from the chapter according to your platform. For instance, if you want to build for Windows x64, change the desired project pom.xml file from:

	<dependency>
		<groupId>org.javaopencvbook</groupId>
		<artifactId>opencvjar</artifactId>
		<version>2.4.7</version>    	     
    	</dependency>
	
	<dependency>
		<groupId>org.javaopencvbook</groupId>
		<artifactId>opencvjar-runtime</artifactId>
		<version>2.4.7</version>
		<classifier>natives-windows-x86</classifier>
	</dependency>

to

	<dependency>
		<groupId>org.javaopencvbook</groupId>
		<artifactId>opencvjar</artifactId>
		<version>2.4.7</version>    	     
    	</dependency>
	
	<dependency>
		<groupId>org.javaopencvbook</groupId>
		<artifactId>opencvjar-runtime</artifactId>
		<version>2.4.7</version>
		<classifier>natives-windows-x86_64</classifier>
	</dependency>
or maybe
natives-mac-x86_64 for 64 bit osx.

5- Import your maven project in your favorite IDE.

5-a) In Netbeans, File -> Open Project -> Browse to the project folder. Click on the Open Project button. Now, right-click the project, and select Build with dependencies. It's time to tell where the native libraries are. Right-click the project -> Properties -> Run ->  VM Options -> Point java.library.path VM option to native files using the following:
 -Djava.library.path="C:\Users\baggio\Documents\OpenCV Offline\code\chapter2\swing-imageshow\target\natives" . Of course, you should change   -Djava.library.path="C:\Users\baggio\Documents\OpenCV Offline\code\chapter2\swing-imageshow\target\natives" to your native files folder. In case Netbeans give you hard time erasing the field everytime, just shut it down and open it again. Now, run the project: Right-click it, Run.

5-b) In Eclipse, generate a project file through:

mvn eclipse::eclipse

Then, import it as an existing project in Eclipse. Before running it, check your referenced libraries and look for opencvjar-3.0.0 or opencvjar-2.4.7 and right click it -> Properties -> Native library -> Workspace -> Point it to the native file location, e.g.: histogram/target/natives . Right click your project -> Run as -> Java application -> Point to the App class, press ok and it should run.
