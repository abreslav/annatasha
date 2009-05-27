package com.google.code.annatasha.validator.internal.build;

public interface IProblemsReportFactory {
	
	void reportProblem(IMarkerFactory markerFactory, Error error);

}
