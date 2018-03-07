import io.att.util.AttDevice;
import io.att.util.Device;
import io.att.util.Type;

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

public class Actuation implements AttDevice {
  
  // device credentials
  private static final String deviceId  = "";
  private static final String token     = "";

  static Device device;
 
  // variables
  static boolean ledValue;

  public void setup()
  {
    // Initialize the device
    device = new Device(this, deviceId, token);

    // Add asset (and optionally set an initial value)
    device.addBoolean("Led", "Led", Type.ACTUATOR);
    device.setAssetState("Led", false);
  
    // Initializing done. Start looping
    device.start(3);  // one loop every 3 seconds
  }

  public void loop()
  {
    // do something with outgoing data here
  }
  
  public void callback(String asset, String value)
  {
    if(asset.equals("Led"))
    {
      ledValue = Boolean.parseBoolean(value);
      if(ledValue == true)
        System.out.println("Led is ON");
      else
        System.out.println("Led is OFF");
    }
  }
  
  //
  public static void main(String[] args)
  {
    new Actuation().setup();
  }  
}