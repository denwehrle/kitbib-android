package com.denwehrle.kitbib.data.remote.interfaces;

import com.denwehrle.kitbib.data.model.BibFee;

import java.util.List;

/**
 * @author Dennis Wehrle
 */
public interface AsyncFeeList {

    void resultList(List<BibFee> resultList);
}