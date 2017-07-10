package io.att.util;

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
  public void loop();
  
  /**
   * This callback function is automatically called when new data arrives for one of the actuators of the device. This method should handle and process all inbound data.
   * 
   * @param <type>asset</type> the asset name
   * @param <type>value</type> the data value
   */
  public void callback(String asset, String value);
}
