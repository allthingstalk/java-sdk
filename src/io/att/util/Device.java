package io.att.util;

import io.att.messaging.Http;
import io.att.messaging.Mqtt;

/*
  Copyright 2017 AllThingsTalk
  
  Licensed under the Apache License, Version 2.0 (the "License");
  
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
      http://www.apache.org/licenses/LICENSE-2.0
      
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

  @Author Sander Hendrickx
  
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
   * @param <type>deviceId</type> the id of your device from AllThingsTalk
   * @param <type>token</type> the authentication token of your device
   * @param <type>attdevice</type> the interface your main program implements. See [AttDevice.java](#attdevicejava) for more info
   */

  public Device(String deviceId, String token, AttDevice attdevice)
  {
    this.attdevice = attdevice;
    setRate(1);  // default 1 ticks per second
    
    this.deviceId  = deviceId;
    this.token = token;
    //this.subTopic  = String.format("client/%s/in/device/%s/asset/+/command", clientId, deviceId);  // listen for command
    //this.pubTopic  = String.format("client/%s/out/device/%s/asset/", clientId, deviceId);
    this.subTopic = String.format("device/%s/asset/+/command", deviceId);  // listen for command
    this.pubTopic = String.format("device/%s/asset/", deviceId);
  }

  /**
   * Creates a Http instance with the credentials of the device for our API calls.
   */
  public void setupHttp()
  {
    http = new Http(this);
  }
  
  /**
   * Opens a connection to the broker and subscribes to all actuator topics for this device.
   */
  public void setupMqtt()
  {
    mqtt = new Mqtt(this);  // connect to mqtt and subscribe to the assets of this device
  }
  
  public Http getHttp() { return http; }
  
  /**
   * Add an asset to your device in AllThingsTalk.
   * 
   * @param <type>name</type> the unique identifier on device level
   * @param <type>title</type> the display title
   * @param <type>description</type> a short description of your asset
   * @param <type>type</type> possible types are `sensor`, `actuator`, `config`, `virtual`
   * @param <type>profile</type> datatype of this asset. Simple types are `integer`, `number`, `boolean` and `string`. Complex json is also allowed; for example `\"number\": {\"type\": \"integer\"}, \"message\": {\"type\": \"string\"}`
   * @return <type>status 200</type> json containing all details of this asset, including the asset id. For more information, check [the REST API section](/developers/api/)
   */
  public String addAsset(String name, String title, String description, String type, String profile)
  {
    return http.addAsset(name, title, description, type, profile);
  }
  
  /**
   * Set the value of your asset through Htpp. Useful for setting initial values before starting Mqtt and the main program loop
   * 
   * @param <type>asset</type> name of the asset
   * @param <type>value</type> value for this asset. Allowed primitives are `boolean`, `float`, `int`, `double` and `String`
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
   * @param <type>asset</type> name of the asset
   * @param <type>value</type> json String; for example `\"number\":35,\"message\":\"hello world\"`
   */
  public String setComplexAssetState(String asset, String value)
  {
    return http.setAssetState(asset, value, true);
  }
  
  /**
   * Publish an asset value over Mqtt
   * 
   * @param <type>asset</type> name of the asset
   * @param <type>value</type> value for this asset. Allowed primitives are `boolean`, `float`, `int`, `double` and `String`
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
   * Start the main loop of our program. The [tick()](#tick) method will be called at a fixed rate, which you can set using [setRate()](#setrate).
   */
  public void start()
  {
    running = true;
    new Thread(this).start();
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
   * Set the rate of our main loop in FPS
   * 
   * @param <type>rate</type> in Frames Per Second
   */
  public void setRate(double rate)
  {
    nsPerTick = 1000000000D/rate;
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
        attdevice.tick();
      }
      try{ Thread.sleep(2); }
      catch(InterruptedException e) { e.printStackTrace(); }
    }
  }
}
