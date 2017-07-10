import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;

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

/*
 * This example show how to actuate a LED connected to a Raspberry Pi on your device from AllThingsTalk
 * 
 * Note:
 * To communicate with the GPIO pins on your Rapsberry Pi, we need a few external jars
 * - pi4j-core.jar
 * - pi4j-gpio-extensions.jar 
 */

public class BlinkingLedRaspberryPi implements AttDevice {
  
  // device credentials
  private static final String deviceId  = "";
  private static final String token     = "";
  private static final String endpoint  = "";  // provide http/mqtt endpoint. Leave blank if using the default
  
  static Device device;
  static final GpioController gpio = GpioFactory.getInstance();
  
  // sensor pin setup
  GpioPinDigitalOutput ledPin;
 
  // variables
  static boolean ledValue;

  public void setup()
  {
    // Initialize the device
    device = new Device(this, deviceId, token, endpoint);

    // Set up Http and create asset
    device.setupHttp();
    device.getHttp().addSensor("LED", "Blinky", "Blinking LED", Sensor.BOOLEAN);
    
    // Set (initial) asset state through http
    device.setAssetState("LED", false);
    
    // Initialize gpio pins
    ledPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01);
    
    try{Thread.sleep(1000);}catch(Exception e){}
    
    // Subscribe to mqtt
    device.setupMqtt();
    device.setRate(1);  // set loop at x ticks per second (tick method is called at this rate)
    
    // Initializing done. Start looping
    device.start();
  }

  public void loop()
  {
    // do something with your device sensors here
  }
  
  public void callback(String asset, String value)
  {
    if(asset.equals("LED"))
    {
      ledValue = Boolean.parseBoolean(value);
      if(ledValue == true)
        ledPin.high();  // turn on the LED
      else
        ledPin.low();   // turn off the LED
    }
    
    System.out.println(ledValue);
  }
  
  //
  public static void main(String[] args)
  {
    new BasicExample().setup();
  }  
}