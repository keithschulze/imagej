package ij.process;

public class Span {
		
	/** create a span array of length numDims initialized to zeroes */
	public static int[] create(int numDims)
	{
		return new int[numDims];
	}
	
	/** create a span array initialized to passed in values */
	public static int[] create(int[] initialValues)
	{
		return initialValues.clone();
	}
	
	/** create a span array that encompasses one plane of dimension width X height and all other dimensions at 1 */
	public static int[] singlePlane(int width, int height, int totalDims)
	{
		int[] values = new int[totalDims];
		values[0] = width;
		values[1] = height;
		for (int i = 2; i < totalDims; i++)
			values[i] = 1;
		return values;
	}
}
