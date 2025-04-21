package com.example.expensemanager.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.expensemanager.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarAdapter extends BaseAdapter {

    private Context context;
    private Calendar month;
    private List<String> days;
    private int selectedPosition = -1;
    private int firstDayPosition;
    private int lastDayPosition;

    public CalendarAdapter(Context context, Calendar month) {
        this.context = context;
        this.month = (Calendar) month.clone();
        this.days = new ArrayList<>();
        populateDays();
    }

    private void populateDays() {
        days.clear();
        month.set(Calendar.DAY_OF_MONTH, 1);

        // Get the first day of the month (0 = Sunday, 1 = Monday, etc.)
        int firstDayOfMonth = month.get(Calendar.DAY_OF_WEEK) - 2; // Adjust for Monday as first day
        if (firstDayOfMonth < 0) {
            firstDayOfMonth += 7; // Adjust for negative values
        }

        // Add days of the previous month
        Calendar prevMonth = (Calendar) month.clone();
        prevMonth.add(Calendar.MONTH, -1);
        int maxDaysPrevMonth = prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < firstDayOfMonth; i++) {
            days.add(String.valueOf(maxDaysPrevMonth - firstDayOfMonth + i + 1));
        }

        // Add days of the current month
        int maxDays = month.getActualMaximum(Calendar.DAY_OF_MONTH);
        firstDayPosition = firstDayOfMonth;
        lastDayPosition = firstDayOfMonth + maxDays - 1;
        for (int i = 1; i <= maxDays; i++) {
            days.add(String.valueOf(i));
        }

        // Add days of the next month to fill the grid
        int nextMonthDay = 1;
        while (days.size() % 7 != 0) {
            days.add(String.valueOf(nextMonthDay++));
        }
    }

    @Override
    public int getCount() {
        return days.size();
    }

    @Override
    public Object getItem(int position) {
        return days.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView dayText;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_calendar_day, parent, false);
            dayText = (TextView) convertView;
            convertView.setTag(dayText);
        } else {
            dayText = (TextView) convertView.getTag();
        }

        String day = days.get(position);

        if (day.isEmpty()) {
            dayText.setText("");
            dayText.setBackgroundResource(0);
        } else {
            dayText.setText(day);
            dayText.setBackgroundResource(R.drawable.calendar_day_background);
            dayText.setSelected(position == selectedPosition);

            // Adjust text color based on whether the day is in the current month
            if (position < firstDayPosition || position > lastDayPosition) {
                dayText.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
            } else {
                dayText.setTextColor(context.getResources().getColor(position == selectedPosition ? android.R.color.white : android.R.color.black));
            }
        }

        return convertView;
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
        populateDays();
        notifyDataSetChanged();
    }

    public int getDayAtPosition(int position) {
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