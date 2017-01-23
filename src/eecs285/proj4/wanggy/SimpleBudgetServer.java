package eecs285.proj4.wanggy;

import java.io.File;
import java.util.Map;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.HashMap;

public class SimpleBudgetServer
{
  public static String UPDATEBUDGET = "!!!";
  public static String SAVETRANS    = "@@@";
  public static String ENDSERVER    = "###";

  private static Scanner inFile;

  public static void main(String[] args)
  {
    ObjClientServerSocket theServer;
    int CategoryNum = 0;

    Map<String, Double> entries = new HashMap<String, Double>();

    theServer = new ObjClientServerSocket("127.0.0.1", 45000);
    theServer.startServer();

    String filename = theServer.recvString();
    String lineStr;
    String tempname;
    Double tempvalue;
    Scanner strScan;
    int count = 0;
    String buffer = null;
    try
    {
      inFile = new Scanner(new File(filename));
      try
      {
        lineStr = inFile.nextLine();
        strScan = new Scanner(lineStr);
        CategoryNum = strScan.nextInt();
      } catch (InputMismatchException excep)
      {
        System.out.println("Input Error");
      }
      for (int i = 0; i < CategoryNum; i++)
      {
        try
        {
          lineStr = inFile.nextLine();
          strScan = new Scanner(lineStr);
          strScan.useDelimiter("[:\n]");
          tempname = strScan.next();
          tempvalue = strScan.nextDouble();
          entries.put(tempname, tempvalue);
          count++;
        } catch (InputMismatchException excep)
        {
          System.out.println("Input Error");
        }
      }

    } catch (FileNotFoundException excep)
    {
      System.out.println("Input Error2");
    }
    while (inFile.hasNext())
    {
      try
      {
        lineStr = inFile.nextLine();
        strScan = new Scanner(lineStr);
        strScan.useDelimiter("[:\n]");
        buffer = strScan.next();
        tempname = strScan.next();
        tempvalue = entries.get(buffer);
        entries.put(buffer,
            tempvalue.doubleValue() - strScan.nextDouble());
      } catch (InputMismatchException excep)
      {
        System.out.println("Input Error");
      }
    }
    inFile.close();
    // finish initialization
    theServer.sendInt(CategoryNum);
    for (String key : entries.keySet())
      theServer.sendString(key);

    theServer
        .sendString(String.format("%.2f", entries.values().toArray()[0]));

    PrintWriter outFile = null;

    String flag;
    int transNum;
    TransactionClass buftrans;

    try
    {
      outFile = new PrintWriter(new FileWriter(filename, true));
    } catch (FileNotFoundException excep)
    {
      System.out.println("Unable to open output file");
    } catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    while (true)
    {
      System.out.println("ile");
      flag = theServer.recvString();
      System.out.println("Flag:" + flag);
      if (!flag.equals(SAVETRANS) && !flag.equals(UPDATEBUDGET)
          && !flag.equals(ENDSERVER))
      {
        theServer.sendString(String.format("%.2f", entries.get(flag)));
        System.out.println("sent");
      } else if (flag.equals(UPDATEBUDGET))
      {
        buftrans = theServer.recvtrans();
        tempvalue = entries.get(buftrans.category);
        entries.put(buftrans.category,
            new Double(tempvalue.doubleValue() - buftrans.amount));
        theServer.sendString(
            String.format("%.2f", entries.get(buftrans.category)));
      } else if (flag.equals(ENDSERVER))
      {
        break;
      } else
      {
        transNum = theServer.recvInt();
        for (int i = 0; i < transNum; i++)
        {
          buftrans = theServer.recvtrans();
          outFile.printf("%s:%s:%.2f\n", buftrans.category,
              buftrans.merchant, buftrans.amount);
        }
        outFile.flush();
      }
    }
    outFile.close();
    System.exit(0);
  }

}
