package kpn.projects.gradehub.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.chip.Chip;

import kpn.projects.gradehub.Colour;
import kpn.projects.gradehub.R;
import kpn.projects.gradehub.databinding.ActivityAddModuleBinding;
import kpn.projects.gradehub.model.Module;
import kpn.projects.gradehub.model.ModuleRepository;

public class AddModuleActivity extends AppCompatActivity {

    public static final String EXTRA_PERIOD_ID = "extra_period_id";

    private ActivityAddModuleBinding binding;
    private ModuleRepository repository;
    private long periodId;
    private Colour selectedColour = Colour.INDIGO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_module);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        periodId = getIntent().getLongExtra(EXTRA_PERIOD_ID, -1);
        repository = new ModuleRepository(this);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.switchExamEntranceRequired.setOnCheckedChangeListener((buttonView, isChecked) ->
                binding.layoutExamEntranceMark.setVisibility(isChecked ? android.view.View.VISIBLE : android.view.View.GONE));

        buildColourChips();
        binding.btnSaveModule.setOnClickListener(v -> save());
    }

    private void buildColourChips() {
        for (Colour colour : Colour.values()) {
            Chip chip = new Chip(this);
            chip.setCheckable(true);
            chip.setText(colour.name().substring(0, 1) + colour.name().substring(1).toLowerCase());
            int colourInt = Color.parseColor(colour.getHex());
            chip.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(colourInt));
            chip.setTextColor(Color.WHITE);
            chip.setChecked(colour == selectedColour);
            chip.setOnClickListener(v -> {
                selectedColour = colour;
                for (int i = 0; i < binding.chipGroupColour.getChildCount(); i++) {
                    ((Chip) binding.chipGroupColour.getChildAt(i)).setChecked(false);
                }
                chip.setChecked(true);
            });
            binding.chipGroupColour.addView(chip);
        }
    }

    private void save() {
        String code = textOf(binding.edtModuleCode);
        String name = textOf(binding.edtModuleName);
        String examWeightStr = textOf(binding.edtExamWeight);

        if (code.isEmpty()) {
            binding.edtModuleCode.setError("Required");
            return;
        }
        if (name.isEmpty()) {
            binding.edtModuleName.setError("Required");
            return;
        }

        double examWeight = parseOrZero(examWeightStr);
        boolean entranceRequired = binding.switchExamEntranceRequired.isChecked();
        double entranceMark = entranceRequired ? parseOrZero(textOf(binding.edtExamEntranceMark)) : 0;

        Module module = new Module(periodId, code, name, examWeight, entranceRequired, entranceMark, selectedColour);
        repository.insert(module, id -> {
            Toast.makeText(this, "Module added", Toast.LENGTH_SHORT).show();
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