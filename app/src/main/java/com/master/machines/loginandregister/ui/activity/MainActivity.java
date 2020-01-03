package com.master.machines.loginandregister.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.master.machines.loginandregister.R;
import com.master.machines.loginandregister.utils.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    @BindView(R.id.input_email)
    EditText mInputEmail;
    @BindView(R.id.input_password)
    EditText mInputPassword;
    @BindView(R.id.btn_login)
    Button mBtnLogin;

    private String user, password;
    private String user1 = "usuario@usuario.com", password1 = "123456789";
    private String user2 = "admin@usuario.com", password2 = "admin123";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initEvents();
    }

    private void initEvents() {
        mBtnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                saveDataInVariables();
                if (!TextUtils.isEmpty(user) && !TextUtils.isEmpty(password)) {
                    if (Util.validateEmail(user)) {
                        if (user.equalsIgnoreCase(user1) && password.equalsIgnoreCase(password1)
                                || user.equalsIgnoreCase(user2) && password.equalsIgnoreCase(password2)) {
                            Intent intent = new Intent(this, ListUserActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Correo incorrecto siga el siguiente formato example@mail.com", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Los campos no deben estar vacios", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void saveDataInVariables() {
        user = mInputEmail.getText().toString().trim();
        password = mInputPassword.getText().toString();
    }
}
