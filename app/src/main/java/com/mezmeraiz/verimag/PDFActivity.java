package com.mezmeraiz.verimag;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class PDFActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String CURRENT_INDEX_KEY = "CURRENT_INDEX_KEY";
    private ParcelFileDescriptor mFileDescriptor;
    private PdfRenderer mPdfRenderer;
    private PdfRenderer.Page mPage;
    private ImageView mPDFImageView, mImageViewNext, mImageViewPrev;
    private TextView mIndexTextView;
    private ArrayList<File> mFileList;
    private int mIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        if (savedInstanceState != null) {
            mIndex = savedInstanceState.getInt(CURRENT_INDEX_KEY, 0);
        }
        mFileList = new ArrayList<>();
        initViews();
        File documents = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/Documents");
        for (File file : documents.listFiles()){
            if(isPDF(file))
                mFileList.add(file);
        }
        try {
            openPDF();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initViews(){
        mPDFImageView = (ImageView) findViewById(R.id.imageView);
        mIndexTextView = (TextView) findViewById(R.id.textViewIndex);
        mImageViewNext = (ImageView) findViewById(R.id.imageViewNext);
        mImageViewPrev = (ImageView) findViewById(R.id.imageViewPrev);
        mImageViewNext.setOnClickListener(this);
        mImageViewPrev.setOnClickListener(this);
        mIndexTextView.setText(String.valueOf(mIndex + 1));
    }

    private void openPDF() throws IOException {
        closeRenderer();
        mFileDescriptor = ParcelFileDescriptor.open(mFileList.get(mIndex), ParcelFileDescriptor.MODE_READ_ONLY);
        mPdfRenderer = new PdfRenderer(mFileDescriptor);
        mPage = mPdfRenderer.openPage(0);
        Bitmap bitmap = Bitmap.createBitmap(mPage.getWidth(), mPage.getHeight(),
                Bitmap.Config.ARGB_8888);
        mPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        mPDFImageView.setImageBitmap(bitmap);
    }

    private boolean isPDF(File file){
        if(!file.isFile()) return false;
        String fileName = file.getName();
        int index = fileName.lastIndexOf(".");
        return (fileName.lastIndexOf(".") > 0 && fileName.substring(index + 1).equalsIgnoreCase("pdf"));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_INDEX_KEY, mIndex);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            closeRenderer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeRenderer() throws IOException {
        if(mPage != null)
            mPage.close();
        if(mPdfRenderer != null)
            mPdfRenderer.close();
        if(mFileDescriptor != null)
            mFileDescriptor.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageViewNext:
                if(mIndex < mFileList.size() - 1){
                    mIndexTextView.setText(String.valueOf(++mIndex + 1));
                    try {
                        openPDF();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.imageViewPrev:
                if(mIndex > 0){
                    mIndexTextView.setText(String.valueOf(--mIndex + 1));
                    try {
                        openPDF();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

    }
}
