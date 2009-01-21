package com.google.code.annatasha.validator.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.compiler.CategorizedProblem;

public class VerificationProblem extends CategorizedProblem {

	private final static String MARKER = "com.google.code.annatasha.validator.core.problem";
	
	private final int category;
	private final IFile file;
	
	/**
	 * @param category
	 * @param file
	 */
	public VerificationProblem(int category, IFile file) {
		this.category = category;
		this.file = file;
	}

	@Override
	public int getCategoryID() {
		return category;
	}

	@Override
	public String getMarkerType() {
		return MARKER;
	}

	public String[] getArguments() {
		return new String[0];
	}

	public int getID() {
		return ExternalProblemFixable;
	}

	public String getMessage() {
		return "Annatasha validator not implemented yet";
	}

	public char[] getOriginatingFileName() {
		return file.getName().toCharArray();
	}

	public int getSourceStart() {
		return -1;
	}
	
	public int getSourceEnd() {
		return -1;
	}

	public int getSourceLineNumber() {
		return 1;
	}

	public boolean isError() {
		return true;
	}

	public boolean isWarning() {
		return false;
	}

	public void setSourceEnd(int sourceEnd) {
	}

	public void setSourceLineNumber(int lineNumber) {
	}

	public void setSourceStart(int sourceStart) {
	}

}
