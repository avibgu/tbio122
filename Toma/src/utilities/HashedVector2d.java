package utilities;

import javax.vecmath.Vector2d;

public class HashedVector2d extends Vector2d {

	private static final long serialVersionUID = -9110779971305597196L;

	public HashedVector2d() {
		super();
	}
	
	public HashedVector2d(int pI, int pI2) {
		super(pI, pI2);
	}

	@Override
	public int hashCode() {
		return (int) (Math.pow(2, x) * Math.pow(3, y));
	}

	@Override
	public boolean equals(Object pOther) {

		if (!(pOther instanceof HashedVector2d))
			return false;

		return x == ((HashedVector2d) pOther).x
				&& y == ((HashedVector2d) pOther).y;
	}
}
