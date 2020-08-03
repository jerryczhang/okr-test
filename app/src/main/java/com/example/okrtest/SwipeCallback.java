package com.example.okrtest;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Icon;
import android.view.View;
import android.widget.Adapter;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.okrtest.R;

public class SwipeCallback extends ItemTouchHelper.SimpleCallback {
    private Context context;
    private SwipeListener listener;
    private Drawable leftIcon;
    private Drawable rightIcon;
    private GradientDrawable leftBackground;
    private GradientDrawable rightBackground;

    public SwipeCallback(Context context, SwipeListener listener) {
        super(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.listener = listener;
        leftIcon = ContextCompat.getDrawable(context, R.drawable.baseline_archive_black_48dp);
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
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
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
