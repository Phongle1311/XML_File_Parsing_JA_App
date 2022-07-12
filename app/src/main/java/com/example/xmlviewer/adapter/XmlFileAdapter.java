package com.example.xmlviewer.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.xmlviewer.R;
import com.example.xmlviewer.model.XmlFile;

import java.util.ArrayList;

public class XmlFileAdapter extends BaseAdapter {
    private final ArrayList<XmlFile> listFiles;

    public XmlFileAdapter(ArrayList<XmlFile> listFiles) {
        this.listFiles = listFiles;
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
        View viewProduct;
        if (view == null) {
            viewProduct = View.inflate(viewGroup.getContext(), R.layout.item_xml_file, null);
        }
        else viewProduct = view;

        XmlFile file = (XmlFile) getItem(i);
        TextView tvFileName = viewProduct.findViewById(R.id.tv_file_name);

        tvFileName.setText(file.getName());
        if (file.getSelected())
            tvFileName.setBackgroundResource(R.drawable.my_border_selected);
        else
            tvFileName.setBackgroundResource(R.drawable.my_border);

        tvFileName.setOnClickListener(view1 -> {
            file.setSelected(!file.getSelected());
            if (file.getSelected())
                tvFileName.setBackgroundResource(R.drawable.my_border_selected);
            else
                tvFileName.setBackgroundResource(R.drawable.my_border);
        });

        return viewProduct;
    }
}
