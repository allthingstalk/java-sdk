package io.att.messaging;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import io.att.util.Device;

public class Http {

  private static final String baseUrl = "https://api.allthingstalk.io/";

  private String clientId, clientKey, deviceId;

  public Http(Device device)
  {
    this.clientId  = device.getClientId();
    this.clientKey = device.getClientKey();
    this.deviceId  = device.getDeviceId();
  }
  
 /****
  * add asset to the device
  * @param name
  * @param title
  * @param description
  * @param type (sensor, actuator, virtual, config)
  * @param profile
  * @param control [optional]
  * @return
  */
 public String addAsset(String name, String title, String description, String type, String profile)
 {
   StringBuffer response = new StringBuffer();

   try
   {
     String url = baseUrl + "device/" + deviceId + "/asset/" + name;
     URL obj = new URL(url);
     HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

     // Add request header
     con.setRequestMethod("PUT");
     con.setRequestProperty("Content-Type", "application/json");
     con.setRequestProperty("Auth-ClientKey", clientKey);
     con.setRequestProperty("Auth-ClientId", clientId);

     // Create request body
     StringBuffer sb = new StringBuffer();
     
     sb.append("{");
     sb.append(String.format("\"name\":\"%s\",",        title));
     sb.append(String.format("\"description\":\"%s\",", description));
     sb.append(String.format("\"is\":\"%s\",",          type));
     System.out.println(profile);
     if(profile.indexOf("type") < 0)
       sb.append(String.format("\"profile\":{\"type\": \"%s\"}", profile));  // todo check for complex profiles
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
     System.out.println(sb.toString());
    
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
      con.setRequestProperty("Auth-ClientKey", clientKey);
      con.setRequestProperty("Auth-ClientId", clientId);

      // Create request body
      String urlParameters;
      if(value instanceof String)
      {
        if(isComplex)
          urlParameters = String.format("{\"value\":{%s}}", value);
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