package com.xjtu.hello.main;

import javax.media.Player;
import javax.media.rtp.ReceiveStream;

//新建PlayerFrame类，保存ReceiveStream和对应的Player
class PlayerFrame{
	//播放器
	Player player;
	//接收数据流对象
	ReceiveStream stream;
	//构造器
	public PlayerFrame(Player player, ReceiveStream strm) {
		this.player = player;
		this.stream = strm;
	}
}

