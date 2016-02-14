package com.czc.myrongdemo.models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.czc.myrongdemo.bean.MsgInfor;
import com.czc.myrongdemo.utils.MLog;
import com.google.gson.Gson;

public class ParseMsgInfor {

	public static List<MsgInfor> parseMsgInfor(String jsonFilePath) {

		List<MsgInfor> listMsgInfor = new ArrayList<MsgInfor>();
		Gson gson = new Gson();
		try {
			File file = new File(jsonFilePath);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while (true) {
				String msgInforJson = reader.readLine();
				if (msgInforJson != null) {
					MsgInfor msgInfor = gson.fromJson(msgInforJson,
							MsgInfor.class);
					listMsgInfor.add(msgInfor);
				} else {
					break;
				}

			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		MLog.d(listMsgInfor.toString());
		return listMsgInfor;

	}

}
