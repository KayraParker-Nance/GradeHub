package kpn.projects.gradehub.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.Locale;

import kpn.projects.gradehub.R;
import kpn.projects.gradehub.databinding.ActivityAddAssessmentBinding;
import kpn.projects.gradehub.databinding.ActivityAddPeriodBinding;
import kpn.projects.gradehub.model.Period;
import kpn.projects.gradehub.model.PeriodRepository;

public class AddPeriodActivity extends AppCompatActivity {

    public static final String EXTRA_PERIOD_ID = "extra_period_id";

    private ActivityAddPeriodBinding binding;
    private PeriodRepository repository;
    private Period editingPeriod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAddPeriodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repository = new PeriodRepository(this);

        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.edtStartDate.setOnClickListener(v -> showDatePicker(binding.edtStartDate));
        binding.edtEndDate.setOnClickListener(v -> showDatePicker(binding.edtEndDate));
        binding.btnSavePeriod.setOnClickListener(v -> save());

        long periodId = getIntent().getLongExtra(EXTRA_PERIOD_ID, -1);
        if (periodId != -1) {
            binding.toolbar.setTitle("Edit Period");
            binding.btnSavePeriod.setText(R.string.action_update);
            repository.getPeriod(periodId, this::bindForEdit);
        }
    }

    private void bindForEdit(Period period) {
        if (period == null) {
            finish();
            return;
        }
        editingPeriod = period;
        binding.edtPeriodName.setText(period.getName());
        binding.edtStartDate.setText(period.getStartDate());
        binding.edtEndDate.setText(period.getEndDate());
    }

    private void showDatePicker(com.google.android.material.textfield.TextInputEditText target) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) ->
                target.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year)),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void save() {
        String name = textOf(binding.edtPeriodName);
        String start = textOf(binding.edtStartDate);
        String end = textOf(binding.edtEndDate);

        if (name.isEmpty()) {
            binding.edtPeriodName.setError("Required");
            return;
        }

        if (editingPeriod != null) {
            editingPeriod.setName(name);
            editingPeriod.setStartDate(start);
            editingPeriod.setEndDate(end);
            repository.update(editingPeriod, () -> {
                Toast.makeText(this, "Period updated", Toast.LENGTH_SHORT).show();
                finish();
            });
        } else {
            Period period = new Period(name, start, end);
            repository.insert(period, id -> {
                Toast.makeText(this, "Period added", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }

    private String textOf(com.google.android.material.textfield.TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }
}