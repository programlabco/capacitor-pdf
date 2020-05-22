package com.programlab.capacitor_pdf;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.annotation.WorkerThread;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This holds all the data we need for this sample app.
 * <p>
 * We use a {@link PdfRenderer} to render PDF pages as
 * {@link Bitmap}s.
 */
public class PdfRendererBasicViewModel extends AndroidViewModel {

    private static final String TAG = "PdfRendererBasic";

    /**
     * The filename of the PDF.
     */
    private static final String FILENAME = "pdfCache.pdf";

    private final MutableLiveData<PageInfo> mPageInfo = new MutableLiveData<>();
    private final MutableLiveData<Bitmap> mPageBitmap = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mPreviousEnabled = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mNextEnabled = new MutableLiveData<>();

    private final Executor mExecutor;
    private ParcelFileDescriptor mFileDescriptor;
    private PdfRenderer mPdfRenderer;
    private PdfRenderer.Page mCurrentPage;
    private boolean mCleared;

    public int getDensityDpi() {
        return densityDpi;
    }

    public void setDensityDpi(int densityDpi) {
        this.densityDpi = densityDpi;
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private int index;


    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    private File file;

    private int densityDpi;

    @SuppressWarnings("unused")
    public PdfRendererBasicViewModel(Application application) {
        this(application, false);
    }


    PdfRendererBasicViewModel(Application application, boolean useInstantExecutor) {
        super(application);
        if (useInstantExecutor) {
            mExecutor = new Executor() {
                @Override
                public void execute(Runnable r) {
                    r.run();
                }
            };;
        } else {
            mExecutor = Executors.newSingleThreadExecutor();
        }
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    PdfRendererBasicViewModel.this.openPdfRenderer();
                    PdfRendererBasicViewModel.this.showPage(0);
                    if (mCleared) {
                        PdfRendererBasicViewModel.this.closePdfRenderer();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Failed to open PdfRenderer", e);
                }
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    PdfRendererBasicViewModel.this.closePdfRenderer();
                    mCleared = true;
                } catch (IOException e) {
                    Log.i(TAG, "Failed to close PdfRenderer", e);
                }
            }
        });
    }



    LiveData<PageInfo> getPageInfo() {
        return mPageInfo;
    }

    LiveData<Bitmap> getPageBitmap() {
        return mPageBitmap;
    }

    LiveData<Boolean> getPreviousEnabled() {
        return mPreviousEnabled;
    }

    LiveData<Boolean> getNextEnabled() {
        return mNextEnabled;
    }

    void showPrevious() {
        if (mPdfRenderer == null || mCurrentPage == null) {
            return;
        }
        final int index = mCurrentPage.getIndex();
        if (index > 0) {
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    PdfRendererBasicViewModel.this.showPage(index - 1);
                }
            });
        }
    }

    void showNext() {
        if (mPdfRenderer == null || mCurrentPage == null) {
            return;
        }
        final int index = mCurrentPage.getIndex();
        if (index + 1 < mPdfRenderer.getPageCount()) {
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    PdfRendererBasicViewModel.this.showPage(index + 1);
                }
            });
        }
    }

    @WorkerThread
    private void openPdfRenderer() throws IOException {
        final File file = new File(Environment.getExternalStorageDirectory() + "/jhonocampo/", FILENAME);
        if (!file.exists()) {
            // Since PdfRenderer cannot handle the compressed asset file directly, we copy it into
            // the cache directory.
            final InputStream asset = getApplication().getAssets().open(FILENAME);
            final FileOutputStream output = new FileOutputStream(file);
            final byte[] buffer = new byte[1024];
            int size;
            while ((size = asset.read(buffer)) != -1) {
                output.write(buffer, 0, size);
            }
            asset.close();
            output.close();
        }
        mFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        if (mFileDescriptor != null) {
            mPdfRenderer = new PdfRenderer(mFileDescriptor);
        }
    }

    @WorkerThread
    private void closePdfRenderer() throws IOException {
        if (mCurrentPage != null) {
            mCurrentPage.close();
        }
        if (mPdfRenderer != null) {
            mPdfRenderer.close();
        }
        if (mFileDescriptor != null) {
            mFileDescriptor.close();
        }
    }

    @WorkerThread
    private void showPage(int index) {
        // Make sure to close the current page before opening another one.
        if (null != mCurrentPage) {
            mCurrentPage.close();
        }
        // Use `openPage` to open a specific page in PDF.
        mCurrentPage = mPdfRenderer.openPage(index);
        // Important: the destination bitmap must be ARGB (not RGB).
        final Bitmap bitmap = Bitmap.createBitmap(
                this.getDensityDpi() * mCurrentPage.getWidth() / 72,
                this.getDensityDpi() * mCurrentPage.getHeight() / 72,
                Bitmap.Config.ARGB_8888);
        // Here, we render the page onto the Bitmap.
        // To render a portion of the page, use the second and third parameter. Pass nulls to get
        // the default result.
        // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
        mCurrentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        mPageBitmap.postValue(bitmap);
        final int count = mPdfRenderer.getPageCount();
        mPageInfo.postValue(new PageInfo(index, count));
        mPreviousEnabled.postValue(index > 0);
        mNextEnabled.postValue(index + 1 < count);
    }

    static class PageInfo {
        final int index;
        final int count;

        PageInfo(int index, int count) {
            this.index = index;
            this.count = count;
        }
    }

}
