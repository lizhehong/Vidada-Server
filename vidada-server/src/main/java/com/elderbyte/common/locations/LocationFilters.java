package com.elderbyte.common.locations;

import com.elderbyte.common.locations.filters.IDirectoryFilter;
import com.elderbyte.common.locations.filters.ILocationFilter;

import java.util.HashSet;
import java.util.Set;


/**
 * Provides some often used filters and filter builders
 * @author IsNull
 *
 */
public final class LocationFilters {

    /***************************************************************************
     *                                                                         *
     * Public static filters                                                   *
     *                                                                         *
     **************************************************************************/

	public transient static final IDirectoryFilter AcceptAllDirectories = new IDirectoryFilter() {
		@Override
		public boolean accept(DirectoryLocation location) {
			return true;
		}

		@Override
		public String toString(){
			return "AcceptAllDirectories Filter";
		}
	};


	public transient static final ILocationFilter AcceptAll = new ILocationFilter() {
		@Override
		public boolean accept(UniformLocation location) {
			return true;
		}

		@Override
		public String toString(){
			return "AcceptAll Filter";
		}
	};


	public transient static final ILocationFilter AcceptAllFiles = new ILocationFilter() {
		@Override
		public boolean accept(UniformLocation location) {
			return location instanceof ResourceLocation;
		}

		@Override
		public String toString(){
			return "AcceptAllFiles Filter";
		}
	};


	public transient static final ILocationFilter AcceptAllDirs = new ILocationFilter() {
		@Override
		public boolean accept(UniformLocation location) {
			boolean accept = location instanceof DirectoryLocation;
			return accept;
		}

		@Override
		public String toString(){
			return "AcceptAllDirs Filter";
		}
	};

    /**
     * A filter which does not accept anything.
     */
    public transient static final ILocationFilter DenyAllFilter =  new ILocationFilter() {
        @Override
        public boolean accept(UniformLocation uniformLocation) {
            return false;
        }

        @Override
        public String toString(){
            return "DenyAll Filter";
        }
    };

    /***************************************************************************
     *                                                                         *
     * Public static API                                                       *
     *                                                                         *
     **************************************************************************/


    /**
	 * Expects a extension list {.abc, .xml, .blub} (without dots dots!)
	 * @param extensions
	 * @return
	 */
	public static ILocationFilter extensionFilter(String[] extensions){
		return new ExtensionFilter(extensions);
	}

	/**
	 * Expects a extension list {.abc, .xml, .blub} (without dots dots!)
	 * @param extensions
	 * @return
	 */
	public static ILocationFilter extensionFilterDotless(String[] extensions){
		return extensionFilter(toSuffixes(extensions));
	}


	/**
	 * Combines two filter with a logical AND
	 * Expression: (left && right)
	 * @param left
	 * @param right
	 * @return
	 */
	public static ILocationFilter and(ILocationFilter left, ILocationFilter right){
		return new IOCompositeFilterAND(left, right);
	}

	/**
	 * Combines two filters with a logical OR
	 * Expression: (left || right)
	 * @param left
	 * @param right
	 * @return
	 */
	public static ILocationFilter or(ILocationFilter left, ILocationFilter right){
		return new IOCompositeFilterOR(left, right);
	}


	private static abstract class IOCompositeFilter implements ILocationFilter
	{
		protected ILocationFilter left;
		protected ILocationFilter right;

		protected IOCompositeFilter(ILocationFilter left, ILocationFilter right){
			this.left = left;
			this.right = right;
		}
	}

    /***************************************************************************
     *                                                                         *
     * Inner classes                                                           *
     *                                                                         *
     **************************************************************************/

    /**
     * A fast, case insensitive file-extension filter.
     * This filter uses a hash-set for high performance.
     */
    static class ExtensionFilter implements ILocationFilter
    {
        private final Set<String> extensions;

        /**
         * Creates a new ExtensionFilter which matches all given extensions
         * @param extensions
         */
        public ExtensionFilter(String[] extensions){

            this.extensions = new HashSet<String>(extensions.length);
            for (String ext : extensions){
                this.extensions.add(ext.toLowerCase());
            }
        }

        @Override
        public boolean accept(UniformLocation location) {
            if(location instanceof ResourceLocation){
                String extension = ((ResourceLocation) location).getExtension();
                return extensions.contains(extension.toLowerCase());
            }
            return false;
        }

        @Override
        public String toString(){
            return "Extension-Filter";
        }
    }

	private static class IOCompositeFilterAND extends IOCompositeFilter
	{

		public IOCompositeFilterAND(ILocationFilter left, ILocationFilter right) {
			super(left, right);
		}

		@Override
		public boolean accept(UniformLocation file) {
			return left.accept(file) && right.accept(file);
		}

		@Override
		public String toString() {
			return "("+ left + " AND " + right + ")";
		}

	}

	private static class IOCompositeFilterOR extends IOCompositeFilter
	{

		public IOCompositeFilterOR(ILocationFilter left, ILocationFilter right) {
			super(left, right);
		}

		@Override
		public boolean accept(UniformLocation file) {
			return left.accept(file) || right.accept(file);
		}

		@Override
		public String toString() {
			return "("+ left + " OR " + right + ")";
		}

	}


    /**
     * Converts an array of file extensions to suffixes for use
     * with IOFileFilters.
     *
     * @param extensions  an array of extensions. Format: {"java", "xml"}
     * @return an array of suffixes. Format: {".java", ".xml"}
     */
    public static String[] toSuffixes(String[] extensions) {
        String[] suffixes = new String[extensions.length];
        for (int i = 0; i < extensions.length; i++) {
            if(extensions[i].length() > 0 && extensions[i].charAt(0) != '.')
                suffixes[i] = "." + extensions[i];
            else
                suffixes[i] = extensions[i];
        }
        return suffixes;
    }


}
