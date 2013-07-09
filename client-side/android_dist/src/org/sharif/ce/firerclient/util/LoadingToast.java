package org.sharif.ce.firerclient.util;

import org.sharif.ce.firerclient.R;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class LoadingToast {
	
	public static void show(Activity activity, String texttoShow) {
		LayoutInflater inflater = activity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.loading_toast,
		                               (ViewGroup) activity.findViewById(R.id.toast_layout_root));

		ProgressBar pBar = (ProgressBar) layout.findViewById(R.id.progressBar1);
		pBar.setEnabled(true);
		
		TextView text = (TextView) layout.findViewById(R.id.progressText);
		text.setText(texttoShow);

		Toast toast = new Toast(activity.getApplicationContext());
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		toast.show();
	}

}
