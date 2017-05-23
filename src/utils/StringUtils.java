package utils;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;


/**
 * 
 * <p>Title: StringUtils</p>
 * <p>Description:TODO</p>
 * <p>Company: YouJiang</p>
 * @author chaychan
 * @date 2016年8月13日下午3:39:20
 * @version 1.0
 */
public class StringUtils {

	/**返回yyyy-MM-dd HH:mm:ss类型的时间字符串*/
	public static String getCurrentTimeString() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")//
				.format(new Date());
	}
	
	/**返回yyyy-MM-dd类型的当前时间*/
	public static String getCurrentDateString() {
		return new SimpleDateFormat("yyyy-MM-dd")//
				.format(new Date());
	}
	/**
	 * MD5加密
	 * @param str
	 * @return
	 */
	public static String resutlMD5(String str) {
		String resutl = "";
		try {
			MessageDigest instance = MessageDigest.getInstance("MD5");// 获取MD5算法对象
			byte[] digest = instance.digest(str.getBytes());// 对字符串加密,返回字节数组
			StringBuffer strBuffer = new StringBuffer();
			for (byte b : digest) {
				int i = b & 0xff;
				String hexString = Integer.toHexString(i);// 将整数转为16进制
				if (hexString.length() < 2) {
					hexString = "0" + hexString;// 如果是1位的话,补0
				}
				strBuffer.append(hexString);
			}
			resutl = strBuffer.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return resutl;
	}
	/**
	 * 对字符进行排序
	 * @param str
	 * @return
	 */
	public static String sortingStr(String str){
		char[] arr=str.toCharArray();
		Arrays.sort(arr);
		StringBuffer buf=new StringBuffer();
		for(int i=0;i<arr.length;i++){
			buf.append(arr[i]);
		}
		return buf.toString();
	}
	
	public static boolean isEmpty(String str){
		if(str == null || str.equals("")){
			return false;
		}
		return true;
	}
	
}
