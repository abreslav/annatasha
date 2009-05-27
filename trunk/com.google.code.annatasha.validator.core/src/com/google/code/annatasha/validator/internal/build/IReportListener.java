package com.google.code.annatasha.validator.internal.build;

interface IReportListener {
	
	void reportProblem(IMarkerFactory factory, Error problem);

}
