package io.att.util;

import io.att.messaging.Broker;
import io.att.messaging.Http;
import io.att.messaging.Mqtt;

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

public class Device implements Runnable {
  
  /**%
   * All communication happens through the <b>Device</b> class. You don't have to worry about the Http and Mqtt communication underneath. 
   %*/

  private String deviceId;
  private String token;
  
  private String subTopic;
  private String pubTopic;
  
  private Http http;
  private Mqtt mqtt;
  private AttDevice attdevice;
  
  private boolean running;
  
  /**
   * Constructor for our device, containing all needed credentials.
   * 
   * @param deviceId the id of your device from AllThingsTalk
   * @param token the authentication token of your device
   * @param attdevice the interface your main program implements. See [AttDevice.java](#attdevicejava) for more info
   */
  public Device(AttDevice attdevice, String deviceId, String token)
  {
    this.attdevice = attdevice;
    
    this.deviceId = deviceId;
    this.token    = token;
    
    this.subTopic = String.format("device/%s/asset/+/command", deviceId);  // listen for command
    this.pubTopic = String.format("device/%s/asset/", deviceId);
    
    this.http = new Http(this);
    try{Thread.sleep(300);}catch(Exception e){}
    this.mqtt = new Mqtt(this);
  }
  
  /**
   * Constructor for our device, containing all needed credentials.
   * 
   * @param deviceId the id of your device from AllThingsTalk
   * @param token the authentication token of your device
   * @param attdevice the interface your main program implements. See [AttDevice.java](#attdevicejava) for more info
   * @param url the REST API endpoint for http. By default set to _api.allthingstalk.io_
   * @param broker mqtt broker used. By default set to _api.allthingstalk.io_
   */
  public Device(AttDevice attdevice, String deviceId, String token, String url, String broker)
  {
    this.attdevice = attdevice;
    
    this.deviceId = deviceId;
    this.token    = token;
    
    this.subTopic = String.format("device/%s/asset/+/command", deviceId);  // listen for command
    this.pubTopic = String.format("device/%s/asset/", deviceId);
    
    this.http = new Http(this, url);  // set custom api endpoint
    try{Thread.sleep(300);}catch(Exception e){}
    
    if(broker != null && !broker.isEmpty())  // set custom broker
      Broker.broker = broker;
    this.mqtt = new Mqtt(this);
  }
  
  public Http getHttp() { return http; }
  
  /****
   * Add a sensor asset to your device with preset primitive data type.
   * 
   * @param name unique idenfitier on device level
   * @param title the display title
   * @param description a short description of your asset
   * @param data preconfigured data type. Options include `INTEGER`, `NUMBER`, `STRING`, `BOOLEAN` and `GPS`
   * @return
   */
  public String addAsset(String name, String title, String description, Sensor data)
  {
    return http.addAsset(name, title, description, data.getType(), data.getProfile());
  }
  
  /****
   * Add an actuator asset to your device with preset primitive data type.
   * 
   * @param name unique idenfitier on device level
   * @param title the display title
   * @param description a short description of your asset
   * @param data preconfigured data type. Options include `INTEGER`, `NUMBER`, `STRING` and `BOOLEAN`
   * @return
   */
  public String addAsset(String name, String title, String description, Actuator data)
  {
    return http.addAsset(name, title, description, data.getType(), data.getProfile());
  }
  
  /****
   * Add an asset to your device.
   * 
   * @param name unique idenfitier on device level
   * @param title the display title
   * @param description a short description of your asset
   * @param type possible types are `sensor`, `actuator`, `virtual`, `config`
   * @param profile data type. Simple types are `integer`, `number`, `boolean` and `string`. Complex json is also allowed; for example `\"number\": {\"type\": \"integer\"}, \"message\": {\"type\": \"string\"}`
   * @return
   */
  public String addAsset(String name, String title, String description, String type, String profile)
  {
    return http.addAsset(name, title, description, type, profile);
  }
  
  /**
   * Set the value of your asset through Htpp. Useful for setting initial values before starting Mqtt and the main program loop.
   * 
   * @param asset name of the asset
   * @param value value for this asset. Allowed primitives are `boolean`, `float`, `int`, `double` and `String`
   */  
  public String setAssetState(String asset, boolean value)
  {
    return http.setAssetState(asset, value);
  }
  public String setAssetState(String asset, float   value) { return http.setAssetState(asset, value); }
  public String setAssetState(String asset, int     value) { return http.setAssetState(asset, value); }
  public String setAssetState(String asset, double  value) { return http.setAssetState(asset, value); }
  public String setAssetState(String asset, String  value) { return http.setAssetState(asset, value); }
  
  /**
   * Similar to `setAssetState` but used for complex data types containing multiple json fields.
   * 
   * @param asset name of the asset
   * @param value json String; for example `\"number\":35,\"message\":\"hello world\"`
   */
  public String setComplexAssetState(String asset, String value)
  {
    return http.setAssetState(asset, value, true);
  }
  
  /**
   * Publish an asset value over Mqtt.
   * 
   * @param asset name of the asset
   * @param value value for this asset. Allowed primitives are `boolean`, `float`, `int`, `double` and `String`
   */
  public void publish(String asset, boolean value)
  {
    mqtt.publish(asset, ""+value);
  }  
  public void publish(String asset, float   value) { mqtt.publish(asset, ""+value); }
  public void publish(String asset, int     value) { mqtt.publish(asset, ""+value); }
  public void publish(String asset, double  value) { mqtt.publish(asset, ""+value); }
  public void publish(String asset, String  value) { mqtt.publish(asset, value);    }
  
  /**
   * Start the main loop at a given rate.
   * The [tick()](#tick) method will be called at this rate (sec per tick).
   * You can also set the rate using [setRate()](#setrate).
   */
  public void start(int rate)
  {
    setRate(rate);
    running = true;
    new Thread(this).start();
    System.out.println("Loop started");
  }
  
  /**
   * Start the main loop with a default rate of one second per tick.
   */  
  public void start()
  {
    start(1);
  }
  
  /**
   * Stop the main loop. This does not stop our callback method. Incoming actuator commands are still processed.
   */
  public void stop()
  {
    running = false;
  }
  
  public String getDeviceId()  { return deviceId;  }
  public String getToken()     { return token;     }
  
  public String getSubTopic() { return subTopic; }
  public String getPubTopic() { return pubTopic;}
  
  public AttDevice getAttDevice() { return attdevice; }
  
  private double nsPerTick;
  
  /**
   * Set the rate of our main loop in Seconds per Tick
   * 
   * @param rate in Frames Per Second
   */
  public void setRate(double rate)
  {
    nsPerTick = 1000000000D*rate;
  }

  @Override
  public void run()
  {
    long   lastTime = System.nanoTime();
    double delta    = 0;

    while(running)
    {
      long now = System.nanoTime();
      delta += (now - lastTime) / nsPerTick;
      lastTime = now;

      while(delta >= 1)
      {
        delta -= 1;
        attdevice.loop();
      }
      try{ Thread.sleep(2); }
      catch(InterruptedException e) { e.printStackTrace(); }
    }
  }
}
