import java.util.Random;

import io.att.util.AttDevice;
import io.att.util.Device;
import io.att.util.Sensor;

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

public class BinaryExample implements AttDevice {
  
  // device credentials
  private static final String deviceId  = "";
  private static final String token     = "";
  
  static Device device;
  
  private Random r = new Random();  // Random generator
 
  // variables
  static int count = 1;

  public void setup()
  {
    // Initialize the device
    device = new Device(this, deviceId, token);

    // Add assets (and optionally set an initial value)
    device.addAsset("integer", "Integer", "Binary test", Sensor.INTEGER);
    device.addAsset("number",  "Number",  "Binary test", Sensor.NUMBER);
    device.addAsset("boolean", "Boolean", "Binary test", Sensor.BOOLEAN);
    device.addAsset("string",  "String",  "Binary test", Sensor.STRING);
  
    // Initializing done. Start looping
    device.start(3);  // one loop every 3 seconds
  }

  public void loop()
  {
    count++;
    float random = 1 + r.nextFloat() * (100 - 1);  // Random float between 1 and 100
    boolean odd = count%2==1?true:false;  // True when count is odd, false when count is even

    // Reset the payload
    device.payload.reset();
    
    // Construct the payload
    device.payload.addInteger(count);
    device.payload.addBoolean(odd);
    device.payload.addNumber(random);
    device.payload.addString("Hey now brown cow!");
    
    // Send the payload
    device.payload.send();
  }
  
  public void callback(String asset, String value)
  {
    // do something with incoming data here
  }
  
  //
  public static void main(String[] args)
  {
    new BinaryExample().setup();
  }  
}