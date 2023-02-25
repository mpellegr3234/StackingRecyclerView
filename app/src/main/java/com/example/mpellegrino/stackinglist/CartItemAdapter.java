package com.example.mpellegrino.stackinglist;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {
    private static final String TAG = CartItemAdapter.class.getName();
    private ArrayList<CartItem> mCartItems;

    public CartItemAdapter(ArrayList<CartItem> cartItems){
        mCartItems = cartItems;
    }

    public ArrayList<CartItem> getCartItems() {
        return mCartItems;
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "createviewholder viewType= " + viewType);
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new CartItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder cartItemViewHolder, int position) {
        Log.d(TAG, "bindviewholder position= " + position);
        cartItemViewHolder.mName.setText(mCartItems.get(position).getName());
        cartItemViewHolder.mPrice.setText(mCartItems.get(position).getPrice());
    }

    @Override
    public int getItemCount() {
        return mCartItems.size();
    }

    public void removeItem(int position) {
        mCartItems.remove(position);
        notifyItemRemoved(position);
    }

    public class CartItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView mName;
        private final TextView mPrice;
        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.name);
            mPrice = itemView.findViewById(R.id.price);
        }
    }
}
