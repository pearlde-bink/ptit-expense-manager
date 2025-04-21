package com.example.expensemanager.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarRecyclerAdapter extends RecyclerView.Adapter<CalendarRecyclerAdapter.DayViewHolder> {

    private Context context;
    private Calendar month;
    private List<String> days;
    private int selectedPosition = -1;
    private int firstDayPosition;
    private int lastDayPosition;
    private OnDayClickListener onDayClickListener;

    public interface OnDayClickListener {
        void onDayClick(int position, int day, boolean isInCurrentMonth);
    }

    public CalendarRecyclerAdapter(Context context, Calendar month, OnDayClickListener listener) {
        this.context = context;
        this.month = (Calendar) month.clone();
        this.days = new ArrayList<>();
        this.onDayClickListener = listener;
        new PopulateDaysTask().execute();
    }

    private class PopulateDaysTask extends AsyncTask<Void, Void, List<String>> {
        private Calendar taskMonth;
        private int taskFirstDayPosition;
        private int taskLastDayPosition;

        @Override
        protected void onPreExecute() {
            taskMonth = (Calendar) month.clone();
            days.clear();
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            List<String> newDays = new ArrayList<>();
            taskMonth.set(Calendar.DAY_OF_MONTH, 1);

            // Get the first day of the month (0 = Sunday, 1 = Monday, etc.)
            int firstDayOfMonth = taskMonth.get(Calendar.DAY_OF_WEEK) - 2; // Adjust for Monday as first day
            if (firstDayOfMonth < 0) {
                firstDayOfMonth += 7; // Adjust for negative values
            }

            // Add days of the previous month
            Calendar prevMonth = (Calendar) taskMonth.clone();
            prevMonth.add(Calendar.MONTH, -1);
            int maxDaysPrevMonth = prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int i = 0; i < firstDayOfMonth; i++) {
                newDays.add(String.valueOf(maxDaysPrevMonth - firstDayOfMonth + i + 1));
            }

            // Add days of the current month
            int maxDays = taskMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
            taskFirstDayPosition = firstDayOfMonth;
            taskLastDayPosition = firstDayOfMonth + maxDays - 1;
            for (int i = 1; i <= maxDays; i++) {
                newDays.add(String.valueOf(i));
            }

            // Add days of the next month to fill the grid
            int nextMonthDay = 1;
            while (newDays.size() % 7 != 0) {
                newDays.add(String.valueOf(nextMonthDay++));
            }

            return newDays;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            days.addAll(result);
            firstDayPosition = taskFirstDayPosition;
            lastDayPosition = taskLastDayPosition;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_calendar_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        if (position >= days.size()) return; // Guard against async delay

        String day = days.get(position);

        if (day.isEmpty()) {
            holder.dayText.setText("");
            holder.dayText.setBackgroundResource(0);
        } else {
            holder.dayText.setText(day);
            holder.dayText.setBackgroundResource(R.drawable.calendar_day_background);
            holder.dayText.setSelected(position == selectedPosition);

            // Adjust text color based on whether the day is in the current month
            if (position < firstDayPosition || position > lastDayPosition) {
                holder.dayText.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
            } else {
                holder.dayText.setTextColor(context.getResources().getColor(position == selectedPosition ? android.R.color.white : android.R.color.black));
            }
        }

        holder.dayText.setOnClickListener(v -> {
            int dayNumber = getDayAtPosition(position);
            if (dayNumber != -1) {
                selectedPosition = position;
                notifyDataSetChanged();
                boolean isInCurrentMonth = isDayInCurrentMonth(position);
                onDayClickListener.onDayClick(position, dayNumber, isInCurrentMonth);
            }
        });
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView dayText;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            dayText = (TextView) itemView;
        }
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void updateMonth(Calendar newMonth) {
        this.month = (Calendar) newMonth.clone();
        new PopulateDaysTask().execute();
    }

    public int getDayAtPosition(int position) {
        if (position >= days.size()) return -1; // Guard against async delay
        String day = days.get(position);
        try {
            return Integer.parseInt(day);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public boolean isDayInCurrentMonth(int position) {
        return position >= firstDayPosition && position <= lastDayPosition;
    }
}