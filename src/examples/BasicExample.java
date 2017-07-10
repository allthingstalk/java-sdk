import io.att.util.Sensor;
import io.att.util.AttDevice;
import io.att.util.Device;

/*    _   _ _ _____ _    _              _____     _ _     ___ ___  _  __
 *   /_\ | | |_   _| |_ (_)_ _  __ _ __|_   _|_ _| | |__ / __|   \| |/ /
 *  / _ \| | | | | | ' \| | ' \/ _` (_-< | |/ _` | | / / \__ \ |) | ' <
 * /_/ \_\_|_| |_| |_||_|_|_||_\__, /__/ |_|\__,_|_|_\_\ |___/___/|_|\_\
 *                             |___/
 *
 * Copyright 2017 AllThingsTalk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class BasicExample implements AttDevice {
  
  // device credentials
  private static final String deviceId  = "";
  private static final String token     = "";
  private static final String endpoint  = "";  // provide http/mqtt endpoint. Leave blank if using the default
  
  static Device device;
 
  // variables
  static boolean ledValue;
  static int count = 0;

  public void setup()
  {
    // Initialize the device
    device = new Device(this, deviceId, token, endpoint);

    // Set up Http and create assets
    device.setupHttp();
    device.getHttp().addSensor("Counter", "Counter", "Tick counter", Sensor.INTEGER);
    
    // Set (initial) asset states through http
    device.setAssetState("Counter", 10);
    
    try{Thread.sleep(1000);}catch(Exception e){}
    
    // Subscribe to mqtt
    device.setupMqtt();
    device.setRate(1);  // set loop at x ticks per second (tick method is called at this rate)
    
    // Initializing done. Start looping
    device.start();
  }

  public void loop()
  {
    count++;
    device.publish("Counter", count);
  }
  
  public void callback(String asset, String value)
  {
    // do something with incoming data here
  }
  
  //
  public static void main(String[] args)
  {
    new BasicExample().setup();
  }  
}