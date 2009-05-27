package com.google.code.annatasha.validator.internal.build;

interface SymbolVisitor {
	
	void visit(TypeInformation info);
	void visit(MethodInformation info);
	void visit(FieldInformation info);

}
