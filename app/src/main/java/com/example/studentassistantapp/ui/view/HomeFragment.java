package com.example.studentassistantapp.ui.view;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentassistantapp.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment {
    private TextView welcomeText, timeGreeting, dailyQuoteText, temperatureText;
    private ImageView weatherIcon;
    private RecyclerView featureGrid;
    private MaterialCardView quoteCard, weatherWidget;
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
        quoteCard = view.findViewById(R.id.quoteCard);
        weatherWidget = view.findViewById(R.id.weatherWidget);

        // Set initial alpha to 0 for animated views
        welcomeText.setAlpha(0f);
        timeGreeting.setAlpha(0f);
        quoteCard.setAlpha(0f);
        weatherWidget.setAlpha(0f);
    }

    private void setupAnimations() {
        // Animate welcome text with fade and slight slide
        welcomeText.setTranslationX(-50f);
        ObjectAnimator welcomeFade = ObjectAnimator.ofFloat(welcomeText, "alpha", 0f, 1f);
        ObjectAnimator welcomeSlide = ObjectAnimator.ofFloat(welcomeText, "translationX", -50f, 0f);
        welcomeFade.setDuration(1000);
        welcomeSlide.setDuration(1000);
        welcomeFade.setStartDelay(300);
        welcomeSlide.setStartDelay(300);
        welcomeFade.start();
        welcomeSlide.start();

        // Animate greeting text
        timeGreeting.setTranslationX(-50f);
        ObjectAnimator greetingFade = ObjectAnimator.ofFloat(timeGreeting, "alpha", 0f, 1f);
        ObjectAnimator greetingSlide = ObjectAnimator.ofFloat(timeGreeting, "translationX", -50f, 0f);
        greetingFade.setDuration(1000);
        greetingSlide.setDuration(1000);
        greetingFade.setStartDelay(600);
        greetingSlide.setStartDelay(600);
        greetingFade.start();
        greetingSlide.start();

        // Animate weather widget
        weatherWidget.setTranslationX(50f);
        weatherWidget.animate()
                .alpha(1f)
                .translationX(0f)
                .setDuration(1000)
                .setStartDelay(900)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // Animate quote card
        quoteCard.setTranslationY(100f);
        quoteCard.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(800)
                .setStartDelay(1200)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // Setup RecyclerView animation
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(requireContext(), resId);
        featureGrid.setLayoutAnimation(animation);
    }

    private void setupFeatureGrid() {
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        featureGrid.setLayoutManager(layoutManager);

        List<FeatureItem> features = new ArrayList<>();
        features.add(new FeatureItem("To-Do List", R.drawable.ic_todo, "Manage your tasks"));
        features.add(new FeatureItem("Note Taker", R.drawable.ic_note_taker, "Create and organize notes"));
        features.add(new FeatureItem("Finance", R.drawable.ic_finance, "Track your expenses"));
        features.add(new FeatureItem("Voice Notes", R.drawable.ic_voice, "Record voice memos"));

        featureAdapter = new FeatureAdapter(features, this::handleFeatureClick);
        featureGrid.setAdapter(featureAdapter);
    }

    private void handleFeatureClick(int position) {
        try {
            switch (position) {
                case 0: // Todo List
                    Navigation.findNavController(requireView())
                            .navigate(R.id.nav_todo);
                    break;
                case 1: // Notes
                    Navigation.findNavController(requireView())
                            .navigate(R.id.nav_notetaker);
                    break;
                case 2: // Finance
                    Navigation.findNavController(requireView())
                            .navigate(R.id.nav_personalFinanace);
                    break;
                case 3: // Voice Notes
                    Snackbar.make(requireView(), "Coming soon!", Snackbar.LENGTH_SHORT).show();
                    break;
            }
        } catch (Exception e) {
            Snackbar.make(requireView(), "Navigation error occurred", Snackbar.LENGTH_SHORT).show();
        }
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
                "Learn from yesterday, live for today, hope for tomorrow. - Albert Einstein",
                "The beautiful thing about learning is that no one can take it away from you. - B.B. King",
                "Knowledge is power. Information is liberating. - Kofi Annan",
                "The only way to do great work is to love what you do. - Steve Jobs"
        };
        Random random = new Random();
        String selectedQuote = quotes[random.nextInt(quotes.length)];

        // Animate quote text change
        dailyQuoteText.setAlpha(0f);
        dailyQuoteText.setText(selectedQuote);
        dailyQuoteText.animate()
                .alpha(1f)
                .setDuration(500)
                .start();
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

        // Add subtle rotation animation to weather icon
        ObjectAnimator rotation = ObjectAnimator.ofFloat(weatherIcon, "rotation", 0f, 360f);
        rotation.setDuration(20000);
        rotation.setRepeatCount(ObjectAnimator.INFINITE);
        rotation.setInterpolator(new AccelerateDecelerateInterpolator());
        rotation.start();
    }

    private void startBackgroundUpdates() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAdded()) {
                    updateGreeting();
                    updateWeather();
                    handler.postDelayed(this, 60000); // Update every minute
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
        String description;

        FeatureItem(String title, int iconResource, String description) {
            this.title = title;
            this.iconResource = iconResource;
            this.description = description;
        }
    }

    // Feature Adapter class with enhanced animations
    private static class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.ViewHolder> {
        private final List<FeatureItem> features;
        private final OnFeatureClickListener clickListener;

        interface OnFeatureClickListener {
            void onFeatureClick(int position);
        }

        FeatureAdapter(List<FeatureItem> features, OnFeatureClickListener clickListener) {
            this.features = features;
            this.clickListener = clickListener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_feature, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            FeatureItem item = features.get(position);
            holder.titleText.setText(item.title);
            holder.iconImage.setImageResource(item.iconResource);

            // Add click animation
            holder.itemView.setOnClickListener(v -> {
                holder.itemView.animate()
                        .scaleX(0.95f)
                        .scaleY(0.95f)
                        .setDuration(100)
                        .withEndAction(() -> {
                            holder.itemView.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(100)
                                    .start();
                            clickListener.onFeatureClick(position);
                        })
                        .start();
            });

            // Add hover effect
            holder.itemView.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case android.view.MotionEvent.ACTION_DOWN:
                        holder.itemView.setAlpha(0.8f);
                        break;
                    case android.view.MotionEvent.ACTION_UP:
                    case android.view.MotionEvent.ACTION_CANCEL:
                        holder.itemView.setAlpha(1.0f);
                        break;
                }
                return false;
            });
        }

        @Override
        public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            // Add fade-in animation when items are attached
            holder.itemView.setAlpha(0f);
            holder.itemView.animate()
                    .alpha(1f)
                    .setDuration(500)
                    .setStartDelay(holder.getAdapterPosition() * 100L)
                    .start();
        }

        @Override
        public int getItemCount() {
            return features.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
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