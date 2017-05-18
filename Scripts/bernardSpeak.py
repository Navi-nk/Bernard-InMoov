from org.myrobotlab.net import BareBonesBrowserLaunch

leftPort="COM3"
aimlBotName = "bernard"
aimlUserName = "Navi"
webgui = Runtime.create("webgui","WebGui")

mouth=Runtime.start('speech','MarySpeech')
mouth.setVoice('cmu-rms-hsmm')
mouth.setAudioEffects('F0Add(f0Add:20.0)+F0Scale(f0Scale:1.5)')

ear = Runtime.createAndStart("ear", "WebkitSpeechRecognition")
#ear.addListener("publishText", python.name, "heard");
ear.addMouth(mouth)


bernardBot = Runtime.createAndStart("Bernard", "ProgramAB")
bernardBot.startSession(aimlUserName, aimlBotName)
htmlfilter = Runtime.createAndStart("htmlfilter", "HtmlFilter")
bernardBot.addTextListener(htmlfilter)
htmlfilter.addTextListener(mouth)

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
