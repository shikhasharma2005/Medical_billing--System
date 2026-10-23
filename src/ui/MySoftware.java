package ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import db.MyCon;
import java.awt.BasicStroke;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import java.awt.SystemColor;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.event.ActionEvent;

public class MySoftware extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField;
	private JPanel Login, Register, Dashboard, Patient, Medicine, Billing;
	private JTextField textField_1;
	private JPasswordField passwordField_1;
	private JTextField textField_2;

	// Patient
	private JTextField patName, patAge, patDisease;
	private JTable patTable;
	private DefaultTableModel patModel;
	// Medicine
	private JTextField medName, medPrice, medQty;
	private JTable medTable;
	private DefaultTableModel medModel;
	// Billing
	private JTextField billName, billAmount, billDate;
	private JTable billTable;
	private DefaultTableModel billModel;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MySoftware frame = new MySoftware();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// CardLayout needs show(), setVisible() does not switch panels
	private void showCard(String name) {
		CardLayout cl = (CardLayout) contentPane.getLayout();
		cl.show(contentPane, name);
	}

	// ---------------------------------------------------------------
	// ICONS: drawn by the program itself, so no image files are needed
	// and they work on every computer.
	// kinds: hospital, patient, medicine, billing, logout
	// ---------------------------------------------------------------
	private ImageIcon icon(String kind, int s) {
		BufferedImage im = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = im.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setStroke(new BasicStroke(Math.max(1.5f, s / 14f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		Color dark = new Color(0, 70, 140);
		Color red = new Color(210, 40, 50);

		if (kind.equals("hospital")) {
			g.setColor(Color.WHITE);
			g.fillOval(1, 1, s - 2, s - 2);
			g.setColor(red);
			int t = s / 5;
			g.fillRect(s / 2 - t / 2, s / 6, t, s * 2 / 3);
			g.fillRect(s / 6, s / 2 - t / 2, s * 2 / 3, t);
		} else if (kind.equals("patient")) {
			g.setColor(dark);
			g.fillOval(s * 3 / 10, s / 10, s * 2 / 5, s * 2 / 5); // head
			g.fillArc(s / 8, s / 2, s * 3 / 4, s * 3 / 4, 0, 180); // body
		} else if (kind.equals("medicine")) {
			g.rotate(-Math.PI / 4, s / 2.0, s / 2.0);
			Shape cap = new RoundRectangle2D.Double(s / 10.0, s * 3 / 8.0, s * 4 / 5.0, s / 4.0, s / 4.0, s / 4.0);
			g.setColor(red);
			g.fill(cap);
			g.setClip(cap);
			g.setColor(Color.WHITE);
			g.fillRect(s / 2, 0, s, s);
			g.setClip(null);
			g.setColor(dark);
			g.draw(cap);
			g.drawLine(s / 2, s * 3 / 8, s / 2, s * 5 / 8);
		} else if (kind.equals("billing")) {
			g.setColor(Color.WHITE);
			g.fillRoundRect(s / 5, s / 10, s * 3 / 5, s * 4 / 5, s / 8, s / 8);
			g.setColor(dark);
			g.drawRoundRect(s / 5, s / 10, s * 3 / 5, s * 4 / 5, s / 8, s / 8);
			for (int i = 0; i < 4; i++) {
				int y = s * 3 / 10 + i * s / 6;
				g.drawLine(s * 3 / 10, y, s * 7 / 10, y);
			}
		} else if (kind.equals("logout")) {
			g.setColor(dark);
			g.drawRect(s / 8, s / 8, s * 3 / 8, s * 3 / 4); // door
			g.drawLine(s / 2, s / 2, s * 7 / 8, s / 2); // arrow
			g.drawLine(s * 7 / 8, s / 2, s * 3 / 4, s * 3 / 8);
			g.drawLine(s * 7 / 8, s / 2, s * 3 / 4, s * 5 / 8);
		}
		g.dispose();
		return new ImageIcon(im);
	}

	// Put an icon picture on a page
	private void addIcon(JPanel p, String kind, int x, int y, int size) {
	}

	// Save one record (3 values) into database
	private boolean save(String query, String a, String b, String c) {
		if (a.trim().isEmpty() || b.trim().isEmpty() || c.trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Please fill all fields");
			return false;
		}
		try {
			Connection con = MyCon.getCon();
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, a.trim());
			ps.setString(2, b.trim());
			ps.setString(3, c.trim());
			int res = ps.executeUpdate();
			con.close();
			if (res > 0) {
				JOptionPane.showMessageDialog(this, "Saved Successfully");
				return true;
			}
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null, e1.getMessage());
			e1.printStackTrace();
		}
		return false;
	}

	// Delete the row the user selected in the table
	private boolean deleteRow(String query, JTable table) {
		return deleteRow(query, table, 0);
	}

	// offset = first column that holds data (1 when the table has an S.No column)
	private boolean deleteRow(String query, JTable table, int offset) {
		int r = table.getSelectedRow();
		if (r < 0) {
			JOptionPane.showMessageDialog(this, "Click a row in the table first, then press Delete");
			return false;
		}
		int ok = JOptionPane.showConfirmDialog(this, "Delete this record?", "Confirm", JOptionPane.YES_NO_OPTION);
		if (ok != JOptionPane.YES_OPTION) {
			return false;
		}
		try {
			Connection con = MyCon.getCon();
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, String.valueOf(table.getValueAt(r, offset)));
			ps.setString(2, String.valueOf(table.getValueAt(r, offset + 1)));
			ps.setString(3, String.valueOf(table.getValueAt(r, offset + 2)));
			ps.executeUpdate();
			con.close();
			return true;
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null, e1.getMessage());
			e1.printStackTrace();
		}
		return false;
	}

	// Fill the table with records from database (3 columns)
	private void loadTable(String query, DefaultTableModel model) {
		model.setRowCount(0);
		try {
			Connection con = MyCon.getCon();
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				model.addRow(new Object[] { rs.getString(1), rs.getString(2), rs.getString(3) });
			}
			con.close();
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null, e1.getMessage());
			e1.printStackTrace();
		}
	}

	// Fill the billing table: S.No (1, 2, 3 ...) + patient name, amount, date
	private void loadBillTable() {
		billModel.setRowCount(0);
		try {
			Connection con = MyCon.getCon();
			PreparedStatement ps = con.prepareStatement("select patient_name,amount,bill_date from billing");
			ResultSet rs = ps.executeQuery();
			int sno = 1;
			while (rs.next()) {
				billModel.addRow(new Object[] { sno++, rs.getString(1), rs.getString(2), rs.getString(3) });
			}
			con.close();
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null, e1.getMessage());
			e1.printStackTrace();
		}
	}

	// Generate the individual bill of one person.
	// Select a row in the table, OR press the button without selecting
	// and type the Serial Number of the person.
	private void generateBill() {
		if (billModel.getRowCount() == 0) {
			JOptionPane.showMessageDialog(this, "No billing records found");
			return;
		}
		int r = billTable.getSelectedRow();
		if (r < 0) {
			String s = JOptionPane.showInputDialog(this, "Enter Serial No. of the person:");
			if (s == null) {
				return;
			}
			try {
				int n = Integer.parseInt(s.trim());
				if (n < 1 || n > billModel.getRowCount()) {
					JOptionPane.showMessageDialog(this,
							"Serial No. must be between 1 and " + billModel.getRowCount());
					return;
				}
				r = n - 1;
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, "Please enter a valid number");
				return;
			}
		}

		final String sno = String.valueOf(billTable.getValueAt(r, 0));
		final String name = String.valueOf(billTable.getValueAt(r, 1));
		final String amount = String.valueOf(billTable.getValueAt(r, 2));
		final String date = String.valueOf(billTable.getValueAt(r, 3));

		final String bill = "==================================\n"
				+ "          HOSPITAL BILL\n"
				+ "==================================\n"
				+ " Serial No.   : " + sno + "\n"
				+ " Patient Name : " + name + "\n"
				+ " Bill Date    : " + date + "\n"
				+ "----------------------------------\n"
				+ " Total Amount : Rs. " + amount + "\n"
				+ "==================================\n"
				+ "     Thank you. Get well soon!\n";

		JTextArea area = new JTextArea(bill);
		area.setFont(new Font("Monospaced", Font.PLAIN, 14));
		area.setEditable(false);

		Object[] options = { "Save to File", "Close" };
		int choice = JOptionPane.showOptionDialog(this, area, "Bill No. " + sno, JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
		if (choice == 0) {
			String fileName = "Bill_" + sno + "_" + name.replaceAll("[^a-zA-Z0-9]", "_") + ".txt";
			try (FileWriter fw = new FileWriter(fileName)) {
				fw.write(bill);
				JOptionPane.showMessageDialog(this, "Bill saved as " + fileName);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(this, e1.getMessage());
				e1.printStackTrace();
			}
		}
	}

	// Add one label + one text box on a panel
	private JTextField addField(JPanel p, String text, int y) {
		JLabel lbl = new JLabel(text);
		lbl.setFont(new Font("Tahoma", Font.BOLD, 18));
		lbl.setBounds(60, y, 230, 26);
		p.add(lbl);
		JTextField tf = new JTextField();
		tf.setBounds(300, y, 160, 26);
		p.add(tf);
		return tf;
	}

	// Add the table below the form and return it
	private JTable addTable(JPanel p, String c1, String c2, String c3) {
		DefaultTableModel model = new DefaultTableModel(new Object[][] {}, new String[] { c1, c2, c3 });
		JTable table = new JTable(model);
		table.setDefaultEditor(Object.class, null); // table is view only
		JScrollPane sp = new JScrollPane(table);
		sp.setBounds(30, 160, 600, 210);
		p.add(sp);
		return table;
	}

	public MySoftware() {
		setTitle("Hospital Management");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 674, 444);
		setIconImage(icon("hospital", 64).getImage());
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new CardLayout(0, 0));

		// ===================== LOGIN =====================
		Login = new JPanel();
		Login.setForeground(Color.WHITE);
		Login.setBackground(SystemColor.activeCaption);
		contentPane.add(Login, "Login");
		Login.setLayout(null);

		addIcon(Login, "hospital", 282, 25, 100);

		JLabel lblNewLabel = new JLabel("UserName");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 33));
		lblNewLabel.setBounds(84, 155, 191, 40);
		Login.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("PassWord");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 33));
		lblNewLabel_1.setBounds(84, 220, 182, 40);
		Login.add(lblNewLabel_1);

		textField = new JTextField();
		textField.setForeground(Color.BLACK);
		textField.setBounds(338, 165, 123, 29);
		Login.add(textField);
		textField.setColumns(10);

		passwordField = new JPasswordField();
		passwordField.setForeground(Color.BLACK);
		passwordField.setBounds(338, 230, 123, 29);
		Login.add(passwordField);

		JButton btnNewButton_1 = new JButton("Login");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String user = textField.getText();
				String pass = new String(passwordField.getPassword());
				try {
					Connection con = MyCon.getCon();
					PreparedStatement ps = con.prepareStatement("select * from register where user=? and pass=?");
					ps.setString(1, user);
					ps.setString(2, pass);
					ResultSet rs = ps.executeQuery();
					if (rs.next()) {
						passwordField.setText("");
						showCard("Dashboard");
					} else {
						JOptionPane.showMessageDialog(btnNewButton_1, "Invalid User or Pass");
					}
					con.close();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
					e1.printStackTrace();
				}
			}
		});
		btnNewButton_1.setFont(new Font("Tahoma", Font.PLAIN, 17));
		btnNewButton_1.setBounds(250, 286, 89, 23);
		Login.add(btnNewButton_1);

		JButton btnNewButton_2 = new JButton("New User? Register Here");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCard("Register");
			}
		});
		btnNewButton_2.setFont(new Font("Tahoma", Font.PLAIN, 17));
		btnNewButton_2.setBounds(173, 333, 238, 23);
		Login.add(btnNewButton_2);
		
		JLabel lblNewLabel_2 = new JLabel("New label");
		lblNewLabel_2.setIcon(new ImageIcon("C:\\Users\\LENOVO\\OneDrive\\Pictures\\head.png"));
		lblNewLabel_2.setBounds(208, 21, 191, 133);
		Login.add(lblNewLabel_2);

		// ===================== REGISTER =====================
		Register = new JPanel();
		Register.setBackground(SystemColor.info);
		contentPane.add(Register, "Register");
		Register.setLayout(null);

		addIcon(Register, "hospital", 292, 15, 80);

		JLabel lblNewLabel_3 = new JLabel("Enter Name");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblNewLabel_3.setBounds(79, 127, 167, 44);
		Register.add(lblNewLabel_3);

		textField_1 = new JTextField();
		textField_1.setBounds(272, 136, 128, 35);
		Register.add(textField_1);
		textField_1.setColumns(10);

		JLabel lblNewLabel_4 = new JLabel("Enter UserName");
		lblNewLabel_4.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblNewLabel_4.setBounds(65, 193, 197, 35);
		Register.add(lblNewLabel_4);

		JLabel lblNewLabel_5 = new JLabel("Enter Password");
		lblNewLabel_5.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblNewLabel_5.setBounds(65, 261, 167, 25);
		Register.add(lblNewLabel_5);

		passwordField_1 = new JPasswordField();
		passwordField_1.setBounds(275, 260, 104, 35);
		Register.add(passwordField_1);

		textField_2 = new JTextField();
		textField_2.setBounds(272, 197, 128, 35);
		Register.add(textField_2);
		textField_2.setColumns(10);

		JButton btnNewButton_4 = new JButton("Register");
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = textField_1.getText();
				String username = textField_2.getText();
				String password = new String(passwordField_1.getPassword());
				if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
					JOptionPane.showMessageDialog(btnNewButton_4, "Please fill all fields");
					return;
				}
				try {
					Connection con = MyCon.getCon();
					PreparedStatement ps = con.prepareStatement("insert into register(name,user,pass) values(?,?,?)");
					ps.setString(1, name);
					ps.setString(2, username);
					ps.setString(3, password);
					int res = ps.executeUpdate();
					if (res > 0) {
						JOptionPane.showMessageDialog(btnNewButton_4, "Registered Successfully");
						textField_1.setText("");
						textField_2.setText("");
						passwordField_1.setText("");
						showCard("Login");
					}
					con.close();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
					e1.printStackTrace();
				}
			}
		});
		btnNewButton_4.setFont(new Font("Tahoma", Font.BOLD, 20));
		btnNewButton_4.setBounds(214, 332, 128, 23);
		Register.add(btnNewButton_4);
		
		JLabel lblNewLabel_6 = new JLabel("New label");
		lblNewLabel_6.setIcon(new ImageIcon("C:\\Users\\LENOVO\\OneDrive\\Pictures\\head.png"));
		lblNewLabel_6.setBounds(198, 11, 202, 114);
		Register.add(lblNewLabel_6);

		// ===================== DASHBOARD =====================
		Dashboard = new JPanel();
		Dashboard.setBackground(SystemColor.activeCaption);
		contentPane.add(Dashboard, "Dashboard");
		Dashboard.setLayout(null);

		JLabel lblNewLabel_11 = new JLabel("WELCOME TO DASHBOARD");
		lblNewLabel_11.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 20));
		lblNewLabel_11.setBounds(190, 16, 291, 25);
		Dashboard.add(lblNewLabel_11);

		addIcon(Dashboard, "hospital", 272, 140, 100);

		JButton btnNewButton_5 = new JButton("PATIENT");
		btnNewButton_5.setIcon(icon("patient", 36));
		btnNewButton_5.setIconTextGap(12);
		btnNewButton_5.setFont(new Font("Tahoma", Font.BOLD, 17));
		btnNewButton_5.setBounds(10, 63, 210, 60);
		btnNewButton_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadTable("select name,age,disease from patient", patModel);
				showCard("Patient");
			}
		});
		Dashboard.add(btnNewButton_5);

		JButton btnNewButton_6 = new JButton("MEDICINE");
		btnNewButton_6.setIcon(icon("medicine", 36));
		btnNewButton_6.setIconTextGap(12);
		btnNewButton_6.setFont(new Font("Tahoma", Font.BOLD, 17));
		btnNewButton_6.setBounds(414, 63, 210, 60);
		btnNewButton_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadTable("select name,price,quantity from medicine", medModel);
				showCard("Medicine");
			}
		});
		Dashboard.add(btnNewButton_6);

		JButton btnNewButton_7 = new JButton("BILLING");
		btnNewButton_7.setIcon(icon("billing", 36));
		btnNewButton_7.setIconTextGap(12);
		btnNewButton_7.setFont(new Font("Tahoma", Font.BOLD, 17));
		btnNewButton_7.setBounds(10, 301, 210, 60);
		btnNewButton_7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadBillTable();
				showCard("Billing");
			}
		});
		Dashboard.add(btnNewButton_7);

		JButton btnNewButton_8 = new JButton("LOGOUT");
		btnNewButton_8.setIcon(icon("logout", 36));
		btnNewButton_8.setIconTextGap(12);
		btnNewButton_8.setFont(new Font("Tahoma", Font.BOLD, 17));
		btnNewButton_8.setBounds(428, 301, 210, 60);
		btnNewButton_8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCard("Login");
			}
		});
		Dashboard.add(btnNewButton_8);
		
		JLabel lblNewLabel_7 = new JLabel("New label");
		lblNewLabel_7.setIcon(new ImageIcon("C:\\Users\\LENOVO\\OneDrive\\Pictures\\head.png"));
		lblNewLabel_7.setBounds(235, 121, 159, 158);
		Dashboard.add(lblNewLabel_7);

		// ===================== PATIENT =====================
		Patient = new JPanel();
		Patient.setBackground(SystemColor.activeCaption);
		contentPane.add(Patient, "Patient");
		Patient.setLayout(null);

		addIcon(Patient, "patient", 10, 4, 40);
		JLabel p0 = new JLabel("PATIENT DETAILS");
		p0.setFont(new Font("Tahoma", Font.BOLD, 22));
		p0.setBounds(230, 5, 300, 30);
		Patient.add(p0);

		patName = addField(Patient, "Patient Name", 45);
		patAge = addField(Patient, "Age", 80);
		patDisease = addField(Patient, "Disease", 115);
		patTable = addTable(Patient, "Name", "Age", "Disease");
		patModel = (DefaultTableModel) patTable.getModel();

		JButton patSave = new JButton("Save");
		patSave.setFont(new Font("Tahoma", Font.BOLD, 16));
		patSave.setBounds(500, 45, 110, 30);
		patSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean ok = save("insert into patient(name,age,disease) values(?,?,?)",
						patName.getText(), patAge.getText(), patDisease.getText());
				if (ok) {
					patName.setText("");
					patAge.setText("");
					patDisease.setText("");
					loadTable("select name,age,disease from patient", patModel);
				}
			}
		});
		Patient.add(patSave);

		JButton patBack = new JButton("Back");
		patBack.setFont(new Font("Tahoma", Font.BOLD, 16));
		patBack.setBounds(500, 85, 110, 30);
		patBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCard("Dashboard");
			}
		});
		Patient.add(patBack);

		JButton patDel = new JButton("Delete");
		patDel.setFont(new Font("Tahoma", Font.BOLD, 16));
		patDel.setBounds(500, 123, 110, 30);
		patDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (deleteRow("delete from patient where name=? and age=? and disease=? limit 1", patTable)) {
					loadTable("select name,age,disease from patient", patModel);
				}
			}
		});
		Patient.add(patDel);

		// ===================== MEDICINE =====================
		Medicine = new JPanel();
		Medicine.setBackground(SystemColor.info);
		contentPane.add(Medicine, "Medicine");
		Medicine.setLayout(null);

		addIcon(Medicine, "medicine", 10, 4, 40);
		JLabel m0 = new JLabel("MEDICINE DETAILS");
		m0.setFont(new Font("Tahoma", Font.BOLD, 22));
		m0.setBounds(220, 5, 300, 30);
		Medicine.add(m0);

		medName = addField(Medicine, "Medicine Name", 45);
		medPrice = addField(Medicine, "Price", 80);
		medQty = addField(Medicine, "Quantity", 115);
		medTable = addTable(Medicine, "Medicine", "Price", "Quantity");
		medModel = (DefaultTableModel) medTable.getModel();

		JButton medSave = new JButton("Save");
		medSave.setFont(new Font("Tahoma", Font.BOLD, 16));
		medSave.setBounds(500, 45, 110, 30);
		medSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean ok = save("insert into medicine(name,price,quantity) values(?,?,?)",
						medName.getText(), medPrice.getText(), medQty.getText());
				if (ok) {
					medName.setText("");
					medPrice.setText("");
					medQty.setText("");
					loadTable("select name,price,quantity from medicine", medModel);
				}
			}
		});
		Medicine.add(medSave);

		JButton medBack = new JButton("Back");
		medBack.setFont(new Font("Tahoma", Font.BOLD, 16));
		medBack.setBounds(500, 85, 110, 30);
		medBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCard("Dashboard");
			}
		});
		Medicine.add(medBack);

		JButton medDel = new JButton("Delete");
		medDel.setFont(new Font("Tahoma", Font.BOLD, 16));
		medDel.setBounds(500, 123, 110, 30);
		medDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (deleteRow("delete from medicine where name=? and price=? and quantity=? limit 1", medTable)) {
					loadTable("select name,price,quantity from medicine", medModel);
				}
			}
		});
		Medicine.add(medDel);

		// ===================== BILLING =====================
		Billing = new JPanel();
		Billing.setBackground(SystemColor.activeCaption);
		contentPane.add(Billing, "Billing");
		Billing.setLayout(null);

		addIcon(Billing, "billing", 10, 4, 40);
		JLabel b0 = new JLabel("BILLING DETAILS");
		b0.setFont(new Font("Tahoma", Font.BOLD, 22));
		b0.setBounds(230, 5, 300, 30);
		Billing.add(b0);

		billName = addField(Billing, "Patient Name", 45);
		billAmount = addField(Billing, "Amount", 80);
		billDate = addField(Billing, "Date (YYYY-MM-DD)", 115);

		// Billing table has 4 columns: S.No + Patient Name + Amount + Date
		billModel = new DefaultTableModel(new Object[][] {},
				new String[] { "S.No", "Patient Name", "Amount", "Date" });
		billTable = new JTable(billModel);
		billTable.setDefaultEditor(Object.class, null); // table is view only
		billTable.getColumnModel().getColumn(0).setPreferredWidth(50);
		billTable.getColumnModel().getColumn(1).setPreferredWidth(220);
		JScrollPane billScroll = new JScrollPane(billTable);
		billScroll.setBounds(30, 200, 600, 170);
		Billing.add(billScroll);

		JButton billGen = new JButton("Generate Bill");
		billGen.setFont(new Font("Tahoma", Font.BOLD, 16));
		billGen.setBounds(30, 160, 200, 32);
		billGen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				generateBill();
			}
		});
		Billing.add(billGen);

		JButton billSave = new JButton("Save");
		billSave.setFont(new Font("Tahoma", Font.BOLD, 16));
		billSave.setBounds(500, 45, 110, 30);
		billSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean ok = save("insert into billing(patient_name,amount,bill_date) values(?,?,?)",
						billName.getText(), billAmount.getText(), billDate.getText());
				if (ok) {
					billName.setText("");
					billAmount.setText("");
					billDate.setText("");
					loadBillTable();
				}
			}
		});
		Billing.add(billSave);

		JButton billBack = new JButton("Back");
		billBack.setFont(new Font("Tahoma", Font.BOLD, 16));
		billBack.setBounds(500, 85, 110, 30);
		billBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCard("Dashboard");
			}
		});
		Billing.add(billBack);

		JButton billDel = new JButton("Delete");
		billDel.setFont(new Font("Tahoma", Font.BOLD, 16));
		billDel.setBounds(500, 123, 110, 30);
		billDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// offset 1: column 0 is S.No, real data starts at column 1
				if (deleteRow("delete from billing where patient_name=? and amount=? and bill_date=? limit 1", billTable, 1)) {
					loadBillTable();
				}
			}
		});
		Billing.add(billDel);
	}
}