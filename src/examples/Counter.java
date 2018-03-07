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

public class Counter implements AttDevice {
  
  // device credentials
  private static final String deviceId  = "eKfwdMREFN0cRrV0aFuPoJ6z";
  private static final String token     = "maker:4Nq1axyolPgtm0lqFz6KOatLyvCLn1vnFcdqXHY0";

  static Device device;
 
  // variables
  static int count = 1;

  public void setup()
  {
    // Initialize the device
    device = new Device(this, deviceId, token);

    // Add asset (and optionally set an initial value)
    device.addInteger("Counter", "Counter");
    device.setAssetState("Counter", count);
  
    // Initializing done. Start looping
    device.start(3);  // one loop every 3 seconds
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
    new Counter().setup();
  }  
}