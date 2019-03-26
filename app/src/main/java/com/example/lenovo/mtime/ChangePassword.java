package com.example.lenovo.mtime;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lenovo.mtime.fragment.UserFragment;

public class ChangePassword extends AppCompatActivity {
    public String realName;
    public String realPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        final EditText et_oldPassword = (EditText) findViewById(R.id.et_oldPassword);
        final EditText et_newPassword = (EditText) findViewById(R.id.et_newPassword);
        final EditText et_repeatPassword = (EditText) findViewById(R.id.et_repeatPassword);
        et_oldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        et_newPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        et_repeatPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        et_newPassword.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });
        et_repeatPassword.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });
        Button btn_reset = (Button) findViewById(R.id.btn_reset);
        Button btn_out = (Button) findViewById(R.id.btn_out);
        final String userName;
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        setEditTextInhibitInputSpace(et_newPassword);
        setEditTextInhibitInputSpace(et_oldPassword);
        setEditTextInhibitInputSpace(et_repeatPassword);
        btn_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangePassword.this, UserFragment.class);
                intent.putExtra("userName",userName);
                startActivity(intent);
            }
        });
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    public static void setEditTextInhibitInputSpace(EditText editText){
        InputFilter filter=new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if(source.equals(" "))
                    return "";
                else return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }
}
