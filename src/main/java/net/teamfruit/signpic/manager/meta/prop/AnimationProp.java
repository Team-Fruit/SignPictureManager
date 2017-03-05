package net.teamfruit.signpic.manager.meta.prop;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.kamesuta.mc.bnnwidget.motion.Easings;

import net.teamfruit.signpic.manager.meta.ISignMetaProp;

public class AnimationProp implements ISignMetaProp<AnimationProp> {
	public Easings easing = Easings.easeLinear;
	public RSNeed redstone = RSNeed.IGNORE;

	private boolean include;

	@Override
	public boolean parse(final String src, final String key, final String value) {
		if (StringUtils.equals(key, PropSyntax.ANIMATION_EASING.id))
			this.easing = Easings.fromId(NumberUtils.toInt(value));
		else if (StringUtils.equals(key, PropSyntax.ANIMATION_REDSTONE.id))
			this.redstone = RSNeed.fromId(NumberUtils.toInt(value));
		else
			this.include = true;
		return !this.include;
	}

	@Override
	public boolean isInclude() {
		return !this.include;
	}

	@Override
	public AnimationProp data() {
		return this;
	}

	public static enum RSNeed {
		IGNORE(0),
		RS_ON(1),
		RS_OFF(2),
		;

		public final int id;

		private RSNeed(final int id) {
			this.id = id;
		}

		private static final ImmutableMap<Integer, RSNeed> rsIds;

		public static RSNeed fromId(final int id) {
			RSNeed rs = rsIds.get(id);
			if (rs==null)
				rs = IGNORE;
			return rs;
		}

		static {
			final Builder<Integer, RSNeed> builder = ImmutableMap.builder();
			for (final RSNeed easing : RSNeed.values())
				builder.put(easing.id, easing);
			rsIds = builder.build();
		}
	}
}
