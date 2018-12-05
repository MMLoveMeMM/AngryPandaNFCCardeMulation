package cn.pumpkin.angrypandanfccardemulation;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;

/**
 * @author: zhibao.Liu
 * @version:
 * @date: 2018/12/5 15:06
 * @des:
 * @see {@link }
 */

public class CardService extends HostApduService {

    private final static String TAG = CardService.class.getName();
    // 正确信号
    private byte[] SELECT_OK = hexStringToByteArray("9000");

    // 错误信号
    private byte[] UNKNOWN_ERROR = hexStringToByteArray("0000");

    /**
     * 接收到 NFC 读卡器发送的应用协议数据单元 (APDU) 调用
     * 注意：此方法回调在UI线程,若进行联网操作时，需开子线程
     * 并先返回null，当子线程有数据结果后，再进行回调返回处理
     */
    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        final String AID = "F123466666";

        // 将指令转换成 byte[]
        byte[] selectAPDU = buildSelectApdu(AID);
        Log.d(TAG,"commandApdu : "+commandApdu.toString());
        // 判断是否和读卡器发来的数据相同
        // if (Arrays.equals(selectAPDU, commandApdu)) {
            // 直接模拟返回16位卡号
            String account = "6222222200000001";

            // 获取卡号 byte[]
            byte[] accountBytes = account.getBytes();

            // 处理欲返回的响应数据
            return concatArrays(accountBytes, SELECT_OK);
        // } else {
            // return UNKNOWN_ERROR;
        //}
    }

    @Override
    public void onDeactivated(int reason) {

    }

    private byte[] hexStringToByteArray(String s) throws IllegalArgumentException {
        int len = s.length();
        if (len % 2 == 1) {
            throw new IllegalArgumentException("指令字符串长度必须为偶数 !!!");
        }
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private byte[] buildSelectApdu(String aid) {
        final String HEADER = "00A40400";
        return hexStringToByteArray(HEADER + String.format("%02X", aid.length() / 2) + aid);
    }

    private byte[] concatArrays(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

}
