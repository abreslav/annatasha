package com.google.code.annatasha.validator.internal.build;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;

/**
 * Stores mapping from {@link IBinding}s to {@link ASTNode}s
 * 
 * @author Ivan Egorov
 * 
 */

public final class LexicalContext implements Map<IBinding, ASTNode> {

	private final ASTNode fallback;
	private final Map<IBinding, ASTNode> map = new HashMap<IBinding, ASTNode>();

	/**
	 * Creates lexical context manager
	 * 
	 * @param fallback
	 *            The node returned by default when no better context found, not
	 *            <code>null</code>
	 */
	public LexicalContext(ASTNode fallback) {
		assert fallback != null : "Fallback node must be set for lexical context";
		this.fallback = fallback;
	}

	public void clear() {
		map.clear();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public Set<java.util.Map.Entry<IBinding, ASTNode>> entrySet() {
		return map.entrySet();
	}

	public ASTNode get(Object key) {
		final ASTNode result = map.get(key);
		return result == null ? fallback : result;
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Set<IBinding> keySet() {
		return map.keySet();
	}

	public ASTNode put(IBinding key, ASTNode value) {
		return map.put(key, value);
	}

	public void putAll(Map<? extends IBinding, ? extends ASTNode> m) {
		map.putAll(m);
	}

	public ASTNode remove(Object key) {
		return map.remove(key);
	}

	public int size() {
		return map.size();
	}

	public Collection<ASTNode> values() {
		return map.values();
	}
}