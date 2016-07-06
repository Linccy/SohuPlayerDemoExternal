package com.sohuvideo.playerdemo.entity;

public class Channel {
	private String channelName;
	private int channelCid;

	public Channel(String channelName, int channelCid) {
		this.channelName = channelName;
		this.channelCid = channelCid;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public int getChannelCid() {
		return channelCid;
	}

	public void setChannelCid(int channelCid) {
		this.channelCid = channelCid;
	}

}
