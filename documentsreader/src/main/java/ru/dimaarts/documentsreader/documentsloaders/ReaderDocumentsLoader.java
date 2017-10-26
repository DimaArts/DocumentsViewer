package ru.dimaarts.documentsreader.documentsloaders;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.dimaarts.documentsreader.items.DocumentItem;
import ru.dimaarts.documentsreader.utils.FileUtils;
import ru.dimaarts.documentsreader.utils.Size;

/**
 * Created by gorshunovdv on 1/20/2017.
 */
public class ReaderDocumentsLoader implements DocumentsLoader {
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private ThreadPoolExecutor mDocumentsLoaderThreadPoolExecutor;
    private final BlockingQueue<Runnable> mDocumentsLoaderWorkQueue;
    // Sets the amount of time an idle thread waits before terminating
    private static final int KEEP_ALIVE_TIME = 1;
    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    private Callback mLoadingDocumentsCallback;
    private DocumentsItemsCreator mItemsCreator;
    private Size mScreenSize;
    private SparseArray<Future> displayFutureArray = new SparseArray<>();
    private SparseArray<Future> getSizeFutureArray = new SparseArray<>();
    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private List<DocumentItem> items;
    private AtomicBoolean destroyed = new AtomicBoolean(false);
    private int loadedDocumentsCount;

    protected DocumentsItemsCreator createItemsCreator() {
        return new ReaderDocumentsItemsCreator();
    }

    public interface Callback {
        void documentLoaded(DocumentItem item);
        void allDocumentsLoaded();
        void itemsCreated(DocumentItem[] items);
    }

    public ReaderDocumentsLoader(Callback callback, Size screenSize) {
        mDocumentsLoaderWorkQueue = new LinkedBlockingQueue<>();
        mDocumentsLoaderThreadPoolExecutor = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mDocumentsLoaderWorkQueue);
        mLoadingDocumentsCallback = callback;
        mItemsCreator = createItemsCreator();
        mScreenSize = screenSize;
    }

    @Override
    public void loadManyItems(DocumentItem[] documentsPaths) {
        int position = 0;
        loadedDocumentsCount=0;
        for (DocumentItem documentPath:
                documentsPaths) {
            loadOne(documentPath, position, documentsPaths.length);
            position++;
        }
    }

    @Override
    public void loadOne(final DocumentItem item, int position, final int documentsCount) {
        Future getSizeFuture = mDocumentsLoaderThreadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
            try {
                if(!destroyed.get()) {
                    item.computeSize(mScreenSize.getWidth(), mScreenSize.getHeight());
                    mainThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            item.sizeComputed();
                            mLoadingDocumentsCallback.documentLoaded(item);
                            loadedDocumentsCount++;
                            if(loadedDocumentsCount == documentsCount) {
                                mLoadingDocumentsCallback.allDocumentsLoaded();
                            }
                        }
                    });
                }
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
            }
        });
        getSizeFutureArray.put(position, getSizeFuture);
    }

    @Override
    public void createManyItems(Context context, String[] documentsPaths) {
        items = new ArrayList<>();
        int position = 0;
        for (String documentPath : documentsPaths) {
            String extension = FileUtils.getExtension(documentPath);
            DocumentItem[] containedItems = mItemsCreator.create(context, documentPath, extension);
            for (DocumentItem item :
                    containedItems) {

                final int finalPosition = position;
                item.setDisplayCallback(new DocumentItem.DisplayCallback() {
                    @Override
                    public void cancelDisplay() {
                        Future displayFuture = displayFutureArray.get(finalPosition);
                        if (displayFuture != null) {
                            displayFuture.cancel(true);
                            displayFutureArray.remove(finalPosition);
                        }
                    }
                });
                items.add(item);
                position++;
            }
        }
        DocumentItem[] itemsArray = new DocumentItem[items.size()];
        items.toArray(itemsArray); // fill the array
        mLoadingDocumentsCallback.itemsCreated(itemsArray);
    }

    @Override
    public void display(int position, final Runnable backgroundThread, final Runnable mainThread) {
        Future displayFuture = mDocumentsLoaderThreadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                backgroundThread.run();
                mainThreadHandler.post(mainThread);
            }
        });
        displayFutureArray.put(position, displayFuture);

    }

    @Override
    public void release() {
        destroyed.set(true);
        int position = 0;
        mDocumentsLoaderThreadPoolExecutor.getQueue().clear();
        /*
        for (DocumentItem item:
                items) {
            Future getSizeFuture = getSizeFutureArray.get(position);
            if (getSizeFuture != null) {
                getSizeFuture.cancel(true);
                getSizeFutureArray.remove(position);
            }
        }*/
        mainThreadHandler.removeCallbacksAndMessages(null);
    }
}
