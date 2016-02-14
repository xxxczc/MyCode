package com.czc.myrongdemo.bean;

import org.litepal.crud.DataSupport;

public class Content extends DataSupport{
	private String duration;
	private String content;
	private String extra;
	private String imageUri;
	private String title;
	private String url;
	private String localPath;
	private String thumUri;
	private long fileSize;
	private String localUri;
	
	public Content() {
		super();
	}
	
	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getExtra() {
		return extra;
	}
	public void setExtra(String extra) {
		this.extra = extra;
	}
	public String getImageUri() {
		return imageUri;
	}
	public void setImageUri(String imageUri) {
		this.imageUri = imageUri;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
	

	public String getThumUri() {
		return thumUri;
	}

	public void setThumUri(String thumUri) {
		this.thumUri = thumUri;
	}
	
	

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	

	public String getLocalUri() {
		return localUri;
	}

	public void setLocalUri(String localUri) {
		this.localUri = localUri;
	}

	@Override
	public String toString() {
		return "Content [duration=" + duration + ", content=" + content
				+ ", extra=" + extra + ", imageUri=" + imageUri + ", title="
				+ title + ", url=" + url + ", localPath=" + localPath
				+ ", thumUri=" + thumUri + ", fileSize=" + fileSize + "]";
	}

	

	
}