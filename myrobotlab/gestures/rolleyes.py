def rollEyes():
  moveEyes(50,60)
  sleep(50)
  moveEyes(70,120)
  sleep(50)
  moveEyes(100,90)
  sleep(50)
  moveEyes(120,60)
  sleep(50)
  moveEyes(90,60)

def moveEyes(x,y):
     bernard.head.eyeX.moveTo(x)
     bernard.head.eyeY.moveTo(y)