package com.example.expensemanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.adapter.EntryAdapter;
import com.example.expensemanager.model.Entry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FragmentSpends extends Fragment {

    private RecyclerView spendsRecyclerView;
    private EntryAdapter entryAdapter;
    private List<Entry> expenses;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment_spends, container, false);

        spendsRecyclerView = view.findViewById(R.id.spends_recycler_view);
        expenses = new ArrayList<>();

        entryAdapter = new EntryAdapter(getContext(), expenses);
        spendsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        spendsRecyclerView.setAdapter(entryAdapter);

        return view;
    }

    private void populateSampleExpenses() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(2024, Calendar.FEBRUARY, 20);
    }
}