package doaing.order.untils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/9/27 15:35
 * 修改人：donghaifeng
 * 修改时间：2017/9/27 15:35
 * 修改备注：
 */

public class BluetoothUtil {

    private static final UUID PRINTER_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String Innerprinter_Address = "00:11:22:33:44:55";

    public static BluetoothAdapter getBTAdapter()
    {
        return BluetoothAdapter.getDefaultAdapter();
    }

    public static BluetoothDevice getDevice(BluetoothAdapter bluetoothAdapter) {

        BluetoothDevice innerprinter_device = null;

        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();//获取蓝牙设备集合

        for (BluetoothDevice device : devices) {

            if (device.getAddress().equals(Innerprinter_Address)) {


                innerprinter_device = device;
                break;
            }
        }
        return innerprinter_device;
    }

    public static BluetoothSocket getSocket(BluetoothDevice device) throws IOException {

        BluetoothSocket socket = device.createRfcommSocketToServiceRecord(PRINTER_UUID);
        socket.connect();
        return socket;
    }

    /**
     *
     *  打印文字
     *  @param text 要打印的文字
     *  @param socket 获取输出流
     */
    public static void printText(String text,BluetoothSocket socket,byte[] command) {

        try {

            OutputStream  outputStream =socket.getOutputStream();

            outputStream.write(command);//设置打印格式

            byte[] data = text.getBytes("gbk");
            outputStream.write(data, 0, data.length);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace(); } }



}
