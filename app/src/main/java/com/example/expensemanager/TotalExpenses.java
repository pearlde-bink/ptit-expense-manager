package com.example.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.expensemanager.adapter.ViewPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TotalExpenses extends BaseActivity{

    private Toolbar toolbar;
    private TextView dateText;
    private ImageView prevMonth;
    private ImageView nextMonth;
    private LinearLayout dayLabels;
    private LinearLayout calendarStrip;
    private TextView totalExpenseValue;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private Calendar selectedDate;
    private int selectedDay = 3; // Default selected day as per screenshot

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_expenses);

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        dateText = findViewById(R.id.date_text);
        prevMonth = findViewById(R.id.prev_month);
        nextMonth = findViewById(R.id.next_month);
        dayLabels = findViewById(R.id.day_labels);
        calendarStrip = findViewById(R.id.calendar_strip);
        totalExpenseValue = findViewById(R.id.total_expense_value);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

//        FloatingActionButton fabAdd = findViewById(R.id.fab_add);

        // Set up Toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Initialize selected date
        selectedDate = Calendar.getInstance();
        selectedDate.set(2023, Calendar.FEBRUARY, 1); // Start at Feb 2023
        updateDateAndCalendar();

        // Set up date navigation
        prevMonth.setOnClickListener(v -> {
            selectedDate.add(Calendar.MONTH, -1);
            selectedDay = 1; // Reset selected day to 1st when changing months
            updateDateAndCalendar();
        });

        nextMonth.setOnClickListener(v -> {
            selectedDate.add(Calendar.MONTH, 1);
            selectedDay = 1; // Reset selected day to 1st when changing months
            updateDateAndCalendar();
        });

        // Set up tabs and ViewPager
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new FragmentSpends());
        fragments.add(new FragmentCategories());

        ViewPagerAdapter adapter = new ViewPagerAdapter(this, fragments);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Spends");
            } else {
                tab.setText("Categories");
            }
        }).attach();

        // Update total expense (placeholder)
        totalExpenseValue.setText("$1,600");

        // Set up Bottom Navigation
        setupBottomNavigation();
    }

    @Override
    protected int getSelectedNavItemId() {
        return R.id.nav_home; // Highlight the "Entries" item (as a placeholder, adjust as needed)
    }

    @Override
    protected Class<?> getFabTargetActivity() {
        return Add.class; // FAB leads to AddActivity (for adding income/expense)
    }

    private void updateDateAndCalendar() {
        // Update date text
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM - yyyy", Locale.getDefault());
        dateText.setText(dateFormat.format(selectedDate.getTime()));

        // Update day labels
        dayLabels.removeAllViews();
        String[] daysOfWeek = {"Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"};
        Calendar tempCalendar = (Calendar) selectedDate.clone();
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1; // 0 = Sunday, 1 = Monday, etc.
        int daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Offset to align days correctly
        for (int i = 0; i < firstDayOfWeek; i++) {
            TextView emptyView = new TextView(this);
            emptyView.setText("");
            emptyView.setTextSize(14);
            emptyView.setPadding(8, 4, 8, 4);
            emptyView.setGravity(android.view.Gravity.CENTER);
            emptyView.setWidth(48); // Fixed width for alignment
            dayLabels.addView(emptyView);
        }

        // Add day labels
        for (int i = firstDayOfWeek; i < daysInMonth + firstDayOfWeek; i++) {
            TextView dayLabelView = new TextView(this);
            dayLabelView.setText(daysOfWeek[i % 7]);
            dayLabelView.setTextSize(12);
            dayLabelView.setTextColor(getResources().getColor(android.R.color.darker_gray));
            dayLabelView.setPadding(8, 4, 8, 4);
            dayLabelView.setGravity(android.view.Gravity.CENTER);
            dayLabelView.setWidth(48); // Fixed width for alignment
            dayLabels.addView(dayLabelView);
        }

        // Update calendar strip
        calendarStrip.removeAllViews();
        for (int day = 1; day <= daysInMonth; day++) {
            TextView dayView = new TextView(this);
            dayView.setText(String.valueOf(day));
            dayView.setTextSize(14);
            dayView.setTextColor(day == selectedDay ? getResources().getColor(android.R.color.white) : getResources().getColor(android.R.color.black));
            dayView.setBackground(day == selectedDay ? getResources().getDrawable(R.drawable.selected_day_background) : null);
            dayView.setPadding(8, 8, 8, 8);
            dayView.setGravity(android.view.Gravity.CENTER);
            dayView.setWidth(48); // Fixed width for alignment

            final int currentDay = day;
            dayView.setOnClickListener(v -> {
                // Update selected day
                selectedDay = currentDay;
                for (int i = 0; i < calendarStrip.getChildCount(); i++) {
                    TextView child = (TextView) calendarStrip.getChildAt(i);
                    child.setBackground(null);
                    child.setTextColor(getResources().getColor(android.R.color.black));
                }
                dayView.setBackground(getResources().getDrawable(R.drawable.selected_day_background));
                dayView.setTextColor(getResources().getColor(android.R.color.white));
                Toast.makeText(this, "Selected day: " + selectedDay, Toast.LENGTH_SHORT).show();
            });

            calendarStrip.addView(dayView);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            Toast.makeText(this, "Added successfully", Toast.LENGTH_SHORT).show();
            // Notify fragments to refresh their data
            FragmentSpends spendsFragment = (FragmentSpends) getSupportFragmentManager()
                    .findFragmentByTag("f" + viewPager.getCurrentItem());
            FragmentCategories categoriesFragment = (FragmentCategories) getSupportFragmentManager()
                    .findFragmentByTag("f" + viewPager.getCurrentItem());
            if (spendsFragment != null) {
                // Update SpendsFragment (you'll need to expose a method to refresh its data)
            }
            if (categoriesFragment != null) {
                // Update CategoriesFragment (you'll need to expose a method to refresh its data)
            }
        }
    }
}