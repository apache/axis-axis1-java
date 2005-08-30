package test.wsdl.qname;

import java.rmi.Remote;

public interface MyServiceInterface extends Remote {

  public String Hello(String pName) throws java.rmi.RemoteException;

  public String HelloAgain(int pName) throws java.rmi.RemoteException;

}