package com.itshiteshverma.attackerapp.ui.tools;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.itshiteshverma.attackerapp.DatabaseHelper;
import com.itshiteshverma.attackerapp.Note;
import com.itshiteshverma.attackerapp.R;
import com.itshiteshverma.attackerapp.ui.gallery.GalleryFragment;

import java.io.File;

import static com.itshiteshverma.attackerapp.DataBase_ImportExportHandler.exportDB;
import static com.itshiteshverma.attackerapp.DataBase_ImportExportHandler.importDB;
import static com.itshiteshverma.attackerapp.MainActivity.toastHelper;

public class ToolsFragment extends Fragment {

    private ToolsViewModel toolsViewModel;
    View view;
    DatabaseHelper dbHelper;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(ToolsViewModel.class);
        view = inflater.inflate(R.layout.fragment_tools, container, false);
        initilize();
        return view;
    }

    private void initilize() {
        dbHelper = new DatabaseHelper(getActivity());

        Button bImport = view.findViewById(R.id.bImportDataBase);
        Button bVisualize = view.findViewById(R.id.bVisualize);
        Button bGetValues = view.findViewById(R.id.bgetValues);
        Button bSelectMap = view.findViewById(R.id.bSelectMap);
        Button bImportLocally = view.findViewById(R.id.bImportDataBaseLocally);
        PRDownloader.initialize(getActivity().getApplicationContext());

        bImportLocally.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importDB(getActivity(), getLayoutInflater());
                toastHelper.toastIconInfo("Completed");
            }
        });


        bImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog Dialog = new Dialog(getActivity());
                Dialog.requestWindowFeature(1);
                Dialog.setContentView(R.layout.dialog_loading);
                Dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                Dialog.setCancelable(false);
                TextView Heading = Dialog.findViewById(R.id.tvHeading);
                Heading.setText("Loading");
                Dialog.show();

                String appFolder_Name = getString(R.string.app_name);
                File appFolderLocation = new File(Environment.getExternalStorageDirectory(), appFolder_Name);
                if (!appFolderLocation.exists()) {
                    appFolderLocation.mkdirs();
                }
                String database_link = "https://firebasestorage.googleapis.com/v0/b/chat-b045a.appspot.com/o/SensorDataCollectionApp%2FDATABASE_DATA%2FHitesh%2Fvalues_db.db?alt=media&token=d50d8d8b-fd0a-4347-8400-a56242f125f8";


                PRDownloader.download(database_link, appFolderLocation.getPath(), Note.DATABASE_NAME + ".db")
                        .build()
                        .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                            @Override
                            public void onStartOrResume() {
                                toastHelper.toastIconInfo(" Started");
                            }
                        })
                        .setOnPauseListener(new OnPauseListener() {
                            @Override
                            public void onPause() {

                            }
                        })
                        .setOnCancelListener(new OnCancelListener() {
                            @Override
                            public void onCancel() {
                                toastHelper.toastIconError(" Failed");
                            }
                        })
                        .setOnProgressListener(new OnProgressListener() {
                            @Override
                            public void onProgress(Progress progress) {
                            }
                        })
                        .start(new OnDownloadListener() {
                            @Override
                            public void onDownloadComplete() {
                                importDB(getActivity(), getLayoutInflater());
                                Dialog.dismiss();
                                toastHelper.toastIconInfo("Completed");
                            }

                            @Override
                            public void onError(Error error) {
                                Dialog.dismiss();
                                toastHelper.toastIconError("Failed");
                            }

                        });

            }
        });

        bGetValues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.createTable();
                dbHelper.noiseFiltering();
                dbHelper.getCommands();

                exportDB(getActivity(), getLayoutInflater());
                toastHelper.toastIconInfo("Done");
            }
        });

        bVisualize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GalleryFragment nextFrag = new GalleryFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.llMainLayout, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();

            }
        });

        bSelectMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastHelper.toastIconInfo("Map 1 Selected");
            }
        });

    }
}