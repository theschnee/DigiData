package edu.cs.dartmouth.finalproject;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

// imports for firebase and cropper
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

// imports for excel worksheet
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// imports from openCV for text recognition
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    private static final int NUMBER_OF_ROWS = 2;
    private static final int NUMBER_OF_COLUMNS = 8;
    private static final String DEFAULT_VALUE = "Check";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (listWords == null) {
            listWords = new ArrayList<>();
        }

        OpenCVLoader.initDebug();
        //Intent intent = new Intent(this, AddItemActivity.class);
        //startActivity(intent);

        // set system properties for the worksheet to work
        // System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        // System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        // System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

        mButton = findViewById(R.id.button);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog();
                checkPermission();
                //makeExcelSheet();
            }
        });


    }

    // Dialog for choosing whether to pick a photo from library or to take one
    public void Dialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Choose where to take image from");

        alertDialogBuilder.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkPermission();
                if (!permissiongranted) {

                } else {
                    //GetPhoto();
                    crop(null);
                }
            }
        });

        alertDialogBuilder.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int argl) {
                checkPermission();
                if (!permissiongranted) {

                } else {
                    TakePhoto();
                }
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    // NOTE: This is a legacy method. Kept commented out in case of compatibility issues
    /*
    public void GetPhoto() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("Crop Data")
                .setAllowFlipping(false)
                .setFixAspectRatio(false)
                .start(this);
    } */

    // takes a photo using the camera and then sends the photo to the cropper to be cropped
    public void TakePhoto() {
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        photoFile = createImageFile();

        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        }

        try {
            startActivityForResult(intent, REQUEST_CODE_TAKE_FROM_CAMERA);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    // creates the file for the image if a new image is taken by the camera
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

    // cropper method for gallery photos and photos already taken
    public void crop(Uri source) {
        // check if source is null
        if (source != null) {
            // take uri of new photo and crop it
            if (photoFile != null)
                CropImage.activity(source)
                        .setActivityTitle("Crop Data")
                        .setAllowFlipping(false)
                        .setFixAspectRatio(false)
                        .start(this);
        } else {
            // if null pull photo from the gallery
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setActivityTitle("Crop Data")
                    .setAllowFlipping(false)
                    .setFixAspectRatio(false)
                    .start(this);
        }
    }

    /* The following are the functions for checking permissions for the app at the beginning of the cycle.
       This ensures that the app has ful ability to function at all times
     */
    private void checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
        } else {
            permissiongranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            permissiongranted = true;
            // GetPhoto();
            crop(null);

        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) ||
                        shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
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

                // get bitmap of cropped image from selection and set it to the file
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mFinalURI);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                // turn pulled bitmap into a temporary file
                File file = createImageFile();
                OutputStream os;
                try {
                    os = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.flush();
                    os.close();
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Error Cropping Image", e);
                }

                // TODO: Do this without firebase
                //   FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
                //   recognizeText(image);
                //   mProfilePic.setImageBitmap(bitmap);

                // clean up temporary file before reading text
                // TODO: Perhaps have the second portion in a separate activity
                localimageCleaner(file);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = res.getError();
            }
        }
    }

    private void localimageCleaner(File file) {
        // get the filepath of the file
        String filepath = file.getAbsolutePath();

        // turn the image into a matrix for line and text recognition
        Mat image;
        image = Imgcodecs.imread(filepath);

        // Check if image is loaded fine
        if(image.empty()) {
            Toast.makeText(this, "Error: Unable to Open Image", Toast.LENGTH_SHORT).show();
            return;
        }

        // if the image is in color make it into a grayscale image
        Mat grayscale = new Mat();
        if (image.channels() == 3) {
            Imgproc.cvtColor(image, grayscale, Imgproc.COLOR_BGR2GRAY);
        } else {
            grayscale = image;
        }

        // TODO: remove noise with a local function
        Mat denoised = new Mat();
        Photo.fastNlMeansDenoising(grayscale, denoised, 5);

        // convert the greyscale image into a binary one
        Mat binary = new Mat();
        Core.bitwise_not(denoised, denoised);
        Imgproc.adaptiveThreshold(denoised, binary, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, -2);

        // TODO: Straighten the image
        /*
        Mat srcImage = Imgcodecs.imread("input.png");
        Mat destImage = new Mat(500, 700, srcImage.type());
        Mat src = new MatOfPoint2f(new Point(x1, y1), new Point(x2, y2), new Point(x3, y3), new Point(x4, y4));
        Mat dst = new MatOfPoint2f(new Point(0, 0), new Point(destImage.width() - 1, 0), new Point(destImage.width() - 1, destImage.height() - 1), new Point(0, destImage.height() - 1));

        Mat transform = Imgproc.getPerspectiveTransform(src, dst);
        Imgproc.warpPerspective(srcImage, destImage, transform, destImage.size());
        */

        // create image of vertical lines
        Mat vertical = binary.clone();
        int vertical_size = vertical.rows() / 30;
        Mat verticalStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size( 1, vertical_size));
        Imgproc.morphologyEx(binary, vertical, Imgproc.MORPH_OPEN, verticalStructure);

        // remove vertical lines from the image so only horizontal lines remain
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(vertical, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.drawContours(binary, contours, -1, new Scalar(0,0,0), 6);

        // for testing purposes only
        String email = "chs.21@dartmouth.edu";

        // write image
        // Imgcodecs.imwrite(path, vertical);
        Imgcodecs.imwrite(filepath, binary);
        sendExcelSheet(email, file);
    }

    private void localProcessDocumentText() {

    }

    /* these are all the methods needed for excel sheet handling

     */
    public void makeExcelSheet() {

        // create filename of Excel Sheet
        String fileName = "FileName.xlsx";

        // make the directory for the file to be in
        // TODO: Create if statement for folder creation so that all files are in one folder instead of all in their own folders
        File folder = new File(getExternalFilesDir(null), "FolderName");
        folder.mkdir();

        // create the file inside directory
        File file = new File(folder, fileName);
        try {
            file.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        // write to Excel Sheet
        basicWriteExcelSheet(file);

        // set email string
        String email = "chs.21@dartmouth.edu";

        // send Excel sheet by Email
        sendExcelSheet(email, file);
    }

    public void basicWriteExcelSheet(File file) {

        // create excel workbook and sheet
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");

        // while we haven't reached number of rows in physical copy
        for (int i = 0; i < NUMBER_OF_ROWS; i++) {

            // create digital row
            Row row = sheet.createRow(i);

            // while we haven't reached number of columns in physical copy
            for (int k = 0; k < NUMBER_OF_COLUMNS; k++) {

                // create cell in current row and insert value
                row.createCell(k).setCellValue(DEFAULT_VALUE);
            }
        }

        // create, write to output stream, and close
        try {
            FileOutputStream fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendExcelSheet(String email, File fileAttached) {

        // new intent to send email
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_EMAIL, email);

        // Need to grant this permission
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Attachment
        intent.setType("vnd.android.cursor.dir/email");

        // Add file data to the intent
        if (fileAttached != null) {
            Uri outputFileUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID, fileAttached);

            intent.putExtra(Intent.EXTRA_STREAM, outputFileUri);
        } else {
            Toast.makeText(this, "Error: Unable to Send Email", Toast.LENGTH_SHORT).show();
        }

        // Add subject line
        String subject = fileAttached.getName();
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);

        // Check if network is available
        if (isNetworkAvailable(this)) {
            if (isAppAvailable(this, "com.google.android.gm"))
                intent.setPackage("com.google.android.gm");
            startActivityForResult(intent, 101);
        }
    }

    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // check if sdk version is over 23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // if so, use new method of checking for connection with some network
            // check to see if active network connection exists
            Network network;
            assert connectivityManager != null;
            if ((network = connectivityManager.getActiveNetwork()) != null) {

                // if so, check validity of connection
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);

                // long if statement checks for wifi, cellular, bluetooth, and ethernet connections
                assert networkCapabilities != null;
                if ((networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) || (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) || (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) || (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH))) {
                    return true;
                } else {
                    Toast.makeText(context, "Unable to Connect to Network", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {

                // no active network, return false
                Toast.makeText(context, "Unable to Connect to Network", Toast.LENGTH_SHORT).show();
                return false;
            }

        } else {
            // old version of checking for network connection
            assert connectivityManager != null;
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();

            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            } else {
                Toast.makeText(context, "Unable to Connect to Network", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

    }

    public static Boolean isAppAvailable(Context context, String appName) {

        PackageManager pm = context.getPackageManager();
        boolean isInstalled;

        try {
            pm.getPackageInfo(appName, PackageManager.GET_ACTIVITIES);
            isInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            isInstalled = false;
        }
        return isInstalled;
    }


    /* methods responsible for the text processing of the image and the translation of the physical excel sheet into digital form
       These methods are done using firebase and are the ones that would be replaced with a superior, proprietary, and local algorithm
     */
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

    private void ProcessDocumentText(FirebaseVisionDocumentText result) {
        String resultText = result.getText();
        listWords.clear();

        for (FirebaseVisionDocumentText.Block block : result.getBlocks()) {
            String blockText = block.getText();
            Float blockConfidence = block.getConfidence();
            List<RecognizedLanguage> blockRecognizedLanguages = block.getRecognizedLanguages();
            Rect blockFrame = block.getBoundingBox();
            for (FirebaseVisionDocumentText.Paragraph paragraph : block.getParagraphs()) {
                String paragraphText = paragraph.getText();
                Float paragraphConfidence = paragraph.getConfidence();
                List<RecognizedLanguage> paragraphRecognizedLanguages = paragraph.getRecognizedLanguages();
                Rect paragraphFrame = paragraph.getBoundingBox();

                if (list_lines_sentences == null) {
                    list_lines_sentences = new ArrayList<>();
                }
                if (line_breaks_words == null) {
                    line_breaks_words = new ArrayList<>();
                }

                list_lines_sentences.add(paragraphText.replace("\\n", "\n"));

//                    int lines_break_words = Objects.requireNonNull(paragraph.getRecognizedBreak()).getDetectedBreakType();
//
//                    if(lines_break_words == 3 || lines_break_words == 5 || lines_break_words == 2){
//                        line_breaks_words.add(lines_break_words);
//                        Toast.makeText(this, lines_break_words+"",Toast.LENGTH_LONG).show();
//                    }
//                    Toast.makeText(this, line_breaks_words+"",Toast.LENGTH_LONG).show();
                //     Toast.makeText(this, list_lines_sentences+"" ,Toast.LENGTH_LONG).show();
                System.out.println(list_lines_sentences + "");

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


}
