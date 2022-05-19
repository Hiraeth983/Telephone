package com.example.telephone.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.telephone.R;
import com.example.telephone.db.Contact;

public class MyCursorAdapter extends CursorAdapter {

    public MyCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.view_contact, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameView, telephoneView, addressView, workplaceView, letterView;

        nameView = view.findViewById(R.id.name);
        telephoneView = view.findViewById(R.id.telephone);
        addressView = view.findViewById(R.id.address);
        workplaceView = view.findViewById(R.id.workplace);
        letterView = view.findViewById(R.id.letter);

        // 获取对印的索引
        int name = cursor.getColumnIndex(Contact.ContactEntry.COLUMN_NAME);
        int telephone = cursor.getColumnIndex(Contact.ContactEntry.COLUMN_TELEPHONE);
        int address = cursor.getColumnIndex(Contact.ContactEntry.COLUMN_ADDRESS);
        int workplace = cursor.getColumnIndex(Contact.ContactEntry.COLUMN_WORKPLACE);

        // 获取对应值
        String nameValue = cursor.getString(name);
        String telephoneValue = cursor.getString(telephone);
        String addressValue = cursor.getString(address);
        String workplaceValue = cursor.getString(workplace);

        // 设置View的值
        nameView.setText(nameValue);
        telephoneView.setText(telephoneValue);
        addressView.setText(addressValue);
        workplaceView.setText(workplaceValue);
        letterView.setText(nameValue.substring(0, 1));
    }
}
