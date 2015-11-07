package com.elderbyte.vidada.tags;

/**
 * Represents the logical state of a Tag.
 *
 * Implementation Note:
 * The state names are being used as style-class names in JavaFX css
 * to directly map visual appearance of a StateTagControl. If u add more states,
 * you may consider adding also new style-class definitions for them.
 *
 * @author IsNull
 *
 */
public enum TagState {

	/**
	 * Unknown / not defined State
	 */
	None,


	/**
	 * Represents a tag which is allowed but not required.
	 */
	Allowed,

	/**
	 * Represents an required / selected tag
	 */
	Required,

	/**
	 * Represents an active negated tag which should be blocked
	 */
	Blocked,

	/**
	 * Represents an unclear / conflicting logocal state
	 * (Such as when a State has to represent two different child states)
	 */
	Indeterminate,

	/**
	 * Currently unavailable
	 */
	Unavaiable

}
