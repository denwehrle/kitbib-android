package com.denwehrle.kitbib.data.remote.interfaces;

import com.denwehrle.kitbib.data.model.BibSummaryItem;

import java.util.List;

/**
 * @author Dennis Wehrle
 */
public interface AsyncBookList {

    void resultList(List<BibSummaryItem> resultList);
}