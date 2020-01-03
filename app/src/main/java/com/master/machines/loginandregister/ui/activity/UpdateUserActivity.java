package com.master.machines.loginandregister.ui.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.master.machines.loginandregister.R;
import com.master.machines.loginandregister.db.DAO;
import com.master.machines.loginandregister.db.DataBaseUser;
import com.master.machines.loginandregister.model.User;
import com.master.machines.loginandregister.utils.Util;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UpdateUserActivity extends AppCompatActivity implements OnClickListener {

    public static String TAG = UpdateUserActivity.class.getSimpleName();

    @BindView(R.id.photo)
    ImageView mPhoto;
    @BindView(R.id.fab_photo)
    FloatingActionButton mFabPhoto;
    @BindView(R.id.input_name)
    TextInputEditText mInputName;
    @BindView(R.id.input_last_name)
    TextInputEditText mInputLastName;
    @BindView(R.id.input_address)
    TextInputEditText mInputAddress;
    @BindView(R.id.btn_save)
    Button mBtnSave;

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 100;
    private static final int REQUEST_CAMERA = 101;
    private final int REQUEST_CODE_GALLERY = 200;
    private final int REQUEST_CODE_CAMERA = 201;
    private String nameImage, mPath;
    private String mName, mLastname, mAddress;

    private Uri tempUri;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);
        ButterKnife.bind(this);

        init();
        initEvents();

        obtainName();

        fillData();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    Log.e(TAG, "Permiso denegado");
                }
                return;
            }
            case REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Log.e(TAG, "Permiso denegado");
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CAMERA:
                    if (tempUri != null) {
                        mPhoto.setImageURI(tempUri);
                        mPath = tempUri.getPath();
                    }
                    break;
                case REQUEST_CODE_GALLERY:
                    try {
                        tempUri = data.getData();
                    } catch (Error | Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    if (tempUri != null) {
                        mPhoto.setImageURI(tempUri);
                        mPath = Util.getRealPathFromURI(this,
                                tempUri);
                    }
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photo:
            case R.id.fab_photo:
                showDialogAddPhoto().show();
                break;
            case R.id.btn_save:
                saveDataInVariables();
                if (validateEntries(mName, mLastname, mAddress)) {
                    updateUserInDB();
                }
                break;
        }
    }

    private void init() {
        Bundle datos = this.getIntent().getExtras();
        mUser = (User) datos.getSerializable("User");
    }

    private void initEvents() {
        mPhoto.setOnClickListener(this);
        mFabPhoto.setOnClickListener(this);
        mBtnSave.setOnClickListener(this);
    }

    private void fillData() {
        if (mUser != null) {
            if (mUser.getPhoto() != null) {
                mPath = mUser.getPhoto();
                File file = new File(mPath);
                Glide.with(this)
                        .load(file)
                        .placeholder(getResources().getDrawable(R.mipmap.ic_launcher))
                        .error(getResources().getDrawable(R.mipmap.ic_launcher))
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(mPhoto);
            }

            if (!TextUtils.isEmpty(mUser.getName())) {
                mInputName.setText(mUser.getName());
            } else {
                mInputName.setText("");
            }

            if (!TextUtils.isEmpty(mUser.getLastName())) {
                mInputLastName.setText(mUser.getLastName());
            } else {
                mInputLastName.setText("");
            }

            if (!TextUtils.isEmpty(mUser.getAddress())) {
                mInputAddress.setText(mUser.getAddress());
            } else {
                mInputAddress.setText("");
            }
        }
    }

    private AlertDialog showDialogAddPhoto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Información")
                .setMessage("De donde desea obtener su imagen")
                .setPositiveButton("Galeria",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                permissionGallery();
                            }
                        })
                .setNegativeButton("Camara",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                permissionCamera();

                            }
                        });
        return builder.create();
    }

    private User user() {
        mUser.setPhoto(mPath);
        mUser.setName(mName);
        mUser.setLastName(mLastname);
        mUser.setAddress(mAddress);
        return mUser;
    }

    private void permissionCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CAMERA);
            } else {
                openCamera();
            }
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        String APP_DIRECTORY = getResources().getString(R.string.app_name) + "/";
        String MEDIA_DIRECTORY = APP_DIRECTORY + "media/";
        String TEMPORAL_PICTURE_NAME = nameImage + ".jpg";
        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
        file.mkdirs();
        String path = Environment.getExternalStorageDirectory() + file.separator + MEDIA_DIRECTORY + file.separator + TEMPORAL_PICTURE_NAME;
        file = new File(path);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tempUri = FileProvider.getUriForFile(this,
                    "com.master.machines.loginandregister.fileProvider", file);
        } else {
            tempUri = Uri.fromFile(file);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    private void permissionGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                openGallery();
            }
        } else {
            openGallery();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(Intent.createChooser(intent, "Selecione su IMAGEN"), REQUEST_CODE_GALLERY);
    }

    private boolean validatePhoto() {
        if (mPath != null)
            return true;
        else
            return false;
    }

    private boolean validateEntries(String name, String lastName, String address) {
        if (validatePhoto()) {
            if (!TextUtils.isEmpty(name)
                    && !TextUtils.isEmpty(lastName)
                    && !TextUtils.isEmpty(address)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void saveDataInVariables() {
        mName = mInputName.getText().toString().trim();
        mLastname = mInputLastName.getText().toString().trim();
        mAddress = mInputAddress.getText().toString().trim();
    }

    private void updateUserInDB() {
        DataBaseUser DBU = new DataBaseUser(this, "user", null, 1);
        boolean updateUser = DAO.updateUser(DBU, user());
        if (updateUser) {
            Toast.makeText(UpdateUserActivity.this, "Actualizacion exitosa", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ListUserActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(UpdateUserActivity.this, "Ocurrio un problema al actualizar el usuario", Toast.LENGTH_SHORT).show();
        }
    }

    private void obtainName() {
        DateFormat hourDateFormat = new SimpleDateFormat("dd:MM:yyyy-HH:mm:ss");
        Date date = new Date();
        nameImage = hourDateFormat.format(date);
    }
}
