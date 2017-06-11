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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.ufl.digitalworlds.j4k.Skeleton;

import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.service.SwingGui;
import org.python.jline.internal.Log;
import org.myrobotlab.service.Bernard;
import org.myrobotlab.service.Runtime;
import org.slf4j.Logger;


public class BernardGui extends ServiceGui implements ActionListener, ChangeListener, Observer {
	
	static final long serialVersionUID = 1L;
	public final static Logger log = LoggerFactory.getLogger(BernardGui.class);
	
	//DecimalFormat df = new DecimalFormat("#.###");
	
	//JPanel eastPanel = new JPanel();
	
	public Skeleton[] skeletonData;
	
	JPanel controls = new JPanel();
	JButton turnOnRobotImitation = new JButton("Turn On Robot Imitation");
	JButton addKinectObservers = new JButton("Add Kinect Observers");
	JButton turnOnVinMoov = new JButton("Turn On Virtual InMoov Service");
	JButton turnOnInMoov = new JButton("Turn On InMoov Service");
	JButton userFaceTracking = new JButton("Start Face Tracking");
	JButton recordGesture = new JButton("Record Gesture");
	JButton playGesture = new JButton("Play Gesture");

	
	public BernardGui(final String boundServiceName, final SwingGui myService) {

	    super(boundServiceName, myService);
	    Bernard boundService = (Bernard) Runtime.getService(boundServiceName);
	    
	    turnOnRobotImitation.addActionListener(this);
	    addKinectObservers.addActionListener(this);
	    turnOnVinMoov.addActionListener(this);
	    turnOnInMoov.addActionListener(this);
	    userFaceTracking.addActionListener(this);
	    recordGesture.addActionListener(this);
	    playGesture.addActionListener(this);
	    
	    controls.add(turnOnRobotImitation);
	    controls.add(addKinectObservers);
	    controls.add(turnOnVinMoov);
	    controls.add(turnOnInMoov);
	    controls.add(userFaceTracking);
	    controls.add(recordGesture);
	    controls.add(playGesture);
	    
		display.setLayout(new BorderLayout());		
		display.add(controls, BorderLayout.CENTER);
		//display.add(eastPanel, BorderLayout.EAST);
		
	}
	  
	@Override
	public void update(Observable subject, Object arg) {

	}
	

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==turnOnRobotImitation)
		{
			if(turnOnRobotImitation.getText().compareTo("Turn Off Robot Imitation")==0)
			{
				//myKinect.stop();
				send("stopRobotImitation");
				turnOnRobotImitation.setText("Turn On Robot Imitation");
			}
			else
			{
				// angular, hybrid
				send("startRobotImitation");
				turnOnRobotImitation.setText("Turn Off Robot Imitation");
			}
		} else if(e.getSource()==addKinectObservers) {
			send("addKinectObservers");
		} else if(e.getSource()==turnOnVinMoov) {
			if(turnOnVinMoov.getText().compareTo("Turn Off Virtual InMoov Service")==0)
			{
				//myKinect.stop();
				turnOnVinMoov.setText("Turn On Virtual InMoov Service");
			}
			else
			{
				send("startVinMoov");
				turnOnVinMoov.setText("Turn Off Virtual InMoov Service");
				//resetKinect();
			}
		} else if(e.getSource()==turnOnInMoov) {
			if(turnOnInMoov.getText()=="Turn Off InMoov Service")
			{
				//myKinect.stop();
				turnOnInMoov.setText("Turn On InMoov Service");
			}
			else
			{
				send("startInMoov");
				turnOnInMoov.setText("Turn Off InMoov Service");
				//resetKinect();
			}
		} else if(e.getSource()==userFaceTracking) {
			if(userFaceTracking.getText()=="Stop Face Tracking")
			{
				userFaceTracking.setText("Start Face Tracking");
			}
			else {
				send("startFaceTracking");
				userFaceTracking.setText("Stop Face Tracking");
			}
		} else if(e.getSource()==recordGesture) {
			JFrame frame = new JFrame();
			frame.setTitle("Set Gesture Name");
			String name = JOptionPane.showInputDialog(frame, "new Gesture name");
			send("recordGesture", name);
		} else if(e.getSource()==playGesture) {
			JFrame frame = new JFrame();
			frame.setTitle("Get Gesture Name");
			String name = JOptionPane.showInputDialog(frame, "Gesture name");
			send("playGesture", name);
		}
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		/*if(e.getSource()==elevation_angle)
		{
			if(!elevation_angle.getValueIsAdjusting())
			{
				myKinect.setElevationAngle(elevation_angle.getValue());
				elevation_angle.setToolTipText("Elevation Angle ("+elevation_angle.getValue()+" degrees)");
			}
		}*/
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
		
	}  
	  /**
	   * Service State change - this method will be called when a "broadcastState"
	   * method is called which triggers a publishState.  This event handler is typically
	   * used when data or state information in the service has changed, and the UI should
	   * update to reflect this changed state.
	   * @param template
	   */
	public void onState(BernardGui template) {
	  SwingUtilities.invokeLater(new Runnable() {
	    @Override
	    public void run() {

	    }
	  });
	}

}