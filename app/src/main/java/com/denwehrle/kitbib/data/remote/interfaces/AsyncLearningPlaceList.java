package com.denwehrle.kitbib.data.remote.interfaces;

import com.denwehrle.kitbib.data.model.LearningPlace;

import java.util.List;

/**
 * @author Dennis Wehrle
 */
public interface AsyncLearningPlaceList {

    void resultList(List<LearningPlace> resultList);
}