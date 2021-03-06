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


package io.att.messaging;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import io.att.util.Asset;
import io.att.util.BuildVersion;
import io.att.util.Device;
import io.att.util.Type;


public class Http {

    public static String endpoint = "https://api.allthingstalk.io/";
    private static String agent;

    private String deviceId;
    private String token;

    static {
        agent = "ATTalk-JavaSDK/" + BuildVersion.getBuildVersion();
    }

    public Http(Device device) {
        this.deviceId = device.getDeviceId();
        this.token = device.getToken();
    }

    public Http(Device device, String url) {
        this.deviceId = device.getDeviceId();
        this.token = device.getToken();

        if (url != null && !url.isEmpty())
            endpoint = "https://" + url + "/";
    }


    /**
     * add sensor to the device
     *
     * @param name    unique identifier on device level
     * @param title   the display title
     * @param type    type of the asset
     * @param profile asset profile
     * @param unit    asset profile unit
     * @return HTTP Response
     */
    public String addAsset(String name, String title, Type type, Asset profile, String unit) {
        return addAsset(name, title, type.name().toLowerCase(), profile.getProfile(), unit);
    }

    public String addAsset(String name, String title, String type, String profile, String unit) {
        StringBuffer response = new StringBuffer();

        try {
            String url = endpoint + "device/" + deviceId + "/assets";
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            // Add request header
            con.setRequestMethod("POST");
            prepareRequest(con);

            // Create request body
            StringBuffer sb = new StringBuffer();

            sb.append("{");
            sb.append(String.format("\"name\":\"%s\",", name));
            sb.append(String.format("\"title\":\"%s\",", title));
            sb.append(String.format("\"is\":\"%s\",", type));
            if (unit == null)
                sb.append(String.format("\"profile\":{\"type\": %s}", profile));  // profile
            else
                sb.append(String.format("\"profile\":{\"type\": %s, \"unit\": \" %s\"}", profile, unit));  // profile

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
        } catch (IOException e) {
            System.out.println("!! Error creating asset");
        }

        if (response.length() > 0)
            return response.toString();

        return null;
    }

    /****
     * set state of an asset
     * @param name
     * @param value
     * @return
     */
    public String setAssetState(String name, Object value) {
        return setAssetState(name, value, false);
    }

    public String setAssetState(String name, Object value, boolean isComplex) {
        StringBuffer response = new StringBuffer();

        try {
            String url = endpoint + "device/" + deviceId + "/asset/" + name + "/state";
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            // Add request header
            con.setRequestMethod("PUT");
            prepareRequest(con);

            // Create request body
            String urlParameters;
            if (value instanceof String) {
                if (isComplex) {
                    System.out.println("Sending complex");
                    urlParameters = String.format("{\"value\":{%s}}", value);
                } else
                    urlParameters = String.format("{\"value\":\"%s\"}", value);
            } else
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response.length() > 0)
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
    public String getAssetState(String assetName) {
        StringBuffer response = new StringBuffer();

        try {
            String url = endpoint + "device/" + deviceId + "/asset/" + assetName + "/state";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // Add request header
            con.setRequestMethod("GET");
            prepareRequest(con);

            int responseCode = con.getResponseCode();
            System.out.println("Sending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            // Read response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response.length() > 0)
            return response.toString();

        return null;
    }

    private void prepareRequest(HttpURLConnection con) {
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "Bearer " + token);
        con.setRequestProperty("User-Agent", agent);
    }
}