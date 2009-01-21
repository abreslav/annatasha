package com.google.code.annatasha.validator.internal.build;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTRequestor;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IMemberValuePairBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.google.code.annatasha.annotations.Field.ReadPermissions;
import com.google.code.annatasha.annotations.Field.WritePermissions;
import com.google.code.annatasha.annotations.Method.ExecPermissions;
import com.google.code.annatasha.validator.core.AnnatashaCore;
import com.google.code.annatasha.validator.internal.analysis.TypeInformation;
import com.google.code.annatasha.validator.internal.build.AnnatashaVisitor.Listener;

public final class ASTValidator extends ASTRequestor {

	private enum CircullarReferenceDiagnostics {

		NO_DIAGNOSTICS("Circullar type dependency found")// ,
		;

		private final String message;

		CircullarReferenceDiagnostics(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}

	}

//	private static final String ANNATASHA_VISITOR_PROPERTY = "com.google.code.annatasha.validator.visitor";

	public final static String TYPE_INFO = "com.google.code.annatasha.validator.typeinfo";
	public final static String FIELD_INFO = "com.google.code.annatasha.validator.fieldinfo";
	public final static String METHOD_INFO = "com.google.code.annatasha.validator.methodinfo";

	private final static class CircullarTypeReference extends Exception {

		private final ArrayList<TypeInformation> cycle = new ArrayList<TypeInformation>();

		public CircullarTypeReference(TypeInformation clazz) {
			cycle.add(clazz);
		}

		public void pushClass(TypeInformation clazz) {
			cycle.add(clazz);
		}

		public TypeInformation[] getCycle() {
			return cycle.toArray(new TypeInformation[cycle.size()]);
		}

		public TypeInformation getInitialClass() {
			return cycle.get(0);
		}

	}

	private static interface Annotations {
		final static String THREAD_MARKER = "com.google.code.annatasha.annotations.ThreadMarker";

		final static String READ_PERMISSIONS = "com.google.code.annatasha.annotations.Field.ReadPermissions";
		final static String WRITE_PERMISSIONS = "com.google.code.annatasha.annotations.Field.WritePermissions";
		final static String EXEC_PERMISSIONS = "com.google.code.annatasha.annotations.Method.ExecPermissions";
	}

	private final static class SourcedDeclaration<D> {
		public final IResource source;
		public final D declaration;

		public SourcedDeclaration(IResource source, D declaration) {
			super();
			this.source = source;
			this.declaration = declaration;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;

			if (obj instanceof SourcedDeclaration) {
				return source.equals(((SourcedDeclaration) obj).source)
						&& declaration
								.equals(((SourcedDeclaration) obj).declaration);
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return declaration.hashCode();
		}
	}

	private final ASTValidatorListener listener = new ASTValidatorListener();
	private final ArrayList<SourcedDeclaration<TypeDeclaration>> types = new ArrayList<SourcedDeclaration<TypeDeclaration>>();
	private final ArrayList<SourcedDeclaration<MethodDeclaration>> methods = new ArrayList<SourcedDeclaration<MethodDeclaration>>();
	private final ArrayList<SourcedDeclaration<FieldDeclaration>> fields = new ArrayList<SourcedDeclaration<FieldDeclaration>>();

	private final class ASTValidatorListener implements Listener {

		public void acceptFieldDeclaration(AnnatashaVisitor visitor,
				FieldDeclaration node) {
			fields.add(new SourcedDeclaration<FieldDeclaration>(visitor.getResource(), node));
		}

		public void acceptMethodDeclaration(AnnatashaVisitor visitor,
				MethodDeclaration node) {
			methods.add(new SourcedDeclaration<MethodDeclaration>(visitor.getResource(), node));
		}

		public void acceptTypeDeclaration(AnnatashaVisitor visitor,
				TypeDeclaration node) {
			types.add(new SourcedDeclaration<TypeDeclaration>(visitor.getResource(), node));
		}

	}

	@Override
	public void acceptAST(ICompilationUnit source, CompilationUnit ast) {
		System.out.println("Accepting source: " + source.getElementName());
		AnnatashaVisitor visitor = new AnnatashaVisitor(source.getResource(),
				listener);
		ast.accept(visitor);
		super.acceptAST(source, ast);
	}

	public void validate() throws CoreException {
		proceedTypeChecks();
	}

	private void proceedTypeChecks() throws CoreException {
		for (final SourcedDeclaration<TypeDeclaration> srcType : types) {
			final TypeDeclaration type = srcType.declaration;
			ITypeBinding binding = type.resolveBinding();
			System.err.println(type.getName().getFullyQualifiedName());
			assert binding != null : "Unresolved binding for type "
					+ type.getName().getFullyQualifiedName();

			try {
				processTypeDeclaration(srcType.source, type, binding,
						CircullarReferenceDiagnostics.NO_DIAGNOSTICS);
			} catch (CircullarTypeReference ccr) {
				assert false : "All circullar references must be handled inside processTypeDeclaration";
			}
		}

		// for (MethodDeclaration method : methods) {
		// IMethodBinding binding = method.resolveBinding();
		// assert binding != null : "Unresolved binding for method "
		// + method.getName().getFullyQualifiedName();
		//
		// final MethodInformation information = getMethodInformation(method);
		// final boolean isStatic = Modifier.isStatic(binding.getModifiers());
		// final boolean isOverriddable = !Modifier.isPrivate(binding
		// .getModifiers())
		// && !Modifier.isFinal(binding.getModifiers())
		// && !Modifier.isFinal(binding.getDeclaringClass()
		// .getModifiers()) && !isStatic; // &&;

		// for (IAnnotationBinding annotationBindings : binding
		// .getAnnotations()) {
		// final String fqn = annotationBindings.getAnnotationType()
		// .getQualifiedName();
		// if (EXEC_PERMISSIONS.equals(fqn)) {
		// for (IMemberValuePairBinding memVal : annotationBindings
		// .getAllMemberValuePairs()) {
		// final Object[] threads = (Object[]) memVal.getValue();
		// final ITypeBinding[] threadTypes = new ITypeBinding[threads.length];
		// for (int i = 0; i < threads.length; i++) {
		// threadTypes[i] = (ITypeBinding) threads[i];
		// }
		// }
		// }
		// }
		// information.initialize(method, binding, upperDeclarations,
		// isStatic, isOverriddable, calls, isAnnotated,
		// executionClasses);
		// }
	}

	/**
	 * @param lexicalScope
	 * @param semanticalScope
	 * @throws CoreException
	 */
	private TypeInformation processTypeDeclaration(final IResource source,
			final ASTNode lexicalScope, ITypeBinding semanticalScope,
			CircullarReferenceDiagnostics circullarReferenceDiagnostics)
			throws CircullarTypeReference, CoreException {
		final TypeInformation information = getTypeInformation(semanticalScope);

		if (!information.isInitialized()) {
			if (information.isProcessing()) {
				throw new CircullarTypeReference(information);
			}
			try {
				information.markProcessing();

				final LexicalContext context = new LexicalContext(lexicalScope);
				if (lexicalScope instanceof TypeDeclaration) {
					TypeDeclaration lex = (TypeDeclaration) lexicalScope;
					mapSuperClassToNode(context, lex);
					mapSuperInterfacesToNodes(context, lex);
					mapAnnotationsToNodes(context, lex);
				}

				boolean valid = true;

				// Pre-process annotations
				IAnnotationBinding threadMarkerAnnotation = null;
				IAnnotationBinding execPermissionsAnnotation = null;
				for (IAnnotationBinding annotation : semanticalScope
						.getAnnotations()) {
					final String qualifiedName = annotation.getAnnotationType()
							.getQualifiedName();
					if (Annotations.THREAD_MARKER.equals(qualifiedName)) {
						threadMarkerAnnotation = annotation;
					} else if (Annotations.EXEC_PERMISSIONS
							.equals(qualifiedName)) {
						execPermissionsAnnotation = annotation;
					}
				}

				if (threadMarkerAnnotation != null
						&& execPermissionsAnnotation != null) {
					reportError(source, context.get(semanticalScope),
							"Type cannot be both thread marker and have execution permissions defined");
					valid = false;
					return information;
				}

				// Process modifiers
				boolean isFinal = Modifier.isFinal(semanticalScope
						.getModifiers());

				// Process super class
				final ITypeBinding superclass = semanticalScope.getSuperclass();
				TypeInformation superInformation = null;
				if (superclass != null) {
					superInformation = processTypeReference(context
							.get(semanticalScope), information, context
							.get(superclass), superclass, source,
							circullarReferenceDiagnostics);
					valid &= (superInformation != null);
				}

				// Process all super interfaces
				final ITypeBinding[] superinterfaces = semanticalScope
						.getInterfaces();
				final TypeInformation[] superInterfacesInformation = new TypeInformation[superinterfaces.length];
				for (int i = 0; i < superInterfacesInformation.length; ++i) {
					superInterfacesInformation[i] = processTypeReference(
							context.get(semanticalScope), information, context
									.get(superinterfaces[i]),
							superinterfaces[i], source,
							circullarReferenceDiagnostics);
					valid &= (superInterfacesInformation[i] != null);
				}

				// Validate class
				// - thread marker
				boolean isThreadMarker = false;
				if (threadMarkerAnnotation != null) {
					if (semanticalScope.isInterface()
							&& !semanticalScope.isAnnotation()) {
						isThreadMarker = true;
						for (int i = 0; i < superInterfacesInformation.length; ++i) {
							if (superInterfacesInformation[i] == null)
								continue;

							if (!superInterfacesInformation[i].isThreadMarker()) {
								reportError(source, context
										.get(superInterfacesInformation[i]
												.getSourceType()),
										"Super interface of thread marker must be thread marker");
								valid = false;
							}
						}
					} else {
						reportError(source, context.get(semanticalScope),
								"Thread markers might only be interfaces");
						valid = false;
					}
				}

				// - if no thread marker and ExecPermissions
				TypeInformation[] markers = null;
				if (threadMarkerAnnotation == null
						&& execPermissionsAnnotation != null) {
					final ITypeBinding[] threadMarkersList = getThreadMarkersList(execPermissionsAnnotation);
					markers = new TypeInformation[threadMarkersList.length];
					for (int i = 0; i < threadMarkersList.length; ++i) {
						TypeInformation ti = processTypeDeclaration(source,
								context.get(execPermissionsAnnotation),
								threadMarkersList[i],
								circullarReferenceDiagnostics);
						if (!ti.isThreadMarker()) {
							reportError(source, context
									.get(execPermissionsAnnotation),
									"Permissions must be set in terms of thread markers");
							valid = false;
						}
						markers[i] = ti;
					}
				}

				information.initialize(valid, isFinal, isThreadMarker, markers);

			} finally {
				information.markProcessed();
			}
		}
		return information;
	}

	/**
	 * @param classContext
	 * @param classInformation
	 * @param superContext
	 * @param superType
	 * @param circullarReferenceDiagnostics
	 * @throws CircullarTypeReference
	 * @throws CoreException
	 */
	private TypeInformation processTypeReference(final ASTNode classContext,
			final TypeInformation classInformation, final ASTNode superContext,
			final ITypeBinding superType, IResource source,
			CircullarReferenceDiagnostics circullarReferenceDiagnostics)
			throws CircullarTypeReference, CoreException {
		try {
			return processTypeDeclaration(source, superContext, superType,
					circullarReferenceDiagnostics);
		} catch (CircullarTypeReference exception) {
			final TypeInformation ti = exception.getInitialClass();
			if (ti != classInformation) {
				exception.pushClass(classInformation);
				throw exception;
			} else {
				reportError(source, classContext, circullarReferenceDiagnostics
						.getMessage());
				return null;
			}
		}
	}

	private void mapSuperClassToNode(LexicalContext context,
			TypeDeclaration type) {
		final Type superType = type.getSuperclassType();
		final ITypeBinding binding = superType == null ? null : superType
				.resolveBinding();
		if (superType != null)
			context.put(binding, superType);
	}

	private void mapSuperInterfacesToNodes(final LexicalContext context,
			TypeDeclaration type) {
		for (Object declaration : type.superInterfaceTypes()) {
			final Type superType = (Type) declaration;
			final ITypeBinding binding = superType.resolveBinding();
			context.put(binding, superType);
		}
	}

	/**
	 * Maps annotations to nodes by fully qualified names of annotation classes.
	 * Java prohibits multiple annotations of the same class for single element
	 * 
	 * @param annotated
	 *            The annotated element
	 */
	private void mapAnnotationsToNodes(final LexicalContext context,
			final ASTNode annotated) {
		annotated.accept(new ASTVisitor() {

			@Override
			public boolean visit(MarkerAnnotation node) {
				if (node.getParent() == annotated) {
					context.put(node.resolveAnnotationBinding(), node);
				}
				return false;
			}

			@Override
			public boolean visit(NormalAnnotation node) {
				if (node.getParent() == annotated) {
					context.put(node.resolveAnnotationBinding(), node);
				}
				return false;
			}

			@Override
			public boolean visit(SingleMemberAnnotation node) {
				if (node.getParent() == annotated) {
					context.put(node.resolveAnnotationBinding(), node);
				}
				return false;
			}

		});
	}

	/**
	 * Gets the list of interfaces in Read/Write/Exec-Permissions annotations
	 * 
	 * @param annotation
	 *            The annotation containing the list. It must be one of
	 *            {@link ReadPermissions}, {@link WritePermissions},
	 *            {@link ExecPermissions}.
	 * @return In case the list is valid (that is, all elements are thread
	 *         markers) then the list of markers (probably of zero length),
	 *         <code>null</code> otherwise.
	 *         <p>
	 *         In case error occurs, the method reports it.
	 */
	private ITypeBinding[] getThreadMarkersList(IAnnotationBinding annotation) {
		ITypeBinding[] result = null;
		for (IMemberValuePairBinding memberValue : annotation
				.getAllMemberValuePairs()) {
			Object[] markers = (Object[]) memberValue.getValue();
			result = new ITypeBinding[markers.length];
			for (int i = 0; i < markers.length; ++i) {
				ITypeBinding marker = (ITypeBinding) markers[i];
				result[i] = marker;
			}
		}
		return result;
	}

	private void reportError(IResource resource, ASTNode node, String string)
			throws CoreException {
		// IResource src = ((AnnatashaVisitor) node
		// .getProperty(ANNATASHA_VISITOR_PROPERTY)).getResource();
		IMarker marker = resource.createMarker(AnnatashaCore.MARKER_TYPE);
		marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		marker.setAttribute(IMarker.MESSAGE, string);
		marker.setAttribute(IMarker.CHAR_START, node.getStartPosition());
		marker.setAttribute(IMarker.CHAR_END, node.getStartPosition() + node.getLength());
		// TODO Auto-generated method stub

	}

	private final Map<ITypeBinding, TypeInformation> typesMap = new HashMap<ITypeBinding, TypeInformation>();

	private TypeInformation getTypeInformation(ITypeBinding typeBinding) {
		TypeInformation typeInformation = typesMap.get(typeBinding);
		if (typeInformation == null) {
			typeInformation = new TypeInformation(typeBinding);
			typesMap.put(typeBinding, typeInformation);
		}
		return typeInformation;
	}

	// private static MethodInformation getMethodInformation(MethodDeclaration
	// node) {
	// Object information = node.getProperty(METHOD_INFO);
	// if (information == null) {
	// information = new MethodInformation();
	// node.setProperty(METHOD_INFO, information);
	// }
	// return (MethodInformation) information;
	// }
}