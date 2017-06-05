package org.myrobotlab.swing;

/* Adapted from:
 * 
 * Copyright 2011-2014, Digital Worlds Institute, University of 
 * Florida, Angelos Barmpoutis.
 * All rights reserved.
 *
 * When this program is used for academic or research purposes, 
 * please cite the following article that introduced this Java library: 
 * 
 * A. Barmpoutis. "Tensor Body: Real-time Reconstruction of the Human Body 
 * and Avatar Synthesis from RGB-D', IEEE Transactions on Cybernetics, 
 * October 2013, Vol. 43(5), Pages: 1347-1356. 
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *     * Redistributions of source code must retain this copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce this
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.myrobotlab.j4kdemo.kinectviewerapp.Kinect;
import org.myrobotlab.j4kdemo.kinectviewerapp.ViewerPanel3D;
import org.myrobotlab.j4kdemo.JointFilter;
import edu.ufl.digitalworlds.j4k.Skeleton;
import org.myrobotlab.j4kdemo.KSkeleton;
import com.jme3.math.Vector3f;
import com.jme3.math.Quaternion;

import edu.ufl.digitalworlds.j4k.J4K1;
import edu.ufl.digitalworlds.j4k.J4K2;
import edu.ufl.digitalworlds.j4k.J4KSDK;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.service.SwingGui;
import org.myrobotlab.service.J4K;
import org.myrobotlab.service.Runtime;
import org.slf4j.Logger;


public class J4KGui extends ServiceGui implements ActionListener, ChangeListener, Observer {
	
	static final long serialVersionUID = 1L;
	public final static Logger log = LoggerFactory.getLogger(J4KGui.class);
	DecimalFormat df = new DecimalFormat("#.###");
	
	public Kinect myKinect = new Kinect();
	public ViewerPanel3D mainPanel = new ViewerPanel3D();
	JSlider elevation_angle = new JSlider();
	JCheckBox near_mode = new JCheckBox("Near Mode");
	JCheckBox seated_skeleton = new JCheckBox("Seated Skeleton");
	JCheckBox show_infrared = new JCheckBox("Infrared");
	JButton turn_off = new JButton("Turn Off");
	JComboBox depth_resolution = new JComboBox();
	JComboBox video_resolution = new JComboBox();
	
	JCheckBox show_video = new JCheckBox("Show Texture");
	JCheckBox mask_players = new JCheckBox("Mask Players");
	JLabel accelerometer = new JLabel("0,0,0");
	
	//JPanel eastPanel = new JPanel();
	JTabbedPane eastPanel = new JTabbedPane();
	JPanel coordPanel = new JPanel();

	JPanel filterPanel = new JPanel();
	JComboBox joint_filter = new JComboBox();
	JSlider frame_history = new JSlider();
	JSlider smoothing = new JSlider();
	JSlider correction = new JSlider();
	JSlider prediction = new JSlider();
	JSlider jitter_radius = new JSlider();
	JSlider max_deviation_radius = new JSlider();
	
	public KSkeleton[] skeletonData;
	
	JCheckBox joint_orientation = new JCheckBox("Joint Orientation");
	JLabel spine_base = new JLabel("0,0,0");
	JLabel spine_mid = new JLabel("0,0,0");
	JLabel neck = new JLabel("0,0,0");
	JLabel head = new JLabel("0,0,0");
	JLabel shoulder_left = new JLabel("0,0,0");
	JLabel elbow_left = new JLabel("0,0,0");
	JLabel wrist_left = new JLabel("0,0,0");
	JLabel hand_left = new JLabel("0,0,0");
	JLabel shoulder_right = new JLabel("0,0,0");
	JLabel elbow_right = new JLabel("0,0,0");
	JLabel wrist_right = new JLabel("0,0,0");
	JLabel hand_right = new JLabel("0,0,0");
	JLabel hip_left = new JLabel("0,0,0");
	JLabel knee_left = new JLabel("0,0,0");
	JLabel ankle_left = new JLabel("0,0,0");
	JLabel foot_left = new JLabel("0,0,0");
	JLabel hip_right = new JLabel("0,0,0");
	JLabel knee_right = new JLabel("0,0,0");
	JLabel ankle_right = new JLabel("0,0,0");
	JLabel foot_right = new JLabel("0,0,0");
	JLabel spine_shoulder = new JLabel("0,0,0");
	JLabel hand_tip_left = new JLabel("0,0,0");
	JLabel thumb_left = new JLabel("0,0,0");
	JLabel hand_tip_right = new JLabel("0,0,0");
	JLabel thumb_right = new JLabel("0,0,0");
	JLabel body_orientation = new JLabel("0,0,0");
	JLabel torso_orientation = new JLabel("0,0,0");
	JLabel[] skeleton_labels = {spine_base,spine_mid,neck,head,shoulder_left,elbow_left,wrist_left,hand_left,shoulder_right,
								elbow_right,wrist_right,hand_right,hip_left,knee_left,ankle_left,foot_left,hip_right,knee_right,
								ankle_right,foot_right,spine_shoulder,hand_tip_left,thumb_left,hand_tip_right,thumb_right,body_orientation,torso_orientation};
	
	JPanel controls = new JPanel();
	
	public J4KGui(final String boundServiceName, final SwingGui myService) {

	    super(boundServiceName, myService);
	    J4K boundService = (J4K) Runtime.getService(boundServiceName);
	    
	    if(!myKinect.start(Kinect.DEPTH| Kinect.COLOR |Kinect.SKELETON |Kinect.XYZ|Kinect.PLAYER_INDEX))
		{
	    	log.info("ERROR: The Kinect device could not be initialized. Check if the Microsoft's Kinect SDK was succesfully installed on this computer or check if the Kinect is plugged into a power outlet. Otherwise, check if the Kinect is connected to a USB port of this computer.");
		}
	   
	    near_mode.setSelected(false);
		near_mode.addActionListener(this);
		if(myKinect.getDeviceType()!=J4KSDK.MICROSOFT_KINECT_1) near_mode.setEnabled(false);
		
		seated_skeleton.addActionListener(this);
		if(myKinect.getDeviceType()!=J4KSDK.MICROSOFT_KINECT_1) seated_skeleton.setEnabled(false);
		
		// Elevation Angle
		elevation_angle.setMinimum(-27);
		elevation_angle.setMaximum(27);
		elevation_angle.setValue((int)myKinect.getElevationAngle());
		elevation_angle.setToolTipText("Elevation Angle ("+elevation_angle.getValue()+" degrees)");
		elevation_angle.addChangeListener(this);
		
		turn_off.addActionListener(this);
		
		// Add Depth Resolution
		if(myKinect.getDeviceType()==J4KSDK.MICROSOFT_KINECT_1) {
			depth_resolution.addItem("80x60");
			depth_resolution.addItem("320x240");
			depth_resolution.addItem("640x480");
			depth_resolution.setSelectedIndex(1);
		} else if(myKinect.getDeviceType()==J4KSDK.MICROSOFT_KINECT_2) {
			depth_resolution.addItem("512x424");
			depth_resolution.setSelectedIndex(0);
		}
		depth_resolution.addActionListener(this);
		
		// Add Video Resolution
		if(myKinect.getDeviceType()==J4KSDK.MICROSOFT_KINECT_1)
		{
			video_resolution.addItem("640x480");
			video_resolution.addItem("1280x960");
			video_resolution.setSelectedIndex(0);
		}
		else if(myKinect.getDeviceType()==J4KSDK.MICROSOFT_KINECT_2)
		{
			video_resolution.addItem("1920x1080");
			video_resolution.setSelectedIndex(0);
		}
		video_resolution.addActionListener(this);

		//Infrared
		show_infrared.setSelected(false);
		show_infrared.addActionListener(this);
		
		//Show Texture
		show_video.setSelected(false);
		show_video.addActionListener(this);
		
		//Mask Players
		mask_players.setSelected(false);
		mask_players.addActionListener(this);
		
		//Show Joint Orientation
		joint_orientation.setSelected(false);
		joint_orientation.addActionListener(this);
		
		//Joint Filtering
		joint_filter.addItem("None");
		joint_filter.addItem("ExponentialSmoothing1");
		joint_filter.addItem("ExponentialSmoothing2");
		joint_filter.addItem("HoltDoubleExponential");
		joint_filter.addActionListener(this);
		joint_filter.setSelectedIndex(0);
		
		//Frame Slider
		frame_history.setMinimum(1);
		frame_history.setMaximum(15);
		frame_history.setValue(5);
		frame_history.setToolTipText("History ("+String.valueOf(frame_history.getValue())+" frames)");
		frame_history.addChangeListener(this);
		
		//Smoothing Slider
		smoothing.setMinimum(0);
		smoothing.setMaximum(100);
		smoothing.setValue(90);
		smoothing.setToolTipText("Smoothing ("+String.valueOf((float)smoothing.getValue()/100.0f)+")");
		smoothing.addChangeListener(this);
		
		//Correction Slider
		correction.setMinimum(0);
		correction.setMaximum(100);
		correction.setValue(50);
		correction.setToolTipText("Correction ("+String.valueOf((float)correction.getValue()/100.0f)+")");
		correction.addChangeListener(this);
		
		//Prediction Slider
		prediction.setMinimum(0);
		prediction.setMaximum(100);
		prediction.setValue(0);
		prediction.setToolTipText("Prediction ("+String.valueOf((float)prediction.getValue()/100.0f)+")");
		prediction.addChangeListener(this);
		
		//Jitter Radius Slider
		jitter_radius.setMinimum(0);
		jitter_radius.setMaximum(10);
		jitter_radius.setValue(50);
		jitter_radius.setToolTipText("Jitter Radius ("+String.valueOf((float)jitter_radius.getValue()/50.0f)+")");
		jitter_radius.addChangeListener(this);
		
		//Max Deviation Radius Slider
		max_deviation_radius.setMinimum(0);
		max_deviation_radius.setMaximum(8);
		max_deviation_radius.setValue(50);
		max_deviation_radius.setToolTipText("Max Deviation Radius ("+String.valueOf((float)max_deviation_radius.getValue()/50.0f)+")");
		max_deviation_radius.addChangeListener(this);
		
		controls.setLayout(new GridLayout(0, 6));
		controls.add(new JLabel("Depth Stream:"));
		controls.add(depth_resolution);
		controls.add(mask_players);
		controls.add(near_mode);
		controls.add(seated_skeleton);
		controls.add(accelerometer);
		controls.add(new JLabel("Texture Stream:"));
		controls.add(video_resolution);
		controls.add(show_infrared);
		controls.add(show_video);
		controls.add(elevation_angle);
		controls.add(turn_off);
		
		mainPanel.setShowVideo(false);
		myKinect.setViewer(mainPanel);
		myKinect.setLabel(accelerometer);
		
		myKinect.subject.addObserver(this);
		send("updateSkeletonSubject", myKinect.subject);
		
		eastPanel.setPreferredSize(new Dimension(300,0));
		eastPanel.addTab("Joint Coordinates", coordPanel);
		eastPanel.addTab("Joint Filter", filterPanel);
		
		filterPanel.setLayout(new GridLayout(14,1));
		filterPanel.add(new JLabel("Joint Filter: "));
		filterPanel.add(joint_filter);
		filterPanel.add(new JLabel("Frames: "));
		filterPanel.add(frame_history);
		filterPanel.add(new JLabel("Smoothing: "));
		filterPanel.add(smoothing);
		filterPanel.add(new JLabel("Correction: "));
		filterPanel.add(correction);
		filterPanel.add(new JLabel("Prediction: "));
		filterPanel.add(prediction);
		filterPanel.add(new JLabel("Jitter Radius: "));
		filterPanel.add(jitter_radius);
		filterPanel.add(new JLabel("Max Deviation Radius: "));
		filterPanel.add(max_deviation_radius);
		
		coordPanel.setLayout(new GridLayout(31,1));
		coordPanel.add(new JLabel("Joint Positions X,Y,Z"));
		coordPanel.add(joint_orientation);
		coordPanel.add(new JLabel("Spine Base:"));
		coordPanel.add(spine_base);
		coordPanel.add(new JLabel("Spine Mid:"));
		coordPanel.add(spine_mid);
		coordPanel.add(new JLabel("Neck:"));
		coordPanel.add(neck);
		coordPanel.add(new JLabel("Head:"));
		coordPanel.add(head);
		coordPanel.add(new JLabel("Shoulder Left:"));
		coordPanel.add(shoulder_left);
		coordPanel.add(new JLabel("Elbow Left:"));
		coordPanel.add(elbow_left);
		coordPanel.add(new JLabel("Wrist Left:"));
		coordPanel.add(wrist_left);
		coordPanel.add(new JLabel("Hand Left:"));
		coordPanel.add(hand_left);
		coordPanel.add(new JLabel("Shoulder Right:"));
		coordPanel.add(shoulder_right);
		coordPanel.add(new JLabel("Elbow Right:"));
		coordPanel.add(elbow_right);
		coordPanel.add(new JLabel("Wrist Right:"));
		coordPanel.add(wrist_right);
		coordPanel.add(new JLabel("Hand Right:"));
		coordPanel.add(hand_right);
		coordPanel.add(new JLabel("Hip Left:"));
		coordPanel.add(hip_left);
		coordPanel.add(new JLabel("Knee Left:"));
		coordPanel.add(knee_left);
		coordPanel.add(new JLabel("Ankle Left:"));
		coordPanel.add(ankle_left);
		coordPanel.add(new JLabel("Foot Left:"));
		coordPanel.add(foot_left);
		coordPanel.add(new JLabel("Hip Right:"));
		coordPanel.add(hip_right);
		coordPanel.add(new JLabel("Knee Right:"));
		coordPanel.add(knee_right);
		coordPanel.add(new JLabel("Ankle Right:"));
		coordPanel.add(ankle_right);
		coordPanel.add(new JLabel("Foot Right:"));
		coordPanel.add(foot_right);
		coordPanel.add(new JLabel("Spine Shoulder:"));
		coordPanel.add(spine_shoulder);
		coordPanel.add(new JLabel("Hand Tip Left:"));
		coordPanel.add(hand_tip_left);
		coordPanel.add(new JLabel("Thumb Left:"));
		coordPanel.add(thumb_left);
		coordPanel.add(new JLabel("Hand Tip Right:"));
		coordPanel.add(hand_tip_right);
		coordPanel.add(new JLabel("Thumb Right:"));
		coordPanel.add(thumb_right);
		coordPanel.add(new JLabel("Body Orientation:"));
		coordPanel.add(body_orientation);
		coordPanel.add(new JLabel("Torso Orientation:"));
		coordPanel.add(torso_orientation);
		
		display.setLayout(new BorderLayout());		
		display.add(mainPanel, BorderLayout.CENTER);
		display.add(controls, BorderLayout.SOUTH);
		display.add(eastPanel, BorderLayout.EAST);
		
	}
	  
	@Override
	public void update(Observable subject, Object arg) {
		
		if(arg instanceof KSkeleton) {
			send("getSkeleton",arg);
		}
		
		// Update Gui with Skeleton Joint Positions and Orientations
		if(arg instanceof KSkeleton && !joint_orientation.isSelected()) {
			
			for(int i=0;i<KSkeleton.JOINT_COUNT;i++) {
				Vector3f joint_data = ((KSkeleton) arg).getJoint(i).getAbsPosition();
				skeleton_labels[i].setText(String.valueOf(df.format(joint_data.getX()))+","+String.valueOf(df.format(joint_data.getY()))+","+String.valueOf(df.format(joint_data.getZ())));
			}
			
		} else if(arg instanceof KSkeleton && joint_orientation.isSelected()) {
			
			for(int i=0;i<KSkeleton.JOINT_COUNT;i++) {
				Quaternion joint_data = ((KSkeleton) arg).getJoint(i).getAbsOrientation();
				skeleton_labels[i].setText(String.valueOf(df.format(joint_data.getX()))+","+String.valueOf(df.format(joint_data.getY()))+","+String.valueOf(df.format(joint_data.getZ()))+","+String.valueOf(df.format(joint_data.getW())));
			}
			
			//float jointOrientation[] = ((KSkeleton)arg).getJointOrientations();
			double bodyOrientation = ((KSkeleton)arg).getBodyOrientation();
			double torsoOrientation[] = ((KSkeleton)arg).getTorsoOrientation();
			body_orientation.setText(String.valueOf(df.format(bodyOrientation)));
			torso_orientation.setText(String.valueOf(df.format(torsoOrientation[0]))+","+String.valueOf(df.format(torsoOrientation[1]))+","+String.valueOf(df.format(torsoOrientation[2])));
		
		}
	}
	
	private void resetKinect() {
		if(turn_off.getText().compareTo("Turn on")==0) return;
		
		myKinect.stop();
		int depth_res=J4K1.NUI_IMAGE_RESOLUTION_INVALID;
		if(depth_resolution.getSelectedIndex()==0) myKinect.setDepthResolution(80, 60);//  depth_res=J4K1.NUI_IMAGE_RESOLUTION_80x60;
		else if(depth_resolution.getSelectedIndex()==1) myKinect.setDepthResolution(320, 240);//depth_res=J4K1.NUI_IMAGE_RESOLUTION_320x240;
		else if(depth_resolution.getSelectedIndex()==2) myKinect.setDepthResolution(640, 480);//depth_res=J4K1.NUI_IMAGE_RESOLUTION_640x480;
		
		int video_res=J4K1.NUI_IMAGE_RESOLUTION_INVALID;
		if(video_resolution.getSelectedIndex()==0) myKinect.setColorResolution(640, 480);//video_res=J4K1.NUI_IMAGE_RESOLUTION_640x480;
		else if(video_resolution.getSelectedIndex()==1) myKinect.setDepthResolution(1280, 960);//video_res=J4K1.NUI_IMAGE_RESOLUTION_1280x960;
		
		int flags=Kinect.SKELETON;
		flags=flags|Kinect.COLOR;
		flags=flags|Kinect.DEPTH;
		flags=flags|Kinect.XYZ;
		if(show_infrared.isSelected()) {flags=flags|Kinect.INFRARED; myKinect.updateTextureUsingInfrared(true);}
		else myKinect.updateTextureUsingInfrared(false);
			
		myKinect.start(flags);
		if(show_video.isSelected())myKinect.computeUV(true);
		else myKinect.computeUV(false);
		if(seated_skeleton.isSelected())myKinect.setSeatedSkeletonTracking(true);
		if(near_mode.isSelected()) myKinect.setNearMode(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==near_mode)
		{
			if(near_mode.isSelected()) myKinect.setNearMode(true);
			else myKinect.setNearMode(false);
		}
		else if(e.getSource()==seated_skeleton)
		{
			if(seated_skeleton.isSelected()) myKinect.setSeatedSkeletonTracking(true);
			else myKinect.setSeatedSkeletonTracking(false);
		}
		else if(e.getSource()==show_infrared)
		{
			resetKinect();
		}
		else if(e.getSource()==turn_off)
		{
			if(turn_off.getText().compareTo("Turn Off")==0)
			{
				myKinect.stop();
				turn_off.setText("Turn On");
			}
			else
			{
				turn_off.setText("Turn Off");
				resetKinect();
			}
		}
		else if(e.getSource()==depth_resolution)
		{
			resetKinect();
		}
		else if(e.getSource()==video_resolution)
		{
			resetKinect();
		}
		else if(e.getSource()==show_video)
		{
			mainPanel.setShowVideo(show_video.isSelected());
			if(show_video.isSelected()) myKinect.computeUV(true);
			else myKinect.computeUV(false);
		}
		else if(e.getSource()==mask_players)
		{
			myKinect.maskPlayers(mask_players.isSelected());
		} 
		else if(e.getSource()==joint_filter)
		{
			myKinect.jf.setFilterMethod(joint_filter.getSelectedItem().toString());
			
			if(joint_filter.getSelectedIndex()==2 || joint_filter.getSelectedIndex()==3) {
				myKinect.jf.setFrameHistory(1);
				frame_history.setValue(1);
				myKinect.jf.elapsedFrames = 0;
			} else if(joint_filter.getSelectedIndex()!=0) {
				
				myKinect.jf.setFrameHistory((int)frame_history.getValue());
			}
		} 
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		if(e.getSource()==elevation_angle)
		{
			if(!elevation_angle.getValueIsAdjusting())
			{
				myKinect.setElevationAngle(elevation_angle.getValue());
				elevation_angle.setToolTipText("Elevation Angle ("+elevation_angle.getValue()+" degrees)");
			}
		} 
		else if(e.getSource()==frame_history) 
		{
			myKinect.jf.setFrameHistory((int)frame_history.getValue());
			frame_history.setToolTipText("History ("+frame_history.getValue()+" frames)");
		}
		else if(e.getSource()==smoothing) 
		{
			myKinect.jf.setSmoothing((float)smoothing.getValue()/100.0f);
			smoothing.setToolTipText("Smoothing ("+String.valueOf((float)smoothing.getValue()/100.0f)+")");
		}
		else if(e.getSource()==correction) 
		{
			myKinect.jf.setCorrection((float)correction.getValue()/100.0f);
			correction.setToolTipText("Correction ("+String.valueOf((float)correction.getValue()/100.0f)+")");
		}
		else if(e.getSource()==prediction) 
		{
			myKinect.jf.setPrediction((float)prediction.getValue()/100.0f);
			prediction.setToolTipText("Prediction ("+String.valueOf((float)prediction.getValue()/100.0f)+")");
		}
		else if(e.getSource()==jitter_radius) 
		{
			myKinect.jf.setJitterRadius((float)jitter_radius.getValue()/50.0f);
			jitter_radius.setToolTipText("Jitter Radius ("+String.valueOf((float)jitter_radius.getValue()/50.0f)+")");
		}	
		else if(e.getSource()==max_deviation_radius) 
		{
			myKinect.jf.setMaxDeviationRadius((float)max_deviation_radius.getValue()/50.0f);
			max_deviation_radius.setToolTipText("Max Deviation Radius ("+String.valueOf((float)max_deviation_radius.getValue()/50.0f)+")");
		}			
	}
	
	@Override
	public void subscribeGui() {
	// un-defined gui's

	// subscribe("someMethod");
	// send("someMethod");
	}

	@Override
	public void unsubscribeGui() {
	// commented out subscription due to this class being used for
	// un-defined gui's
	// unsubscribe("someMethod");
		myKinect.stop();
	}  
	  /**
	   * Service State change - this method will be called when a "broadcastState"
	   * method is called which triggers a publishState.  This event handler is typically
	   * used when data or state information in the service has changed, and the UI should
	   * update to reflect this changed state.
	   * @param template
	   */
	public void onState(J4KGui template) {
	  SwingUtilities.invokeLater(new Runnable() {
	    @Override
	    public void run() {

	    }
	  });
	}

}