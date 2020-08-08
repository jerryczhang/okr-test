package com.example.okrtest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeCallback extends ItemTouchHelper.SimpleCallback {
    private Context context;
    private SwipeListener listener;
    private Drawable leftIcon;
    private Drawable rightIcon;
    private GradientDrawable leftBackground;
    private GradientDrawable rightBackground;
    private String layout;

    public static final String DEFAULT = "default";
    public static final String ARCHIVED = "archived";
    public static final String KR = "kr";

    public SwipeCallback(Context context, String layout, SwipeListener listener) {
        super(0,0);
        this.listener = listener;
        this.layout = layout;
        switch (layout) {
            case DEFAULT:
            case KR:
                leftIcon = ContextCompat.getDrawable(context, R.drawable.baseline_archive_black_48dp);
                break;
            case ARCHIVED:
                leftIcon = ContextCompat.getDrawable(context, R.drawable.baseline_unarchive_black_48dp);
                break;
        }
        rightIcon = ContextCompat.getDrawable(context, R.drawable.baseline_delete_black_48dp);
        leftBackground = new GradientDrawable();
        rightBackground = new GradientDrawable();
        leftBackground.setColor(ContextCompat.getColor(context, R.color.MEDIUMSEAGREEN));
        rightBackground.setColor(ContextCompat.getColor(context, R.color.TOMATO));
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        listener.onMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        switch (direction) {
            case ItemTouchHelper.LEFT:
                listener.onSwipeLeft(viewHolder.getAdapterPosition());
                break;
            case ItemTouchHelper.RIGHT:
                listener.onSwipeRight(viewHolder.getAdapterPosition());
                break;
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;

        int iconHeight = leftIcon.getIntrinsicHeight() / 2;
        int iconWidth = leftIcon.getIntrinsicWidth() / 2;
        int iconVerticalMargin = (itemView.getHeight() - iconHeight) / 2;
        int iconHorizontalMargin = iconVerticalMargin / 2;
        int iconTop = itemView.getTop() + iconVerticalMargin;
        int iconBottom = iconTop + iconHeight;

        int backgroundCornerOffset = 20;
        if (dX > 0) {
            leftBackground.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
            rightBackground.setBounds(0, 0, 0, 0);

            int iconLeft = itemView.getLeft() + iconHorizontalMargin;
            int iconRight = iconLeft + iconWidth;
            leftIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            rightIcon.setBounds(0,0,0,0);
        } else if (dX < 0) {
            rightBackground.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            leftBackground.setBounds(0, 0, 0, 0);

            int iconRight = itemView.getRight() - iconHorizontalMargin;
            int iconLeft = iconRight - iconWidth;
            rightIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            leftIcon.setBounds(0,0,0,0);
        } else {
            leftBackground.setBounds(0, 0, 0, 0);
            rightBackground.setBounds(0, 0, 0, 0);
            leftIcon.setBounds(0,0,0,0);
            rightIcon.setBounds(0,0,0,0);
        }
        leftBackground.draw(c);
        rightBackground.draw(c);
        leftIcon.draw(c);
        rightIcon.draw(c);
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int swipeFlags = 0;
        int dragFlags = 0;
        switch (layout) {
            case DEFAULT:
            case ARCHIVED:
                swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                break;
            case KR:
                swipeFlags = ItemTouchHelper.LEFT;
                break;
        }
        dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    public interface SwipeListener {
        void onMove(int fromPosition, int toPosition);
        void onSwipeLeft(final int position);
        void onSwipeRight(final int position);
    }
}
