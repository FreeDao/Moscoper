package com.skycober.mineral.util;

import java.util.List;

/**
 * �ַ����жϣ���Ҫ���ж��Ƿ�Ϊ��
 * 
 * @author Yes366
 * 
 */
public class StringUtil {

	private static StringUtil instance;

	public static StringUtil getInstance() {
		if (null == instance)
			instance = new StringUtil();
		return instance;
	}

	public boolean IsEmpty(String str) {
		return null == str || "".equalsIgnoreCase(str) ;
	}

}
