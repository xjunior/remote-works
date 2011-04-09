package me.xjunior.remoteworks.gui;


import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;

import me.xjunior.remoteworks.script.ScriptException;
import me.xjunior.remoteworks.script.ScriptSource;

public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1985629957504940960L;
	JPanel main;
	LoginComponent login;
	TerminalTabPane tabs;
	
	public MainWindow(String title) {
		super(title);
		setMinimumSize(new Dimension(900, 600));
		
		login = new LoginComponent();
		login.getLoginButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				createConnection(login.getUserField().getText(),
						login.getHostField().getText());
				login.setEnabled(false);
			}
		});
		
		tabs = new TerminalTabPane();
		tabs.addTab("Connect to", login);
		
		final JSplitPane sidebar = new JSplitPane();
		sidebar.setOrientation(JSplitPane.VERTICAL_SPLIT);
		sidebar.setTopComponent(new ScriptsSideBar(this));
		sidebar.setBottomComponent(new FavoritesSideBar(this));
		
		final JSplitPane mainDivisor = new JSplitPane();
		mainDivisor.setOneTouchExpandable(true);
		
		mainDivisor.setDividerSize(10);
		mainDivisor.setLeftComponent(sidebar);
		mainDivisor.setRightComponent(tabs);
		
		add(mainDivisor);
		addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent arg0) {
				System.out.println(arg0.isControlDown());
				System.out.println(arg0.getKeyChar());
				if (arg0.isControlDown() && arg0.getKeyChar() == '0') {
					login.setVisible(false);
					sidebar.setVisible(false);
				}
			}
		});
		mainDivisor.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				login.setVisible(false);
				sidebar.setVisible(false);
			}
		}, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_0, java.awt.Event.CTRL_MASK), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}

	public boolean runSript(final ScriptSource script) throws IOException {
		try {
			script.read();
		
			for (final TerminalArea term : tabs.getSelectedTerminals()) {
				new Thread() {
					public void run() {
						try {
							for (ScriptSource dep : script.getDependencies())
								term.getConnection().sendMessage(dep.getSource());
							term.getConnection().sendMessage(script.getSource());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}.start();
			}
			if (tabs.getSelectedComponent() instanceof TerminalArea)
				((TerminalArea)tabs.getSelectedComponent()).requestFocus();
			return true;
		} catch (ScriptException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}
	}

	protected void createConnection(String username, String host) {
		createConnection(username, host, host);
	}
	
	protected void createConnection(String username, String host, final String name) {
		ConnectionThread conn = new ConnectionThread(host, username);
		conn.setKeyboardInteractionListener(new KeyboardInteractioListener() {
			public String onInteraction(String error, String name, String instruction,
					String prompt, boolean echo) {
				EnterSomethingDialog esd = new EnterSomethingDialog(null, name,
						new String[] { error, instruction, prompt}, echo);
				return esd.answer;
			}
		});
		conn.setConnectionListener(new ConnectionThread.ConnectionListener() {
			public void onConnect(ConnectionThread conn) {
				try {
					TerminalArea term = new TerminalArea(conn);
					tabs.addTab(name, term);
					tabs.setSelectedComponent(term);
					term.requestFocus();
					login.setEnabled(true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			public void onDisconnect(ConnectionThread conn) {
				login.setEnabled(true);
				//getContentPane().remove(term);
				//term = null;
			}
		});
		conn.start();
	}
}
