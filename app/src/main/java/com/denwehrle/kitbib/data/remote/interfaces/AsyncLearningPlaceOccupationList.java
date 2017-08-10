package com.denwehrle.kitbib.data.remote.interfaces;

import com.denwehrle.kitbib.data.model.LearningPlaceOccupation;

import java.util.List;

/**
 * @author Dennis Wehrle
 */
public interface AsyncLearningPlaceOccupationList {

    void resultList(List<LearningPlaceOccupation> resultList);
}