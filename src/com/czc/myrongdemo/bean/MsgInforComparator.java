package com.czc.myrongdemo.bean;

import java.util.Comparator;

import com.czc.myrongdemo.utils.DateUtils;


public class MsgInforComparator implements Comparator<MsgInfor>{

	@Override
	public int compare(MsgInfor o1, MsgInfor o2) {
		String dateTime1 = o1.getDateTime();
		String dateTime2 = o2.getDateTime();
		
		//如果dateTime1小于dateTime2
		if(DateUtils.compareDate(dateTime1, dateTime2)){
			return -1;
		}else{
			return 1;
		}
	}

}
