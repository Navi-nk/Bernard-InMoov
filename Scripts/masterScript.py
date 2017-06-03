from org.myrobotlab.net import BareBonesBrowserLaunch

#Created by  Navi
#Master script to initiate all the services needed for operation of Bernard

#Connecting to virtual or real
startVirtualBernard=True

#ports for connecting to adruino
leftPort = "COM"
rightPort = "COM"

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

guiType = WebGui


def heardSentence(sentence):
    print(sentence)
    if sentence == "start conversation":
        ear.addTextListener(chatBot)
        bernard.mouthControl.setmouth(30,80)
        mouth.speakBlocking('Starting conversation mode')
    elif sentence == "stop conversation":
        ear.removeListener("publishText", chatbot, "onText")
        mouth.speakBlocking('Stoping conversation mode')
    elif sentence == "start recognition":
        fr=opencv.addFilter("fr","FaceRecognizer")
        opencv.setDisplayFilter("fr")
        opencv.capture()
        fr.train()
        fr.setModeRecognize()
        chatBot.getResponse(fr.getLastRecognizedName())
    elif sentence == "start introduction":
        if fr is None:
            fr=opencv.addFilter("fr","FaceRecognizer")
        opencv.setDisplayFilter("fr")
        opencv.capture()
        #fr.train()
        fr.setModeTrain()
        fr.setTrainName("li")
        sleep(5)
        fr.setModeRecognize()
    elif sentence == "start imitation":
        kinect.startRobotImitation()
        kinect.addKinectObservers()


def createBernard():
    chatBot.configureAIBot(apiKey)
    chatBot.addTextListener(mouth)
    ear.addListener("publishText", python.name, "heardSentence")
    


def createVirtualBernard():
    v1 = Runtime.start('v1', 'VirtualArduino')
    v2 = Runtime.start('v2', 'VirtualArduino')
    v1.connect(leftPort)
    v2.connect(rightPort)
    bernard.startVinMoov()

def startWebGui():
    webgui.autoStartBrowser(False)
    webgui.startService()
    BareBonesBrowserLaunch.openURL("http://localhost:8888/#service/ear")

def setMouthEffects():
    mouth.setVoice(voiceStyle)
    mouth.setAudioEffects(voiceEffects)

if __name == "__main__":
    #start webgui for speech recognition-Fix this soon
    webgui = Runtime.create("webgui",guiType)
    startWebGui()

    #start bernard mouth service
    mouth = Runtime.createAndStart('bernard.mouth',speechType)
    setMouthEffects()

    #start bernard ear
    ear = Runtime.createAndStart("bernard.ear", speechRecoType)

    #chat bot
    chatBot = Runtime.createAndStart("chatBot", chatbot)

    #opencv for face recognition
    opencv = Runtime.start("cv","OpenCV")

    kinect=Runtime.createAndStart("kinect","Bernard")

    #create bernard object
    bernard = Runtime.create('bernard','InMoov')
    bernard.startAll(leftPort, rightPort)

    #Link to bernard- either in simulator or real 
    if startVirtualBernard
        createVirtualBernard()
    else:
        createBernard()

