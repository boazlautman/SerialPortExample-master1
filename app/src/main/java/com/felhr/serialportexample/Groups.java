package com.felhr.serialportexample;

import android.util.Log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by boaz on 11/19/2015.
 */


public class Groups {
    public enum HDWType
    {
        UU1,
        SML4,
        DoorLogGPRS,
        SML4RF
    }

    public enum MediaType
    {
        RS232,
        SMS
    }
    public static int Protocol;
    public static short[] U1Protocols = {12,11 ,10 ,9, 8, 7, 6, 5, 4 };
    public static String[] HardwareCodes = new String[] { "8191", "8494", "90A0", "8595" };
    public static String ESNNumber = "000000000000000";
    public static String ParametesFlag = "";
    public java.util.Map<String, IGroup> GroupsList;

    public Groups(HDWType DeviceType, int Protocol) {
        GroupsList = new java.util.HashMap<String, IGroup>();

        switch (DeviceType) {
            case UU1:
                switch (Protocol) {
                    case 12:
                        GroupsList.put("SP1", new SP1_P12());
                }
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)

    public @interface GroupsAttributes {
        public boolean Browsable();
    }
    public abstract class SP1_Base implements IGroup
    {
        public String Vehicle_ID;
        public String Office_SMS_Number;
        @GroupsAttributes(Browsable=false)
        public short GPS_accident_deceleration_threshold ;
        @GroupsAttributes(Browsable=false)
        public short GPS_deceleration_threshold;
        @GroupsAttributes(Browsable=false)
        public short GPS_acceleration_threshold;
        public String Pin_code;
        public short driver_data_length;
        public long Engine_hours;
        public long Next_service_odometer;
        public long Next_service_engine_hours;
        public String Company_ID;
        public short Pulse_Per_Revolution;
        @GroupsAttributes(Browsable=false)
        public String Firmware_Version_Number;
        @GroupsAttributes(Browsable=false)
        public String Modem_firmware_version;
    }


    public abstract class SP1_UU1_Base extends  SP1_Base
    {
        public float Current_Odometer;
        public short permission_code_length;
        @GroupsAttributes(Browsable=false)
        public int Pulse_Per_KM_PPK;

    }

    public class SP1_P12 extends SP1_UU1_Base
    {
        @GroupsAttributes(Browsable=false)
        public String ParameterFlags = "FFFFFFFF";
        public int Accelerometer_identifier;
        public int Hardware_version_number;
        public int GSM_modem_type ;

        public SP1_P12()
        {
            Company_ID = "001000";
            driver_data_length = 10;
            Next_service_engine_hours = 999999;
            Next_service_odometer = 999999;
            permission_code_length = 10;
            Pin_code = "11111";
        }
    @Override
    public  String toString()
    {
        StringBuilder str = new StringBuilder("91");
        try
        {

            str.append(Conversions.StringToHexPadRight(ESNNumber, 40,""));
            str.append(ParameterFlags);
            str.append(Conversions.StringToHexPadRight(Vehicle_ID, 20, ""));
            str.append(Conversions.StringToHexPadRight(Office_SMS_Number, 40, ""));
            str.append(Conversions.U1Odometer(String.valueOf(Current_Odometer), 8, "20"));
            str.append("000000");//Reserved param 4,5,6
            str.append(Conversions.StringToHexPadRight(Pin_code, 10, ""));
            str.append(Conversions.NumberToHex(String.valueOf(driver_data_length), 2));
            str.append(Conversions.NumberToHex(String.valueOf(permission_code_length), 2));
            str.append(Conversions.NumberToHex(String.valueOf(Pulse_Per_Revolution), 2));
            str.append(Conversions.NumberToHex(String.valueOf(Engine_hours), 8));
            str.append(Conversions.NumberToHex(String.valueOf(Next_service_odometer), 8));
            str.append(Conversions.NumberToHex(String.valueOf(Next_service_engine_hours), 8));
            str.append(Conversions.NumberToHex(String.valueOf(Pulse_Per_KM_PPK), 4));
            str.append(Conversions.StringToHexPadRight(Company_ID, 12, ""));
/*            if (Company_ID == null || Company_ID.length() != 6)
            {
              //  throw new "CompanyID must be 6 numbers");
            }*/
            str.append("000000000000");//6 bytes reserved to fill 120 bytes
        }
        catch (Exception ex)
        {
            return ex.getMessage();
        }
       // Log.d("aaaa", str.toString().toUpperCase());
        return str.toString().toUpperCase();
    }


    public  void FromBuffer(byte[] buffer)
    {
        try
        {
            ESNNumber = new String(buffer, "UTF-8"); // Encoding.ASCII.GetString(buffer, 5, 20).Trim();
            ParametesFlag = "1111111111111111";
            Vehicle_ID = new String(buffer, 29, 10).trim();
            Office_SMS_Number =new String(buffer, 39, 20).trim();

            byte[] odo2 = { buffer[61], buffer[60] };
            double odo = (double)(buffer[59] * 10000 + BitConverter.toInt16(odo2, 0)) + (double)buffer[62] / 10.0;
            Current_Odometer = (float)(odo);

            //GPS_accident_deceleration_threshold = short.Parse(buffer[61].ToString());

            // GPS_deceleration_threshold = short.Parse(buffer[62].ToString());

            //GPS_acceleration_threshold = short.Parse(buffer[63].ToString());

            Pin_code = new String(buffer, 66, 5).trim();

            driver_data_length =buffer[71];

            permission_code_length = buffer[72];

            Pulse_Per_Revolution = buffer[73];
/*
            byte[] temp = new byte[4];
            Array.Copy(buffer, 74, temp, 0, 4);
            Array.Reverse(temp);
            int i = BitConverter.ToInt32(temp, 0);
            Engine_hours = long.Parse(i.ToString());

            Array.Copy(buffer, 78, temp, 0, 4);
            Array.Reverse(temp);
            i = 0;
            i = BitConverter.ToInt32(temp, 0);
            Next_service_odometer = long.Parse(i.ToString());

            Array.Copy(buffer, 82, temp, 0, 4);
            Array.Reverse(temp);
            i = 0;
            i = BitConverter.ToInt32(temp, 0);
            Next_service_engine_hours = long.Parse(i.ToString());

            temp = new byte[2];
            Array.Copy(buffer, 86, temp, 0, 2);
            Array.Reverse(temp);
            i = 0;
            i = BitConverter.ToUInt16(temp, 0);
            Pulse_Per_KM_PPK = uint.Parse(i.ToString());

            Company_ID = Encoding.ASCII.GetString(buffer, 88, 6).Trim().PadLeft(6, '0');

            string subVersion = Convert.ToInt16(buffer[101]).ToString();
            if (subVersion.Length == 1)
            {
                subVersion = "0" + subVersion;
            }

            Firmware_Version_Number = String.Format("{0}.{1}", Convert.ToInt16(buffer[100]), subVersion);

            double result = 0;
            double.TryParse(Firmware_Version_Number, out result);
            if (result >= 8.14)
            {
                Accelerometer_identifier = int.Parse(buffer[109].ToString());
                Hardware_version_number = int.Parse(buffer[110].ToString());
            }


            Modem_firmware_version = Encoding.ASCII.GetString(buffer, 102, 7).Trim();
            GSM_modem_type = int.Parse(buffer[111].ToString());
            */
        }
        catch (Exception ex)
        {

        }
    }

}

}
