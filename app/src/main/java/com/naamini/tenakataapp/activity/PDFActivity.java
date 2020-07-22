package com.naamini.tenakataapp.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.database.annotations.NotNull;
import com.naamini.tenakataapp.R;
import com.uttampanchasara.pdfgenerator.CreatePdf;

import java.io.File;

/**
 * Created by Naamini Yonazi on 19/07/20
 */
public class PDFActivity extends AppCompatActivity {

    Context mContext;
    PDFView pdfView;
    File file;
    String pdfFileName = "Tenakata PDF Report", filePath;
    Uri newUriPath;
    String pdfExtension = ".pdf";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_p_d_f);
            initToolbar();
            initComponents();
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
        mContext = getApplicationContext();
        pdfView = findViewById(R.id.pdfView);
    }

    public void createPdf() {
        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tenakata";
        file = new File(filePath + "/" + pdfFileName);

        new CreatePdf(this)
                .setPdfName(pdfFileName)
                .openPrintDialog(false)
                .setContentBaseUrl(null)
                .setPageSize(PrintAttributes.MediaSize.ISO_A4)
                .setContent("Naamini Yonazi")
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
}
