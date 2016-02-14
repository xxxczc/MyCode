package com.czc.myrongdemo.bean;

import java.util.List;

import org.litepal.crud.DataSupport;

public class MsgInfor extends DataSupport{
	private long id;
	private long msgInforId;
	private String fromUserId ;
	private String targetId ;
	private String targetType ;
	private String GroupId ;
	private String classname ;
	private Content content ;
	private String dateTime ;
	private int sentStatus;  // see io.rong.imlib.model.Message.SendStatus
	private int messageDirection; // see io.rong.imlib.model.Message.ReceivedStatus
	private int messageReceivedStatue;
	
	public MsgInfor() {
		super();
	}
	
	long getMsgInforId() {
		return msgInforId;
	}

	void setMsgInforId(long msgInforId) {
		this.msgInforId = msgInforId;
	}



	public String getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public String getGroupId() {
		return GroupId;
	}

	public void setGroupId(String groupId) {
		GroupId = groupId;
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}
	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}
	
	public List<Content> getContents(){
		return DataSupport.where("msginfor_id = ?",String.valueOf(id)).find(Content.class);
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public int getSentStatus() {
		return sentStatus;
	}


	public void setSentStatus(int sentStatus) {
		this.sentStatus = sentStatus;
	}


	public int getMessageDirection() {
		return messageDirection;
	}


	public void setMessageDirection(int messageDirection) {
		this.messageDirection = messageDirection;
	}
	
	

	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}

	public int getMessageReceivedStatue() {
		return messageReceivedStatue;
	}
	
	public void setMessageReceivedStatue(int messageReceivedStatue) {
		this.messageReceivedStatue = messageReceivedStatue;
	}

	@Override
	public String toString() {
		return "MsgInfor [id=" + id + ", msgInforId=" + msgInforId
				+ ", fromUserId=" + fromUserId + ", targetId=" + targetId
				+ ", targetType=" + targetType + ", GroupId=" + GroupId
				+ ", classname=" + classname + ", content=" + content
				+ ", dateTime=" + dateTime + ", sentStatus=" + sentStatus
				+ ", messageDirection=" + messageDirection
				+ ", messageReceivedStatue=" + messageReceivedStatue + "]";
	}

}
