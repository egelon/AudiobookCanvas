package com.nimbusbg.audiobookcanvas.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;

public interface BaseDao<T> {


    /**
     * Insert an object in the database.
     *
     * @param entity the object to be inserted.
     * @return the new rowId for the inserted item
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(T entity);

    /**
     * Insert an array of objects in the database.
     *
     * @param entities the objects to be inserted.
     * @return array or a collection of long values instead, with each value as the rowId for one of the inserted items
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insert(List<T> entities);

    /**
     * Update an object from the database.
     *
     * @param entity the object to be updated
     */
    @Update
    void update(T entity);

    /**
     * Update an object from the database.
     *
     * @param entities the list of objects to be updated
     * @return the number of entities affected
     */
    @Update
    int update(List<T> entities);

    /**
     * Delete an object from the database
     *
     * @param entity the object to be deleted
     */
    @Delete
    void delete(T entity);

    /**
     * Delete an object from the database
     *
     * @param entities the list of objects to be deleted
     * @return the number of entities deleted
     */
    @Delete
    int delete(List<T> entities);
}
