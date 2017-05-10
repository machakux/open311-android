package com.github.codetanzania.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.codetanzania.util.Util;

import java.io.File;
import java.io.IOException;

import tz.co.codetanzania.R;

import static android.app.Activity.RESULT_OK;

public class ImageCaptureFragment extends Fragment {

    public interface OnStartCapturePhoto {
        void startCapture(ImageView mImageView);
    }

    private ImageView mImageView;
    private View      mCaptureButton;

    private OnStartCapturePhoto mOnStartCapturePhoto;

    @Override public View onCreateView(
            LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState ) {
        return inflater.inflate(R.layout.frag_image_capture, viewGroup, false);
    }

    @Override public void onAttach(Context ctx) {
        super.onAttach(ctx);
        mOnStartCapturePhoto = (OnStartCapturePhoto) ctx;
    }

    @Override public void onViewCreated(
        View view, Bundle savedInstanceStatep) {
        mImageView = (ImageView) view.findViewById(R.id.img_CameraPreview);
        mCaptureButton = view.findViewById(R.id.btn_CaptureMoment);
        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // dispatchTakePictureIntent();
                mOnStartCapturePhoto.startCapture(mImageView);
            }
        });
    }
}
