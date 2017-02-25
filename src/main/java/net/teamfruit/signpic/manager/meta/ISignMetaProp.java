package net.teamfruit.signpic.manager.meta;

public interface ISignMetaProp<E extends ISignMetaProp> {
	boolean parse(String src, String key, String value);

	boolean isInclude();

	E data();
}
