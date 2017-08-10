package com.denwehrle.kitbib.data.model;

import java.util.Date;

/**
 * @author Dennis Wehrle
 */
public class BibSummaryItem {

    public String index;
    public String title;
    public BibSummaryItemState state;
    public String originBib;
    public String signature;
    public Date dueDate;
    public String orderInfo;
    public boolean flagged;
    public String bookCover;
}