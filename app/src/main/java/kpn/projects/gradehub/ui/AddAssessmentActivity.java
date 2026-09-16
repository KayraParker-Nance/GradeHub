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
    public static final String EXTRA_ASSESSMENT_ID = "extra_assessment_id";

    private ActivityAddAssessmentBinding binding;
    private AssessmentRepository repository;
    private long moduleId;
    private Assessment editingAssessment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAddAssessmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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

        long assessmentId = getIntent().getLongExtra(EXTRA_ASSESSMENT_ID, -1);
        if (assessmentId != -1) {
            binding.toolbar.setTitle("Edit Assessment");
            binding.btnSaveAssessment.setText(R.string.action_update);
            repository.getAssessment(assessmentId, this::bindForEdit);
        }
    }

    private void bindForEdit(Assessment assessment) {
        if (assessment == null) {
            finish();
            return;
        }
        editingAssessment = assessment;
        moduleId = assessment.getModuleId();
        binding.edtAssessmentName.setText(assessment.getName());
        binding.edtAssessmentType.setText(assessment.getType());
        binding.edtAssessmentWeight.setText(formatNumber(assessment.getWeight()));
        binding.edtAssessmentMark.setText(formatNumber(assessment.getMark()));
        binding.edtAssessmentDate.setText(assessment.getDate());
        binding.switchCompleted.setChecked(assessment.isCompleted());
        binding.toggleMarkType.check(assessment.countsForClassMark() ? R.id.btnClassMark : R.id.btnExam);
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

        if (editingAssessment != null) {
            editingAssessment.setName(name);
            editingAssessment.setType(type);
            editingAssessment.setWeight(weight);
            editingAssessment.setMark(mark);
            editingAssessment.setDate(date);
            editingAssessment.setCompleted(completed);
            editingAssessment.setCountsForClassMark(countsForClassMark);
            repository.update(editingAssessment, () -> {
                Toast.makeText(this, "Assessment updated", Toast.LENGTH_SHORT).show();
                finish();
            });
        } else {
            Assessment assessment = new Assessment(moduleId, name, type, weight, mark, date, completed, countsForClassMark);
            repository.insert(assessment, () -> {
                Toast.makeText(this, "Assessment added", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }

    private String formatNumber(double value) {
        if (value == Math.floor(value) && !Double.isInfinite(value)) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
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