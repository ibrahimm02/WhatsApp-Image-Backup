package com.ibrahim.whatsappbackup;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class SelectAndUploadActivity extends AppCompatActivity {

 //   List<String> ImageList;
    private ArrayList<String> ImageList = new ArrayList<String>();

    private CheckBox checkBox;
    private GridView gView1;
    private View v;

    private Uri imageUri; //imageUri
    private String downloadImageUrl, saveCurrentDate, saveCurrentTime, imageRandomKey;
    private String Fname;
    private ProgressDialog progressDialog;

    private GridviewImageAdapter mImageAdapter;

    //Firebase
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private StorageTask uploadTask;

    private Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectandupload);

        gView1 = (GridView) findViewById(R.id.gridView1);
        checkBox = (CheckBox)findViewById(R.id.checkBox1);
        progressDialog = new ProgressDialog(this);

        ImageList = getSD();

        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();
        String current_user_Id = mAuth.getCurrentUser().getUid();

        storageReference = FirebaseStorage.getInstance().getReference().child("Users").child(current_user_Id);
//        databaseReference = FirebaseDatabase.getInstance().getReference("images");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_Id).child("user_images");

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Select an Image");

        gView1.setAdapter(new GridviewImageAdapter(this,ImageList));

        // Get Item Checked
        Button btnGetItem = (Button) findViewById(R.id.btnGetItem);
        btnGetItem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                uploadCheckedBoxes(gView1);
            }
        });
    }

    private ArrayList<String> getSD() {

        ArrayList <String> it = new ArrayList <String>();
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "WhatsApp/Media/WhatsApp Images");
        File[] files = f.listFiles ();

        for (int i = 0; i <files.length; i++)
        {
            File  file = files[i];
//            Log.d("Count",file.getPath());
            it.add (file.getPath());
        }
        return it;

    }

    private void uploadCheckedBoxes(GridView gView1) {
        for (int i = 0; i < gView1.getChildCount() ; i++ ){
            View v = gView1.getChildAt(i);
            checkBox = (CheckBox) v.findViewById(R.id.checkBox1);
            if  (checkBox.isChecked()){
//                Log.d("Item " + String.valueOf(i), checkBox.getTag().toString());

//                Log.d("PATH " + String.valueOf(i), Environment.getExternalStorageDirectory() + File.separator + "WhatsApp/Media/TempImagesFolder/" + checkBox.getTag().toString());

//                Toast.makeText(SelectAndUploadActivity.this, checkBox.getTag().toString(), Toast.LENGTH_LONG).show();

                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(SelectAndUploadActivity.this, "Upload in Progress", Toast.LENGTH_SHORT).show();
                }
                uploadImage(Environment.getExternalStorageDirectory() + File.separator + "WhatsApp/Media/WhatsApp Images/" + checkBox.getTag().toString());
            }
//            if (!checkBox.isChecked()){
//                Toast.makeText(this, "No Image selected", Toast.LENGTH_SHORT).show();
//            }
        }
    }

//    private List <String> getSD()
//    {
//        List <String> it = new ArrayList <String>();
//        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "WhatsApp/Media/TempImagesFolder");
//        File[] files = f.listFiles ();
//
//        for (int i = 0; i <files.length; i++)
//        {
//            File  file = files[i];
//            Log.d("Count",file.getPath());
//            it.add (file.getPath());
//        }
//        return it;
//    }

    private void uploadImage(String fname) {
        imageUri = Uri.fromFile(new File(fname));
        if (imageUri != null) {
            //imageUri = fname;
            Log.d("Upload Image: ", imageUri.toString());
         //   final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            //getting the storage reference
            // final StorageReference ref = storageReference.child("images/"+ System.currentTimeMillis() + "." + getFileExtension(imageUri));

            Calendar calendar = Calendar.getInstance();

            @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy ");
            saveCurrentDate = currentDate.format(calendar.getTime());

            @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
            saveCurrentTime = currentTime.format(calendar.getTime());

            imageRandomKey = saveCurrentDate + saveCurrentTime;

//           final  StorageReference ref = storageReference.child(imageUri.getLastPathSegment() + imageRandomKey + "jpg");
//            imageRandomKey = UUID.randomUUID().toString();

            //getting storage reference
            final StorageReference ref = storageReference.child("images/" + imageRandomKey);

            //adding the file to reference
            uploadTask = ref.putFile(imageUri)

                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            progressDialog.dismiss();

                            Toast.makeText(SelectAndUploadActivity.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();

                            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }

                                    downloadImageUrl = ref.getDownloadUrl().toString();
                                    return ref.getDownloadUrl();
                                }


                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {

                                        downloadImageUrl = task.getResult().toString();

                                        HashMap<String, Object> ImageMap = new HashMap<>();
                                        ImageMap.put("imageUrl", downloadImageUrl);

                                        databaseReference.child(imageRandomKey).updateChildren(ImageMap);

                                        Intent mainIntent = new Intent(SelectAndUploadActivity.this, MainActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();
//                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                if (task.isSuccessful()) {
//
//                                                    Toast.makeText(MainActivity.this, "Image is added to database", Toast.LENGTH_SHORT).show();
//                                                } else {
//                                                    String message = task.getException().toString();
//                                                    Toast.makeText(MainActivity.this, "Error " + message, Toast.LENGTH_SHORT).show();
//                                                }
//                                            }
//                                        });
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            String message = e.toString();
                            Toast.makeText(SelectAndUploadActivity.this, "Upload Failed " + message, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
//                            Upload upload = new Upload(editTextNull.getText().toString().trim(),
//                                    taskSnapshot.getStorage().getDownloadUrl().toString());
//
//                            String uploadId = databaseReference.push().getKey();
//                            databaseReference.child(uploadId).setValue(upload);
        } else {
                Toast.makeText(this, "No Image selected", Toast.LENGTH_SHORT).show();
        }
    }
}
