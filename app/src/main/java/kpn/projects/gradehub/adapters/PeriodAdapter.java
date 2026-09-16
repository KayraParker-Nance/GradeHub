package kpn.projects.gradehub.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kpn.projects.gradehub.utils.GradeCalculator;
import kpn.projects.gradehub.R;
import kpn.projects.gradehub.model.PeriodWithStats;
import kpn.projects.gradehub.utils.GradeFormatter;
import kpn.projects.gradehub.databinding.ItemPeriodBinding;

public class PeriodAdapter extends RecyclerView.Adapter<PeriodAdapter.PeriodViewHolder> {

    public interface OnPeriodClickListener {
        void onPeriodClick(PeriodWithStats item);
    }

    private final List<PeriodWithStats> items = new ArrayList<>();
    private final OnPeriodClickListener listener;
    private int decimalPlaces = 1;

    public PeriodAdapter(OnPeriodClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<PeriodWithStats> newItems) {
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
    public PeriodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPeriodBinding binding = ItemPeriodBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new PeriodViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PeriodViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class PeriodViewHolder extends RecyclerView.ViewHolder {
        private final ItemPeriodBinding binding;

        PeriodViewHolder(ItemPeriodBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(PeriodWithStats item) {
            binding.txtPeriodName.setText(item.period.getName());
            binding.txtPeriodDates.setText(
                    item.period.getStartDate() + " – " + item.period.getEndDate());
            binding.txtModuleCount.setText(
                    item.moduleCount + (item.moduleCount == 1 ? " module" : " modules"));

            if (item.moduleCount == 0) {
                binding.txtPeriodAverage.setText("–");
            } else {
                binding.txtPeriodAverage.setText(GradeFormatter.percent(item.average, decimalPlaces));
            }
            applyStatusColour(item.moduleCount == 0 ? null : GradeCalculator.classify(item.average));

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onPeriodClick(item);
            });
        }

        private void applyStatusColour(GradeCalculator.GradeStatus status) {
            int bg;
            int fg;
            if (status == null) {
                bg = resolveColor(R.color.surface_variant_light);
                fg = resolveColor(R.color.on_surface_variant_light);
            } else {
                switch (status) {
                    case GOOD:
                        bg = resolveColor(R.color.grade_good_container);
                        fg = resolveColor(R.color.grade_good);
                        break;
                    case BORDERLINE:
                        bg = resolveColor(R.color.grade_borderline_container);
                        fg = resolveColor(R.color.grade_borderline);
                        break;
                    default:
                        bg = resolveColor(R.color.grade_risk_container);
                        fg = resolveColor(R.color.grade_risk);
                }
            }
            binding.txtPeriodAverage.getBackground().mutate()
                    .setTint(bg);
            binding.txtPeriodAverage.setTextColor(fg);
        }

        private int resolveColor(int colorRes) {
            return androidx.core.content.ContextCompat.getColor(binding.getRoot().getContext(), colorRes);
        }
    }
}