package com.mezmeraiz.verimag;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnClickFileListener {

    RecyclerView mRecyclerView;
    RecyclerViewAdapter mRecyclerViewAdapter;
    public static final String NAME = "NAME";
    public static final String IS_DIRECTORY = "IS_DIRECTORY";
    private String mCurrentDirectory;
    public Context mContext;
    public String mDestinationDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDestinationDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/Documents";
        mContext = getApplicationContext();
        mCurrentDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Выберите ZIP файл");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        initRecyclerView();
    }

    private void initRecyclerView(){
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewAdapter = new RecyclerViewAdapter(this, createFileList(), this);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
    }

    private ArrayList<Map> createFileList(){
        ArrayList<Map> fileList = new ArrayList<>();
        File[] files = new File(mCurrentDirectory).listFiles();
        ArrayList<Map> zipFiles = new ArrayList<>();
        for (File file : files) {
            Map map = new HashMap();
            if (file.isDirectory()) {
                map.put(NAME, file.getName());
                map.put(IS_DIRECTORY, true);
                fileList.add(map);
            } else if (file.isFile() && isZip(file)) {
                map.put(NAME, file.getName());
                map.put(IS_DIRECTORY, false);
                zipFiles.add(map);
            }
        }
        fileList.addAll(zipFiles);
        return fileList;
    }

    private boolean isZip(File file){
        if(!file.isFile()) return false;
        String fileName = file.getName();
        int index = fileName.lastIndexOf(".");
        return (fileName.lastIndexOf(".") > 0 && fileName.substring(index + 1).equalsIgnoreCase("zip"));
    }

    @Override
    public void onClickFile(String fileName) {
        final File file = new File(mCurrentDirectory + "/" + fileName);
        if(file.isDirectory()){
            mCurrentDirectory = file.getAbsolutePath();
            mRecyclerViewAdapter.update(createFileList());
        }else{
            try {
                decompress(file);
            } catch (ZipException e) {
                e.printStackTrace();
            }
        }
    }

    private void decompress(File file) throws ZipException {
        File oldFile = new File(mDestinationDir);
        if(oldFile.exists()){
            for(File f : oldFile.listFiles()){
                f.delete();
            }
        }
        ZipFile zipFile = new ZipFile(file);
        zipFile.setFileNameCharset("Cp866");
        zipFile.extractAll(mDestinationDir);
        startActivity(new Intent(this, PDFActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        if(mCurrentDirectory.equals(Environment.getExternalStorageDirectory().getAbsolutePath())){
            finish();
        }else{
            int index = mCurrentDirectory.lastIndexOf("/");
            mCurrentDirectory = mCurrentDirectory.substring(0, index);
            mRecyclerViewAdapter.update(createFileList());
        }
    }
}
