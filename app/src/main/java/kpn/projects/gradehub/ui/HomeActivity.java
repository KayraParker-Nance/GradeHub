package kpn.projects.gradehub.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import kpn.projects.gradehub.R;
import kpn.projects.gradehub.adapters.PeriodAdapter;
import kpn.projects.gradehub.databinding.ActivityHomeBinding;
import kpn.projects.gradehub.model.PeriodRepository;
import kpn.projects.gradehub.utils.SettingsManager;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private PeriodRepository repository;
    private PeriodAdapter adapter;
    private SettingsManager settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repository = new PeriodRepository(this);
        settings = new SettingsManager(this);

        binding.toolbar.setOnMenuItemClickListener(this::onMenuItemClick);

        adapter = new PeriodAdapter(item ->
                startActivity(new Intent(this, PeriodActivity.class)
                        .putExtra(PeriodActivity.EXTRA_PERIOD_ID, item.period.getId())
                        .putExtra(PeriodActivity.EXTRA_PERIOD_NAME, item.period.getName())));
        binding.recyclerPeriods.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerPeriods.setAdapter(adapter);

        binding.btnAddPeriod.setOnClickListener(v ->
                startActivity(new Intent(this, AddPeriodActivity.class)));
    }

    private boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == kpn.projects.gradehub.R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPeriods();
    }

    private void loadPeriods() {
        adapter.setDecimalPlaces(settings.getDecimalPlaces());
        repository.getPeriodsWithStats(items -> {
            adapter.submitList(items);
            binding.txtEmptyState.setVisibility(items.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
            binding.recyclerPeriods.setVisibility(items.isEmpty() ? android.view.View.GONE : android.view.View.VISIBLE);
        });
    }
}