package com.programlab.capacitor_pdf;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

@NativePlugin()
public class PdfPlugin extends Plugin {

    private PluginCall call;

    @PluginMethod()
    public void viewPdf(PluginCall callJavascript) {
        call = callJavascript;
        String linkPdf = call.getString("linkPdf");
        JSArray annotations = call.getArray("annotations", new JSArray());

        Intent intent = new Intent(this.getBridge().getActivity(), PdfActivity.class);
        intent.putExtra("linkPdf", linkPdf);
        intent.putExtra("annotations", annotations.toString());
        this.getBridge().getActivity().startActivity(intent);
        /*JSObject ret = new JSObject();
        ret.put("value", value);*/
        call.success();
    }


}
