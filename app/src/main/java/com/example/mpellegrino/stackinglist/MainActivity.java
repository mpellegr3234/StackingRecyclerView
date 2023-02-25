package com.example.mpellegrino.stackinglist;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private CartItemAdapter mCartItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView stackRecyclerView = findViewById(R.id.stack_recycler_view);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        //TODO: test various offset values
        stackRecyclerView.setLayoutManager(new HorizontalStackLinearLayoutManager(MainActivity.this,
                new HorizontalStackLinearConfig(4, 0.8f, 9, 9), false));
        mCartItemAdapter = new CartItemAdapter(new ArrayList<>());
        stackRecyclerView.setAdapter(mCartItemAdapter);

        //TODO: fix bug where swipe to delete on stack selects lowest stack item
        SwipeUpToDeleteCallback swipeUpToDeleteCallback = new SwipeUpToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                mCartItemAdapter.removeItem(position);
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeUpToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(stackRecyclerView);

        populate();

        //TODO: add items to list when fully unstacked causes a crash
        findViewById(R.id.button).setOnClickListener(v -> populate());
    }

    private void populate() {
        int numberItemsToAdd = 50;
        int numberExistingCartItems = mCartItemAdapter.getItemCount();
        for (int nextItemIndex = numberExistingCartItems; nextItemIndex < numberItemsToAdd + numberExistingCartItems; nextItemIndex++) {
            mCartItemAdapter.getCartItems().add(new CartItem("Item " + nextItemIndex, "$" + nextItemIndex));
            mCartItemAdapter.notifyItemInserted(nextItemIndex);
        }
    }
}
