package com.czc.myrongdemo.bean;

import java.util.ArrayList;
/**
 * 此类为登陆模块的类,与本应用无关联
 * @author czc
 */
public class TeacherBean {
	
	public DataInfor data;
	public MapInfor map;
	public String status;
	public String statusCode;
	
	public class DataInfor {
		public String account;
		public String alipay;
		public String headImg;
		public String id;
		public String name;
		public String state;
		public String token;
		public String userType;
	}
	public class MapInfor{
		public String httpSrc;
		public String studentId;
		public String subject;
		public String xxtAccount;
		public ArrayList<ManagerInfor> managerList;
		
		
		public class ManagerInfor {
			public String classId;
			public String manager;
		}
		
	}
	
	
	

}
