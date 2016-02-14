package com.czc.myrongdemo.bean;



public class MessageBean {
	private String code ;
	private String url ;
	private String date ;
	public MessageBean() {
		super();
	}
	public MessageBean(String code, String url, String date) {
		super();
		this.code = code;
		this.url = url;
		this.date = date;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	@Override
	public String toString() {
		return "MesageBean [code=" + code + ", url=" + url + ", date=" + date
				+ "]";
	}
	
	public class Content {
		private String duration;
		private String content;
		private String extra;
		private String imageUri;
		private String title;
		private String url;
		
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
		@Override
		public String toString() {
			return "Content [duration=" + duration + ", content=" + content
					+ ", extra=" + extra + ", imageUri=" + imageUri
					+ ", title=" + title + ", url=" + url + "]";
		}
		
		
		
	}
	
}
