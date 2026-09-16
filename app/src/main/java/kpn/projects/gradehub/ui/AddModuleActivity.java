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
import kpn.projects.gradehub.databinding.ActivityAddAssessmentBinding;
import kpn.projects.gradehub.databinding.ActivityAddModuleBinding;
import kpn.projects.gradehub.model.Module;
import kpn.projects.gradehub.model.ModuleRepository;

public class AddModuleActivity extends AppCompatActivity {

    public static final String EXTRA_PERIOD_ID = "extra_period_id";
    public static final String EXTRA_MODULE_ID = "extra_module_id";

    private ActivityAddModuleBinding binding;
    private ModuleRepository repository;
    private long periodId;
    private Colour selectedColour = Colour.INDIGO;
    private Module editingModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAddModuleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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

        long moduleId = getIntent().getLongExtra(EXTRA_MODULE_ID, -1);
        if (moduleId != -1) {
            binding.toolbar.setTitle("Edit Module");
            binding.btnSaveModule.setText(R.string.action_update);
            repository.getModule(moduleId, this::bindForEdit);
        }
    }

    private void bindForEdit(Module module) {
        if (module == null) {
            finish();
            return;
        }
        editingModule = module;
        periodId = module.getPeriodId();
        binding.edtModuleCode.setText(module.getCode());
        binding.edtModuleName.setText(module.getName());
        binding.edtExamWeight.setText(formatNumber(module.getExamWeight()));
        binding.switchExamEntranceRequired.setChecked(module.isExamEntranceRequired());
        if (module.isExamEntranceRequired()) {
            binding.edtExamEntranceMark.setText(formatNumber(module.getExamEntranceMark()));
        }
        if (module.getModuleColour() != null) {
            selectColourChip(module.getModuleColour());
        }
    }

    private void selectColourChip(Colour colour) {
        selectedColour = colour;
        for (int i = 0; i < binding.chipGroupColour.getChildCount(); i++) {
            Chip chip = (Chip) binding.chipGroupColour.getChildAt(i);
            chip.setChecked(chip.getTag() == colour);
        }
    }

    private void buildColourChips() {
        for (Colour colour : Colour.values()) {
            Chip chip = new Chip(this);
            chip.setTag(colour);
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

        if (editingModule != null) {
            editingModule.setCode(code);
            editingModule.setName(name);
            editingModule.setExamWeight(examWeight);
            editingModule.setExamEntranceRequired(entranceRequired);
            editingModule.setExamEntranceMark(entranceMark);
            editingModule.setModuleColour(selectedColour);
            repository.update(editingModule, () -> {
                Toast.makeText(this, "Module updated", Toast.LENGTH_SHORT).show();
                finish();
            });
        } else {
            Module module = new Module(periodId, code, name, examWeight, entranceRequired, entranceMark, selectedColour);
            repository.insert(module, id -> {
                Toast.makeText(this, "Module added", Toast.LENGTH_SHORT).show();
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