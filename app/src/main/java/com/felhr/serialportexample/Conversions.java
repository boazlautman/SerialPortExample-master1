package com.felhr.serialportexample;

import android.util.Log;

/**
 * Created by boaz on 11/22/2015.
 */
public class Conversions {
    static public String StringToHexPadRight(String paramValue, int p_Len, String  p_ParamMask)
    {
        paramValue = (paramValue == null) ? "" : paramValue;
        if (paramValue.length() > p_Len / 2)
        {
          //  throw new Exception("");
        }
        String val = padRight(paramValue, p_Len);
        String result = "";
        for(char item : val.toCharArray())
        {
            if (String.valueOf(item) == " " && p_ParamMask != "")
            {
                result += p_ParamMask;
            }
            else
            {
                //c# result += ((byte)item).ToString("X2");
                result += String.format("%02x", (byte)item & 0xff);
            }
        }

        return result.substring(0, p_Len);
    }

    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    public static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }

    public static String padLeftZeros(String str, int n) {
        return String.format("%1$" + n + "s", str).replace(' ', '0');
    }

    static public String NumberToHex(String paramValue, int p_Len)
    {
        Log.d("Pulse_Per_KM_PPK", "before1");
        paramValue = (paramValue == null) ? "0" : paramValue;
        Log.d("Pulse_Per_KM_PPK", "before2");
        //int tmpNumber = int.Parse(paramValue);
        long tmpNumber = Long.parseLong(paramValue);
        Log.d("Pulse_Per_KM_PPK", "before3");
        //  string result = tmpNumber.ToString("X" + p_Len.ToString());
        String result = String.format("%0" + p_Len + "x",(byte)tmpNumber & 0xff);
        Log.d("Pulse_Per_KM_PPK", "before4");
        if (result.length() > p_Len)
        {
           // throw new Exception("Invalid value:" + paramValue);
        }
        return result;
    }

    static public String U1Odometer(String paramValue, int p_Len, String p_ParamMask) throws Exception {
        paramValue = (paramValue == null) ? "" : paramValue;
        byte[] arr = new byte[4];
        Log.d("1",paramValue );
        double tmpValue = Double.parseDouble(paramValue);
        Log.d("2",Double.toString(tmpValue));
        String result = "";


        int x = (int)(Math.floor(tmpValue / 10000));
        tmpValue = tmpValue - x * 10000;
        arr[0] = (byte)x;


        x = (int)Math.floor(tmpValue / 256);
        tmpValue = tmpValue - x * 256;
        arr[1] = (byte)x;

        x = (int)(Math.floor(tmpValue));
        tmpValue = tmpValue - x;
        arr[2] = (byte)x;

        arr[3] = (byte)Math.round(tmpValue * 10);//(byte)(tmpValue * 10);


        String hex = BitConverter.toString(arr).replace("-", "");
        if (hex.length() > p_Len)
        {
            throw new Exception("Invalid value:" + paramValue);
        }
        return hex;

    }

}
