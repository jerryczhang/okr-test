package com.example.okrtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.okrtest.ui.main.Tab1Fragment;
import com.google.android.material.tabs.TabLayout;
import com.example.okrtest.SwipeCallback;

import java.util.ArrayList;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class GoalDetailActivity extends AppCompatActivity {
    private String goalName;
    private String goalDesc;
    private KRAdapter KRAdapter;
    private RecyclerView KRRecyclerView;
    private SaveManager saveManager;

    private ArrayList<String> KRNames = new ArrayList<>();
    private ArrayList<Integer> KRNums = new ArrayList<>();
    private ArrayList<Integer> KRDens = new ArrayList<>();

    private int numKRs;

    private TextView goalDescTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_detail);
        saveManager = new SaveManager(GoalDetailActivity.this);
        goalDescTextView = (TextView) findViewById(R.id.goalDescTextView);

        goalName = getIntent().getStringExtra(Tab1Fragment.EXTRA_GOAL_NAME);

        ImageView addKR = (ImageView) findViewById(R.id.addKRImageView);
        addKR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resources res = getResources();
                String title = res.getString(R.string.add_kr_dialog_title);
                String positiveName = res.getString(R.string.add_kr_dialog_positive);
                String negativeName = res.getString(R.string.add_kr_dialog_negative);
                String hint = res.getString(R.string.add_kr_dialog_hint);
                DialogFragment addGoalDialog = new InputTextDialog(title, positiveName, negativeName, hint, new InputTextDialog.textDialogListener() {
                    @Override
                    public void onPositiveInput(String text) {
                        if (KRNames.contains(text)) {
                            String title = getString(R.string.kr_exists_dialog_title);
                            String message = getString(R.string.kr_exists_dialog_text);
                            String positiveName = getString(R.string.kr_exists_dialog_positive);
                            OutputTextDialog KRExistsDialog = new OutputTextDialog(title, message, positiveName);
                            KRExistsDialog.show(getSupportFragmentManager(), "kr_exists");
                        }
                        else {
                            addKR(text);
                        }
                    }
                });
                addGoalDialog.show(getSupportFragmentManager(), "add_goal");
            }
        });

        goalDescTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = getString(R.string.change_desc_dialog_title);
                String positiveName = getString(R.string.change_desc_dialog_positive);
                String negativeName = getString(R.string.change_desc_dialog_negative);
                String hint = getString(R.string.change_desc_dialog_hint);
                DialogFragment changeDescDialog = new InputTextDialog(title, positiveName, negativeName, hint, new InputTextDialog.textDialogListener() {
                    @Override
                    public void onPositiveInput(String text) {
                        setGoalDesc(text);
                    }
                });
                changeDescDialog.show(getSupportFragmentManager(), "change_desc");
            }
        });

        KRRecyclerView = (RecyclerView) findViewById(R.id.KRRecyclerView);
        KRRecyclerView.setLayoutManager(new LinearLayoutManager(GoalDetailActivity.this));
        KRRecyclerView.scrollToPosition(0);

        loadKRs();
        KRAdapter = new KRAdapter(GoalDetailActivity.this, KRNames, new RecyclerClickListener() {
            @Override
            public void onViewClicked(final int position) {
                final KRAdapter.ViewHolder v = (KRAdapter.ViewHolder)KRRecyclerView.findViewHolderForAdapterPosition(position);
                String title = getString(R.string.edit_prog_dialog_title);
                String positiveName = getString(R.string.edit_prog_dialog_positive);
                String negativeName = getString(R.string.edit_prog_dialog_negative);
                assert v != null;
                int hintNum = v.getProgNum();
                int hintDen = v.getProgDen();
                DialogFragment editProgDialog = new InputProgDialog(title, positiveName, negativeName, hintNum, hintDen, new InputProgDialog.progDialogListener() {
                    @Override
                    public void onPositiveInput(int num, int den) {
                        v.setProgress(num, den);
                        v.setKRProgressBar();
                        KRNums.set(position, num);
                        KRDens.set(position, den);
                        saveKRProg(position);
                    }
                });
                editProgDialog.show(getSupportFragmentManager(), "add_goal");
            }
            @Override
            public void onItemClicked(final int position, int id) {
            }
        });
        SwipeCallback swipeCallback = new SwipeCallback(KRAdapter, GoalDetailActivity.this);
        /*
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            private Drawable icon;

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                String title, message, positiveName, negativeName, hint;
                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        title = getString(R.string.delete_kr_dialog_title);
                        message = "Delete \"" + KRNames.get(position) + "\"?";
                        positiveName = getString(R.string.delete_kr_dialog_positive);
                        negativeName = getString(R.string.delete_kr_dialog_negative);
                        OutputTextDialog deleteKRDialog = new OutputTextDialog(title, message, positiveName, negativeName, new OutputTextDialog.OutputTextListener() {
                            @Override
                            public void onPositiveInput() {
                                deleteKR(position);
                            }

                            @Override
                            public void onNegativeInput() {
                                KRAdapter.notifyItemChanged(position);
                            }
                        });
                        deleteKRDialog.show(getSupportFragmentManager(), "delete_kr");
                        break;
                    case ItemTouchHelper.RIGHT:
                        title = getString(R.string.rename_kr_dialog_title);
                        positiveName = getString(R.string.rename_kr_dialog_positive);
                        negativeName = getString(R.string.rename_kr_dialog_negative);
                        hint = getString(R.string.rename_kr_dialog_hint);
                        DialogFragment renameKRDialog = new InputTextDialog(title, positiveName, negativeName, hint, new InputTextDialog.textDialogListener() {
                            @Override
                            public void onPositiveInput(String text) {
                                if (KRNames.contains(text)) {
                                    String title = getString(R.string.kr_exists_dialog_title);
                                    String message = getString(R.string.kr_exists_dialog_text);
                                    String positiveName = getString(R.string.kr_exists_dialog_positive);
                                    OutputTextDialog KRExistsDialog = new OutputTextDialog(title, message, positiveName);
                                    KRExistsDialog.show(getSupportFragmentManager(), "kr_exists");
                                } else {
                                    renameKR(position, text);
                                }
                            }
                        });
                        renameKRDialog.show(getSupportFragmentManager(), "rename_kr");
                        KRAdapter.notifyItemChanged(position);
                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(GoalDetailActivity.this, R.color.TOMATO))
                        .addSwipeLeftActionIcon(R.drawable.baseline_delete_black_48dp)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(GoalDetailActivity.this, R.color.KHAKI))
                        .addSwipeRightActionIcon(R.drawable.baseline_create_black_48dp)
                        .create()
                        .decorate();
            }
        };
        */
       ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeCallback);
       itemTouchHelper.attachToRecyclerView(KRRecyclerView);

        KRRecyclerView.setAdapter(KRAdapter);
        KRAdapter.setKRProg(KRNums, KRDens);
        setTitle(goalName);
        goalDesc = saveManager.loadGoalDesc(goalName);
        goalDescTextView.setText(goalDesc);
    }

    private void setGoalDesc(String desc) {
        goalDesc = desc;
        goalDescTextView.setText(desc);
        saveManager.saveGoalDesc(goalName, desc);
    }

    public void addKR(String name) {
        KRNames.add(name);
        KRNums.add(0);
        KRDens.add(100);
        KRAdapter.setKRProg(KRNums, KRDens);
        ++numKRs;
        saveKRs();
        KRAdapter.notifyItemInserted(KRNames.size() - 1);
    }

    private void deleteKR(int position) {
        String KRName = KRNames.get(position);
        KRNames.remove(position);
        KRNums.remove(position);
        KRDens.remove(position);
        --numKRs;
        saveManager.deleteKR(goalName, position, KRName);
        saveKRs();
        KRAdapter.notifyItemRemoved(position);
    }

    private void loadKRs() {
        SaveManager.SaveData saveData = saveManager.loadKRs(goalName);
        numKRs = saveData.getNumData();
        KRNames = (ArrayList<String>)saveData.getListData(0);
        KRNums = (ArrayList<Integer>)saveData.getListData(1);
        KRDens = (ArrayList<Integer>)saveData.getListData(2);
    }

    private void saveKRs() {
        saveManager.saveKRNames(goalName, KRNames);
    }

    private void renameKR(int position, String name) {
        String KRName = KRNames.get(position);
        KRNames.set(position, name);
        saveManager.deleteKR(goalName, position, KRName);
        saveKRProg(position);
        saveManager.saveKRNames(goalName, KRNames);
        KRAdapter.notifyItemChanged(position);
    }

    private void saveKRProg(int position) {
        String KRName = KRNames.get(position);
        KRAdapter.ViewHolder v = (KRAdapter.ViewHolder)KRRecyclerView.findViewHolderForAdapterPosition(position);
        assert v != null;
        saveManager.saveKRPRog(goalName, KRName, v.getProgNum(), v.getProgDen());
    }

}
