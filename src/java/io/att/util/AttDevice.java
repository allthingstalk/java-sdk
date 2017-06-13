package io.att.util;

public interface AttDevice {
  
  /**%
   * Your program implements this interface. It contains 3 methods. In short
   * 
   * - `setup()` should handle the initialization of the device
   * - `tick()` is the main loop of the program
   * - `callback()` handles incoming actuator commands
   * 
   * > Apart from implementing these 3 methods to fit your needs, all you have to do is declare all needed variables.
   %*/
  
  /**
   * This method should contain the initialization of the device. It should include
   * 
   * - Creation of a `Device` object with the correct credentials
   * - Initializing Htpp
   * - Creating assets
   * - Set initial asset values if applicable
   * - Initialize Gpio pins
   * - Initialize Mqtt
   * - Set the loop rate _(optional)_
   * - Start the loop
   */
  public void setup();
  
  /**
   * The main loop of the program. Depending on the rate set for the device, this method is called every x milliseconds. Use this method to read your sensors and send their values to the cloud on a regular basis.
   */
  public void tick();
  
  /**
   * This callback function is automatically called when new data arrives for one of the actuators of the device. This method should handle and process all inbound data.
   * 
   * @param <type>asset</type> the asset name
   * @param <type>value</type> the data value
   */
  public void callback(String asset, String value);
}
