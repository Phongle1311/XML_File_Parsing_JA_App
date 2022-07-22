package com.example.xmlviewer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xmlviewer.R;
import com.example.xmlviewer.model.XmlFile;
import com.example.xmlviewer.interfaces.IClickItemImportedFile;

import java.util.ArrayList;

public class ImportedXmlFileAdapter extends
        RecyclerView.Adapter<ImportedXmlFileAdapter.XmlFileViewHolder> {
    private final IClickItemImportedFile iClick;
    private final ArrayList<XmlFile> listFiles;
    private View oldItem;

    public ImportedXmlFileAdapter(ArrayList<XmlFile> listFiles, IClickItemImportedFile listener) {
        this.listFiles = listFiles;
        iClick = listener;
    }

    @Override
    public int getItemCount() {
        if (listFiles == null) return 0;
        return listFiles.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public XmlFileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_imported_xml_file, parent, false);
        return new XmlFileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull XmlFileViewHolder holder, int position) {
        XmlFile file = listFiles.get(position);
        if (file == null) return;

        holder.tvInstanceID.setText(file.getInstanceId());

        holder.vFile.setOnClickListener(view1 -> {
            iClick.onClickItemImportedFile(file);

            file.setSelected(!file.getSelected());
            holder.vFile.setBackgroundResource(R.color.gray);
            if (oldItem != null && oldItem != holder.vFile)
                oldItem.setBackgroundResource(R.color.white);

            oldItem = holder.vFile;
        });
    }

    public static class XmlFileViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout vFile;
        public TextView tvInstanceID;

        public XmlFileViewHolder(@NonNull View itemView) {
            super(itemView);
            vFile = itemView.findViewById(R.id.file_view);
            tvInstanceID = itemView.findViewById(R.id.tv_instanceID);
        }
    }
}