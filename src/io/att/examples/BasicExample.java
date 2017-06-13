package io.att.examples;

import io.att.util.Device;
import io.att.util.AttDevice;

public class BasicExample implements AttDevice {
  
  // device credentials
  private static final String deviceId  = "4scA0xAXs71iwbze3ugTAk7w";
  private static final String token     = "spicy:4OLvfJU4aMgVW1VeVsb6iOzJtOuyswvEghpcJQz0";
  
  static Device device;
 
  // variables
  static boolean ledValue;
  static int count = 0;

  public void setup()
  {
    // Initialize the device
    device = new Device(deviceId, token, this);

    // Set up Http and create assets
    device.setupHttp();
    device.getHttp().addAsset("Counter", "Counter", "Tick counter", "sensor", "integer");
    device.getHttp().addAsset("LED", "Blinky", "Blinking LED", "actuator", "boolean");
    
    // Set (initial) asset states through http
    device.setAssetState("Counter", 10);
    device.setAssetState("LED", false);
    
    try{Thread.sleep(1000);}catch(Exception e){}
    
    // Subscribe to mqtt
    device.setupMqtt();
    device.setRate(1);  // set loop at x ticks per second (tick method is called at this rate)
    
    // Initializing done. Start looping
    device.start();
  }

  public void tick()
  {
    count++;
    device.publish("Counter", count);
  }
  
  public void callback(String asset, String value)
  {
    if(asset.equals("LED"))
      ledValue = Boolean.parseBoolean(value);
    
    System.out.println(ledValue);
  }
  
  //
  public static void main(String[] args)
  {
    new BasicExample().setup();
  }  
}