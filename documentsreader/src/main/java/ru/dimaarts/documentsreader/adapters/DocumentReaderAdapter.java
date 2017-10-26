package ru.dimaarts.documentsreader.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.dimaarts.documentsreader.documentsloaders.DocumentsLoader;
import ru.dimaarts.documentsreader.items.DocumentItem;
import ru.dimaarts.documentsreader.utils.DimensionUtils;
import ru.dimaarts.documentsreader.utils.FilePicker;
import ru.dimaarts.documentsreader.utils.ScreenUtils;
import ru.dimaarts.documentsreader.utils.Size;
import ru.dimaarts.documentsreader.viewholders.DocumentsReaderViewHolder;

/**
 * Created by gorshunovdv on 1/20/2017.
 */
public class DocumentReaderAdapter extends RecyclerView.Adapter implements PageItemsAdapter {
    private List<DocumentItem> mItems;
    private List<Integer> mTypes = new ArrayList<>();
    private Context mContext;
    private DocumentsLoader mDocumentsLoader;
    private RecyclerView recyclerView;
    private FilePicker.FilePickerSupport mFilePickerSupport;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    public DocumentReaderAdapter(FilePicker.FilePickerSupport filePickerSupport, Context context, DocumentsLoader documentsLoader) {
        super();
        mItems = new ArrayList<>();
        mContext = context;
        mDocumentsLoader = documentsLoader;
        mFilePickerSupport = filePickerSupport;
    }

    @Override
    public int getItemViewType(int position) {
        DocumentItem currentItem = mItems.get(position);
        return mTypes.indexOf(currentItem.getLayoutId());
    }

    private void addTypeIfNotExists(DocumentItem item) {
        if (!mTypes.contains(item.getLayoutId())) {
            mTypes.add(item.getLayoutId());
        }
    }


    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void addItem(DocumentItem item) {
        mItems.add(item);
        addTypeIfNotExists(item);
        notifyItemInserted(mItems.size()-1);
    }

    public void replaceItem(int position, DocumentItem item) {
        mItems.set(position, item);
        addTypeIfNotExists(item);
        notifyItemChanged(position);
    }

    public void clear() {
        int itemsCount = mItems.size();
        mItems.clear();
        mTypes.clear();
        notifyItemRangeRemoved(0, itemsCount);
    }

    public void addItemsRange(List<DocumentItem> drawerItem) {
        mItems.addAll(drawerItem);
        for (DocumentItem item : mItems) {
            addTypeIfNotExists(item);
        }
        notifyItemRangeInserted(mItems.size()-drawerItem.size(), drawerItem.size());
    }

    public void removeItem(DocumentItem item) {
        int position = mItems.indexOf(item);
        mItems.remove(item);
        notifyItemRemoved(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(mTypes.get(viewType), parent, false);
        View[] cachedViews = null;
        for(int i=0; i<mItems.size(); i++) {
            DocumentItem currentItem = mItems.get(i);
            int itemViewType = mTypes.indexOf(currentItem.getLayoutId());
            if(itemViewType == viewType) {
                cachedViews = mItems.get(i).prepareAndGetViewsForCache(view);
                break;
            }
        }

        return new DocumentsReaderViewHolder(view, cachedViews);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final DocumentItem currentItem = mItems.get(position);
        if(holder instanceof DocumentsReaderViewHolder) {
            final DocumentsReaderViewHolder documentsViewHolder = (DocumentsReaderViewHolder) holder;
            currentItem.bindHolder(holder.itemView, recyclerView, mFilePickerSupport, getBlankPageSize(), documentsViewHolder.isWasBounded());
            documentsViewHolder.setWasBounded(true);
            mDocumentsLoader.display(position, new Runnable() {
                @Override
                public void run() {
                    currentItem.prepareToDisplayInBackground();
                }
                },
                new Runnable() {
                @Override
                public void run() {
                    currentItem.onDisplayed(documentsViewHolder.getCachedViews());
                }
            });
        }
    }

    protected Size getBlankPageSize() {
        Point size = ScreenUtils.getScreenSize((Activity) mContext);
        Size resultSize = new Size();
        resultSize.setWidth(size.x);
        resultSize.setHeight(DimensionUtils.dpToPx(mContext, 200));
        return resultSize;
    }

    @Override
    public Size getViewItemSize(int position) {
        DocumentItem currentItem = mItems.get(position);
        Size size = null;
        if(!currentItem.isBlank()) {
            size = currentItem.getDisplayedPageSize();
        }
        else {
            size = getBlankPageSize();
        }
        return size;
    }

    @Override
    public void recycleItem(View view, int position) {
        /*RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(view);
        DocumentItem currentItem = mItems.get(holder.getAdapterPosition());
        DocumentsReaderViewHolder docHolder = (DocumentsReaderViewHolder) holder;
        currentItem.onRecycle(docHolder.getCachedViews());*/
    }

    @Override
    public DocumentItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        DocumentItem currentItem = mItems.get(holder.getAdapterPosition());
        DocumentsReaderViewHolder docHolder = (DocumentsReaderViewHolder) holder;
        currentItem.onRecycle(holder.itemView, docHolder.getCachedViews());
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        DocumentItem currentItem = mItems.get(holder.getAdapterPosition());
        DocumentsReaderViewHolder docHolder = (DocumentsReaderViewHolder) holder;
        currentItem.onDetach(holder.itemView, docHolder.getCachedViews());
    }

    public int getItemPosition(DocumentItem item) {
        return mItems.indexOf(item);
    }
}
