package com.example.xmlviewer.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.xmlviewer.R;
import com.example.xmlviewer.model.XmlFile;
import com.example.xmlviewer.my_interface.IClickItemImportedFile;

import java.util.ArrayList;

public class ImportedXmlFileAdapter extends BaseAdapter {
    private final IClickItemImportedFile iClick;
    private final ArrayList<XmlFile> listFiles;
    private View oldItem;

    public ImportedXmlFileAdapter(ArrayList<XmlFile> listFiles, IClickItemImportedFile listener) {
        this.listFiles = listFiles;
        iClick = listener;
        oldItem = null;
    }

    @Override
    public int getCount() {
        if (listFiles != null)
            return listFiles.size();
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return listFiles.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View vFile;
        if (view == null) {
            vFile = View.inflate(viewGroup.getContext(), R.layout.item_imported_xml_file, null);
        }
        else vFile = view;

        XmlFile file = (XmlFile) getItem(i);
        TextView tvInstanceID = vFile.findViewById(R.id.tv_instanceID);

        tvInstanceID.setText(file.getId());

        vFile.setOnClickListener(view1 -> {
            iClick.onClickItemImportedFile(file);

            file.setSelected(!file.getSelected());
            vFile.setBackgroundResource(R.color.gray);
            if (oldItem != null && oldItem != vFile)
                oldItem.setBackgroundResource(R.color.white);

            oldItem = vFile;
        });

        return vFile;
    }
}
