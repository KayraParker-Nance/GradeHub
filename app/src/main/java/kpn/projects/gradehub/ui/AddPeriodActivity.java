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
import kpn.projects.gradehub.databinding.ActivityAddPeriodBinding;
import kpn.projects.gradehub.model.Period;
import kpn.projects.gradehub.model.PeriodRepository;

public class AddPeriodActivity extends AppCompatActivity {

    private ActivityAddPeriodBinding binding;
    private PeriodRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_period);
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

        Period period = new Period(name, start, end);
        repository.insert(period, id -> {
            Toast.makeText(this, "Period added", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private String textOf(com.google.android.material.textfield.TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }


}