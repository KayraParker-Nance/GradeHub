package kpn.projects.gradehub.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import kpn.projects.gradehub.R;
import kpn.projects.gradehub.adapters.ModuleAdapter;
import kpn.projects.gradehub.databinding.ActivityAddAssessmentBinding;
import kpn.projects.gradehub.databinding.ActivityPeriodBinding;
import kpn.projects.gradehub.model.ModuleRepository;
import kpn.projects.gradehub.utils.SettingsManager;

public class PeriodActivity extends AppCompatActivity {
    public static final String EXTRA_PERIOD_ID = "extra_period_id";
    public static final String EXTRA_PERIOD_NAME = "extra_period_name";

    private ActivityPeriodBinding binding;
    private ModuleRepository repository;
    private ModuleAdapter adapter;
    private SettingsManager settings;
    private long periodId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityPeriodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        periodId = getIntent().getLongExtra(EXTRA_PERIOD_ID, -1);
        String periodName = getIntent().getStringExtra(EXTRA_PERIOD_NAME);

        repository = new ModuleRepository(this);
        settings = new SettingsManager(this);

        binding.toolbar.setTitle(periodName);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.toolbar.setOnMenuItemClickListener(this::onMenuItemClick);

        adapter = new ModuleAdapter(item ->
                startActivity(new Intent(this, ModuleActivity.class)
                        .putExtra(ModuleActivity.EXTRA_MODULE_ID, item.module.getId())));
        binding.recyclerModules.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerModules.setAdapter(adapter);

        binding.btnAddModule.setOnClickListener(v ->
                startActivity(new Intent(this, AddModuleActivity.class)
                        .putExtra(AddModuleActivity.EXTRA_PERIOD_ID, periodId)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadModules();
    }

    private void loadModules() {
        adapter.setDecimalPlaces(settings.getDecimalPlaces());
        repository.getModulesWithStatsForPeriod(periodId, items -> {
            adapter.submitList(items);
            binding.txtEmptyState.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            binding.recyclerModules.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);
        });
    }

    private boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_edit_period) {
            startActivity(new Intent(this, AddPeriodActivity.class)
                    .putExtra(AddPeriodActivity.EXTRA_PERIOD_ID, periodId));
            return true;
        }
        return false;
    }
}