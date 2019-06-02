package pe.edu.cibertec.camera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Button btCamera;
    ImageView ivPhoto;

    static final int REQUEST_CAMERA = 1;
    static final int REQUEST_TAKE_PICTURE = 2;

    String currentPathImage; // Ruta absoluta de la imagen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btCamera = findViewById(R.id.btCamera);
        ivPhoto = findViewById(R.id.ivPhoto);

        btCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
    }

    private void takePicture() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Validar que la cámara este disponible
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {

            // Verificar que se disponga del permiso

            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {

                requestPermission();


            } else {

                File photofile = null;

                try {
                    photofile = createImage();
                } catch (IOException e) {
                    Log.e("Error", "Mesaje: "+e.getMessage()+", Causa: "+e.getCause()+", Localización del Mensaje: "+e.getLocalizedMessage());
                }

                if(photofile != null){
                    Uri photoUri = FileProvider.getUriForFile(this, "pe.edu.cibertec.camera", photofile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(cameraIntent, REQUEST_TAKE_PICTURE);
                }else{
                    takePicture();
                }
            }
        }

    }

    private File createImage() throws IOException {

        // Asignarle un nombre

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // Asignale un directorio de almacenamiento
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Crear el archivo
        File image = File.createTempFile(
                imageFileName, // nombre
                ".jpg", // extensión
                storageDir
        );

        currentPathImage = image.getAbsolutePath();
        return image;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}
                , REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent information) {
        Glide.with(this).load(currentPathImage).into(ivPhoto);

        if (requestCode == REQUEST_TAKE_PICTURE && resultCode == RESULT_OK) {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
                takePicture();
            }
        }

    }
}
