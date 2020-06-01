package model.creatures;

/**
 * Value or set of values part of a committable object. The preparation phase 
 * before committing the values makes it easier to check that all parts are
 * ready to be committed before saving any modification in the commit.
 * @author TLM
 *
 * @param <T>
 */
interface CommittablePart<T> {
	
	/**
	 * Checks that the values in this object can be committed. This operation 
	 * fails if any value is invalid. If any modification is made to the object
	 * after calling this method, it must be called again before calling 
	 * {@link CommittablePart#commit(T)}.
	 * @throws IllegalStateException if any value is invalid.
	 */
	public void prepareCommit();

	/**
	 * Commits the values of this object to a given one. This operation must be
	 * preceded by a call to {@link CommittablePart#prepareCommit()} on the source
	 * object.
	 * @param toReplace	object whose value will be overwritten.
	 * @throws IllegalStateException if the preparatory call has not been made.
	 */
	public void commit(T toReplace);
}
