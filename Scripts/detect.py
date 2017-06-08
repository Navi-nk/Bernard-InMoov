
#start open cv
opencv = Runtime.start("cv","OpenCV")
opencv.setCameraIndex(1)
fr=opencv.addFilter("fr","FaceRecognizer")
opencv.setDisplayFilter("fr")
opencv.capture()
fr.train()
#fr.setModeTrain()
#fr.setTrainName("li")
#sleep(5)
#fr.setModeRecognize()
#fr.train()

#recognize face
while 1:
	#fr = opencv.getFilter("fr")
	print(fr.getLastRecognizedName())
	sleep(3)