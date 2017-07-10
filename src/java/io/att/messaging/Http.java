package io.att.messaging;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import io.att.util.Sensor;
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

public class Http {

  private String baseUrl = "https://api.allthingstalk.io/";

  private String clientId, clientKey, deviceId;
  private String token;

  public Http(Device device)
  {
    this.deviceId  = device.getDeviceId();
    this.token     = device.getToken();
    
    if(device.getEndpoint() != null && !device.getEndpoint().isEmpty())
      this.baseUrl   = String.format("https://%s/", device.getEndpoint());
  }
  
 /****
  * add sensor to the device
  * @param name unique idenfitier on device level
  * @param title the display title
  * @param description a short description of your asset
  * @param data proconfigured data type. Options include `INTEGER`, `NUMBER`, `STRING`, `BOOLEAN` and `GPS`
  * @return
  */
 public String addSensor(String name, String title, String description, Sensor data)
 {
   return addAsset(name, title, description, data.getType(), data.getProfile());
 }
 
 /****
  * Instead of using the preconfigured data types, you can also set the data type of your sensor manually.
  * @param name unique idenfitier on device level
  * @param title the display title
  * @param description a short description of your asset
  * @param type possible types are `sensor`, `actuator`, `virtual`, `config`
  * @param profile data type. Simple types are `integer`, `number`, `boolean` and `string`. Complex json is also allowed; for example `\"number\": {\"type\": \"integer\"}, \"message\": {\"type\": \"string\"}`
  * @return
  */
 public String addAsset(String name, String title, String description, String type, String profile)
 {
   StringBuffer response = new StringBuffer();

   try
   {
     String url = baseUrl + "device/" + deviceId + "/assets";
     URL obj = new URL(url);
     HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

     // Add request header
     con.setRequestMethod("POST");
     con.setRequestProperty("Content-Type", "application/json");
     con.setRequestProperty("Authorization", "Bearer " + token);

     // Create request body
     StringBuffer sb = new StringBuffer();
     
     sb.append("{");
     sb.append(String.format("\"name\":\"%s\",",        name));
     sb.append(String.format("\"title\":\"%s\",",       title));
     sb.append(String.format("\"description\":\"%s\",", description));
     sb.append(String.format("\"is\":\"%s\",",          type));
     // primitive profile
     if(profile.equals("string") ||
         profile.equals("boolean") ||
         profile.equals("number") ||
         profile.equals("integer"))
       sb.append(String.format("\"profile\":{\"type\": \"%s\"}", profile));
     // complex json profile
     else
     {
       sb.append("\"profile\":{\"type\": \"object\",");
       sb.append("\"properties\":{");
       sb.append(profile);
       sb.append("}");
       sb.append("}");
     }
     
     sb.append("}");
     
     String urlParameters = sb.toString();
    
     // Send post request
     con.setDoOutput(true);
     DataOutputStream wr = new DataOutputStream(con.getOutputStream());
     wr.writeBytes(urlParameters);
     wr.flush();
     wr.close();

     int responseCode = con.getResponseCode();
     System.out.println("Sending 'PUT' request to URL : " + url);
     System.out.println("Parameters : " + urlParameters);
     System.out.println("Response Code : " + responseCode);

     BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
     String inputLine;

     while ((inputLine = in.readLine()) != null) {
       response.append(inputLine);
     }
     in.close();
   }
   catch (IOException e){ e.printStackTrace(); }
   
   if(response.length() > 0)
     return response.toString();
   
   return null; 
 }
  
  /****
   * set state of an asset
   * @param name
   * @param value
   * @return
   */
  public String setAssetState(String name, Object value)
  {
    return setAssetState(name, value, false);
  }

  public String setAssetState(String name, Object value, boolean isComplex)
  {
    StringBuffer response = new StringBuffer();

    try
    {
      String url = baseUrl + "device/" + deviceId + "/asset/" + name + "/state";
      URL obj = new URL(url);
      HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

      // Add request header
      con.setRequestMethod("PUT");
      con.setRequestProperty("Content-Type", "application/json");
      con.setRequestProperty("Authorization", "Bearer " + token);

      // Create request body
      String urlParameters;
      if(value instanceof String)
      {
        if(isComplex)
        {
          System.out.println("Sending complex");
          //urlParameters = String.format("{\"at\":\"2016-04-02T14:56:30.957378Z\",\"value\":{%s}}", value);
          urlParameters = String.format("{\"value\":{%s}}", value);
        }
        else
          urlParameters = String.format("{\"value\":\"%s\"}", value);
      }
      else
        urlParameters = String.format("{\"value\":%s}", value);
     
      // Send post request
      con.setDoOutput(true);
      DataOutputStream wr = new DataOutputStream(con.getOutputStream());
      wr.writeBytes(urlParameters);
      wr.flush();
      wr.close();

      int responseCode = con.getResponseCode();
      System.out.println("Sending 'PUT' request to URL : " + url);
      System.out.println("Parameters : " + urlParameters);
      System.out.println("Response Code : " + responseCode);

      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      String inputLine;

      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();
    }
    catch (IOException e){ e.printStackTrace(); }
    
    if(response.length() > 0)
      return response.toString();
    
    return null; 
  }
  
  /****
   * retrieve asset states
   * used to set threshold configs on startup 
   * @param clientId
   * @param clientKey
   * @param deviceId
   * @param assetName
   * @return
   */
  public String getAssetState(String assetName)
  {
    StringBuffer response = new StringBuffer();
    
    try
    {
      String url = baseUrl + "device/" + deviceId + "/asset/" + assetName + "/state";
      URL obj = new URL(url);
      HttpURLConnection con = (HttpURLConnection) obj.openConnection();

      // Add request header
      con.setRequestMethod("GET");
      con.setRequestProperty("Content-Type", "application/json");
      con.setRequestProperty("Auth-ClientKey", clientKey);
      con.setRequestProperty("Auth-ClientId", clientId);
      
      int responseCode = con.getResponseCode();
      System.out.println("Sending 'GET' request to URL : " + url);
      System.out.println("Response Code : " + responseCode);
      
      // Read response
      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      String inputLine;

      while ((inputLine = in.readLine()) != null)
      {
        response.append(inputLine);
      }
      in.close();
    }
    catch (IOException e){ e.printStackTrace(); }
    
    if(response.length() > 0)
      return response.toString();
    
    return null; 
  }
}