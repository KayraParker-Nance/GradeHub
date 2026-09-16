package kpn.projects.gradehub.ui;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import kpn.projects.gradehub.R;
import kpn.projects.gradehub.databinding.ActivitySettingsBinding;
import kpn.projects.gradehub.model.PeriodRepository;
import kpn.projects.gradehub.utils.SettingsManager;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding;
    private SettingsManager settings;
    private PeriodRepository repository;

    private final ActivityResultLauncher<String> createDocumentLauncher =
            registerForActivityResult(new ActivityResultContracts.CreateDocument("application/json"), uri -> {
                if (uri != null) exportTo(uri);
            });

    private final ActivityResultLauncher<String[]> openDocumentLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri != null) importFrom(uri);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        settings = new SettingsManager(this);
        repository = new PeriodRepository(this);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.switchDarkMode.setChecked(settings.isDarkMode());
        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> settings.setDarkMode(isChecked));

        binding.edtDecimalPlaces.setText(String.valueOf(settings.getDecimalPlaces()));
        binding.edtDecimalPlaces.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(android.text.Editable s) {
                try {
                    settings.setDecimalPlaces(Integer.parseInt(s.toString()));
                } catch (NumberFormatException ignored) {}
            }
        });

        binding.btnExportData.setOnClickListener(v -> createDocumentLauncher.launch("gradehub_export.json"));
        binding.btnImportData.setOnClickListener(v -> openDocumentLauncher.launch(new String[]{"application/json"}));
        binding.btnResetData.setOnClickListener(v -> confirmReset());
    }

    private void exportTo(Uri uri) {
        repository.exportToJson(json -> {
            if (json == null) {
                Toast.makeText(this, "Export failed", Toast.LENGTH_SHORT).show();
                return;
            }
            try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                if (out != null) {
                    out.write(json.getBytes(StandardCharsets.UTF_8));
                    Toast.makeText(this, "Data exported", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void importFrom(Uri uri) {
        try (InputStream in = getContentResolver().openInputStream(uri)) {
            if (in == null) return;
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);

            new AlertDialog.Builder(this)
                    .setTitle("Import data?")
                    .setMessage("This replaces everything currently stored in GradeHub with the contents of this file.")
                    .setPositiveButton("Import", (dialog, which) ->
                            repository.importFromJson(sb.toString(), success ->
                                    Toast.makeText(this, success ? "Data imported" : "Import failed — invalid file",
                                            Toast.LENGTH_SHORT).show()))
                    .setNegativeButton(R.string.action_cancel, null)
                    .show();
        } catch (Exception e) {
            Toast.makeText(this, "Import failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmReset() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.settings_reset_confirm_title)
                .setMessage(R.string.settings_reset_confirm_message)
                .setPositiveButton(R.string.action_delete, (dialog, which) ->
                        repository.resetAllData(() ->
                                Toast.makeText(this, "All data cleared", Toast.LENGTH_SHORT).show()))
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }
}