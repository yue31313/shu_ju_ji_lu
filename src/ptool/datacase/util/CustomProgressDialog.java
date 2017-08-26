package ptool.datacase.util;

import ptool.datacase.R;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class CustomProgressDialog extends Dialog {

	public CustomProgressDialog(Context context, int theme) {
		super(context, theme);
	}

	public CustomProgressDialog(Context context) {
		super(context);
	}

	/**
	 * Helper class for creating a custom dialog
	 */
	public static class Builder {

		private Context context;
		private String title;

		private OnKeyListener keyListener;

		public Builder(Context context) {
			this.context = context;
		}

		/**
		 * Set the Dialog title from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * Set the Dialog title from String
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setOnKeyListener(OnKeyListener listener) {
			this.keyListener = listener;
			return this;
		}

		/**
		 * Create the custom dialog
		 */
		public CustomProgressDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final CustomProgressDialog dialog = new CustomProgressDialog(
					context, R.style.WaitDialog);
			View layout = inflater.inflate(R.layout.wait_dialog, null);

			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			// set the dialog title
			((TextView) layout.findViewById(R.id.dialogTitle)).setText(title);

			if (keyListener != null) {
				 dialog.setOnKeyListener(new OnKeyListener() {
				 @Override
				 public boolean onKey(DialogInterface dialog, int keyCode,
				 KeyEvent event) {
				 // TODO Auto-generated method stub
				 //keyListener.onKey(dialog, keyCode, event);
				 return false;
				 }
				 });
			}

			dialog.setContentView(layout);
			return dialog;
		}

	}

}