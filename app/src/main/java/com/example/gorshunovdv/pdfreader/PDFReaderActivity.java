package com.example.gorshunovdv.pdfreader;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import ru.dimaarts.documentsreader.activity.DocumentsReaderActivity;

public class PDFReaderActivity extends AppCompatActivity {
    private static final String LOG_TAG = "PDFReaderActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfreader);
        Intent intent = new Intent(this, DocumentsReaderActivity.class);
        List<String> files = copyAssetsDir(this, "docs");
        String[] paths = files.toArray(new String[files.size()]);
        intent.putExtra(DocumentsReaderActivity.DOC_PATHS, paths);
        intent.putExtra(DocumentsReaderActivity.TITLE, R.string.documents_reader_title);
        startActivity(intent);
    }

    public static List<String> copyAssetsDir(Context context, String start_dir) {
        AssetManager assetManager = context.getAssets();

        List<String> result = new ArrayList<>();

        String cut_dir = "";
        int slash_pos = start_dir.indexOf(File.separatorChar);

        if (slash_pos > 0)
            cut_dir = start_dir.substring(slash_pos + 1);

        final String output_dir = context.getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + cut_dir;

        File newDir = new File(output_dir);
        newDir.mkdir();

        String[] files;

        try {

            files = assetManager.list(start_dir);

            for (String file : files) {

                String input_name = start_dir + File.separator + file;
                String output_name = output_dir + File.separator + file;

                boolean isFile = input_name.lastIndexOf('.') > 0;

                if (isFile) {
                    copyFileFromAsset(context, input_name, output_name);
                    result.add(output_name);
                } else {
                    copyAssetsDir(context, input_name);
                }

            }

        } catch (IOException e) {
            Log.d(LOG_TAG, "Cannot find assets files: " + e.getMessage());
        }
        return result;
    }

    private static void copyFileFromAsset(Context context, String input, String output) throws IOException {

        File file = new File(output);

        AssetManager assetManager = context.getAssets();

        InputStream in = assetManager.open(input);
        OutputStream out = new FileOutputStream(output);

        int length = 0;
        byte[] buffer = new byte[1024];

        while ((length = in.read(buffer)) > 0) {

            out.write(buffer, 0, length);
        }

        in.close();
        out.close();
    }
}
