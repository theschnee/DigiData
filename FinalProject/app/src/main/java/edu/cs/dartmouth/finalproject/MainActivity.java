package edu.cs.dartmouth.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.document.FirebaseVisionCloudDocumentRecognizerOptions;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.google.firebase.ml.vision.document.FirebaseVisionDocumentText.RecognizedBreak.EOL_SURE_SPACE;
import static org.apache.commons.lang3.StringUtils.join;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_TAKE_FROM_CAMERA = 0;
    private boolean permissiongranted;
    private String mImagePath = "NA";
    private Uri mFinalURI;
    private String uriString;
    private Uri mURI;
    private Intent intent;
    private Bitmap bitmap;
    private File photoFile;
    private Button mButton;
    private static ArrayList<String> listWords;
    private static ArrayList<Integer> line_breaks_words;
    private static ArrayList<String> list_lines_sentences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(listWords == null){
            listWords = new ArrayList<>();
        }


//        Intent intent = new Intent(this, AddItemActivity.class);
//        startActivity(intent);


        mButton = findViewById(R.id.button);


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog();

            }
        });


    }

    public void Dialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Choose where to take image from");

        alertDialogBuilder.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkPermission();
                if(!permissiongranted) {

                } else {
                    GetPhoto();
                }
            }
        });

        alertDialogBuilder.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int argl) {
                checkPermission();
                if(!permissiongranted) {

                } else {
                    TakePhoto();
                }
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void GetPhoto() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("Sign Up")
                .setAllowFlipping(false)
                .setFixAspectRatio(false)
                .start(this);
    }

    private void checkPermission(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
        } else {
            permissiongranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            permissiongranted = true;
            GetPhoto();

        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) ||
                        shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    AlertDialog.Builder Request = new AlertDialog.Builder(this);
                    Request.setMessage("This permission is important for the proper function of the app.").setTitle("Important Permission Requested");
                    Request.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
                        }
                    });

                    Request.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    Request.show();
                } else {
                    finish();
                }
            }
        }
    }

    public void TakePhoto() {
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        photoFile = createImageFile();

        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        }

        try {
            startActivityForResult(intent, REQUEST_CODE_TAKE_FROM_CAMERA);
        } catch(ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public void crop(Uri source) {
        if(photoFile != null)
            CropImage.activity(source)
                    .setActivityTitle("Crop Data")
                    .setAllowFlipping(false)
                    .setFixAspectRatio(false)
                    .start(this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TAKE_FROM_CAMERA && resultCode == AppCompatActivity.RESULT_OK) {
            mURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, photoFile);
            crop(mURI);

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            CropImage.ActivityResult res = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mFinalURI = res.getUri();
                mImagePath = mFinalURI.getPath();
                bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mFinalURI);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
                recognizeText(image);
                //   mProfilePic.setImageBitmap(bitmap);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = res.getError();
            }
        }
    }
    private void recognizeText(FirebaseVisionImage image) {
        /*FOR NORMAL IMAGE DETECTION*/
//        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();


        /*FOR LANGUAGE ASSIST DETECTION*/
//        FirebaseVisionCloudDocumentRecognizerOptions options = new FirebaseVisionCloudDocumentRecognizerOptions.Builder()
//                        .setLanguageHints(Arrays.asList("en", "hi")).build();
//        FirebaseVisionDocumentTextRecognizer detector = FirebaseVision.getInstance()
//                .getCloudDocumentTextRecognizer(options);

        FirebaseVisionDocumentTextRecognizer detector = FirebaseVision.getInstance().getCloudDocumentTextRecognizer();

        final Task<FirebaseVisionDocumentText> result = detector.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionDocumentText>() {
            @Override
            public void onSuccess(FirebaseVisionDocumentText firebaseVisionDocumentText) {
                ProcessDocumentText(firebaseVisionDocumentText);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void ProcessDocumentText(FirebaseVisionDocumentText result){
        String resultText = result.getText();
        listWords.clear();

        for (FirebaseVisionDocumentText.Block block: result.getBlocks()) {
            String blockText = block.getText();
            Float blockConfidence = block.getConfidence();
            List<RecognizedLanguage> blockRecognizedLanguages = block.getRecognizedLanguages();
            Rect blockFrame = block.getBoundingBox();
            for (FirebaseVisionDocumentText.Paragraph paragraph: block.getParagraphs()) {
                String paragraphText = paragraph.getText();
                Float paragraphConfidence = paragraph.getConfidence();
                List<RecognizedLanguage> paragraphRecognizedLanguages = paragraph.getRecognizedLanguages();
                Rect paragraphFrame = paragraph.getBoundingBox();

                if(list_lines_sentences == null){
                    list_lines_sentences = new ArrayList<>();
                }
                if(line_breaks_words == null){
                    line_breaks_words = new ArrayList<>();
                }

                list_lines_sentences.add(paragraphText.replace("\\n","\n"));

//                    int lines_break_words = Objects.requireNonNull(paragraph.getRecognizedBreak()).getDetectedBreakType();
//
//                    if(lines_break_words == 3 || lines_break_words == 5 || lines_break_words == 2){
//                        line_breaks_words.add(lines_break_words);
//                        Toast.makeText(this, lines_break_words+"",Toast.LENGTH_LONG).show();
//                    }
//                    Toast.makeText(this, line_breaks_words+"",Toast.LENGTH_LONG).show();
           //     Toast.makeText(this, list_lines_sentences+"" ,Toast.LENGTH_LONG).show();
                System.out.println(list_lines_sentences+"");

                for (FirebaseVisionDocumentText.Word word : paragraph.getWords()) {
                    String wordText = word.getText();
                    Float wordConfidence = word.getConfidence();
                    List<RecognizedLanguage> wordRecognizedLanguages = word.getRecognizedLanguages();
                    Rect wordFrame = word.getBoundingBox();

                    listWords.add(wordText);

                    int break_type = paragraph.getRecognizedBreak().getDetectedBreakType();
                   // Toast.makeText(this, break_type+"",Toast.LENGTH_LONG).show();



//                            int lines_break_words = Objects.requireNonNull(word.getRecognizedBreak()).getDetectedBreakType();
//
//                            if(line_breaks_words == null){
//                                line_breaks_words = new ArrayList<>();
//                            }
//
//                            if(lines_break_words == 3 || lines_break_words == 5 || lines_break_words == 2){
//
//                                line_breaks_words.add(lines_break_words);
//                                Toast.makeText(this, lines_break_words+"",Toast.LENGTH_LONG).show();
//
//                            }
//                            Toast.makeText(this, line_breaks_words+"",Toast.LENGTH_LONG).show();


                    for (FirebaseVisionDocumentText.Symbol symbol : word.getSymbols()) {
                        String symbolText = symbol.getText();


                        Float symbolConfidence = symbol.getConfidence();
                        List<RecognizedLanguage> symbolRecognizedLanguages = symbol.getRecognizedLanguages();
                        Rect symbolFrame = symbol.getBoundingBox();
                    }
                }
            }
        }


//            String sentence = StringUtils.join(listWords, " ");

        Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
        intent.putStringArrayListExtra("sentences", list_lines_sentences);
//            intent.putExtra("word", sentence);

        startActivity(intent);
    }


    /* FOR NORMAL IMAGE DETECTION */

//        Task<FirebaseVisionText> result = detector.processImage(image)
//            .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
//                @Override
//                public void onSuccess(FirebaseVisionText firebaseVisionText) {
//                    for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
//                        Rect boundingBox = block.getBoundingBox();
//                        Point[] cornerPoints = block.getCornerPoints();
//                        String text = block.getText();
//                        Float block_confidence = block.getConfidence();
//                        List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
//
//                        for (FirebaseVisionText.Line line: block.getLines()) {
//                            String lineText = line.getText();
//                            Float lineConfidence = line.getConfidence();
//                            List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
//                            Point[] lineCornerPoints = line.getCornerPoints();
//                            Rect lineFrame = line.getBoundingBox();
//                            for (FirebaseVisionText.Element element: line.getElements()) {
//                                String elementText = element.getText();
//                                Float elementConfidence = element.getConfidence();
//                                List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
//                                Point[] elementCornerPoints = element.getCornerPoints();
//                                Rect elementFrame = element.getBoundingBox();
//                            }
//                        }
//                    }
//                }
//            })
//            .addOnFailureListener(
//                    new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//
//                        }
//                    });

//    }





}
