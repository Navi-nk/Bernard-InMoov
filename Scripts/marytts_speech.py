speech=Runtime.start('speech','MarySpeech')
speech.setVoice('cmu-rms-hsmm')
speech.setAudioEffects('F0Add(f0Add:20.0)+F0Scale(f0Scale:1.5)')
speech.speakBlocking('Lets start robot revolution')