/*
 * todos Copyright (C) 2016 Fatih.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fs.mvvm.todo.managers;

import android.content.Context;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.List;
import java8.util.function.Predicate;
import org.fs.mvvm.common.AbstractOrmliteHelper;
import org.fs.mvvm.todo.BuildConfig;
import org.fs.mvvm.todo.R;
import org.fs.mvvm.todo.entities.Entry;
import rx.Observable;

public final class DatabaseManager extends AbstractOrmliteHelper
    implements IDatabaseManager {

  private final static String DB_NAME = "todos";
  private final static int DB_VERSION = 1;

  private RuntimeExceptionDao<Entry, Integer> entryDao;

  public DatabaseManager(Context context) {
    super(context, DB_NAME, DB_VERSION, R.raw.ormlite_config);
  }

  @Override protected void createTables(ConnectionSource cs) throws SQLException {
    TableUtils.createTableIfNotExists(cs, Entry.class);
  }

  @Override protected void dropTables(ConnectionSource cs) throws SQLException {
    TableUtils.dropTable(cs, Entry.class, true);
  }

  @Override public Observable<List<Entry>> all() {
    createIfEntryDaoIsNull();
    return Observable.just(entryDao)
        .map(RuntimeExceptionDao::queryForAll);
  }

  @Override public Observable<List<Entry>> all(Predicate<Entry> filter) {
    createIfEntryDaoIsNull();
    return Observable.just(entryDao)
        .map(RuntimeExceptionDao::queryForAll)
        .flatMap(Observable::from)
        .filter(filter::test)
        .toList();
  }

  @Override public Observable<Entry> firstOrDefault(Predicate<Entry> filter) {
    return all()
        .flatMap(Observable::from)
        .filter(filter::test)
        .firstOrDefault(null);
  }

  @Override public Observable<Boolean> create(Entry entry) {
    createIfEntryDaoIsNull();
    return Observable.just(entryDao)
        .map(dao -> dao.createOrUpdate(entry).isCreated());
  }

  @Override public Observable<Boolean> update(Entry entry) {
    createIfEntryDaoIsNull();
    return Observable.just(entryDao)
        .map(dao -> dao.createOrUpdate(entry).isUpdated());
  }

  @Override public Observable<Boolean> delete(Entry entry) {
    createIfEntryDaoIsNull();
    return Observable.just(entryDao)
        .map(dao -> dao.delete(entry) == 1);
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }

  @Override protected String getClassTag() {
    return DatabaseManager.class.getSimpleName();
  }

  private void createIfEntryDaoIsNull() {
    if (entryDao == null) {
      entryDao = getRuntimeExceptionDao(Entry.class);
    }
  }
}