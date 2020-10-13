package constant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class SystemConstant {
	public static final int STATUS_ACTIVE = 1;
	public static final int STATUS_DISABLE = 0;
	
	public static DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
}
