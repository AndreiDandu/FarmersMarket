package dandu.andrei.farmersmarket.Util;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import dandu.andrei.farmersmarket.Ad.CustomRecycledViewAdapter;
import dandu.andrei.farmersmarket.R;

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {
    private RecyclerItemTouchHelperListener listener;
    private Context context;

    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener listener, Context context) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
        this.context = context;

    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            final View foregroundView = ((CustomRecycledViewAdapter.MyViewHolder) viewHolder).foreground;

            getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((CustomRecycledViewAdapter.MyViewHolder) viewHolder).foreground;
        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final View foregroundView = ((CustomRecycledViewAdapter.MyViewHolder) viewHolder).foreground;
        getDefaultUIUtil().clearView(foregroundView);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
//        final View foregroundView = ((CustomRecycledViewAdapter.MyViewHolder) viewHolder).foreground;
//
//        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
//                actionState, isCurrentlyActive);
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            View itemView = viewHolder.itemView;
            Paint paint = new Paint();
            Bitmap icon;

            if (dX > 0) {
                //swipe right
                try {
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 5;
                    viewHolder.itemView.setTranslationX(dX / 5);

                    paint.setColor(Color.parseColor("#D32F2F"));
                    RectF background = new RectF((float) itemView.getLeft() + dX / 5, (float) itemView.getTop(), (float) itemView.getLeft(), (float) itemView.getBottom());
                    c.drawRect(background, paint);
                    icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.fui_ic_phone_white_24dp);
                    RectF icon_dest = new RectF((float) (itemView.getLeft() + dX / 7), (float) itemView.getTop() + width, (float) itemView.getLeft() + dX / 20, (float) itemView.getBottom() - width);
                    c.drawBitmap(icon, null, icon_dest, paint);


//                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
//                    float width = height / 5;
//                    viewHolder.itemView.setTranslationX(dX / 5);
//
//                    paint.setColor(Color.parseColor("#D32F2F"));
//                    RectF background = new RectF((float) itemView.getRight() + dX / 5, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
//                    c.drawRect(background, paint);
//                    icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.farmer_market_image);
//                    RectF icon_dest = new RectF((float) (itemView.getRight() + dX / 7), (float) itemView.getTop() + width, (float) itemView.getRight() + dX / 20, (float) itemView.getBottom() - width);
//                    c.drawBitmap(icon, null, icon_dest, paint);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                try {
                    //swipe left
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 5;
                    viewHolder.itemView.setTranslationX(dX / 5);

                    paint.setColor(Color.parseColor("#D32F2F"));
                    RectF background = new RectF((float) itemView.getRight() + dX / 5, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                    c.drawRect(background, paint);
                    icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.farmer_market_image);
                    RectF icon_dest = new RectF((float) (itemView.getRight() + dX / 7), (float) itemView.getTop() + width, (float) itemView.getRight() + dX / 20, (float) itemView.getBottom() - width);
                    c.drawBitmap(icon, null, icon_dest, paint);
//                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
//                    float width = height / 5;
//                    viewHolder.itemView.setTranslationX(dX / 5);
//
//                    paint.setColor(Color.parseColor("#D32F2F"));
//                    RectF background = new RectF((float) itemView.getLeft() + dX / 5, (float) itemView.getTop(), (float) itemView.getLeft(), (float) itemView.getBottom());
//                    c.drawRect(background, paint);
//                    icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.farmer_market_image);
//                    RectF icon_dest = new RectF((float) (itemView.getLeft() + dX / 7), (float) itemView.getTop() + width, (float) itemView.getLeft() + dX / 20, (float) itemView.getBottom() - width);
//                    c.drawBitmap(icon, null, icon_dest, paint);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            } else {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    public interface RecyclerItemTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }

}