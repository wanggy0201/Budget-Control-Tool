package eecs285.proj4.wanggy;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ObjClientServerSocket
{
  private String     ipAddr;
  private int        portNum;
  Socket     socket;
  ObjectOutputStream outData;
  ObjectInputStream  inData;

  public ObjClientServerSocket(String inIPAddr, int inPortNum)
  {
    ipAddr = inIPAddr;
    portNum = inPortNum;
    inData = null;
    outData = null;
    socket = null;
  }

  public void startClient()
  {
    try
    {
      socket = new Socket(ipAddr, portNum);
      outData = new ObjectOutputStream(socket.getOutputStream());
      inData = new ObjectInputStream(socket.getInputStream());
    } catch (IOException ioe)
    {
      System.out.println("Error: Unable to connect");
      System.exit(10);
    }
  }

  public void startServer()
  {
    ServerSocket serverSock;

    try
    {
      serverSock = new ServerSocket(portNum);

      System.out.println("Wailting for client to connect.....");
      socket = serverSock.accept();

      outData = new ObjectOutputStream(socket.getOutputStream());
      inData = new ObjectInputStream(socket.getInputStream());

      System.out.println("Client Connection Accepted");
    } catch (IOException ioe)
    {
      System.out.println("Error: Caught Exception Starting Server");
      System.exit(7);
    }

  }

  public boolean sendString(String strTosend)
  {
    boolean result = false;

    try
    {
      outData.writeObject(strTosend);
      result = true;
    } catch (IOException e)
    {
      System.out.println("Caught Exception Writing to Socket Stream");
      System.exit(-1);
    }
    return result;
  }

  public String recvString()
  {
    String result = null;
    try
    {
      result = (String) inData.readObject();
    }

    catch (ClassNotFoundException e)
    {
      System.out
          .println("Caught Exception of Sending Wrong Class(String)");
      System.exit(-1);
    } catch (IOException e)
    {
      System.out.println("Caught Exception Reading from Socket Stream");
      System.exit(-1);
    }

    return result;
  }

  public boolean sendInt(int intTosend)
  {
    boolean result = false;

    try
    {
      outData.writeInt(intTosend);
      result = true;
    } catch (IOException e)
    {
      System.out.println("Caught Exception Writing to Socket Stream");
      System.exit(-1);
    }
    return result;
  }

  public int recvInt()
  {
    int result = 0;
    try
    {
      result = inData.readInt();
    }

    catch (IOException e)
    {
      System.out.println("Caught Exception Reading from Socket Stream");
      System.exit(-1);
    }

    return result;
  }

  public boolean sendDouble(double dbTosend)
  {
    boolean result = false;

    try
    {
      outData.writeDouble(dbTosend);
      result = true;
    } catch (IOException e)
    {
      System.out.println("Caught Exception Writing to Socket Stream");
      System.exit(-1);
    }
    return result;
  }

  public double recvDouble()
  {
    double result = 0;
    try
    {

      result = inData.readDouble();
      System.out.println(result);
    }

    catch (IOException e)
    {
      System.out.println("Caught Exception Reading from Socket Stream");
      System.exit(-1);
    }

    return result;
  }

  public boolean sendTransaction(TransactionClass transTosend)
  {
    boolean result = false;

    try
    {
      outData.writeObject(transTosend.category);
      outData.writeObject(transTosend.merchant);
      outData.writeObject(new Double(transTosend.amount));
      result = true;
    } catch (IOException e)
    {
      System.out.println(
          "Caught Exception Writing to Socket Stream(TransactionCLass)");
      System.exit(-1);
    }
    return result;
  }

  public TransactionClass recvtrans()
  {
    TransactionClass result = new TransactionClass();
    try
    {
      result.category = (String) inData.readObject();
      result.merchant = (String) inData.readObject();
      result.amount = (Double) inData.readObject();
    } catch (ClassNotFoundException e)
    {
      System.out
          .println("Caught Exception of Sending Wrong Class(Transaction)");
      System.exit(-1);
    } catch (IOException e)
    {
      System.out.println("Caught Exception Reading from Socket Stream");
      System.exit(-1);
    }

    return result;
  }

}
