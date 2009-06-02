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
package com.google.code.annatasha.validator.internal.build;

public enum Error {
	ThreadMarkerCannotSpecifyExecPermissions(0x1,
			"An type cannot both be thread marker and have execution permissions specified"), ThreadMarkerMustBeAnInterface(
			0x2, "Thread marker must be an interface"), ThreadMarkerSupertypeError(
			0x3,
			"Thread marker may only extend thread markers or java.lang.Runnable, java.util.concurrent.Callable"), ThreadMarkerInvalidInheritance(
			0x4, "Thread marker might only extend other thread markers"), PermissionsMustEnumerateThreadMarkers(
			0x5, "Only thread markers may specify permissions"), MethodAttemptsToReadInaccessibleVariable(
			0x6, "Method attempts to read inaccessible variable"), MethodAttemptsToWriteInaccessibleVariable(
			0x7, "Method attempts to write inaccessible variable"), MethodAttemptsToExecInaccessibleMethod(
			0x8, "Method attempts to execute inaccessible method"), MethodPermissionsMustIncludeInherited(
			0x9, "Method permissions must be wider than inherited permissions"), NonRunnableArgumentThreadStarter(
			0xA, "Non-runnable argument may not be thread starter"), ExternalTypeIsInvalid(
			0xB, "External type is invalid"), InternalError(0xC,
			"Internal error"), ExactlyOneThreadMarkerExpectedForThreadStarter(
			0xD, "Exactly one thread marker is expected for thread starter"), ThreadStarterNotInheritedFromEntryPoint(
			0xE,
			"Thread starter must be inherited from entry point class (Callable, Runnable, Thread)"), ThreadMarkerMustHaveNoMethods(
			0xF, "There must be no methods declared in thread marker"), ExecPermissionsInThreadStarterMethod(
			0x10,
			"ExecPermissions cannot be specified in thread starter method"), InvalidTypeUpCast(
			0x11, "Invalid type downcast"), ExecPermissionsInheritedViolation(
			0x12,
			"Execution permissions for method violate inherited execution permissions"), ThreadStarterArgumentInvalid(
			0x13, "Thread starter argument of non-EntryPoint class"), ThreadStarterArgumentsDiffer(
			0x14,
			"Thread starter arguments differ in method and its super-declaration and/or super-definitions"), InternalErrorInvalidObjectNotReported(
			0x15,
			"Internal error: code unit seems invalid, but not reported as invalid"), ThreadMarkerMustBeSpecifiedExplicitly(
			0x16,
			"Interfaces extending thread markers must be explicitly specified as thread marker"), MethodAttemptsToAccessThreadStarterParameter(
			0x17, "Method attempts to directly access thread starter parameter"), ThreadStarterCannotBePartOfExpression(
			0x18,
			"Unable to access field or execute method of thread starter object"), InvalidAnnotation(
			0x19, "Annotation usage is invalid in this context"), CircularReference(
			0x1A, "Circular reference found"), SymbolUndefined(0x1B,
			"Symbol undefined"), InvalidTypeDownCast(0x1B,
			"Downcast is invalid"), InvalidAssignment(0x1C,
			"Invalid assignment"), ConstructorMightOnlyHaveAnyAccess(0x1D,
			"Constructors may only have { ATask } access modifier");

	public final int code;
	public final String message;

	Error(int code, String message) {
		this.code = code;
		this.message = message;
	}
}