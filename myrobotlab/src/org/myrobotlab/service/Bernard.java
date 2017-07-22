package org.myrobotlab.service;

//import java.io.IOException;

import java.util.Observable;
import java.util.Observer;

import org.myrobotlab.framework.Service;
import org.myrobotlab.framework.ServiceType;
import org.myrobotlab.logging.Level;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.logging.Logging;
import org.myrobotlab.logging.LoggingFactory;
import org.slf4j.Logger;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import edu.ufl.digitalworlds.math.Geom;
import org.myrobotlab.j4kdemo.KSkeleton;
import org.myrobotlab.j4kdemo.Joint;
import org.myrobotlab.service.J4K;
import org.myrobotlab.service.InMoov;
import org.myrobotlab.service.VirtualArduino;

import org.myrobotlab.bernard.Gesture;

public class Bernard extends Service implements Observer {

	private static final long serialVersionUID = 1L;
	
	// MRL Services
	public J4K KinectViewer;
	public InMoov bernard;
	public VirtualArduino v1;
	public VirtualArduino v2;

	// Skeletal Mapping Variables
	public KSkeleton kSkeleton;
	public boolean robotImitation = false;
	public boolean vinMoovStarted = false;
	public boolean userFacing = false;
	public boolean recordingGesture = false;
	Gesture currentGesture;
	public int gestureLength = 200;
	//public mapping coordinates for virtual bernard
	
	
	// InMoov Variables Initialisation
	public String leftPort = "COM20";
	public String rightPort = "COM91";
	public String headPort = leftPort;

	public final static Logger log = LoggerFactory.getLogger(Bernard.class);
	  
	public Bernard(String n) {
		super(n);
	}
	/**
	 * This static method returns all the details of the class without it having
	 * to be constructed. It has description, categories, dependencies, and peer
	 * definitions.
	 * 
	 * @return ServiceType - returns all the data
	 * 
	 */
	static public ServiceType getMetaData() {

	    ServiceType meta = new ServiceType(Bernard.class.getCanonicalName());
	    meta.addDescription("Bernard");
	    meta.setAvailable(true); // false if you do not want it viewable in a gui
	    // add dependency if necessary
	    // meta.addDependency("org.coolproject", "1.0.0");
	    meta.addCategory("general");
	    meta.addPeer("KinectViewer", "J4K", "Kinect Viewer");
	    meta.addPeer("bernard", "InMoov", "InMoov");
	    meta.addPeer("v1", "VirtualArduino", "Virtual Arduino 1: Left");
	    meta.addPeer("v2", "VirtualArduino", "Virtual Arduino 2: Right");
	   // meta.addPeer("AliceBot", "ProgramAB", "Alice Chatbot");
	   // meta.addPeer("bernard.mouth", "NaturalReaderSpeech", "Speech Control");
	    return meta;
	}

	public static void main(String[] args) {
		try {
			LoggingFactory.init(Level.INFO);

			Bernard VirtualBernard = (Bernard) Runtime.start("Bernard", "Bernard");
			Runtime.start("gui", "SwingGui");
	      
	    } catch (Exception e) {
	    	Logging.logError(e);
	    }
	}

	public void startKinectViewer() {
		if(KinectViewer == null) {
			KinectViewer = (J4K) startPeer("KinectViewer");
		}
	}
	
	public void setRobotImitation(boolean state) {
		robotImitation = state;
	}
	
	public void setFacingUser(boolean state) {
		userFacing = state;
	}
	
	public void startRobotImitation() {
		robotImitation = true; 
		startKinectViewer();
		//System.out.println("startRobotImitation");
		//if(method=="angular") {
			//offset for kinect is 0,0,220
		//}
	}
	public void stopRobotImitation() {
		robotImitation = false;
	}
	
	public void addKinectObservers() {
		this.KinectViewer.sub.addObserver(this);
		System.out.println("Current Kinect Observers: "+String.valueOf(this.KinectViewer.sub.countObservers()));
	}
		
	public void startInMoov() {
		// Actual InMoov
		bernard = (InMoov) startPeer("bernard");
		try {
			bernard.startAll(leftPort, rightPort);
			//bernard.startArm("right", rightPort, null);
			//bernard.startArm("left", leftPort, null);
			//bernard.startHead(headPort);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void startVinMoov() {
		try {
			bernard = (InMoov) startPeer("bernard");
			//vinMoov = bernard;
			v1 = (VirtualArduino) startPeer("v1");
			//VirtualArduino va1 = v1;
			v2 = (VirtualArduino) startPeer("v2");
			//VirtualArduino va2 = v2;
			v1.connect(leftPort);
			v2.connect(rightPort);
			bernard.startAll(leftPort, rightPort);
			
			bernard.startVinMoov();
			//vinMoov.startIntegratedMovement();
			vinMoovStarted = true;
			
		} catch (Exception e) {
		      Logging.logError(e);
		}
	}
	/*
	public void getSkeleton(Skeleton sk) {
		this.kSkeleton = sk;
		if(this.robotImitation) {
			mapSkeletonToRobot();
		}
	}
	*/
	public void mapSkeletonToRobot() {
		
		double t[] = Geom.identity4();
		double inv_t[] = Geom.identity4();
		//double torsoTransform = kSkeleton.getTorsoTransform(t, inv_t);
		//double headTransform = kSkeleton.getHeadTransform(t, inv_t);
		//double fixHeadTransform = kSkeleton.getFixHeadTransform(t, inv_t);
		double shoulderTransform = kSkeleton.getShoulderTransform(t, inv_t);
		//double betweenHandsTransform = kSkeleton.getBetweenHandsTransform(t, inv_t);
		//double rightArmTransform = kSkeleton.getRightArmTransform(t, inv_t);
		//double rightForearmTransform = kSkeleton.getRightArmTransform(t, inv_t);
		//Float leftArmTransform = (float) kSkeleton.getLeftArmTransform(t, inv_t);
		//double leftForearmTransform = kSkeleton.getLeftArmTransform(t, inv_t);

		Joint[] myJoint = kSkeleton.getJoints();
		//Joint headJoint = myJoint[KSkeleton.HEAD];
		//Joint neckJoint = myJoint[KSkeleton.NECK];
		//Joint spineShoulderJoint = myJoint[KSkeleton.SPINE_SHOULDER];
		
		// Head
		
		if(userFacing != true) {
			//System.out.println(90+jointOrientation[4*Skeleton.HEAD+2]*240);
			//bernard.head.rothead.moveTo(90-jointOrientation[4*Skeleton.NECK+3]*120);
			
			Vector3f neck = kSkeleton.getBone(KSkeleton.SPINE_SHOULDER, KSkeleton.HEAD);
			//float[] neckAngle = neckJoint.getAbsOrientation().toAngles(null);
			
			float degrees = Math.round(Math.toDegrees(FastMath.atan2(neck.getY(),neck.getZ())));
			//System.out.println(kSkeleton.torsoOrientation[1]*90);
			if(bernard.head != null) {
				bernard.head.neck.moveTo(65+(90-degrees)-kSkeleton.torsoOrientation[1]*90);
			}
		}
		
		// Left Arm 
		Joint shoulderLeftJoint = myJoint[KSkeleton.SHOULDER_LEFT];
		Joint elbowLeftJoint = myJoint[KSkeleton.ELBOW_LEFT];
		//Joint wristLeftJoint = myJoint[KSkeleton.WRIST_LEFT];
		//Joint handLeftJoint = myJoint[KSkeleton.HAND_LEFT];
		//Joint handTipLeftJoint = myJoint[KSkeleton.HAND_TIP_LEFT];
		
		Quaternion elbowLeftQ = elbowLeftJoint.getAbsOrientation();
		float[] elbowLeftAngle = elbowLeftQ.toAngles(null);
		//Quaternion wristLeftQ = wristLeftJoint.getAbsOrientation();
		//float[] wristLeftAngle = wristLeftQ.toAngles(null);
		//Quaternion handLeftQ = handLeftJoint.getAbsOrientation();
		//float[] handLeftAngle = handLeftQ.toAngles(null);
		
		//Float wristLeftRotateAngle = wristLeftJoint.getNormalAngle();
		//System.out.println(Math.toDegrees(wristLeftRotateAngle));
		
		
		//Quaternion handLeftQ = handLeftJoint.getAbsOrientation();
		//float[] handLeftAngle = handLeftQ.toAngles(null);
		/*
		Vector3f spineShoulderVec = spineShoulderJoint.getAbsPosition();
		Vector3f shoulderLeftVec = shoulderLeftJoint.getAbsPosition();
		Vector3f elbowLeftVec = elbowLeftJoint.getAbsPosition();
		Vector3f wristLeftVec = wristLeftJoint.getAbsPosition();
		Vector3f handLeftVec = handLeftJoint.getAbsPosition();
		Vector3f handTipLeftVec = handTipLeftJoint.getAbsPosition();
		*/
		//Vector3f leftShoulder = kSkeleton.getBone(KSkeleton.SPINE_SHOULDER, KSkeleton.SHOULDER_LEFT);
		Vector3f leftArm = kSkeleton.getBone(KSkeleton.SHOULDER_LEFT, KSkeleton.ELBOW_LEFT);
		Vector3f leftForeArm = kSkeleton.getBone(KSkeleton.ELBOW_LEFT, KSkeleton.WRIST_LEFT);
		//Vector3f leftHand = kSkeleton.getBone(KSkeleton.WRIST_LEFT, KSkeleton.HAND_LEFT);
		//Vector3f leftFingers = kSkeleton.getBone(KSkeleton.HAND_LEFT, KSkeleton.HAND_TIP_LEFT);
		//Vector3f leftThumb = kSkeleton.getBone(KSkeleton.HAND_LEFT, KSkeleton.THUMB_LEFT);
		
		float degrees = Math.round(Math.toDegrees(FastMath.atan2(leftArm.getY(),Math.abs(leftArm.getX()))));
		if(bernard.leftArm != null) {
			bernard.leftArm.omoplate.moveTo(90+degrees);
		}

		degrees = Math.round(Math.toDegrees(FastMath.atan2(leftArm.getZ(),leftArm.getY())));
		
		if(bernard.leftArm != null) {
			bernard.leftArm.shoulder.moveTo(degrees>0?degrees-180:degrees+180);
		}
		degrees = (float) Math.toDegrees(elbowLeftAngle[1]);
		if(bernard.leftArm != null) {
			bernard.leftArm.rotate.moveTo(Math.round(((degrees<-90)?360+degrees:degrees)+kSkeleton.torsoOrientation[0]*90));
			bernard.leftArm.bicep.moveTo(Math.round(Math.toDegrees(leftForeArm.angleBetween(leftArm))));
		}
		
		// for wrist, need to use all three rotations
		
		//degrees = (float) Math.toDegrees(wristLeftRotateAngle);
		//bernard.leftHand.wrist.moveTo(degrees);
				
		// Right Arm
		Joint shoulderRightJoint = myJoint[KSkeleton.SHOULDER_RIGHT];
		Joint elbowRightJoint = myJoint[KSkeleton.ELBOW_RIGHT];
		//Joint wristRightJoint = myJoint[KSkeleton.WRIST_RIGHT];
		//Joint handRightJoint = myJoint[KSkeleton.HAND_RIGHT];
		//Joint handTipRightJoint = myJoint[KSkeleton.HAND_TIP_LEFT];

		//Quaternion shoulderRightQ = shoulderRightJoint.getAbsOrientation();
		Quaternion elbowRightQ = elbowRightJoint.getAbsOrientation();
		float[] elbowRightAngle = elbowRightQ.toAngles(null);
		//Quaternion wristRightQ = wristRightJoint.getAbsOrientation();
		//float[] wristRightAngle = wristRightQ.toAngles(null);
		//Quaternion wristRightRelativeQ = elbowRightQ.inverseLocal().mult(wristRightQ);
		//float[] wristRightRotateAngle = wristRightRelativeQ.toAngles(null);
		//Quaternion handRightQ = handRightJoint.getAbsOrientation();
		//float[] handRightAngle = handRightQ.toAngles(null);
		/*
		Vector3f shoulderRightVec = shoulderRightJoint.getAbsPosition();
		Vector3f elbowRightVec = elbowRightJoint.getAbsPosition();
		Vector3f wristRightVec = wristRightJoint.getAbsPosition();
		Vector3f handRightVec = handRightJoint.getAbsPosition();
		Vector3f handTipRightVec = handTipRightJoint.getAbsPosition();
		*/
		//Vector3f rightShoulder = kSkeleton.getBone(KSkeleton.SPINE_SHOULDER, KSkeleton.SHOULDER_RIGHT);
		Vector3f rightArm = kSkeleton.getBone(KSkeleton.SHOULDER_RIGHT, KSkeleton.ELBOW_RIGHT);
		Vector3f rightForeArm = kSkeleton.getBone(KSkeleton.ELBOW_RIGHT, KSkeleton.WRIST_RIGHT);
		//Vector3f rightHand = kSkeleton.getBone(KSkeleton.WRIST_RIGHT, KSkeleton.HAND_RIGHT);
		//Vector3f rightFingers = kSkeleton.getBone(KSkeleton.HAND_RIGHT, KSkeleton.HAND_TIP_RIGHT);
		//Vector3f rightThumb = kSkeleton.getBone(KSkeleton.HAND_RIGHT, KSkeleton.THUMB_RIGHT);
		
		degrees = Math.round(Math.toDegrees(FastMath.atan2(rightArm.getY(),Math.abs(rightArm.getX()))));
		if(bernard.rightArm != null) {
			bernard.rightArm.omoplate.moveTo(90+degrees);
		}
		degrees = Math.round(Math.toDegrees(FastMath.atan2(rightArm.getZ(),rightArm.getY())));
		if(bernard.rightArm != null) {
			bernard.rightArm.shoulder.moveTo(30+(degrees>0?degrees-180:degrees+180));
		}
		
		degrees = (float) -Math.toDegrees(elbowRightAngle[1]);
		if(bernard.rightArm != null) {
			bernard.rightArm.rotate.moveTo(Math.round(((degrees<-90)?360+degrees:degrees)-kSkeleton.torsoOrientation[0]*90));
			bernard.rightArm.bicep.moveTo(Math.round(Math.toDegrees(rightForeArm.angleBetween(rightArm))));
		}
		//degrees = (float) Math.toDegrees(wristRightAngle[1]);
		//bernard.rightHand.wrist.moveTo(90-degrees);
		
		
		// Body
		
		// Torso Upper
		if(bernard.torso != null) {
			bernard.torso.topStom.moveTo(90+Math.toDegrees(Math.asin(Math.abs(shoulderLeftJoint.getAbsY()-shoulderRightJoint.getAbsY())/shoulderTransform)));
			bernard.torso.midStom.moveTo(90+kSkeleton.torsoOrientation[0]*90);
		}
		// Torso Low
		//bernard.torso.lowStom.moveTo(90+kSkeleton.torsoOrientation[1]*90);
		
	}
	
	public void startFacingUser() {
		userFacing = true;
		startKinectViewer();
		faceUser();
	}
	
	public void stopFacingUser() {
		userFacing = false;
	}
	
	public void faceUser() {
		Float bernardEyesY = 0.43f;
		Joint head = kSkeleton.getJoint(KSkeleton.HEAD);
		//Float zDistance = head.getAbsZ();
		//Float height = head.getAbsY();
		Float neckYAngle = (float) Math.toDegrees(FastMath.atan2(head.getAbsY()-bernardEyesY, head.getAbsZ()));
		Float neckXAngle = (float) Math.toDegrees(FastMath.atan2(head.getAbsX(),head.getAbsZ()));
		if(bernard.head != null) {
			bernard.head.neck.moveTo(neckYAngle+bernard.head.neck.getRest());
			bernard.head.rothead.moveTo(neckXAngle+bernard.head.rothead.getRest());
		}
	}
	
	public void rollEyes() {
		//Gesture RollEye = new Gesture();
		double startX = bernard.head.eyeX.getPos();
		double startY = bernard.head.eyeY.getPos();
		bernard.head.eyeY.moveTo(bernard.head.eyeY.getMax());
		bernard.head.eyeX.moveTo(bernard.head.eyeX.getMax());
		bernard.head.eyeY.moveTo(bernard.head.eyeY.getMin());
		bernard.head.eyeY.moveTo(bernard.head.eyeX.getMin());
		bernard.head.eyeY.moveTo(bernard.head.eyeY.getMax());
		bernard.head.eyeY.moveTo(startX);
		bernard.head.eyeY.moveTo(startY);
	}
	
	public void playGesture(String ref) throws Exception {
		if(currentGesture == null){
			currentGesture = new Gesture();
		}
		if(ref == null) {
			ref = "default";
		}
		currentGesture.load(ref);
		long startTime = System.currentTimeMillis();
		for(int i=0;i<currentGesture.duration;i++) {
			this.kSkeleton = currentGesture.skeletonList.poll();
			Thread.sleep(50);
			mapSkeletonToRobot();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("That took " + (endTime - startTime) + " milliseconds");
		bernard.restAll();
	}
	
	public void recordGesture(String name) {
		recordingGesture = true;
		if(currentGesture == null) {
			currentGesture = new Gesture();
			currentGesture.setLength(gestureLength);	
		}
		currentGesture.GestureName = name;
			
	}
	
	public Boolean findGesture(String name){
		if(currentGesture == null) {
			currentGesture = new Gesture();
			currentGesture.setLength(gestureLength);
			}
		return currentGesture.findGesture(name);
	}
	
	@Override
	public void update(Observable subject, Object arg) {
		if(arg instanceof KSkeleton) {
			if(recordingGesture) {
				robotImitation = true;
				if(currentGesture == null) {
					currentGesture = new Gesture();
					currentGesture.setLength(gestureLength);
					currentGesture.GestureName = "default";
				}
				if(!currentGesture.finished) {
					currentGesture.record((KSkeleton)arg);
				} else {
					currentGesture = null;
					recordingGesture = false;
					robotImitation = false;
					bernard.restAll();
				}
			}
			if(robotImitation) {
				this.kSkeleton = ((KSkeleton)arg);
				mapSkeletonToRobot();
			}
			if(userFacing) {
				this.kSkeleton = ((KSkeleton)arg);
				faceUser();
			}
			
		}

	}
	
}