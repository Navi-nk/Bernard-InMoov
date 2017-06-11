rightHand = Runtime.createAndStart("rightHand","InMoovHand")
sg=Runtime.createAndStart("sg", "SensorGlove")
sg.arduino=rightHand.arduino
sg.connect('COM3')
rightHand.arduino.enablePin(62,1)
rightHand.arduino.enablePin(63,1)
sg.connectHand(rightHand)
sg.addArduinoListener()