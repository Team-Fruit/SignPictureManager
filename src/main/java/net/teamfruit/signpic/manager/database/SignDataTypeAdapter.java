package net.teamfruit.signpic.manager.database;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.annotation.Nullable;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * データベースから取り出したSignDataをJsonにするとStackOverflowErrorを吐かれる事の対策。
 *
 * @author TeamFruit
 */
public class SignDataTypeAdapter extends TypeAdapter<SignData> {

	private static SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	@Override
	public void write(final @Nullable JsonWriter out, final @Nullable SignData value) throws IOException {
		if (out!=null&&value!=null) {
			out.beginObject();
			out.name("id").value(value.getId());
			out.name("createDate").value(format.format(value.getCreateDate()));
			out.name("updateDate").value(format.format(value.getUpdateDate()));
			out.name("worldName").value(value.getWorldName());
			out.name("x").value(value.getX());
			out.name("y").value(value.getY());
			out.name("z").value(value.getZ());
			out.name("sign").value(value.getSign());
			out.name("playerName").value(value.getPlayerName());
			out.name("playerUUID").value(value.getPlayerUUID());
			out.endObject();
		}
	}

	@Override
	public SignData read(final @Nullable JsonReader in) throws IOException {
		throw new UnsupportedOperationException();
	}

}
