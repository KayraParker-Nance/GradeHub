package kpn.projects.gradehub.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kpn.projects.gradehub.model.Assessment;
import kpn.projects.gradehub.utils.GradeFormatter;
import kpn.projects.gradehub.databinding.ItemAssessmentBinding;

public class AssessmentAdapter extends RecyclerView.Adapter<AssessmentAdapter.AssessmentViewHolder> {

    public interface OnAssessmentActionListener {
        void onDelete(Assessment assessment);
    }

    public interface OnAssessmentEditListener {
        void onEdit(Assessment assessment);
    }

    private final List<Assessment> items = new ArrayList<>();
    private final OnAssessmentActionListener listener;
    private final OnAssessmentEditListener editListener;
    private int decimalPlaces = 1;

    public AssessmentAdapter(OnAssessmentActionListener listener, OnAssessmentEditListener editListener) {
        this.listener = listener;
        this.editListener = editListener;
    }

    public void submitList(List<Assessment> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public void setDecimalPlaces(int decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

    @NonNull
    @Override
    public AssessmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAssessmentBinding binding = ItemAssessmentBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new AssessmentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AssessmentViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class AssessmentViewHolder extends RecyclerView.ViewHolder {
        private final ItemAssessmentBinding binding;

        AssessmentViewHolder(ItemAssessmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Assessment assessment) {
            binding.txtAssessmentName.setText(assessment.getName());
            binding.txtAssessmentStatus.setText(assessment.isCompleted() ? "Completed" : "Pending");
            binding.txtAssessmentType.setText(String.format(Locale.getDefault(),
                    "%s · %s weight%s",
                    assessment.getType() == null || assessment.getType().isEmpty() ? "Assessment" : assessment.getType(),
                    GradeFormatter.percent(assessment.getWeight(), decimalPlaces),
                    assessment.isExam() ? " · Exam" : " · Class mark"));
            binding.txtAssessmentMark.setText(String.format(Locale.getDefault(),
                    "Mark: %s · Contribution: %s",
                    assessment.isCompleted() ? GradeFormatter.percent(assessment.getMark(), decimalPlaces) : "–",
                    assessment.isCompleted() ? GradeFormatter.percent(assessment.getContribution(), decimalPlaces) : "–"));

            binding.getRoot().setOnClickListener(v -> {
                if (editListener != null) editListener.onEdit(assessment);
            });

            binding.btnDeleteAssessment.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(assessment);
            });
        }
    }
}
