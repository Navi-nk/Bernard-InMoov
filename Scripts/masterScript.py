from org.myrobotlab.net import BareBonesBrowserLaunch


#Created by  Navi
#Master script to initiate all the services needed for operation of Bernard

#Connecting to virtual or real
startVirtualBernard=False

#ports for connecting to adruino
leftPort = "COM3"
rightPort = "COM4"

#chatbot choice: we have two chatbots built using api.ai and aiml
#default is api.ai but aiml option is still there for people to explore
#check out bernardSpeakAIML.py script for reference
chatbot="ApiAI"
apiKey="4f0a1116671b4e2ab5e38f3f5c17a840"

#Speech parameters
#using marrytts for speech
#check out for "http://mary.dfki.de:59125/"
speechType = "MarySpeech"
voiceStyle = "cmu-rms-hsmm" #male voice
voiceEffects = "F0Add(f0Add:20.0)+F0Scale(f0Scale:1.5)" #audio effects-tune it as and when required

#Speech recognition params
#this uses google speech recognition using google chrome
#this is a limitation, needs to be fixed in future
speechRecoType = "WebkitSpeechRecognition"

guiType = "WebGui"
recognize = False
gestureRecord = False
gesturePlay = False

def heardSentence(sentence):
    print(sentence)
    global recognize
    global gestureRecord
    global gesturePlay
    if sentence == "start conversation":
        mouth.speakBlocking("starting conversation mode")
        ear.addTextListener(chatBot)
        kinect.setFacingUser(True)
        bernard.mouthControl.setmouth(60,120)
    elif sentence == "stop conversation":
        ear.removeListener("publishText", "chatBot", "onText")
        kinect.setFacingUser(False)
    elif sentence == "start recognition":
        ear.removeListener("publishText", "chatBot", "onText")
        global fr
        if fr is None:
            fr=opencv.addFilter("fr","FaceRecognizer")
            opencv.setDisplayFilter("fr")
            print("testing")
            sleep(5)
        print(fr.getLastRecognizedName())
        name=fr.getLastRecognizedName()
<<<<<<< HEAD
<<<<<<< HEAD
        if name == "None":
            mouth.speakBlocking("Sorry could not recognize you")
        else:
            sleep(3)
=======
        if name == "none":
            mouth.speakBlocking("could not recognize the person standing infront of me")
>>>>>>> 3366f715887f1143f91b6615674bb9f646e85631
=======
        if name == "none":
            mouth.speakBlocking("could not recognize the person standing infront of me")
>>>>>>> 3366f715887f1143f91b6615674bb9f646e85631
        ear.addTextListener(chatBot) 
        chatBot.getParsedRespose(chatBot.getAIResponse(name))
    elif sentence == "start introduction":
        ear.removeListener("publishText", "chatBot", "onText")
<<<<<<< HEAD
<<<<<<< HEAD
        mouth.speakBlocking("who is this standing in front of me?")
=======
=======
>>>>>>> 3366f715887f1143f91b6615674bb9f646e85631
        mouth.speakBlocking("who is this standing infront of me?")
>>>>>>> 3366f715887f1143f91b6615674bb9f646e85631
        recognize = True
    elif recognize == True:
        ear.removeListener("publishText", "chatBot", "onText")
        global fr
        if fr is None:
            fr=opencv.addFilter("fr","FaceRecognizer")
            opencv.setDisplayFilter("fr")
        fr.setTrainName(sentence)
        fr.setModeTrain()
        sleep(20)
        fr.setModeRecognize()
        mouth.speakBlocking("Learning done")
        recognize = False
    elif sentence == "start imitation":
        #mouth.speakBlocking("Starting Imitation mode")
        kinect.setRobotImitation(True)
    elif sentence == "stop imitation":
        #mouth.speakBlocking("Stopping Imitation mode")
        kinect.setRobotImitation(False)
        bernard.restAll()
    elif sentence == "start facing user":
        #mouth.speakBlocking("Starting Facing User Mode")
        kinect.setFacingUser(True)
    elif sentence == "stop facing user":
        #mouth.speakBlocking("Stopping Facing User Mode")
        kinect.setFacingUser(False)
    elif sentence == "record gesture":
        ear.removeListener("publishText", "chatBot", "onText")
        mouth.speakBlocking("what name should i give to this gesture?")
        gestureRecord=True
    elif gestureRecord == True:
        kinect.recordGesture(sentence)
        sleep(8)
        mouth.speakBlocking("Gesture Recording Done")
        gestureRecord = False
    elif sentence == "play gesture":
        ear.removeListener("publishText", "chatBot", "onText")
        mouth.speakBlocking("what gesture should i play?")
        gesturePlay = True
    elif gesturePlay == True:
        if(kinect.findGesture(sentence)):
            kinect.playGesture(sentence)
        else:
            mouth.speakBlocking("sorry! I dont know that gesture")
        gesturePlay = False
    elif sentence == "roll eyes":
        rollEyes()
    elif sentence == "relax":
        bernard.restAll()
    


#create bernard
def createBernard():
    #bernard.startAll(leftPort, rightPort)
    #bernard.startRightHand(rightPort)
    bernard.startMouth()
    bernard.startMouthControl(leftPort)
    bernard.startEar()
    #bernard.startRightArm(rightPort)
    #bernard.startLeftArm(leftPort)
    bernard.startHead(leftPort)
    #bernard.startTorso(leftPort)
    ear.addListener("publishText", python.name, "heardSentence")
    chatBot.configureAIBot(apiKey)
    chatBot.addTextListener(mouth)
    
    
#create virtual inmoov
def createVirtualBernard():
    global vinmoov
    v1 = Runtime.start('v1', 'VirtualArduino')
    v2 = Runtime.start('v2', 'VirtualArduino')
    v1.connect(leftPort)
    v2.connect(rightPort)
    bernard.startAll(leftPort, rightPort)
    bernard.startVinMoov()
    ear.addListener("publishText", "python", "heardSentence")
    chatBot.configureAIBot(apiKey)
    chatBot.addTextListener(mouth)

def startWebGui():
    webgui.autoStartBrowser(False)
    webgui.startService()
    BareBonesBrowserLaunch.openURL("http://localhost:8888/#service/bernard.ear")

def setMouthEffects():
    mouth.setVoice(voiceStyle)
    mouth.setAudioEffects(voiceEffects)

if __name__ == "__main__":
    #start webgui for speech recognition-Fix this soon
    webgui = Runtime.create("webgui",guiType)
    

    #start bernard mouth service
    mouth = Runtime.createAndStart('bernard.mouth',speechType)
    setMouthEffects()

    #start bernard ear
    ear = Runtime.createAndStart("bernard.ear", speechRecoType)

    #chat bot
    chatBot = Runtime.createAndStart("chatBot", chatbot)

    #create bernard object
    bernard = Runtime.start('bernard','InMoov')

    #opencv for face recognition
    opencv = Runtime.start("cv","OpenCV")
    opencv.setCameraIndex(1)
    fr=opencv.addFilter("fr","FaceRecognizer")
    fr.train()
    opencv.setDisplayFilter("fr")
    opencv.capture()

    #kinect for imitation
    kinect=Runtime.start("kinect","Bernard")

    #Link to bernard- either in simulator or real 
    if startVirtualBernard:
        createVirtualBernard()
    else:
        createBernard()

    startWebGui()

    #setting up kinect
    kinect.startRobotImitation()
    sleep(5)
    kinect.setRobotImitation(False)
    kinect.addKinectObservers()
    kinect.bernard = bernard
   

