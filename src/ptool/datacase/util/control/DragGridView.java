package ptool.datacase.util.control;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

public class DragGridView extends GridView
{

	private int							dragPosition;		// ��ʼ��ק��λ��
	private int							dropPosition;		// ������ק��λ��
	private int							dragPointX;		// �����item��x����
	private int							dragPointY;		// �����item��y����
	private int							dragOffsetX;
	private int							dragOffsetY;
	private ImageView					dragImageView;		// �϶�item��preview

	private WindowManager				windowManager;
	private WindowManager.LayoutParams	windowParams;

	private int							itemHeight;
	private final int					numColumns	= 3;	// ÿ��3��

	public DragGridView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public void setOnItemLongClickListener(final MotionEvent ev, int xx, int yy)
	{
		final int x = xx;
		final int y = yy;
		this.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3)
			{
				dragPosition = dropPosition = arg2;
				if (dragPosition == AdapterView.INVALID_POSITION)
				{

				}
				ViewGroup itemView = (ViewGroup) getChildAt(dragPosition
						- getFirstVisiblePosition());
				// �õ���ǰ����item�ڲ���ƫ���� �������item���Ͻǵ�����
				dragPointX = 0;
				dragOffsetY = 0;
				dragPointX = x - itemView.getLeft();
				dragPointY = y - itemView.getTop();

				dragOffsetX = (int) (ev.getRawX() - x);
				dragOffsetY = (int) (ev.getRawY() - y);

				itemHeight = itemView.getHeight();

				// L.l("========================y:" + y + " getRawY:"
				// + ev.getRawY());

				// �������3
				// ÿ�ζ�����һ��cache����������һ��bitmap
				itemView.destroyDrawingCache();
				itemView.setDrawingCacheEnabled(true);
				Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache());
				// ����item������ͼ
				startDrag(bm, x, y);
				return true;
			};
		});
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		if (ev.getAction() == MotionEvent.ACTION_DOWN)
		{
			int x = (int) ev.getX();
			int y = (int) ev.getY();
			setOnItemLongClickListener(ev, x, y);
		}
		return super.onInterceptTouchEvent(ev);
		// return false;
	}

	private void startDrag(Bitmap bm, int x, int y)
	{
		stopDrag();
		windowParams = new WindowManager.LayoutParams();
		windowParams.gravity = Gravity.TOP | Gravity.LEFT;// ��������
		// �õ�preview���Ͻ��������Ļ������
		windowParams.x = x - dragPointX + dragOffsetX;
		windowParams.y = y - dragPointY + dragOffsetY;
		// L.l("==================windowParams.y==============="+windowParams.y);
		// ���ÿ�͸�
		windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

		windowParams.format = PixelFormat.TRANSLUCENT;
		windowParams.windowAnimations = 0;

		ImageView iv = new ImageView(getContext());
		iv.setImageBitmap(bm);
		windowManager = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);// "window"
		windowManager.addView(iv, windowParams);
		dragImageView = iv;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{

		if (dragImageView != null
				&& dragPosition != AdapterView.INVALID_POSITION)
		{
			int x = (int) ev.getX();
			int y = (int) ev.getY();
			switch (ev.getAction())
			{
				case MotionEvent.ACTION_MOVE:
					onDrag(x, y);
					break;
				case MotionEvent.ACTION_UP:
					stopDrag();
					onDrop(x, y);
					setEnabled(true);
					break;
			}
		}
		return super.onTouchEvent(ev);
	}

	private void onDrag(int x, int y)
	{
		if (dragImageView != null)
		{
			windowParams.alpha = 0.6f;
			windowParams.x = x - dragPointX + dragOffsetX;
			windowParams.y = y - dragPointY + dragOffsetY;
			// L.l("=================windowParams.y=====000========"+windowParams.y);
			windowManager.updateViewLayout(dragImageView, windowParams);
		}

		int tempScrollX = x - dragPointX + dragOffsetX;
		int tempScrollY = y - dragPointY + dragOffsetY;

		int rangeY = itemHeight;
		int maxHeight = getHeight() - rangeY;

		int position = pointToPosition(x, y);

		int gridViewCount = this.getCount();
		int allContainCount = gridViewCount;
		int leftCount = gridViewCount % numColumns;
		if (leftCount != 0)
		{
			allContainCount += (numColumns - leftCount);
		}
		int upMaxPosition = allContainCount - numColumns;
		// ���position��������item
		if (position >= upMaxPosition || position < numColumns)
		{
			setEnabled(false);
		}
		else
		{
			if (tempScrollY < rangeY)// ����Ư����view�Ѿ������һ��,��ѵ�ǰ��gridView����һ��
			{
				setEnabled(true);
				int position2 = getFirstVisiblePosition();
				smoothScrollToPosition(position2 - 1);
				// scrollTo(0, -itemHeight);
			}
			else
			{
				if (tempScrollY > maxHeight)
				{
					setEnabled(true);
					int position1 = getLastVisiblePosition();
					smoothScrollToPosition(position1 + 1);
					// scrollTo(0, itemHeight);
				}
			}
		}

	}

	private void onDrop(int x, int y)
	{
		int tempPosition = pointToPosition(x, y);
		if (tempPosition != AdapterView.INVALID_POSITION)
		{
			dropPosition = tempPosition;
		}
		if (dropPosition != dragPosition)
		{
			System.out.println("dragPosition: " + dragPosition
					+ " dropPosition: " + dropPosition);
			GridImageAdapter adapter = (GridImageAdapter) this.getAdapter();
			adapter.exchange(dragPosition, dropPosition);

			// �������3
			/*
			 * ViewGroup itemView1 = (ViewGroup)getChildAt(dragPosition - getFirstVisiblePosition()); ViewGroup
			 * itemView2 = (ViewGroup)getChildAt(dropPosition - getFirstVisiblePosition());
			 * itemView1.destroyDrawingCache(); itemView2.destroyDrawingCache();
			 */
		}
	}

	private void stopDrag()
	{
		if (dragImageView != null)
		{
			windowManager.removeView(dragImageView);
			dragImageView = null;
		}
	}
}
