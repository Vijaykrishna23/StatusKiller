package com.hemanth.statuskiller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import java.io.File;

public class DeleteStatusesReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        File file = new File(Environment.getExternalStorageDirectory() + "//Whatsapp//Media//.Statuses");
        deleteRecursive(file);

    }


    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }


}
