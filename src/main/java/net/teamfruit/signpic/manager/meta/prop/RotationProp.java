package net.teamfruit.signpic.manager.meta.prop;

import org.apache.commons.lang.StringUtils;

import net.teamfruit.signpic.manager.meta.ISignMetaProp;

public class RotationProp implements ISignMetaProp<RotationProp> {
	private boolean include;

	@Override
	public boolean parse(final String src, final String key, final String value) {
		//Rotationがかかっているかのみ判定
		if (StringUtils.equals(key, RotateType.X.name()))
			;
		else if (StringUtils.equals(key, RotateType.Y.name()))
			;
		else if (StringUtils.equals(key, RotateType.Z.name()))
			;
		else if (StringUtils.equals(key, PropSyntax.ROTATION_ANGLE.id))
			;
		else if (StringUtils.equals(key, PropSyntax.ROTATION_AXIS_X.id))
			;
		//			if (StringUtils.isEmpty(value))
		//				;
		//			else
		//				;
		else if (StringUtils.equals(key, PropSyntax.ROTATION_AXIS_Y.id))
			;
		//			if (StringUtils.isEmpty(value))
		//				;
		//			else
		//				;
		else if (StringUtils.equals(key, PropSyntax.ROTATION_AXIS_Z.id))
			;
		//			if (StringUtils.isEmpty(value))
		//				;
		//			else
		//				;
		else
			this.include = true;
		return !this.include;
	}

	@Override
	public boolean isInclude() {
		return !this.include;
	}

	@Override
	public RotationProp data() {
		return this;
	}

	public static enum RotateType {
		X(PropSyntax.ROTATION_X.id),
		Y(PropSyntax.ROTATION_Y.id),
		Z(PropSyntax.ROTATION_Z.id),
		;

		public final String id;

		private RotateType(final String id) {
			this.id = id;
		}
	}

}
