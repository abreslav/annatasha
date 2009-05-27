package test001;
import com.google.code.annatasha.annotations.ThreadMarker;

@ThreadMarker
public interface ValidThreadMarker3 extends ValidThreadMarker,
		ValidThreadMarker2 {

}
