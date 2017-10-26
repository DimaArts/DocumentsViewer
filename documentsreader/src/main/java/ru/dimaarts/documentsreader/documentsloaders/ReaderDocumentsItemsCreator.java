package ru.dimaarts.documentsreader.documentsloaders;

import android.content.Context;

import ru.dimaarts.documentsreader.items.DocumentItem;
import ru.dimaarts.documentsreader.items.ImageDocumentItem;
import ru.dimaarts.documentsreader.items.PdfDocumentItem;
import ru.dimaarts.documentsreader.utils.MuPDFCore;

/**
 * Created by gorshunovdv on 1/20/2017.
 */
public class ReaderDocumentsItemsCreator implements DocumentsItemsCreator {

    @Override
    public DocumentItem[] create(Context context, String documentsPath, String documentExtension) {
        switch (documentExtension) {
            case "jpg":
            case "png":
            case "bmp":
            case "jpeg":
                return new DocumentItem[] {new ImageDocumentItem(documentsPath)};
            case "pdf":
            {
                DocumentItem[] items = new DocumentItem[0];
                try {
                    MuPDFCore core = new MuPDFCore(context, documentsPath);
                    int countPages = core.countPages();
                    items = new DocumentItem[countPages];
                    for (int i = 0; i<countPages; i++) {
                        PdfDocumentItem pdfItem = new PdfDocumentItem(documentsPath, core, i);
                        items[i] = pdfItem;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return items;

            }
        }
        return null;
    }
}
