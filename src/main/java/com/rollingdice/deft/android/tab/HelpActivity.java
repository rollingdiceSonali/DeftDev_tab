package com.rollingdice.deft.android.tab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import net.sf.andpdf.pdfviewer.PdfViewerActivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Rolling Dice on 3/5/2016.
 */
public class HelpActivity extends Activity implements OnPageChangeListener
{


    public static final String ABOUT_FILE = "usermanual.pdf";
    Integer pageNumber = 1;
    String pdfName ;

    private com.joanzapata.pdfview.PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(
                R.layout.user_manual);
        pdfView= (PDFView) findViewById(R.id.pdfView);

        pdfName=ABOUT_FILE;
        display(pdfName, false);
        Toast.makeText(HelpActivity.this,"Help",Toast.LENGTH_LONG).show();
       /* DatabaseReference localRef = GlobalApplication.firebaseRef;

        AssetManager assetManager = getAssets();

        InputStream in = null;
        OutputStream out = null;
        File file = new File(getFilesDir(), "usermanual.pdf");
        try
        {
            in = assetManager.open("usermanual.pdf");
            out = openFileOutput(file.getName(), Context.MODE_WORLD_READABLE);

            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e)
        {
            Log.e("tag", e.getMessage());
        }
*/
       /* Intent intent = new Intent(this, MyPdfViewerActivity.class);
        intent.putExtra(PdfViewerActivity.EXTRA_PDFFILENAME, getFilesDir()+"/usermanual.pdf");
        startActivity(intent);
*/


    }

    /*private void copyFile(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, read);
        }
    }*/
    private void display(String assetFileName, boolean jumpToFirstPage)
    {
        if (jumpToFirstPage) pageNumber = 1;
        setTitle(pdfName = assetFileName);

        pdfView.fromAsset(assetFileName)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .load();
    }

    @Override
    public void onPageChanged(int page, int pageCount)
    {
        pageNumber = page;

        //setTitle(format("%s %s / %s", pdfName, page, pageCount));

    }




}
