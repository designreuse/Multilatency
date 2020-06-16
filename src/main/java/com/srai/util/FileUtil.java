package com.srai.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Properties;

public abstract class FileUtil {

	public static String fileRead(String fileName) throws Exception {
		File file = new File(fileName);
		if (!file.exists())
			return "";
		FileInputStream fin = new FileInputStream(file);
		byte[] read = new byte[fin.available()];
		fin.read(read, 0, read.length);
		return new String(read);
	}

	public static void fileWrite(String data, String fileName) throws Exception {
		FileOutputStream fileOutput = new FileOutputStream(fileName);
		fileOutput.write(data.getBytes());
		fileOutput.close();
	}

	public static Properties propertiesFile(String fileName) throws Exception {
		Properties properties = new Properties();
		properties.load(new FileInputStream(fileName));
		return properties;
	}

	
	public static String readFile(String fileName) throws Exception {
		StringBuffer sb = new StringBuffer();
		File file = new File(fileName);
		if (!file.exists())
			return "File Not Exists";
		BufferedReader br = new BufferedReader(new FileReader(file));
		br.readLine();
		String line = br.readLine();
		while (line != null) {
			sb.append(line + "\n");
			line = br.readLine();
		}
		return new String(sb);

	}
}
