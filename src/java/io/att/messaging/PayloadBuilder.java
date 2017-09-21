package io.att.messaging;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class PayloadBuilder {

  private static final short INT_LENGTH     = 4;
  private static final short FLOAT_LENGTH   = 4;
  private static final short BOOLEAN_LENGTH = 1;
  
  private int length;
  private Mqtt mqtt;
  private List<Byte> payload;
  
  public PayloadBuilder(Mqtt mqtt)
  {
    this.mqtt = mqtt;
    reset();
  }
  
  /****
   * Reset the lenght of the payload
   */
  public void reset()
  {
    length = 0;
    payload = new ArrayList<Byte>();
  }
  
  /****
   * Get the length of the current payload
   * @return
   */
  public int getLength()
  {
    return length;
  }
  
  /****
   * Publish a binary payload to the device
   * 
   * @param value
   */
  public void send()
  {
    byte[] bytes = new byte[payload.size()];
    for(int i=0; i<payload.size(); i++)
    {
      bytes[i] = payload.get(i);  // Copy list to array
    }
   
    mqtt.publishBinary(new String(bytes));
  }
  
  /****
   * Add a boolean to our binary payload
   * 
   * @param b
   * @return
   */
  public int addBoolean(boolean b)
  {
    byte bite = b==true?(byte)1:(byte)0;
    payload.add(bite);

    System.out.println("Added " + BOOLEAN_LENGTH + " bytes to the payload");
    length += BOOLEAN_LENGTH;
    return BOOLEAN_LENGTH;
  }
  
  /****
   * Add an integer to our binary payload
   * 
   * @param i
   * @return
   */
  public int addInteger(int i)
  {
    byte[] bytes = ByteBuffer.allocate(4).putInt(i).array();
    for (byte b : bytes) {
      payload.add(b);
    }

    System.out.println("Added " + INT_LENGTH + " bytes to the payload");
    length += INT_LENGTH;
    return INT_LENGTH; 
  }
  
  /****
   * Add a float to our binary payload
   * 
   * @param f
   * @return
   */
  public int addNumber(float f)
  {    
    byte[] bytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(f).array();
    for (byte b : bytes) {
      payload.add(b);
    }

    System.out.println("Added " + FLOAT_LENGTH + " bytes to the payload");
    length += FLOAT_LENGTH;
    return FLOAT_LENGTH;
  }
  
  /****
   * Add a string to our binary payload
   * 
   * @param s
   * @return
   */
  public int addString(String s)
  {    
    byte[] bytes = s.getBytes();
    for(byte b : bytes) {
      payload.add(b);
    }
   
    int l = bytes.length;
    System.out.println("Added " + l + " bytes to the payload");
    length += l;
    return l;
  }
}