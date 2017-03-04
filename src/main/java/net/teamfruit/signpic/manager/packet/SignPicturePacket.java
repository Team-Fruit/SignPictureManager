package net.teamfruit.signpic.manager.packet;

public class SignPicturePacket {
	public final String command;
	public final String token;
	public final String data;

	public SignPicturePacket(final String command, final String token, final String data) {
		this.command = command;
		this.token = token;
		this.data = data;
	}

}
