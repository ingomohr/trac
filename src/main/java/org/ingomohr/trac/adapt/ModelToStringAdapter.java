package org.ingomohr.trac.adapt;

/**
 * Adapts a given model to a String representation.
 * 
 * @param <T> the model type
 */
public interface ModelToStringAdapter<T> {

	/**
	 * Adapts the given model to a String representation.
	 * 
	 * @param pModel the model. Cannot be <code>null</code>.
	 * @return string representation of that model. Never <code>null</code>.
	 */
	String adapt(T pModel);

}
