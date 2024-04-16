package com.dhay.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CoursesRVAdapter extends RecyclerView.Adapter<CoursesRVAdapter.ViewHolder> {

    // variable for our array list and context
    private ArrayList<CoursesModel> coursesModelArrayList;
    private Context context;

    public void setCoursesModelArrayList(ArrayList<CoursesModel> coursesModelArrayList) {
        this.coursesModelArrayList = coursesModelArrayList;
    }

    private OnItemLongClickListener onItemLongClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    public CoursesRVAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.courses_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CoursesModel modal = coursesModelArrayList.get(position);
        holder.idTV.setText(modal.getId() + "");
        holder.courseNameTV.setText(modal.getName());
        holder.courseDescriptionTV.setText(modal.getDescription());
        holder.dateStarTv.setText(modal.getDateStart());
        holder.dateEndTV.setText(modal.getDateEnd());
    }

    @Override
    public int getItemCount() {
        return coursesModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView courseNameTV, courseDescriptionTV, dateStarTv, dateEndTV, idTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            idTV = itemView.findViewById(R.id.id_course_item);
            courseNameTV = itemView.findViewById(R.id.name_course_item);
            courseDescriptionTV = itemView.findViewById(R.id.description_course);
            dateStarTv = itemView.findViewById(R.id.date_course_start);
            dateEndTV = itemView.findViewById(R.id.date_course_end);

            itemView.setOnLongClickListener(v -> {
                if (onItemLongClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        int id = coursesModelArrayList.get(position).getId();
                        onItemLongClickListener.onItemLongClick(position,id);
                        Log.d(TAG, "ViewHolder: id number" + id);
                        return true;
                    }
                }
                return false;
            });
        }

        private static final String TAG = "ViewHolder";
    }
    public interface OnItemLongClickListener {
        void onItemLongClick(int position , int id);
    }
}

