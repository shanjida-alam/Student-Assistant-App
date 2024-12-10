package com.example.studentassistantapp.ui.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentassistantapp.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment {
    private TextView welcomeText, timeGreeting, dailyQuoteText, temperatureText;
    private ImageView weatherIcon;
    private RecyclerView featureGrid;
    private Handler handler;
    private FeatureAdapter featureAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        handler = new Handler(Looper.getMainLooper());

        initViews(view);
        setupFeatureGrid();
        setupAnimations();
        updateGreeting();
        updateWeather();
        showDailyQuote();
        startBackgroundUpdates();

        return view;
    }

    private void initViews(View view) {
        welcomeText = view.findViewById(R.id.welcomeText);
        timeGreeting = view.findViewById(R.id.timeGreeting);
        dailyQuoteText = view.findViewById(R.id.dailyQuoteText);
        temperatureText = view.findViewById(R.id.temperatureText);
        weatherIcon = view.findViewById(R.id.weatherIcon);
        featureGrid = view.findViewById(R.id.featureGrid);
    }

    private void setupAnimations() {
        welcomeText.setAlpha(0f);
        timeGreeting.setAlpha(0f);

        welcomeText.animate()
                .alpha(1f)
                .setDuration(1000)
                .setStartDelay(300)
                .setInterpolator(new OvershootInterpolator())
                .start();

        timeGreeting.animate()
                .alpha(1f)
                .setDuration(1000)
                .setStartDelay(600)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    private void setupFeatureGrid() {
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        featureGrid.setLayoutManager(layoutManager);

        List<FeatureItem> features = new ArrayList<>();
        features.add(new FeatureItem("To-Do List", R.drawable.ic_todo));
        features.add(new FeatureItem("Note Taker", R.drawable.ic_note_taker));
        features.add(new FeatureItem("Finance", R.drawable.ic_finance));
        features.add(new FeatureItem("Voice Notes", R.drawable.ic_voice));

        featureAdapter = new FeatureAdapter(features);
        featureGrid.setAdapter(featureAdapter);
    }

    private void updateGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (hourOfDay >= 0 && hourOfDay < 12) {
            greeting = "Good Morning";
        } else if (hourOfDay >= 12 && hourOfDay < 16) {
            greeting = "Good Afternoon";
        } else if (hourOfDay >= 16 && hourOfDay < 21) {
            greeting = "Good Evening";
        } else {
            greeting = "Good Night";
        }

        timeGreeting.setText(greeting);
    }

    private void showDailyQuote() {
        String[] quotes = {
                "The secret of getting ahead is getting started. - Mark Twain",
                "Education is the passport to the future. - Malcolm X",
                "Success is not final, failure is not fatal. - Winston Churchill",
                "The best way to predict the future is to create it. - Peter Drucker",
                "Learn from yesterday, live for today, hope for tomorrow. - Albert Einstein"
        };
        Random random = new Random();
        dailyQuoteText.setText(quotes[random.nextInt(quotes.length)]);
    }

    private void updateWeather() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 6 && hour < 18) {
            weatherIcon.setImageResource(R.drawable.ic_weather_sunny);
            temperatureText.setText("25°C");
        } else {
            weatherIcon.setImageResource(R.drawable.ic_weather_night);
            temperatureText.setText("20°C");
        }
    }

    private void startBackgroundUpdates() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAdded()) {
                    updateGreeting();
                    handler.postDelayed(this, 60000);
                }
            }
        }, 60000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    // Feature Item class
    private static class FeatureItem {
        String title;
        int iconResource;

        FeatureItem(String title, int iconResource) {
            this.title = title;
            this.iconResource = iconResource;
        }
    }

    // Feature Adapter class
    private static class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.ViewHolder> {
        private final List<FeatureItem> features;

        FeatureAdapter(List<FeatureItem> features) {
            this.features = features;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_feature, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            FeatureItem item = features.get(position);
            holder.titleText.setText(item.title);
            holder.iconImage.setImageResource(item.iconResource);

            holder.itemView.setOnClickListener(v -> {
                switch (position) {
                    case 0: // Todo List
                        Navigation.findNavController(holder.itemView)
                                .navigate(R.id.nav_todo);
                        break;
                    case 1: // Notes
                        Navigation.findNavController(holder.itemView)
                                .navigate(R.id.nav_notetaker);
                        break;
                    case 2: // Finance
                        Navigation.findNavController(holder.itemView)
                                .navigate(R.id.nav_personalFinanace);
                        break;
                    case 3: // Voice Notes
                        // Navigate to Voice Notes Fragment
                        break;
                }
            });
        }


        @Override
        public int getItemCount() {
            return features.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView iconImage;
            TextView titleText;

            ViewHolder(View view) {
                super(view);
                iconImage = view.findViewById(R.id.featureIcon);
                titleText = view.findViewById(R.id.featureTitle);
            }
        }
    }
}