package com.example.okrtest;

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
    private RecyclerView.Adapter adapter;
    private Drawable leftIcon;
    private Drawable rightIcon;
    private GradientDrawable leftBackground;
    private GradientDrawable rightBackground;

    public SwipeCallback(RecyclerView.Adapter adapter, Context context) {
        super(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
        leftIcon = ContextCompat.getDrawable(context, R.drawable.baseline_create_black_48dp);
        rightIcon = ContextCompat.getDrawable(context, R.drawable.baseline_delete_black_48dp);
        leftBackground = new GradientDrawable();
        rightBackground = new GradientDrawable();
        leftBackground.setColor(ContextCompat.getColor(context, R.color.KHAKI));
        rightBackground.setColor(ContextCompat.getColor(context, R.color.TOMATO));
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;

        int iconMargin = (itemView.getHeight() - leftIcon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + iconMargin;
        int iconBottom = iconTop + leftIcon.getIntrinsicHeight();

        int backgroundCornerOffset = 20;
        if (dX > 0) {
            leftBackground.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
            rightBackground.setBounds(0, 0, 0, 0);

            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = itemView.getLeft() + iconMargin + leftIcon.getIntrinsicWidth();
            leftIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
        } else if (dX < 0) {
            rightBackground.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            leftBackground.setBounds(0, 0, 0, 0);

            int iconLeft = itemView.getRight() - iconMargin - rightIcon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            rightIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
        } else {
            leftBackground.setBounds(0, 0, 0, 0);
            rightBackground.setBounds(0, 0, 0, 0);
        }
        leftBackground.draw(c);
        rightBackground.draw(c);
        leftIcon.draw(c);
        rightIcon.draw(c);
    }
}
