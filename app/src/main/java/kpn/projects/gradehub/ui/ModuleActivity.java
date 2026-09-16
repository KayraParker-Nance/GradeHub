package kpn.projects.gradehub.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;
import java.util.Locale;

import kpn.projects.gradehub.R;
import kpn.projects.gradehub.adapters.AssessmentAdapter;
import kpn.projects.gradehub.databinding.ActivityModuleBinding;
import kpn.projects.gradehub.model.Assessment;
import kpn.projects.gradehub.model.AssessmentRepository;
import kpn.projects.gradehub.model.ModuleRepository;
import kpn.projects.gradehub.model.ModuleWithStats;
import kpn.projects.gradehub.utils.GradeCalculator;
import kpn.projects.gradehub.utils.GradeFormatter;
import kpn.projects.gradehub.utils.SettingsManager;

public class ModuleActivity extends AppCompatActivity {
    public static final String EXTRA_MODULE_ID = "extra_module_id";

    private ActivityModuleBinding binding;
    private ModuleRepository moduleRepository;
    private AssessmentRepository assessmentRepository;
    private AssessmentAdapter adapter;
    private SettingsManager settings;
    private long moduleId;

    private List<Assessment> currentAssessments;
    private kpn.projects.gradehub.model.Module currentModule;
    private int decimalPlaces = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityModuleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        moduleId = getIntent().getLongExtra(EXTRA_MODULE_ID, -1);
        moduleRepository = new ModuleRepository(this);
        assessmentRepository = new AssessmentRepository(this);
        settings = new SettingsManager(this);

        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.toolbar.setOnMenuItemClickListener(this::onMenuItemClick);

        adapter = new AssessmentAdapter(
                assessment -> confirmDeleteAssessment(assessment),
                assessment -> startActivity(new Intent(this, AddAssessmentActivity.class)
                        .putExtra(AddAssessmentActivity.EXTRA_MODULE_ID, moduleId)
                        .putExtra(AddAssessmentActivity.EXTRA_ASSESSMENT_ID, assessment.getId())));
        binding.recyclerAssessments.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerAssessments.setAdapter(adapter);

        binding.btnAddAssessment.setOnClickListener(v ->
                startActivity(new Intent(this, AddAssessmentActivity.class)
                        .putExtra(AddAssessmentActivity.EXTRA_MODULE_ID, moduleId)));

        binding.btnDeleteModule.setOnClickListener(v -> confirmDeleteModule());

        TextWatcher calculatorWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateCalculators();
            }
        };
        binding.edtTargetMark.addTextChangedListener(calculatorWatcher);
        binding.edtProjectedAverage.addTextChangedListener(calculatorWatcher);
    }

    @Override
    protected void onResume() {
        super.onResume();
        decimalPlaces = settings.getDecimalPlaces();
        adapter.setDecimalPlaces(decimalPlaces);
        loadModule();
    }

    private void loadModule() {
        moduleRepository.getModuleWithStats(moduleId, stats -> {
            if (stats == null) {
                finish();
                return;
            }
            bindStats(stats);
        });
        assessmentRepository.getAssessmentsForModule(moduleId, assessments -> {
            currentAssessments = assessments;
            adapter.submitList(assessments);
            binding.txtAssessmentsEmpty.setVisibility(assessments.isEmpty() ? View.VISIBLE : View.GONE);
            binding.recyclerAssessments.setVisibility(assessments.isEmpty() ? View.GONE : View.VISIBLE);
            updateCalculators();
        });
    }

    private void bindStats(ModuleWithStats stats) {
        currentModule = stats.module;
        binding.toolbar.setTitle(stats.module.getCode());
        binding.txtModuleName.setText(stats.module.getName());
        binding.txtCurrentAverage.setText(GradeFormatter.percent(stats.currentAverage, decimalPlaces));
        binding.txtSecuredMark.setText(GradeFormatter.percent(stats.securedMark, decimalPlaces));
        binding.txtCompletedWeight.setText(GradeFormatter.percent(stats.completedWeight, decimalPlaces));
        binding.txtRemainingWeight.setText(GradeFormatter.percent(stats.remainingWeight, decimalPlaces));

        if (stats.module.isExamEntranceRequired()) {
            binding.cardExamEntrance.setVisibility(View.VISIBLE);
            binding.txtExamEntrance.setText(stats.examEntranceMet ? "On track" : "At risk");
            int bg = stats.examEntranceMet ? R.color.grade_good_container : R.color.grade_risk_container;
            int fg = stats.examEntranceMet ? R.color.grade_good : R.color.grade_risk;
            binding.txtExamEntrance.getBackground().mutate()
                    .setTint(ContextCompat.getColor(this, bg));
            binding.txtExamEntrance.setTextColor(ContextCompat.getColor(this, fg));
        } else {
            binding.cardExamEntrance.setVisibility(View.GONE);
        }
        updateCalculators();
    }

    private void updateCalculators() {
        if (currentAssessments == null) return;

        if (currentModule != null && currentModule.isExamEntranceRequired()) {
            double required = GradeCalculator.calculateRequiredAverageForExamEntrance(
                    currentAssessments, currentModule.getExamEntranceMark());
            binding.txtRequiredForEntrance.setText(required < 0
                    ? "No remaining class work — outcome is already decided."
                    : String.format(Locale.getDefault(),
                    "Required average for remaining class work: %s", GradeFormatter.percent(required, decimalPlaces)));
        }

        String targetStr = safeText(binding.edtTargetMark);
        if (targetStr.isEmpty()) {
            binding.txtRequiredForTarget.setText("Required average: –");
        } else {
            try {
                double target = Double.parseDouble(targetStr);
                double required = GradeCalculator.calculateRequiredAverageForTarget(currentAssessments, target);
                if (required < 0) {
                    binding.txtRequiredForTarget.setText("No remaining work — nothing more needed.");
                } else {
                    binding.txtRequiredForTarget.setText(String.format(Locale.getDefault(),
                            "Required average on remaining work: %s", GradeFormatter.percent(required, decimalPlaces)));
                }
            } catch (NumberFormatException e) {
                binding.txtRequiredForTarget.setText("Required average: –");
            }
        }

        String projectionStr = safeText(binding.edtProjectedAverage);
        if (projectionStr.isEmpty()) {
            binding.txtProjectedFinalMark.setText("Projected final mark: –");
        } else {
            try {
                double projection = Double.parseDouble(projectionStr);
                double projected = GradeCalculator.calculateProjectedFinalMark(currentAssessments, projection);
                binding.txtProjectedFinalMark.setText(String.format(Locale.getDefault(),
                        "Projected final mark: %s", GradeFormatter.percent(projected, decimalPlaces)));
            } catch (NumberFormatException e) {
                binding.txtProjectedFinalMark.setText("Projected final mark: –");
            }
        }
    }

    private String safeText(com.google.android.material.textfield.TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    private void confirmDeleteAssessment(Assessment assessment) {
        new AlertDialog.Builder(this)
                .setTitle("Delete assessment?")
                .setMessage("Delete \"" + assessment.getName() + "\"? This can't be undone.")
                .setPositiveButton(R.string.action_delete, (dialog, which) ->
                        assessmentRepository.delete(assessment, this::loadModule))
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    private void confirmDeleteModule() {
        if (currentModule == null) return;
        new AlertDialog.Builder(this)
                .setTitle("Delete module?")
                .setMessage("Delete this module and all its assessments? This can't be undone.")
                .setPositiveButton(R.string.action_delete, (dialog, which) ->
                        moduleRepository.delete(currentModule, this::finish))
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    private boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_edit_module) {
            startActivity(new Intent(this, AddModuleActivity.class)
                    .putExtra(AddModuleActivity.EXTRA_MODULE_ID, moduleId));
            return true;
        }
        return false;
    }
}