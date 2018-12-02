package com.merteroglu.ots.Adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.merteroglu.ots.Model.Student;
import com.merteroglu.ots.R;

import java.util.ArrayList;
import java.util.List;

public class StudentListViewAdapter extends ArrayAdapter {
    private final Context context;
    private List students = new ArrayList();

    public StudentListViewAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.context = context;
    }

    @Override
    public void add(@Nullable Object object) {
        super.add(object);
        students.add(object);
    }

    @Override
    public int getCount() {
        return students.size();
    }

    public void clearAll() {
        students.clear();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return students.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        StudentHolder studentHolder;

        if (row == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.studentlistview_item, parent, false);
            studentHolder = new StudentHolder();

            studentHolder.imgPp = row.findViewById(R.id.imgPp);
            studentHolder.imgDurum = row.findViewById(R.id.imgDurum);
            studentHolder.txtParentName = row.findViewById(R.id.parentName);
            studentHolder.txtParentPhone = row.findViewById(R.id.parentPhone);
            studentHolder.txtStudentName = row.findViewById(R.id.studentName);
            studentHolder.txtStudentPhone = row.findViewById(R.id.studentPhone);
            studentHolder.btnParent = row.findViewById(R.id.btnParent);
            studentHolder.btnStudent = row.findViewById(R.id.btnStudent);

            row.setTag(studentHolder);
        } else {
            studentHolder = (StudentHolder) row.getTag();
        }

        final Student student = (Student) this.getItem(position);

        if (student.isInVehicle())
            studentHolder.imgDurum.setBackground(ContextCompat.getDrawable(this.context, R.drawable.circle_green));
        else
            studentHolder.imgDurum.setBackground(ContextCompat.getDrawable(this.context, R.drawable.circle_red));

        studentHolder.txtStudentName.setText(student.getName());
        studentHolder.txtStudentPhone.setText(student.getPhone());
        studentHolder.txtParentName.setText(student.getParentName());
        studentHolder.txtParentPhone.setText(student.getParentPhone());

        studentHolder.btnStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + student.getPhone()));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    context.startActivity(intent);
                }else{
                    Toast.makeText(context, "Arama yapma izinlerini vermelisiniz", Toast.LENGTH_SHORT).show();
                }
            }
        });

        studentHolder.btnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + student.getParentPhone()));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    context.startActivity(intent);
                }else{
                    Toast.makeText(context, "Arama yapma izinlerini vermelisiniz", Toast.LENGTH_SHORT).show();
                }
            }
        });



        return row;
    }

    static class StudentHolder{
        ImageView imgPp,imgDurum;
        TextView txtStudentName,txtStudentPhone,txtParentName,txtParentPhone;
        Button btnStudent,btnParent;
    }

}
