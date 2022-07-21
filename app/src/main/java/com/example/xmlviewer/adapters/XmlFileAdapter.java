package com.example.xmlviewer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xmlviewer.R;
import com.example.xmlviewer.model.XmlFile;

import java.util.ArrayList;

public class XmlFileAdapter extends RecyclerView.Adapter<XmlFileAdapter.XmlFileViewHolder> {
    private final ArrayList<XmlFile> listFiles;

    public XmlFileAdapter(ArrayList<XmlFile> listFiles) {
        this.listFiles = listFiles;
    }

    @Override
    public int getItemCount() {
        if (listFiles == null) return 0;
        return listFiles.size();
    }

    public XmlFile getFile(int i) {
        return listFiles.get(i);
    }
    public void setFile(int i, XmlFile file) {
        listFiles.set(i, file);
        notifyItemChanged(i);
    }

    @NonNull
    @Override
    public XmlFileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_xml_file, parent, false);
        return new XmlFileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull XmlFileViewHolder holder, int position) {
        XmlFile file = listFiles.get(position);
        if (file == null) return;
        holder.bind(file);
    }

    public static class XmlFileViewHolder extends RecyclerView.ViewHolder {
        TextView tvFileName;

        public XmlFileViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFileName = itemView.findViewById(R.id.tv_file_name);
        }

        public void bind(XmlFile file) {
            tvFileName.setText(file.getName());
            if (file.getSelected())
                tvFileName.setBackgroundResource(R.drawable.my_border_selected);
            else
                tvFileName.setBackgroundResource(R.drawable.my_border);

            tvFileName.setOnClickListener(view -> {
                file.setSelected(!file.getSelected());
                if (file.getSelected())
                    tvFileName.setBackgroundResource(R.drawable.my_border_selected);
                else
                    tvFileName.setBackgroundResource(R.drawable.my_border);
            });
        }
    }
}