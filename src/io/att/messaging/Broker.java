package io.att.messaging;

public class Broker {

  public  static String  broker   = "broker.allthingstalk.io";
  private static boolean ssl      = true;
  public  static int     port     = ssl==true ? 8883 : 1883;
  private static String  protocol = ssl==true ? "ssl://" : "tcp://";
  
  public static String getUrl()
  {
    return protocol + broker + ":" + port;
  }
}