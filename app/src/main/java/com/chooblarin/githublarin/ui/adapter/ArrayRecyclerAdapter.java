package com.chooblarin.githublarin.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.chooblarin.githublarin.ui.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.Collection;

public abstract class ArrayRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    final protected LayoutInflater inflater;
    final protected ArrayList<T> list;

    protected OnItemClickListener onItemClickListener;

    public ArrayRecyclerAdapter(@NonNull Context context) {
        inflater = LayoutInflater.from(context);
        list = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public T getItem(int position) {
        return list.get(position);
    }

    public void add(T item) {
        list.add(item);
        notifyItemInserted(list.size() - 1);
    }

    public void addAll(Collection<T> items) {
        for (T item : items) {
            add(item);
        }
    }

    public void remove(T item) {
        int position = list.indexOf(item);
        if (-1 < position) {
            list.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        list.clear();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    protected void dispathItemClickEvent(View view, int positon) {
        onItemClickListener.onItemClick(view, positon);
    }
}
