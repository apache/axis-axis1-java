package org.apache.axis.wsa;

import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.Random;

public class UUIDGenerator
{
  private static UUIDGenerator gen = null;
  
  private Random seeder;
  private String midValue;
  private String baseURI = "http://axis.apache.org/guid/";
  
  protected UUIDGenerator()
  {
    try {
      // get the internet address
      InetAddress inet = InetAddress.getLocalHost();
      byte[] bytes = inet.getAddress();
      String hexInetAddress = hexFormat(getInt(bytes),8);
      
      // get the hashcode for this object
      String thisHashCode = hexFormat(System.identityHashCode(this),8);
      
      // set up midvalue string
      this.midValue = hexInetAddress+thisHashCode;
      
      //load up the randomizer
      seeder = new Random(); // SecureRandom();
      int node = seeder.nextInt();
      
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    
  }
  
  public static UUIDGenerator getInstance()
  {
    if (gen == null)
    {
      gen = new UUIDGenerator();
    }    
    return gen;
  }

  public String getUUID()
  {
    long timeNow = System.currentTimeMillis();
    
    // get int value as unsigned
    int timeLow = (int) timeNow & 0xFFFFFFFF;
    
    // get next random value
    int node = seeder.nextInt();
    
    return (hexFormat(timeLow,8) + midValue + hexFormat(node,8));
  }
  
  public String getUUIDURI()
  {
    long timeNow = System.currentTimeMillis();
    
    // get int value as unsigned
    int timeLow = (int) timeNow & 0xFFFFFFFF;
    
    // get next random value
    int node = seeder.nextInt();
    
    return baseURI + (hexFormat(timeLow,8) + midValue + hexFormat(node,8));
    
  }
  
  private int getInt(byte[] byteInput)
    {
        int i = 0;
        int j = 24;
        for(int k = 0; j >= 0; k++)
        {
            int l = byteInput[k] & 0xff;
            i += l << j;
            j -= 8;
        }

        return i;
    }

    private String hexFormat(int i, int len)
    {
        String str = Integer.toHexString(i);
        StringBuffer stringbuffer = new StringBuffer();
        int actualLen = str.length();
        
        if(actualLen < len)
        {
            for(int j = 0; j < len - actualLen; j++)
                stringbuffer.append("0");

        }
        stringbuffer.append(str);
        
        return stringbuffer.toString();
        
    }

    public void setBaseURI(String baseURI)
    {
      this.baseURI = baseURI;
    }
   
}

