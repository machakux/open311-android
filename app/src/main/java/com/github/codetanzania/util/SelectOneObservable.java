package com.github.codetanzania.util;


import java.util.List;
import java.util.Observable;

public class SelectOneObservable<T> extends Observable {
    private final List<T> mSelectableList;
    private T mSelection;

    public SelectOneObservable(List<T> mSelectableList) {
        this.mSelectableList = mSelectableList;
    }

    public void addSelectable(T selection) {
        this.mSelectableList.add(selection);
    }

    public void removeSelectable(int position) {
        this.mSelectableList.remove(position);
    }

    public T getSelectableAt(int position) {
        return this.mSelectableList.get(position);
    }

    public void select(int position) {
        this.mSelection = getSelectableAt(position);
        setChanged();
        notifyObservers(position);
    }

    public boolean isEmpty() {
        return this.mSelectableList.isEmpty();
    }

    public int size() {
        return this.mSelectableList.size();
    }

    public List<T> getSelectableList() {
        return mSelectableList;
    }

    public T getSelection() {
        return this.mSelection;
    }
}
