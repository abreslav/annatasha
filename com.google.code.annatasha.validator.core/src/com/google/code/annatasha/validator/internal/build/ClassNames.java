/**
 * 
 */
package com.google.code.annatasha.validator.internal.build;

interface ClassNames {
	final static String PACKAGE_PREFIX = "com.google.code.annatasha.annotations";
	
	final static String THREAD_MARKER = PACKAGE_PREFIX + ".ThreadMarker";
	final static String THREAD_STARTER = PACKAGE_PREFIX + ".ThreadStarter";

	final static String READ_PERMISSIONS = PACKAGE_PREFIX + ".Field.ReadPermissions";
	final static String WRITE_PERMISSIONS = PACKAGE_PREFIX + ".Field.WritePermissions";
	final static String EXEC_PERMISSIONS = PACKAGE_PREFIX + ".Method.ExecPermissions";

	public static class EntryPoint {
		public final String className;
		public final String methodName;

		public String typeBindingKey;

		public EntryPoint(String className, String methodName) {
			this.className = className;
			this.methodName = methodName;
		}
	}

	final static ClassNames.EntryPoint[] EntryPoints = new ClassNames.EntryPoint[] {
			new EntryPoint("java.lang.Runnable", "run"),
			new EntryPoint("java.util.concurrent.Callable", "call"),
			new EntryPoint("java.lang.Thread", "start") };
}