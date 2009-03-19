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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import com.google.code.annatasha.validator.core.AnnatashaCore;
import com.google.code.annatasha.validator.internal.build.tasks.FieldTaskNode;
import com.google.code.annatasha.validator.internal.build.tasks.ITaskVisitor;
import com.google.code.annatasha.validator.internal.build.tasks.MethodTaskNode;
import com.google.code.annatasha.validator.internal.build.tasks.TaskNode;
import com.google.code.annatasha.validator.internal.build.tasks.TypeTaskNode;
import com.google.code.annatasha.validator.internal.structures.FieldInformation;
import com.google.code.annatasha.validator.internal.structures.MethodInformation;
import com.google.code.annatasha.validator.internal.structures.Permissions;
import com.google.code.annatasha.validator.internal.structures.TypeInformation;

public class ValidationVisitor implements ITaskVisitor {

	private final Map<IBinding, Object> resolved = new HashMap<IBinding, Object>();
	private final Set<IBinding> underConstruction = new HashSet<IBinding>();

	private final Set<IVariableBinding> threadStarters = new HashSet<IVariableBinding>();
	private final Map<IBinding, TaskNode> bindings;
	private int errorsCounter;

	public ValidationVisitor(final Map<IBinding, TaskNode> bindings) {
		this.bindings = bindings;
	}

	public void visit(TypeTaskNode typeTask) throws CoreException {
		try {
			TypeInformation information = getTypeInfo(typeTask.getBinding());
			validateTypeInformation(information, typeTask.getBinding(),
					typeTask.getResource(), typeTask.getNode());
		} catch (CircularReferenceException e) {
			// XXX FIX Circular references handling
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void visit(MethodTaskNode methodTask) throws CoreException {
		try {
			MethodInformation information = getHeadMethodInfo(methodTask
					.getBinding());
			validateMethodInformation(information, methodTask.getResource(),
					(MethodDeclaration) methodTask.getNode());
			validateMethodBody(methodTask.getBinding(), methodTask
					.getResource(), (MethodDeclaration) methodTask.getNode());
		} catch (CircularReferenceException e) {
			// XXX FIX Circular references handling
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	private void validateMethodInformation(MethodInformation information,
			IResource resource, MethodDeclaration node) throws CoreException,
			CircularReferenceException {

		Annotation annotation = null;
		List<IExtendedModifier> modifiers = node.modifiers();
		for (IExtendedModifier modifier : modifiers) {
			if (modifier instanceof Annotation
					&& ((Annotation) modifier).resolveAnnotationBinding()
							.getAnnotationType().getQualifiedName().equals(
									ClassNames.EXEC_PERMISSIONS)) {
				annotation = (Annotation) modifier;
			}
		}

		// Check ExecPermissions validity
		if (!information.areExecPermissionsValid()) {
			if (annotation != null) {
				if ((information.isEntryPoint() || information
						.isInheritedFromEntryPoint())
						&& information.getType().isThreadStarter()) {
					reportError(resource, annotation,
							Error.ExecPermissionsInThreadStarterMethod);
				} else {
					reportError(resource, annotation,
							Error.PermissionsMustEnumerateThreadMarkers);
				}
			} else {
				reportError(resource, node, Error.InternalError);
			}
		}
		if (!information.areInheritedExecPermissionsValid()) {
			reportError(resource, annotation == null ? node.getName()
					: annotation, Error.ExecPermissionsInheritedViolation);
		}

		// Thread starters
		if (!information.areThreadStartersValid()) {
			boolean localProblem = false;
			IMethodBinding methodBinding = node.resolveBinding();
			ITypeBinding[] parameterTypes = methodBinding.getParameterTypes();
			for (int p : information.getThreadStarterParameters()) {
				TypeInformation typeInfo = getTypeInfo(parameterTypes[p]);
				if (!typeInfo.isEntryPoint()
						&& !typeInfo.isInheritedFromEntryPoint()) {
					reportError(resource, (ASTNode) node.parameters().get(p),
							Error.ThreadStarterArgumentInvalid);
					localProblem = true;
				}
			}
			if (!localProblem) {
				reportError(resource, node.getName(),
						Error.ThreadStarterArgumentsDiffer);
			}
		}

		// if (!information.isValid() && errorsCounter == storedErrorCounter) {
		// reportError(resource, node,
		// Error.InternalErrorInvalidObjectNotReported);
		// }

	}

	public void visit(FieldTaskNode fieldTask) throws CoreException {
		try {
			FieldInformation information = getFieldInfo(fieldTask.getBinding());
			validateFieldInfo(information, fieldTask.getResource(),
					(FieldDeclaration) fieldTask.getNode());
		} catch (CircularReferenceException e) {
			// XXX FIX Circular references handling
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void validateFieldInfo(FieldInformation information,
			IResource resource, FieldDeclaration node) throws CoreException {
		if (!information.areReadPermissionsValid()) {
			List<IExtendedModifier> modifiers = node.modifiers();
			Annotation annotation = null;
			for (IExtendedModifier modifier : modifiers) {
				if (modifier instanceof Annotation
						&& ((Annotation) modifier).resolveAnnotationBinding()
								.getAnnotationType().getQualifiedName().equals(
										ClassNames.READ_PERMISSIONS)) {
					annotation = (Annotation) modifier;
				}
			}
			if (annotation != null) {
				reportError(resource, annotation,
						Error.PermissionsMustEnumerateThreadMarkers);
			} else {
				reportError(resource, node, Error.InternalError);
			}

		}
		if (!information.areWritePermissionsValid()) {
			List<IExtendedModifier> modifiers = node.modifiers();
			Annotation annotation = null;
			for (IExtendedModifier modifier : modifiers) {
				if (modifier instanceof Annotation
						&& ((Annotation) modifier).resolveAnnotationBinding()
								.getAnnotationType().getQualifiedName().equals(
										ClassNames.WRITE_PERMISSIONS)) {
					annotation = (Annotation) modifier;
				}
			}
			if (annotation != null) {
				reportError(resource, annotation,
						Error.PermissionsMustEnumerateThreadMarkers);
			} else {
				reportError(resource, node, Error.InternalError);
			}

		}

	}

	private class ResolvePermissionsResult {
		public final Permissions execPermissions;
		public final boolean isValid;

		public ResolvePermissionsResult(Permissions execPermissions,
				boolean isValid) {
			this.execPermissions = execPermissions;
			this.isValid = isValid;
		}
	}

	TypeInformation getTypeInfo(ITypeBinding binding)
			throws CircularReferenceException {
		TypeInformation typeInformation = (TypeInformation) resolved
				.get(binding);
		if (typeInformation == null) {
			if (underConstruction.contains(binding)) {
				throw new CircularReferenceException(binding);
			}
			try {
				underConstruction.add(binding);

				TypeInformation superClassInformation = null;
				TypeInformation[] interfacesInformation = null;

				boolean isInterface = false;
				boolean isClass = false;
				boolean isAnnotationOrEnum = false;

				// Whether the class is EntryPoint type.
				// The type is EntryPoint type if it contains
				// an EntryPoint method.
				// Note! Currently all these types are hard-coded:
				// Callable, Runnable, Thread.
				boolean isEntryPoint = false;
				boolean isInheritedFromEntryPoint = false;

				Permissions execPermissions = null;

				// TypeInformation builder validity flags

				// Annotation (and enum) should not be:
				// 1. marked with ExecPermission
				// 2. marked with ThreadMarker
				boolean isAnnotationOrEnumValid = true;

				// True when ExecPermissions's syntax is valid and
				// only ThreadMarkers occur in the list of markers.
				boolean isExecPermissionValid = true;

				// The class is considered to be thread starter if
				// it's derived from one or more thread markers.
				// The class is valid thread starter if:
				// 1. it's thread starter
				// 2. it implements exactly one ThreadMarker (note,
				// that the ThreadMarker itself may be derived from other
				// ThreadMarkers)
				// 3. it's derived from an entry point class (directly, or
				// indirectly)
				// The thread starter is invalid if either of 2-3 is violated.
				boolean isThreadStarter = false;
				boolean isInvalidThreadStarter = false;

				// The type is considered to be thread marker if
				// it's marked with @ThreadMarker annotation
				boolean isThreadMarker = false;
				// The thread marker is valid if all of the
				// following takes place:
				// 1. it's an interface
				// 2. it's not derived or is only derived from thread markers
				// 3. it has no methods inside
				// 4. it has no ExecPermissions annotations
				boolean isThreadMarkerValid = true;

				List<TypeInformation> superThreadMarkers = new ArrayList<TypeInformation>();

				boolean isValid = true;

				isInterface = binding.isInterface() && !binding.isAnnotation();
				isClass = binding.isClass();
				isAnnotationOrEnum = binding.isEnum() || binding.isAnnotation();

				if (isClass)
					superClassInformation = getSuperTypeInfo(binding);
				interfacesInformation = getInterfacesTypeInfo(binding);

				for (int i = 0; i < ClassNames.EntryPoints.length; ++i) {
					if (ClassNames.EntryPoints[i].className.equals(binding
							.getQualifiedName())) {
						isEntryPoint = true;
					}
				}

				isInheritedFromEntryPoint = false;
				if (superClassInformation != null) {
					isInheritedFromEntryPoint = superClassInformation
							.isEntryPoint()
							|| superClassInformation
									.isInheritedFromEntryPoint();
				}

				boolean hasSuperThreadMarkers;
				for (int i = 0; i < interfacesInformation.length; ++i) {
					isInheritedFromEntryPoint |= interfacesInformation[i]
							.isEntryPoint()
							|| interfacesInformation[i]
									.isInheritedFromEntryPoint();
					if (interfacesInformation[i].isThreadMarker()) {
						superThreadMarkers.add(interfacesInformation[i]);
					}
				}
				hasSuperThreadMarkers = superThreadMarkers.size() != 0;

				isThreadStarter = isClass && hasSuperThreadMarkers;
				isInvalidThreadStarter = isThreadStarter
						&& (superThreadMarkers.size() != 1 || !isInheritedFromEntryPoint);

				boolean hasThreadMarkerMark = false;
				for (IAnnotationBinding annotation : binding.getAnnotations()) {
					if (ClassNames.EXEC_PERMISSIONS.equals(annotation
							.getAnnotationType().getQualifiedName())) {
						final ResolvePermissionsResult result = processPermissions(annotation);
						isExecPermissionValid = result.isValid;
						execPermissions = result.execPermissions;
					} else if (ClassNames.THREAD_MARKER.equals(annotation
							.getAnnotationType().getQualifiedName())) {
						hasThreadMarkerMark = true;
					}
				}
				isThreadMarker = (isInterface && hasSuperThreadMarkers)
						|| hasThreadMarkerMark;
				isThreadMarkerValid = !isThreadMarker
						|| (isInterface
								&& superThreadMarkers.size() == interfacesInformation.length
								&& binding.getDeclaredMethods().length == 0
								&& execPermissions == null && hasThreadMarkerMark);

				isAnnotationOrEnumValid = !isAnnotationOrEnum
						|| (execPermissions == null && !isThreadMarker);

				isValid &= execPermissions == null || isExecPermissionValid;
				isValid &= isThreadMarkerValid && isAnnotationOrEnumValid;

				TypeInformation[] stm = new TypeInformation[superThreadMarkers
						.size()];
				superThreadMarkers.toArray(stm);

				typeInformation = new TypeInformation(binding, isClass,
						isInterface, isAnnotationOrEnum, isThreadMarker,
						isThreadStarter, !isInvalidThreadStarter, isEntryPoint,
						isInheritedFromEntryPoint, superClassInformation,
						interfacesInformation, stm, execPermissions,
						isExecPermissionValid, isValid);
				resolved.put(binding, typeInformation);
			} finally {
				underConstruction.remove(binding);
			}

		}
		return typeInformation;
	}

	private ResolvePermissionsResult processPermissions(
			IAnnotationBinding annotation) throws CircularReferenceException {
		ArrayList<TypeInformation> execs = new ArrayList<TypeInformation>();
		boolean isValid = annotation.getAllMemberValuePairs().length == 1;
		if (!isValid)
			return new ResolvePermissionsResult(null, false);

		Object value = annotation.getAllMemberValuePairs()[0].getValue();
		ITypeBinding[] types = null;
		if (value instanceof ITypeBinding) {
			types = new ITypeBinding[] { (ITypeBinding) value };
		} else {
			Object[] array = (Object[]) value;
			types = new ITypeBinding[array.length];
			for (int i = 0; i < types.length; ++i) {
				types[i] = (ITypeBinding) array[i];
			}
		}
		for (ITypeBinding type : types) {
			if (type.isInterface() && !type.isAnnotation()) {
				TypeInformation marker = getTypeInfo(type);
				isValid &= marker.isThreadMarker();
				if (marker.isThreadMarker()) {
					execs.add(marker);
				}
			} else {
				isValid = false;
			}
		}
		Permissions permissions = new Permissions(execs);

		return new ResolvePermissionsResult(permissions, isValid);
	}

	@SuppressWarnings("unchecked")
	private void validateTypeInformation(TypeInformation information,
			ITypeBinding typeBinding, IResource resource, ASTNode node)
			throws CoreException {
		TypeDeclaration declNode = (TypeDeclaration) node;

		// Check validity of supertypes.
		// If they are not and they are not in bindings map,
		// then report external error.
		// If they are not and ther are in bindings map,
		// this will be reported at the moment of visiting
		// appropriate TypeTaskNode.
		if (information.getSuperClass() != null
				&& !information.getSuperClass().isValid()
				&& !bindings.containsKey(information.getSuperClass()
						.getBinding())) {
			reportError(resource, declNode.getSuperclassType(),
					Error.ExternalTypeIsInvalid);
		}
		List<Type> superInterfacesNodesList = declNode.superInterfaceTypes();
		Type[] superInterfacesNodes = new Type[superInterfacesNodesList.size()];
		superInterfacesNodesList.toArray(superInterfacesNodes);

		for (TypeInformation superInterface : information.getSuperInterfaces()) {
			if (!superInterface.isValid()
					&& !bindings.containsKey(superInterface.getBinding())) {
				int i;
				for (i = 0; i < superInterfacesNodes.length; ++i) {
					if (superInterfacesNodes[i].resolveBinding()
							.getQualifiedName()
							.equals(superInterface.getName())) {
						reportError(resource, superInterfacesNodes[i],
								Error.ExternalTypeIsInvalid);
						break;
					}
				}
				if (i == superInterfacesNodes.length) {
					reportError(resource, node, Error.InternalError);
				}
			}
		}

		// Check thread starter validity
		if (information.isThreadStarter()) {
			if (information.getSuperThreadMarkers().length != 1) {
				reportError(resource, declNode.getName(),
						Error.ExactlyOneThreadMarkerExpectedForThreadStarter);
			}
			if (!information.isInheritedFromEntryPoint()) {
				reportError(resource, declNode.getName(),
						Error.ThreadStarterNotInheritedFromEntryPoint);
			}
		}

		// Check thread marker validity
		if (information.isThreadMarker()) {
			ASTNode tmNode = null;
			ASTNode exNode = null;
			List<ASTNode> modifiers = declNode.modifiers();
			for (ASTNode modif : modifiers) {
				if (modif instanceof Annotation) {
					Annotation a = (Annotation) modif;
					final String fqn = a.resolveAnnotationBinding()
							.getAnnotationType().getQualifiedName();
					if (ClassNames.THREAD_MARKER.equals(fqn)) {
						tmNode = a;
						break;
					} else if (ClassNames.EXEC_PERMISSIONS.equals(fqn)) {
						exNode = a;
					}
				}
			}
			if (tmNode == null) {
				reportError(resource, declNode.getName(),
						Error.ThreadMarkerMustBeSpecifiedExplicitly);
				tmNode = declNode.getName();
			}
			if (!information.isInterface()) {
				reportError(resource, tmNode,
						Error.ThreadMarkerMustBeAnInterface);
			} else if (information.getBinding().getDeclaredMethods().length != 0) {
				reportError(resource, tmNode,
						Error.ThreadMarkerMustHaveNoMethods);
			}
			if (information.getExecPermissions() != null) {
				reportError(resource, exNode,
						Error.ThreadMarkerCannotSpecifyExecPermissions);
			}
			List<Type> types = declNode.superInterfaceTypes();
			for (Type type : types) {
				TypeInformation sI;
				try {
					sI = getTypeInfo(type.resolveBinding());
					if (!sI.isThreadMarker()) {
						reportError(resource, type,
								Error.ThreadMarkerInvalidInheritance);
					}
				} catch (CircularReferenceException e) {
					assert false;
				}
			}
		}

		// Check ExecPermissions validity
		if (!information.areExecPermissionsValid()) {
			List<IExtendedModifier> modifiers = declNode.modifiers();
			Annotation annotation = null;
			for (IExtendedModifier modifier : modifiers) {
				if (modifier instanceof Annotation
						&& ((Annotation) modifier).getTypeName()
								.getFullyQualifiedName().equals(
										ClassNames.EXEC_PERMISSIONS)) {
					annotation = (Annotation) modifier;
				}
			}
			if (annotation != null) {
				reportError(resource, annotation,
						Error.PermissionsMustEnumerateThreadMarkers);
			} else {
				reportError(resource, node, Error.InternalError);
			}
		}
	}

	private MethodInformation getHeadMethodInfo(IMethodBinding binding)
			throws CircularReferenceException {
		MethodInformation result = (MethodInformation) resolved.get(binding);
		if (result == null) {
			if (underConstruction.contains(binding)) {
				throw new CircularReferenceException(binding);
			}
			try {
				underConstruction.add(binding);

				ITypeBinding typeBinding = binding.getDeclaringClass();
				TypeInformation typeInfo = getTypeInfo(typeBinding);
				ITypeBinding[] parameterTypes = binding.getParameterTypes();

				StringBuilder fullMethodNameBuilder = new StringBuilder(
						typeBinding.getQualifiedName() + "."
								+ binding.getName() + "(");
				for (int i = 0; i < parameterTypes.length; ++i) {
					fullMethodNameBuilder.append(
							parameterTypes[i].getQualifiedName()).append(",");
				}
				String fullMethodName = fullMethodNameBuilder.toString();

				boolean isEntryPoint = false;
				boolean isInheritedFromEntryPoint = false;

				// Execution permissions are set as described:
				// I. For non-EntryPoint-inherited
				// a. If ExecPermissions are specified, the permissions
				// are set to be ones, listed in ExecPermissions.
				// b. Otherwise, if super-definition exists, they are
				// taken from super-definition
				// c. If there is no super-definition, they are set to
				// Any thread marker
				// d. In any cases the permissions are validated to be
				// compatible with ones of super-declarations and
				// super-definition.
				// II. For one EntryPoint-inherited
				// a. In thread starters they are set to the context of
				// thread starter.
				// b. In other classes they have Any permissions.
				// c. In interfaces they might use ExecPermissions to
				// specify permissions for the EntryPoint-inherited class.
				// d. In any cases the permissions are validated to be
				// compatible with ones of super-declaration
				boolean isLocalExecPermissionValid = true;
				boolean isInheritedExecPermissionValid = true;
				Permissions execPermissions = null;

				boolean isThreadStarter = false;
				boolean isValid = true;

				MethodInformation superDefinition = getSuperDefinition(binding,
						typeBinding);

				HashMap<TypeInformation, TypeInformation> declarationTypeToParentType = new HashMap<TypeInformation, TypeInformation>();
				ArrayList<MethodInformation> superDeclarations = getSuperDeclarations(
						binding, typeBinding, declarationTypeToParentType);

				if (typeInfo.isEntryPoint()) {
					for (ClassNames.EntryPoint point : ClassNames.EntryPoints) {
						if (point.className.equals(typeInfo.getName())
								&& point.methodName.equals(binding.getName())) {
							isEntryPoint = true;
							break;
						}
					}
				}

				if (typeInfo.isInheritedFromEntryPoint()) {
					if (superDefinition != null
							&& (superDefinition.isEntryPoint() || superDefinition
									.isInheritedFromEntryPoint())) {
						isInheritedFromEntryPoint = true;
					}
					if (!isInheritedFromEntryPoint) {
						for (int i = 0; i < superDeclarations.size(); ++i) {
							if (superDeclarations.get(i).isEntryPoint()
									|| superDeclarations.get(i)
											.isInheritedFromEntryPoint()) {
								isInheritedFromEntryPoint = true;
								break;
							}
						}
					}
				}

				// Exec Permissions
				for (IAnnotationBinding annotation : binding.getAnnotations()) {
					if (ClassNames.EXEC_PERMISSIONS.equals(annotation
							.getAnnotationType().getQualifiedName())) {
						final ResolvePermissionsResult permResult = processPermissions(annotation);
						isLocalExecPermissionValid = permResult.isValid;
						execPermissions = permResult.execPermissions;
					}
				}

				isThreadStarter = typeInfo.isThreadStarter()
						&& (isEntryPoint || isInheritedFromEntryPoint);
				if (isThreadStarter) {
					if (execPermissions != null) {
						isLocalExecPermissionValid = false;
					}
					execPermissions = new Permissions(Arrays
							.asList(new TypeInformation[] { typeInfo
									.getSuperThreadMarkers()[0] }));
				} else if (execPermissions == null) {
					execPermissions = typeInfo.getExecPermissions();
				}
				if (execPermissions == null) {
					execPermissions = Permissions.Any;
				}

				if (!typeInfo.isThreadStarter()
						|| (!isEntryPoint && !isInheritedFromEntryPoint)) {
					if (superDefinition != null) {
						isInheritedExecPermissionValid &= superDefinition
								.getExecPermissions().mightAccess(
										execPermissions);
					}

					for (MethodInformation superDeclaration : superDeclarations) {
						isInheritedExecPermissionValid &= superDeclaration
								.getExecPermissions().mightAccess(
										execPermissions);
					}
				}

				// Thread starters
				boolean areParametersValid = true;
				ArrayList<Integer> tsParams = null;
				if (superDefinition != null) {
					tsParams = superDefinition.getThreadStarterParameters();
				}
				for (MethodInformation superDeclaration : superDeclarations) {
					ArrayList<Integer> localTs = superDeclaration
							.getThreadStarterParameters();
					if (tsParams != null) {
						if (tsParams.size() != localTs.size()) {
							areParametersValid = false;
						} else {
							for (int i = 0; i < tsParams.size(); ++i) {
								if (tsParams.get(i).intValue() != localTs
										.get(i).intValue()) {
									areParametersValid = false;
								}
							}
						}
					} else {
						tsParams = localTs;
					}
				}

				ArrayList<Integer> threadStarterParams;
				if (ThreadStartersParameters.containsKey(fullMethodName)) {
					threadStarterParams = ThreadStartersParameters
							.get(fullMethodName);
				} else {
					threadStarterParams = new ArrayList<Integer>();
					int paramsCount = binding.getParameterTypes().length;
					for (int i = 0; i < paramsCount; ++i) {
						for (IAnnotationBinding ann : binding
								.getParameterAnnotations(i)) {
							if (ClassNames.THREAD_STARTER.equals(ann
									.getAnnotationType().getQualifiedName())) {
								threadStarterParams.add(i);
								TypeInformation info = getTypeInfo(parameterTypes[i]);
								areParametersValid &= info.isEntryPoint()
										|| info.isInheritedFromEntryPoint();
							}
						}
					}
				}

				if (tsParams == null) {
					tsParams = threadStarterParams;
				} else {
					if (tsParams.size() != threadStarterParams.size()) {
						areParametersValid = false;
					} else {
						for (int i = 0; i < tsParams.size(); ++i) {
							if (tsParams.get(i).intValue() != threadStarterParams
									.get(i).intValue()) {
								areParametersValid = false;
							}
						}
					}
				}

				result = new MethodInformation(typeInfo, superDefinition,
						superDeclarations, execPermissions,
						isLocalExecPermissionValid,
						isInheritedExecPermissionValid, isEntryPoint,
						isInheritedFromEntryPoint, isThreadStarter,
						threadStarterParams, areParametersValid);

				resolved.put(binding, result);
			} finally {
				underConstruction.remove(binding);
			}
		}
		return result;
	}

	/**
	 * @param binding
	 * @param typeBinding
	 * @param declarationTypeToParentType
	 * @return
	 * @throws CircularReferenceException
	 */
	private ArrayList<MethodInformation> getSuperDeclarations(
			IMethodBinding binding,
			ITypeBinding typeBinding,
			final HashMap<TypeInformation, TypeInformation> declarationTypeToParentType)
			throws CircularReferenceException {

		class Pair {
			public final ITypeBinding type;
			public final ITypeBinding parent;

			public Pair(ITypeBinding type, ITypeBinding parent) {
				this.type = type;
				this.parent = parent;
			}

			@Override
			public int hashCode() {
				return type.hashCode();
			}

			@Override
			public boolean equals(Object obj) {
				if (obj == this)
					return true;
				if (obj instanceof Pair) {
					return type.equals(((Pair) obj).type);
				}
				return false;
			}
		}

		ArrayList<MethodInformation> superDeclarations = new ArrayList<MethodInformation>();
		Queue<Pair> ifacesQueue = new ConcurrentLinkedQueue<Pair>();
		for (ITypeBinding t : typeBinding.getInterfaces()) {
			ifacesQueue.add(new Pair(t, t));
		}
		Set<ITypeBinding> ifacesInQueue = new HashSet<ITypeBinding>();
		while (!ifacesQueue.isEmpty()) {
			Pair pair = ifacesQueue.poll();
			ITypeBinding iface = pair.type;
			ITypeBinding par = pair.parent;

			MethodInformation localSuperDeclaration = null;
			for (IMethodBinding superMethodBinding : iface.getDeclaredMethods()) {
				if (binding.overrides(superMethodBinding)) {
					localSuperDeclaration = getHeadMethodInfo(superMethodBinding);
					break;
				}
			}
			if (localSuperDeclaration != null) {
				superDeclarations.add(localSuperDeclaration);
				declarationTypeToParentType.put(
						localSuperDeclaration.getType(),
						(TypeInformation) resolved.get(par));
			} else {
				Set<ITypeBinding> toAdd = new HashSet<ITypeBinding>(Arrays
						.asList(iface.getInterfaces()));
				toAdd.removeAll(ifacesInQueue);
				ifacesInQueue.addAll(toAdd);
				for (ITypeBinding i : toAdd) {
					ifacesQueue.add(new Pair(i, par));
				}
			}
		}
		return superDeclarations;
	}

	/**
	 * @param binding
	 * @param typeBinding
	 * @return
	 * @throws CircularReferenceException
	 */
	private MethodInformation getSuperDefinition(IMethodBinding binding,
			ITypeBinding typeBinding) throws CircularReferenceException {
		MethodInformation superDefinition = null;
		for (ITypeBinding superClass = typeBinding.getSuperclass(); superClass != null; superClass = superClass
				.getSuperclass()) {
			for (IMethodBinding superMethodBinding : superClass
					.getDeclaredMethods()) {
				if (binding.overrides(superMethodBinding)) {
					superDefinition = getHeadMethodInfo(superMethodBinding);
					break;
				}
			}
		}
		return superDefinition;
	}

	@SuppressWarnings("unchecked")
	private void validateMethodBody(IMethodBinding binding, IResource resource,
			MethodDeclaration node) throws CircularReferenceException,
			CoreException {
		MethodInformation info = getHeadMethodInfo(binding);

		Permissions execPermissions = info.getExecPermissions();

		List<VariableDeclaration> params = (List<VariableDeclaration>) node
				.getStructuralProperty(MethodDeclaration.PARAMETERS_PROPERTY);
		final ArrayList<Integer> threadStarterParameters = info
				.getThreadStarterParameters();
		for (int i : threadStarterParameters) {
			threadStarters.add(params.get(i).resolveBinding());
		}

		// If we've got source, we should check access rules
		if (node != null && node.getBody() != null) {
			final Set<IVariableBinding> readAccess = new HashSet<IVariableBinding>();
			final Set<IVariableBinding> writeAccess = new HashSet<IVariableBinding>();
			final Set<IMethodBinding> execAccess = new HashSet<IMethodBinding>();

			MethodBodyVerifier builder = new MethodBodyVerifier(this, resource,
					info);
			builder.buildAccessStructures(node.getBody());

			for (IVariableBinding read : readAccess) {
				FieldInformation field = getFieldInfo(read);
				if (!execPermissions.mightAccess(field.getReadPermissions())) {
					reportError(resource, node,
							Error.MethodAttemptsToReadInaccessibleVariable);
				}
			}

			for (IVariableBinding write : writeAccess) {
				FieldInformation field = getFieldInfo(write);
				if (!execPermissions.mightAccess(field.getWritePermissions())) {
					reportError(resource, node,
							Error.MethodAttemptsToWriteInaccessibleVariable);
				}
			}

			for (IMethodBinding exec : execAccess) {
				MethodInformation method = getHeadMethodInfo(exec);
				if (!execPermissions.mightAccess(method.getExecPermissions())) {
					reportError(resource, node,
							Error.MethodAttemptsToExecInaccessibleMethod);
				}
			}
		}
	}

	FieldInformation getFieldInfo(IVariableBinding binding)
			throws CircularReferenceException {

		FieldInformation result = (FieldInformation) resolved.get(binding);
		if (result == null) {

			if (underConstruction.contains(binding)) {
				throw new CircularReferenceException(binding);
			}
			try {
				underConstruction.add(binding);

				ITypeBinding type = binding.getType();
				TypeInformation information = getTypeInfo(type);

				boolean isReadPermissionsValid = true;
				boolean isWritePermissionsValid = true;
				Permissions readPermissions = Permissions.Any;
				Permissions writePermissions = Permissions.Any;

				for (IAnnotationBinding annotation : binding.getAnnotations()) {
					String fqn = annotation.getAnnotationType()
							.getQualifiedName();
					if (ClassNames.READ_PERMISSIONS.equals(fqn)) {
						ResolvePermissionsResult readPermissionsResult = processPermissions(annotation);
						isReadPermissionsValid = readPermissionsResult.isValid;
						readPermissions = readPermissionsResult.execPermissions;
					} else if (ClassNames.WRITE_PERMISSIONS.equals(fqn)) {
						ResolvePermissionsResult writePermissionsResult = processPermissions(annotation);
						isWritePermissionsValid = writePermissionsResult.isValid;
						writePermissions = writePermissionsResult.execPermissions;
					}
				}

				result = new FieldInformation(information, readPermissions,
						isReadPermissionsValid, writePermissions,
						isWritePermissionsValid);

				resolved.put(binding, result);
			} finally {
				underConstruction.remove(binding);
			}

		}
		return result;
	}

	private TypeInformation getSuperTypeInfo(ITypeBinding binding)
			throws CircularReferenceException {
		ITypeBinding superClassBinding = binding.getSuperclass();
		if (superClassBinding == null) {
			return null;
		}
		TypeInformation information = getTypeInfo(superClassBinding);
		return information;
	}

	private TypeInformation[] getInterfacesTypeInfo(ITypeBinding binding)
			throws CircularReferenceException {
		final TypeInformation[] interfacesInformation;

		ITypeBinding[] interfaces = binding.getInterfaces();
		interfacesInformation = new TypeInformation[interfaces.length];
		for (int i = 0; i < interfaces.length; ++i) {
			interfacesInformation[i] = getTypeInfo(interfaces[i]);
		}
		return interfacesInformation;
	}

	void reportError(IResource resource, ASTNode node, Error code)
			throws CoreException {
		IMarker marker = resource.createMarker(AnnatashaCore.MARKER_TYPE);
		if (code == Error.InternalError) {
			try {
				throw new Exception("Internal Error");
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
		marker.setAttribute(IMarker.MESSAGE, code.message);
		marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		marker.setAttribute(IMarker.CHAR_START, node.getStartPosition());
		marker.setAttribute(IMarker.CHAR_END, node.getStartPosition()
				+ node.getLength());
		++errorsCounter;
	}

	private static interface ClassNames {
		final static String THREAD_MARKER = "com.google.code.annatasha.annotations.ThreadMarker";
		final static String THREAD_STARTER = "com.google.code.annatasha.annotations.ThreadStarter";

		final static String READ_PERMISSIONS = "com.google.code.annatasha.annotations.Field.ReadPermissions";
		final static String WRITE_PERMISSIONS = "com.google.code.annatasha.annotations.Field.WritePermissions";
		final static String EXEC_PERMISSIONS = "com.google.code.annatasha.annotations.Method.ExecPermissions";

		final static String RUNNABLE = "java.lang.Runnable";

		public static class EntryPoint {
			public final String className;
			public final String methodName;

			public EntryPoint(String className, String methodName) {
				this.className = className;
				this.methodName = methodName;
			}
		}

		final static EntryPoint[] EntryPoints = new EntryPoint[] {
				new EntryPoint("java.lang.Runnable", "run"),
				new EntryPoint("java.util.concurrent.Callable", "call"),
				new EntryPoint("java.lang.Thread", "start") };
	}

	public enum Error {
		ThreadMarkerCannotSpecifyExecPermissions(0x1,
				"An type cannot both be thread marker and have execution permissions specified"), ThreadMarkerMustBeAnInterface(
				0x2, "Thread marker must be an interface"), ThreadMarkerSupertypeError(
				0x3,
				"Thread marker may only extend thread markers or java.lang.Runnable"), ThreadMarkerInvalidInheritance(
				0x4, "Thread marker might only extend other thread markers"), PermissionsMustEnumerateThreadMarkers(
				0x5, "Only thread markers may specify permissions"), MethodAttemptsToReadInaccessibleVariable(
				0x6, "Method attempts to read inaccessible variable"), MethodAttemptsToWriteInaccessibleVariable(
				0x7, "Method attempts to write inaccessible variable"), MethodAttemptsToExecInaccessibleMethod(
				0x8, "Method attempts to execute inaccessible method"), MethodPermissionsMustIncludeInherited(
				0x9,
				"Method permissions must be wider than inherited permissions"), NonRunnableArgumentThreadStarter(
				0xA, "Non-runnable argument may not be thread starter"), ExternalTypeIsInvalid(
				0xB, "External type is invalid"), InternalError(0xC,
				"Internal error"), ExactlyOneThreadMarkerExpectedForThreadStarter(
				0xD, "Exactly one thread marker is expected for thread starter"), ThreadStarterNotInheritedFromEntryPoint(
				0xE,
				"Thread starter must be inherited from entry point class (Callable, Runnable, Thread)"), ThreadMarkerMustHaveNoMethods(
				0xF, "There must be no methods declared in thread marker"), ExecPermissionsInThreadStarterMethod(
				0x10,
				"ExecPermissions cannot be specified in thread starter method"), InvalidTypeCast(
				0x11, "Type cast loses thread marker"), ExecPermissionsInheritedViolation(
				0x12,
				"Execution permissions for method violate inherited execution permissions"), ThreadStarterArgumentInvalid(
				0x13, "Thread starter argument of non-EntryPoint class"), ThreadStarterArgumentsDiffer(
				0x14,
				"Thread starter arguments differ in method and its super-declaration and/or super-definitions"), InternalErrorInvalidObjectNotReported(
				0x15,
				"Internal error: code unit seems invalid, but not reported as invalid"), ThreadMarkerMustBeSpecifiedExplicitly(
				0x16,
				"Interfaces extending thread markers must be explicitly specified as thread marker"), MethodAttemptsToAccessThreadStarterParameter(
				0x17,
				"Method attempts to directly access thread starter parameter");

		public final int code;
		public final String message;

		Error(int code, String message) {
			this.code = code;
			this.message = message;
		}
	}

	public MethodInformation getMethodInfo(IMethodBinding binding)
			throws CircularReferenceException {
		return getHeadMethodInfo(binding);
	}

	public boolean isThreadStarter(IVariableBinding binding) {
		return threadStarters.contains(binding);
	}

	private static HashMap<String, ArrayList<Integer>> ThreadStartersParameters = new HashMap<String, ArrayList<Integer>>();

	static {
		ThreadStartersParameters.put(
				"java.lang.Thread.Thread(java.lang.Runnable,",
				new ArrayList<Integer>(Arrays.asList(0)));
	}

}
