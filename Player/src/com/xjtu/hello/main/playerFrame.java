package com.xjtu.hello.main;

import javax.media.Player;
import javax.media.rtp.ReceiveStream;

//�½�PlayerFrame�࣬����ReceiveStream�Ͷ�Ӧ��Player
class PlayerFrame{
	//������
	Player player;
	//��������������
	ReceiveStream stream;
	//������
	public PlayerFrame(Player player, ReceiveStream strm) {
		this.player = player;
		this.stream = strm;
	}
}

