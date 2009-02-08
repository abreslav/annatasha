package com.google.code.annatasha.validator.internal.build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IMemberValuePairBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import com.google.code.annatasha.validator.internal.analysis.FieldInformation;
import com.google.code.annatasha.validator.internal.analysis.MethodInformation;
import com.google.code.annatasha.validator.internal.analysis.Permissions;
import com.google.code.annatasha.validator.internal.analysis.TypeInformation;

public class ValidationVisitor implements ITaskVisitor {

	private final Map<IBinding, Object> resolved = new HashMap<IBinding, Object>();
	private final Set<IBinding> underConstruction = new HashSet<IBinding>();

	private final Map<IBinding, TaskNode> bindings;

	public ValidationVisitor(final Map<IBinding, TaskNode> bindings) {
		this.bindings = bindings;
	}

	public void visit(TypeTaskNode typeTask) {
		try {
			getTypeInfo(typeTask);
		} catch (CircularReferenceException e) {
			// XXX FIX Circular references handling
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void visit(MethodTaskNode methodTask) {
		try {
			getMethodInfo(methodTask);
		} catch (CircularReferenceException e) {
			// XXX FIX Circular references handling
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void visit(FieldTaskNode fieldTask) {
		getFieldInfo(fieldTask);
	}

	private TypeInformation getTypeInfo(TypeTaskNode task)
			throws CircularReferenceException {
		final ITypeBinding binding = task.getBinding();

		TypeInformation typeInformation = (TypeInformation) resolved
				.get(binding);
		if (typeInformation == null) {
			if (underConstruction.contains(binding)) {
				throw new CircularReferenceException(binding);
			}
			try {
				underConstruction.add(binding);

				int[] flags = new int[1];
				flags[0] = getTypeKind(task);
				final TypeInformation superClassInformation = flags[0] == TypeInformation.K_CLASS ? getSuperTypeInfo(task)
						: null;
				final TypeInformation[] interfacesInformation = getInterfacesTypeInfo(
						task, flags);
				final Permissions execPermissions = processTypeAnnotations(
						task, interfacesInformation, flags);

				typeInformation = new TypeInformation(binding
						.getQualifiedName(), flags[0], superClassInformation,
						interfacesInformation, execPermissions);
				resolved.put(binding, typeInformation);

			} finally {
				underConstruction.remove(binding);
			}

		}
		return typeInformation;
	}

	private MethodInformation getMethodInfo(MethodTaskNode methodTask)
			throws CircularReferenceException {
		IMethodBinding binding = methodTask.getBinding();

		ITypeBinding typeBinding = binding.getDeclaringClass();
		TypeInformation typeInfo = getTypeInfo(getTypeTaskNode(methodTask
				.getResource(), methodTask.getNode(), typeBinding));

		MethodInformation result = (MethodInformation) resolved.get(binding);
		if (result == null) {
			MethodDeclaration node = null;
			if (methodTask.getNode() instanceof MethodDeclaration
					&& ((MethodDeclaration) methodTask.getNode())
							.resolveBinding() == binding) {
				node = (MethodDeclaration) methodTask.getNode();
			}

			Permissions typeInheritedExecPermissions = typeInfo
					.getExecPermissions();
			Permissions selfExecPermissions = Permissions.Anonymous;
			for (IAnnotationBinding annotation : binding.getAnnotations()) {
				if (ClassNames.EXEC_PERMISSIONS.equals(annotation
						.getAnnotationType().getQualifiedName())) {
					selfExecPermissions = processPermissionsAnnotation(
							methodTask, annotation);
				}
			}

			MethodInformation superDefinition = null;
			for (ITypeBinding superClass = typeBinding.getSuperclass(); superClass != null; superClass = superClass
					.getSuperclass()) {
				for (IMethodBinding superMethodBinding : superClass
						.getDeclaredMethods()) {
					if (binding.overrides(superMethodBinding)) {
						superDefinition = getMethodInfo(getMethodTaskNode(
								methodTask.getResource(), methodTask.getNode(),
								superMethodBinding));
						break;
					}
				}
			}

			ArrayList<MethodInformation> superDeclarations = new ArrayList<MethodInformation>();
			Queue<ITypeBinding> ifacesQueue = new ConcurrentLinkedQueue<ITypeBinding>(
					Arrays.asList(typeBinding.getInterfaces()));
			Set<ITypeBinding> ifacesInQueue = new HashSet<ITypeBinding>();
			while (!ifacesQueue.isEmpty()) {
				ITypeBinding iface = ifacesQueue.poll();
				MethodInformation localSuperDeclaration = null;
				for (IMethodBinding superMethodBinding : iface
						.getDeclaredMethods()) {
					if (binding.overrides(superMethodBinding)) {
						localSuperDeclaration = getMethodInfo(getMethodTaskNode(
								methodTask.getResource(), methodTask.getNode(),
								superMethodBinding));
						break;
					}
				}
				if (localSuperDeclaration != null) {
					superDeclarations.add(localSuperDeclaration);
				} else {
					Set<ITypeBinding> toAdd = new HashSet<ITypeBinding>(Arrays
							.asList(iface.getInterfaces()));
					toAdd.removeAll(ifacesInQueue);
					ifacesQueue.addAll(toAdd);
					ifacesInQueue.addAll(toAdd);
				}
			}

			Permissions execPermissions = selfExecPermissions.isAnonymous() ? typeInheritedExecPermissions
					: selfExecPermissions;
			if (superDefinition != null) {
				if (!execPermissions.contain(superDefinition.getExecPermissions())) {
					reportError(methodTask.getResource(), methodTask.getNode(), Error.MethodPermissionsMustIncludeInherited);
				}
			}
			for (MethodInformation superDeclaration : superDeclarations) {
				if (!execPermissions.contain(superDeclaration.getExecPermissions())) {
					reportError(methodTask.getResource(), methodTask.getNode(), Error.MethodPermissionsMustIncludeInherited);
				}
			}

			// If we've got source, we should check access rules
			if (node != null && node.getBody() != null) {
				final Set<IVariableBinding> readAccess = new HashSet<IVariableBinding>();
				final Set<IVariableBinding> writeAccess = new HashSet<IVariableBinding>();
				final Set<IMethodBinding> execAccess = new HashSet<IMethodBinding>();

				AccessBuilder builder = new AccessBuilder(readAccess,
						writeAccess, execAccess);
				node.getBody().accept(builder);

				for (IVariableBinding read : readAccess) {
					FieldInformation field = getFieldInfo(getFieldTaskNode(
							methodTask.getResource(), methodTask.getNode(),
							read));
					if (!execPermissions
							.mightAccess(field.getReadPermissions())) {
						reportError(methodTask.getResource(), methodTask
								.getNode(),
								Error.MethodAttemptsToReadInaccessibleVariable);
					}
				}

				for (IVariableBinding write : writeAccess) {
					FieldInformation field = getFieldInfo(getFieldTaskNode(
							methodTask.getResource(), methodTask.getNode(),
							write));
					if (!execPermissions.mightAccess(field
							.getWritePermissions())) {
						reportError(methodTask.getResource(), methodTask
								.getNode(),
								Error.MethodAttemptsToWriteInaccessibleVariable);
					}
				}

				for (IMethodBinding exec : execAccess) {
					MethodInformation method = getMethodInfo(getMethodTaskNode(
							methodTask.getResource(), methodTask.getNode(),
							exec));
					if (!execPermissions.mightAccess(method
							.getExecPermissions())) {
						reportError(methodTask.getResource(), methodTask
								.getNode(),
								Error.MethodAttemptsToExecInaccessibleMethod);
					}
				}
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private FieldInformation getFieldInfo(FieldTaskNode fieldTask) {
		IVariableBinding binding = fieldTask.getBinding();

		FieldInformation result = (FieldInformation) resolved.get(binding);
		if (result == null) {
			VariableDeclarationFragment fragment = (VariableDeclarationFragment) fieldTask
					.getNode();
			FieldDeclaration field = (FieldDeclaration) fragment.getParent();

			ITypeBinding type = binding.getType();
			TypeInformation information = null;
			try {
				information = getTypeInfo(getTypeTaskNode(fieldTask
						.getResource(), fieldTask.getNode(), type));
			} catch (CircularReferenceException e) {
			}
			Permissions readPermissions = Permissions.Anonymous;
			Permissions writePermissions = Permissions.Anonymous;

			List<IExtendedModifier> modifiers = field.modifiers();
			for (IExtendedModifier modifier : modifiers) {
				if (modifier instanceof Annotation) {
					Annotation annotation = (Annotation) modifier;
					final String fqn = annotation.getTypeName()
							.getFullyQualifiedName();
					if (ClassNames.READ_PERMISSIONS.equals(fqn)) {
						try {
							readPermissions = processPermissionsAnnotation(
									fieldTask, annotation
											.resolveAnnotationBinding());
						} catch (CircularReferenceException e) {
						}
					} else if (ClassNames.WRITE_PERMISSIONS.equals(fqn)) {
						try {
							writePermissions = processPermissionsAnnotation(
									fieldTask, annotation
											.resolveAnnotationBinding());
						} catch (CircularReferenceException e) {
						}
					}
					;
				}
			}
			result = new FieldInformation(information, readPermissions,
					writePermissions);

		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private Permissions processTypeAnnotations(TypeTaskNode task,
			final TypeInformation[] interfacesInformation, int[] flags)
			throws CircularReferenceException {
		final ITypeBinding binding = task.getBinding();
		Permissions execPermissions = Permissions.Anonymous;
		if (doesTheNodeCorrespondToTheType(task.getNode(), binding)) {
			TypeDeclaration typeDeclaration = (TypeDeclaration) task.getNode();
			List<IExtendedModifier> modifiers = typeDeclaration.modifiers();

			boolean isThreadMarker = false;
			boolean hasExecPermissions = false;
			boolean bothFlag = false;
			for (IExtendedModifier modifier : modifiers) {
				if (modifier instanceof Annotation) {
					final Annotation annotation = (Annotation) modifier;
					final String fqn = annotation.getTypeName()
							.getFullyQualifiedName();
					if (fqn.equals(ClassNames.THREAD_MARKER)) {
						isThreadMarker = true;
						flags[0] |= TypeInformation.F_MARKER;
						processThreadMarkerAnnotation(task, flags[0],
								interfacesInformation);
					} else if (ClassNames.EXEC_PERMISSIONS.equals(fqn)) {
						hasExecPermissions = true;
						execPermissions = processPermissionsAnnotation(task,
								annotation.resolveAnnotationBinding());
					}
					if (isThreadMarker && hasExecPermissions && !bothFlag) {
						bothFlag = true;
						reportError(task.getResource(), task.getNode(),
								Error.ThreadMarkerCannotSpecifyExecPermissions);
					}
				}
			}
		}
		return execPermissions;
	}

	private Permissions processPermissionsAnnotation(TaskNode task,
			final IAnnotationBinding annotationBinding)
			throws CircularReferenceException {
		// IAnnotationBinding annotationBinding = annotation
		// .resolveAnnotationBinding();
		IMemberValuePairBinding memValueBinding = annotationBinding
				.getAllMemberValuePairs()[0];
		ITypeBinding[] markersBindings = (ITypeBinding[]) memValueBinding
				.getValue();
		TypeInformation[] markers = new TypeInformation[markersBindings.length];
		for (int i = 0; i < markers.length; ++i) {
			// FIXME cyclic reference may occur in
			// incorrectly annotated program
			markers[i] = getTypeInfo(getTypeTaskNode(task.getResource(), task
					.getNode(), markersBindings[i]));
			if (!markers[i].isMarker()) {
				reportError(task.getResource(), task.getNode(),
						Error.NonThreadMarkerPermission);
			}
		}
		return new Permissions(Arrays.asList(markers));
	}

	private void processThreadMarkerAnnotation(TypeTaskNode task, int flags,
			final TypeInformation[] interfacesInformation) {
		if (!TypeInformation.isInterface(flags)) {
			reportError(task.getResource(), task.getNode(),
					Error.ThreadMarkerMustBeAnInterface);
		}
		boolean hasRunnable = false;
		for (int i = 0; i < interfacesInformation.length; ++i) {
			final boolean isRunnable = interfacesInformation[i]
					.getFullyQualifiedName().equals(ClassNames.RUNNABLE);
			final boolean isMarker = interfacesInformation[i].isMarker();

			hasRunnable |= isRunnable | isMarker;
			if (!isRunnable && !isMarker) {
				reportError(task.getResource(), task.getNode(),
						Error.ThreadMarkerSupertypeError);
			}
		}
		if (!hasRunnable) {
			reportError(task.getResource(), task.getNode(),
					Error.ThreadMarkerNotRunnable);

		}
	}

	private int getTypeKind(final TypeTaskNode task) {
		final ITypeBinding binding = task.getBinding();
		int kind = TypeInformation.K_CLASS;
		if (binding.isAnnotation()) {
			kind = TypeInformation.K_ANNOTATION;
		} else if (binding.isEnum()) {
			kind = TypeInformation.K_ENUM;
		} else if (binding.isInterface()) {
			kind = TypeInformation.K_INTERFACE;
		}
		return kind;
	}

	private TypeInformation getSuperTypeInfo(TypeTaskNode task)
			throws CircularReferenceException {
		ITypeBinding superClassBinding = task.getBinding().getSuperclass();
		if (superClassBinding == null) {
			return null;
		}
		ASTNode superClassNode = task.getNode();
		if (task.getNode() instanceof TypeDeclaration) {
			superClassNode = ((TypeDeclaration) task.getNode())
					.getSuperclassType();
		}
		TypeInformation information = getTypeInfo(getTypeTaskNode(task
				.getResource(), superClassNode, superClassBinding));
		return information;
	}

	private TypeInformation[] getInterfacesTypeInfo(TypeTaskNode task,
			int[] flags) throws CircularReferenceException {
		final TypeInformation[] interfacesInformation;
		final ASTNode node = task.getNode();
		final ITypeBinding binding = task.getBinding();
		if (doesTheNodeCorrespondToTheType(node, binding)) {
			TypeDeclaration typeDeclaration = (TypeDeclaration) node;
			Type[] superInterfacesNodes = (Type[]) typeDeclaration
					.getStructuralProperty(TypeDeclaration.SUPER_INTERFACES_PROPERTY);
			interfacesInformation = new TypeInformation[superInterfacesNodes.length];
			for (int i = 0; i < superInterfacesNodes.length; ++i) {
				ITypeBinding interfaceBinding = superInterfacesNodes[i]
						.resolveBinding();
				TypeTaskNode interfaceTask = getTypeTaskNode(
						task.getResource(), superInterfacesNodes[i],
						interfaceBinding);
				interfacesInformation[i] = getTypeInfo(interfaceTask);
			}
		} else {
			ITypeBinding[] interfaces = binding.getInterfaces();
			interfacesInformation = new TypeInformation[interfaces.length];
			for (int i = 0; i < interfaces.length; ++i) {
				TypeTaskNode interfaceTask = getTypeTaskNode(
						task.getResource(), node, interfaces[i]);
				interfacesInformation[i] = getTypeInfo(interfaceTask);
			}
		}
		for (int i = 0; i < interfacesInformation.length; ++i) {
			if (interfacesInformation[i].isRunnable()) {
				flags[0] |= TypeInformation.F_RUNNABLE;
				break;
			}
		}
		return interfacesInformation;
	}

	private boolean doesTheNodeCorrespondToTheType(final ASTNode node,
			final ITypeBinding binding) {
		return node instanceof TypeDeclaration
				&& ((TypeDeclaration) node).resolveBinding() == binding;
	}

	private TypeTaskNode getTypeTaskNode(IResource resource, ASTNode node,
			ITypeBinding typeBinding) {
		TypeTaskNode result = (TypeTaskNode) bindings.get(typeBinding);
		if (result == null) {
			result = new TypeTaskNode(resource, node, typeBinding);
		}
		return result;
	}

	private MethodTaskNode getMethodTaskNode(IResource resource, ASTNode node,
			IMethodBinding binding) {
		MethodTaskNode result = (MethodTaskNode) bindings.get(binding);
		if (result == null) {
			result = new MethodTaskNode(resource, node, binding);
		}
		return result;
	}

	private FieldTaskNode getFieldTaskNode(IResource resource, ASTNode node,
			IVariableBinding binding) {
		FieldTaskNode result = (FieldTaskNode) bindings.get(binding);
		if (result == null) {
			result = new FieldTaskNode(resource, node, binding);
		}
		return result;
	}

	private void reportError(IResource resource, ASTNode node, Error code) {
		// TODO Auto-generated method stub

	}

	// private TypeDeclaration getTypeDeclarationNode(final ITypeBinding
	// binding,
	// final ASTNode node) {
	// TaskNode taskNode = bindings.get(binding);
	// if (taskNode != null) {
	// return (TypeDeclaration) taskNode.getNode();
	// }
	// return null;
	// }

	private static interface ClassNames {
		final static String THREAD_MARKER = "com.google.code.annatasha.annotations.ThreadMarker";
		final static String THREAD_STARTER = "com.google.code.annatasha.annotations.ThreadStarter";

		final static String READ_PERMISSIONS = "com.google.code.annatasha.annotations.Field.ReadPermissions";
		final static String WRITE_PERMISSIONS = "com.google.code.annatasha.annotations.Field.WritePermissions";
		final static String EXEC_PERMISSIONS = "com.google.code.annatasha.annotations.Method.ExecPermissions";

		final static String RUNNABLE = "java.lang.Runnable";
	}

	private enum Error {
		ThreadMarkerCannotSpecifyExecPermissions(0x1,
				"An type cannot both be thread marker and have execution permissions specified"), ThreadMarkerMustBeAnInterface(
				0x2, "Thread marker must be an interface"), ThreadMarkerSupertypeError(
				0x3,
				"Thread marker may only extend thread markers or java.lang.Runnable"), ThreadMarkerNotRunnable(
				0x4, "Thread marker must extend java.lang.Runnable"), NonThreadMarkerPermission(
				0x5, "Only thread markers may specify permissions"), MethodAttemptsToReadInaccessibleVariable(
				0x6, "Method attempts to read inaccessible variable"), MethodAttemptsToWriteInaccessibleVariable(
				0x7, "Method attempts to write inaccessible variable"), MethodAttemptsToExecInaccessibleMethod(
				0x8, "Method attempts to execute inaccessible method"), MethodPermissionsMustIncludeInherited(0x9, "Method permissions must be wider than inherited permissions");

		public final int code;
		public final String message;

		Error(int code, String message) {
			this.code = code;
			this.message = message;
		}
	}
}
