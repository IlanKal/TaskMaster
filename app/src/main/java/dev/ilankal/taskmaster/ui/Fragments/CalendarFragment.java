package dev.ilankal.taskmaster.ui.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import dev.ilankal.taskmaster.R;
import dev.ilankal.taskmaster.databinding.FragmentCalendarBinding;
public class CalendarFragment extends Fragment {
    private CalendarView calendarView;
    private Calendar calendar;
    private FragmentCalendarBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        findVies(root);
        initViews();

        return root;
    }

    private void findVies(View root) {
        calendarView = root.findViewById(R.id.calendarView);
        calendar = Calendar.getInstance();
    }
    private void initViews() {
        currentDate();
        getDate();
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int day) {
                Toast.makeText(getActivity(), dateView(day, month, year), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String dateView(int day, int month, int year) {
        String str;
        if (day < 10){
            str = "0" + day;
        }
        else {
            str = "" + day;
        }
        if (month < 10){
            str += "/0" +(month +1);
        }
        else {
            str += "/" + (month +1);
        }
        str += "/" + year;

        return str;
    }
    public void getDate(){
        long date = calendarView.getDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        calendar.setTimeInMillis(date);
        String selectedDate = simpleDateFormat.format(calendar.getTime());
        Toast.makeText(getActivity(),selectedDate,Toast.LENGTH_SHORT).show();
    }
    public void currentDate(){
        long time = calendar.getTimeInMillis();
        calendarView.setDate(time);
    }

    // maybe i will use
    public void setDate(int day, int month, int year){
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        long time = calendar.getTimeInMillis();
        calendarView.setDate(time);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}