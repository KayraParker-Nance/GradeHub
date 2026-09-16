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
import kpn.projects.gradehub.model.Assessment;
import kpn.projects.gradehub.model.AssessmentRepository;

public class AddAssessmentActivity extends AppCompatActivity {
    public static final String EXTRA_MODULE_ID = "extra_module_id";

    private ActivityAddAssessmentBinding binding;
    private AssessmentRepository repository;
    private long moduleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_assessment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        moduleId = getIntent().getLongExtra(EXTRA_MODULE_ID, -1);
        repository = new AssessmentRepository(this);

        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.edtAssessmentDate.setOnClickListener(v -> showDatePicker());
        binding.toggleMarkType.check(R.id.btnClassMark);
        binding.btnSaveAssessment.setOnClickListener(v -> save());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) ->
                binding.edtAssessmentDate.setText(
                        String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year)),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void save() {
        String name = textOf(binding.edtAssessmentName);
        String type = textOf(binding.edtAssessmentType);
        String weightStr = textOf(binding.edtAssessmentWeight);
        String markStr = textOf(binding.edtAssessmentMark);
        String date = textOf(binding.edtAssessmentDate);

        if (name.isEmpty()) {
            binding.edtAssessmentName.setError("Required");
            return;
        }
        if (weightStr.isEmpty()) {
            binding.edtAssessmentWeight.setError("Required");
            return;
        }

        double weight = parseOrZero(weightStr);
        double mark = parseOrZero(markStr);
        boolean completed = binding.switchCompleted.isChecked();
        boolean countsForClassMark = binding.toggleMarkType.getCheckedButtonId() == R.id.btnClassMark;

        Assessment assessment = new Assessment(moduleId, name, type, weight, mark, date, completed, countsForClassMark);
        repository.insert(assessment, () -> {
            Toast.makeText(this, "Assessment added", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private double parseOrZero(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String textOf(com.google.android.material.textfield.TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }
}