package com.elderbyte.vidada.filters;

import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.File;

/**
 * Provides filter expression building methods
 * @author IsNull
 *
 */
public class FilterExpression {

	/**
	 * Combines two filter with a logical AND
	 * Expression: (left && right)
	 * @param left
	 * @param right
	 * @return
	 */
	public static IOFileFilter and(IOFileFilter left, IOFileFilter right){
		return new IOCompositeFilterAND(left, right);
	}

	/**
	 * Combines two filters with a logical OR
	 * Expression: (left || right)
	 * @param left
	 * @param right
	 * @return
	 */
	public static IOFileFilter or(IOFileFilter left, IOFileFilter right){
		return new IOCompositeFilterOR(left, right);
	}


	private static abstract class IOCompositeFilter implements IOFileFilter
	{
		protected IOFileFilter left;
		protected IOFileFilter right;

		protected IOCompositeFilter(IOFileFilter left, IOFileFilter right){
			this.left = left;
			this.right = right;
		}
	}

	private static class IOCompositeFilterAND extends IOCompositeFilter
	{

		public IOCompositeFilterAND(IOFileFilter left, IOFileFilter right) {
			super(left, right);
		}

		@Override
		public boolean accept(File file) {
			return left.accept(file) && right.accept(file);
		}

		@Override
		public boolean accept(File dir, String name) {
			return left.accept(dir, name) && right.accept(dir, name);
		}
	}

	private static class IOCompositeFilterOR extends IOCompositeFilter
	{

		public IOCompositeFilterOR(IOFileFilter left, IOFileFilter right) {
			super(left, right);
		}

		@Override
		public boolean accept(File file) {
			return left.accept(file) || right.accept(file);
		}

		@Override
		public boolean accept(File dir, String name) {
			return left.accept(dir, name) || right.accept(dir, name);
		}
	}
}
