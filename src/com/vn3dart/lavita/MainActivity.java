/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package com.vn3dart.lavita;

import android.os.Bundle;
import android.widget.Toast;

import org.apache.cordova.CordovaActivity;

public class MainActivity extends CordovaActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//         enable Cordova apps to be started in the background
//        Bundle extras = getIntent().getExtras();
//        if (extras != null && extras.getBoolean("cdvStartInBackground", false)) {
//            moveTaskToBack(true);
//        }
        try {
            String urlFile = getIntent().getStringExtra("path_url");
            loadUrl("file://" + urlFile);
        } catch (Exception e) {
            Toast.makeText(this, "Xin vui lòng khởi động lại!", Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (appView.canGoBack()){
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
