package com.foxconn.cnsbg.escort.subsys.usbserial;

import android.content.Context;

public class SerialCtrl {
    public static final int BAUD_RATE_300 = 300;
    public static final int BAUD_RATE_600 = 600;
    public static final int BAUD_RATE_1200 = 1200;
    public static final int BAUD_RATE_4800 = 4800;
    public static final int BAUD_RATE_9600 = 9600;
    public static final int BAUD_RATE_19200 = 19200;
    public static final int BAUD_RATE_38400 = 38400;
    public static final int BAUD_RATE_57600 = 57600;
    public static final int BAUD_RATE_115200 = 115200;
    public static final int BAUD_RATE_230400 = 230400;
    public static final int BAUD_RATE_460800 = 460800;
    public static final int BAUD_RATE_921600 = 921600;

    public static final byte DATA_BITS_5 = 5;
    public static final byte DATA_BITS_6 = 6;
    public static final byte DATA_BITS_7 = 7;
    public static final byte DATA_BITS_8 = 8;

    public static final byte STOP_BITS_1 = 1;
    public static final byte STOP_BITS_1_5 = 3;
    public static final byte STOP_BITS_2 = 2;

    public static final byte PARITY_NONE = 0;
    public static final byte PARITY_ODD = 1;
    public static final byte PARITY_EVEN = 2;
    public static final byte PARITY_MARK = 3;
    public static final byte PARITY_SPACE = 4;

    public static final byte FLOW_CONTROL_NONE = 0;
    public static final byte FLOW_CONTROL_RTSCTS_IN = 1;
    public static final byte FLOW_CONTROL_RTSCTS_OUT = 2;
    public static final byte FLOW_CONTROL_XONXOFF_IN = 4;
    public static final byte FLOW_CONTROL_XONXOFF_OUT = 8;

    public FT311UARTInterface uart;

    public SerialCtrl(Context context) {
        uart = new FT311UARTInterface(context);
    }

    public int open() {
        return uart.ResumeAccessory();
    }

    public void close() {
        uart.DestroyAccessory(true);
    }

    public int read(byte[] buffer, int size) {
        int[] readBytes = new int[1];
        byte status = uart.ReadData(size, buffer, readBytes);
        if (status == 0x00 && readBytes[0] > 0) {
            return readBytes[0];
        }

        return 0;
    }

    public void write(String destStr) {
        uart.SendData(destStr.length(), destStr.getBytes());
    }

    public void config(int baudRate, byte dataBit, byte stopBit, byte parity, byte flowCtrl) {
        uart.SetConfig(baudRate, dataBit, stopBit, parity, flowCtrl);
    }
}
