package cc.guider.architeature.inspurhotfix.fix;

import android.content.Context;
import android.widget.Toast;

/**
 * @author 0086-Paul
 * @date 2018-09-17
 *
 */
public class BugFeature {

	public void math(Context context) {
		int a = 10;
		// TODO: 2019-11-22 此处遗留bug 需要修复之后，打包apk 拿到classes2.dex 并 push到sdcard
		int b = 0;
		//memory leak
		Toast.makeText(context.getApplicationContext(), "math >>> " + a / b, Toast.LENGTH_SHORT).show();
	}
}
