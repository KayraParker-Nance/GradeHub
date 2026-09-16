package kpn.projects.gradehub.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kpn.projects.gradehub.R;
import kpn.projects.gradehub.model.ModuleWithStats;
import kpn.projects.gradehub.utils.GradeCalculator;
import kpn.projects.gradehub.utils.GradeFormatter;
import kpn.projects.gradehub.databinding.ItemModuleBinding;

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder> {

    public interface OnModuleClickListener {
        void onModuleClick(ModuleWithStats item);
    }

    private final List<ModuleWithStats> items = new ArrayList<>();
    private final OnModuleClickListener listener;
    private int decimalPlaces = 1;

    public ModuleAdapter(OnModuleClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<ModuleWithStats> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public void setDecimalPlaces(int decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    @NonNull
    @Override
    public ModuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemModuleBinding binding = ItemModuleBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ModuleViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ModuleViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ModuleViewHolder extends RecyclerView.ViewHolder {
        private final ItemModuleBinding binding;

        ModuleViewHolder(ItemModuleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ModuleWithStats item) {
            binding.txtModuleCode.setText(item.module.getCode());
            binding.txtModuleName.setText(item.module.getName());

            if (item.hasCompletedAssessments) {
                binding.txtModuleAverage.setText(GradeFormatter.percent(item.currentAverage, decimalPlaces));
            } else {
                binding.txtModuleAverage.setText("–");
            }
            applyBadgeColour(item.hasCompletedAssessments
                    ? GradeCalculator.classify(item.currentAverage) : null);

            binding.txtModuleSecured.setText(String.format(
                    "Secured %s · Remaining %s",
                    GradeFormatter.percent(item.securedMark, decimalPlaces),
                    GradeFormatter.percent(item.remainingWeight, decimalPlaces)));

            if (item.module.isExamEntranceRequired()) {
                binding.txtExamEntrance.setVisibility(android.view.View.VISIBLE);
                binding.txtExamEntrance.setText(item.examEntranceMet
                        ? "Exam entrance: on track"
                        : "Exam entrance: at risk");
                binding.txtExamEntrance.setTextColor(ContextCompat.getColor(
                        binding.getRoot().getContext(),
                        item.examEntranceMet ? R.color.grade_good : R.color.grade_risk));
            } else {
                binding.txtExamEntrance.setVisibility(android.view.View.GONE);
            }

            if (item.module.getModuleColour() != null) {
                binding.viewModuleColour.getBackground().mutate()
                        .setTint(android.graphics.Color.parseColor(item.module.getModuleColour().getHex()));
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onModuleClick(item);
            });
        }

        private void applyBadgeColour(GradeCalculator.GradeStatus status) {
            int bg;
            int fg;
            if (status == null) {
                bg = ContextCompat.getColor(binding.getRoot().getContext(), R.color.surface_variant_light);
                fg = ContextCompat.getColor(binding.getRoot().getContext(), R.color.on_surface_variant_light);
            } else {
                switch (status) {
                    case GOOD:
                        bg = ContextCompat.getColor(binding.getRoot().getContext(), R.color.grade_good_container);
                        fg = ContextCompat.getColor(binding.getRoot().getContext(), R.color.grade_good);
                        break;
                    case BORDERLINE:
                        bg = ContextCompat.getColor(binding.getRoot().getContext(), R.color.grade_borderline_container);
                        fg = ContextCompat.getColor(binding.getRoot().getContext(), R.color.grade_borderline);
                        break;
                    default:
                        bg = ContextCompat.getColor(binding.getRoot().getContext(), R.color.grade_risk_container);
                        fg = ContextCompat.getColor(binding.getRoot().getContext(), R.color.grade_risk);
                }
            }
            binding.txtModuleAverage.getBackground().mutate().setTint(bg);
            binding.txtModuleAverage.setTextColor(fg);
        }
    }
}
