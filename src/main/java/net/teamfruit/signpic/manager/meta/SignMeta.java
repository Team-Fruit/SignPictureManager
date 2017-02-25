package net.teamfruit.signpic.manager.meta;

import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.teamfruit.signpic.manager.meta.prop.AnimationProp;
import net.teamfruit.signpic.manager.meta.prop.OffsetProp;
import net.teamfruit.signpic.manager.meta.prop.RotationProp;
import net.teamfruit.signpic.manager.meta.prop.SizeProp;

public class SignMeta {
	protected static final Pattern g = Pattern.compile("\\((?:([^\\)]*?)~)?(.*?)\\)");
	protected static final Pattern p = Pattern.compile("(?:([^\\d-\\+Ee\\.]?)([\\d-\\+Ee\\.]*)?)+?");

	public static final float defaultInterval = 1f;

	private final boolean hasInvalidMeta;

	private final Set<ISignMetaProp> metas = Sets.newHashSet();

	private <E extends ISignMetaProp> E add(final E e) {
		this.metas.add(e);
		return e;
	}

	public final ISignMetaProp<AnimationProp> animations = add(new AnimationProp());
	public final ISignMetaProp<SizeProp> sizes = add(new SizeProp());
	public final ISignMetaProp<OffsetProp> offsets = add(new OffsetProp());
	public final ISignMetaProp<RotationProp> rotations = add(new RotationProp());

	public SignMeta(final String src) {
		Validate.notNull(src);

		final TreeMap<Float, String> timeline = Maps.newTreeMap();

		final Matcher mgb = g.matcher(src);
		final String s = mgb.replaceAll("");
		timeline.put(0f, s);

		float current = 0;
		float lastinterval = defaultInterval;
		final Matcher mg = g.matcher(src);
		while (mg.find()) {
			final int gcount = mg.groupCount();
			if (2<=gcount) {
				final float time = NumberUtils.toFloat(mg.group(1), lastinterval);
				lastinterval = time;
				current += time;
				final String before = timeline.get(current);
				String meta = mg.group(2);
				if (before!=null)
					meta = before+meta;
				timeline.put(current, meta);
			}
		}

		boolean bb = true;

		for (final Entry<Float, String> entry : timeline.entrySet()) {
			final float time = entry.getKey();
			final String meta = entry.getValue();

			final Matcher mp = p.matcher(meta);
			while (mp.find()) {
				final int gcount = mp.groupCount();
				if (1<=gcount) {
					final String key = mp.group(1);
					final String value = 2<=gcount ? mp.group(2) : "";
					if (!StringUtils.isEmpty(key)||!StringUtils.isEmpty(value)) {
						boolean b = false;
						for (final ISignMetaProp m : this.metas)
							b = m.parse(src, key, value)||b;
						bb = b&&bb;
					}
				}
			}
		}

		this.hasInvalidMeta = !bb;
	}

	public boolean hasInvalidMeta() {
		return this.hasInvalidMeta;
	}
}
