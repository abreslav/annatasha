/*******************************************************************************
 * Copyright (c) 2008, 2009 Ivan Egorov <egorich.3.04@gmail.com>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ivan Egorov <egorich.3.04@gmail.com>
 *******************************************************************************/

/**
 * 
 */
package com.google.code.annatasha.validator.internal.build.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import com.google.code.annatasha.validator.internal.build.ClassNames;
import com.google.code.annatasha.validator.internal.build.Error;
import com.google.code.annatasha.validator.internal.build.KeysFactory;
import com.google.code.annatasha.validator.internal.build.ModelValidator;
import com.google.code.annatasha.validator.internal.build.markers.AstNodeMarkerFactory;
import com.google.code.annatasha.validator.internal.build.markers.MarkedBoolean;
import com.google.code.annatasha.validator.internal.build.markers.MarkedString;
import com.google.code.annatasha.validator.internal.build.markers.NullMarkerFactory;
import com.google.code.annatasha.validator.internal.build.symbols.FieldInformation;
import com.google.code.annatasha.validator.internal.build.symbols.MethodInformation;
import com.google.code.annatasha.validator.internal.build.symbols.Permissions;
import com.google.code.annatasha.validator.internal.build.symbols.SymbolInformation;
import com.google.code.annatasha.validator.internal.build.symbols.TypeInformation;

final class SourceFileProcessor extends ASTVisitor {

	private final IResource resource;
	private final ISourceFileRequestorCallback callback;

	/**
	 * @param resource
	 * @param listener
	 */
	public SourceFileProcessor(IResource resource,
			ISourceFileRequestorCallback callback) {
		this.resource = resource;
		this.callback = callback;
	}

	// / TYPES
	private MarkedString typeSuperClass;
	private ArrayList<MarkedString> typeInterfaces;
	private boolean typeIsInterface;
	private boolean typeIsClass;
	private boolean typeIsAnnotationOrEnum;
	private boolean typeIsEntryPoint;
	private Permissions typeExecPermissions;
	private MarkedBoolean typeIsThreadMarker;
	private MarkedString typeName;
	private boolean typeHasMethods;

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(EnumDeclaration node) {
		ITypeBinding binding = node.resolveBinding();
		String key = KeysFactory.getKey(binding);

		callback.symbolDefined(key);

		TypeInformation typeInformation = callback.getTypeInformation(key);
		initLocalTypeInformation(typeInformation, binding, node.getName(),
				null, node.superInterfaceTypes(), node.modifiers());
		updateLocalTypeInformation(typeInformation, binding);

		for (IMethodBinding method : binding.getDeclaredMethods()) {
			String mkey = KeysFactory.getKey(method);
			callback.symbolDefined(mkey);

			MethodInformation old = methodInformation;
			methodInformation = callback.getMethodInformation(mkey);
			initLocalMethodInformation(method, null);
			updateLocalMethodInformation(method, null);
			methodInformation = old;
		}

		for (IVariableBinding field : binding.getDeclaredFields()) {
			String fkey = KeysFactory.getKey(field);
			callback.symbolDefined(fkey);

			FieldInformation info = callback.getFieldInformation(fkey);
			initLocalFieldInformation(null, new ArrayList<IExtendedModifier>());
			updateLocalFieldInformation(info);
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(AnnotationTypeDeclaration node) {
		ITypeBinding binding = node.resolveBinding();
		String key = KeysFactory.getKey(binding);

		callback.symbolDefined(key);

		TypeInformation typeInformation = callback.getTypeInformation(key);
		initLocalTypeInformation(typeInformation, binding, node.getName(),
				null, new ArrayList<Type>(), node.modifiers());
		updateLocalTypeInformation(typeInformation, binding);

		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(TypeDeclaration node) {
		ITypeBinding binding = node.resolveBinding();
		String key = KeysFactory.getKey(binding);

		callback.symbolDefined(key);

		TypeInformation typeInformation = callback.getTypeInformation(key);
		initLocalTypeInformation(typeInformation, binding, node.getName(), node
				.getSuperclassType(), node.superInterfaceTypes(), node
				.modifiers());
		updateLocalTypeInformation(typeInformation, binding);

		IMethodBinding defaultConstructor = null;
		for (IMethodBinding method : binding.getDeclaredMethods()) {
			if (method.isDefaultConstructor())
				defaultConstructor = method;
		}
		if (defaultConstructor != null) {
			String ckey = KeysFactory.getKey(defaultConstructor);
			callback.symbolDefined(ckey);

			MethodInformation old = methodInformation;
			methodInformation = callback.getMethodInformation(ckey);
			initLocalMethodInformation(defaultConstructor, null);
			updateLocalMethodInformation(defaultConstructor, null);
			methodInformation = old;
		}

		return true;
	}

	private void initLocalTypeInformation(TypeInformation typeInformation,
			ITypeBinding binding, ASTNode typeName, Type superclassType,
			List<Type> superInterfaceTypes, List<IExtendedModifier> modifiers) {

		this.typeName = new MarkedString(new AstNodeMarkerFactory(resource,
				typeName), binding.getName());

		typeSuperClass = null;
		typeInterfaces = new ArrayList<MarkedString>();

		typeIsInterface = binding.isInterface() && !binding.isAnnotation();
		typeIsClass = binding.isClass();
		typeIsAnnotationOrEnum = binding.isEnum() || binding.isAnnotation();

		typeIsEntryPoint = false;

		typeExecPermissions = null;

		typeIsThreadMarker = new MarkedBoolean(new AstNodeMarkerFactory(
				resource, typeName), false);

		typeHasMethods = binding.getDeclaredMethods().length != 0;

		if (typeIsClass && superclassType != null) {
			final ITypeBinding superTypeBinding = superclassType
					.resolveBinding();
			typeSuperClass = new MarkedString(new AstNodeMarkerFactory(
					resource, superclassType), KeysFactory
					.getKey(ModelValidator.getCorrectBinding(superTypeBinding)));
		}
		if (typeIsClass && binding.getSuperclass() != null) {
			callback.symbolRequested(ModelValidator.getCorrectBinding(binding
					.getSuperclass()));
		}

		for (Type superInterface : superInterfaceTypes) {
			typeInterfaces.add(new MarkedString(new AstNodeMarkerFactory(
					resource, superInterface), KeysFactory
					.getKey(ModelValidator.getCorrectBinding(superInterface
							.resolveBinding()))));
		}
		for (ITypeBinding iface : binding.getInterfaces()) {
			callback.symbolRequested(ModelValidator.getCorrectBinding(iface));
		}

		for (int i = 0; i < ClassNames.EntryPoints.length; ++i) {
			if (ClassNames.EntryPoints[i].className.equals(binding
					.getQualifiedName())) {
				typeIsEntryPoint = true;
				ClassNames.EntryPoints[i].typeBindingKey = KeysFactory
						.getKey(binding);
				break;
			}
		}

		for (IExtendedModifier modifierObject : modifiers) {
			if (modifierObject instanceof Annotation) {
				Annotation annotation = (Annotation) modifierObject;
				final String fqn = annotation.getTypeName()
						.getFullyQualifiedName();
				if (ClassNames.EXEC_PERMISSIONS.equals(fqn)) {
					typeExecPermissions = processPermissions(annotation);
				} else if (ClassNames.THREAD_MARKER.equals(fqn)) {
					typeIsThreadMarker = new MarkedBoolean(
							new AstNodeMarkerFactory(resource, annotation),
							true);
				} else if (fqn.startsWith(ClassNames.PACKAGE_PREFIX)) {
					callback.reportProblem(new AstNodeMarkerFactory(resource,
							annotation), Error.InvalidAnnotation);
				}
			}
		}
	}

	private void updateLocalTypeInformation(TypeInformation typeInformation,
			ITypeBinding binding) {
		validateSourcePolicy(typeInformation);

		typeInformation.binding = binding;
		typeInformation.name = typeName;
		typeInformation.clazz = typeIsClass;
		typeInformation.iface = typeIsInterface;
		typeInformation.annot = typeIsAnnotationOrEnum;
		typeInformation.entryPoint = typeIsEntryPoint;
		typeInformation.threadMarker = typeIsThreadMarker;
		typeInformation.superClass = typeSuperClass;
		typeInformation.superInterfaces = typeInterfaces;
		typeInformation.execPermissions = typeExecPermissions;
		typeInformation.hasMethods = typeHasMethods;
	}

	// / METHODS
	// private boolean methodIsAbstract;
	private boolean methodIsEntryPoint;
	private Permissions methodExecPermissions;
	private MarkedString methodReturnType;
	private ArrayList<Integer> methodThreadStarters;
	private String methodDeclaringType;
	private MarkedString methodName;
	private HashSet<String> methodReferencedBindings;
	private boolean methodIsConstructor;
	private boolean methodIsStatic;

	@Override
	public boolean visit(MethodDeclaration node) {
		IMethodBinding binding = node.resolveBinding();
		String key = KeysFactory.getKey(binding);

		callback.symbolDefined(key);

		MethodInformation oldMethodInformation = methodInformation;
		try {
			methodInformation = callback.getMethodInformation(key);
			initLocalMethodInformation(binding, node);
			updateLocalMethodInformation(binding, node);
		} finally {
			methodInformation = oldMethodInformation;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private void initLocalMethodInformation(IMethodBinding binding,
			MethodDeclaration node) {
		if (node != null) {
			final ITypeBinding declaringClass = binding.getDeclaringClass();
			methodDeclaringType = KeysFactory.getKey(declaringClass);
			methodName = new MarkedString(new AstNodeMarkerFactory(resource,
					node.getName()), node.getName().getIdentifier());
			methodIsStatic = Modifier.isStatic(binding.getModifiers())
					|| binding.isConstructor();
			methodIsConstructor = binding.isConstructor();

			// return type
			if (!binding.isConstructor()) {
				final Type returnType = node.getReturnType2();
				methodReturnType = new MarkedString(new AstNodeMarkerFactory(
						resource, returnType), KeysFactory.getKey(returnType
						.resolveBinding()));
			} else {
				methodReturnType = new MarkedString(new AstNodeMarkerFactory(
						resource, node.getName()), KeysFactory
						.getKey(declaringClass));
			}
			callback.symbolRequested(ModelValidator.getCorrectBinding(binding
					.getReturnType()));

			// is abstract?
			// methodIsAbstract = Modifier.isAbstract(node.getModifiers())
			// || declaringClass.isInterface();

			// is entry point?
			for (ClassNames.EntryPoint point : ClassNames.EntryPoints) {
				if (point.className.equals(declaringClass.getQualifiedName())
						&& point.methodName.equals(binding.getName())) {
					methodIsEntryPoint = true;
					break;
				}
			}

			// get modifiers
			methodExecPermissions = null;

			List<IExtendedModifier> modifiers = node.modifiers();
			for (IExtendedModifier modifierObject : modifiers) {
				if (modifierObject instanceof Annotation) {
					Annotation annotation = (Annotation) modifierObject;
					final String fqn = annotation.resolveTypeBinding()
							.getQualifiedName();
					if (ClassNames.EXEC_PERMISSIONS.equals(fqn)) {
						methodExecPermissions = processPermissions(annotation);
					} else if (fqn.startsWith(ClassNames.PACKAGE_PREFIX)) {
						callback.reportProblem(new AstNodeMarkerFactory(
								resource, annotation), Error.InvalidAnnotation);
					}
				}
			}

			// scan parameters for thread starters
			methodThreadStarters = new ArrayList<Integer>();

			List<SingleVariableDeclaration> parameters = node.parameters();
			methodReferencedBindings = new HashSet<String>();
			int i = 0;
			for (SingleVariableDeclaration param : parameters) {
				methodReferencedBindings.add(KeysFactory.getKey(param.getType()
						.resolveBinding()));

				List<IExtendedModifier> paramModifiers = param.modifiers();
				for (IExtendedModifier paramModifierObject : paramModifiers) {
					if (paramModifierObject instanceof Annotation) {
						Annotation paramAnnotation = (Annotation) paramModifierObject;
						final String fqn = paramAnnotation.getTypeName()
								.getFullyQualifiedName();
						if (ClassNames.THREAD_STARTER.equals(fqn)) {
							methodThreadStarters.add(i);
						}
					}
				}
				++i;
			}

			for (ITypeBinding param : binding.getParameterTypes()) {
				callback.symbolRequested(ModelValidator
						.getCorrectBinding(param));
			}
		} else if (binding.isDefaultConstructor()) {
			String typeKey = KeysFactory.getKey(ModelValidator
					.getCorrectBinding(binding.getDeclaringClass()));
			methodIsEntryPoint = false;
			methodExecPermissions = Permissions.Any;
			methodReturnType = new MarkedString(new NullMarkerFactory(),
					typeKey);
			methodThreadStarters = new ArrayList<Integer>();
			methodDeclaringType = typeKey;
			methodName = new MarkedString(new NullMarkerFactory(),
					ModelValidator.getCorrectBinding(
							binding.getDeclaringClass()).getName());
			methodReferencedBindings = new HashSet<String>();
			methodIsConstructor = true;
			methodIsStatic = true;
		}

	}

	private void updateLocalMethodInformation(IMethodBinding binding,
			MethodDeclaration node) {
		validateSourcePolicy(methodInformation);

		methodInformation.binding = binding;
		methodInformation.name = methodName;

		methodInformation.isStatic = methodIsStatic;
		methodInformation.isConstructor = methodIsConstructor;

		methodInformation.declaringType = methodDeclaringType;

		methodInformation.entryPoint = methodIsEntryPoint;
		methodInformation.execPermissions = methodExecPermissions;
		methodInformation.returnType = methodReturnType;
		methodInformation.threadStarters = methodThreadStarters;

		methodInformation.referencedBindings = methodReferencedBindings;

		if (node != null && node.getBody() != null)
			callback.enqueueMethodBodyValidation(resource, binding, node
					.getBody());
	}

	// / VARIABLES
	private MarkedString variableType;
	private Permissions variableReadPermissions;
	private Permissions variableWritePermissions;
	private MarkedBoolean variableIsThreadStarter;
	private MethodInformation methodInformation;

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(FieldDeclaration node) {
		variableType = new MarkedString(new AstNodeMarkerFactory(resource, node
				.getType()), KeysFactory.getKey(ModelValidator
				.getCorrectBinding(node.getType().resolveBinding())));
		callback.symbolRequested(ModelValidator.getCorrectBinding(node
				.getType().resolveBinding()));
		final List<IExtendedModifier> modifiers = node.modifiers();
		initLocalFieldInformation(node, modifiers);
		return true;
	}

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		IVariableBinding binding = node.resolveBinding();
		if (binding.isField()) {
			String key = KeysFactory.getKey(binding);
			callback.symbolDefined(key);

			FieldInformation fieldInformation = callback
					.getFieldInformation(key);
			updateLocalFieldInformation(fieldInformation);
		}
		return true;
	}

	/**
	 * @param node
	 * @param modifiers
	 */
	private void initLocalFieldInformation(ASTNode node,
			final List<IExtendedModifier> modifiers) {
		if (node != null) {
			variableIsThreadStarter = new MarkedBoolean(
					new AstNodeMarkerFactory(resource, node), false);
			variableReadPermissions = Permissions.Any;
			variableWritePermissions = Permissions.Any;
			for (IExtendedModifier modifierObject : modifiers) {
				if (modifierObject instanceof Annotation) {
					Annotation annotation = (Annotation) modifierObject;
					final String fqn = annotation.resolveTypeBinding()
							.getQualifiedName();
					if (ClassNames.READ_PERMISSIONS.equals(fqn)) {
						variableReadPermissions = processPermissions(annotation);
					} else if (ClassNames.WRITE_PERMISSIONS.equals(fqn)) {
						variableWritePermissions = processPermissions(annotation);
					} else if (ClassNames.THREAD_STARTER.equals(fqn)) {
						variableIsThreadStarter = new MarkedBoolean(
								new AstNodeMarkerFactory(resource, annotation),
								true);
					} else if (fqn.startsWith(ClassNames.PACKAGE_PREFIX)) {
						callback.reportProblem(new AstNodeMarkerFactory(
								resource, annotation), Error.InvalidAnnotation);
					}
				}
			}
		} else {
			variableIsThreadStarter = new MarkedBoolean(
					new NullMarkerFactory(), false);
			variableReadPermissions = Permissions.Any;
			variableWritePermissions = Permissions.Any;
		}
	}

	private void updateLocalFieldInformation(FieldInformation fieldInformation) {
		validateSourcePolicy(fieldInformation);

		fieldInformation.type = variableType;
		fieldInformation.readPermissions = variableReadPermissions;
		fieldInformation.writePermissions = variableWritePermissions;
		fieldInformation.threadStarter = variableIsThreadStarter;
	}

	// / EXPRESSIONS
	@Override
	public boolean visit(ArrayCreation node) {
		if (methodReferencedBindings != null) {
			ITypeBinding type = ModelValidator.getCorrectBinding(node.getType()
					.getComponentType().resolveBinding());
			callback.symbolRequested(type);
			methodReferencedBindings.add(KeysFactory.getKey(type));
		}
		return true;
	}

	@Override
	public boolean visit(CastExpression node) {
		if (methodReferencedBindings != null) {
			ITypeBinding type = ModelValidator.getCorrectBinding(node.getType()
					.resolveBinding());
			callback.symbolRequested(type);
			methodReferencedBindings.add(KeysFactory.getKey(ModelValidator
					.getCorrectBinding(node.getType().resolveBinding())));
		}
		return true;
	}

	@Override
	public boolean visit(ClassInstanceCreation node) {
		AnonymousClassDeclaration declaration = node
				.getAnonymousClassDeclaration();
		if (declaration != null) {
			ITypeBinding binding = node.getAnonymousClassDeclaration()
					.resolveBinding();
			String key = KeysFactory.getKey(binding);
			callback.symbolDefined(key);

			Type type = node.getType();
			boolean isClass = type.resolveBinding().isClass();

			TypeInformation typeInformation = callback.getTypeInformation(key);
			initLocalTypeInformation(typeInformation, binding, node.getType(),
					isClass ? node.getType() : null,
					isClass ? new ArrayList<Type>() : Arrays.asList(node
							.getType()), new ArrayList<IExtendedModifier>());
			updateLocalTypeInformation(typeInformation, binding);

			if (methodReferencedBindings != null) {
				callback.symbolRequested(binding);
				methodReferencedBindings.add(key);
			}
		} else {
			callback.symbolRequested(ModelValidator.getCorrectBinding(node
					.getType().resolveBinding()));
			callback.symbolRequested(node.resolveConstructorBinding()
					.getMethodDeclaration());
			if (methodReferencedBindings != null) {
				methodReferencedBindings.add(KeysFactory.getKey(ModelValidator
						.getCorrectBinding(node.getType().resolveBinding())));
			}
		}
		return true;
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		if (methodReferencedBindings != null) {
			callback.symbolRequested(ModelValidator.getCorrectBinding(node
					.getParameter().getType().resolveBinding()));
			methodReferencedBindings.add(KeysFactory.getKey(ModelValidator
					.getCorrectBinding(node.getParameter().getType()
							.resolveBinding())));
		}
		return true;
	}

	@Override
	public boolean visit(FieldAccess node) {
		if (methodReferencedBindings != null) {
			callback.symbolRequested(ModelValidator.getCorrectBinding(node
					.resolveTypeBinding()));
			methodReferencedBindings.add(KeysFactory.getKey(ModelValidator
					.getCorrectBinding(node.resolveTypeBinding())));
		}
		return true;
	}

	@Override
	public boolean visit(InstanceofExpression node) {
		if (methodReferencedBindings != null) {
			callback.symbolRequested(ModelValidator.getCorrectBinding(node
					.getRightOperand().resolveBinding()));
			methodReferencedBindings.add(KeysFactory
					.getKey(ModelValidator.getCorrectBinding(node
							.getRightOperand().resolveBinding())));
		}
		return true;
	}

	@Override
	public boolean visit(MethodInvocation node) {
		if (methodReferencedBindings != null) {
			callback.symbolRequested(ModelValidator.getCorrectBinding(node
					.resolveTypeBinding()));
			callback.symbolRequested(node.resolveMethodBinding()
					.getMethodDeclaration());
			methodReferencedBindings.add(KeysFactory.getKey(ModelValidator
					.getCorrectBinding(node.resolveTypeBinding())));
			methodReferencedBindings.add(KeysFactory.getKey(node
					.resolveMethodBinding().getMethodDeclaration()));
		}
		return true;
	}

	@Override
	public boolean visit(QualifiedName node) {
		ITypeBinding type = node.resolveTypeBinding();
		if (type != null && methodReferencedBindings != null) {
			callback.symbolRequested(ModelValidator.getCorrectBinding(type));
			methodReferencedBindings.add(KeysFactory.getKey(ModelValidator
					.getCorrectBinding(type)));
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(SimpleName node) {
		ITypeBinding type = node.resolveTypeBinding();
		if (type != null && methodReferencedBindings != null) {
			callback.symbolRequested(ModelValidator.getCorrectBinding(type));
			methodReferencedBindings.add(KeysFactory.getKey(ModelValidator
					.getCorrectBinding(type)));
		}
		return true;
	}

	@Override
	public boolean visit(SingleVariableDeclaration node) {
		if (methodReferencedBindings != null) {
			callback.symbolRequested(ModelValidator.getCorrectBinding(node
					.getType().resolveBinding()));
			methodReferencedBindings.add(KeysFactory.getKey(ModelValidator
					.getCorrectBinding(node.getType().resolveBinding())));
		}
		return true;
	}

	@Override
	public boolean visit(VariableDeclarationStatement node) {
		if (methodReferencedBindings != null) {
			callback.symbolRequested(ModelValidator.getCorrectBinding(node
					.getType().resolveBinding()));
			methodReferencedBindings.add(KeysFactory.getKey(ModelValidator
					.getCorrectBinding(node.getType().resolveBinding())));
		}
		return true;
	}

	// Helpers
	private Permissions processPermissions(Annotation annotation) {
		final ArrayList<MarkedString> classes = new ArrayList<MarkedString>();

		ASTVisitor visitor = new ASTVisitor() {
			@Override
			public boolean visit(TypeLiteral node) {
				ITypeBinding binding = node.getType().resolveBinding();
				classes.add(new MarkedString(new AstNodeMarkerFactory(resource,
						node), KeysFactory.getKey(binding)));
				return false;
			}
		};
		annotation.accept(visitor);
		return new Permissions(classes);
	}

	private void validateSourcePolicy(SymbolInformation information) {
		DelayedSourcePolicy policy = (DelayedSourcePolicy) information
				.getSourcePolicy();
		if (!policy.isAssigned()) {
			policy.setSourcePolicy(new SourceFilePolicy((IFile) resource));
		}
	}

	IResource getResource() {
		return resource;
	}
}
