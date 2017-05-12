package com.github.codetanzania.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.codetanzania.adapter.InternalNotesAdapter;
import com.github.codetanzania.model.Comment;
import com.github.codetanzania.Constants;

import java.util.ArrayList;
import java.util.List;

import tz.co.codetanzania.R;

public class InternalNoteFragment extends Fragment {


    public static InternalNoteFragment getInstance(Bundle args) {
        InternalNoteFragment iNF = new InternalNoteFragment();
        iNF.setArguments(args);
        return iNF;
    }

    @Override public View onCreateView(
            LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_internal_note, group, false);
    }

    @Override public void onViewCreated(
            View fragView, Bundle savedInstanceState) {
        RecyclerView rvLogs = (RecyclerView) fragView.findViewById(R.id.rv_Logs);
        // create adapter
        List<Comment> comments = getArguments().getParcelableArrayList(Constants.Const.ISSUE_COMMENTS);
        comments = comments == null ? new ArrayList<Comment>() : comments;
        InternalNotesAdapter iAdapter = new InternalNotesAdapter(getActivity(), comments);
        rvLogs.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvLogs.setAdapter(iAdapter);
    }
}
