
mouth = Runtime.createAndStart("i01.mouth", "MarySpeech")
mouth.setVoice('cmu-rms-hsmm')
mouth.setAudioEffects('F0Add(f0Add:20.0)+F0Scale(f0Scale:1.5)')
i01 = Runtime.start('i01','InMoov')
v1 = Runtime.start('v1', 'VirtualArduino')
v1.connect('COM20')
v2 = Runtime.start('v2', 'VirtualArduino')
v2.connect('COM91')
i01.startAll('COM20', 'COM91')



i01.startVinMoov()
i01.stopVinMoov()
