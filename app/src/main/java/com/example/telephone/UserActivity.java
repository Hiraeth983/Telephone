package com.example.telephone;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.example.telephone.db.Contact;

import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;

public class UserActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // View
    TextView letterTextView, nameTextView;
    EditText nameEditText, telephoneEditText, workplaceEditText, addressEditText;

    private Uri currentContactUri;
    private boolean mContactHasChanged = false;
    public static final int LOADER = 0;
    boolean hasAllRequiredValues = false;

    // 点击即视为修改
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mContactHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Intent intent = getIntent();
        currentContactUri = intent.getData();
        Log.i("currentContactUri", currentContactUri + "");

        // 获取View
        letterTextView = findViewById(R.id.letter);
        nameTextView = findViewById(R.id.name);
        nameEditText = findViewById(R.id.nameEditText);
        telephoneEditText = findViewById(R.id.telephoneEditText);
        workplaceEditText = findViewById(R.id.workplaceEditText);
        addressEditText = findViewById(R.id.addressEditText);

        // 新建联系人
        if (currentContactUri == null) {
            setTitle("新建联系人");
            // 在新建联系人时隐藏删除按钮
            invalidateOptionsMenu();
        } else {
            setTitle("修改联系人");
            getLoaderManager().initLoader(LOADER, null, this);
        }

        // 添加触控监听
        nameEditText.setOnTouchListener(onTouchListener);
        telephoneEditText.setOnTouchListener(onTouchListener);
        workplaceEditText.setOnTouchListener(onTouchListener);
        addressEditText.setOnTouchListener(onTouchListener);
    }

    // 设置菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.modify, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // 新建联系人时隐藏删除选项
        super.onPrepareOptionsMenu(menu);
        if (currentContactUri == null) {
            MenuItem item = (MenuItem) menu.findItem(R.id.delete);
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 对应操作
        switch (item.getItemId()) {
            // 保存
            case R.id.action_save:
                saveContact();
                if (hasAllRequiredValues == true) {
                    finish();
                }
                return true;
            // 删除
            case R.id.delete:
                showDeleteConfirmationDialog();
                return true;
            // 返回主菜单
            case android.R.id.home:
                if (!mContactHasChanged) {
                    // 按下返回键提醒需不需要保存
                    NavUtils.navigateUpFromSameTask(UserActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButton = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(UserActivity.this);
                    }
                };
                showUnsavedChangesDialog(discardButton);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {Contact.ContactEntry._ID,
                Contact.ContactEntry.COLUMN_NAME,
                Contact.ContactEntry.COLUMN_TELEPHONE,
                Contact.ContactEntry.COLUMN_WORKPLACE,
                Contact.ContactEntry.COLUMN_ADDRESS,
                Contact.ContactEntry.COLUMN_TIME
        };

        return new CursorLoader(this, currentContactUri,
                projection, null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
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
            nameEditText.setText(nameValue);
            telephoneEditText.setText(telephoneValue);
            addressEditText.setText(addressValue);
            workplaceEditText.setText(workplaceValue);
            letterTextView.setText(nameValue.substring(0, 1));
            nameTextView.setText(nameValue);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText("");
        telephoneEditText.setText("");
        addressEditText.setText("");
        workplaceEditText.setText("");
    }

    // 保存联系人
    private boolean saveContact() {

        // 保存结果
        String name = nameEditText.getText().toString().trim();
        String telephone = telephoneEditText.getText().toString().trim();
        String workplace = workplaceEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        // 当用户没有输入时
        if (currentContactUri == null
                && TextUtils.isEmpty(name)
                && TextUtils.isEmpty(telephone)
                && TextUtils.isEmpty(workplace)
                && TextUtils.isEmpty(address)) {

            hasAllRequiredValues = true;
            return true;
        }

        ContentValues values = new ContentValues();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "请输入姓名！", Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;
        } else {
            values.put(Contact.ContactEntry.COLUMN_NAME, name);
        }

        if (TextUtils.isEmpty(telephone)) {
            Toast.makeText(this, "请输入电话号码!", Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;
        } else {
            values.put(Contact.ContactEntry.COLUMN_TELEPHONE, telephone);
        }

        if (TextUtils.isEmpty(workplace)) {
            Toast.makeText(this, "请输入工作单位!", Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;
        } else {
            values.put(Contact.ContactEntry.COLUMN_WORKPLACE, workplace);
        }

        if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "请输入住址!", Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;
        } else {
            values.put(Contact.ContactEntry.COLUMN_ADDRESS, address);
        }

        // 记录时间
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        values.put(Contact.ContactEntry.COLUMN_TIME, ft.format(new Date()));


        if (currentContactUri == null) {
            Uri newUri = getContentResolver().insert(Contact.ContactEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
            }
        } else {
            int result = getContentResolver().update(currentContactUri, values, null, null);
            if (result == 0) {
                Toast.makeText(this, "更新失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
            }
        }
        hasAllRequiredValues = true;
        return hasAllRequiredValues;
    }

    // 编辑界面的用户返回确认功能
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("是否放弃修改？");
        builder.setPositiveButton("放弃", discardButtonClickListener);
        builder.setNegativeButton("继续编辑", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // 编辑界面的确认删除功能
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("删除此联系人？");
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // 删除选中的联系人
    private void deleteProduct() {
        // Only perform the delete if this is an existing product.
        if (currentContactUri != null) {
            // Call the ContentResolver to delete the product at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentProductUri
            // content URI already identifies the product that we want.
            int rowsDeleted = getContentResolver().delete(currentContactUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, "删除失败",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, "删除成功",
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    // 在编辑页面按下返回键
    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mContactHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }
}