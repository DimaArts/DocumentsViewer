package ru.dimaarts.documentsreader.activity;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;

import ru.dimaarts.documentsreader.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.dimaarts.documentsreader.adapters.DocumentReaderAdapter;
import ru.dimaarts.documentsreader.documentsloaders.DocumentsLoader;
import ru.dimaarts.documentsreader.documentsloaders.ReaderDocumentsLoader;
import ru.dimaarts.documentsreader.items.DocumentItem;
import ru.dimaarts.documentsreader.layouts.DocumentsReaderLayoutManager;
import ru.dimaarts.documentsreader.pager.PagerView;
import ru.dimaarts.documentsreader.recyclerviews.DocumentsReaderRecyclerView;
import ru.dimaarts.documentsreader.utils.FilePicker;
import ru.dimaarts.documentsreader.utils.ScreenUtils;
import ru.dimaarts.documentsreader.utils.Size;

/**
 * Created by gorshunovdv on 1/20/2017.
 */
public class DocumentsReaderActivity extends AppCompatActivity implements ReaderDocumentsLoader.Callback, FilePicker.FilePickerSupport {
    public final static String DOC_PATHS = "document_paths";
    public final static String TITLE = "title";
    private DocumentsLoader mDocumentsLoader;
    private DocumentReaderAdapter adapter;
    private DocumentsReaderRecyclerView documentsReaderRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.documents_reader_main);
        documentsReaderRecyclerView = (DocumentsReaderRecyclerView) findViewById(R.id.document_reader_recycler_view);
        PagerView pager = (PagerView) findViewById(R.id.pager);

        Point screenPointSize = ScreenUtils.getScreenSize(this);
        Size screenSize = new Size();
        screenSize.setWidth(screenPointSize.x);
        screenSize.setHeight(screenPointSize.y);
        mDocumentsLoader = new ReaderDocumentsLoader(this, screenSize);
        adapter = new DocumentReaderAdapter(this, this, mDocumentsLoader);

        DocumentsReaderLayoutManager layoutManager = new DocumentsReaderLayoutManager();
        layoutManager.setPager(pager);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        documentsReaderRecyclerView.setLayoutManager(layoutManager);
        documentsReaderRecyclerView.setItemAnimator(itemAnimator);
        documentsReaderRecyclerView.setAdapter(adapter);

        String[] docs = getIntent().getStringArrayExtra(DOC_PATHS);
        int titleId = getIntent().getIntExtra(TITLE, -1);
        if(titleId>-1)
            setTitle(titleId);
        loadDocuments(mDocumentsLoader, docs);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDocumentsLoader.release();
        documentsReaderRecyclerView.onDestroy();
        documentsReaderRecyclerView.setAdapter(null);
    }

    protected void loadDocuments(DocumentsLoader documentsLoader, String[] docs) {
        documentsLoader.createManyItems(this, docs);
    }

    @Override
    public void documentLoaded(DocumentItem item) {
        // update computed page view size
        adapter.getItemPosition(item);
        documentsReaderRecyclerView.requestLayout();
    }

    @Override
    public void allDocumentsLoaded() {
        documentsReaderRecyclerView.documentsLoaded();
    }

    @Override
    public void itemsCreated(DocumentItem[] items) {
        List<DocumentItem> listItems = new ArrayList<>(Arrays.asList(items));
        adapter.addItemsRange(listItems);
        mDocumentsLoader.loadManyItems(items);
    }

    @Override
    public void performPickFor(FilePicker picker) {
    }
}
