
from org.myrobotlab.net import BareBonesBrowserLaunch

leftPort="COM3"
webgui = Runtime.create("webgui","WebGui")

mouth=Runtime.start('speech','MarySpeech')
mouth.setVoice('cmu-rms-hsmm')
mouth.setAudioEffects('F0Add(f0Add:20.0)+F0Scale(f0Scale:1.5)')

ear = Runtime.createAndStart("ear", "WebkitSpeechRecognition")
#ear.addListener("publishText", python.name, "heard");
ear.addMouth(mouth)

bernardBot = Runtime.createAndStart("bernardBot", "ApiAI")
bernardBot.configureAIBot("4f0a1116671b4e2ab5e38f3f5c17a840")
bernardBot.addTextListener(mouth)

ear.addTextListener(bernardBot)

bernard = Runtime.create("bernard", "InMoov")

bernard.startHead(leftPort)
bernard.mouth=mouth
bernard.startMouthControl(leftPort)
bernard.mouthControl.setMouth(mouth)
bernard.mouthControl.setmouth(30,80)

webgui.autoStartBrowser(False)
webgui.startService()
BareBonesBrowserLaunch.openURL("http://localhost:8888/#service/ear")

#bernardBot.getResponse("hello")
#bernardBot.getResponse("what is my name")
