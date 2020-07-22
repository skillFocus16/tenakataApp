package com.naamini.tenakataapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.naamini.tenakataapp.R;
import com.naamini.tenakataapp.model.Student;

import java.util.List;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.StudentViewHolder> {
    private final LayoutInflater mInflater;
    private List<Student> mStudents;
    private Context context;
    String profilePath;
    private AdapterView.OnItemClickListener mOnItemClickListener;

    public StudentListAdapter(Context context,List<Student> students ) {
        mInflater = LayoutInflater.from(context);
        this.mStudents =students;
        this.context = context;
    }

    public void setOnItemClickListener(final AdapterView.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
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
            holder.studentName.setText(currentStudent.getsName());
            holder.itemViewDesc.setText("Country: "+currentStudent.getsLocation());
            if (currentStudent.getsAdmissibility().equalsIgnoreCase("true")){
                holder.status.setTextColor(context.getResources().getColor(R.color.green));
                holder.status.setText(R.string.status_admitted);
            }else {
                holder.status.setTextColor(context.getResources().getColor(R.color.design_default_color_error));
                holder.status.setText(R.string.status_not_admitted);
            }

            if(currentStudent.issUploaded()!=null) {
                if (currentStudent.issUploaded().equalsIgnoreCase("true")) {
                    holder.btnUpload.setVisibility(View.VISIBLE);
                } else {
                    holder.btnUpload.setVisibility(View.GONE);
                }
            }
           /* holder.btnUpload.setOnClickListener(view -> {
                if (((MainActivity)context).isOnline()) {
                    ((MainActivity)context).uploadStudentToFirebase();
                }else{
                    Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
                }
            });*/
               /* uploadImageToFirebase(s,storageReference, Uri.parse(data.getStringExtra(REPLY_IMG_PATH)), profilePath, key);            //send to firebase

                ((MainActivity)context).uploadImageToFirebase(currentStudent,currentStudent.getsProfileImg(),profilePath,currentStudent.getsID());
*/
               /* Intent i = new Intent(context, PDFActivity.class).setAction(currentStudent.getsName());;
//            i.putStringArrayListExtra("students", students);
//            i.putExtra("filePath", );
                context.startActivity(i);*/



            Glide.with(context)
                    .load(currentStudent.getsProfileImg())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .apply(RequestOptions.overrideOf(500, 500))
                    .into(holder.pImg);
        } else {
            // Covers the case of data not being ready yet.
            holder.studentName.setText(context.getString(R.string.noStudentAvailable));
        }
    }

    public void setStudents(List<Student> studentList){
        mStudents = studentList;
        notifyDataSetChanged();
    }

    public Student getStudentAtPosition (int position) {
        return mStudents.get(position);
    }

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
        ImageView pImg,btnUpload;
        LinearLayout lyt_parent;

        private StudentViewHolder(View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.studentName);
            itemViewDesc = itemView.findViewById(R.id.description);
            status = itemView.findViewById(R.id.status);
            pImg = itemView.findViewById(R.id.pImg);
            btnUpload = itemView.findViewById(R.id.btnDone);
            lyt_parent = itemView.findViewById(R.id.lyt_parent);
        }
    }
}
