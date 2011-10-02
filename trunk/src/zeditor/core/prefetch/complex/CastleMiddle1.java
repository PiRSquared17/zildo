package zeditor.core.prefetch.complex;

public class CastleMiddle1 extends AbstractPatch12 {

	final byte[] conv_value =
	{ 0, 49, 47, 48, 35, 40, 0, 57, 33, 0, 36, 56, 34, 55, 54, 0 };

	final byte[] value =
	getReverseTab(conv_value, 33);

	public CastleMiddle1() {
		super(true);
	}

	@Override
	int toBinaryValue(int p_val) {
		int a = p_val - 256 * 7 - 33;
		if (a < 0 || a >= value.length) {
			return 0;
		}
		return value[a];
	}

	@Override
	int toGraphicalValue(int p_val) {
		return conv_value[p_val] + 256 * 7;
	}

}
