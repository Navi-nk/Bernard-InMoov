rightHand = Runtime.createAndStart("rightHand","InMoovHand")
sg=Runtime.createAndStart("sg", "SensorGlove")
sg.connect('COM3','mega')
sg.addArduinoListener()