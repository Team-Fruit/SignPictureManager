package net.teamfruit.signpic.manager.meta.prop;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import net.teamfruit.signpic.manager.meta.ISignMetaProp;

public class OffsetProp implements ISignMetaProp<OffsetProp> {
	public static final float defaultOffset = 0.5f;

	public final OffsetPropBuilder x = new OffsetPropBuilder(PropSyntax.OFFSET_LEFT.id, PropSyntax.OFFSET_RIGHT.id);
	public final OffsetPropBuilder y = new OffsetPropBuilder(PropSyntax.OFFSET_DOWN.id, PropSyntax.OFFSET_UP.id);
	public final OffsetPropBuilder z = new OffsetPropBuilder(PropSyntax.OFFSET_BACK.id, PropSyntax.OFFSET_FRONT.id);

	private boolean include;

	@Override
	public boolean parse(final String src, final String key, final String value) {
		final boolean a = this.x.parse(src, key, value);
		final boolean b = this.y.parse(src, key, value);
		final boolean c = this.z.parse(src, key, value);
		this.include = a||b||c;
		return this.include;
	}

	@Override
	public boolean isInclude() {
		return this.include;
	}

	@Override
	public OffsetProp data() {
		return this;
	}

	public static class OffsetPropBuilder implements ISignMetaProp {
		public final String neg;
		public final String pos;

		private float offset;
		private boolean include;

		public OffsetPropBuilder(final String neg, final String pos) {
			this.neg = neg;
			this.pos = pos;
		}

		public void set(final float offset) {
			this.offset = offset;
		}

		public float get() {
			return this.offset;
		}

		@Override
		public String toString() {
			return "OffsetPropBuilder [neg="+this.neg+", pos="+this.pos+", offset="+this.offset+"]";
		}

		@Override
		public boolean parse(final String src, final String key, final String value) {
			if (StringUtils.equals(key, this.neg))
				if (StringUtils.isEmpty(value))
					this.offset -= OffsetProp.defaultOffset;
				else
					this.offset -= NumberUtils.toFloat(value, 0f);
			else if (StringUtils.equals(key, this.pos))
				if (StringUtils.isEmpty(value))
					this.offset += OffsetProp.defaultOffset;
				else
					this.offset += NumberUtils.toFloat(value, 0f);
			else
				this.include = true;
			return !this.include;
		}

		@Override
		public boolean isInclude() {
			return this.include;
		}

		@Override
		public OffsetPropBuilder data() {
			return this;
		}
	}
}
