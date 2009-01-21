/**
 * 
 */
package com.google.code.annatasha.validator.internal.analysis;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import com.google.code.annatasha.annotations.Method.ExecPermissions;

/**
 * @author Ivan Egorov
 * 
 */
public final class MethodInformation {

	private boolean initialized = false;

	private MethodDeclaration ast;
	private IMethodBinding method;
	private MethodInformation[] declarations;

	private boolean isStatic;

	private boolean isOverridable;
	private MethodInformation[] calls;

	private boolean isAnnotated;
	private Class<?>[] executionClasses;

	/**
	 * @param ast
	 *            The link to AST node
	 * @param method
	 *            The link to method binding
	 * @param declarations
	 *            The link to all declarations of the method from which this
	 *            method inherits the annotations
	 * @param isStatic
	 *            Specifies whether the method is static
	 * @param isOverriddable
	 *            Whether the method might be overridden some way. This means
	 *            whether the <code><pre>
	 *            A ref;
	 *            ref.method();
	 *            </pre></code> would always call A.method() and not the one
	 *            from class B, extending class A.
	 * @param calls
	 *            The list of references to the methods, which are called from
	 *            this method. Note, we should make our best effort to specify
	 *            the most specific type of the object on which the method is
	 *            called. For example for the following code we should link
	 *            <code>Main.main()</code> with <code>A.method()</code> and not
	 *            with <code>I.method()</code>: <code><pre>
	 *            public interface I {
	 *            	void method();
	 *            }
	 *            public class A implements I {
	 *            	void method(){}
	 *            }
	 *            
	 *            public class Main {
	 *            	public static void main(String[] args) {
	 *            		I i = new A();
	 *            		i.method();
	 *            	}
	 *            }
	 *            </pre></code>
	 * @param isAnnotated
	 *            Specifies whether the method has non-inherited
	 *            <code>{@link ExecPermissions}</code> annotation
	 * @param executionClasses
	 *            Meaningful only in case <code>{@link #isAnnotated}</code> is
	 *            set. In this case specifies the list of thread classes,
	 *            allowed to execute method.
	 *            <p>
	 *            If the method overrides an inherited method definition (i.e.
	 *            an sub-class definition of virtual method) then the list must
	 *            contain an sub-class for each of classes in the overridden
	 *            method's list. The list may not contain any additional classes
	 *            which do not subclass an class of the super-method's list.
	 *            <p>
	 *            If the method overrides several inherited methods definition
	 *            (defined in T1, T2, ..., Tn with L1, L2, ..., Ln threads
	 *            classes respectively) then the list must contain all the
	 *            subclasses of any arbitrary set of classes l1, ..., ln, where
	 *            L1 contains l1, ... Ln contains ln.
	 */
	public void initialize(MethodDeclaration ast, IMethodBinding method,
			MethodInformation[] declarations, boolean isStatic,
			boolean isOverriddable, MethodInformation[] calls,
			boolean isAnnotated, Class<?>[] executionClasses) {
		assert !initialized : "Re-initialization of MethodInformation is prohibited";

		this.ast = ast;
		this.method = method;
		this.declarations = declarations;
		this.isStatic = isStatic;
		this.isOverridable = isOverriddable;
		this.calls = calls;
		this.isAnnotated = isAnnotated;
		this.executionClasses = executionClasses;

		this.initialized = true;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public boolean isAnnotated() {
		return isAnnotated;
	}

	public boolean isOverridable() {
		return isOverridable;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public MethodDeclaration getAst() {
		return ast;
	}

	public MethodInformation[] getCalls() {
		return calls;
	}

	public MethodInformation[] getDeclarations() {
		return declarations;
	}

	public Class<?>[] getExecutionClasses() {
		return executionClasses;
	}

	public IMethodBinding getMethod() {
		return method;
	}

}
