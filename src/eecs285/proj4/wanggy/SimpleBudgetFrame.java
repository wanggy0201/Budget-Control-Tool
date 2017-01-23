package eecs285.proj4.wanggy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Struct;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.JMenuBar;

public class SimpleBudgetFrame extends JFrame
{
  public static String UPDATEBUDGET = "!!!";
  public static String SAVETRANS    = "@@@";
  public static String ENDSERVER    = "###";
  LinkedList<TransactionClass> transactions;
  JMenuItem                    saveitem;
  JMenuItem                    quititem;
  JComboBox                    Calist;
  JTextField                   merchanttext;
  JTextField                   amounttext;
  JLabel                       balanceval;
  JButton                      Addtran;
  int                          CategoryNum;
  String[]                     Categories;
  ObjClientServerSocket        theClient;
  String                       temp2;

  SimpleBudgetFrame(String inTitle, String filename)
  {
    super(inTitle);
    theClient = new ObjClientServerSocket("127.0.0.1", 45000);
    theClient.startClient();

    theClient.sendString(filename);
    CategoryNum = theClient.recvInt();
    Categories = new String[CategoryNum];
    for (int i = 0; i < CategoryNum; i++)
    {
      Categories[i] = theClient.recvString();
    }

    setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

    ImageViewerDemoActionListener budgetlistener = new ImageViewerDemoActionListener();

    JMenuBar menubar = new JMenuBar();

    JMenu fileMenu = new JMenu("File");
    saveitem = new JMenuItem("Save Budget");
    saveitem.addActionListener(budgetlistener);
    quititem = new JMenuItem("Exit Program");
    quititem.addActionListener(budgetlistener);
    fileMenu.add(saveitem);
    fileMenu.add(quititem);
    menubar.add(fileMenu);
    setJMenuBar(menubar);

    transactions = new LinkedList<TransactionClass>();

    Calist = new JComboBox(Categories);
    Box listbox = new Box(BoxLayout.X_AXIS);
    listbox.add(new JLabel("Category: "));
    listbox.add(Calist);

    Calist.addActionListener(budgetlistener);
    Box merchantbox = new Box(BoxLayout.X_AXIS);
    merchantbox.add(new JLabel("Merchant: "));
    merchanttext = new JTextField();
    merchantbox.add(merchanttext);

    Box amountbox = new Box(BoxLayout.X_AXIS);
    amountbox.add(new JLabel("Amount: $"));
    amounttext = new JTextField();
    amountbox.add(amounttext);

    Box balancebox = new Box(BoxLayout.X_AXIS);
    balancebox.add(new JLabel("Balance: $"));
    balanceval = new JLabel("---");
    balancebox.add(balanceval);
    balanceval.setText(theClient.recvString());
    Addtran = new JButton("Add Transaction");
    Addtran.addActionListener(budgetlistener);

    add(listbox);
    add(merchantbox);
    add(amountbox);
    add(balancebox);
    add(Addtran);

    setSize(350, 170);
    setVisible(true);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

  }

  public class ImageViewerDemoActionListener implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      if (event.getSource().equals(saveitem))
      {
        theClient.sendString(SAVETRANS);
        theClient.sendInt(transactions.size());
        while (transactions.size() != 0)
        {
          theClient.sendTransaction(transactions.getFirst());
          transactions.remove(0);
        }
      } else if (event.getSource().equals(quititem))
      {
        try
        {
          theClient.sendString(ENDSERVER);
          theClient.socket.close();
        } catch (IOException e)
        {
          System.exit(0);
        }
        System.exit(0);
      } else if (event.getSource().equals(Addtran))
      {
        TransactionClass temptran = new TransactionClass(
            (String) Calist.getSelectedItem(), merchanttext.getText(),
            Double.parseDouble(amounttext.getText()));
        transactions.addLast(temptran);
        theClient.sendString(UPDATEBUDGET);
        theClient.sendTransaction(temptran);
        balanceval.setText(theClient.recvString());
        merchanttext.setText("");
        amounttext.setText("");
      } else if (event.getSource().equals(Calist))
      {
        theClient.sendString((String) Calist.getSelectedItem());
        balanceval.setText(theClient.recvString());
      }
    }
  }

}
