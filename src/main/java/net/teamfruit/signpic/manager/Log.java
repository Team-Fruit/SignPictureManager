package net.teamfruit.signpic.manager;

import static java.util.logging.Level.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import org.apache.commons.lang.exception.ExceptionUtils;

public class Log {

	private final Logger logger;

	public Log(final Logger logger) {
		this.logger = logger;
	}

	public Logger getLogger() {
		return this.logger;
	}

	private void log(final Level level, final @Nullable String msg) {
		this.logger.log(level, msg);
	}

	private void log(final Level level, final @Nullable Object obj) {
		if (obj!=null) {
			if (obj instanceof Throwable)
				log(level, ExceptionUtils.getFullStackTrace((Throwable) obj));
			else
				log(level, obj.toString());
		} else
			log(level, "null");
	}

	private void log(final Level level, final boolean b) {
		log(level, String.valueOf(b));
	}

	private void log(final Level level, final int i) {
		log(level, String.valueOf(i));
	}

	private void log(final Level level, final long l) {
		log(level, String.valueOf(l));
	}

	private void log(final Level level, final float f) {
		log(level, String.valueOf(f));
	}

	private void log(final Level level, final double d) {
		log(level, String.valueOf(d));
	}

	public void severe(final @Nullable String msg) {
		log(SEVERE, msg);
	}

	public void severe(final @Nullable Object obj) {
		log(SEVERE, obj);
	}

	public void severe(final boolean b) {
		log(SEVERE, b);
	}

	public void severe(final int i) {
		log(SEVERE, i);
	}

	public void severe(final long l) {
		log(SEVERE, l);
	}

	public void severe(final float f) {
		log(SEVERE, f);
	}

	public void severe(final double d) {
		log(SEVERE, d);
	}

	public void warning(final @Nullable String msg) {
		log(WARNING, msg);
	}

	public void warning(final @Nullable Object obj) {
		log(WARNING, obj);
	}

	public void warning(final boolean b) {
		log(WARNING, b);
	}

	public void warning(final int i) {
		log(WARNING, i);
	}

	public void warning(final long l) {
		log(WARNING, l);
	}

	public void warning(final float f) {
		log(WARNING, f);
	}

	public void warning(final double d) {
		log(WARNING, d);
	}

	public void info(final @Nullable String msg) {
		log(INFO, msg);
	}

	public void info(final @Nullable Object obj) {
		log(INFO, obj);
	}

	public void info(final boolean b) {
		log(INFO, b);
	}

	public void info(final int i) {
		log(INFO, i);
	}

	public void info(final long l) {
		log(INFO, l);
	}

	public void info(final float f) {
		log(INFO, f);
	}

	public void info(final double d) {
		log(INFO, d);
	}

	public void config(final @Nullable String msg) {
		log(CONFIG, msg);
	}

	public void config(final @Nullable Object obj) {
		log(CONFIG, obj);
	}

	public void config(final boolean b) {
		log(CONFIG, b);
	}

	public void config(final int i) {
		log(CONFIG, i);
	}

	public void config(final long l) {
		log(CONFIG, l);
	}

	public void config(final float f) {
		log(CONFIG, f);
	}

	public void config(final double d) {
		log(CONFIG, d);
	}

	public void fine(final @Nullable String msg) {
		log(FINE, msg);
	}

	public void fine(final @Nullable Object obj) {
		log(FINE, obj);
	}

	public void fine(final boolean b) {
		log(FINE, b);
	}

	public void fine(final int i) {
		log(FINE, i);
	}

	public void fine(final long l) {
		log(FINE, l);
	}

	public void fine(final float f) {
		log(FINE, f);
	}

	public void fine(final double d) {
		log(FINE, d);
	}

	public void finer(final @Nullable String msg) {
		log(FINER, msg);
	}

	public void finer(final @Nullable Object obj) {
		log(FINER, obj);
	}

	public void finer(final boolean b) {
		log(FINER, b);
	}

	public void finer(final int i) {
		log(FINER, i);
	}

	public void finer(final long l) {
		log(FINER, l);
	}

	public void finer(final float f) {
		log(FINER, f);
	}

	public void finer(final double d) {
		log(FINER, d);
	}

	public void finest(final @Nullable String msg) {
		log(FINEST, msg);
	}

	public void finest(final @Nullable Object obj) {
		log(FINEST, obj);
	}

	public void finest(final boolean b) {
		log(FINEST, b);
	}

	public void finest(final int i) {
		log(FINEST, i);
	}

	public void finest(final long l) {
		log(FINEST, l);
	}

	public void finest(final float f) {
		log(FINEST, f);
	}

	public void finest(final double d) {
		log(FINEST, d);
	}

}
