package com.naamini.tenakataapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.naamini.tenakataapp.R;
import com.naamini.tenakataapp.activity.MainActivity;
import com.naamini.tenakataapp.activity.RegisterStudentActivity;
import com.naamini.tenakataapp.model.Student;

import java.util.List;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.StudentViewHolder> {
    private final LayoutInflater mInflater;
    private List<Student> mStudents;
    private Context context;
//    private int animation_type = 0;

    public StudentListAdapter(Context context,List<Student> students ) {
        mInflater = LayoutInflater.from(context);
        this.mStudents =students;
        this.context = context;
//        this.animation_type=animation_type;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_student_item, parent, false);
        return new StudentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, final int position) {
        if (mStudents != null) {
           final Student currentStudent = mStudents.get(position);
            holder.studentName.setText(position +" | "+currentStudent.getsName());
            holder.itemViewDesc.setText("NE:"+currentStudent.getsLocation());
            holder.status.setText("NE:"+currentStudent.getsCreatedOn());
        } else {
            // Covers the case of data not being ready yet.
            holder.studentName.setText("No Student");
        }

        holder.studentName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Intent i = new Intent(context, RegisterStudentActivity.class);
                i.putExtra("studentName", mStudents.get(position).getsName());
                context.startActivity(i);
*/            }
        });
    }

    public void setStudents(List<Student> studentList){
        mStudents = studentList;
        notifyDataSetChanged();
    }

    public Student getStudentAtPosition (int position) {
        return mStudents.get(position);
    }

    // getItemCount() is called many times, and when it is first called,
    // mStudents has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mStudents != null)
            return mStudents.size();
        else{
//            ((MainActivity)context).noContentLayout.setVisibility(View.VISIBLE);
            return 0;
        }
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder {
        public final TextView studentName,itemViewDesc, status;

        private StudentViewHolder(View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.studentName);
            itemViewDesc = itemView.findViewById(R.id.description);
            status = itemView.findViewById(R.id.status);
        }
    }
}
