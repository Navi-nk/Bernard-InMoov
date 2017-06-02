
#start open cv
opencv = Runtime.start("cv","OpenCV")
opencv.addFilter("fr","FaceRecognizer")
opencv.setDisplayFilter("fr")
opencv.capture()

#recognize face
while 1:
	fr = opencv.getFilter("fr")
	print(fr.getLastRecognizedName())
	sleep(3)