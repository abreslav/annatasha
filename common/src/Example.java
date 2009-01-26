import com.google.code.annatasha.annotations.ThreadMarker;
import com.google.code.annatasha.annotations.Method.ExecPermissions;


public class Example {

	@ThreadMarker
	interface IMyMarker extends Runnable {
		@Override
		@ExecPermissions(IMyMarker.class)
		public void run();
	}

	static class R implements IMyMarker {

		@Override
		public void run() {
		}
		
	}
	
	public static void r(Runnable r) {
		r.run(); // No error: markers are lost
	}
	
	public static void r1(IMyMarker r) {
		r.run(); // Error: markers violated
	}
	
	public static void main(String[] args) {
		new Thread(new R()).start(); // No Error: Thread starter
		r(new R()); // Error
		r1(new R()); // No error: type is not lost
	}
	
}
