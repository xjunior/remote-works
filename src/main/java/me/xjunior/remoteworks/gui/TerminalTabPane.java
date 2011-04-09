package me.xjunior.remoteworks.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class TerminalTabPane extends JTabbedPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TerminalTabPane() {
		setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
	}
	
	public void insertTab(String title, Icon icon, Component component, String tip, int index) {
		super.insertTab(title, icon, component, tip, index);
		if (component instanceof TerminalArea)
	    	setTabComponentAt(index,
	                new TabLabel(title));
	}
	
	public TerminalArea[] getSelectedTerminals() {
		if (getTabCount() == 0) return new TerminalArea[0];
		ArrayList<TerminalArea> a = new ArrayList<TerminalArea>();
		for (int i = 0; i < getTabCount(); i++) {
			if (getComponentAt(i) instanceof TerminalArea
					&& ((TabLabel)getTabComponentAt(i)).isSelected())
				a.add((TerminalArea)getComponentAt(i));
		}
		if (a.size() == 0)
			return new TerminalArea[] {(TerminalArea)getComponentAt(getSelectedIndex())};
		else
			return a.toArray(new TerminalArea[0]);
	}

	class TabLabel extends JPanel {
		private static final long serialVersionUID = 1L;
		JCheckBox check;

		public TabLabel(String title) {
	        /*super(new FlowLayout(FlowLayout.LEFT, 0, 0));
	        if (pane == null) {
	            throw new NullPointerException("TabbedPane is null");
	        }*/
	        setOpaque(false);
	        check = new JCheckBox();

	        add(check);
	        add(new JLabel(title));
	        add(new TabButton());
	        //add more space to the top of the component
	        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
	    }

		public boolean isSelected() {
			return check.isSelected();
		}

		private class TabButton extends JButton implements ActionListener {
			private static final long serialVersionUID = 1L;

			public TabButton() {
		        setPreferredSize(new Dimension(18, 18));
		        setToolTipText("Close connection/tab");
		        setContentAreaFilled(false);
		        setFocusable(false);
		        addActionListener(this);
		    }
		
		    public void actionPerformed(ActionEvent e) {
		    	TerminalTabPane pane = TerminalTabPane.this;
		        int i = pane.indexOfTabComponent(TabLabel.this);
		        if (i != -1) {
		        	pane.remove(i);
		        }
		    }
		
		    //paint the cross
		    protected void paintComponent(Graphics g) {
		        super.paintComponent(g);
		        Graphics2D g2 = (Graphics2D) g.create();
		        //shift the image for pressed buttons
		        if (getModel().isPressed()) {
		            g2.translate(1, 1);
		        }
		        g2.setStroke(new BasicStroke(2));
		        g2.setColor(Color.BLACK);
		        if (getModel().isRollover()) {
		            g2.setColor(Color.MAGENTA);
		        }
		        int delta = 6;
		        g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
		        g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
		        g2.dispose();
		    }
		}
	}

}
