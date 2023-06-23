package com.nimbusbg.audiobookcanvas.data.repository;

import java.util.List;

public interface InsertedMultipleItemsListener
{
    void onInsert(List<Long> itemIds);
}
