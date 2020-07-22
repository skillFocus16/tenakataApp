package com.naamini.tenakataapp.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.data.BufferedOutputStream;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.database.annotations.NotNull;
import com.naamini.tenakataapp.R;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Environment;
import android.print.PrintAttributes;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.uttampanchasara.pdfgenerator.CreatePdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Naamini Yonazi on 19/07/20
 */
public class PDFActivity extends AppCompatActivity {

    Context mContext;
    PDFView pdfView;
    File file;
    String pdfFileName = "Tenakata PDF Report", filePath;//,prevDataFile;
//     prevDataFile;
    Uri newUriPath;
    String pdfExtension = ".pdf";
    private File sdIconStorageDir;
    private SharedPreferences pref;
    private Bitmap bmp;
    private ImageView img;
    private byte prevDataFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_p_d_f);
            initToolbar();
            initComponents();
//            getWholeListViewItemsToBitmap(prevDataFile);
            createPdf();
            /*viewing pdf: 2 methods*/
            viewPDFWithinApp();
//            viewPDFWithAnotherApp();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initToolbar() {
        getSupportActionBar().setTitle(R.string.view_pdf);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initComponents() {
//        pref = getApplicationContext().getSharedPreferences("TenakataPref", 0); // 0 - for private mode
//        prevDataFile = pref.getString("recyclerViewData","");
        mContext = getApplicationContext();
        pdfView = findViewById(R.id.pdfView);
        img = findViewById(R.id.img);
        bmp=null;

//        Log.e("prevDataFile?: ", prevDataFile);

        Bundle extras = getIntent().getExtras();
        byte[] byteArray = extras.getByteArray("students");
        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

//            saveImageToPDF(bmp);
        Glide.with(this)
                .load(bmp)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .apply(RequestOptions.overrideOf(500, 500))
                .into(img);
//        prevDataFile = intent.getStringExtra("student");
        /*if (getIntent().getAction() != null) {
            prevDataFile = getIntent().getAction();
            Log.e("prevDataFile?: ", prevDataFile);
        }*/
       /* if (!getIntent().getExtras().isEmpty()) {
//            prevDataFile = intent.getStringExtra("students");
            prevDataFile = intent.getByteExtra("students",null);

            byte[] data = prevDataFile.getBytes();
            bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

            Glide.with(this)
                    .load(bmp)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .apply(RequestOptions.overrideOf(500, 500))
                    .into(img);
           *//* try {
                FileInputStream is = this.openFileInput(prevDataFile);
                bmp = BitmapFactory.decodeStream(is);
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }*//*
            Log.e("prevDataFile?: ", prevDataFile);
        } else {
            Log.e("prevDataFile?: ", "empty");
        }*/
    }

    public void createPdf() {
        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tenakata";
        file = new File(filePath + "/" + pdfFileName);

        new CreatePdf(this)
                .setPdfName(pdfFileName)
                .openPrintDialog(false)
                .setContentBaseUrl(null)
                .setPageSize(PrintAttributes.MediaSize.ISO_A4)
                .setContent("Nemmy")
//                .setContent(prevDataFile)
//                    .setFilePath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/tenakata")
                .setFilePath(filePath)
                .setCallbackListener(new CreatePdf.PdfCallbackListener() {
                    @Override
                    public void onFailure(@NotNull String s) {
                        // handle error
                        Toast.makeText(mContext, "Failure to create file", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(@NotNull String s) {
                        // do your stuff here
                        Toast.makeText(mContext, "PDF successfully created.", Toast.LENGTH_SHORT).show();
                    }
                })
                .create();
    }

    /**
     * Method for opening a pdf file within the App
     */
    private void viewPDFWithinApp() {
        try {
            File pdfFile = new File(filePath + "/" + pdfFileName + pdfExtension);
            newUriPath = Uri.fromFile(pdfFile);
            ;
            if (file != null) {
                pdfView.fromUri(newUriPath)
                        .enableSwipe(true) // allows to block changing pages using swipe
                        .swipeHorizontal(false)
                        //                .pageSnap(true)
                        //                .autoSpacing(true)
                        //                .pageFling(true)
                        .enableDoubletap(true)
                        .defaultPage(0)
                        .onError(t -> {
                            Toast.makeText(PDFActivity.this, "Can't load pdf file", Toast.LENGTH_SHORT).show();
                            Log.e("error?:", t.toString());
                        }).enableAntialiasing(true).spacing(10)
                        .load();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method for opening a pdf file with another App
     */
    private void viewPDFWithAnotherApp() {
        File pdfFile = new File(filePath + "/" + pdfFileName + pdfExtension);
        newUriPath = Uri.fromFile(pdfFile);

        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(newUriPath, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try {
            startActivity(Intent.createChooser(pdfIntent, "OPen pdf with:"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(PDFActivity.this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
        }
    }

    /*public void getWholeListViewItemsToBitmap(ListView p_ListView) {
        ListAdapter adapter = p_ListView.getAdapter();
        int itemscount = adapter.getCount();
        int allitemsheight = 0;
        List<Bitmap> bmps = new ArrayList<Bitmap>();
        for (int i = 0; i < itemscount; i++) {
            View childView = adapter.getView(i, null, p_ListView);
            childView.measure(
                    View.MeasureSpec.makeMeasureSpec(p_ListView.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            childView.layout(0, 0, childView.getMeasuredWidth(), childView.getMeasuredHeight());
            childView.setDrawingCacheEnabled(true);
            childView.buildDrawingCache();
            bmps.add(childView.getDrawingCache());
            allitemsheight += childView.getMeasuredHeight();
        }
        Bitmap bigbitmap = Bitmap.createBitmap(p_ListView.getMeasuredWidth(), allitemsheight,
                Bitmap.Config.ARGB_8888);

        Canvas bigcanvas = new Canvas(bigbitmap);
        Paint paint = new Paint();
        int iHeight = 0;
        for (int i = 0; i < bmps.size(); i++) {
            Bitmap bmp = bmps.get(i);
            bigcanvas.drawBitmap(bmp, 0, iHeight, paint);
            iHeight += bmp.getHeight();
            bmp.recycle();
            bmp = null;
        }
        storeImage(bigbitmap, "tenakataScreenShot.jpg");
    }

    public boolean storeImage(Bitmap imageData, String filename) {
        // get path to external storage (SD card)
        sdIconStorageDir = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/tenakata/");
        // create storage directories, if they don't exist
        sdIconStorageDir.mkdirs();
        try {
            String filePath = sdIconStorageDir.toString() + File.separator + filename;
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
            Toast.makeText(PDFActivity.this, "Image Saved at--" + filePath, Toast.LENGTH_LONG).show();
            // choose another format if PNG doesn't suit you
            imageData.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return false;
        }
        return true;
    }*/

    private void saveImageToPDF(Bitmap bitmap) {
        file = new File(filePath + "/" + pdfFileName);

//        file = new File(mFolder, filename + ".pdf");
        if (!file.exists()) {
            int height = 1979;//title.getHeight() + bitmap.getHeight();
            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), height, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
//            title.draw(canvas);
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(bitmap, null, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), null);

            document.finishPage(page);

            try {
                file.createNewFile();
                OutputStream out = new FileOutputStream(file);
                document.writeTo(out);
                document.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
