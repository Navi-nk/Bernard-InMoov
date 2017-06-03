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
import com.jme3.math.Vector2f;

import edu.ufl.digitalworlds.math.Geom;
//import edu.ufl.digitalworlds.j4k.Skeleton;
import org.myrobotlab.j4kdemo.KSkeleton;
import org.myrobotlab.j4kdemo.Joint;
import org.myrobotlab.service.J4K;
import org.myrobotlab.service.InMoov;
import org.myrobotlab.service.VirtualArduino;
//import org.python.modules.math;
import org.myrobotlab.service.ProgramAB;
import org.myrobotlab.service.NaturalReaderSpeech;

public class Bernard extends Service implements Observer {

	private static final long serialVersionUID = 1L;
	
	// MRL Services
	public J4K KinectViewer;
	public InMoov i01;
	public VirtualArduino v1;
	public VirtualArduino v2;
	public ProgramAB AliceBot;
	public NaturalReaderSpeech i01mouth;
	
	// Skeletal Mapping Variables
	public KSkeleton kSkeleton;
	public boolean robotImitation = false;
	public boolean vinMoovStarted=false;
	public boolean trackUserFace=false;
	//public mapping coordinates for virtual bernard
	
	
	// InMoov Variables Initialisation
	public String leftPort = "COM20";
	public String rightPort = "COM91";
	public String headPort = leftPort;
	public String voiceType = "Ryan";
	public String lang = "EN";
	public String ProgramABPath = "C:\\Users\\Joycob\\git\\myrobotlab\\ProgramAB";

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
	    meta.addPeer("i01", "InMoov", "InMoov");
	    meta.addPeer("v1", "VirtualArduino", "Virtual Arduino 1: Left");
	    meta.addPeer("v2", "VirtualArduino", "Virtual Arduino 2: Right");
	    meta.addPeer("AliceBot", "ProgramAB", "Alice Chatbot");
	    meta.addPeer("i01.mouth", "NaturalReaderSpeech", "Speech Control");
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
		KinectViewer = (J4K) startPeer("KinectViewer");
	}
	
	public void startRobotImitation() {
		robotImitation = true; 
		startKinectViewer();
		//System.out.println("startRobotImitation");
		//if(method=="angular") {
			//offset for kinect is 0,0,220
		//}
	}
	
	public void addKinectObservers() {
		this.KinectViewer.sub.addObserver(this);
		System.out.println("Current Kinect Observers: "+String.valueOf(this.KinectViewer.sub.countObservers()));
	}
		
	public void startInMoov() {
		// Actual InMoov
	}
	
	public void startVinMoov() {
		try {
			i01 = (InMoov) startPeer("i01");
			//vinMoov = i01;
			v1 = (VirtualArduino) startPeer("v1");
			//VirtualArduino va1 = v1;
			v2 = (VirtualArduino) startPeer("v2");
			//VirtualArduino va2 = v2;
			v1.connect(leftPort);
			v2.connect(rightPort);
			i01.startAll(leftPort, rightPort);
			
			AliceBot = (ProgramAB) startPeer("AliceBot");
			//ProgramAB alice = AliceBot;
			AliceBot.setPath(ProgramABPath);
			i01mouth = (NaturalReaderSpeech) startPeer("i01.mouth");
			i01mouth.setVoice(voiceType);
			i01mouth.setLanguage(lang);
			
			i01.startVinMoov();
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
		
		double[] torsoOrientation = kSkeleton.getTorsoOrientation();
		double t[] = Geom.identity4();
		double inv_t[] = Geom.identity4();
		//System.out.println(t.equals(inv_t));
		//double torsoTransform = kSkeleton.getTorsoTransform(t, inv_t);
		//double headTransform = kSkeleton.getHeadTransform(t, inv_t);
		//double fixHeadTransform = kSkeleton.getFixHeadTransform(t, inv_t);
		//double shoulderTransform = kSkeleton.getShoulderTransform(t, inv_t);
		//double betweenHandsTransform = kSkeleton.getBetweenHandsTransform(t, inv_t);
		//double rightArmTransform = kSkeleton.getRightArmTransform(t, inv_t);
		//double rightForearmTransform = kSkeleton.getRightArmTransform(t, inv_t);
		double leftArmTransform = kSkeleton.getLeftArmTransform(t, inv_t);
		double leftForearmTransform = kSkeleton.getLeftArmTransform(t, inv_t);
		//System.out.println(leftArmTransform);
		
		Joint spineShoulderJoint = kSkeleton.getJoint(KSkeleton.SPINE_SHOULDER);
		
		// Head
		if(trackUserFace != true) {
			//System.out.println(90+jointOrientation[4*Skeleton.HEAD+2]*240);
			//i01.head.rothead.moveTo(90-jointOrientation[4*Skeleton.NECK+3]*120);
			//i01.head.neck.moveTo(90+jointOrientation[12+2]*240);
			//i01.head.rollNeck.moveTo(90+);
		}
		
		// Left Arm 
		Joint shoulderLeftJoint = kSkeleton.getJoint(KSkeleton.SHOULDER_LEFT);
		Joint elbowLeftJoint = kSkeleton.getJoint(KSkeleton.ELBOW_LEFT);
		Joint wristLeftJoint = kSkeleton.getJoint(KSkeleton.WRIST_LEFT);
		Joint handLeftJoint = kSkeleton.getJoint(KSkeleton.HAND_LEFT);
		Joint handTipLeftJoint = kSkeleton.getJoint(KSkeleton.HAND_TIP_LEFT);
		
		// leftArmTransform and leftForearmTransform
		
		Float shoulderElbowDistanceLeft = FastMath.sqrt(FastMath.pow(shoulderLeftJoint.getAbsX()-elbowLeftJoint.getAbsX(),2)+FastMath.pow(shoulderLeftJoint.getAbsY()-elbowLeftJoint.getAbsY(),2)+FastMath.pow(shoulderLeftJoint.getAbsZ()-elbowLeftJoint.getAbsZ(),2));
		//System.out.println(shoulderElbowDistanceLeft);
		Float elbowWristDistanceLeft = FastMath.sqrt(FastMath.pow(elbowLeftJoint.getAbsX()-wristLeftJoint.getAbsX(),2)+FastMath.pow(elbowLeftJoint.getAbsY()-wristLeftJoint.getAbsY(),2)+FastMath.pow(elbowLeftJoint.getAbsZ()-wristLeftJoint.getAbsZ(),2));
		
		Quaternion elbowLeftQ = elbowLeftJoint.getAbsOrientation();
		float[] elbowLeftAngle = elbowLeftQ.toAngles(null);
		Quaternion wristLeftQ = wristLeftJoint.getAbsOrientation();
		float[] wristLeftAngle = wristLeftQ.toAngles(null);
		Quaternion handLeftQ = handLeftJoint.getAbsOrientation();
		float[] handLeftAngle = handLeftQ.toAngles(null);
		
		Vector3f normal = new Vector3f(1,0,0);
		//Vector3f wristLeftNormal = elbowLeftQ.inverse().multLocal(wristLeftQ).mult(normal);
		Vector3f handLeftNormal = wristLeftQ.multLocal(handLeftQ).mult(normal);
		//Vector3f wristLeftNormal = wristLeftJoint.getNormal();
		Float wristLeftRotateAngle = normal.angleBetween(handLeftNormal);
		System.out.println(Math.toDegrees(wristLeftRotateAngle));
		
		
		//Quaternion handLeftQ = handLeftJoint.getAbsOrientation();
		//float[] handLeftAngle = handLeftQ.toAngles(null);
		
		Vector3f spineShoulderVec = spineShoulderJoint.getAbsPosition();
		Vector3f shoulderLeftVec = shoulderLeftJoint.getAbsPosition();
		Vector3f elbowLeftVec = elbowLeftJoint.getAbsPosition();
		Vector3f wristLeftVec = wristLeftJoint.getAbsPosition();
		Vector3f handLeftVec = handLeftJoint.getAbsPosition();
		Vector3f handTipLeftVec = handTipLeftJoint.getAbsPosition();
		
		Vector3f spineShoulderLeftVec = shoulderLeftVec.subtract(spineShoulderVec);
		Vector3f shoulderElbowLeftVec = elbowLeftVec.subtract(shoulderLeftVec);
		//Vector3f spineElbowLeftVec = shoulderLeftVec.add(shoulderElbowLeftVec);
		Vector3f elbowWristLeftVec = wristLeftVec.subtract(elbowLeftVec);
		Vector3f handHandTipLeftVec = handTipLeftVec.subtract(handLeftVec);
		spineShoulderLeftVec = spineShoulderLeftVec.normalizeLocal();
		shoulderElbowLeftVec = shoulderElbowLeftVec.normalizeLocal();
		elbowWristLeftVec = elbowWristLeftVec.normalizeLocal();
		handHandTipLeftVec = handHandTipLeftVec.normalizeLocal();
		
		//float degrees = Math.round(Math.toDegrees(FastMath.atan2(shoulderElbowLeftVec.getY(),shoulderElbowLeftVec.getX())));
		//i01.leftArm.omoplate.moveTo(degrees>90?270-degrees:-degrees-90);
		
		//i01.leftArm.omoplate.moveTo(Math.toDegrees(Math.asin(Math.abs(shoulderLeftJoint[0]-elbowLeftJoint[0])/shoulderElbowDistanceLeft)));
		float degrees = Math.round(Math.toDegrees(FastMath.atan2(shoulderElbowLeftVec.getZ(),shoulderElbowLeftVec.getY())));
		//i01.leftArm.shoulder.moveTo(30+(degrees>0?degrees-180:degrees+180));
		degrees = (float) Math.toDegrees(elbowLeftAngle[1]);
		//i01.leftArm.rotate.moveTo(Math.round(((degrees<-90)?360+degrees:degrees)+torsoOrientation[0]*90));
		//i01.leftArm.bicep.moveTo(Math.round(Math.toDegrees(elbowWristLeftVec.angleBetween(shoulderElbowLeftVec))));
		
		// for wrist, need to use all three rotations
		
		degrees = (float) Math.toDegrees(wristLeftRotateAngle);
		//i01.leftHand.wrist.moveTo(degrees);
				
		// Right Arm
		Joint shoulderRightJoint = kSkeleton.getJoint(KSkeleton.SHOULDER_RIGHT);
		Joint elbowRightJoint = kSkeleton.getJoint(KSkeleton.ELBOW_RIGHT);
		Joint wristRightJoint = kSkeleton.getJoint(KSkeleton.WRIST_RIGHT);
		Joint handRightJoint = kSkeleton.getJoint(KSkeleton.HAND_RIGHT);
		Joint handTipRightJoint = kSkeleton.getJoint(KSkeleton.HAND_TIP_LEFT);
		Float shoulderElbowDistanceRight = FastMath.sqrt(FastMath.pow(shoulderRightJoint.getAbsX()-elbowRightJoint.getAbsX(),2)+FastMath.pow(shoulderRightJoint.getAbsY()-elbowRightJoint.getAbsY(),2)+FastMath.pow(shoulderRightJoint.getAbsZ()-elbowRightJoint.getAbsZ(),2));
		Float elbowWristDistanceRight = FastMath.sqrt(FastMath.pow(elbowRightJoint.getAbsX()-wristRightJoint.getAbsX(),2)+FastMath.pow(elbowRightJoint.getAbsY()-wristRightJoint.getAbsY(),2)+FastMath.pow(elbowRightJoint.getAbsZ()-wristRightJoint.getAbsZ(),2));
		
		Quaternion shoulderRightQ = shoulderRightJoint.getAbsOrientation();
		Quaternion elbowRightQ = elbowRightJoint.getAbsOrientation();
		float[] elbowRightAngle = elbowRightQ.toAngles(null);
		Quaternion wristRightQ = wristRightJoint.getAbsOrientation();
		float[] wristRightAngle = wristRightQ.toAngles(null);
		Quaternion wristRightRelativeQ = elbowRightQ.inverseLocal().mult(wristRightQ);
		float[] wristRightRotateAngle = wristRightRelativeQ.toAngles(null);
		Quaternion handRightQ = handRightJoint.getAbsOrientation();
		float[] handRightAngle = handRightQ.toAngles(null);
		
		
		Vector3f shoulderRightVec = shoulderRightJoint.getAbsPosition();
		Vector3f elbowRightVec = elbowRightJoint.getAbsPosition();
		Vector3f wristRightVec = wristRightJoint.getAbsPosition();
		Vector3f handRightVec = handRightJoint.getAbsPosition();
		Vector3f handTipRightVec = handTipRightJoint.getAbsPosition();
		
		Vector3f spineShoulderRightVec = shoulderRightVec.subtract(spineShoulderVec);
		Vector3f shoulderElbowRightVec = elbowRightVec.subtract(shoulderRightVec);
		//Vector3f spineElbowLeftVec = shoulderLeftVec.add(shoulderElbowLeftVec);
		Vector3f elbowWristRightVec = wristRightVec.subtract(elbowRightVec);
		Vector3f handHandTipRightVec = handTipRightVec.subtract(handRightVec);
		spineShoulderRightVec = spineShoulderRightVec.normalizeLocal();
		shoulderElbowRightVec = shoulderElbowRightVec.normalizeLocal();
		elbowWristRightVec = elbowWristRightVec.normalizeLocal();
		handHandTipRightVec = handHandTipRightVec.normalizeLocal();
		
		
		
		//i01.rightArm.omoplate.moveTo(10+Math.toDegrees(Math.asin(Math.abs(shoulderRightJoint[0]-elbowRightJoint[0])/shoulderElbowDistanceRight)));
		degrees = Math.round(Math.toDegrees(FastMath.atan2(shoulderElbowRightVec.getZ(),shoulderElbowRightVec.getY())));
		//i01.rightArm.shoulder.moveTo(30+(degrees>0?degrees-180:degrees+180));
		
		//System.out.println(degrees);
		
		//degrees = (float) -Math.toDegrees(elbowRightAngle[1]);
		//i01.rightArm.rotate.moveTo(Math.round(((degrees<-90)?360+degrees:degrees)-torsoOrientation[0]*90));
		//i01.rightArm.bicep.moveTo(Math.round(Math.toDegrees(elbowWristRightVec.angleBetween(shoulderElbowRightVec))));
		//degrees = (float) Math.toDegrees(wristRightAngle[1]);
		//i01.rightHand.wrist.moveTo(90-degrees);
		
		
		
		// Body
		
		// Torso Upper
		Float shoulderDistance = FastMath.sqrt(FastMath.pow(shoulderLeftJoint.getAbsX()-shoulderRightJoint.getAbsX(),2)+FastMath.pow(shoulderLeftJoint.getAbsY()-shoulderRightJoint.getAbsY(),2)+FastMath.pow(shoulderLeftJoint.getAbsZ()-shoulderRightJoint.getAbsZ(),2));
		//i01.torso.topStom.moveTo(90+Math.toDegrees(Math.asin(Math.abs(shoulderLeftJoint[1]-shoulderRightJoint[1])/shoulderDistance)));
		// Torso Mid
		
		//i01.torso.midStom.moveTo(90+torsoOrientation[0]*90);
		// Torso Low
		//i01.torso.lowStom.moveTo(90+torsoOrientation[1]*90);
		//double leftForeArmOrientation = kSkeleton.getLeftForearmTransform(transf, inv_transf);
		//double rightForeArmOrientation = kSkeleton.getRightForearmTransform(transf, inv_transf);
		//double leftArmOrientation = kSkeleton.getLeftArmTransform(transf, inv_transf);
		//double rightArmOrientation = kSkeleton.getRightArmTransform(transf, inv_transf);
		
	}
	
	public void startFaceTracking() {
		System.out.println("Hello");
	}
	
	
	@Override
	public void update(Observable subject, Object arg) {
		if(arg instanceof KSkeleton) {
			//if(robotImitation && vinMoovStarted) {
			if(robotImitation) {
				this.kSkeleton = ((KSkeleton)arg);
				mapSkeletonToRobot();
			}
		}

	}
	
}