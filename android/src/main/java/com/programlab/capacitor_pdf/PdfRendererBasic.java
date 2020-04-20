package com.programlab.capacitor_pdf;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.programlab.capacitor_pdf.models.PdfAnnotations;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PdfRendererBasic#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PdfRendererBasic extends Fragment {
    private static final String DEBUG_TAG = "Gesture";
    private PdfRendererBasicViewModel mViewModel;
    private ScaleGestureDetector mScaleGestureDetector;
    View view;
    ArrayList<View> btns = new ArrayList<View>();
    ImageView image;
    RelativeLayout vertical2;
    private float mScaleFactor = 1.0f;
    private GestureDetectorCompat mDetector;
    private ArrayList<PdfAnnotations> annotation;

    public PdfRendererBasic(ArrayList<PdfAnnotations> annotationsData){
        annotation = annotationsData;
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.previous) {
                if (mViewModel != null) {
                    mViewModel.showPrevious();
                    clearAnottationLink();
                }
            } else if (id == R.id.next) {
                if (mViewModel != null) {
                    mViewModel.showNext();
                    clearAnottationLink();
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_pdf_renderer_basic, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        mScaleGestureDetector = new ScaleGestureDetector(this.getContext(), new ScaleListener());
        image = view.findViewById(R.id.image);
        vertical2 = view.findViewById(R.id.frame);
        final Button buttonPrevious = view.findViewById(R.id.previous);
        final Button buttonNext = view.findViewById(R.id.next);

        view.setOnTouchListener(new View.OnTouchListener() {
            private float my;
            private float mx;

            public boolean onTouch(View v, MotionEvent event) {
                float curX, curY;
                ImageView image2 = v.findViewById(R.id.image);
                RelativeLayout vertical = v.findViewById(R.id.frame);
                final int width = image2.getWidth();
                int height = image2.getHeight();
                mScaleGestureDetector.onTouchEvent(event);
                final int action = event.getAction();
                switch (action & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        System.out.println("down");
                        mx = event.getX();
                        my = event.getY();
                        System.out.println("x: " + mx + ",y: " + my);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        System.out.println("Moviendo");
                        curX = event.getX();
                        curY = event.getY();
                        int scrollX = (int) (mx - curX);
                        int scrollY = (int) (my - curY);

                        if (curX > width){
                            scrollX = width;
                        }
                        // image.scrollBy(scrollX, scrollY);
                        vertical.scrollBy(scrollX, scrollY);
                        mx = curX;
                        my = curY;
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        System.out.println("up");
                        curX = event.getX();
                        curY = event.getY();
                        // image.scrollBy((int) (mx - curX), (int) (my - curY));
                        vertical.scrollBy((int) (mx - curX), (int) (my - curY));
                        System.out.println("x_up: " + curX + ",y_up: " + curY);
                        break;
                }

                return true;
            }
        });




        // Bind data.
        mViewModel = ViewModelProviders.of(this).get(PdfRendererBasicViewModel.class);
        mViewModel.setDensityDpi(getResources().getDisplayMetrics().densityDpi);
        final LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();
        mViewModel.getPageInfo().observe(viewLifecycleOwner, new Observer<PdfRendererBasicViewModel.PageInfo>() {
            @Override
            public void onChanged(PdfRendererBasicViewModel.PageInfo pageInfo) {
                if (pageInfo == null) {
                    return;
                }
                final Activity activity = PdfRendererBasic.this.getActivity();
                if (activity != null) {
                    activity.setTitle(PdfRendererBasic.this.getString(R.string.app_name_with_index,
                            pageInfo.index + 1, pageInfo.count));
                    /**
                     * Añadir anotaciones
                     */

                    for (PdfAnnotations pdfAnnotation: annotation
                         ) {
                        addAnottationLink(pdfAnnotation.getPoint_x(), pdfAnnotation.getPoint_y(), pdfAnnotation.getPoint_link());
                    }
                }
            }
        });
        mViewModel.getPageBitmap().observe(viewLifecycleOwner, new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bm) {
                image.setImageBitmap(bm);
            }
        });
        mViewModel.getPreviousEnabled().observe(viewLifecycleOwner, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean enabled) {
                buttonPrevious.setEnabled(enabled);
            }
        });
        mViewModel.getNextEnabled().observe(viewLifecycleOwner, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean enabled) {
                buttonNext.setEnabled(enabled);
            }
        });

        // Bind events.
        buttonPrevious.setOnClickListener(mOnClickListener);
        buttonNext.setOnClickListener(mOnClickListener);
    }

    public void addAnottationLink(int x, int y, final String link) {
        System.out.println("annotation x: " + x);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(50, 50);
        ImageButton tempButton = new ImageButton(getActivity().getApplicationContext());
        layoutParams.setMargins(x, y, 0, 0);
        tempButton.setBackground(getResources().getDrawable(R.drawable.circle_background));
        tempButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_black_24dp));
        tempButton.setLayoutParams(layoutParams);
        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("evento click en el enlace");
                System.out.println(link);
                Intent navigator = new Intent(getActivity(), NavigatorActivity.class);
                navigator.putExtra("url", link);
                startActivity(navigator);
            }
        });
        btns.add(tempButton);
        vertical2.addView(tempButton);
    }

    public void clearAnottationLink(){
        for (View btn: btns) {
            vertical2.removeView(btn);
        }
        btns.clear();
        Log.i(DEBUG_TAG, "Limpio");
    }


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f,
                    Math.min(mScaleFactor, 10.0f));
            vertical2.setScaleX(mScaleFactor);
            vertical2.setScaleY(mScaleFactor);
            //image.setScaleX(mScaleFactor);
            //image.setScaleY(mScaleFactor);
            return true;
        }
    }
}
